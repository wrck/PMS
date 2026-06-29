package com.dp.plat.ehr.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.ehr.entity.EhrEmpPower;

public interface EhrEmpPowerMapper extends AbstractBaseMapper<EhrEmpPower> {

	EhrEmpPower selectByEmpID(Integer empID);

	/**
	 * 插入员工ehr的部门权限
	 */
	void insertEhrDepPower();

	/**
	 * 插入员工ehr的员工权限
	 */
	void insertEhrEmpPower();

	/**
	 * 清除原来的员工ehr部门权限
	 */
	void clearEhrDepPower();
	
	/**
	 * 清除原来的员工ehr员工权限
	 */
	void clearEhrEmpPower();

	void setGroupConcatMaxLen(int i);
}