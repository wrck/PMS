package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.PmsProjectPlanMapper;
import com.dp.plat.model.entity.PmsProjectPlan;
import com.dp.plat.service.ProjectPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目计划服务 - migrated from Struts
 */
@Service
public class ProjectPlanServiceImpl implements ProjectPlanService {

    @Autowired
    private PmsProjectPlanMapper pmsProjectPlanMapper;

    @Override
    public IPage<PmsProjectPlan> queryPage(Integer pageNum, Integer pageSize) {
        Page<PmsProjectPlan> page = new Page<>(pageNum, pageSize);
        return pmsProjectPlanMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public PmsProjectPlan getById(Long id) {
        return pmsProjectPlanMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(PmsProjectPlan entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        pmsProjectPlanMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(PmsProjectPlan entity) {
        entity.setUpdateTime(LocalDateTime.now());
        pmsProjectPlanMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        pmsProjectPlanMapper.deleteById(id);
    }

    @Override
    public List<PmsProjectPlan> listAll() {
        return pmsProjectPlanMapper.selectList(new LambdaQueryWrapper<>());
    }

}