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
import com.dp.plat.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of {@link IAssetTransferService}.
 */
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

    private final AssetMapper assetMapper;
    private final AssetLifecycleLogMapper assetLifecycleLogMapper;

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
        // TODO: integrate with workflow engine (Flowable) to start the transfer approval process
        return this.save(transfer);
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
        // TODO: complete the corresponding workflow task in the Flowable engine
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
        // TODO: cancel/terminate the corresponding workflow task in the Flowable engine
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
}
