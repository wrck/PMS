package com.dp.plat.governance.change.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.governance.change.entity.ChangeRequest;
import com.dp.plat.governance.change.mapper.ChangeRequestMapper;
import com.dp.plat.governance.change.service.IBaselineHistoryService;
import com.dp.plat.governance.change.service.IChangeRequestService;
import com.dp.plat.workflow.dto.CompleteTaskRequest;
import com.dp.plat.workflow.dto.ProcessInstanceDTO;
import com.dp.plat.workflow.dto.StartProcessRequest;
import com.dp.plat.workflow.dto.TaskDTO;
import com.dp.plat.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IChangeRequestService}.
 *
 * <p>The workflow integration uses an {@link ObjectProvider} so that the
 * governance module can operate even when the workflow engine is unavailable.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl
        extends ServiceImpl<ChangeRequestMapper, ChangeRequest>
        implements IChangeRequestService {

    /** Initial status for a newly created change request. */
    private static final String STATUS_SUBMITTED = "SUBMITTED";
    /** Status while the CCB is reviewing the request. */
    private static final String STATUS_UNDER_REVIEW = "UNDER_REVIEW";
    /** Status after CCB approval. */
    private static final String STATUS_CCB_APPROVED = "CCB_APPROVED";
    /** Status after CCB rejection. */
    private static final String STATUS_CCB_REJECTED = "CCB_REJECTED";
    /** Status while the approved change is being implemented. */
    private static final String STATUS_IMPLEMENTING = "IMPLEMENTING";
    /** Final closed status. */
    private static final String STATUS_CLOSED = "CLOSED";

    /** Default priority for a new change request. */
    private static final String PRIORITY_MEDIUM = "MEDIUM";

    /** Workflow process definition key for change request CCB approval. */
    private static final String PROCESS_KEY_CR_APPROVAL = "changeRequestApproval";
    /** Workflow variable: CCB approver user id. */
    private static final String VAR_CCB_APPROVER_ID = "ccbApproverId";
    /** Workflow variable: requester user id. */
    private static final String VAR_REQUESTER_ID = "requesterId";
    /** Workflow variable: project id. */
    private static final String VAR_PROJECT_ID = "projectId";
    /** Workflow variable: approval outcome flag. */
    private static final String VAR_APPROVED = "approved";
    /** Page size used when querying todo tasks for a process instance. */
    private static final int TODO_QUERY_SIZE = 200;

    /** Baseline change type for schedule. */
    private static final String CHANGE_TYPE_SCHEDULE = "SCHEDULE";
    /** Baseline change type for cost. */
    private static final String CHANGE_TYPE_COST = "COST";
    /** Baseline change type for scope. */
    private static final String CHANGE_TYPE_SCOPE = "SCOPE";

    private final IBaselineHistoryService baselineHistoryService;
    private final ObjectProvider<WorkflowService> workflowServiceProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ChangeRequest> create(ChangeRequest changeRequest) {
        if (changeRequest == null) {
            throw new BusinessException("变更请求信息不能为空");
        }
        if (!StringUtils.hasText(changeRequest.getTitle())) {
            throw new BusinessException("变更请求标题不能为空");
        }
        changeRequest.setId(null);
        changeRequest.setCrNo(generateCrNo());
        changeRequest.setStatus(STATUS_SUBMITTED);
        if (!StringUtils.hasText(changeRequest.getPriority())) {
            changeRequest.setPriority(PRIORITY_MEDIUM);
        }
        if (changeRequest.getRequestDate() == null) {
            changeRequest.setRequestDate(LocalDate.now());
        }
        if (changeRequest.getBaselineUpdated() == null) {
            changeRequest.setBaselineUpdated(false);
        }
        this.save(changeRequest);
        return Result.ok(changeRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(ChangeRequest changeRequest) {
        if (changeRequest == null || changeRequest.getId() == null) {
            throw new BusinessException("变更请求信息或ID不能为空");
        }
        ChangeRequest existing = baseMapper.selectById(changeRequest.getId());
        if (existing == null) {
            throw new BusinessException("变更请求不存在");
        }
        this.updateById(changeRequest);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(Long id) {
        ChangeRequest existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("变更请求不存在");
        }
        this.removeById(id);
        return Result.ok();
    }

    @Override
    public Result<List<ChangeRequest>> listAll() {
        List<ChangeRequest> list = this.list(new LambdaQueryWrapper<ChangeRequest>()
                .orderByDesc(ChangeRequest::getCreateTime));
        return Result.ok(list);
    }

    @Override
    public Result<ChangeRequest> getById(Long id) {
        ChangeRequest changeRequest = baseMapper.selectById(id);
        if (changeRequest == null) {
            throw new BusinessException("变更请求不存在");
        }
        return Result.ok(changeRequest);
    }

    @Override
    public Result<List<ChangeRequest>> listByProject(Long projectId) {
        if (projectId == null) {
            return Result.ok(List.of());
        }
        List<ChangeRequest> list = this.list(new LambdaQueryWrapper<ChangeRequest>()
                .eq(ChangeRequest::getProjectId, projectId)
                .orderByDesc(ChangeRequest::getCreateTime));
        return Result.ok(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ChangeRequest> submit(Long id) {
        ChangeRequest cr = baseMapper.selectById(id);
        if (cr == null) {
            throw new BusinessException("变更请求不存在");
        }
        if (!STATUS_SUBMITTED.equals(cr.getStatus())) {
            throw new BusinessException("当前变更请求状态不允许提交，当前状态: " + cr.getStatus());
        }
        cr.setStatus(STATUS_UNDER_REVIEW);
        this.updateById(cr);
        startApprovalWorkflow(cr);
        return Result.ok(cr);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ChangeRequest> approve(Long id, String approverName) {
        ChangeRequest cr = baseMapper.selectById(id);
        if (cr == null) {
            throw new BusinessException("变更请求不存在");
        }
        if (!STATUS_UNDER_REVIEW.equals(cr.getStatus())) {
            throw new BusinessException("当前变更请求状态不允许审批，当前状态: " + cr.getStatus());
        }
        String currentUser = SecurityUtils.getCurrentUsername();
        cr.setStatus(STATUS_CCB_APPROVED);
        cr.setApproverName(approverName);
        cr.setApprovedAt(LocalDateTime.now());
        // Record baseline changes for each non-empty impact dimension.
        recordBaselineChanges(cr, currentUser);
        cr.setBaselineUpdated(true);
        this.updateById(cr);
        // Complete the CCB review task in the workflow with approved=true.
        completeReviewTask(cr, Boolean.TRUE, "CCB审批通过");
        return Result.ok(cr);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ChangeRequest> reject(Long id, String reason) {
        ChangeRequest cr = baseMapper.selectById(id);
        if (cr == null) {
            throw new BusinessException("变更请求不存在");
        }
        if (!STATUS_UNDER_REVIEW.equals(cr.getStatus())) {
            throw new BusinessException("当前变更请求状态不允许驳回，当前状态: " + cr.getStatus());
        }
        cr.setStatus(STATUS_CCB_REJECTED);
        cr.setApprovedAt(LocalDateTime.now());
        this.updateById(cr);
        // Complete the CCB review task in the workflow with approved=false.
        completeReviewTask(cr, Boolean.FALSE, "CCB驳回: " + reason);
        return Result.ok(cr);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ChangeRequest> implement(Long id) {
        ChangeRequest cr = baseMapper.selectById(id);
        if (cr == null) {
            throw new BusinessException("变更请求不存在");
        }
        if (!STATUS_CCB_APPROVED.equals(cr.getStatus())) {
            throw new BusinessException("当前变更请求状态不允许实施，当前状态: " + cr.getStatus());
        }
        cr.setStatus(STATUS_IMPLEMENTING);
        this.updateById(cr);
        return Result.ok(cr);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ChangeRequest> close(Long id) {
        ChangeRequest cr = baseMapper.selectById(id);
        if (cr == null) {
            throw new BusinessException("变更请求不存在");
        }
        cr.setStatus(STATUS_CLOSED);
        cr.setClosedAt(LocalDateTime.now());
        this.updateById(cr);
        return Result.ok(cr);
    }

    @Override
    public String generateCrNo() {
        int year = LocalDate.now().getYear();
        String prefix = "CR-" + year + "-";
        long count = this.count(new LambdaQueryWrapper<ChangeRequest>()
                .likeRight(ChangeRequest::getCrNo, prefix));
        long sequence = count + 1;
        return prefix + String.format("%04d", sequence);
    }

    /**
     * Start the CCB approval workflow for the given change request and persist
     * the returned process instance id.
     */
    private void startApprovalWorkflow(ChangeRequest cr) {
        WorkflowService workflowService = workflowServiceProvider.getIfAvailable();
        if (workflowService == null) {
            log.warn("工作流引擎不可用，变更请求 {} 未启动审批流程", cr.getId());
            return;
        }
        try {
            StartProcessRequest req = new StartProcessRequest();
            req.setProcessDefinitionKey(PROCESS_KEY_CR_APPROVAL);
            req.setBusinessKey(cr.getCrNo());
            Map<String, Object> variables = new HashMap<>();
            if (cr.getApproverId() != null) {
                variables.put(VAR_CCB_APPROVER_ID, cr.getApproverId());
            }
            if (cr.getRequesterId() != null) {
                variables.put(VAR_REQUESTER_ID, cr.getRequesterId());
            }
            if (cr.getProjectId() != null) {
                variables.put(VAR_PROJECT_ID, cr.getProjectId());
            }
            req.setVariables(variables);
            Result<ProcessInstanceDTO> resp = workflowService.startProcess(req);
            if (resp != null && resp.isSuccess() && resp.getData() != null) {
                cr.setProcessInstanceId(resp.getData().getId());
                this.updateById(cr);
            } else {
                log.warn("变更请求 {} 启动审批流程未返回实例: {}", cr.getId(),
                        resp == null ? "null" : resp.getMessage());
            }
        } catch (Exception e) {
            // Workflow engine failure should not block change request submission.
            log.error("变更请求 {} 启动审批流程失败: {}", cr.getId(), e.getMessage(), e);
        }
    }

    /**
     * Complete the CCB review task for the change request's workflow instance
     * with the given approval outcome.
     */
    @SuppressWarnings("unchecked")
    private void completeReviewTask(ChangeRequest cr, Boolean approved, String comment) {
        String processInstanceId = cr.getProcessInstanceId();
        if (!StringUtils.hasText(processInstanceId)) {
            return;
        }
        WorkflowService workflowService = workflowServiceProvider.getIfAvailable();
        if (workflowService == null) {
            return;
        }
        try {
            String taskId = findCurrentTaskId(workflowService, processInstanceId);
            if (!StringUtils.hasText(taskId)) {
                log.warn("变更请求 {} 未找到当前待办任务，processInstanceId={}", cr.getId(), processInstanceId);
                return;
            }
            CompleteTaskRequest req = new CompleteTaskRequest();
            req.setTaskId(taskId);
            req.setComment(comment);
            Map<String, Object> variables = new HashMap<>();
            variables.put(VAR_APPROVED, approved);
            req.setVariables(variables);
            workflowService.completeTask(req);
        } catch (Exception e) {
            log.error("变更请求 {} 完成审批任务失败: {}", cr.getId(), e.getMessage(), e);
        }
    }

    /**
     * Find the current todo task id for the given process instance.
     */
    private String findCurrentTaskId(WorkflowService workflowService, String processInstanceId) {
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

    /**
     * Record baseline history entries for each non-empty impact dimension
     * (schedule, cost, scope).
     */
    private void recordBaselineChanges(ChangeRequest cr, String changedBy) {
        if (StringUtils.hasText(cr.getImpactSchedule())) {
            baselineHistoryService.recordBaselineChange(cr.getProjectId(), cr.getId(),
                    cr.getCrNo(), CHANGE_TYPE_SCHEDULE, "impactSchedule",
                    "原进度基线", cr.getImpactSchedule(), changedBy);
        }
        if (StringUtils.hasText(cr.getImpactCost())) {
            baselineHistoryService.recordBaselineChange(cr.getProjectId(), cr.getId(),
                    cr.getCrNo(), CHANGE_TYPE_COST, "impactCost",
                    "原成本基线", cr.getImpactCost(), changedBy);
        }
        if (StringUtils.hasText(cr.getImpactScope())) {
            baselineHistoryService.recordBaselineChange(cr.getProjectId(), cr.getId(),
                    cr.getCrNo(), CHANGE_TYPE_SCOPE, "impactScope",
                    "原范围基线", cr.getImpactScope(), changedBy);
        }
    }
}
