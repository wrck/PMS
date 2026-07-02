package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.DispatchSettlement;
import java.util.List;

/**
 * 调度结算服务 - migrated from Struts
 */
public interface DispatchSettlementService {

    IPage<DispatchSettlement> queryPage(Integer pageNum, Integer pageSize);

    DispatchSettlement getById(Long id);

    void add(DispatchSettlement entity);

    void update(DispatchSettlement entity);

    void delete(Long id);

    List<DispatchSettlement> listAll();

}