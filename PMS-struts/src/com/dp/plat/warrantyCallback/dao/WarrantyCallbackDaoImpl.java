package com.dp.plat.warrantyCallback.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import com.dp.plat.dao.BaseDao;
import com.dp.plat.dao.ProjectDao;
import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.Project;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.warrantyCallback.entity.ProjectWarrantyCallback;
import com.dp.plat.warrantyCallback.vo.ProjectWarrantyCallbackVO;
import com.dp.plat.warrantyCallback.vo.WarrantyCallbackPageParam;

public class WarrantyCallbackDaoImpl extends BaseDao implements WarrantyCallbackDao {

	private ProjectDao projectDao;

	public ProjectDao getProjectDao() {
		return projectDao;
	}

	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	@Override
	public ProjectWarrantyCallback selectProjectWarrantyCallbackById(Integer id) {
		ProjectWarrantyCallback warrantyCallback = new ProjectWarrantyCallback();
		warrantyCallback.setId(id);
		return (ProjectWarrantyCallback) getSqlMapClientTemplate().queryForObject("selectProjectWarrantyCallbackById",
				warrantyCallback);
	}

	@Override
	public ProjectWarrantyCallbackVO selectProjectWarrantyCallbackVOById(Integer id) {
		return (ProjectWarrantyCallbackVO) getSqlMapClientTemplate()
				.queryForObject("selectProjectWarrantyCallbackVOById", id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectWarrantyCallback> selectProjectWarrantyCallbackList(ProjectWarrantyCallback subcontractProject) {
		return getSqlMapClientTemplate().queryForList("selectProjectWarrantyCallbackList", subcontractProject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOList(
			ProjectWarrantyCallback subcontractProject) {
		return getSqlMapClientTemplate().queryForList("selectProjectWarrantyCallbackVOList", subcontractProject);
	}

	@Override
	public List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOListPageable(
			WarrantyCallbackPageParam pageParam) {
		return getSqlMapClientTemplate().queryForList("selectProjectWarrantyCallbackVOListPageable", pageParam);
	}

	@Override
	public Integer countProjectWarrantyCallbackVOListPageable(WarrantyCallbackPageParam pageParam) {
		return (Integer) getSqlMapClientTemplate().queryForObject("countProjectWarrantyCallbackVOListPageable",
				pageParam);
	}

	@Override
	public List<Project> queryProjectList(Project project) {
		if (StringUtils.isBlank(project.getContractNo())) {
			return new ArrayList<>();
		}
		String[] contractNoArr = project.getContractNo().split(",");
		List<String> contractNoList = Arrays.asList(contractNoArr);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("contractNos", contractNoList);
		return getSqlMapClientTemplate().queryForList("queryProjectList", params);
	}

	@Override
	public List<Project> queryProjectList(ProjectWarrantyCallback subcontract) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("projectIds", subcontract.getProjectIds());
		return getSqlMapClientTemplate().queryForList("queryProjectList", params);
	}

	@Override
	public void insertProjectWarrantyCallback(ProjectWarrantyCallback subcontract) {
		getSqlMapClientTemplate().insert("insertProjectWarrantyCallback", subcontract);
	}

	@Override
	public void insertProjectWarrantyCallbackSelective(ProjectWarrantyCallback subcontract) {
		getSqlMapClientTemplate().insert("insertProjectWarrantyCallbackSelective", subcontract);
	}

	@Override
	public void updateProjectWarrantyCallbackByIdSelective(ProjectWarrantyCallback subcontract) {
		getSqlMapClientTemplate().update("updateProjectWarrantyCallbackByIdSelective", subcontract);
	}

	@Override
	public List<Map<String, Object>> selectProjectWarrantyCallbackMapList(
			ProjectWarrantyCallbackVO projectWarrantyCallback, DisplayParam displayParam) {
		String quesType = "projectWarrantyCallback";
		try {
			String resultType = "1";
			if (displayParam != null && displayParam.getExport()) {
				resultType = null;
			} else if (displayParam != null && displayParam.getPagesize() == -1) {
//				displayParam = null;
			}
			Map<String, Object> questionColumns = projectDao.queryQuestionColumns(quesType, resultType);
			projectWarrantyCallback.setQuestionColumns(questionColumns);

			getSqlMapClientTemplate().insert("createTempQuesnaireResultTable", questionColumns);

			Map<Object, Object> params = new HashMap<>();
			params.put("model", projectWarrantyCallback);
			if (displayParam != null && displayParam.getPagesize() != -1) {
				params.put("hideQuesnaire", "true");
//				Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("countProjectWarrantyCallbackList", projectWarrantyCallback);
				Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("countProjectWarrantyCallbackMapList", params);
				params.remove("hideQuesnaire");
				displayParam.setTotalcount(totalcount);
				if (!(displayParam.getExport() || displayParam.getPagesize() == -1)) {
					params.put("hideFiles", "true");
//					displayParam.setPagesize(50);
					displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
				} else {
					displayParam.setPagesize(totalcount);
					displayParam.setOffset(0);
				}
			}
			params.put("displayParam", displayParam);

//            WarrantyCallbackPageParam warrantyCallbackPageParam = new WarrantyCallbackPageParam(displayParam);
//            BeanUtils.copyProperties(projectWarrantyCallback, warrantyCallbackPageParam);
//            List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectProjectWarrantyCallbackMapList", warrantyCallbackPageParam);
			List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectProjectWarrantyCallbackMapList", params);
			if (displayParam != null && displayParam.getPagesize() == -1) {
				displayParam.setPagesize(Math.max(list.size(), displayParam.getPagesize()));
				displayParam.setTotalcount(Math.max(list.size(), displayParam.getTotalcount()));
			}
			return list;
		} finally {
			getSqlMapClientTemplate().delete("deleteTempQuesnaireResultLineTable", quesType);
			getSqlMapClientTemplate().delete("deleteTempQuesnaireResultTable", quesType);
		}
	}

	@Override
	public List<Map<String, Object>> selectProjectWarranty(ProjectWarrantyCallbackVO projectWarrantyCallback,
			DisplayParam displayParam) {
//		if (displayParam != null && displayParam.getPagesize() == -1) {
//			displayParam = null;
//		}
		BeanMap beanMap = BeanMap.create(projectWarrantyCallback);
		Map params = new HashMap(beanMap);
		if (displayParam != null && displayParam.getPagesize() != -1) {
			Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("countProjectWarranty", params);
			displayParam.setTotalcount(totalcount);
			if (!(displayParam.getExport() || displayParam.getPagesize() == -1)) {
//				displayParam.setPagesize(50);
				displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
			} else {
				displayParam.setPagesize(totalcount);
				displayParam.setOffset(0);
			}
		}
//		params.put("model", projectWarrantyCallback);
		params.put("displayParam", displayParam);
		List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectProjectWarranty", params);

//        WarrantyCallbackPageParam warrantyCallbackPageParam = new WarrantyCallbackPageParam(displayParam);
//        BeanUtils.copyProperties(projectWarrantyCallback, warrantyCallbackPageParam);
//        List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectProjectWarranty", warrantyCallbackPageParam);
		if (displayParam != null && displayParam.getPagesize() == -1) {
			displayParam.setPagesize(Math.max(list.size(), displayParam.getPagesize()));
			displayParam.setTotalcount(Math.max(list.size(), displayParam.getTotalcount()));
		}
		return list;
	}
	
	@Override
	public List<Map<String, Object>> selectCustomerProjectWarrantyCallbackStatistics(ProjectWarrantyCallbackVO projectWarrantyCallback,
			DisplayParam displayParam) {
//		if (displayParam != null && displayParam.getPagesize() == -1) {
//			displayParam = null;
//		}
		BeanMap beanMap = BeanMap.create(projectWarrantyCallback);
		Map params = new HashMap(beanMap);
		if (displayParam != null && displayParam.getPagesize() != -1) {
			Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("countCustomerProjectWarrantyCallbackStatistics", params );
			displayParam.setTotalcount(totalcount);
			if (!(displayParam.getExport() || displayParam.getPagesize() == -1)) {
//				displayParam.setPagesize(50);
				displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
			} else {
				displayParam.setPagesize(totalcount);
				displayParam.setOffset(0);
			}
		}
		params.put("displayParam", displayParam);
		List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectCustomerProjectWarrantyCallbackStatistics", params);
		if (displayParam != null && displayParam.getPagesize() == -1) {
			displayParam.setPagesize(Math.max(list.size(), displayParam.getPagesize()));
			displayParam.setTotalcount(Math.max(list.size(), displayParam.getTotalcount()));
		}
		return list;
	}

	@Override
	public int insertWarrantyCallbackEvaluationHeader(PmClEvaluationHeader evaluationHeader) {
		return (int) getSqlMapClientTemplate().insert("insertWarrantyCallbackEvaluationHeader", evaluationHeader);
	}

	@Override
	public void updateWarrantyCallbackEvaluationHeader(PmClEvaluationHeader evaluationHeader) {
		getSqlMapClientTemplate().update("updateWarrantyCallbackEvaluationHeader", evaluationHeader);
	}

	@Override
	public int queryCallBackQuesnaireVersion(Integer id) {
		Object obj = getSqlMapClientTemplate().queryForObject("queryCallBackQuesnaireVersion", id);
		return obj == null ? 1 : (Integer) obj + 1;
	}

	@Override
	public List<ProjectWarrantyCallbackVO> queryWarrantyCallbackInfoForProject(String projectIds) {
		return getSqlMapClientTemplate().queryForList("queryWarrantyCallbackInfoForProject", projectIds);
	}

	@Override
	public List<Map<String, Object>> selectRejectedProjectWarrantyCallbackList(HashMap<String, Object> params) {
		return getSqlMapClientTemplate().queryForList("selectRejectedProjectWarrantyCallbackList", params);
	}

	@Override
	public List<ProjectWarrantyCallbackVO> queryWarrantyCallbackExportData(ProjectWarrantyCallbackVO subcontractVO) {
		return getSqlMapClientTemplate().queryForList("queryWarrantyCallbackExportData", subcontractVO);
	}

	@Override
	public List<Task> queryWarrantyCallbackTaskList(HashMap<String, Object> params) {
		return getSqlMapClientTemplate().queryForList("queryWarrantyCallbackTaskList", params);
	}

}