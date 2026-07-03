package com.dp.plat.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.asset.entity.AssetModel;

import java.util.List;

/**
 * Service for {@link AssetModel} CRUD with category association.
 */
public interface IAssetModelService extends IService<AssetModel> {

    /**
     * List models by category id.
     */
    List<AssetModel> listByCategoryId(Long categoryId);

    /**
     * Create a model.
     */
    boolean create(AssetModel model);

    /**
     * Update a model.
     */
    boolean update(AssetModel model);

    /**
     * Delete a model.
     */
    boolean delete(Long id);
}
