package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.DispatchSettlementMapper;
import com.dp.plat.model.entity.DispatchSettlement;
import com.dp.plat.service.DispatchSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调度结算服务 - migrated from Struts
 */
@Service
public class DispatchSettlementServiceImpl implements DispatchSettlementService {

    @Autowired
    private DispatchSettlementMapper dispatchSettlementMapper;

    @Override
    public IPage<DispatchSettlement> queryPage(Integer pageNum, Integer pageSize) {
        Page<DispatchSettlement> page = new Page<>(pageNum, pageSize);
        return dispatchSettlementMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public DispatchSettlement getById(Long id) {
        return dispatchSettlementMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(DispatchSettlement entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        dispatchSettlementMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(DispatchSettlement entity) {
        entity.setUpdateTime(LocalDateTime.now());
        dispatchSettlementMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        dispatchSettlementMapper.deleteById(id);
    }

    @Override
    public List<DispatchSettlement> listAll() {
        return dispatchSettlementMapper.selectList(new LambdaQueryWrapper<>());
    }

}