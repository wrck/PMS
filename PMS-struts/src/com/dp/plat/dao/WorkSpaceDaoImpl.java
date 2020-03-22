package com.dp.plat.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.DpActProcDesc;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.data.bean.User;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.TaskQueryParam;
import com.dp.plat.prob.param.ProbParam;
import com.dp.plat.supervision.entity.ProjectSupervision;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.Util;

public class WorkSpaceDaoImpl extends BaseDao implements WorkSpaceDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getprojectcodelistbyusername(String usernamenow) {
		return getSqlMapClientTemplate().queryForList("get-projectcodelist-byusername", usernamenow);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getprojectcodelistfrombeforebyusername(
			String usernamenow) {
		return getSqlMapClientTemplate().queryForList("get-projectcodelistfrombefore-byusername", usernamenow);
	}

	@Override
	public String getprojectbyapplyid(int applyid) {
		return (String)getSqlMapClientTemplate().queryForObject("get-projectCode-byapplyid", applyid);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getapplyidsfromorderbyusername(String usernamenow) {
		return getSqlMapClientTemplate().queryForList("get-applyid-byusernameorder", usernamenow);
	}

	@Override
	public String getprojectbyapplyidorder(int applyid) {
		return (String)getSqlMapClientTemplate().queryForObject("get-project-byapplyidorder", applyid);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<String> querybusinessorderprojectcodelist(String usernamenow) {
		return getSqlMapClientTemplate().queryForList("select-businessorderprojectcodelist", usernamenow);
	}


	@Override
	public String queryProductFirstCodeByUsername(String usernamenow) {
		return (String) getSqlMapClientTemplate().queryForObject("query_productfirst_byuserassist", usernamenow);
	}


	@Override
	public String  queryConcatFirstCode(String code) {
		return (String) getSqlMapClientTemplate().queryForObject("query_productfirstCode_byprojectCode", code);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DpActProcDesc> queryActRunTask(String taskType) {
		return getSqlMapClientTemplate().queryForList("query_act_ru_task_bytasktype", taskType);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Notification> checkNotificationList(String username) {
		return getSqlMapClientTemplate().queryForList("check_notification_list", username);
	}


	@Override
	public void updateNotificationState(int notifyStateId) {
		getSqlMapClientTemplate().update("update_notification_state", notifyStateId);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DpActProcDesc> queryPmTaskList(TaskQueryParam taskQueryParam , DisplayParam displayParam) {
		int total = (Integer) getSqlMapClientTemplate().queryForObject("query_pm_task_count", taskQueryParam);
		
		displayParam.setOffset((displayParam.getCurrentpage() - 1)
				* displayParam.getPagesize());
		displayParam.setTotalcount(total);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("taskQueryParam", taskQueryParam);
		params.put("displayParam", displayParam);
		return getSqlMapClientTemplate().queryForList("query_pm_task_list", params);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DpActProcDesc> queryProjectBackTaskList() {
		User user = UserContext.getUserContext().getUser();
		Map<String, String> params = new HashMap<String, String>();
		List<DpActProcDesc> procDescs = new ArrayList<>();
		if(user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)){//如果是工程管理部
			params.put("backstate", MessageUtil.PROJECT_CREATE_STATE36);
			procDescs = getSqlMapClientTemplate().queryForList("query_project_back_task_list", params);
		}
		if(user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)){//如果是服务经理
			params.put("assignee", user.getUsername());
			params.put("backstate", MessageUtil.PROJECT_CREATE_STATE38);
			procDescs.addAll(getSqlMapClientTemplate().queryForList("query_project_back_task_list", params));
		}else{
			return null;
		}
		return procDescs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DpActProcDesc> queryProjectTrackTaskList() {
		User user = UserContext.getUserContext().getUser();
		Map<String, String> params = new HashMap<String, String>();
		if(user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)){//如果是工程管理部
			params.put("backstate", MessageUtil.PROJECT_CREATE_STATE36);
			return getSqlMapClientTemplate().queryForList("query_project_track_task_list", params);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Notification> queryNotifyList(TaskQueryParam notifyQueryParam,
			DisplayParam notifyDisplayParam) {
		int total = (Integer) getSqlMapClientTemplate().queryForObject("query_notify_count", notifyQueryParam);
		
		notifyDisplayParam.setOffset((notifyDisplayParam.getCurrentpage() - 1)
				* notifyDisplayParam.getPagesize());
		notifyDisplayParam.setTotalcount(total);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("notifyQueryParam", notifyQueryParam);
		params.put("notifyDisplayParam", notifyDisplayParam);
		return getSqlMapClientTemplate().queryForList("query_notify_list", params);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DpActProcDesc> queryCallBackTaskList() {
		Map<String , Object> params = new HashMap<String, Object>();
		if(UserContext.getUserContext().isHasRole(MessageUtil.ROLE_CALLBACKPER)){
			params.put("callbackRole", 1);
		}else{
			params.put("callbackRole", 0);
		}
		params.put("assignee", getCurrUsername());
		
		return getSqlMapClientTemplate().queryForList("query_call_back_task", params);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DpActProcDesc> queryPresalesTaskList() {
		String assigne = getCurrUsername();
		Map<String , Object> params = new HashMap<String, Object>();
		UserContext context = UserContext.getUserContext();
		if(context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF)){
			params.put("emRole", 1);
		}else{
			params.put("emRole", 0);
		}
		params.put("assignee", assigne);
		return getSqlMapClientTemplate().queryForList("query_presales_task", params);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DpActProcDesc> queryCallbackHisList(String loginName) {
		return getSqlMapClientTemplate().queryForList("query_callback_his_list", loginName);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<ProbParam> queryProbTaskList() {
		String assignee = getCurrUsername();
		Map<String, Object> params = new HashMap<String, Object>();
		UserContext context = UserContext.getUserContext();
		params.put("assignee", assignee);
		params.put("areapower", Util.appendChar(context.getUser().getAreapower(), "'"));
		int isProbAdmin = 0;
		List<ProbParam> probTaskList = new ArrayList<>();
		List<ProbParam> tempTasks = new ArrayList<>();
		// 技术公告管理员，技术支持人员，以及研发人员 按权限补充任务
		if(context.isHasRole(MessageUtil.ROLE_PROB_ADMIN)){
			isProbAdmin = 1;
			params.put("isProbAdmin", isProbAdmin);
			tempTasks = getSqlMapClientTemplate().queryForList("query_probTask_list", params);
			probTaskList.addAll(tempTasks);
		}
		if(context.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)){
			isProbAdmin = 2;
			params.put("isProbAdmin", isProbAdmin);
			tempTasks = getSqlMapClientTemplate().queryForList("query_probTask_list", params);
			probTaskList.addAll(tempTasks);
		}
		if(context.isHasRole(MessageUtil.ROLE_PROB_RD)){
			isProbAdmin = 3;
			params.put("isProbAdmin", isProbAdmin);
			tempTasks = getSqlMapClientTemplate().queryForList("query_probTask_list", params);
			probTaskList.addAll(tempTasks);
		}
		// 非技术公告管理员，技术支持人员，以及研发人员，查询个人任务和通告
		if(isProbAdmin == 0){
			params.put("isProbAdmin", isProbAdmin);
			tempTasks = getSqlMapClientTemplate().queryForList("query_probTask_list", params);
			probTaskList.addAll(tempTasks);
		}
		return probTaskList;
	}


	@Override
	public List<Map<String, Object>> querySubcontractTaskList(HashMap<String, Object> params) {
		return getSqlMapClientTemplate().queryForList("querySubcontractTaskList", params);
	}

	@Override
    public List<DpActProcDesc> queryProjectSupervisionTask(HashMap<String, Object> params) {
        return getSqlMapClientTemplate().queryForList("queryProjectSupervisionTask", params);
    }
	
}
