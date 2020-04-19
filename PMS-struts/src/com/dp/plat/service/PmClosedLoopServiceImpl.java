package com.dp.plat.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.dao.PmClosedLoopDao;
import com.dp.plat.data.bean.CallBackQuesnaire;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.PmClEvaluationHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.PmClosedLoopConstant;
import com.dp.plat.util.PmClosedLoopUtil;
import com.dp.plat.util.StringEscUtil;

public class PmClosedLoopServiceImpl extends BaseServiceImpl implements PmClosedLoopService {
	private PmClosedLoopDao pmClosedLoopDao;
	private WorkFlowService workFlowService;
	private SendMailService sendMailService;
	private UserManageService userManageService;
	private ProjectService projectService;		

	public String addPmCLApply(WorkflowCommonParam workflowCommonParam,PmClEvaluationHeader pmClEvaluationHeader, Project project){
		log("发起闭环申请");
		String nowUser=UserContext.getUserContext().getUser().getUsername();
		//业务信息更新，如项目状态，人员操作信息的插入
		pmClEvaluationHeader.setCreatedPerson(nowUser);
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_PM);
		pmClEvaluationHeader.setStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
		pmClEvaluationHeader.setEvaluationResult(PmClosedLoopConstant.CL_EVALU_RESULT_AGREE);
		pmClEvaluationHeader.setEvaluationScore(0);
		pmClEvaluationHeader.setEvaluationPeopleId(nowUser);
		pmClEvaluationHeader.setEvaluationPeopleName(UserContext.getUserContext().getUser().getRealName());

		String taskUserKey=PmClosedLoopConstant.CL_TASK_USER_2;
		int evaResult=pmClEvaluationHeader.getEvaluationResult();
		int processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_SM;
		StringBuilder nextAssignPer=new StringBuilder();
		
		
		if(project.getServiceManagerCode().equals(nowUser)){
			String roleStr="";
			
			//是否已通过回访
			if(isPassCb(project)){
				pmClEvaluationHeader.setNextAcceptPerson("工程人员");
				pmClEvaluationHeader.setNextAcceptPersonName("工程人员");
				
				roleStr=";"+MessageUtil.ROLE_ENGINEEMANAGER+";";
				taskUserKey=PmClosedLoopConstant.CL_TASK_USER_4;
				evaResult=3;
				processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_CL;
			}else{
				pmClEvaluationHeader.setNextAcceptPerson("回访人员");
				pmClEvaluationHeader.setNextAcceptPersonName("回访人员");
				
				roleStr=";"+MessageUtil.ROLE_CALLBACKPER+";";
				taskUserKey=PmClosedLoopConstant.CL_TASK_USER_3;
				evaResult=2;
				processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_CB;
			}
				
			nextAssignPer=getNextAssignPer(roleStr);
		}else{
			nextAssignPer.append(project.getServiceManagerCode()+",");
			pmClEvaluationHeader.setNextAcceptPerson(project.getServiceManagerCode());
			pmClEvaluationHeader.setNextAcceptPersonName(project.getServiceManagerCodeforjson().split("-")[1]);
		}
		
		//插入回访头信息
		int returnId=pmClosedLoopDao.addPmClEvaluationHeaderObj(pmClEvaluationHeader);
		
		if(returnId==0){
			return "";
		}
		//更新回访头信息，主要插入applyHeaderId
		pmClEvaluationHeader.setApplyHeaderId(returnId);
		pmClosedLoopDao.updateEvaluationHeaderObj(pmClEvaluationHeader);
		
		//增加流程变量
		Map<String, Object>vars=new HashMap<String, Object>();
		workflowCommonParam.setComment(pmClEvaluationHeader.getEvaluationComment()==null?"":pmClEvaluationHeader.getEvaluationComment());
		workflowCommonParam.setType(1);
		workflowCommonParam.setOutcome("1");
		
		//如果为驳回任务，先结束驳回任务，再启动新的流程
		if(project.getTaskId()!=null&&!(project.getTaskId().equals(""))){
			workflowCommonParam.setTaskId(project.getTaskId());
			vars.put(PmClosedLoopConstant.CL_TASK_EVALU_1, -2);
			vars.put(PmClosedLoopConstant.CL_PROJECT_PROCESS_STATUS, PmClosedLoopConstant.CL_EVALU_TYPE_END);
			workFlowService.submitSelfTask(workflowCommonParam, vars);
		}

		vars.clear();
		String keyString=PmClosedLoopConstant.CL_PROCESS_KEY;
		String projectCode=pmClEvaluationHeader.getProjectCode();
		vars.put(PmClosedLoopConstant.CL_TASK_USER_1,nowUser);
		vars.put("classType", keyString);
		vars.put("projectCode", projectCode);
		vars.put("objId", returnId);
		vars.put(PmClosedLoopConstant.CL_PROJECT_PROCESS_STATUS, PmClosedLoopConstant.CL_EVALU_TYPE_PM);	//记录项目闭环状态
		
		//启动流程
		String businessKey=keyString+"."+pmClEvaluationHeader.getProjectCode()+"."+returnId;
		ProcessInstance processInstance=workFlowService.startProcess(keyString, businessKey, vars);
		
