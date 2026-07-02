package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.CommonRelatedDataMapper;
import com.dp.plat.model.entity.CommonRelatedData;
import com.dp.plat.service.CommonRelatedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公共关联数据服务 - migrated from Struts
 */
@Service
public class CommonRelatedDataServiceImpl implements CommonRelatedDataService {

    @Autowired
    private CommonRelatedDataMapper commonRelatedDataMapper;

    @Override
    public IPage<CommonRelatedData> queryPage(Integer pageNum, Integer pageSize) {
        Page<CommonRelatedData> page = new Page<>(pageNum, pageSize);
        return commonRelatedDataMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public CommonRelatedData getById(Long id) {
        return commonRelatedDataMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(CommonRelatedData entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        commonRelatedDataMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(CommonRelatedData entity) {
        entity.setUpdateTime(LocalDateTime.now());
        commonRelatedDataMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        commonRelatedDataMapper.deleteById(id);
    }

    @Override
    public List<CommonRelatedData> listAll() {
        return commonRelatedDataMapper.selectList(new LambdaQueryWrapper<>());
    }

}