package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.InvoiceProviderInfo;
import java.util.List;

/**
 * 发票供应商信息服务 - migrated from Struts
 */
public interface InvoiceProviderInfoService {

    IPage<InvoiceProviderInfo> queryPage(Integer pageNum, Integer pageSize);

    InvoiceProviderInfo getById(Long id);

    void add(InvoiceProviderInfo entity);

    void update(InvoiceProviderInfo entity);

    void delete(Long id);

    List<InvoiceProviderInfo> listAll();

}