		//办理任务
		List<Task>taskList=workFlowService.findPersonalTask(nowUser);
		for (Task task : taskList) {
			if(task!=null&&task.getProcessInstanceId().equals(processInstance.getId())){
				workflowCommonParam.setTaskId(task.getId());
			}
		}

		vars.clear();
		vars.put(taskUserKey, nextAssignPer.substring(0, nextAssignPer.length()-1));	//添加回访人员
		vars.put(PmClosedLoopConstant.CL_TASK_EVALU_1,evaResult );//判断变量
		vars.put(PmClosedLoopConstant.CL_PROJECT_PROCESS_STATUS, processStatus);	//记录项目闭环状态
	
		workFlowService.submitSelfTask(workflowCommonParam, vars);
		
		//if(processStatus==PmClosedLoopConstant.CL_EVALU_TYPE_CB)
			mailPerson(project, processStatus, pmClEvaluationHeader, PmClosedLoopConstant.CL_EVALU_TYPE_PM, nowUser);
		
		// 更新项目闭环流程状态
		this.updateProjectCloseProcessState(project, processStatus);
		return workflowCommonParam.getTaskId();
		
	}
	
    public String addSmCLApply(WorkflowCommonParam workflowCommonParam,PmClEvaluationHeader pmClEvaluationHeader, Project project){
		log("服务经理审核 闭环申请");
		String nowUser=UserContext.getUserContext().getUser().getUsername();
		
		String roleStr=";"+MessageUtil.ROLE_CALLBACKPER+";";
		String taskUserKey=PmClosedLoopConstant.CL_TASK_USER_3;
		int evaResult=pmClEvaluationHeader.getEvaluationResult();
		int processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_CB;
		String nextPerson="回访人员";
		
		CallBackQuesnaire cbQuesnaire = isCallBack(project.getProjectId()) ;
		//是否已通过回访
		if(isPassCb(project) || (cbQuesnaire != null)){
			if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_AGREE){
				roleStr=";"+MessageUtil.ROLE_ENGINEEMANAGER+";";
				taskUserKey=PmClosedLoopConstant.CL_TASK_USER_4;
				evaResult=2;
				processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_CL;
				nextPerson="工程人员";
			}
		}
						
		//业务信息更新，如项目状态，人员操作信息的插入
		pmClEvaluationHeader.setCreatedPerson(nowUser);
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_SM);
		pmClEvaluationHeader.setStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
		pmClEvaluationHeader.setEvaluationScore(0);
		pmClEvaluationHeader.setEvaluationPeopleId(nowUser);
		pmClEvaluationHeader.setEvaluationPeopleName(UserContext.getUserContext().getUser().getRealName());
		pmClEvaluationHeader.setNextAcceptPersonName(nextPerson);
		pmClEvaluationHeader.setNextAcceptPerson(nextPerson);
		
		if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT){
			taskUserKey=PmClosedLoopConstant.CL_TASK_USER_1;
			processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_PM;
			String programManagerCodeB = "";
			String programManagerNameB = "";
			if(StringUtils.isNotBlank(project.getProgramManagerCodeB())){
				programManagerCodeB = "," + project.getProgramManagerCodeB();
				programManagerNameB = "," + project.getProgramManagerCodeforjsonB().split("-")[1];
			}
			pmClEvaluationHeader.setNextAcceptPerson(project.getProgramManagerCode() + programManagerCodeB);
			pmClEvaluationHeader.setNextAcceptPersonName(project.getProgramManagerCodeforjson().split("-")[1] + programManagerNameB);
		}
				
		//插入回访头信息
		int returnId=pmClosedLoopDao.addPmClEvaluationHeaderObj(pmClEvaluationHeader);
		
		if(cbQuesnaire != null){
			//添加回访问卷信息
			PmClEvaluationHeader evaluationHeader = new PmClEvaluationHeader();
			evaluationHeader.setProjectCode(pmClEvaluationHeader.getProjectCode());
			evaluationHeader.setProjectName(pmClEvaluationHeader.getProjectName());
			evaluationHeader.setProjectId(pmClEvaluationHeader.getProjectId());
			evaluationHeader.setEvaluationTime(cbQuesnaire.getCreateTime());
			evaluationHeader.setEvaluationPeopleName("回访人员");
			evaluationHeader.setEvaluationPeopleId(cbQuesnaire.getCreateBy());
			evaluationHeader.setEvaluationComment("运营商直签项目已完成回访");
			evaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_CB);
			evaluationHeader.setStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
			evaluationHeader.setEvaluationResult(PmClosedLoopConstant.CL_EVALU_RESULT_AGREE);//测评通过
			evaluationHeader.setCreatedTime(new Date());
			evaluationHeader.setCreatedPerson(getLoginName());
			evaluationHeader.setNextAcceptPersonName(nextPerson);
			evaluationHeader.setNextAcceptPerson(nextPerson);
			evaluationHeader.setApplyHeaderId(pmClEvaluationHeader.getApplyHeaderId());
			
			int headerObj = pmClosedLoopDao.addPmClEvaluationHeaderObj(evaluationHeader);
			//将主键写到回访信息表中
			pmClosedLoopDao.updateEvaluationHeaderId(cbQuesnaire.getQuesnaireId() , headerObj);
		}
		
		
		if(returnId==0){
			return "";
		}
				
		//办理任务
		Map<String, Object>vars=new HashMap<String, Object>();

		StringBuilder nextAssignPer=getNextAssignPer(roleStr);

		if(processStatus==PmClosedLoopConstant.CL_EVALU_TYPE_PM){
			vars.put(taskUserKey, project.getProgramManagerCode());
		}else{
			vars.put(taskUserKey, nextAssignPer.substring(0, nextAssignPer.length()-1));	
		}
		
		vars.put(PmClosedLoopConstant.CL_TASK_EVALU_1,evaResult );//判断变量
		vars.put(PmClosedLoopConstant.CL_PROJECT_PROCESS_STATUS, processStatus);	//记录项目闭环状态
				
		workflowCommonParam.setComment(pmClEvaluationHeader.getEvaluationComment());
		workflowCommonParam.setType(1);
		workflowCommonParam.setOutcome("1");
		
		workFlowService.submitSelfTask(workflowCommonParam, vars);
				
		//邮件发送
		if(processStatus==PmClosedLoopConstant.CL_EVALU_TYPE_PM||processStatus==PmClosedLoopConstant.CL_EVALU_TYPE_CB){
			log("发送服务经理审核邮件");
			mailPerson(project, processStatus, pmClEvaluationHeader, PmClosedLoopConstant.CL_EVALU_TYPE_SM, nowUser);
			log("发送服务经理审核邮件成功");
		}
		
		// 更新项目闭环流程状态
        this.updateProjectCloseProcessState(project, processStatus);
		return workflowCommonParam.getTaskId();	
	}
	/**
	 * 
	 * @param projectId
	 * @return
	 */
	private CallBackQuesnaire isCallBack(int projectId) {
		return pmClosedLoopDao.queryIsCallBack(projectId);
	}

	public int addCbCLApplyQues(WorkflowCommonParam workflowCommonParam,
			PmClEvaluationHeader pmClEvaluationHeader, Project project, PmClQuesnaireResultHeader pmClQuesnaireResultHeader, List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
		log("提交测评问卷信息");
		String nowUser=UserContext.getUserContext().getUser().getUsername();
		
		//业务信息更新，如项目状态，人员操作信息的插入
		pmClEvaluationHeader.setCreatedPerson(nowUser);
		pmClEvaluationHeader.setEvaluationPeopleId(nowUser);
		pmClEvaluationHeader.setEvaluationPeopleName(UserContext.getUserContext().getUser().getRealName());		
		pmClEvaluationHeader.setStatus(PmClosedLoopConstant.CL_STATUS_DRAFT);
		int returnId=-1;
		if(pmClEvaluationHeader.getId()==0){
			//1.插入回访头信息
			returnId=pmClosedLoopDao.addPmClEvaluationHeaderObj(pmClEvaluationHeader);
			
			if(returnId<=0){
				return -1;
			}
			
			//2.插入问卷结果头信息
			pmClQuesnaireResultHeader.setEvaluationHeaderId(returnId);
			int pmClQuesnaireResultHeaderId=pmClosedLoopDao.addPmClQuesResultHeader(pmClQuesnaireResultHeader);
			if(pmClQuesnaireResultHeaderId<=0){
				return -1;
			}
			
			//3.插入问卷结果行信息
			pmClosedLoopDao.addPmClQuesResultLineList(pmClQuesnaireResultLineList, pmClQuesnaireResultHeaderId);
			
			
			//认领任务,但不办理
			workFlowService.claimTask(workflowCommonParam.getTaskId(), nowUser);

			return returnId;
			
		}else{
			//1.更新回访头信息
			PmClEvaluationHeader pmObj=new PmClEvaluationHeader();
			pmObj.setId(pmClEvaluationHeader.getId());
			pmObj.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_CB);
			pmClosedLoopDao.deleteEvaluationHeader(pmObj);			
			returnId=pmClosedLoopDao.addPmClEvaluationHeaderObj(pmClEvaluationHeader);
			
			if(returnId<=0){
				return -1;
			}
			
			//2.删除回访问卷头信息，并插入
			pmClQuesnaireResultHeader.setEvaluationHeaderId(pmObj.getId());
			pmClosedLoopDao.deletePmClQuesResultHeader(pmClQuesnaireResultHeader);
			
			if(pmClQuesnaireResultHeader.getId()==0){
				return -1;
			}
			PmClQuesnaireResultLine pmClQuesnaireResultLine=new PmClQuesnaireResultLine();
			pmClQuesnaireResultLine.setQuesnaireResultHeaderId(pmClQuesnaireResultHeader.getId());
			
			pmClQuesnaireResultHeader.setEvaluationHeaderId(returnId);
			int returnId2=pmClosedLoopDao.addPmClQuesResultHeader(pmClQuesnaireResultHeader);
			if(returnId2<=0){
				return -1;
			}
			
			//3.删除回访问卷行信息，并插入
			pmClosedLoopDao.deletePmClQuesResultLine(pmClQuesnaireResultLine);
			pmClQuesnaireResultLine.setId(returnId);
			pmClosedLoopDao.addPmClQuesResultLineList(pmClQuesnaireResultLineList, returnId2);
			
			return pmClEvaluationHeader.getId();
		}
	}
	
	@Override
	public int addCbCLApply(WorkflowCommonParam workflowCommonParam,
			PmClEvaluationHeader pmClEvaluationHeader, Project project) {
		log("项目回访");
		String nowUser=UserContext.getUserContext().getUser().getUsername();
		//业务信息更新，如项目状态，人员操作信息的插入
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_CB);
		pmClEvaluationHeader.setEvaluationPeopleId(nowUser);
		pmClEvaluationHeader.setEvaluationPeopleName(UserContext.getUserContext().getUser().getRealName());
		pmClEvaluationHeader.setProjectCode(project.getProjectCode());
		pmClEvaluationHeader.setNextAcceptPerson("工程人员");
		pmClEvaluationHeader.setNextAcceptPersonName("工程人员");
		pmClEvaluationHeader.setStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
		
		String taskUserKey=PmClosedLoopConstant.CL_TASK_USER_4;		
		int processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_CL;
		if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT){
			taskUserKey=PmClosedLoopConstant.CL_TASK_USER_1;
			processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_PM;
			pmClEvaluationHeader.setNextAcceptPerson(project.getProgramManagerCode());
			pmClEvaluationHeader.setNextAcceptPersonName(project.getProgramManagerCodeforjson().split("-")[1]);
		}else if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB){
			taskUserKey=PmClosedLoopConstant.CL_TASK_USER_2;
			processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_SM;
			pmClEvaluationHeader.setNextAcceptPerson(project.getServiceManagerCode());
			pmClEvaluationHeader.setNextAcceptPersonName(project.getServiceManagerCodeforjson().split("-")[1]);
		}
		
		if(pmClEvaluationHeader.getEvaluationResult()!=PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB){
			//更新回访头信息
			pmClEvaluationHeader.setUpdatedPerson(nowUser);
			pmClosedLoopDao.updateEvaluationHeaderObj(pmClEvaluationHeader);
		}else{
			pmClosedLoopDao.addPmClEvaluationHeaderObj(pmClEvaluationHeader);
		}
		
		//办理任务
		Map<String, Object>vars=new HashMap<String, Object>();	
		StringBuilder nextAssignPer=getNextAssignPer(";"+MessageUtil.ROLE_ENGINEEMANAGER+";");
				
		if(processStatus==PmClosedLoopConstant.CL_EVALU_TYPE_PM){
			vars.put(taskUserKey, project.getProgramManagerCode());
		}else if(processStatus==PmClosedLoopConstant.CL_EVALU_TYPE_SM){
			vars.put(taskUserKey, project.getServiceManagerCode());
		}else{
			vars.put(taskUserKey, nextAssignPer.substring(0, nextAssignPer.length()-1));	//需添加工程部人员
		}
		
		vars.put(PmClosedLoopConstant.CL_TASK_EVALU_1, pmClEvaluationHeader.getEvaluationResult());//判断变量
		vars.put(PmClosedLoopConstant.CL_PROJECT_PROCESS_STATUS, processStatus);	//记录项目闭环状态
		
		workflowCommonParam.setComment(pmClEvaluationHeader.getEvaluationComment());
		workflowCommonParam.setType(1);
		workflowCommonParam.setOutcome("1");
		
		if(processStatus==PmClosedLoopConstant.CL_EVALU_TYPE_SM){
			//认领任务
			workFlowService.claimTask(workflowCommonParam.getTaskId(), nowUser);
		}
		
		//办理任务
		workFlowService.submitSelfTask(workflowCommonParam, vars);
		
		//邮件发送
		log("发送项目回访邮件");
		mailPerson(project, processStatus, pmClEvaluationHeader, PmClosedLoopConstant.CL_EVALU_TYPE_CB, nowUser);
		log("发送项目回访邮件成功");
		
		// 更新项目闭环流程状态
        this.updateProjectCloseProcessState(project, processStatus);
		return pmClEvaluationHeader.getId();
	}
	
	@Override
	public int addClCLApply(WorkflowCommonParam workflowCommonParam,
			PmClEvaluationHeader pmClEvaluationHeader, Project project) {
		log("项目闭环");
		String nowUser=UserContext.getUserContext().getUser().getUsername();
		//业务信息更新，如项目状态，人员操作信息的插入
		pmClEvaluationHeader.setCreatedPerson(nowUser);
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_CL);
		pmClEvaluationHeader.setStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
		pmClEvaluationHeader.setEvaluationPeopleId(nowUser);
		pmClEvaluationHeader.setEvaluationPeopleName(UserContext.getUserContext().getUser().getRealName());
		pmClEvaluationHeader.setNextAcceptPersonName("");
		pmClEvaluationHeader.setNextAcceptPerson("");
		
		int processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_END;
		if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT){
			processStatus=PmClosedLoopConstant.CL_EVALU_TYPE_PM;
			pmClEvaluationHeader.setNextAcceptPerson(project.getProgramManagerCode());
			pmClEvaluationHeader.setNextAcceptPersonName(project.getProgramManagerCodeforjson().split("-")[1]);
		}
		
		//更新回访头信息
		pmClEvaluationHeader.setUpdatedPerson(nowUser);
		pmClosedLoopDao.updateEvaluationHeaderObj(pmClEvaluationHeader);
				
		//办理任务
		Map<String, Object>vars=new HashMap<String, Object>();
		if(processStatus==PmClosedLoopConstant.CL_EVALU_TYPE_PM){
			vars.put(PmClosedLoopConstant.CL_TASK_USER_1, project.getProgramManagerCode());
		}
		vars.put(PmClosedLoopConstant.CL_TASK_EVALU_1, pmClEvaluationHeader.getEvaluationResult());//判断变量
		vars.put(PmClosedLoopConstant.CL_PROJECT_PROCESS_STATUS, processStatus);	//记录项目闭环状态
		
		workflowCommonParam.setComment(pmClEvaluationHeader.getEvaluationComment());
		workflowCommonParam.setType(1);
		workflowCommonParam.setOutcome("1");
		
		/*//认领任务
		workFlowService.claimTask(workflowCommonParam.getTaskId(), nowUser);*/
		
		workFlowService.submitSelfTask(workflowCommonParam, vars);
		
		// 更新项目状态为已闭环
		if(pmClEvaluationHeader.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_AGREE){
			projectService.updateProjectStatus(project.getProjectId(),MessageUtil.PROJECT_STATE_CLOSEDLOOP);
		}
					
		//邮件发送
		log("发送项目闭环邮件");
		mailPerson(project, processStatus, pmClEvaluationHeader, PmClosedLoopConstant.CL_EVALU_TYPE_CL, nowUser); 
		log("发送项目闭环邮件成功");
		
		// 更新项目闭环流程状态
        this.updateProjectCloseProcessState(project, processStatus);
		return 0;
	}
	
    @Override
	public void getProjectSefTaskId(List<Project>projectList) {
    	log("获取私有任务Id");
		String nowUser=UserContext.getUserContext().getUser().getUsername();
		String classType=PmClosedLoopConstant.CL_PROCESS_KEY;
		PmClEvaluationHeader pmClEvaluationHeader=new PmClEvaluationHeader();
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_PM);
		if(projectList.size() == 1 && projectList.get(0) != null){
			pmClEvaluationHeader.setProjectCode(projectList.get(0).getProjectCode());
		}
		Map<String, Integer>evaluationHeaderIdMap=queryEvaluationHeaderMap(pmClEvaluationHeader);
		if(evaluationHeaderIdMap!=null){
			for (Project project : projectList) {
				String businessKey=classType+"."+project.getProjectCode()+"."+evaluationHeaderIdMap.get(project.getProjectCode());	
				Task task=workFlowService.queryTaskByBussinessKeyUser(businessKey,nowUser);
				if (task!=null) {
					project.setTaskId(task.getId());
				}
				
			}
		}
	}
    
    @Override
	public void getProjectPubTaskId(List<Project>projectList) {
    	log("获取公有任务Id");
		String nowUser=UserContext.getUserContext().getUser().getUsername();
		String classType=PmClosedLoopConstant.CL_PROCESS_KEY;
		PmClEvaluationHeader pmClEvaluationHeader=new PmClEvaluationHeader();
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_PM);
		if(projectList.size() == 1 && projectList.get(0) != null){
			pmClEvaluationHeader.setProjectCode(projectList.get(0).getProjectCode());
		}
		Map<String, Integer>evaluationHeaderIdMap=queryEvaluationHeaderMap(pmClEvaluationHeader);
		if(evaluationHeaderIdMap!=null){
			for (Project project : projectList) {
				String businessKey=classType+"."+project.getProjectCode()+"."+evaluationHeaderIdMap.get(project.getProjectCode());	
				Task task=workFlowService.queryPubTaskByBussinessKeyUser(businessKey,nowUser);
				if (task!=null) {
					project.setTaskId(task.getId());
				}
				
			}
		}
	}
	
	@Override
	public void querymaxDefinitionObjByKey(String keyString,
			WorkflowCommonParam workflowCommonParam) {
		workFlowService.querymaxDefinitionObjByKey(keyString, workflowCommonParam);
		
	}
	
	@Override
	public Map<String, Object> queryProcessVarMap(Project project) {
		log("获取流程状态变量");
		String classType=PmClosedLoopConstant.CL_PROCESS_KEY;
		PmClEvaluationHeader pmClEvaluationHeader=new PmClEvaluationHeader();
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_PM);
		Map<String, Integer>evaluationHeaderIdMap=queryEvaluationHeaderMap(pmClEvaluationHeader);
		if(evaluationHeaderIdMap!=null){
			String businessKey=classType+"."+project.getProjectCode()+"."+evaluationHeaderIdMap.get(project.getProjectCode());	
			Task task=workFlowService.queryTaskByBussinessKey(businessKey);
			if (task!=null){
				return workFlowService.queryProcessVarMap(task.getId());
			}
		}
		return null;
	}

	@Override
	public String queryTaskByBussinessKey(Project project){
		String classType=PmClosedLoopConstant.CL_PROCESS_KEY;
		PmClEvaluationHeader pmClEvaluationHeader=new PmClEvaluationHeader();
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_PM);
		
		Map<String, Integer>evaluationHeaderIdMap=queryEvaluationHeaderMap(pmClEvaluationHeader);
		Task task=null;
		if(evaluationHeaderIdMap!=null){
			String businessKey=classType+"."+project.getProjectCode()+"."+evaluationHeaderIdMap.get(project.getProjectCode());
			task= workFlowService.queryTaskByBussinessKey(businessKey);
		}
		return task==null?null:task.getId();
		
	}
	
	@Override
	public String queryTaskByBussinessKeyAndUser(Project project, String assignee){
		if (StringUtils.isBlank(assignee)) {
			return null;
		}
		String classType=PmClosedLoopConstant.CL_PROCESS_KEY;
		PmClEvaluationHeader pmClEvaluationHeader=new PmClEvaluationHeader();
		pmClEvaluationHeader.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_PM);
		Map<String, Integer>evaluationHeaderIdMap=queryEvaluationHeaderMap(pmClEvaluationHeader);
		Task task=null;
		if(evaluationHeaderIdMap!=null){
			String businessKey=classType+"."+project.getProjectCode()+"."+evaluationHeaderIdMap.get(project.getProjectCode());
			task = workFlowService.queryTaskByBussinessKeyUser(businessKey, assignee);
		}
		return task==null?null:task.getId();
		
	}
	
	private StringBuilder getNextAssignPer(String roleStr){
		User user=new User();
		user.setStatus(1);	//获取有效的回访人员或工程人员
		List<User>userList=userManageService.queryAllUserList(user);
		StringBuilder nextAssignPer=new StringBuilder();
		
		for (User userObj : userList) {
			if(userObj.getRoleids().contains(roleStr)){
				nextAssignPer.append(userObj.getUsername()+",");
			}
		}

		if(nextAssignPer.length()<=0){
			throw new RuntimeException("获取下一级审核人员出错");
		}
		
		return nextAssignPer;
	}
	
	private void mailPerson(Project project,int processStatus,PmClEvaluationHeader pmObj,int nowStatus,String nowUser){
		//获取所有回访人员
		User user=new User();
		user.setStatus(1);	
		List<User>userList=userManageService.queryAllUserList(user);

		Map<String, String>paraMap=new HashMap<String, String>();
		paraMap.put("role"+PmClosedLoopConstant.CL_EVALU_TYPE_CB, ";"+MessageUtil.ROLE_CALLBACKPER+";");
		paraMap.put("role"+PmClosedLoopConstant.CL_EVALU_TYPE_CL, ";"+MessageUtil.ROLE_ENGINEEMANAGER+";");
		
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_PM+PmClosedLoopConstant.CL_EVALU_TYPE_SM+PmClosedLoopConstant.CL_EVALU_RESULT_AGREE, PmClosedLoopConstant.CL_MAIL_TEMPLATE_08);
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_PM+PmClosedLoopConstant.CL_EVALU_TYPE_CB+PmClosedLoopConstant.CL_EVALU_RESULT_AGREE, PmClosedLoopConstant.CL_MAIL_TEMPLATE_07);
		
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_SM+PmClosedLoopConstant.CL_EVALU_TYPE_CB+PmClosedLoopConstant.CL_EVALU_RESULT_AGREE, PmClosedLoopConstant.CL_MAIL_TEMPLATE_07);
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_SM+PmClosedLoopConstant.CL_EVALU_TYPE_PM+PmClosedLoopConstant.CL_EVALU_RESULT_REJECT, PmClosedLoopConstant.CL_MAIL_TEMPLATE_01);
		
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_CB+PmClosedLoopConstant.CL_EVALU_TYPE_SM+PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB, PmClosedLoopConstant.CL_MAIL_TEMPLATE_06);
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_CB+PmClosedLoopConstant.CL_EVALU_TYPE_PM+PmClosedLoopConstant.CL_EVALU_RESULT_REJECT, PmClosedLoopConstant.CL_MAIL_TEMPLATE_03);
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_CB+PmClosedLoopConstant.CL_EVALU_TYPE_CL+PmClosedLoopConstant.CL_EVALU_RESULT_AGREE, PmClosedLoopConstant.CL_MAIL_TEMPLATE_02);
		
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_CL+PmClosedLoopConstant.CL_EVALU_TYPE_PM+PmClosedLoopConstant.CL_EVALU_RESULT_REJECT, PmClosedLoopConstant.CL_MAIL_TEMPLATE_05);
		paraMap.put(""+PmClosedLoopConstant.CL_EVALU_TYPE_CL+PmClosedLoopConstant.CL_EVALU_TYPE_END+PmClosedLoopConstant.CL_EVALU_RESULT_AGREE, PmClosedLoopConstant.CL_MAIL_TEMPLATE_04);
		
		Set<String>mailSet=new HashSet<String>();
		String mailTos="";
		for (User userObj : userList) {
			if(paraMap.get("role"+processStatus)!=null&&userObj.getRoleids().contains(paraMap.get("role"+processStatus))){
				mailSet.add(userObj.getEmail());
			}
			
			if(nowStatus>=PmClosedLoopConstant.CL_EVALU_TYPE_CB&&userObj.getRoleids().contains(";"+MessageUtil.ROLE_ENGINEEMANAGER+";")){
				mailSet.add(userObj.getEmail());
			}
			
			if(userObj.getUsername().equals(project.getProgramManagerCode())){
				mailTos += ";"+ userObj.getEmail();
			}
			if(userObj.getUsername().equals(project.getProgramManagerCodeB())){
				mailTos += ";"+ userObj.getEmail();
			}
			if(userObj.getUsername().equals(project.getServiceManagerCode())){
				// 闭环申请
				if(nowStatus == PmClosedLoopConstant.CL_EVALU_TYPE_PM){
					mailTos += ";"+userObj.getEmail();
					continue;
				}
				mailSet.add(userObj.getEmail());
				
			}
			if(userObj.getUsername().equals(nowUser)){
				mailSet.add(userObj.getEmail());
			}	
		}
		if(mailSet.size()<1)throw new RuntimeException(nowStatus+":没有获取到邮件发送人地址");
		
		if(nowStatus==PmClosedLoopConstant.CL_EVALU_TYPE_CL){
			List<ProjectMember>projectMemberList = projectService.queryProjectMembers(project.getProjectId());	//获取所有项目组成员邮箱
			for (ProjectMember memberObj : projectMemberList) {
				if(memberObj.getEmail()!=null&&!(memberObj.getEmail().equals(""))){
					if(memberObj.getEmail().endsWith(PmClosedLoopConstant.DP_MAIL)){
						mailSet.add(memberObj.getEmail());
					}
					
				}
			}
			String cbUserMails = projectService.getMails(MessageUtil.ROLE_CALLBACKPER);
			if(StringUtils.isNotBlank(cbUserMails)){
				mailSet.add(cbUserMails);
			}
		}
		
		String mailCcs=(mailSet.toString().substring(1,mailSet.toString().length()-1)).replaceAll(",", ";");
		BasicDataService basicDataService = (BasicDataService) SpringContext.getBean("basicDataService");
		String arg = basicDataService.querySysArg("sys.envirment.argu");
		if(arg.equals("0")){
			mailCcs = StringEscUtil.getText("plat.develop.mail.tos");
		}
		if(paraMap.get(""+nowStatus+processStatus+pmObj.getEvaluationResult())!=null)
			getMail(paraMap.get(""+nowStatus+processStatus+pmObj.getEvaluationResult()),defaultParaMap(project, pmObj),mailTos,mailCcs);
	}

	private void getMail(String tempCode,Map<String, String>paraMap,String mailTos,String mailCcs){
		NotificationTemplate mailTemplate=projectService.queryNotificationTemplate(tempCode);
		
		String mailSubject=mailTemplate.getNotificationSubject();
		String mailContent=mailTemplate.getNotificationContent();
		
		for (String key : paraMap.keySet()) {
			String value=paraMap.get(key);
			if(value==null){
				value="null";
			}
			mailContent=mailContent.replace(key,value);
		}
		sendMailService.keepMailInfo(PmClosedLoopUtil.pmCLSendMail(mailSubject, mailContent, mailTos, mailCcs));
	}
	
	private Map<String, String>defaultParaMap(Project project,PmClEvaluationHeader pmClEvaluationHeaderObj){
		Map<String, String>paraMap=new HashMap<String, String>();
		paraMap.put("$username$", project.getProgramManagerCodeforjson());
		paraMap.put("$approvedName$", project.getProgramManagerCodeforjson());
		paraMap.put("$projectName$", project.getProjectName()); 
		paraMap.put("$officeName$", project.getOfficeName());
		paraMap.put("$servicename$", project.getServiceManagerCodeforjson());
		paraMap.put("$evaluationName$", pmClEvaluationHeaderObj.getEvaluationPeopleId() + "-" + pmClEvaluationHeaderObj.getEvaluationPeopleName());
		if(pmClEvaluationHeaderObj.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_REJECT&&pmClEvaluationHeaderObj.getEvaluationComment()!=null&&!(pmClEvaluationHeaderObj.getEvaluationComment().equals(""))){
			paraMap.put("$evaluationComment$", "，驳回原因为"+pmClEvaluationHeaderObj.getEvaluationComment());
		}else if(pmClEvaluationHeaderObj.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_CANTCB&&pmClEvaluationHeaderObj.getEvaluationComment()!=null&&!(pmClEvaluationHeaderObj.getEvaluationComment().equals(""))){
			paraMap.put("$evaluationComment$", "，无法回访原因为"+pmClEvaluationHeaderObj.getEvaluationComment());
		}else {
			paraMap.put("$evaluationComment$", "");
		}
		return paraMap;
	}
	
	private boolean isPassCb(Project project){
		//是否已通过回访
		PmClEvaluationHeader pmClEvaluationHeaderObj=new PmClEvaluationHeader();
		pmClEvaluationHeaderObj.setEvaluationType(PmClosedLoopConstant.CL_EVALU_TYPE_CB);
		pmClEvaluationHeaderObj.setProjectCode(project.getProjectCode());
		List<PmClEvaluationHeader>pmClEvaluationHeaderList=queryPmEvaluationHeaderList(pmClEvaluationHeaderObj);
		if(pmClEvaluationHeaderList!=null&&pmClEvaluationHeaderList.size()>0){
			for (PmClEvaluationHeader obj2 : pmClEvaluationHeaderList) {
				if(obj2.getEvaluationResult()==PmClosedLoopConstant.CL_EVALU_RESULT_AGREE){
					return true;
				}				
				/*PmClQuesnaireResultHeader rHeaderObj=new PmClQuesnaireResultHeader();
				rHeaderObj.setEvaluationHeaderId(obj2.getId());
				List<PmClQuesnaireResultHeader>rHeaderList=queryPmClQuesResultHeaderList(rHeaderObj);
				if(rHeaderList!=null&&rHeaderList.size()>0){
					for (PmClQuesnaireResultHeader rHeaderObj2 : rHeaderList) {
						if (rHeaderObj2.getQuesMarkResult()==PmClosedLoopConstant.CL_EVALU_RESULT_AGREE) {
							return true;
						}
					}
				}*/
			}
		}
		return false;
	}
	
	@Override
	public void deletePmClEvaRecur(PmClEvaluationHeader pmObj){
		pmClosedLoopDao.deleteEvaluationHeader(pmObj);
		
		PmClQuesnaireResultHeader quesResHeader=new PmClQuesnaireResultHeader();
		quesResHeader.setEvaluationHeaderId(pmObj.getId());
		List<PmClQuesnaireResultHeader>quesResHeaderList=pmClosedLoopDao.queryPmClQuesResultHeaderList(quesResHeader);
		if(quesResHeaderList==null||quesResHeaderList.size()<1)throw new RuntimeException("删除问卷结果头信息出错");
		pmClosedLoopDao.deletePmClQuesResultHeader(quesResHeader);
		
		PmClQuesnaireResultLine quesResLine=new PmClQuesnaireResultLine();
		quesResLine.setQuesnaireResultHeaderId(quesResHeaderList.get(0).getId());
		pmClosedLoopDao.deletePmClQuesResultLine(quesResLine);
	}
	
	
	@Override
	public List<PmClEvaluationHeader> queryPmEvaluationHeaderList(
			PmClEvaluationHeader pmClEvaluationHeader) {
		return pmClosedLoopDao.queryEvaluationHeaderList(pmClEvaluationHeader);
	}
	
	@Override
	public Map<String, Integer> queryEvaluationHeaderMap(
			PmClEvaluationHeader pmClEvaluationHeader) {
		return pmClosedLoopDao.queryEvaluationHeaderMap(pmClEvaluationHeader);
	}

	@Override
	public List<PmClQuesnaireResultHeader> queryPmClQuesResultHeaderList(
			PmClQuesnaireResultHeader pmClQuesnaireResultHeader) {
		return pmClosedLoopDao.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader);
		
	}

	@Override
	public List<PmClQuesnaireResultLine> queryPmClQuesResultLineList(
			PmClQuesnaireResultLine pmClQuesnaireResultLine) {
		return pmClosedLoopDao.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
	}
	
	public WorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(WorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public PmClosedLoopDao getPmClosedLoopDao() {
		return pmClosedLoopDao;
	}

	public void setPmClosedLoopDao(PmClosedLoopDao pmClosedLoopDao) {
		this.pmClosedLoopDao = pmClosedLoopDao;
	}

	public SendMailService getSendMailService() {
		return sendMailService;
	}

	public void setSendMailService(SendMailService sendMailService) {
		this.sendMailService = sendMailService;
	}

	public UserManageService getUserManageService() {
		return userManageService;
	}

	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}

	public ProjectService getProjectService() {
		return projectService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	@Override
	public Map<String, PmClEvaluationHeader> queryEvaluationHeaderObjMap(
			PmClEvaluationHeader pmClEvaluationHeader, String sqlText) {
		return pmClosedLoopDao.queryEvaluationHeaderObjMap(pmClEvaluationHeader, sqlText);
	}

	@Override
	public void updateEvaluationHeaderNextAcceptPerson(HashMap<String, String> params) {
		pmClosedLoopDao.updateEvaluationHeaderNextAcceptPerson(params);
	}

	/**
	 * 更新项目闭环流程状态
     * @param projectId
     * @param processStatus
     */
    private void updateProjectCloseProcessState(Project project, int processStatus) {
        if (project == null || project.getProjectId() <= 0) {
            return;
        }
        // 更新项目闭环流程状态
        String closeProcessState = String.valueOf(Math.abs(processStatus) * 10);
        if (MessageUtil.PROJECT_CLOSE_PROCESS_STATE_10.equals(closeProcessState)) {
            int canCloseLoop = projectService.canCloseLoop(project);
            if (canCloseLoop == 1) {
                closeProcessState = MessageUtil.PROJECT_CLOSE_PROCESS_STATE_15;
            }
        }
        // 项目闭环状态更新项目跟踪、或闭环申请时，判断是否有进行中的回访流程，如果有改为回访
        if (MessageUtil.PROJECT_CLOSE_PROCESS_STATE_15.compareTo(closeProcessState) >= 0) {
            int callbackCount = projectService.queryCallBackingSize(project.getProjectId());
            if (callbackCount > 0) {
                closeProcessState = MessageUtil.PROJECT_CLOSE_PROCESS_STATE_30;
            }
        }
        Project temp = new Project(project.getProjectId());
        temp.setCloseProcessState(closeProcessState);
        projectService.insertOrUpdateProjectState(temp);
    }
}
