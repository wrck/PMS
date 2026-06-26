package com.dp.plat.ehr.service;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.ehr.entity.EhrEmpPower;

/**
 *
 * Created by CodeGenerator
 */
public interface IEhrEmpPowerService extends IAbstractBaseService<EhrEmpPower>{

	/**
	 * 根绝empID查询绩效计划的权限
	 * @param empID
	 * @return 
	 */
	EhrEmpPower selectByEmpID(Integer empID);

	/**
	 * 插入员工ehr的部门权限
	 */
	void insertEhrDepPower();

	/**
	 * 插入员工ehr的员工权限
	 */
	void insertEhrEmpPower();
}