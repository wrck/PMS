package com.dp.plat.pms.springmvc.service;

import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

import java.util.List;

import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;

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

    Result verifySettlementInvoice(DispatchSettlement settlement);

    /**
     * 查询去重后的发票明细
     * @param settlement
     * @return
     */
    List<FileInfo> selectDispatchSettlementInvoiceDetails(DispatchSettlement settlement);

}
