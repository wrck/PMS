package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.PurchaseReceiptLine;
import java.util.List;

/**
 * 采购收货行服务 - migrated from Struts
 */
public interface PurchaseReceiptLineService {

    IPage<PurchaseReceiptLine> queryPage(Integer pageNum, Integer pageSize);

    PurchaseReceiptLine getById(Long id);

    void add(PurchaseReceiptLine entity);

    void update(PurchaseReceiptLine entity);

    void delete(Long id);

    List<PurchaseReceiptLine> listAll();

}