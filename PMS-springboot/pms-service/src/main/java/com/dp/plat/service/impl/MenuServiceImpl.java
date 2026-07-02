package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.UserMenuMapper;
import com.dp.plat.model.entity.UserMenu;
import com.dp.plat.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单服务 - migrated from Struts
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private UserMenuMapper userMenuMapper;

    @Override
    public IPage<UserMenu> queryPage(Integer pageNum, Integer pageSize) {
        Page<UserMenu> page = new Page<>(pageNum, pageSize);
        return userMenuMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public UserMenu getById(Long id) {
        return userMenuMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(UserMenu entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        userMenuMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(UserMenu entity) {
        entity.setUpdateTime(LocalDateTime.now());
        userMenuMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userMenuMapper.deleteById(id);
    }

    @Override
    public List<UserMenu> listAll() {
        return userMenuMapper.selectList(new LambdaQueryWrapper<>());
    }

}