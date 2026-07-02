package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.DispatchProjectMapper;
import com.dp.plat.model.entity.DispatchProject;
import com.dp.plat.service.DispatchProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调度项目服务 - migrated from Struts
 */
@Service
public class DispatchProjectServiceImpl implements DispatchProjectService {

    @Autowired
    private DispatchProjectMapper dispatchProjectMapper;

    @Override
    public IPage<DispatchProject> queryPage(Integer pageNum, Integer pageSize) {
        Page<DispatchProject> page = new Page<>(pageNum, pageSize);
        return dispatchProjectMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public DispatchProject getById(Long id) {
        return dispatchProjectMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(DispatchProject entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        dispatchProjectMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(DispatchProject entity) {
        entity.setUpdateTime(LocalDateTime.now());
        dispatchProjectMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        dispatchProjectMapper.deleteById(id);
    }

    @Override
    public List<DispatchProject> listAll() {
        return dispatchProjectMapper.selectList(new LambdaQueryWrapper<>());
    }

}