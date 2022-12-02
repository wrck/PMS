package com.dp.plat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.DpActProcDesc;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.TaskQueryParam;
import com.dp.plat.prob.param.ProbParam;

public interface WorkSpaceService extends BaseService {
	
	List<String> getprojectcodelistbyusername(String usernamenow);
	
	
	List<Integer> getprojectcodelistfrombeforebyusername(String usernamenow);
	
	String getprojectbyapplyid(int applyid);
	
	
	List<Integer> getapplyidsfromorderbyusername(String usernamenow);
	
	String getprojectbyapplyidorder(int applyid);
	
	
	List<String> querybusinessorderprojectcodelist(String usernamenow);
	
	String queryProductFirstCodeByUsername(String usernamenow);

	String queryConcatFirstCode(String code);

	List<DpActProcDesc> queryActRunTask(String taskType);
	/**
	 * 查看消息通知
	 * @param username
	 * @return
	 */
	List<Notification> checkNotificationList(String username);
	/**
	 * 更新消息状态
	 * @param notifyStateId
	 */
	void updateNotificationState(int notifyStateId);
	
	/**
	 *获取闭环流程待办任务列表
	 * @return
	 */
	List<DpActProcDesc>  queryPmCLTaskList();
	
	/**
	 *获取闭环流程已办理任务列表
	 * @return
	 */
	List<DpActProcDesc>  queryPmCLHisTaskList();
	/**
	 * 获取项目经理的工程待办任务
	 * @param taskQueryParam 
	 * @param displayParam 
	 * @return
	 */
	List<DpActProcDesc> queryPmTaskList(TaskQueryParam taskQueryParam, DisplayParam displayParam);
	
	/**
	 * 查询项目回退服务经理或工程管理部确认任务
	 * @return
	 */
	List<DpActProcDesc> queryProjectBackTaskList();

	/**
	 * 查询系统通知
	 * @param notifyQueryParam
	 * @param notifyDisplayParam
	 * @return
	 */
	List<Notification> queryNotifyList(TaskQueryParam notifyQueryParam,
			DisplayParam notifyDisplayParam);

	/**
	 * 查询回访申请代办理任务
	 * @return
	 */
	List<DpActProcDesc> queryCallBackTaskList();
	/**
	 * 查询售前流程代办理任务
	 * @return
	 */
	List<DpActProcDesc> queryPresalesTaskList();

	/**
	 * 查询已办理的回访任务
	 * @return
	 */
	List<DpActProcDesc> queryCallbackHisList();


	/**
	 * 查询不予跟踪任务
	 * @return
	 */
	List<DpActProcDesc> queryProjectTrackTaskList();


	/**
	 * 查询技术公告
	 * @return
	 */
	List<ProbParam> queryProbTaskList();


	/**
	 * 查询项目转包任务
	 * @return
	 */
	List<Map<String, Object>> querySubcontractTaskList();
	/**
     * 查询项目转包任务
     * @return
     */
	List<Map<String, Object>> querySubcontractTaskList(Map<String, String> queryParams);


    /**
     * @param hashMap
     */
    List<DpActProcDesc> queryProjectSupervisionTask(HashMap hashMap);



}
