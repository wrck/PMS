package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.RoleMenuPowerMapper;
import com.dp.plat.model.entity.RoleMenuPower;
import com.dp.plat.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色菜单服务 - migrated from Struts
 */
@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private RoleMenuPowerMapper roleMenuPowerMapper;

    @Override
    public IPage<RoleMenuPower> queryPage(Integer pageNum, Integer pageSize) {
        Page<RoleMenuPower> page = new Page<>(pageNum, pageSize);
        return roleMenuPowerMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public RoleMenuPower getById(Long id) {
        return roleMenuPowerMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(RoleMenuPower entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        roleMenuPowerMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(RoleMenuPower entity) {
        entity.setUpdateTime(LocalDateTime.now());
        roleMenuPowerMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleMenuPowerMapper.deleteById(id);
    }

    @Override
    public List<RoleMenuPower> listAll() {
        return roleMenuPowerMapper.selectList(new LambdaQueryWrapper<>());
    }

}