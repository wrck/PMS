package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.JobMapper;
import com.dp.plat.model.entity.Job;
import com.dp.plat.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 岗位服务 - migrated from Struts
 */
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public IPage<Job> queryPage(Integer pageNum, Integer pageSize) {
        Page<Job> page = new Page<>(pageNum, pageSize);
        return jobMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public Job getById(Long id) {
        return jobMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(Job entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        jobMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Job entity) {
        entity.setUpdateTime(LocalDateTime.now());
        jobMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        jobMapper.deleteById(id);
    }

    @Override
    public List<Job> listAll() {
        return jobMapper.selectList(new LambdaQueryWrapper<>());
    }

}