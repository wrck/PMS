package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.dao.DispatchSettlementMapper;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 *
 * Created by CodeGenerator
 */
@Service("dispatchSettlementService")
public class DispatchSettlementService extends AbstractBaseService<DispatchSettlementMapper, DispatchSettlement> implements IDispatchSettlementService {

	@Override
	public long countSettlementWidthDispatchPageable(PageParam<Object> pageParam) {
		return dao.countSettlementWidthDispatchPageable(pageParam);
	}

	@Override
	public List<Object> selectSettlementWidthDispatchPageable(PageParam<Object> pageParam) {
		return dao.selectSettlementWidthDispatchPageable(pageParam);
	}
}
