package com.dp.plat.dao;

import java.util.HashMap;
import java.util.List;

import com.dp.plat.data.activity.ActComment;
import com.dp.plat.data.activity.Procdef;

public interface WorkflowDao {
	/**
	 * 根据KEY—— 查询CATEGORY_
	 * @param procdef
	 * @return
	 */
	Procdef queryProcdef(Procdef procdef);
	/**
	 * 插入fnd_act_comment
	 * @param callBackId
	 * @param key
	 * @param taskId
	 * @param instId
	 * @param result
	 * @param message
	 */
	void insertActComment(int callBackId, String key, String taskId,
			String instId, int result, String message);
	
	/**
	 * @param params
	 * 
	 * 	keys: objId, procdefKey, taskId, instId, result, message, nextAssignee, nextAssigneeName
	 * 	
	 */
	void insertActComment(HashMap<String, Object> params);
	
	/**
	 * @param params
	 * 	keys: commentId, taskId, instId
	 */
	void updateSelfActComment(HashMap<String, Object> params);
	
	/**
	 * 根据业务ID查询fnd_act_hi_comment
	 * @param objId
	 * @param procdefKey 
	 * @return
	 */
	List<ActComment> queryActComment(int objId, String procdefKey);
	/**
	 * 更新业务表流程相关的数据
	 * @param tableName
	 * @param instId
	 * @param objId
	 * @param objColumn 
	 */
	void updateApplytableInfo(String tableName, String instId, int objId, String objColumn);
	/**
	 * 根据流程实例ID以及流程变量值，找流程变量的ID_
	 * @param instId
	 * @param variableName
	 * @param oldValue
	 */
	String queryTaskByInstIdAndVariable(String instId, String variableName, Object oldValue);
	/**
	 * 根据流程变量ID，更新流程变量值
	 * @param id
	 * @param newValue
	 */
	void updateRunVariableById(String id, String newValue);
	/**
	 * 根据流程实例ID，流程变量名，流程变量旧值，更新流程变量值
	 * @param instId
	 * @param variableName
	 * @param oldValue
	 * @param newValue
	 */
	void updateRunVariableByInstIdAndVariable(String instId, String variableName, Object oldValue, String newValue);
}
