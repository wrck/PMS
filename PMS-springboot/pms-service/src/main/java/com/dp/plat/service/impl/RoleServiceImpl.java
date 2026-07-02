package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.SysRoleMapper;
import com.dp.plat.model.entity.SysRole;
import com.dp.plat.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务 - migrated from Struts
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public IPage<SysRole> queryPage(Integer pageNum, Integer pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        return sysRoleMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public SysRole getById(Long id) {
        return sysRoleMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(SysRole entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(SysRole entity) {
        entity.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sysRoleMapper.deleteById(id);
    }

    @Override
    public List<SysRole> listAll() {
        return sysRoleMapper.selectList(new LambdaQueryWrapper<>());
    }

}