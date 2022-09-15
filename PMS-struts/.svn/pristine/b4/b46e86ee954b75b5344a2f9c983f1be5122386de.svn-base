package com.dp.plat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.WorkflowCommonParam;

public interface PmClosedLoopService {
	/**
	 * 项目经理提交闭环申请
	 * @param workflowCommonParam
	 * @param pmClEvaluationHeader
	 * @param project TODO
	 * @return TODO
	 */
	String addPmCLApply(WorkflowCommonParam workflowCommonParam,PmClEvaluationHeader pmClEvaluationHeader, Project project);
	
	/**
	 * 根据项目编码获得任务Id，私有任务
	 * @param projectList
	 */
	void getProjectSefTaskId(List<Project>projectList);
	
	/**
	 * 服务经理审核提交
	 * @param workflowCommonParam
	 * @param pmClEvaluationHeader
	 * @param project TODO
	 * @return TODO
	 */
	String addSmCLApply(WorkflowCommonParam workflowCommonParam,PmClEvaluationHeader pmClEvaluationHeader, Project project);
	
	/**
	 *根据流程定义Key获取最新版流程发布Id和流程发布图片
	 * @param keyString
	 * @param workflowCommonParam
	 * @return
	 */
	void querymaxDefinitionObjByKey(String keyString,WorkflowCommonParam workflowCommonParam);
	
	/**
	 * 获取项目经理审核的头信息集合
	 * @param pmClEvaluationHeader
	 * @return
	 */
	List<PmClEvaluationHeader> queryPmEvaluationHeaderList(
			PmClEvaluationHeader pmClEvaluationHeader);
	
	/**
	 * 获取项目经理申请的最新头信息
	 * @param pmClEvaluationHeader
	 * @return
	 */
	Map<String,Integer>queryEvaluationHeaderMap(PmClEvaluationHeader pmClEvaluationHeader);
	
	/**
	 * 回访结果提交
	 * @param workflowCommonParam
	 * @param pmClEvaluationHeader
	 * @param project TODO
	 * @return
	 */
	int addCbCLApply(WorkflowCommonParam workflowCommonParam,PmClEvaluationHeader pmClEvaluationHeader, Project project);
	
	/**
	 * 回访结果提交
	 * @param workflowCommonParam
	 * @param pmClEvaluationHeader
	 * @param project TODO
	 * @return
	 */
	int addClCLApply(WorkflowCommonParam workflowCommonParam,PmClEvaluationHeader pmClEvaluationHeader, Project project);
	
	/**
	 * 根据流程实例BusinessKey获取流程变量
	 * @param project
	 * @return
	 */
	Map<String, Object>queryProcessVarMap(Project project);
	
	/**
	 * 根据项目编码获得任务Id，公有任务
	 * @param projectList
	 */
	void getProjectPubTaskId(List<Project>projectList);
	
	/**
	 * 根据BusinessKey获取流程任务Id
	 * @param project
	 * @return
	 */
	String queryTaskByBussinessKey(Project project);
	
	/**
	 * 
	 * 根据BusinessKey和assignee获取流程任务Id
	 * @param project
	 * @param assignee
	 * @return
	 */
	String queryTaskByBussinessKeyAndUser(Project project, String assignee);
	
	/**
	 * 插入问卷结果信息
	 * @param workflowCommonParam
	 * @param pmClEvaluationHeader
	 * @param project
	 * @param pmClQuesnaireResultHeader
	 * @param pmClQuesnaireResultLineList
	 * @return
	 */
	int addCbCLApplyQues(WorkflowCommonParam workflowCommonParam,
			PmClEvaluationHeader pmClEvaluationHeader, Project project, PmClQuesnaireResultHeader pmClQuesnaireResultHeader, List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList);
	
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
	 * @param sqlText
	 * @return
	 */
	Map<String, PmClEvaluationHeader> queryEvaluationHeaderObjMap(PmClEvaluationHeader pmClEvaluationHeader, String sqlText);
	
	/**
	 *删除审核信息，包括审核头信息、问卷结果头信息、问卷结果行信息
	 * @param pmObj
	 */
	void deletePmClEvaRecur(PmClEvaluationHeader pmObj);

	/** 
	 * 更新评审头下一个审批人信息
	 * @param params
	 */
	void updateEvaluationHeaderNextAcceptPerson(HashMap<String, String> params);

}
