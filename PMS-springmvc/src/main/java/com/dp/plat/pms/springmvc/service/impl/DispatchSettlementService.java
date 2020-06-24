package com.dp.plat.pms.springmvc.service.impl;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.dao.DispatchSettlementMapper;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.vo.SettlementVO;
import com.dp.plat.subcontract.entity.SubcontractPayment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	public List<SettlementVO> querySSEDispatchSettlementPaymentList() {
		return dao.querySSEDispatchSettlementPaymentList();
	}

	@Override
	public void saveSettlementPayment(List<SettlementVO> settlementPaymentList) {
		saveSettlementPayment(settlementPaymentList, null);
	}

	@Override
	@Transactional
	public void saveSettlementPayment(List<SettlementVO> settlementPaymentList, Integer[] delIds) {
		// 删除原来的付款信息
		if (delIds != null && delIds.length > 0) {
			for (Integer id : delIds) {
				DispatchSettlement temp = new DispatchSettlement();
				temp.setId(id);
				temp.setDisabled(true);
				this.updateByPrimaryKeySelective(temp);
			}
		}
		if (settlementPaymentList != null && !settlementPaymentList.isEmpty()) {
			for (SettlementVO settlementVO : settlementPaymentList) {
				if (settlementVO.getId() != null) {
					this.updateByPrimaryKeySelective(settlementVO);
				} else {
					this.insertSelective(settlementVO);
				}
			}
		}
	}
	
}
