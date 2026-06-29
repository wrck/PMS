package com.dp.plat.dao;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.data.bean.ProjectPlan;


public class ProjectPlanDaoImpl extends BaseDao implements ProjectPlanDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectPlan> queryProjectPlanListByContractNo(String contractNo) {
	    if (StringUtils.isBlank(contractNo)) {
	        return Collections.emptyList();
	    }
		return getSqlMapClientTemplate().queryForList("query-projectplanlist-bycontractno", contractNo);
	}

}
