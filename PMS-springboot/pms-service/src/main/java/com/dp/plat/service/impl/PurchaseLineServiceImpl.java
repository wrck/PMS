package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.PurchaseLineMapper;
import com.dp.plat.model.entity.PurchaseLine;
import com.dp.plat.service.PurchaseLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购行服务 - migrated from Struts
 */
@Service
public class PurchaseLineServiceImpl implements PurchaseLineService {

    @Autowired
    private PurchaseLineMapper purchaseLineMapper;

    @Override
    public IPage<PurchaseLine> queryPage(Integer pageNum, Integer pageSize) {
        Page<PurchaseLine> page = new Page<>(pageNum, pageSize);
        return purchaseLineMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public PurchaseLine getById(Long id) {
        return purchaseLineMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(PurchaseLine entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        purchaseLineMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(PurchaseLine entity) {
        entity.setUpdateTime(LocalDateTime.now());
        purchaseLineMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        purchaseLineMapper.deleteById(id);
    }

    @Override
    public List<PurchaseLine> listAll() {
        return purchaseLineMapper.selectList(new LambdaQueryWrapper<>());
    }

}