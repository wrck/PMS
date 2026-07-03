package com.dp.plat.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetAllocation;
import com.dp.plat.asset.entity.AssetLifecycleLog;
import com.dp.plat.asset.mapper.AssetAllocationMapper;
import com.dp.plat.asset.mapper.AssetLifecycleLogMapper;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.service.IAssetService;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link IAssetService}.
 */
@Service
@RequiredArgsConstructor
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements IAssetService {

    /** Asset status constants. */
    private static final String STATUS_IN_STOCK = "IN_STOCK";
    private static final String STATUS_ALLOCATED = "ALLOCATED";
    private static final String STATUS_IN_TRANSIT = "IN_TRANSIT";

    /** Lifecycle action type constants. */
    private static final String ACTION_INBOUND = "INBOUND";
    private static final String ACTION_ALLOCATE = "ALLOCATE";
    private static final String ACTION_RETURN = "RETURN";

    private final AssetAllocationMapper assetAllocationMapper;
    private final AssetLifecycleLogMapper assetLifecycleLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inbound(Asset asset) {
        if (asset.getStatus() == null) {
            asset.setStatus(STATUS_IN_STOCK);
        }
        LocalDateTime now = LocalDateTime.now();
        if (asset.getInboundTime() == null) {
            asset.setInboundTime(now);
        }
        boolean saved = this.save(asset);
        if (saved) {
            recordLog(asset.getId(), ACTION_INBOUND, null, null, "设备入库", now);
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean allocate(Long assetId, Long projectId) {
        Asset asset = loadAsset(assetId);
        if (!STATUS_IN_STOCK.equals(asset.getStatus())) {
            throw new BusinessException("设备当前状态非在库，无法分配");
        }
        Long fromProjectId = asset.getProjectId();
        LocalDateTime now = LocalDateTime.now();
        asset.setStatus(STATUS_ALLOCATED);
        asset.setProjectId(projectId);
        if (asset.getOutboundTime() == null) {
            asset.setOutboundTime(now);
        }
        boolean updated = this.updateById(asset);

        // Create allocation record
        AssetAllocation allocation = AssetAllocation.builder()
                .assetId(assetId)
                .projectId(projectId)
                .modelId(asset.getModelId())
                .quantity(1)
                .allocateTime(now)
                .allocateUserId(SecurityUtils.getCurrentUserId())
                .allocateUserName(SecurityUtils.getCurrentUsername())
                .status("ACTIVE")
                .build();
        assetAllocationMapper.insert(allocation);

        recordLog(assetId, ACTION_ALLOCATE, fromProjectId, projectId, "设备分配至项目", now);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnAsset(Long assetId) {
        Asset asset = loadAsset(assetId);
        if (!STATUS_ALLOCATED.equals(asset.getStatus())) {
            throw new BusinessException("设备当前状态非已分配，无法归还");
        }
        Long fromProjectId = asset.getProjectId();
        LocalDateTime now = LocalDateTime.now();
        asset.setStatus(STATUS_IN_STOCK);
        asset.setProjectId(null);
        boolean updated = this.updateById(asset);

        // Update the latest ACTIVE allocation for this asset
        AssetAllocation active = assetAllocationMapper.selectOne(
                new LambdaQueryWrapper<AssetAllocation>()
                        .eq(AssetAllocation::getAssetId, assetId)
                        .eq(AssetAllocation::getStatus, "ACTIVE")
                        .orderByDesc(AssetAllocation::getId)
                        .last("LIMIT 1"));
        if (active != null) {
            active.setStatus("RETURNED");
            active.setReturnTime(now);
            assetAllocationMapper.updateById(active);
        }

        recordLog(assetId, ACTION_RETURN, fromProjectId, null, "设备归还入库", now);
        return updated;
    }

    @Override
    public IPage<Asset> list(int page, int size, Asset filter) {
        Page<Asset> p = new Page<>(page, size);
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<Asset>()
                .like(filter != null && filter.getSerialNo() != null, Asset::getSerialNo, filter == null ? null : filter.getSerialNo())
                .like(filter != null && filter.getAssetName() != null, Asset::getAssetName, filter == null ? null : filter.getAssetName())
                .eq(filter != null && filter.getStatus() != null, Asset::getStatus, filter == null ? null : filter.getStatus())
                .eq(filter != null && filter.getCategoryId() != null, Asset::getCategoryId, filter == null ? null : filter.getCategoryId())
                .eq(filter != null && filter.getModelId() != null, Asset::getModelId, filter == null ? null : filter.getModelId())
                .eq(filter != null && filter.getProjectId() != null, Asset::getProjectId, filter == null ? null : filter.getProjectId())
                .orderByDesc(Asset::getId);
        return this.page(p, wrapper);
    }

    @Override
    public List<AssetLifecycleLog> getLifecycleLog(Long assetId) {
        return assetLifecycleLogMapper.selectList(new LambdaQueryWrapper<AssetLifecycleLog>()
                .eq(AssetLifecycleLog::getAssetId, assetId)
                .orderByAsc(AssetLifecycleLog::getActionTime)
                .orderByAsc(AssetLifecycleLog::getId));
    }

    @Override
    public List<Asset> returnByProject(Long projectId) {
        return this.list(new LambdaQueryWrapper<Asset>()
                .eq(Asset::getProjectId, projectId)
                .eq(Asset::getStatus, STATUS_ALLOCATED)
                .orderByDesc(Asset::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int recycleByProject(Long projectId) {
        if (projectId == null) {
            return 0;
        }
        List<Asset> assets = baseMapper.selectList(new LambdaQueryWrapper<Asset>()
                .eq(Asset::getProjectId, projectId)
                .eq(Asset::getStatus, STATUS_ALLOCATED));
        for (Asset asset : assets) {
            returnAsset(asset.getId());
        }
        return assets.size();
    }

    private Asset loadAsset(Long assetId) {
        if (assetId == null) {
            throw new BusinessException("设备 id 不能为空");
        }
        Asset asset = this.getById(assetId);
        if (asset == null) {
            throw new BusinessException("设备不存在");
        }
        return asset;
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
