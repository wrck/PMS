package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.PurchaseLine;
import java.util.List;

/**
 * 采购行服务 - migrated from Struts
 */
public interface PurchaseLineService {

    IPage<PurchaseLine> queryPage(Integer pageNum, Integer pageSize);

    PurchaseLine getById(Long id);

    void add(PurchaseLine entity);

    void update(PurchaseLine entity);

    void delete(Long id);

    List<PurchaseLine> listAll();

}