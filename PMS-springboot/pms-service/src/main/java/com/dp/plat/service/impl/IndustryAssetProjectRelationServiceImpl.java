package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.IndustryAssetProjectRelationMapper;
import com.dp.plat.model.entity.IndustryAssetProjectRelation;
import com.dp.plat.service.IndustryAssetProjectRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行业资产项目关系服务 - migrated from Struts
 */
@Service
public class IndustryAssetProjectRelationServiceImpl implements IndustryAssetProjectRelationService {

    @Autowired
    private IndustryAssetProjectRelationMapper industryAssetProjectRelationMapper;

    @Override
    public IPage<IndustryAssetProjectRelation> queryPage(Integer pageNum, Integer pageSize) {
        Page<IndustryAssetProjectRelation> page = new Page<>(pageNum, pageSize);
        return industryAssetProjectRelationMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public IndustryAssetProjectRelation getById(Long id) {
        return industryAssetProjectRelationMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(IndustryAssetProjectRelation entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        industryAssetProjectRelationMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(IndustryAssetProjectRelation entity) {
        entity.setUpdateTime(LocalDateTime.now());
        industryAssetProjectRelationMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        industryAssetProjectRelationMapper.deleteById(id);
    }

    @Override
    public List<IndustryAssetProjectRelation> listAll() {
        return industryAssetProjectRelationMapper.selectList(new LambdaQueryWrapper<>());
    }

}