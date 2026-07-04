package com.dp.plat.asset.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.asset.dto.AssetImportDTO;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetLifecycleLog;
import com.dp.plat.common.excel.ExcelImportResult;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Recycle all assets currently allocated to a project. Each asset is returned
     * to the warehouse (status IN_STOCK, project cleared, RETURN lifecycle log).
     *
     * @param projectId project id whose bound assets should be recycled
     * @return number of assets recycled
     */
    int recycleByProject(Long projectId);

    /**
     * Batch import assets from an uploaded Excel file. Each row is validated
     * (assetNo non-empty and unique within the upload, projectId exists, status
     * is a known {@link com.dp.plat.asset.enums.AssetStatus}); valid rows are
     * persisted via {@link #saveBatch(java.util.Collection)} and invalid rows
     * are returned as error entries.
     *
     * @param file uploaded .xlsx file
     * @return aggregated import result carrying success and error lists
     */
    ExcelImportResult<AssetImportDTO> batchImport(MultipartFile file);
}
