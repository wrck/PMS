package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.DataFieldRelationMapper;
import com.dp.plat.model.entity.DataFieldRelation;
import com.dp.plat.service.DataFieldRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据字段关系服务 - migrated from Struts
 */
@Service
public class DataFieldRelationServiceImpl implements DataFieldRelationService {

    @Autowired
    private DataFieldRelationMapper dataFieldRelationMapper;

    @Override
    public IPage<DataFieldRelation> queryPage(Integer pageNum, Integer pageSize) {
        Page<DataFieldRelation> page = new Page<>(pageNum, pageSize);
        return dataFieldRelationMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public DataFieldRelation getById(Long id) {
        return dataFieldRelationMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(DataFieldRelation entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        dataFieldRelationMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(DataFieldRelation entity) {
        entity.setUpdateTime(LocalDateTime.now());
        dataFieldRelationMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        dataFieldRelationMapper.deleteById(id);
    }

    @Override
    public List<DataFieldRelation> listAll() {
        return dataFieldRelationMapper.selectList(new LambdaQueryWrapper<>());
    }

}