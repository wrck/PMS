package com.dp.plat.pms.springmvc.dao;

import java.util.List;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;

public interface DispatchSettlementMapper extends AbstractBaseMapper<DispatchSettlement> {

	long countSettlementWidthDispatchPageable(PageParam<Object> pageParam);

	List<Object> selectSettlementWidthDispatchPageable(PageParam<Object> pageParam);
}
