package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.UserLoginMapper;
import com.dp.plat.model.entity.UserLogin;
import com.dp.plat.service.UserLoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录记录服务 - migrated from Struts
 */
@Service
public class UserLoginRecordServiceImpl implements UserLoginRecordService {

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Override
    public IPage<UserLogin> queryPage(Integer pageNum, Integer pageSize) {
        Page<UserLogin> page = new Page<>(pageNum, pageSize);
        return userLoginMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public UserLogin getById(Long id) {
        return userLoginMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(UserLogin entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        userLoginMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(UserLogin entity) {
        entity.setUpdateTime(LocalDateTime.now());
        userLoginMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userLoginMapper.deleteById(id);
    }

    @Override
    public List<UserLogin> listAll() {
        return userLoginMapper.selectList(new LambdaQueryWrapper<>());
    }

}