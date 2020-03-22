package com.dp.plat.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.activity.ActComment;
import com.dp.plat.data.activity.Procdef;

public class WorkflowDaoImpl extends BaseDao implements WorkflowDao{

	@Override
	public Procdef queryProcdef(Procdef procdef) {
		return (Procdef) getSqlMapClientTemplate().queryForObject("query_procdef", procdef);
	}

	@Override
	public void insertActComment(int callBackId, String key, String taskId,
			String instId, int result, String message) {
		ActComment comment = new ActComment(callBackId, key, taskId, instId, getCurrUsername(), new Date(), result, message);
		getSqlMapClientTemplate().insert("insert_fnd_act_comment", comment);
	}

	@Override
	public void insertActComment(HashMap<String, Object> params) {
		params.put("assignee", getCurrUsername());
		params.put("assigneeTime", new Date());
		getSqlMapClientTemplate().insert("insert_fnd_act_comment_by_params", params);
	}
	
	@Override
	public void updateSelfActComment(HashMap<String, Object> params) {
		getSqlMapClientTemplate().update("update_fnd_act_comment_by_params", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActComment> queryActComment(int objId , String procdefKey) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("objId", objId);
		param.put("procdefKey", procdefKey);
		return getSqlMapClientTemplate().queryForList("query_act_comment_list", param);
	}

	@Override
	public void updateApplytableInfo(String tableName, String instId, int objId ,String objColumn) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tableName", tableName);
		param.put("instId", instId);
		param.put("objId", objId);
		param.put("applyBy", getCurrUsername());
		param.put("objColumn", objColumn);
		getSqlMapClientTemplate().update("update_apply_info_byobjid", param);
	}

	@Override
	public String queryTaskByInstIdAndVariable(String instId, String variableName, Object oldValue) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("instId", instId);
		param.put("NAME_", variableName);
		param.put("TEXT_", oldValue);
		param.put("TYPE_", oldValue.getClass().getSimpleName().toLowerCase());
		return (String) getSqlMapClientTemplate().queryForObject("queryTaskByInstIdAndVariable", param);
	}

	@Override
	public void updateRunVariableByInstIdAndVariable(String instId, String variableName, Object oldValue, String newValue) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("instId", instId);
		param.put("NAME_", variableName);
		param.put("TEXT_", oldValue);
		param.put("TYPE_", oldValue.getClass().getSimpleName().toLowerCase());
		param.put("value", newValue);
		getSqlMapClientTemplate().update("updateRunVariableByInstIdAndVariable", param);
	}

	@Override
	public void updateRunVariableById(String id, String newValue) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);
		param.put("TEXT_", newValue);
		getSqlMapClientTemplate().update("updateRunVariableById", param);
	}

}
