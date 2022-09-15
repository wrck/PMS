package com.dp.plat.warrantyCallback.service.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.context.UserContext;
import com.dp.plat.dao.PmClosedLoopDao;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BaseServiceImpl;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.SendMailService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.service.WorkFlowService;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.UploadFileUtil;
import com.dp.plat.util.UserUtil;
import com.dp.plat.warrantyCallback.dao.WarrantyCallbackDao;
import com.dp.plat.warrantyCallback.entity.ProjectWarrantyCallback;
import com.dp.plat.warrantyCallback.service.WarrantyCallbackService;
import com.dp.plat.warrantyCallback.vo.ProjectWarrantyCallbackVO;
import com.dp.plat.warrantyCallback.vo.WarrantyCallbackPageParam;

public class WarrantyCallbackServiceImpl extends BaseServiceImpl implements WarrantyCallbackService {

	/**
	 * 上传路径 /upload/projectWarrantyCallback/
	 */
//	private static final String uploadDir = File.separator + "upload" + File.separator + "projectWarrantyCallback" + File.separator;
	private static final String uploadDir = File.separator + UploadFileUtil.UPLOAD_PATH + File.separator + "projectWarrantyCallback" + File.separator;

	private WarrantyCallbackDao dao;
	private BasicDataService basicDataService;
	private CallBackService callBackService;
	private TaskService taskService;
	private SendMailService sendMailService;
	private UserManageService userManageService;
	private PmClosedLoopDao pmClosedLoopDao;
	private WorkFlowService workFlowService;
	private DepartmentManageService departmentManageService;

