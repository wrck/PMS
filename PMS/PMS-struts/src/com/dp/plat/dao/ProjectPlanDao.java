package com.dp.plat.dao;

import java.util.List;

import com.dp.plat.data.bean.ProjectPlan;

public interface ProjectPlanDao {

	/**
	 * 根据合同号查询计划列表
	 * @param contractNo
	 * @return
	 */
	public List<ProjectPlan> queryProjectPlanListByContractNo(String contractNo);

}
