package com.dp.plat.asset.warranty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.warranty.entity.Warranty;
import com.dp.plat.asset.warranty.mapper.WarrantyMapper;
import com.dp.plat.asset.warranty.service.IWarrantyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of {@link IWarrantyService}.
 */
@Service
@RequiredArgsConstructor
public class WarrantyServiceImpl extends ServiceImpl<WarrantyMapper, Warranty> implements IWarrantyService {

    /** Default warranty duration (months) when none is supplied. */
    private static final int DEFAULT_DURATION_MONTHS = 12;

    private final AssetMapper assetMapper;

    @Override
    public List<Warranty> listByAsset(Long assetId) {
        return this.list(new LambdaQueryWrapper<Warranty>()
                .eq(Warranty::getAssetId, assetId)
                .orderByDesc(Warranty::getId));
    }

    @Override
    public List<Warranty> listByProject(Long projectId) {
        return this.list(new LambdaQueryWrapper<Warranty>()
                .eq(Warranty::getProjectId, projectId)
                .orderByDesc(Warranty::getId));
    }

    @Override
    public List<Warranty> listExpiringSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(days);
        return this.list(new LambdaQueryWrapper<Warranty>()
                .ge(Warranty::getEndDate, today)
                .le(Warranty::getEndDate, threshold)
                .orderByAsc(Warranty::getEndDate));
    }

    @Override
    public boolean isInWarranty(Long assetId, LocalDate date) {
        if (assetId == null || date == null) {
            return false;
        }
        Warranty warranty = this.getOne(new LambdaQueryWrapper<Warranty>()
                .eq(Warranty::getAssetId, assetId)
                .orderByDesc(Warranty::getId)
                .last("LIMIT 1"));
        if (warranty == null || warranty.getStartDate() == null || warranty.getEndDate() == null) {
            return false;
        }
        return !date.isBefore(warranty.getStartDate()) && !date.isAfter(warranty.getEndDate());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initWarrantyForProject(Long projectId, LocalDate finalAcceptanceDate, Integer durationMonths) {
        if (projectId == null || finalAcceptanceDate == null) {
            return;
        }
        int months = (durationMonths == null || durationMonths <= 0) ? DEFAULT_DURATION_MONTHS : durationMonths;
        LocalDate startDate = finalAcceptanceDate.plusDays(1);
        LocalDate endDate = startDate.plusMonths(months);

        List<Asset> assets = assetMapper.selectList(new LambdaQueryWrapper<Asset>()
                .eq(Asset::getProjectId, projectId));
        for (Asset asset : assets) {
            // Skip assets that already have a warranty record.
            Long existing = this.count(new LambdaQueryWrapper<Warranty>()
                    .eq(Warranty::getAssetId, asset.getId()));
            if (existing != null && existing > 0) {
                continue;
            }
            Warranty warranty = Warranty.builder()
                    .assetId(asset.getId())
                    .projectId(projectId)
                    .startDate(startDate)
                    .endDate(endDate)
                    .durationMonths(months)
                    .build();
            this.save(warranty);
        }
    }
}
