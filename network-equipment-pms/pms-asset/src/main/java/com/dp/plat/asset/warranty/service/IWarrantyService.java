package com.dp.plat.asset.warranty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.asset.warranty.entity.Warranty;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for {@link Warranty} management.
 */
public interface IWarrantyService extends IService<Warranty> {

    /**
     * List warranty records for an asset.
     */
    List<Warranty> listByAsset(Long assetId);

    /**
     * List warranty records for a project.
     */
    List<Warranty> listByProject(Long projectId);

    /**
     * List warranty records expiring within the given number of days from today.
     */
    List<Warranty> listExpiringSoon(int days);

    /**
     * Whether the given date falls within the asset's warranty period (inclusive).
     * Returns {@code false} when no warranty record exists for the asset.
     */
    boolean isInWarranty(Long assetId, LocalDate date);

    /**
     * Initialize warranty records for all assets associated with a project.
     *
     * <p>Each asset's warranty starts the day after the final acceptance date and
     * lasts {@code durationMonths} months (default 12 when null).</p>
     *
     * @param projectId            project id whose assets should receive warranties
     * @param finalAcceptanceDate  final acceptance approval date
     * @param durationMonths       warranty duration in months (nullable → 12)
     */
    void initWarrantyForProject(Long projectId, LocalDate finalAcceptanceDate, Integer durationMonths);
}
