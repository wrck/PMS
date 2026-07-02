package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.PurchaseReceipt;
import java.util.List;

/**
 * 采购收货服务 - migrated from Struts
 */
public interface PurchaseReceiptService {

    IPage<PurchaseReceipt> queryPage(Integer pageNum, Integer pageSize);

    PurchaseReceipt getById(Long id);

    void add(PurchaseReceipt entity);

    void update(PurchaseReceipt entity);

    void delete(Long id);

    List<PurchaseReceipt> listAll();

}