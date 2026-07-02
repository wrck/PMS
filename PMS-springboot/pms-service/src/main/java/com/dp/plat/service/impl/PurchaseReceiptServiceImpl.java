package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.PurchaseReceiptMapper;
import com.dp.plat.model.entity.PurchaseReceipt;
import com.dp.plat.service.PurchaseReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购收货服务 - migrated from Struts
 */
@Service
public class PurchaseReceiptServiceImpl implements PurchaseReceiptService {

    @Autowired
    private PurchaseReceiptMapper purchaseReceiptMapper;

    @Override
    public IPage<PurchaseReceipt> queryPage(Integer pageNum, Integer pageSize) {
        Page<PurchaseReceipt> page = new Page<>(pageNum, pageSize);
        return purchaseReceiptMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public PurchaseReceipt getById(Long id) {
        return purchaseReceiptMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(PurchaseReceipt entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        purchaseReceiptMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(PurchaseReceipt entity) {
        entity.setUpdateTime(LocalDateTime.now());
        purchaseReceiptMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        purchaseReceiptMapper.deleteById(id);
    }

    @Override
    public List<PurchaseReceipt> listAll() {
        return purchaseReceiptMapper.selectList(new LambdaQueryWrapper<>());
    }

}