package com.dp.plat.warrantyCallback.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;

import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.Project;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.warrantyCallback.entity.ProjectWarrantyCallback;
import com.dp.plat.warrantyCallback.vo.ProjectWarrantyCallbackVO;
import com.dp.plat.warrantyCallback.vo.WarrantyCallbackPageParam;

public interface WarrantyCallbackDao {
	/**
	 * @param projectWarrantyCallbackId
	 * @return
	 */
	ProjectWarrantyCallback selectProjectWarrantyCallbackById(Integer projectWarrantyCallbackId);

	/**
	 * @param projectWarrantyCallbackId
	 * @return
	 */
	ProjectWarrantyCallbackVO selectProjectWarrantyCallbackVOById(Integer projectWarrantyCallbackId);

	/**
	 * @param projectWarrantyCallbackProject
	 * @return
	 */
	List<ProjectWarrantyCallback> selectProjectWarrantyCallbackList(ProjectWarrantyCallback projectWarrantyCallbackProject);

	/**
	 * @param projectWarrantyCallbackProject
	 * @return
	 */
	List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOList(ProjectWarrantyCallback projectWarrantyCallbackProject);

	
	/**
	 * @param project
	 * @return
	 */
	List<Project> queryProjectList(Project project);

	/**
	 * @param projectWarrantyCallback
	 * @return
	 */
	List<Project> queryProjectList(ProjectWarrantyCallback projectWarrantyCallback);

	/**
	 * @param projectWarrantyCallback
	 */
	void insertProjectWarrantyCallback(ProjectWarrantyCallback projectWarrantyCallback);

	/**
	 * @param projectWarrantyCallback
	 */
	void insertProjectWarrantyCallbackSelective(ProjectWarrantyCallback projectWarrantyCallback);
	
	/**
	 * @param projectWarrantyCallback
	 */
	void updateProjectWarrantyCallbackByIdSelective(ProjectWarrantyCallback projectWarrantyCallback);

	/**
	 * @param id
	 * @return
	 */
	int queryCallBackQuesnaireVersion(Integer id);


	/**
	 * @param projectIds
	 * @return
	 */
	List<ProjectWarrantyCallbackVO> queryWarrantyCallbackInfoForProject(String projectIds);

	/**
	 * 服务经理查询被驳回的转包申请，显示在待办事项中
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectRejectedProjectWarrantyCallbackList(HashMap<String, Object> params);

	/**
	 * 转包列表数据导出
	 * @param projectWarrantyCallbackVO
	 * @return
	 */
	List<ProjectWarrantyCallbackVO> queryWarrantyCallbackExportData(ProjectWarrantyCallbackVO projectWarrantyCallbackVO);

	void updateWarrantyCallbackEvaluationHeader(PmClEvaluationHeader evaluationHeader);

	int insertWarrantyCallbackEvaluationHeader(PmClEvaluationHeader evaluationHeader);

	List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOListPageable(WarrantyCallbackPageParam pageParam);

	Integer countProjectWarrantyCallbackVOListPageable(WarrantyCallbackPageParam pageParam);

	List<Task> queryWarrantyCallbackTaskList(HashMap<String, Object> params);

	List<Map<String, Object>> selectProjectWarrantyCallbackMapList(ProjectWarrantyCallbackVO projectWarrantyCallback,
			DisplayParam displayParam);

	List<Map<String, Object>> selectProjectWarranty(ProjectWarrantyCallbackVO projectWarrantyCallback,
			DisplayParam displayParam);

	List<Map<String, Object>> selectCustomerProjectWarrantyCallbackStatistics(
			ProjectWarrantyCallbackVO projectWarrantyCallback, DisplayParam displayParam);

}
