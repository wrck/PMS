package com.dp.plat.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.WorkSpaceDao;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.DpActProcDesc;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.TaskQueryParam;
import com.dp.plat.prob.param.ProbParam;
import com.dp.plat.subcontract.service.SubcontractService;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.PmClosedLoopConstant;

public class WorkSpaceServiceImpl extends BaseServiceImpl implements WorkSpaceService {
	private PmClosedLoopService pmClosedLoopService;
	private WorkFlowService workFlowService;
	private WorkSpaceDao workspaceDao;
	private BasicDataService basicDataService;

	public List<DpActProcDesc> queryPmCLTaskList() {
		String userId = getUserContext().getUser().getUsername();

		PmClEvaluationHeader pmClEvaluationHeader = new PmClEvaluationHeader();
		pmClEvaluationHeader.setEvaluationType(0);
		Map<String, PmClEvaluationHeader> pmClEvaluationHeaderMapDesc = pmClosedLoopService
				.queryEvaluationHeaderObjMap(pmClEvaluationHeader, "desc");

		List<Task> pubTaskList = workFlowService.queryAllPubTaskList(userId);
		List<Task> selfTaskList = workFlowService.queryAllSelfTaskList(userId);

		Set<String> taskSet = new HashSet<String>();
		if (selfTaskList != null && selfTaskList.size() > 0) {
			for (Task task : selfTaskList) {
				taskSet.add(task.getId());
			}
		}
		if (pubTaskList != null && pubTaskList.size() > 0) {
			for (Task task : pubTaskList) {
				taskSet.add(task.getId());
			}
		}

		List<DpActProcDesc> dpActProcDescList = new ArrayList<DpActProcDesc>();
		List<BasicDataBean> quesTypeList = basicDataService
				.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_PROCESSID);

		Map<String, String> processTypeMap = new HashMap<String, String>();
		if (quesTypeList == null) {
			throw new RuntimeException("获取闭环信息出错");
		}
		for (BasicDataBean basicDataBean : quesTypeList) {
			processTypeMap.put(basicDataBean.getBasicDataId(), basicDataBean.getBasicDataName());
		}

