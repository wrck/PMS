package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;

/**
 *
 * Created by CodeGenerator
 */
public interface IDispatchSettlementService extends IAbstractBaseService<DispatchSettlement> {

    void insertOrUpdateSelective(SettlementVO settlement);
    
    void settlementSubmit(Integer id, SettlementVO settlement);

	long countSettlementWidthDispatchPageable(PageParam<Object> pageParam);

	List<Object> selectSettlementWidthDispatchPageable(PageParam<Object> pageParam);

	/**
	 * 查询匹配到带SSE的结算信息
	 * @return
	 */
	List<SettlementVO> querySSEDispatchSettlementPaymentList();

	void saveSettlementPayment(List<SettlementVO> settlementPaymentList);

	void saveSettlementPayment(List<SettlementVO> settlementPaymentList, Integer[] delIds);

}
