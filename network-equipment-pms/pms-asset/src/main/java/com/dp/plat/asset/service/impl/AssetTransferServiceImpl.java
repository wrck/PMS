package com.dp.plat.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetLifecycleLog;
import com.dp.plat.asset.entity.AssetTransfer;
import com.dp.plat.asset.mapper.AssetLifecycleLogMapper;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.mapper.AssetTransferMapper;
import com.dp.plat.asset.service.IAssetTransferService;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.workflow.dto.CompleteTaskRequest;
import com.dp.plat.workflow.dto.ProcessInstanceDTO;
import com.dp.plat.workflow.dto.StartProcessRequest;
import com.dp.plat.workflow.dto.TaskDTO;
import com.dp.plat.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IAssetTransferService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetTransferServiceImpl extends ServiceImpl<AssetTransferMapper, AssetTransfer> implements IAssetTransferService {

    /** Transfer status constants. */
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";

    /** Asset status constants. */
    private static final String ASSET_ALLOCATED = "ALLOCATED";
    private static final String ASSET_IN_TRANSIT = "IN_TRANSIT";

    private static final String ACTION_TRANSFER = "TRANSFER";

    /** Workflow process definition key for asset transfer approval. */
    private static final String PROCESS_KEY_ASSET_TRANSFER = "assetTransfer";
    /** Workflow variable names. */
    private static final String VAR_FROM_PROJECT_ID = "fromProjectId";
    private static final String VAR_TO_PROJECT_ID = "toProjectId";
    private static final String VAR_ASSET_ID = "assetId";
    /** Page size used when querying todo tasks for a process instance. */
    private static final int TODO_QUERY_SIZE = 200;

    private final AssetMapper assetMapper;
    private final AssetLifecycleLogMapper assetLifecycleLogMapper;
    private final WorkflowService workflowService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean apply(AssetTransfer transfer) {
        if (transfer.getAssetId() == null) {
            throw new BusinessException("调拨设备不能为空");
        }
        if (transfer.getToProjectId() == null) {
            throw new BusinessException("目标项目不能为空");
        }
        Asset asset = assetMapper.selectById(transfer.getAssetId());
        if (asset == null) {
            throw new BusinessException("设备不存在");
        }
        // Default source project to the asset's current project
        if (transfer.getFromProjectId() == null) {
            transfer.setFromProjectId(asset.getProjectId());
        }
        // Set asset status to IN_TRANSIT
        asset.setStatus(ASSET_IN_TRANSIT);
        assetMapper.updateById(asset);

        LocalDateTime now = LocalDateTime.now();
        transfer.setStatus(STATUS_PENDING);
        transfer.setApplyTime(now);
        transfer.setApplyUserId(SecurityUtils.getCurrentUserId());
        transfer.setApplyUserName(SecurityUtils.getCurrentUsername());
        boolean saved = this.save(transfer);
        startTransferWorkflow(transfer);
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Long transferId, String opinion) {
        AssetTransfer transfer = loadTransfer(transferId);
        if (!STATUS_PENDING.equals(transfer.getStatus())) {
            throw new BusinessException("调拨申请当前状态不可审批通过");
        }
        LocalDateTime now = LocalDateTime.now();
        transfer.setStatus(STATUS_APPROVED);
        transfer.setApproveTime(now);
        transfer.setApproveUserId(SecurityUtils.getCurrentUserId());
        transfer.setApproveUserName(SecurityUtils.getCurrentUsername());
        transfer.setApproveOpinion(opinion);
        boolean updated = this.updateById(transfer);

        // Update asset: move to target project, restore allocated status
        Asset asset = assetMapper.selectById(transfer.getAssetId());
        if (asset != null) {
            Long previousProjectId = asset.getProjectId();
            asset.setProjectId(transfer.getToProjectId());
            asset.setStatus(ASSET_ALLOCATED);
            assetMapper.updateById(asset);
            recordLog(transfer.getAssetId(), ACTION_TRANSFER,
                    transfer.getFromProjectId(), transfer.getToProjectId(),
                    "设备调拨审批通过", now);
        }
        completeTransferTask(transfer, "调拨审批通过: " + (opinion == null ? "" : opinion));
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reject(Long transferId, String opinion) {
        AssetTransfer transfer = loadTransfer(transferId);
        if (!STATUS_PENDING.equals(transfer.getStatus())) {
            throw new BusinessException("调拨申请当前状态不可驳回");
        }
        LocalDateTime now = LocalDateTime.now();
        transfer.setStatus(STATUS_REJECTED);
        transfer.setApproveTime(now);
        transfer.setApproveUserId(SecurityUtils.getCurrentUserId());
        transfer.setApproveUserName(SecurityUtils.getCurrentUsername());
        transfer.setApproveOpinion(opinion);
        boolean updated = this.updateById(transfer);

        // Restore asset status: back to allocated to the source project
        Asset asset = assetMapper.selectById(transfer.getAssetId());
        if (asset != null) {
            asset.setStatus(ASSET_ALLOCATED);
            asset.setProjectId(transfer.getFromProjectId());
            assetMapper.updateById(asset);
            recordLog(transfer.getAssetId(), ACTION_TRANSFER,
                    transfer.getFromProjectId(), transfer.getToProjectId(),
                    "设备调拨申请被驳回", now);
        }
        completeTransferTask(transfer, "调拨审批驳回: " + (opinion == null ? "" : opinion));
        return updated;
    }

    @Override
    public IPage<AssetTransfer> list(int page, int size, AssetTransfer filter) {
        Page<AssetTransfer> p = new Page<>(page, size);
        LambdaQueryWrapper<AssetTransfer> wrapper = new LambdaQueryWrapper<AssetTransfer>()
                .eq(filter != null && filter.getStatus() != null, AssetTransfer::getStatus, filter == null ? null : filter.getStatus())
                .eq(filter != null && filter.getAssetId() != null, AssetTransfer::getAssetId, filter == null ? null : filter.getAssetId())
                .eq(filter != null && filter.getFromProjectId() != null, AssetTransfer::getFromProjectId, filter == null ? null : filter.getFromProjectId())
                .eq(filter != null && filter.getToProjectId() != null, AssetTransfer::getToProjectId, filter == null ? null : filter.getToProjectId())
                .orderByDesc(AssetTransfer::getId);
        return this.page(p, wrapper);
    }

    private AssetTransfer loadTransfer(Long transferId) {
        if (transferId == null) {
            throw new BusinessException("调拨申请 id 不能为空");
        }
        AssetTransfer transfer = this.getById(transferId);
        if (transfer == null) {
            throw new BusinessException("调拨申请不存在");
        }
        return transfer;
    }

    private void recordLog(Long assetId, String actionType, Long fromProjectId, Long toProjectId,
                           String remarks, LocalDateTime actionTime) {
        AssetLifecycleLog log = AssetLifecycleLog.builder()
                .assetId(assetId)
                .actionType(actionType)
                .fromProjectId(fromProjectId)
                .toProjectId(toProjectId)
                .operatorId(SecurityUtils.getCurrentUserId())
                .operatorName(SecurityUtils.getCurrentUsername())
                .actionTime(actionTime)
                .remarks(remarks)
                .build();
        assetLifecycleLogMapper.insert(log);
    }

    /**
     * Start the asset transfer approval workflow for the given transfer and persist
     * the returned process instance id.
     */
    private void startTransferWorkflow(AssetTransfer transfer) {
        try {
            StartProcessRequest req = new StartProcessRequest();
            req.setProcessDefinitionKey(PROCESS_KEY_ASSET_TRANSFER);
            req.setBusinessKey(transfer.getId().toString());
            Map<String, Object> variables = new HashMap<>();
            if (transfer.getFromProjectId() != null) {
                variables.put(VAR_FROM_PROJECT_ID, transfer.getFromProjectId());
            }
            if (transfer.getToProjectId() != null) {
                variables.put(VAR_TO_PROJECT_ID, transfer.getToProjectId());
            }
            if (transfer.getAssetId() != null) {
                variables.put(VAR_ASSET_ID, transfer.getAssetId());
            }
            req.setVariables(variables);
            Result<ProcessInstanceDTO> resp = workflowService.startProcess(req);
            if (resp != null && resp.isSuccess() && resp.getData() != null) {
                transfer.setProcessInstanceId(resp.getData().getId());
                this.updateById(transfer);
            } else {
                log.warn("调拨 {} 启动审批流程未返回实例: {}", transfer.getId(),
                        resp == null ? "null" : resp.getMessage());
            }
        } catch (Exception e) {
            // Workflow engine unavailable should not block transfer creation.
            log.error("调拨 {} 启动审批流程失败: {}", transfer.getId(), e.getMessage(), e);
        }
    }

    /**
     * Complete the current approval task for the transfer's workflow instance.
     */
    private void completeTransferTask(AssetTransfer transfer, String comment) {
        String processInstanceId = transfer.getProcessInstanceId();
        if (!StringUtils.hasText(processInstanceId)) {
            return;
        }
        try {
            String taskId = findCurrentTaskId(processInstanceId);
            if (!StringUtils.hasText(taskId)) {
                log.warn("调拨 {} 未找到当前待办任务，processInstanceId={}", transfer.getId(), processInstanceId);
                return;
            }
            CompleteTaskRequest req = new CompleteTaskRequest();
            req.setTaskId(taskId);
            req.setComment(comment);
            workflowService.completeTask(req);
        } catch (Exception e) {
            log.error("调拨 {} 完成审批任务失败: {}", transfer.getId(), e.getMessage(), e);
        }
    }

    /**
     * Find the current user's todo task id for the given process instance.
     */
    private String findCurrentTaskId(String processInstanceId) {
        Result<Map<String, Object>> todoResult = workflowService.getTodoTasks(1, TODO_QUERY_SIZE);
        if (todoResult == null || !todoResult.isSuccess() || todoResult.getData() == null) {
            return null;
        }
        Object records = todoResult.getData().get("records");
        if (!(records instanceof List<?> list)) {
            return null;
        }
        for (Object item : list) {
            if (item instanceof TaskDTO task
                    && processInstanceId.equals(task.getProcessInstanceId())) {
                return task.getId();
            }
        }
        return null;
    }
}
