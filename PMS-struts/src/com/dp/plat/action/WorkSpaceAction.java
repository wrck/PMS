package com.dp.plat.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.DpActProcDesc;
import com.dp.plat.data.bean.DpActProcType;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.TaskQueryParam;
import com.dp.plat.prob.param.ProbParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.WorkFlowService;
import com.dp.plat.service.WorkSpaceService;
import com.dp.plat.util.MessageUtil;
import com.opensymphony.xwork2.Preparable;

public class WorkSpaceAction extends BaseAction implements Preparable {

	private static final long serialVersionUID = 1L;
	private DisplayParam displayParam;
	private DisplayParam notifyDisplayParam;
	private WorkSpaceService workspaceService;
	private BasicDataService basicDataService;
	private DepartmentManageService departmentManageService;
	private Map<String, Integer> countMap;// 存放工作台待办任务数量map
	private int livalue;
	private DpActProcDesc dpActProcDesc;
	private List<DpActProcDesc> dapdlist = new ArrayList<DpActProcDesc>();// 任务list
	private WorkFlowService workFlowService;
	private List<DpActProcType> daptlist;// 流程类型list
	private List<Department> departmentList;
	// 系统通知
	private List<Notification> notificationList;// 消息集合
	private List<BasicDataBean> navTabList;// 项目维护页面选项卡集合
	private List<DpActProcDesc> dailyTaskList;// 日常项目跟踪
	private List<ProbParam> probTaskList = new ArrayList<ProbParam>();;
	private int notifyStateId;
	private List<DpActProcDesc> dpHisList = new ArrayList<DpActProcDesc>();
	private TaskQueryParam taskQueryParam;// 筛选条件
	private TaskQueryParam notifyQueryParam;// 系统通知筛选条件
	private int tabIndex;// 选项卡索引
	private String tabName;// 选项卡，由于tabIndex 多变，改用tabName，前台自动查找相应索引
	private Boolean isCbRole;
	private List<Map<String, Object>> subcontractTaskList = new ArrayList<>();

	@Override
	public void prepare() throws Exception {
		User user = UserContext.getUserContext().getUser();
		isCbRole = user.isHasRole(MessageUtil.ROLE_CALLBACKPER);
		// 办事处集合
		departmentList = departmentManageService.queryDepartments();
		// 选项卡
		navTabList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_NAV_WORK_TAB);
		Map<String, BasicDataBean> navTabMap = new LinkedHashMap<>(navTabList.size());
		// HashMap<String, Integer> navTabWithIndex = new HashMap<>();
		for (int i = 0; i < navTabList.size(); i++) {
			BasicDataBean navTab = navTabList.get(i);
			// navTabWithIndex.put(navTab.getBasicDataId(), i);
			navTabMap.put(navTab.getBasicDataId(), navTab);
		}
		if (!user.isHasRole(MessageUtil.ROLE_CALLBACKPER)) {
//			navTabList.remove(navTabList.size()-1);
			// navTabList.remove(navTabWithIndex.get("hisselftask").intValue());
			navTabMap.remove("hisselftask");
		}
		// navTabWithIndex.clear();
		// for (int i = 0;i<navTabList.size();i++) {
		// BasicDataBean navTab = navTabList.get(i);
		// navTabWithIndex.put(navTab.getBasicDataId(), i);
		// }

		if (((user.isHasRole(MessageUtil.ROLE_PROB_ADMIN) || user.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)
				|| user.isHasRole(MessageUtil.ROLE_PROB_RD)) && user.getRoleids().length() == 4)

//		  || (user.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER) ||
//		  user.isHasRole(MessageUtil.ROLE_PROB_RD))
		) {
			// Integer probTaskIndex = navTabWithIndex.get("probTask");
			// if (probTaskIndex != null) {
			// BasicDataBean navTab = navTabList.get(probTaskIndex.intValue());
			// navTabList.clear();
			// navTabList.add(navTab);
			// tabIndex = 4;
			// navTabMap.clear();
			// navTabMap.put(navTab.getBasicDataId(), navTab);
			// }
			if (navTabMap.containsKey("probTask")) {
				BasicDataBean navTab = navTabMap.get("probTask");
				tabIndex = 4;
				navTabMap.clear();
				navTabMap.put(navTab.getBasicDataId(), navTab);
			}
		}
		//// else{
		//// navTabList.remove(navTabWithIndex.get("probTask").intValue());
		//// }
		// navTabWithIndex.clear();
		// for (int i = 0;i<navTabList.size();i++) {
		// BasicDataBean navTab = navTabList.get(i);
		// navTabWithIndex.put(navTab.getBasicDataId(), i);
