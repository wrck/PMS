package com.dp.plat.warrantyCallback.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Project;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BaseService;
import com.dp.plat.warrantyCallback.entity.ProjectWarrantyCallback;
import com.dp.plat.warrantyCallback.vo.ProjectWarrantyCallbackVO;
import com.dp.plat.warrantyCallback.vo.WarrantyCallbackPageParam;

public interface WarrantyCallbackService extends BaseService {

	/**
	 * 查询列表
	 * 
	 * @param warrantyCallbackProject
	 */
	List<ProjectWarrantyCallback> selectProjectWarrantyCallbackList(ProjectWarrantyCallback warrantyCallbackProject);

	/**
	 * @param warrantyCallbackProject
	 * @return
	 */
	List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOList(ProjectWarrantyCallback warrantyCallbackProject);

	/**
	 * @param project
	 * @return
	 */
	List<Project> queryProjectList(Project project);

	/**
	 * @param warrantyCallback
	 * @return
	 */
	List<Project> queryProjectList(ProjectWarrantyCallback warrantyCallback);

	/**
	 * @param warrantyCallback
	 */
	void insertProjectWarrantyCallback(ProjectWarrantyCallback warrantyCallback);

	/**
	 * @param warrantyCallback
	 */
	void insertProjectWarrantyCallbackSelective(ProjectWarrantyCallback warrantyCallback);
	

	/**
	 * @param warrantyCallback
	 */
	void updateProjectWarrantyCallbackByIdSelective(ProjectWarrantyCallback warrantyCallback);

	/**
	 * @param id
	 * @return
	 */
	ProjectWarrantyCallback selectProjectWarrantyCallbackById(Integer id);
	
	/**
	 * @param id
	 * @return
	 */
	ProjectWarrantyCallbackVO selectProjectWarrantyCallbackVOById(Integer id);

	List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOListPageable(WarrantyCallbackPageParam pageParam);

	void insertOrUpdateProjectWarrantyCallback(ProjectWarrantyCallbackVO projectWarrantyCallback);

	List<Map<String, Object>> selectProjectWarrantyCallbackMapList(ProjectWarrantyCallbackVO projectWarrantyCallback,
			DisplayParam displayParam);

	Map<String, Object> selectProjectWarrantyByProjectId(Integer projectId);

	List<Map<String, Object>> selectProjectWarranty(ProjectWarrantyCallbackVO projectWarrantyCallback);

	List<Map<String, Object>> selectProjectWarranty(ProjectWarrantyCallbackVO projectWarrantyCallback,
			DisplayParam displayParam);

	List<Map<String, Object>> selectCustomerProjectWarrantyCallbackStatistics(
			ProjectWarrantyCallbackVO projectWarrantyCallback, DisplayParam displayParam);

    /**
     * 填充项目维保信息
     * @param projectWarrantyCallback
     * @return filled
     */
    ProjectWarrantyCallbackVO fillProjectWarrantyInfo(ProjectWarrantyCallbackVO projectWarrantyCallback);


}
