package com.dp.plat.ehr.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.ehr.dao.EhrEmpPowerMapper;
import com.dp.plat.ehr.entity.EhrEmpPower;
import com.dp.plat.ehr.service.IEhrEmpPowerService;

/**
 *
 * Created by CodeGenerator
 */
@Service("ehrEmpPowerService")
public class EhrEmpPowerService extends AbstractBaseService<EhrEmpPowerMapper, EhrEmpPower> implements IEhrEmpPowerService {

	@Override
	public EhrEmpPower selectByEmpID(Integer empID) {
		return dao.selectByEmpID(empID);
	}
	
	@Override
	public void insertEhrDepPower() {
		dao.clearEhrDepPower();
		dao.setGroupConcatMaxLen(102400);
		dao.insertEhrDepPower();
	}
	
	@Override
	public void insertEhrEmpPower() {
		dao.clearEhrEmpPower();
		dao.setGroupConcatMaxLen(102400);
		dao.insertEhrEmpPower();
	}
}