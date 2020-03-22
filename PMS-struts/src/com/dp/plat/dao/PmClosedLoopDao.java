package com.dp.plat.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.CallBackQuesnaire;
import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;

public interface PmClosedLoopDao {
	/**
	 *插入审核头信息
	 * @param pmClEvaluationHeader
	 * @return
	 */
	int addPmClEvaluationHeaderObj(PmClEvaluationHeader pmClEvaluationHeader);
	
	/**
	 * 获取项目经理申请的头信息集合
	 * @param pmClEvaluationHeader
	 * @return
	 */
	List<PmClEvaluationHeader> queryEvaluationHeaderList(PmClEvaluationHeader pmClEvaluationHeader);
	
	/**
	 * 获取项目经理申请的最新头信息
	 * @param pmClEvaluationHeader
	 * @return
	 */
	Map<String,Integer>queryEvaluationHeaderMap(PmClEvaluationHeader pmClEvaluationHeader);
	
	/**
	 *插入问卷结果头信息
	 * @param pmClQuesnaireResultHeader
	 * @return
	 */
	int addPmClQuesResultHeader(PmClQuesnaireResultHeader pmClQuesnaireResultHeader);
	
	/**
	 *插入问卷结果行信息
	 * @param pmClQuesnaireResultLineList
	 * @param pmClQuesnaireResultHeaderId TODO
	 */
	void addPmClQuesResultLineList(List<PmClQuesnaireResultLine>pmClQuesnaireResultLineList, int pmClQuesnaireResultHeaderId);
	
	 /**
	 * 插入问卷结果行信息
     * @param pmClQuesnaireResultLineList
     * @param pmClQuesnaireResultHeader
     */
    void addPmClQuesResultLineList(List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList, PmClQuesnaireResultHeader pmClQuesnaireResultHeader);

    
	/**
	 * 删除回访结果头信息
	 * @param pmClEvaluationHeader
	 */
	void deleteEvaluationHeader(PmClEvaluationHeader pmClEvaluationHeader);
	
	/**
	 * 删除问卷结果头信息
	 * @param pmClQuesnaireResultHeader
	 */
	void deletePmClQuesResultHeader(PmClQuesnaireResultHeader pmClQuesnaireResultHeader);
	
	/**
	 * 删除问卷结果头信息
	 * @param pmClQuesnaireResultLine
	 */
	void deletePmClQuesResultLine(PmClQuesnaireResultLine pmClQuesnaireResultLine);
	
	/**
	 *更新审核头信息
	 * @param pmClEvaluationHeader
	 */
	void updateEvaluationHeaderObj(PmClEvaluationHeader pmClEvaluationHeader);
	
	/**
	 *获取问卷结果头信息集合
	 * @param pmClQuesnaireResultHeader
	 * @return
	 */
	List<PmClQuesnaireResultHeader>queryPmClQuesResultHeaderList(PmClQuesnaireResultHeader pmClQuesnaireResultHeader);
	
	/**
	 * 获取问卷结果行信息集合
	 * @param pmClQuesnaireResultLine
	 * @return
	 */
	List<PmClQuesnaireResultLine> queryPmClQuesResultLineList(PmClQuesnaireResultLine pmClQuesnaireResultLine);
	
	/**
	 *获取评审头信息Map，以Id为key
	 * @param pmClEvaluationHeader
	 * @param sqlText TODO
	 * @return
	 */
	Map<String, PmClEvaluationHeader> queryEvaluationHeaderObjMap(PmClEvaluationHeader pmClEvaluationHeader, String sqlText);
	/**
	 * 查询是否进行过回访流程
	 * @param projectId
	 */
	CallBackQuesnaire queryIsCallBack(int projectId);
	/**
	 * update pm_cl_quesnaire_result_header set evaluationHeaderId = ? where id = ?
	 * @param quesnaireId
	 * @param headerObj
	 */
	void updateEvaluationHeaderId(int quesnaireId, int headerObj);

	/**
	 * update pm_cl_evaluation_header set nextAcceptPerson = ? , nextAcceptPersonName = ?  where  nextAcceptPerson = ? and projectId in (?)
	 * @param params
	 */
	void updateEvaluationHeaderNextAcceptPerson(HashMap<String, String> params);

}
