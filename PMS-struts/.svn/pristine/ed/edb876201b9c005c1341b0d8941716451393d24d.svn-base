package com.dp.plat.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.param.DisplayParam;

public class PmClosedLoopQuesnaireDaoImpl extends BaseDao implements
		PmClosedLoopQuesnaireDao {

	@Override
	public int insertQuesnaireHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
		return (Integer)getSqlMapClientTemplate().insert("insert_questionnaire_template_header_oneObj", pmClosedLoopQuesnaire);		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PmClosedLoopQuesnaire> selectQuesnaireHeaderList(
			PmClosedLoopQuesnaire pmClosedLoopQuesnaire,
			DisplayParam displayParam) {
		return (List<PmClosedLoopQuesnaire>)getSqlMapClientTemplate().queryForList("select-quesnaire_template_header-list",pmClosedLoopQuesnaire);
		
	}

	@Override
	public int insertQuesnaireLineList(
			PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine) {
		int id=(Integer)getSqlMapClientTemplate().insert("insert-quesnaire_template_line-obj",pmClosedLoopQuesnaireLine);
		return id;
	}

	@Override
	public int insertQuesnaireOptList(
			List<PmClosedLoopQuesnaireOpt> pmClosedLoopQuesnaireOptList,int questionId) {
		Map<String, Object>paramMap=new HashMap<String, Object>();
		paramMap.put("questionId", questionId);
		paramMap.put("pmClosedLoopQuesnaireOptList", pmClosedLoopQuesnaireOptList);
		getSqlMapClientTemplate().insert("insert-quesnaire_template_options-list",paramMap);
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PmClosedLoopQuesnaireLine> queryPmClQuesnaireLineList(
			PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine, String sqlType) {
		if(sqlType==null||sqlType.equals("")){
			sqlType="desc";
		}
		Map<String, Object>parameterMap=new HashMap<String, Object>();
		parameterMap.put("pmClosedLoopQuesnaireLine", pmClosedLoopQuesnaireLine);
		parameterMap.put("sqlType", sqlType);
		return (List<PmClosedLoopQuesnaireLine>)getSqlMapClientTemplate().queryForList("query-quesnaire_template_line-list",parameterMap);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PmClosedLoopQuesnaireOpt> queryPmClosedLoopQuesnaireOptList(
			PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt, String sqlType) {
		if(sqlType==null||sqlType.equals("")){
			sqlType="desc";
		}
		Map<String, Object>parameterMap=new HashMap<String, Object>();
		parameterMap.put("pmClosedLoopQuesnaireOpt", pmClosedLoopQuesnaireOpt);
		parameterMap.put("sqlType", sqlType);
		return (List<PmClosedLoopQuesnaireOpt>)getSqlMapClientTemplate().queryForList("query-quesnaire_template_options-list",parameterMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer,PmClosedLoopQuesnaireOpt> queryPmClosedLoopQuesnaireOptMap(
			PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt) {
		Map<String, Object>parameterMap=new HashMap<String, Object>();
		parameterMap.put("pmClosedLoopQuesnaireOpt", pmClosedLoopQuesnaireOpt);
		parameterMap.put("sqlType", "desc");
		return (Map<Integer,PmClosedLoopQuesnaireOpt>)getSqlMapClientTemplate().queryForMap("query-quesnaire_template_options-list", parameterMap, "id");

	}
	
	@Override
	public void updateQuesHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire){
		getSqlMapClientTemplate().update("update-quesnaire_template_header",pmClosedLoopQuesnaire);
	}
	
	@Override
	public int updateQuesStatus(PmClosedLoopQuesnaire pmClosedLoopQuesnaire){
		if((pmClosedLoopQuesnaire.getQuesType()==null||pmClosedLoopQuesnaire.getQuesType().equals(""))&&pmClosedLoopQuesnaire.getId()<=0){
			return -1;
		}
		getSqlMapClientTemplate().update("updateStatus-quesnaire_template_header",pmClosedLoopQuesnaire);
		return 1;
	}
	
	@Override
	public int deleteQuesLine(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine) {
		return (Integer)getSqlMapClientTemplate().delete("delete-quesnaire_template_line",pmClosedLoopQuesnaireLine);
	}
	
	@Override
	public void deleteQuesOpt(PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt){
		getSqlMapClientTemplate().delete("delete-quesnaire_template_options",pmClosedLoopQuesnaireOpt);
	}
	
	@Override
	public void updateLineQuesnum(PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine){
		getSqlMapClientTemplate().update("update-quesnaire_template_header-questionNum",pmClosedLoopQuesnaireLine);
	}
	
	@Override
	public int deleteQuesHeader(PmClosedLoopQuesnaire pmClosedLoopQuesnaire){
		return (Integer)getSqlMapClientTemplate().delete("delete-quesnaire_template_header",pmClosedLoopQuesnaire);
	}
	
	@Override
	public void deleteLineAll(int quesnaireTemplateHeaderId){
		getSqlMapClientTemplate().delete("delete-quesnaire_template_line-all",quesnaireTemplateHeaderId);
	}
	
	@Override
	public void deleteOptAll(int quesnaireTemplateHeaderId){
		getSqlMapClientTemplate().delete("delete-quesnaire_template_options-all",quesnaireTemplateHeaderId);
	}
	
	@Override
    public int addPmClQuesResultHeader(PmClQuesnaireResultHeader pmClQuesnaireResultHeader){
        pmClQuesnaireResultHeader.setCreatedPerson(getCurrUsername());
        return (Integer)getSqlMapClientTemplate().insert("insert-quesnaire_result_header-obj",pmClQuesnaireResultHeader);
    }
	
	@Override
    public void addPmClQuesResultLineList(List<PmClQuesnaireResultLine>pmClQuesnaireResultLineList, int pmClQuesnaireResultHeaderId){
	    if (pmClQuesnaireResultLineList == null || pmClQuesnaireResultLineList.isEmpty()) {
	        return;
	    }
	    Map<String, Object>parameterMap=new HashMap<String, Object>();
        parameterMap.put("pmClQuesnaireResultLineList", pmClQuesnaireResultLineList);
        parameterMap.put("pmClQuesnaireResultHeaderId", pmClQuesnaireResultHeaderId);
        getSqlMapClientTemplate().insert("insert-quesnaire_result_line-obj", parameterMap);
    }
}
