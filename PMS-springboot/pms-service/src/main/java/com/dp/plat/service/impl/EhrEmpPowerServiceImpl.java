package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.EhrEmpPowerMapper;
import com.dp.plat.model.entity.EhrEmpPower;
import com.dp.plat.service.EhrEmpPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EHR员工权限服务 - migrated from Struts
 */
@Service
public class EhrEmpPowerServiceImpl implements EhrEmpPowerService {

    @Autowired
    private EhrEmpPowerMapper ehrEmpPowerMapper;

    @Override
    public IPage<EhrEmpPower> queryPage(Integer pageNum, Integer pageSize) {
        Page<EhrEmpPower> page = new Page<>(pageNum, pageSize);
        return ehrEmpPowerMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public EhrEmpPower getById(Long id) {
        return ehrEmpPowerMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(EhrEmpPower entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        ehrEmpPowerMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(EhrEmpPower entity) {
        entity.setUpdateTime(LocalDateTime.now());
        ehrEmpPowerMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ehrEmpPowerMapper.deleteById(id);
    }

    @Override
    public List<EhrEmpPower> listAll() {
        return ehrEmpPowerMapper.selectList(new LambdaQueryWrapper<>());
    }

}