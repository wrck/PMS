package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.entity.DispatchSettlement;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;

/**
 *
 * Created by CodeGenerator
 */
public interface IDispatchSettlementService extends IAbstractBaseService<DispatchSettlement> {

	long countSettlementWidthDispatchPageable(PageParam<Object> pageParam);

	List<Object> selectSettlementWidthDispatchPageable(PageParam<Object> pageParam);
}
