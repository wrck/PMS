package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.IndustryAssetLeakRelationMapper;
import com.dp.plat.model.entity.IndustryAssetLeakRelation;
import com.dp.plat.service.IndustryAssetLeakRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行业资产泄漏关系服务 - migrated from Struts
 */
@Service
public class IndustryAssetLeakRelationServiceImpl implements IndustryAssetLeakRelationService {

    @Autowired
    private IndustryAssetLeakRelationMapper industryAssetLeakRelationMapper;

    @Override
    public IPage<IndustryAssetLeakRelation> queryPage(Integer pageNum, Integer pageSize) {
        Page<IndustryAssetLeakRelation> page = new Page<>(pageNum, pageSize);
        return industryAssetLeakRelationMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public IndustryAssetLeakRelation getById(Long id) {
        return industryAssetLeakRelationMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(IndustryAssetLeakRelation entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        industryAssetLeakRelationMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(IndustryAssetLeakRelation entity) {
        entity.setUpdateTime(LocalDateTime.now());
        industryAssetLeakRelationMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        industryAssetLeakRelationMapper.deleteById(id);
    }

    @Override
    public List<IndustryAssetLeakRelation> listAll() {
        return industryAssetLeakRelationMapper.selectList(new LambdaQueryWrapper<>());
    }

}