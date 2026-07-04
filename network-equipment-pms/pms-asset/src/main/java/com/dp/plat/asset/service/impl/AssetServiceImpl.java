package com.dp.plat.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.asset.dto.AssetImportDTO;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetAllocation;
import com.dp.plat.asset.entity.AssetLifecycleLog;
import com.dp.plat.asset.enums.AssetStatus;
import com.dp.plat.asset.mapper.AssetAllocationMapper;
import com.dp.plat.asset.mapper.AssetLifecycleLogMapper;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.service.AssetStateTransitionValidator;
import com.dp.plat.asset.service.IAssetService;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.excel.ExcelImportResult;
import com.dp.plat.common.excel.ExcelUtils;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link IAssetService}.
 */
@Service
@RequiredArgsConstructor
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements IAssetService {

    /** Asset status constants (9-state lifecycle). */
    private static final String STATUS_RECEIVED = "RECEIVED";
    private static final String STATUS_INSTALLED = "INSTALLED";
    /** Legacy status retained for backward-compatible queries on unmigrated rows. */
    private static final String STATUS_ALLOCATED = "ALLOCATED";

    /** Lifecycle action type constants. */
    private static final String ACTION_INBOUND = "INBOUND";
    private static final String ACTION_ALLOCATE = "ALLOCATE";
    private static final String ACTION_RETURN = "RETURN";

    private final AssetAllocationMapper assetAllocationMapper;
    private final AssetLifecycleLogMapper assetLifecycleLogMapper;
    private final AssetStateTransitionValidator stateValidator;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inbound(Asset asset) {
        LocalDateTime now = LocalDateTime.now();
        AssetStatus current = parseStatus(asset.getStatus());
        AssetStatus target = AssetStatus.RECEIVED;
        stateValidator.validate(current, target);
        asset.setStatus(target.name());
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
        AssetStatus current = parseStatus(asset.getStatus());
        // Allocation installs the asset at a project site; allowed from RECEIVED or STAGED.
        if (current != AssetStatus.RECEIVED && current != AssetStatus.STAGED) {
            throw new BusinessException("设备当前状态不可分配，仅已收货/已暂存设备可分配");
        }
        stateValidator.validate(current, AssetStatus.INSTALLED);
        Long fromProjectId = asset.getProjectId();
        LocalDateTime now = LocalDateTime.now();
        asset.setStatus(STATUS_INSTALLED);
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
        AssetStatus current = parseStatus(asset.getStatus());
        stateValidator.validate(current, AssetStatus.RECEIVED);
        Long fromProjectId = asset.getProjectId();
        LocalDateTime now = LocalDateTime.now();
        asset.setStatus(STATUS_RECEIVED);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportResult<AssetImportDTO> batchImport(MultipartFile file) {
        // Track asset numbers seen within this upload to enforce intra-batch uniqueness.
        Set<String> seenAssetNos = new HashSet<>();
        // Pre-fetch any asset numbers that already exist for the rows in this batch
        // so that duplicates against the database can be reported per row.
        ExcelImportResult<AssetImportDTO> result = ExcelUtils.importWithValidation(
                file, AssetImportDTO.class, row -> {
                    if (row == null) {
                        throw new BusinessException("空行");
                    }
                    String assetNo = row.getAssetNo();
                    if (!StringUtils.hasText(assetNo)) {
                        throw new BusinessException("资产编号不能为空");
                    }
                    String trimmedNo = assetNo.trim();
                    if (!seenAssetNos.add(trimmedNo)) {
                        throw new BusinessException("资产编号在本次导入中重复: " + trimmedNo);
                    }
                    Long count = baseMapper.selectCount(new LambdaQueryWrapper<Asset>()
                            .eq(Asset::getAssetName, trimmedNo));
                    if (count != null && count > 0) {
                        throw new BusinessException("资产编号已存在: " + trimmedNo);
                    }
                    if (!StringUtils.hasText(row.getSerialNo())) {
                        throw new BusinessException("序列号不能为空");
                    }
                    if (StringUtils.hasText(row.getProjectId())) {
                        Long projectId = parseLong(row.getProjectId(), "项目ID格式错误");
                        Project project = projectMapper.selectById(projectId);
                        if (project == null) {
                            throw new BusinessException("项目不存在: " + projectId);
                        }
                    }
                    if (StringUtils.hasText(row.getStatus())) {
                        try {
                            AssetStatus.valueOf(row.getStatus().trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new BusinessException("状态不在合法枚举内: " + row.getStatus());
                        }
                    } else {
                        throw new BusinessException("状态不能为空");
                    }
                });

        // Convert validated rows into Asset entities and persist in one batch.
        List<Asset> entities = new ArrayList<>(result.getSuccessList().size());
        for (AssetImportDTO dto : result.getSuccessList()) {
            Asset asset = new Asset();
            asset.setAssetName(dto.getAssetNo().trim());
            asset.setSerialNo(dto.getSerialNo().trim());
            if (StringUtils.hasText(dto.getProjectId())) {
                asset.setProjectId(parseLong(dto.getProjectId(), "项目ID格式错误"));
            }
            if (StringUtils.hasText(dto.getCategoryId())) {
                asset.setCategoryId(parseLong(dto.getCategoryId(), "设备类别ID格式错误"));
            }
            if (StringUtils.hasText(dto.getModelId())) {
                asset.setModelId(parseLong(dto.getModelId(), "型号ID格式错误"));
            }
            asset.setStatus(dto.getStatus().trim().toUpperCase());
            if (StringUtils.hasText(dto.getManufacturer())) {
                // The Asset entity has no dedicated manufacturer column; persist it
                // in the remarks field so the data is not lost.
                asset.setRemarks("制造商: " + dto.getManufacturer().trim());
            }
            entities.add(asset);
        }
        if (!entities.isEmpty()) {
            this.saveBatch(entities);
        }
        return result;
    }

    /**
     * Parse a String into a Long, throwing a {@link BusinessException} with the
     * supplied message when the value is not a valid long.
     *
     * @param value   raw string
     * @param errMsg  error message used when parsing fails
     * @return parsed long value
     */
    private Long parseLong(String value, String errMsg) {
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new BusinessException(errMsg);
        }
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

    /**
     * Parse a stored status string into an {@link AssetStatus}. Returns {@code null}
     * when the stored value is null (e.g. a brand-new asset prior to first save).
     *
     * @throws BusinessException when the stored value is not a known status
     */
    private AssetStatus parseStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return AssetStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("未知的资产状态: " + status);
        }
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
