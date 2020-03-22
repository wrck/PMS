package com.dp.plat.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.DpActProcDesc;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.TaskQueryParam;
import com.dp.plat.prob.param.ProbParam;

public interface WorkSpaceDao {
	
	List<String> getprojectcodelistbyusername(String usernamenow);
	

	List<Integer> getprojectcodelistfrombeforebyusername(String usernamenow);
	
	String getprojectbyapplyid(int applyid);
	
	
	List<Integer> getapplyidsfromorderbyusername(String usernamenow);
	
	String getprojectbyapplyidorder(int applyid);
	
	
	List<String> querybusinessorderprojectcodelist(String usernamenow);
	

	String queryProductFirstCodeByUsername(String usernamenow);


	String queryConcatFirstCode(String code);

	List<DpActProcDesc> queryActRunTask(String taskType);


	List<Notification> checkNotificationList(String username);


	void updateNotificationState(int notifyStateId);


	List<DpActProcDesc> queryPmTaskList(TaskQueryParam taskQueryParam, DisplayParam displayParam);


	List<DpActProcDesc> queryProjectBackTaskList();


	List<Notification> queryNotifyList(TaskQueryParam notifyQueryParam,
			DisplayParam notifyDisplayParam);


	List<DpActProcDesc> queryCallBackTaskList();


	List<DpActProcDesc> queryPresalesTaskList();


	List<DpActProcDesc> queryCallbackHisList(String loginName);

	List<DpActProcDesc> queryProjectTrackTaskList();

	/**
	 * 查询技术公告
	 * @return
	 */
	List<ProbParam> queryProbTaskList();


	/**
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> querySubcontractTaskList(HashMap<String, Object> params);


    /**
     * @param params
     * @return
     */
    List<DpActProcDesc> queryProjectSupervisionTask(HashMap<String, Object> params);
}
