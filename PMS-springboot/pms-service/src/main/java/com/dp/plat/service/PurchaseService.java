package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.Purchase;
import java.util.List;

/**
 * 采购服务 - migrated from Struts
 */
public interface PurchaseService {

    IPage<Purchase> queryPage(Integer pageNum, Integer pageSize);

    Purchase getById(Long id);

    void add(Purchase entity);

    void update(Purchase entity);

    void delete(Long id);

    List<Purchase> listAll();

}