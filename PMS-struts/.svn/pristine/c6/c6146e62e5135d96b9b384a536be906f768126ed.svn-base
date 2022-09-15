package com.dp.plat.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.CallBackQuesnaire;
import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;

public class PmClosedLoopDaoImpl extends BaseDao implements PmClosedLoopDao {

	@Override
	public int addPmClEvaluationHeaderObj(
			PmClEvaluationHeader pmClEvaluationHeader) {
		return (Integer)getSqlMapClientTemplate().insert("insert-evaluation_header-obj",pmClEvaluationHeader);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PmClEvaluationHeader> queryEvaluationHeaderList(
			PmClEvaluationHeader pmClEvaluationHeader) {
		return (List<PmClEvaluationHeader>)getSqlMapClientTemplate().queryForList("select-evaluation_header-list",pmClEvaluationHeader);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> queryEvaluationHeaderMap(
			PmClEvaluationHeader pmClEvaluationHeader) {
		return (Map<String, Integer>)getSqlMapClientTemplate().queryForMap("select-evaluation_header-maxDateMap", pmClEvaluationHeader, "projectCode","id");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, PmClEvaluationHeader> queryEvaluationHeaderObjMap(
			PmClEvaluationHeader pmClEvaluationHeader, String sqlText) {
		// 2018-07-12 注释，受mysql 5.17以下引擎影响，查询语句过慢，采用临时表的方式优化查询速度
		// Map<String, Object>parameterMap=new HashMap<String, Object>();
		// parameterMap.put("pmClEvaluationHeader", pmClEvaluationHeader);
		// parameterMap.put("sqlText", sqlText);
		// return (Map<String, PmClEvaluationHeader>)getSqlMapClientTemplate().queryForMap("select-evaluation_header-objMap", parameterMap, "projectCode");
		return queryEvaluationHeaderObjMapUserTempTable(pmClEvaluationHeader, sqlText);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, PmClEvaluationHeader> queryEvaluationHeaderObjMapUserTempTable(
			PmClEvaluationHeader pmClEvaluationHeader, String sqlText) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("pmClEvaluationHeader", pmClEvaluationHeader);
		parameterMap.put("sqlText", sqlText);

		// 创建最终客户临时表
		getSqlMapClientTemplate().insert("create_temp_final_customer_table");

		Map<String, PmClEvaluationHeader> pmClEvaluationHeaderMap = (Map<String, PmClEvaluationHeader>)getSqlMapClientTemplate().queryForMap("select-evaluation_header-objMap", parameterMap, "projectCode");
		
		// 删除最终客户临时表
		getSqlMapClientTemplate().delete("drop_temp_final_customer_table");
		
		return pmClEvaluationHeaderMap;
	}
	
	@Override
	public int addPmClQuesResultHeader(PmClQuesnaireResultHeader pmClQuesnaireResultHeader){
		pmClQuesnaireResultHeader.setCreatedPerson(getCurrUsername());
		return (Integer)getSqlMapClientTemplate().insert("insert-quesnaire_result_header-obj",pmClQuesnaireResultHeader);
	}
	
	@Override
	public void addPmClQuesResultLineList(List<PmClQuesnaireResultLine>pmClQuesnaireResultLineList, int pmClQuesnaireResultHeaderId){
        //		Map<String, Object>parameterMap=new HashMap<String, Object>();
//		parameterMap.put("pmClQuesnaireResultLineList", pmClQuesnaireResultLineList);
//		parameterMap.put("pmClQuesnaireResultHeaderId", pmClQuesnaireResultHeaderId);
//		getSqlMapClientTemplate().insert("insert-quesnaire_result_line-obj", parameterMap);
	    PmClQuesnaireResultHeader pmClQuesnaireResultHeader = new PmClQuesnaireResultHeader();
	    pmClQuesnaireResultHeader.setId(pmClQuesnaireResultHeaderId);
	    this.addPmClQuesResultLineList(pmClQuesnaireResultLineList, pmClQuesnaireResultHeader);
	}
	
	@Override
    public void addPmClQuesResultLineList(List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList, PmClQuesnaireResultHeader pmClQuesnaireResultHeader) {
	    if (pmClQuesnaireResultHeader == null || pmClQuesnaireResultHeader.getId() == 0) {
	        return;
	    }
	    Map<String, Object>parameterMap=new HashMap<String, Object>();
        parameterMap.put("pmClQuesnaireResultLineList", pmClQuesnaireResultLineList);
        parameterMap.put("pmClQuesnaireResultHeaderId", pmClQuesnaireResultHeader.getId());
        if (pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId() != 0) {
            parameterMap.put("pmClQuesnaireTemplateHeaderId", pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
        }
        getSqlMapClientTemplate().insert("insert-quesnaire_result_line-obj", parameterMap);
    }

    @Override
	public void deletePmClQuesResultHeader(PmClQuesnaireResultHeader pmClQuesnaireResultHeader){
		getSqlMapClientTemplate().delete("delete-quesnaire_result_header",pmClQuesnaireResultHeader);
	}
	
	@Override
	public void deletePmClQuesResultLine(PmClQuesnaireResultLine pmClQuesnaireResultLine){
		getSqlMapClientTemplate().delete("delete-quesnaire_result_line",pmClQuesnaireResultLine);
	}
	
	@Override
	public void updateEvaluationHeaderObj(PmClEvaluationHeader pmClEvaluationHeader){
		getSqlMapClientTemplate().update("update-evaluation_header-obj",pmClEvaluationHeader);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PmClQuesnaireResultHeader>queryPmClQuesResultHeaderList(PmClQuesnaireResultHeader pmClQuesnaireResultHeader){
		return (List<PmClQuesnaireResultHeader>)getSqlMapClientTemplate().queryForList("select-quesnaire_result_header-list",pmClQuesnaireResultHeader);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PmClQuesnaireResultLine> queryPmClQuesResultLineList(PmClQuesnaireResultLine pmClQuesnaireResultLine){
		return (List<PmClQuesnaireResultLine>)getSqlMapClientTemplate().queryForList("select-quesnaire_result_line-list",pmClQuesnaireResultLine);
	}

	@Override
	public void deleteEvaluationHeader(PmClEvaluationHeader pmClEvaluationHeader) {
		getSqlMapClientTemplate().delete("delete-evaluation_header",pmClEvaluationHeader);
		
	}

	@Override
	public CallBackQuesnaire queryIsCallBack(int projectId) {
		return (CallBackQuesnaire) getSqlMapClientTemplate().queryForObject("query_is_callback", projectId);
	}

	@Override
	public void updateEvaluationHeaderId(int quesnaireId, int headerObj) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("quesnaireId", quesnaireId);
		params.put("headerId", headerObj);
		getSqlMapClientTemplate().update("update_EvaluationHeaderId_byId", params);
	}

	@Override
	public void updateEvaluationHeaderNextAcceptPerson(HashMap<String, String> params) {
		getSqlMapClientTemplate().update("update_EvaluationHeader_NextAcceptPerson", params);
	}
	
}
