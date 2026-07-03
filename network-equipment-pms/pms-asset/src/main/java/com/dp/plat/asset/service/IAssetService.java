package com.dp.plat.asset.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetLifecycleLog;

import java.util.List;

/**
 * Service for {@link Asset} lifecycle management.
 */
public interface IAssetService extends IService<Asset> {

    /**
     * Inbound a new asset. Sets status to IN_STOCK, records lifecycle log.
     */
    boolean inbound(Asset asset);

    /**
     * Allocate an asset to a project. Sets status ALLOCATED, creates allocation, logs.
     */
    boolean allocate(Long assetId, Long projectId);

    /**
     * Return an allocated asset. Sets status IN_STOCK, updates allocation record, logs.
     */
    boolean returnAsset(Long assetId);

    /**
     * Paginated asset list with optional filters (status, serialNo, categoryId, projectId).
     */
    IPage<Asset> list(int page, int size, Asset filter);

    /**
     * Get complete lifecycle history of an asset.
     */
    List<AssetLifecycleLog> getLifecycleLog(Long assetId);

    /**
     * Return all assets currently allocated to a project. Used by final acceptance.
     */
    List<Asset> returnByProject(Long projectId);
}
