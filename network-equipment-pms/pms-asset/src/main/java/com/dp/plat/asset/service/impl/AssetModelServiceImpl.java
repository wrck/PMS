package com.dp.plat.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.asset.entity.AssetModel;
import com.dp.plat.asset.mapper.AssetModelMapper;
import com.dp.plat.asset.service.IAssetModelService;
import com.dp.plat.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link IAssetModelService}.
 */
@Service
@RequiredArgsConstructor
public class AssetModelServiceImpl extends ServiceImpl<AssetModelMapper, AssetModel> implements IAssetModelService {

    @Override
    public List<AssetModel> listByCategoryId(Long categoryId) {
        return this.list(new LambdaQueryWrapper<AssetModel>()
                .eq(categoryId != null, AssetModel::getCategoryId, categoryId)
                .orderByAsc(AssetModel::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(AssetModel model) {
        if (model.getCategoryId() == null) {
            throw new BusinessException("所属分类不能为空");
        }
        if (model.getStatus() == null) {
            model.setStatus(1);
        }
        return this.save(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(AssetModel model) {
        if (model.getId() == null) {
            throw new BusinessException("型号 id 不能为空");
        }
        return this.updateById(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        if (id == null) {
            throw new BusinessException("型号 id 不能为空");
        }
        return this.removeById(id);
    }
}
