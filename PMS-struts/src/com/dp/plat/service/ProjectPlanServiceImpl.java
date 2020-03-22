package com.dp.plat.service;

import java.util.List;

import com.dp.plat.dao.ProjectPlanDao;
import com.dp.plat.data.bean.ProjectPlan;


public class ProjectPlanServiceImpl extends BaseServiceImpl implements ProjectPlanService{
	private ProjectPlanDao projectPlanDao;

	public ProjectPlanDao getProjectPlanDao() {
		return projectPlanDao;
	}

	public void setProjectPlanDao(ProjectPlanDao projectPlanDao) {
		this.projectPlanDao = projectPlanDao;
	}

	@Override
	public List<ProjectPlan> queryProjectPlanListByContractNo(String contractNo) {
		return projectPlanDao.queryProjectPlanListByContractNo(contractNo);
	}
	
}
