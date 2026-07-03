package com.dp.plat.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.asset.entity.AssetCategory;

import java.util.List;

/**
 * Service for {@link AssetCategory} tree management.
 */
public interface IAssetCategoryService extends IService<AssetCategory> {

    /**
     * Get the full category tree (root nodes with nested children).
     */
    List<AssetCategory> getTree();

    /**
     * Create a category.
     */
    boolean create(AssetCategory category);

    /**
     * Update a category.
     */
    boolean update(AssetCategory category);

    /**
     * Delete a category. Fails if it has children.
     */
    boolean delete(Long id);
}