		for (String taskId : taskSet) {
			Map<String, Object> varMap = workFlowService.queryProcessVarMap(taskId);
			if (varMap != null && varMap.get("projectCode") != null) {
				PmClEvaluationHeader pmObj = pmClEvaluationHeaderMapDesc.get(varMap.get("projectCode").toString());
				int objId = (Integer) varMap.get("objId");

				if (pmObj != null && objId == pmObj.getApplyHeaderId()) {
					DpActProcDesc dpActProcDescObj = new DpActProcDesc();

					dpActProcDescObj.setProjectCode(pmObj.getProjectCode());
					dpActProcDescObj.setProjectName(pmObj.getProjectName());
					dpActProcDescObj.setOfficeName(pmObj.getOfficeName());
					dpActProcDescObj.setApplyNum(pmObj.getProjectId());
					dpActProcDescObj.setTaskId(taskId);
					dpActProcDescObj.setUsername(pmObj.getApplyPersonId());
					dpActProcDescObj.setRealName(pmObj.getApplyPersonName());
					dpActProcDescObj.setCreateTime(pmObj.getApplyTime());
					dpActProcDescObj.setProcTypeDesc("项目闭环流程");
					dpActProcDescObj.setProjectCustomer(pmObj.getProjectCustomer());
					dpActProcDescObj.setProjectImpl(pmObj.getProjectImpl());
					dpActProcDescList.add(dpActProcDescObj);

					dpActProcDescObj.setProcTypeName("项目闭环");
					if (pmObj.getEvaluationType() == 0) {
						throw new RuntimeException("待办事项出错");
					} else if (pmObj.getEvaluationType() == PmClosedLoopConstant.CL_EVALU_TYPE_CL) {
						dpActProcDescObj.setName(processTypeMap.get(PmClosedLoopConstant.CL_EVALU_TYPE_CL + ""));

					} else if (pmObj.getStatus() != PmClosedLoopConstant.CL_STATUS_SUBMIT) {
						dpActProcDescObj.setName(processTypeMap.get(pmObj.getEvaluationType() + ""));

					} else {
						dpActProcDescObj.setName(processTypeMap.get((pmObj.getEvaluationType() + 1) + ""));
						if (pmObj.getEvaluationType() == PmClosedLoopConstant.CL_EVALU_TYPE_PM
								&& pmObj.getNextAcceptPerson().equals("回访人员"))
							dpActProcDescObj.setName(processTypeMap.get(PmClosedLoopConstant.CL_EVALU_TYPE_CB + ""));
					}

					dpActProcDescObj.setAssigneeName(pmObj.getNextAcceptPersonName());

					if (dpActProcDescObj.getAssigneeName().equals("工程人员")) {
						dpActProcDescObj.setName(processTypeMap.get(PmClosedLoopConstant.CL_EVALU_TYPE_CL + ""));
					}
					if (pmObj.getEvaluationResult() == PmClosedLoopConstant.CL_EVALU_RESULT_REJECT
							&& pmObj.getStatus() == PmClosedLoopConstant.CL_STATUS_SUBMIT)
						dpActProcDescObj.setName(processTypeMap.get(PmClosedLoopConstant.CL_EVALU_TYPE_PM + ""));

					if (pmObj.getEvaluationResult() == PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB
							&& pmObj.getStatus() == PmClosedLoopConstant.CL_STATUS_SUBMIT)
						dpActProcDescObj.setName(processTypeMap.get(PmClosedLoopConstant.CL_EVALU_TYPE_SM + ""));
				}

			}
		}
		return dpActProcDescList;

	}

	@Override
	public List<DpActProcDesc> queryPmCLHisTaskList() {
		String userId = getUserContext().getUser().getUsername();

		PmClEvaluationHeader pmClEvaluationHeaderObj = new PmClEvaluationHeader();
		pmClEvaluationHeaderObj.setEvaluationPeopleId(userId);
		pmClEvaluationHeaderObj.setStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
		List<PmClEvaluationHeader> pmHisList = pmClosedLoopService.queryPmEvaluationHeaderList(pmClEvaluationHeaderObj);

		List<BasicDataBean> quesTypeList = basicDataService
				.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_PROCESSID);
		Map<String, String> processTypeMap = new HashMap<String, String>();
		if (quesTypeList == null) {
			throw new RuntimeException("获取闭环信息出错");
		}
		for (BasicDataBean basicDataBean : quesTypeList) {
			processTypeMap.put(basicDataBean.getBasicDataId(), basicDataBean.getBasicDataName());
		}

		pmClEvaluationHeaderObj.setEvaluationPeopleId("");
		pmClEvaluationHeaderObj.setStatus(0);
		pmClEvaluationHeaderObj.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_PM);

		List<DpActProcDesc> dpHisList = new ArrayList<DpActProcDesc>();
		if (pmHisList != null) {
			for (PmClEvaluationHeader pmObj : pmHisList) {
				DpActProcDesc dpObj = new DpActProcDesc();
				dpObj.setApplyNum(pmObj.getProjectId());
				dpObj.setProjectCode(pmObj.getProjectCode());
				dpObj.setProjectName(pmObj.getProjectName());
				dpObj.setProcTypeName("项目闭环");
				dpObj.setName(processTypeMap.get(pmObj.getEvaluationType() + ""));
				dpObj.setEndTime(pmObj.getEvaluationTime());
				dpObj.setEvaluaResult(pmObj.getEvaluationResult());
				if (pmObj.getEvaluationType() == PmClosedLoopConstant.CL_EVALU_TYPE_PM) {
					dpObj.setEvaluaResult(0);
				}
				dpObj.setAssigneeName(pmObj.getEvaluationPeopleName());
				dpObj.setUsername(pmObj.getApplyPersonId());
				dpObj.setRealName(pmObj.getApplyPersonName());

				dpHisList.add(dpObj);
			}
		}

		return dpHisList;

	}

	public WorkSpaceDao getWorkspaceDao() {
		return workspaceDao;
	}

	public void setWorkspaceDao(WorkSpaceDao workspaceDao) {
		this.workspaceDao = workspaceDao;
	}

	public WorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(WorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@Override
	public List<String> getprojectcodelistbyusername(String usernamenow) {
		return workspaceDao.getprojectcodelistbyusername(usernamenow);
	}

	@Override
	public List<Integer> getprojectcodelistfrombeforebyusername(String usernamenow) {
		return workspaceDao.getprojectcodelistfrombeforebyusername(usernamenow);
	}

	@Override
	public String getprojectbyapplyid(int applyid) {
		return workspaceDao.getprojectbyapplyid(applyid);
	}

	@Override
	public List<Integer> getapplyidsfromorderbyusername(String usernamenow) {
		return workspaceDao.getapplyidsfromorderbyusername(usernamenow);
	}

	@Override
	public String getprojectbyapplyidorder(int applyid) {
		return workspaceDao.getprojectbyapplyidorder(applyid);
	}

	@Override
	public List<String> querybusinessorderprojectcodelist(String usernamenow) {
		return workspaceDao.querybusinessorderprojectcodelist(usernamenow);
	}

	@Override
	public String queryProductFirstCodeByUsername(String usernamenow) {
		return workspaceDao.queryProductFirstCodeByUsername(usernamenow);
	}

	@Override
	public String queryConcatFirstCode(String code) {
		return workspaceDao.queryConcatFirstCode(code);
	}

	@Override
	public List<DpActProcDesc> queryActRunTask(String taskType) {
		return workspaceDao.queryActRunTask(taskType);
	}

	@Override
	public List<Notification> checkNotificationList(String username) {
		return workspaceDao.checkNotificationList(username);
	}

	@Override
	public void updateNotificationState(int notifyStateId) {
		workspaceDao.updateNotificationState(notifyStateId);
	}

	public PmClosedLoopService getPmClosedLoopService() {
		return pmClosedLoopService;
	}

	public void setPmClosedLoopService(PmClosedLoopService pmClosedLoopService) {
		this.pmClosedLoopService = pmClosedLoopService;
	}

	public BasicDataService getBasicDataService() {
		return basicDataService;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	@Override
	public List<DpActProcDesc> queryPmTaskList(TaskQueryParam taskQueryParam, DisplayParam displayParam) {
		try {
			if (taskQueryParam == null) {
				taskQueryParam = new TaskQueryParam();
			}
			if (displayParam == null) {
				displayParam = new DisplayParam();
			}
			displayParam.getParam();
			User user = UserContext.getUserContext().getUser();
			if (!user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) && !user.isHasRole(MessageUtil.ROLE_ADMIN)) {// 非工程管理部或管理员
				if (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {// 服务经理
					taskQueryParam.setServiceManager(getLoginName());
				} else if (user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)) {// 项目经理
					taskQueryParam.setProgramManager(getLoginName());
				} else {
					return new ArrayList<DpActProcDesc>();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return workspaceDao.queryPmTaskList(taskQueryParam, displayParam);
	}

	@Override
	public List<DpActProcDesc> queryProjectBackTaskList() {
		return workspaceDao.queryProjectBackTaskList();
	}

	@Override
	public List<DpActProcDesc> queryProjectTrackTaskList() {
		return workspaceDao.queryProjectTrackTaskList();
	}

	@Override
	public List<Notification> queryNotifyList(TaskQueryParam notifyQueryParam, DisplayParam notifyDisplayParam) {
		try {
			if (notifyQueryParam == null) {
				notifyQueryParam = new TaskQueryParam();
			}
			if (notifyDisplayParam == null) {
				notifyDisplayParam = new DisplayParam();
			}
			notifyDisplayParam.getParam();
			User user = UserContext.getUserContext().getUser();
			if (!user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) && !user.isHasRole(MessageUtil.ROLE_ADMIN)) {// 非工程管理部或管理员
				if (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {// 服务经理
					notifyQueryParam.setServiceManager(getLoginName());
				} else if (user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)) {// 项目经理
					notifyQueryParam.setProgramManager(getLoginName());
				} else {
					return new ArrayList<Notification>();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return workspaceDao.queryNotifyList(notifyQueryParam, notifyDisplayParam);
	}

	@Override
	public List<DpActProcDesc> queryCallBackTaskList() {

		return workspaceDao.queryCallBackTaskList();
	}

	@Override
	public List<DpActProcDesc> queryPresalesTaskList() {
		return workspaceDao.queryPresalesTaskList();
	}

	@Override
	public List<DpActProcDesc> queryCallbackHisList() {

		return workspaceDao.queryCallbackHisList(getLoginName());
	}

	@Override
	public List<ProbParam> queryProbTaskList() {
		return workspaceDao.queryProbTaskList();
	}
	
	@Override
    public List<Map<String, Object>> querySubcontractTaskList() {
        return this.querySubcontractTaskList(Collections.emptyMap());
    }

	@Override
	public List<Map<String, Object>> querySubcontractTaskList(Map<String, String> queryParams) {
		HashMap<String, Object> params = new HashMap<>();
		if (queryParams != null) {
		    params.putAll(queryParams);
		}
		UserContext context = UserContext.getUserContext();
		params.put("assignee", getLoginName());
		String areaPower = StringUtils.trimToEmpty(context.getUser().getAreapower());
////		if (StringUtils.isNotBlank(areaPower)) {
////			Set<String> newAreaList = new HashSet<>();
////			List<String> areaList = Arrays.asList(StringUtils.split(areaPower, ","));
////			newAreaList.addAll(areaList);
////			for (String area : areaList) {
////				String newArea = null;
////				if (area.length() > 6) {
////					area = area.substring(0, 6);
////				}
////				if (area.startsWith("16")) {
////					newArea = area.replaceFirst("16", "31");
////				} else if (area.startsWith("31")) {
////					newArea = area.replaceFirst("31", "16");
////				}
////				if (StringUtils.isNotBlank(newArea) && !newAreaList.contains(newArea)) {
////					newAreaList.add(newArea);
////				}
////			}
////			areaPower = StringUtils.join(newAreaList, ",");
////		}
//		areaPower = UserUtil.processAreaPower(areaPower);
		params.put("areaPower", areaPower);
		List<Map<String, Object>> allTaskList = new ArrayList<>();
		List<Map<String, Object>> emTaskList = new ArrayList<>();
		List<Map<String, Object>> roleGroups = new ArrayList<>();
		if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
				|| context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
			// params.put("roleGroup", "emRole");
			// emTaskList = workspaceDao.querySubcontractTaskList(params);
			HashMap<String, Object> roleGroup = new HashMap<>();
			roleGroup.put("roleGroup", "emRole");
			roleGroups.add(roleGroup);
			
			if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
				roleGroup = new HashMap<>();
				roleGroup.put("roleGroup", "emlRole");
				roleGroups.add(roleGroup);
				
				roleGroup = new HashMap<>();
				roleGroup.put("roleGroup", "role_" + MessageUtil.ROLE_ENGINEEMANAGER_LEADER);
				roleGroups.add(roleGroup);
			}
		}

		List<Map<String, Object>> cbTaskList = new ArrayList<>();
		if (context.isHasRole(MessageUtil.ROLE_CALLBACKPER)) {
			// params.put("roleGroup", "cbRole");
			// cbTaskList = workspaceDao.querySubcontractTaskList(params);
			HashMap<String, Object> roleGroup = new HashMap<>();
			roleGroup.put("roleGroup", "cbRole");
			roleGroups.add(roleGroup);
		}
		
		List<Map<String, Object>> zrTaskList = new ArrayList<>();
		if (context.isHasRole(MessageUtil.ROLE_AREA_LEADER)) {
			// params.put("roleGroup", "zrRole");
			// params.put("checkOffice", true);
			// zrTaskList = workspaceDao.querySubcontractTaskList(params);
			HashMap<String, Object> roleGroup = new HashMap<>();
			roleGroup.put("roleGroup", "zrRole");
			roleGroup.put("checkProfitDep", true);
			roleGroups.add(roleGroup);
			
			roleGroup = new HashMap<>();
			roleGroup.put("roleGroup", "role_" + MessageUtil.ROLE_AREA_LEADER);
			roleGroup.put("checkProfitDep", true);
			roleGroups.add(roleGroup);
		}

		List<Map<String, Object>> smTaskList = new ArrayList<>();
		if (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {
			// params.put("roleGroup", "smRole");
			// params.put("checkOffice", true);
			// smTaskList = workspaceDao.querySubcontractTaskList(params);
			HashMap<String, Object> roleGroup = new HashMap<>();
			roleGroup.put("roleGroup", "smRole");
			roleGroup.put("checkOffice", true);
			roleGroups.add(roleGroup);

			roleGroup = new HashMap<>();
			roleGroup.put("roleGroup", "profitSmRole");
			roleGroup.put("checkProfitDep", true);
			roleGroups.add(roleGroup);
			try {
				SubcontractService subcontractService = SpringContext.getApplicationContext()
						.getBean("subcontractService", SubcontractService.class);
				List<Map<String, Object>> rejectedProjectList = subcontractService
						.selectRejectedSubcontractProjectList(params);
				smTaskList.addAll(rejectedProjectList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		List<Map<String, Object>> apTaskList = new ArrayList<>();
        if (context.isHasRole(MessageUtil.ROLE_FINANCIAL_STAFF)) {
            // params.put("roleGroup", "role_" + MessageUtil.ROLE_FINANCIAL_STAFF);
            // apTaskList = workspaceDao.querySubcontractTaskList(params);
            HashMap<String, Object> roleGroup = new HashMap<>();
            roleGroup.put("roleGroup", "role_" + MessageUtil.ROLE_FINANCIAL_STAFF);
            roleGroups.add(roleGroup);
        }
//		try {
//    		String[] roleids = StringUtils.split(UserContext.getUserContext().getUser().getRoleids(), ",");
//    		for (String roleId : roleids) {
//    		    String role = "role_" + roleId.replaceAll(";", "");
//    		    HashMap<String, Object> roleGroup = new HashMap<>();
//                roleGroup.put("roleGroup", role);
//                roleGroups.add(roleGroup);
//                
//                roleGroup = new HashMap<>();
//                roleGroup.put("roleGroup", role);
//                roleGroup.put("checkOffice", true);
//                roleGroups.add(roleGroup);
//            }
//		} catch (Exception e) {
//		    e.printStackTrace();
//        }
		
		params.put("roleGroups", roleGroups);
		allTaskList = workspaceDao.querySubcontractTaskList(params);
		allTaskList.addAll(emTaskList);
		allTaskList.addAll(cbTaskList);
		allTaskList.addAll(zrTaskList);
		allTaskList.addAll(apTaskList);
		allTaskList.addAll(smTaskList);
		return allTaskList;
		// return workspaceDao.querySubcontractTaskList(params);
	}
	
    @Override
    public List<DpActProcDesc> queryProjectSupervisionTask(HashMap params) {
        return workspaceDao.queryProjectSupervisionTask(params);
    }
	
}