	public void setWarrantyCallbackDao(WarrantyCallbackDao projectWarrantyCallbackDao) {
		this.dao = projectWarrantyCallbackDao;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public void setCallBackService(CallBackService callBackService) {
		this.callBackService = callBackService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setSendMailService(SendMailService sendMailService) {
		this.sendMailService = sendMailService;
	}

	public WarrantyCallbackDao getWarrantyCallbackDao() {
		return dao;
	}

	public BasicDataService getBasicDataService() {
		return basicDataService;
	}

	public CallBackService getCallBackService() {
		return callBackService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public SendMailService getSendMailService() {
		return sendMailService;
	}

	public UserManageService getUserManageService() {
		return userManageService;
	}

	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}

	public PmClosedLoopDao getPmClosedLoopDao() {
		return pmClosedLoopDao;
	}

	public void setPmClosedLoopDao(PmClosedLoopDao pmClosedLoopDao) {
		this.pmClosedLoopDao = pmClosedLoopDao;
	}

	public WorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(WorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public DepartmentManageService getDepartmentManageService() {
		return departmentManageService;
	}

	public void setDepartmentManageService(DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	@Override
	public ProjectWarrantyCallback selectProjectWarrantyCallbackById(Integer projectWarrantyCallbackId) {
		return dao.selectProjectWarrantyCallbackById(projectWarrantyCallbackId);
	}

	@Override
	public ProjectWarrantyCallbackVO selectProjectWarrantyCallbackVOById(Integer projectWarrantyCallbackId) {
		return dao.selectProjectWarrantyCallbackVOById(projectWarrantyCallbackId);
	}

	@Override
	public List<ProjectWarrantyCallback> selectProjectWarrantyCallbackList(ProjectWarrantyCallback projectWarrantyCallbackProject) {
		return dao.selectProjectWarrantyCallbackList(projectWarrantyCallbackProject);
	}

	@Override
	public List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOList(ProjectWarrantyCallback projectWarrantyCallbackProject) {
		return dao.selectProjectWarrantyCallbackVOList(projectWarrantyCallbackProject);
	}

	@Override
	public List<ProjectWarrantyCallbackVO> selectProjectWarrantyCallbackVOListPageable(WarrantyCallbackPageParam pageParam) {
		List<ProjectWarrantyCallbackVO> list = new ArrayList<>();
		try {
			DisplayParam displayParam = pageParam.getDisplayParam();
			if (displayParam == null) {
				displayParam = new DisplayParam();
			}
			displayParam.getParam();

			Integer totalcount = this.countProjectWarrantyCallbackVOListPageable(pageParam);
			if (!displayParam.getExport()) {
				displayParam.setPagesize(50);
				displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
			} else {
				displayParam.setPagesize(totalcount);
				displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
			}
			displayParam.setTotalcount(totalcount);
			list = dao.selectProjectWarrantyCallbackVOListPageable(pageParam);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param pageParam
	 */
	private Integer countProjectWarrantyCallbackVOListPageable(WarrantyCallbackPageParam pageParam) {
		return dao.countProjectWarrantyCallbackVOListPageable(pageParam);
	}

	@Override
	public List<Project> queryProjectList(Project project) {
		return dao.queryProjectList(project);
	}

	@Override
	public List<Project> queryProjectList(ProjectWarrantyCallback projectWarrantyCallback) {
		return dao.queryProjectList(projectWarrantyCallback);
	}

	@Override
	@Transactional
	public void insertProjectWarrantyCallback(ProjectWarrantyCallback projectWarrantyCallback) {
		projectWarrantyCallback.setCreateBy(getLoginName());
		projectWarrantyCallback.setCreateTime(new Date());
		dao.insertProjectWarrantyCallback(projectWarrantyCallback);
	}

	@Override
	@Transactional
	public void insertProjectWarrantyCallbackSelective(ProjectWarrantyCallback projectWarrantyCallback) {
		projectWarrantyCallback.setCreateBy(getLoginName());
		projectWarrantyCallback.setCreateTime(new Date());
		dao.insertProjectWarrantyCallbackSelective(projectWarrantyCallback);
	}

	@Override
	public void updateProjectWarrantyCallbackByIdSelective(ProjectWarrantyCallback projectWarrantyCallback) {
		projectWarrantyCallback.setUpdateBy(getLoginName());
		projectWarrantyCallback.setUpdateTime(new Date());
		dao.updateProjectWarrantyCallbackByIdSelective(projectWarrantyCallback);
	}

	@Override
	public void insertOrUpdateProjectWarrantyCallback(ProjectWarrantyCallbackVO projectWarrantyCallback) {
		if (projectWarrantyCallback == null) {
			return;
		}
		if (projectWarrantyCallback.getId() == null || projectWarrantyCallback.getId() == 0) {
			this.insertProjectWarrantyCallbackSelective(projectWarrantyCallback);
		} else {
			this.updateProjectWarrantyCallbackByIdSelective(projectWarrantyCallback);
		}
	}

	@Override
	public List<Map<String, Object>> selectProjectWarrantyCallbackMapList(
			ProjectWarrantyCallbackVO projectWarrantyCallback, DisplayParam displayParam) {
		return dao.selectProjectWarrantyCallbackMapList(projectWarrantyCallback, displayParam);
	}
	
	@Override
	public List<Map<String, Object>> selectProjectWarranty(ProjectWarrantyCallbackVO projectWarrantyCallback) {
		return dao.selectProjectWarranty(projectWarrantyCallback, null);
	}

	@Override
	public List<Map<String, Object>> selectProjectWarranty(ProjectWarrantyCallbackVO projectWarrantyCallback, DisplayParam displayParam) {
		return dao.selectProjectWarranty(projectWarrantyCallback, displayParam);
	}

	@Override
	public List<Map<String, Object>> selectCustomerProjectWarrantyCallbackStatistics(ProjectWarrantyCallbackVO projectWarrantyCallback, DisplayParam displayParam) {
		return dao.selectCustomerProjectWarrantyCallbackStatistics(projectWarrantyCallback, displayParam);
	}

	
	/**
	 * 
	 * @param taskKey
	 * @return
	 */
	public List<Task> queryWarrantyCallbackTaskList(String taskKey) {
		return queryWarrantyCallbackTaskList(taskKey, null, null, null, null);
	}

	/**
	 * 
	 * @param taskKey
	 * @param taskId
	 * @return
	 */
	public List<Task> queryWarrantyCallbackTaskList(String taskKey, String taskId) {
		return queryWarrantyCallbackTaskList(taskKey, null, taskId);
	}

	/**
	 * 
	 * @param taskKey
	 * @param taskId
	 * @return
	 */
	public List<Task> queryWarrantyCallbackTaskList(String taskKey, Integer projectWarrantyCallbackId) {
		return queryWarrantyCallbackTaskList(taskKey, projectWarrantyCallbackId, null);
	}

	/**
	 * 
	 * @param taskKey
	 * @param taskId
	 * @return
	 */
	public List<Task> queryWarrantyCallbackTaskList(String taskKey, Integer projectWarrantyCallbackId, String taskId) {
		return queryWarrantyCallbackTaskList(taskKey, null, projectWarrantyCallbackId, taskId);
	}

	public List<Task> queryWarrantyCallbackTaskList(String taskKey, String roleGroup, Integer projectWarrantyCallbackId) {
		return queryWarrantyCallbackTaskList(taskKey, roleGroup, projectWarrantyCallbackId, null, null);
	}

	/**
	 * 
	 * @param taskKey
	 * @param roleGroup
	 * @param projectWarrantyCallbackId
	 * @param taskId
	 * @return
	 */
	public List<Task> queryWarrantyCallbackTaskList(String taskKey, String roleGroup, Integer projectWarrantyCallbackId, String taskId) {
		return queryWarrantyCallbackTaskList(taskKey, roleGroup, projectWarrantyCallbackId, taskId, new HashMap<String, Object>());
	}

	/**
	 * 
	 * @param taskKey
	 * @param roleGroup
	 * @param projectWarrantyCallbackId
	 * @param taskId
	 * @return
	 */
	public List<Task> queryWarrantyCallbackTaskList(String taskKey, String roleGroup, Integer projectWarrantyCallbackId,
			HashMap<String, Object> params) {
		return queryWarrantyCallbackTaskList(taskKey, roleGroup, projectWarrantyCallbackId, null, params);
	}

	/**
	 * 
	 * @param taskKey
	 * @param roleGroup
	 * @param projectWarrantyCallbackId
	 * @param taskId
	 * @return
	 */
	public List<Task> queryWarrantyCallbackTaskList(String taskKey, String roleGroup, Integer projectWarrantyCallbackId, String taskId,
			HashMap<String, Object> params) {
		// HashMap<String, Object> params = new HashMap<>();
		if (params == null) {
			params = new HashMap<>();
		}
		UserContext context = UserContext.getUserContext();
		params.put("assignee", getLoginName());
		params.put("areaPower", context.getUser().getAreapower());
		if (StringUtils.isNotBlank(taskKey)) {
			String[] taskKeys = taskKey.split(";");
			params.put("taskKey", taskKeys);
		}
		params.put("taskId", taskId);
		params.put("projectWarrantyCallbackId", projectWarrantyCallbackId);
		if (StringUtils.isBlank(roleGroup)) {
			if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
					|| context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
				roleGroup = "emRole";
				if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
					roleGroup += ",role_" + MessageUtil.ROLE_ENGINEEMANAGER_LEADER;
				}
				// params.put("roleGroup", "emRole");
			} else if (context.isHasRole(MessageUtil.ROLE_CALLBACKPER)) {
				// params.put("roleGroup", "cbRole");
				roleGroup = "cbRole";
			} else if (context.isHasRole(MessageUtil.ROLE_WARRANTY_CALLBACKER)) {
                // params.put("roleGroup", "wcbRole");
                roleGroup = "role_" + MessageUtil.ROLE_WARRANTY_CALLBACKER;
            } else if (context.isHasRole(MessageUtil.ROLE_AREA_LEADER)) {
				roleGroup = "zrRole,role_" + MessageUtil.ROLE_AREA_LEADER;
				params.put("checkProfitDep", "true");
			}
		}
		params.put("roleGroup", roleGroup);
		return dao.queryWarrantyCallbackTaskList(params);
	}

	/**
	 * 查询某角色的用户名字符串
	 * 
	 * @param roleStr
	 * @return
	 */
	@SuppressWarnings("unused")
	private StringBuilder getNextAssignPer(String roleStr) {
		User user = new User();
		user.setStatus(1); // 获取有效的回访人员或工程人员
		List<User> userList = userManageService.queryAllUserList(user);
		StringBuilder nextAssignPer = new StringBuilder();

		for (User userObj : userList) {
			if (userObj.getRoleids().contains(roleStr)) {
				nextAssignPer.append(userObj.getUsername() + ",");
			}
		}

		if (nextAssignPer.length() <= 0) {
			throw new RuntimeException("获取下一级审核人员出错");
		}

		return nextAssignPer;
	}

	/**
	 * 查询某角色的用户名字符串,以及姓名
	 * 
	 * @param roleId
	 * @return
	 */
	private String[] getNextAssignPer(int roleId) {
		return getNextAssignPer(roleId, null);
	}

	/**
	 * 查询某角色的用户名字符串,以及姓名
	 * 
	 * @param roleId
	 * @return
	 */
	private String[] getNextAssignPer(int roleId, String dpNo) {
		// 获取有效的回访人员或工程人员
		Map<String, String> params = new HashMap<>();
		String newdpNo = null;
		if (StringUtils.isNotBlank(dpNo)) {
			newdpNo = UserUtil.transferDepNo(dpNo);
		}
		params.put("roleid", String.valueOf(roleId));
		params.put("dpNo", dpNo);
		List<User> userList = userManageService.queryUserWithRoleIdAndDpNo(params);
		
		if (userList.isEmpty()) {
			params.remove("dpNo");
			params.put("areaPower", dpNo);
			userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
		}
		// 如果没找到，则查找转换后的部门对应觉得人员
		if (userList.isEmpty() && StringUtils.isNotBlank(newdpNo) && !newdpNo.equals(dpNo)) {
			params.clear();
			params.put("roleid", String.valueOf(roleId));
			params.put("dpNo", newdpNo);
			userList = userManageService.queryUserWithRoleIdAndDpNo(params);
			
			if (userList.isEmpty()) {
				params.remove("dpNo");
				params.put("areaPower", newdpNo);
				userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
			}
		}
		List<String> nextAssignPer = new ArrayList<>(userList.size());
		List<String> nextAssignName = new ArrayList<>(userList.size());
		List<String> nextAssignEmail = new ArrayList<>(userList.size());
		for (User userObj : userList) {
			nextAssignPer.add(userObj.getUsername());
			nextAssignName.add(userObj.getUsername() + "-" + userObj.getRealName());
			nextAssignEmail.add(userObj.getEmail());
		}
		
		if (nextAssignPer.size() <= 0) {
			throw new RuntimeException("获取下一级审核人员出错");
		}
		String[] nextAssigen = new String[] { 
			StringUtils.join(nextAssignPer, ","),
			StringUtils.join(nextAssignName, ","), 
			StringUtils.join(nextAssignEmail, ";"),
			String.valueOf(nextAssignPer.size())
		};
		return nextAssigen;
	}

	/**
	 * 将项目转包的所包含的项目转变成一个Table
	 * 
	 * @param projectList
	 * @return
	 */
	private String initProjectDetailTable(List<Project> projectList) {
		StringBuilder projectDetailHtml = new StringBuilder("");
		if (!projectList.isEmpty()) {
			for (Project project : projectList) {
				projectDetailHtml.append("<p style='font-size: 11pt; font-family: 宋体;'>")
						.append(project.getProjectName()).append("</p>");
			}
		}
		return projectDetailHtml.toString();
	}
}
