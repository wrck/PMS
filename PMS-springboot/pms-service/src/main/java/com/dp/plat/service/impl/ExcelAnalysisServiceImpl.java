package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.PmsProjectMapper;
import com.dp.plat.model.entity.PmsProject;
import com.dp.plat.service.ExcelAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Excel分析服务 - migrated from Struts
 */
@Service
public class ExcelAnalysisServiceImpl implements ExcelAnalysisService {

    @Autowired
    private PmsProjectMapper pmsProjectMapper;

    @Override
    public IPage<PmsProject> queryPage(Integer pageNum, Integer pageSize) {
        Page<PmsProject> page = new Page<>(pageNum, pageSize);
        return pmsProjectMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public PmsProject getById(Long id) {
        return pmsProjectMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(PmsProject entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        pmsProjectMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(PmsProject entity) {
        entity.setUpdateTime(LocalDateTime.now());
        pmsProjectMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        pmsProjectMapper.deleteById(id);
    }

    @Override
    public List<PmsProject> listAll() {
        return pmsProjectMapper.selectList(new LambdaQueryWrapper<>());
    }

}