//		}

		if (!(user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
				|| user.isHasRole(MessageUtil.ROLE_CALLBACKPER) || user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER))) {
			// Integer subtaskIndex = navTabWithIndex.get("subcontractTask");
			// if (subtaskIndex != null) {
			// if (user.isHasRole(MessageUtil.ROLE_AREA_LEADER) &&
			// user.getRoleids().length() == 3) {
			// BasicDataBean navTab = navTabList.get(subtaskIndex.intValue());
			// navTabList.clear();
			// navTabList.add(navTab);
			// tabIndex = 5;
			// navTabMap.clear();
			// navTabMap.put(navTab.getBasicDataId(), navTab);
			// } else {
			// navTabList.remove(subtaskIndex.intValue());
			// navTabMap.remove("subcontractTask");
			// }
			// }
			if (navTabMap.containsKey("subcontractTask")) {
				if (user.isHasAnyRole(MessageUtil.ROLE_AREA_LEADER, MessageUtil.ROLE_FINANCIAL_STAFF) && user.getRoleids().length() == 3) {
					BasicDataBean navTab = navTabMap.get("subcontractTask");
					tabIndex = 5;
					navTabMap.clear();
					navTabMap.put(navTab.getBasicDataId(), navTab);
				} else if (user.isHasAnyRole(MessageUtil.ROLE_AREA_LEADER, MessageUtil.ROLE_FINANCIAL_STAFF)) {
				    tabIndex = 5;
				} else {
				    navTabMap.remove("subcontractTask");
				}
			}
		}

		navTabList.clear();
		navTabList.addAll(navTabMap.values());
	}

	public void prepareExecute() {
		if (notificationList == null) {
			notificationList = new ArrayList<Notification>();
		}
		if (dapdlist == null) {
			dapdlist = new ArrayList<DpActProcDesc>();
		}
	}

	/**
	 * 查看需要办理的任务,点击登陆以后默认的是这个action,点击日常跟踪的查询，也是这个action,点击待办事项也是这个action
	 */
	@Override
	public String execute() throws Exception {
		if (tabIndex == 4) {
			probTask();
		} else if (tabIndex == 5) {
			subcontractTask();
		} else {
			// 日常项目跟踪
			dailyTaskList = workspaceService.queryPmTaskList(taskQueryParam, displayParam);
			tabName = "dailyTask";
		}
		return SUCCESS;
	}

	public void prepareNotice() {
		if (dapdlist == null) {
			dapdlist = new ArrayList<DpActProcDesc>();
		}
		if (dailyTaskList == null) {
			dailyTaskList = new ArrayList<DpActProcDesc>();
		}
	}

	/**
	 * 通知
	 * 
	 * @return
	 */
	public String notice() {
		// 系统通知
		notificationList = workspaceService.queryNotifyList(notifyQueryParam, notifyDisplayParam);
		tabIndex = 2;
		tabName = "notice";
		return SUCCESS;
	}

	public void prepareTask() {
		if (dailyTaskList == null) {
			dailyTaskList = new ArrayList<DpActProcDesc>();
		}
		if (notificationList == null) {
			notificationList = new ArrayList<Notification>();
		}
	}

	/**
	 * 业务流程办理
	 * 
	 * @return
	 */
	public String task() {
		// 业务流程办理
		dapdlist = workspaceService.queryPmCLTaskList();
		// 项目回退确认任务
		List<DpActProcDesc> list2 = workspaceService.queryProjectBackTaskList();
		if (list2 != null) {
			dapdlist.addAll(list2);
		}
		// 项目不予跟踪确认任务
		List<DpActProcDesc> list3 = workspaceService.queryProjectTrackTaskList();
		if (list3 != null) {
			dapdlist.addAll(list3);
		}
		// 回访申请待办任务
		List<DpActProcDesc> list = workspaceService.queryCallBackTaskList();
		if (list != null) {
			dapdlist.addAll(list);
		}
		// 项目督查任务
		User user = UserContext.getUserContext().getUser();
        if (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
            List<DpActProcDesc> supervisionTaskList = workspaceService.queryProjectSupervisionTask(new HashMap());
            if (supervisionTaskList != null) {
                dapdlist.addAll(supervisionTaskList);
            }
        }
		// 售前流程待办任务
		List<DpActProcDesc> presalesList = workspaceService.queryPresalesTaskList();
		if (presalesList != null) {
			dapdlist.addAll(presalesList);
		}
		
		tabIndex = 1;
		tabName = "task";
		return SUCCESS;
	}

	/**
	 * 日常项目跟踪
	 * 
	 * @return
	 * @throws Exception
	 */
	public String dailyTask() throws Exception {
		return execute();
	}

	/**
	 * 查看自己办理过的任务
	 * 
	 * @return
	 * @throws Exception
	 */
	public String hisselftask() throws Exception {
		dpHisList = workspaceService.queryPmCLHisTaskList();
		List<DpActProcDesc> callbackList = workspaceService.queryCallbackHisList();
		dpHisList.addAll(callbackList);
		tabIndex = 3;
		tabName = "hisselftask";
		return SUCCESS;
	}

	/**
	 * 查看自己技术公告的任务
	 * 
	 * @return
	 * @throws Exception
	 */
	public String probTask() throws Exception {
		probTaskList = workspaceService.queryProbTaskList();
		tabIndex = 4;
		tabName = "probTask";
		return SUCCESS;
	}

	/**
	 * 查看项目转包任务
	 * 
	 * @return
	 * @throws Exception
	 */
	public String subcontractTask() throws Exception {
		subcontractTaskList = workspaceService.qerySubcontractTaskList();
		tabIndex = 5;
//		if (UserContext.getUserContext().isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {
//			workspaceService.qerySubcontractTaskList();
//		}
		tabName = "subcontractTask";
		return SUCCESS;
	}

	/**
	 * 更新系统消息状态
	 * 
	 * @return
	 */
	@Deprecated
	public String updateNotifyState() {
		workspaceService.updateNotificationState(notifyStateId);
		return SUCCESS;
	}

	public Map<String, Integer> getCountMap() {
		return countMap;
	}

	public void setCountMap(Map<String, Integer> countMap) {
		this.countMap = countMap;
	}

	public DpActProcDesc getDpActProcDesc() {
		return dpActProcDesc;
	}

	public void setDpActProcDesc(DpActProcDesc dpActProcDesc) {
		this.dpActProcDesc = dpActProcDesc;
	}

	public List<DpActProcDesc> getDapdlist() {
		return dapdlist;
	}

	public void setDapdlist(List<DpActProcDesc> dapdlist) {
		this.dapdlist = dapdlist;
	}

	public WorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(WorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public List<DpActProcType> getDaptlist() {
		return daptlist;
	}

	public void setDaptlist(List<DpActProcType> daptlist) {
		this.daptlist = daptlist;
	}

	public int getLivalue() {
		return livalue;
	}

	public void setLivalue(int livalue) {
		this.livalue = livalue;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public WorkSpaceService getWorkspaceService() {
		return workspaceService;
	}

	public void setWorkspaceService(WorkSpaceService workspaceService) {
		this.workspaceService = workspaceService;
	}

	public List<Notification> getNotificationList() {
		return notificationList;
	}

	public void setNotificationList(List<Notification> notificationList) {
		this.notificationList = notificationList;
	}

	public List<BasicDataBean> getNavTabList() {
		return navTabList;
	}

	public void setNavTabList(List<BasicDataBean> navTabList) {
		this.navTabList = navTabList;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public int getNotifyStateId() {
		return notifyStateId;
	}

	public void setNotifyStateId(int notifyStateId) {
		this.notifyStateId = notifyStateId;
	}

	public List<DpActProcDesc> getDpHisList() {
		return dpHisList;
	}

	public void setDpHisList(List<DpActProcDesc> dpHisList) {
		this.dpHisList = dpHisList;
	}

	public List<DpActProcDesc> getDailyTaskList() {
		return dailyTaskList;
	}

	public void setDailyTaskList(List<DpActProcDesc> dailyTaskList) {
		this.dailyTaskList = dailyTaskList;
	}

	public TaskQueryParam getTaskQueryParam() {
		return taskQueryParam;
	}

	public void setTaskQueryParam(TaskQueryParam taskQueryParam) {
		this.taskQueryParam = taskQueryParam;
	}

	public List<Department> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<Department> departmentList) {
		this.departmentList = departmentList;
	}

	public void setDepartmentManageService(DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public DisplayParam getNotifyDisplayParam() {
		return notifyDisplayParam;
	}

	public void setNotifyDisplayParam(DisplayParam notifyDisplayParam) {
		this.notifyDisplayParam = notifyDisplayParam;
	}

	public TaskQueryParam getNotifyQueryParam() {
		return notifyQueryParam;
	}

	public void setNotifyQueryParam(TaskQueryParam notifyQueryParam) {
		this.notifyQueryParam = notifyQueryParam;
	}

	public Boolean getIsCbRole() {
		return isCbRole;
	}

	public List<ProbParam> getProbTaskList() {
		return probTaskList;
	}

	public void setProbTaskList(List<ProbParam> probTaskList) {
		this.probTaskList = probTaskList;
	}

	public List<Map<String, Object>> getSubcontractTaskList() {
		return subcontractTaskList;
	}

	public void setSubcontractTaskList(List<Map<String, Object>> subcontractTaskList) {
		this.subcontractTaskList = subcontractTaskList;
	}

}
