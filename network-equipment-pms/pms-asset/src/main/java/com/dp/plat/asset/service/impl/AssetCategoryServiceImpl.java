package com.dp.plat.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.asset.entity.AssetCategory;
import com.dp.plat.asset.mapper.AssetCategoryMapper;
import com.dp.plat.asset.service.IAssetCategoryService;
import com.dp.plat.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IAssetCategoryService}.
 */
@Service
@RequiredArgsConstructor
public class AssetCategoryServiceImpl extends ServiceImpl<AssetCategoryMapper, AssetCategory> implements IAssetCategoryService {

    @Override
    public List<AssetCategory> getTree() {
        List<AssetCategory> all = this.list(new LambdaQueryWrapper<AssetCategory>()
                .orderByAsc(AssetCategory::getSortOrder)
                .orderByAsc(AssetCategory::getId));
        // Group children by parent id
        Map<Long, List<AssetCategory>> grouped = all.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));
        // Bind children recursively
        for (AssetCategory category : all) {
            category.setChildren(grouped.getOrDefault(category.getId(), new ArrayList<>()));
        }
        // Return root nodes (parentId == 0)
        return grouped.getOrDefault(0L, new ArrayList<>());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(AssetCategory category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        return this.save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(AssetCategory category) {
        if (category.getId() == null) {
            throw new BusinessException("分类 id 不能为空");
        }
        return this.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        if (id == null) {
            throw new BusinessException("分类 id 不能为空");
        }
        // Check for children before allowing deletion
        long childCount = this.count(new LambdaQueryWrapper<AssetCategory>()
                .eq(AssetCategory::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }
        return this.removeById(id);
    }
}
