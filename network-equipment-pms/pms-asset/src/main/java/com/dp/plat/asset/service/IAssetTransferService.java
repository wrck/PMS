package com.dp.plat.asset.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.asset.entity.AssetTransfer;

/**
 * Service for {@link AssetTransfer} approval workflow.
 */
public interface IAssetTransferService extends IService<AssetTransfer> {

    /**
     * Apply for a transfer. Sets status PENDING, sets asset status IN_TRANSIT.
     */
    boolean apply(AssetTransfer transfer);

    /**
     * Approve a transfer. Sets status APPROVED, updates asset project_id, logs.
     */
    boolean approve(Long transferId, String opinion);

    /**
     * Reject a transfer. Sets status REJECTED, restores asset status.
     */
    boolean reject(Long transferId, String opinion);

    /**
     * Paginated transfer list with optional filters (status, assetId, fromProjectId, toProjectId).
     */
    IPage<AssetTransfer> list(int page, int size, AssetTransfer filter);
}
