package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.PurchaseReceiptLineMapper;
import com.dp.plat.model.entity.PurchaseReceiptLine;
import com.dp.plat.service.PurchaseReceiptLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购收货行服务 - migrated from Struts
 */
@Service
public class PurchaseReceiptLineServiceImpl implements PurchaseReceiptLineService {

    @Autowired
    private PurchaseReceiptLineMapper purchaseReceiptLineMapper;

    @Override
    public IPage<PurchaseReceiptLine> queryPage(Integer pageNum, Integer pageSize) {
        Page<PurchaseReceiptLine> page = new Page<>(pageNum, pageSize);
        return purchaseReceiptLineMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public PurchaseReceiptLine getById(Long id) {
        return purchaseReceiptLineMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(PurchaseReceiptLine entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        purchaseReceiptLineMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(PurchaseReceiptLine entity) {
        entity.setUpdateTime(LocalDateTime.now());
        purchaseReceiptLineMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        purchaseReceiptLineMapper.deleteById(id);
    }

    @Override
    public List<PurchaseReceiptLine> listAll() {
        return purchaseReceiptLineMapper.selectList(new LambdaQueryWrapper<>());
    }

}