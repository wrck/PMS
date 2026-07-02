package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.SysUserMapper;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息服务 - migrated from Struts
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<SysUser> queryPage(Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        return sysUserMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(SysUser entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(SysUser entity) {
        entity.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sysUserMapper.deleteById(id);
    }

    @Override
    public List<SysUser> listAll() {
        return sysUserMapper.selectList(new LambdaQueryWrapper<>());
    }

}