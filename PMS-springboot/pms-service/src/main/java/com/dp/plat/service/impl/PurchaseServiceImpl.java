package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.PurchaseMapper;
import com.dp.plat.model.entity.Purchase;
import com.dp.plat.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购服务 - migrated from Struts
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Override
    public IPage<Purchase> queryPage(Integer pageNum, Integer pageSize) {
        Page<Purchase> page = new Page<>(pageNum, pageSize);
        return purchaseMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public Purchase getById(Long id) {
        return purchaseMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(Purchase entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        purchaseMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Purchase entity) {
        entity.setUpdateTime(LocalDateTime.now());
        purchaseMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        purchaseMapper.deleteById(id);
    }

    @Override
    public List<Purchase> listAll() {
        return purchaseMapper.selectList(new LambdaQueryWrapper<>());
    }

}