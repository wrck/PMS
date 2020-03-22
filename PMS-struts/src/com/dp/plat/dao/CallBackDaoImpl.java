package com.dp.plat.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.CallBackComment;
import com.dp.plat.data.bean.CallBackQuesnaire;
import com.dp.plat.util.ActivityMessage;

/**
 * 回访数据数据库管理
 * @author admin
 *
 */
public class CallBackDaoImpl extends BaseDao implements CallBackDao{

	@Override
	public int insertCallBack(CallBack callBack) {
		callBack.setApplyBy(getCurrUsername());
		callBack.setApplyTime(new Date());
		return (Integer) getSqlMapClientTemplate().insert("insert_callback_info", callBack);
	}

	@Override
	public void updateCallBackInstId(int callBackId, String instId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instId", instId);
		params.put("applyState", ActivityMessage.FLOW_RUNING);
		params.put("callBackId", callBackId);
		params.put("updateTime", new Date());
		params.put("updateBy", getCurrUsername());
		getSqlMapClientTemplate().update("update_callback_instid", params );
	}

	@Override
	public CallBack queryCallBackById(int callBackId) {
		return (CallBack) getSqlMapClientTemplate().queryForObject( "query_callback_byId" , callBackId);
	}

	@Override
	public int queryCallBackQuesnaireVersion(int callBackId) {
		Object object = getSqlMapClientTemplate().queryForObject("query_cb_quesnaire_version", callBackId);
		return object == null ? 1 : (Integer)object+1;
	}

	@Override
	public void insertCallBackQuesnaire(CallBackQuesnaire cbq) {
		getSqlMapClientTemplate().insert("insert_callback_quesnaire", cbq);
	}

	@Override
	public CallBackQuesnaire queryCbQuesnaire(int quesnaireId) {
		return (CallBackQuesnaire) getSqlMapClientTemplate().queryForObject("query_callback_quesnaire", quesnaireId);
	}

	@Override
	public int queryQuesnaireTemplateID(int quesnaireId) {
		Object object = getSqlMapClientTemplate().queryForObject("query_quesnaire_template_id", quesnaireId);
		if(object == null){
			return 0;
		}
		return (Integer)object;
//		return (Integer) getSqlMapClientTemplate().queryForObject("query_quesnaire_template_id", quesnaireId);
	}

	@Override
	public int queryCallBackQuesnaireId(CallBack callBack) {
		Object object = getSqlMapClientTemplate().queryForObject("query_callbackQuesnaireId", callBack);
		if(object == null){
			return 0;
		}
		return (Integer)object;
	}

	@Override
	public void updateCallBackQuesnaire(int callbackQuesnaireId,
			int pmClQuesnaireResultHeaderId, int status) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", callbackQuesnaireId);
		params.put("quesnaireId", pmClQuesnaireResultHeaderId);
		params.put("quesnaireState", status);
		getSqlMapClientTemplate().update("update_callback_quesnaire", params);
	}

	@Override
	public void updateCallBackApplyState(int callBackId, int applyState) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", callBackId);
		params.put("applyState", applyState);
		params.put("updateTime", new Date());
		params.put("updateBy", getCurrUsername());
		getSqlMapClientTemplate().update("update_callback_applyState" , params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CallBackComment> queryCallBackComment(int callBackId) {
		return getSqlMapClientTemplate().queryForList("query_callback_comment", callBackId);
	}

	@Override
	public void updateCallBack(CallBack callBack) {
		getSqlMapClientTemplate().update("update_callback" , callBack);
	}

}
