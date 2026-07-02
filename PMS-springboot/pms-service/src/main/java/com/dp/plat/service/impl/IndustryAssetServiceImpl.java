package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.IndustryAssetMapper;
import com.dp.plat.model.entity.IndustryAsset;
import com.dp.plat.service.IndustryAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行业资产服务 - migrated from Struts
 */
@Service
public class IndustryAssetServiceImpl implements IndustryAssetService {

    @Autowired
    private IndustryAssetMapper industryAssetMapper;

    @Override
    public IPage<IndustryAsset> queryPage(Integer pageNum, Integer pageSize) {
        Page<IndustryAsset> page = new Page<>(pageNum, pageSize);
        return industryAssetMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public IndustryAsset getById(Long id) {
        return industryAssetMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(IndustryAsset entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        industryAssetMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(IndustryAsset entity) {
        entity.setUpdateTime(LocalDateTime.now());
        industryAssetMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        industryAssetMapper.deleteById(id);
    }

    @Override
    public List<IndustryAsset> listAll() {
        return industryAssetMapper.selectList(new LambdaQueryWrapper<>());
    }

}