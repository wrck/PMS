package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.InvoiceProviderInfoMapper;
import com.dp.plat.model.entity.InvoiceProviderInfo;
import com.dp.plat.service.InvoiceProviderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发票供应商信息服务 - migrated from Struts
 */
@Service
public class InvoiceProviderInfoServiceImpl implements InvoiceProviderInfoService {

    @Autowired
    private InvoiceProviderInfoMapper invoiceProviderInfoMapper;

    @Override
    public IPage<InvoiceProviderInfo> queryPage(Integer pageNum, Integer pageSize) {
        Page<InvoiceProviderInfo> page = new Page<>(pageNum, pageSize);
        return invoiceProviderInfoMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public InvoiceProviderInfo getById(Long id) {
        return invoiceProviderInfoMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(InvoiceProviderInfo entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        invoiceProviderInfoMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(InvoiceProviderInfo entity) {
        entity.setUpdateTime(LocalDateTime.now());
        invoiceProviderInfoMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        invoiceProviderInfoMapper.deleteById(id);
    }

    @Override
    public List<InvoiceProviderInfo> listAll() {
        return invoiceProviderInfoMapper.selectList(new LambdaQueryWrapper<>());
    }

}