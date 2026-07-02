package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.SysDepartmentMapper;
import com.dp.plat.model.entity.SysDepartment;
import com.dp.plat.service.EhrDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EHR部门服务 - migrated from Struts
 */
@Service
public class EhrDepartmentServiceImpl implements EhrDepartmentService {

    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public IPage<SysDepartment> queryPage(Integer pageNum, Integer pageSize) {
        Page<SysDepartment> page = new Page<>(pageNum, pageSize);
        return sysDepartmentMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public SysDepartment getById(Long id) {
        return sysDepartmentMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(SysDepartment entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysDepartmentMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(SysDepartment entity) {
        entity.setUpdateTime(LocalDateTime.now());
        sysDepartmentMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sysDepartmentMapper.deleteById(id);
    }

    @Override
    public List<SysDepartment> listAll() {
        return sysDepartmentMapper.selectList(new LambdaQueryWrapper<>());
    }

}