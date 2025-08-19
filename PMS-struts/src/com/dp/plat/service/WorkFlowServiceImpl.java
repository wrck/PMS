package com.dp.plat.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.UserContext;
import com.dp.plat.dao.WorkflowDao;
import com.dp.plat.data.activity.ActComment;
import com.dp.plat.data.activity.Procdef;
import com.dp.plat.data.bean.DpActProcDesc;
import com.dp.plat.data.bean.DpActProcType;
import com.dp.plat.data.bean.DpComment;
import com.dp.plat.data.bean.ProcdefDelegate;
import com.dp.plat.data.bean.SelfComment;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.param.DisplayParam;

/**
 * 工作流引擎--服务
 * 
 * @author xumaocai
 * 
 */
public class WorkFlowServiceImpl implements WorkFlowService {
	/** 仓库 **/
	private RepositoryService repositoryService;
	/** 运行中 **/
	private RuntimeService runtimeService;
	/** 任务 **/
	private TaskService taskService;
	/** 表单 **/
	private FormService formService;
	/** 历史 **/
	private HistoryService historyService;
	/**用户**/
	private UserManageService userManageService;
	/**自定义查询*/
	private WorkflowDao workflowDao;
	
	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}
	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}
	public void setWorkflowDao(WorkflowDao workflowDao) {
		this.workflowDao = workflowDao;
	}
	@Override
	public void deployFlow(String fileName, File file) {
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
			repositoryService.createDeployment().addZipInputStream(zis) // 设置部署文件
					.name(fileName) // 设置流程显示名
					.deploy(); // 发布流程

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("file not found");
		}

	}

	@Override
	public List<Deployment> listDeployments() {
		return repositoryService.createDeploymentQuery().list();
	}

	@Override
	public List<ProcessDefinition> listProcessDefinition() {
		return repositoryService.createProcessDefinitionQuery().list();
	}

	@Override
	public void delDeployment(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, false);
	}

	@Override
	public InputStream getInputStream(String deploymentId, String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	@Override
	public ProcessInstance startProcess(String processDefinitionKey,
			String businessKey, Map<String, Object> vars) {
		try {
			String username = UserContext.getUserContext().getUser().getUsername();
			Authentication.setAuthenticatedUserId(username);
		} catch (Exception e) {
		}
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				processDefinitionKey, businessKey, vars);
		Authentication.setAuthenticatedUserId(null);
		return pi;
	}

	@Override
	public List<Task> findPersonalTask(String userId) {
		return taskService.createTaskQuery().taskAssignee(userId)
				.orderByTaskCreateTime().desc().list();
	}

	@Override
	public List<Task> findPersonalTask(String userId, String procInstId) {
		return taskService.createTaskQuery().taskAssignee(userId).processInstanceId(procInstId)
				.orderByTaskCreateTime().desc().list();
	}
	
	@Override
	public TaskFormData getTaskFromData(String taskId) {
		return formService.getTaskFormData(taskId);
	}

	@Override
	public String getBusinessObjId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		String businessKey = pi.getBusinessKey();
		String objId = null;
		if (StringUtils.isNotBlank(businessKey)) {
			objId = businessKey.split("\\.")[1];
		}
		return objId;
	}

	@Override
	public void submitTask(WorkflowCommonParam param) {
		String taskId = param.getTaskId();
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("outcome", param.getOutcome());
		vars.put("issamecustomer", param.getIssamecustomer());
		vars.put("needleader", param.getNeedleader());
		String username = UserContext.getUserContext().getUser().getUsername();
		Authentication.setAuthenticatedUserId(username);

		
		Task task = taskService.createTaskQuery().taskId(taskId)
				.singleResult();
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId())
				.singleResult();
		String businessKey = pi.getBusinessKey();
		String[] strs = businessKey.split("\\.");
		String classType = strs[0];
		String objId = strs[1];
		int id = Integer.parseInt(objId);
		if (param.getOutcome() != null && !"1".equals(param.getOutcome())&& !"10".equals(param.getOutcome())) {
			if (classType.startsWith("ProjectProductLendInfo")) {
//				ProjectProductLendInfo info = new ProjectProductLendInfo();
//				info.setId(id);
//				info.setApproveStatus(-1);
//				projectService.updateProjectProductLendInfoApproveStatus(info);
			}else if(classType.equals("PlVerifiApply")){
//				PlVerifiApply pva = new PlVerifiApply();
//				pva.setId(id);
//				pva.setApplystat(-1);
//				projectService.updatePvaApplystat(pva);
			}else if(classType.equals("PlToProject")){
//				PlToProject ptp = new PlToProject();
//				ptp.setId(Integer.parseInt(objId));
//				ptp.setApplystat(-1);
//				projectService.updatePtpApplystat(ptp);
			}else if(classType.equals("ProjectProductLendTurnDutys")){
//				ProjectProductLendTurnDutys ppltd = new ProjectProductLendTurnDutys();
//				ppltd.setId(id);
//				ppltd.setStatus(2);
//				ppltd.setIsagree(-1);
//				ppltd.setRemark(param.getComment());
//				projectService.updateLendTurnDutysStatus(ppltd,0);
			}else if(classType.equals("BusinessBeforeApply")){
//				BusinessBeforeApply bba = new BusinessBeforeApply();
//				BusinessBeforeApply businessBeforeApply = projectService.findbusinessbeforeapplybyid(id);
//				bba.setId(id);
//				bba.setApproveStatus(-1);
//				projectService.updateBusinessBeforeApplyStatus(bba);
//				Project project = new Project();
//				project.setProjectCode(businessBeforeApply.getProjectCode());
//				project.setBpoStatus("13");
//				projectService.updateProjectBpoStatus(project);
			}else if(classType.equals("MajorProject")){
//				MajorProject mp = new MajorProject();
//				mp.setProjectId(id);
//				mp.setMajorId(id);
//				mp.setIsapply(-1);
//				projectService.updateMajorProjectIsApply(mp);
//				projectService.updateMajorproject(mp);
//				mp = projectService.queryMajorProject(id);
//				Project project  = new Project();
//				project.setIsapply(-1);
//				project.setProjectCode(mp.getProjectCode());
//				projectService.updateProjectIsapplyStatus(project);
			}else if(classType.equals("ProjectInvalid")){
//				ProjectInvalid pd = new ProjectInvalid();
//				pd.setId(id);
//				pd.setApproveStatus(-1);//被驳回
//				projectService.updateprojectInvalidStatus(pd);
			}else if(classType.startsWith("BusinessApply")){
//				BusinessApply ba = new BusinessApply();
//				BusinessApply businessApply = projectService.findbusinessapplybyid(id);
//				ba.setId(id);
//				ba.setApproveStatus(-1);
//				projectService.updateBusinessApplyStatus(ba);
//				
//				Project project = new Project();
//				project.setProjectCode(businessApply.getProjectCode());
//				project.setBpoStatus("23");
//				projectService.updateProjectBpoStatus(project);
			}else if(classType.startsWith("BusinessOrder")){
//				BusinessOrder bo = new BusinessOrder();
//				bo.setId(id);
//				bo = projectService.querybusinessorderbyid(id);
//				bo.setApproveStatus(-1);
//				projectService.updateBusinessOrderApproveStatus(bo);
//				
//				Project project = new Project();
//				project.setProjectCode(bo.getProjectCode());
//				project.setBpoStatus("33");
//				projectService.updateProjectBpoStatus(project);
			}
			// else if do other classType
		}else if("1".equals(param.getOutcome())||"10".equals(param.getOutcome())){
			if(classType.equals("PlVerifiApply")){
//				PlVerifiApply pva = new PlVerifiApply();
//				pva.setId(id);
//				pva.setApplystat(1);
//				projectService.updatePvaApplystat(pva);
			}else if(classType.equals("PlToProject")){
//				PlToProject ptp = new PlToProject();
//				ptp.setId(id);
//				ptp.setApplystat(1);
//				projectService.updatePtpApplystat(ptp);
			}else if(classType.equals("ProjectProductLendTurnDutys")){
//				ProjectProductLendTurnDutys ppltd = new ProjectProductLendTurnDutys();
//				ppltd.setId(id);
//				ppltd.setStatus(2);
//				ppltd.setIsagree(1);
//				ppltd.setRemark(param.getComment());
//				projectService.updateLendTurnDutysStatus(ppltd,1);
			}
		}
		
				
		//将审批意见插入借货审批意见表
		DpComment dc = new DpComment();
		dc.setTaskId(taskId);
		dc.setIsagree(Integer.parseInt(param.getOutcome()));
		dc.setUserId(username);
		if(classType.startsWith("BusinessApplyBid")&&UserContext.getUserContext().isHasRole(33)){
			dc.setMessage((param.getIsBusinessBeforeFit() ==1 ? "与商务提前报备一致":"与商务提前报备不一致")
					+(param.getComment().equals("")||param.getComment()==null ? "":"&nbsp;&nbsp;&nbsp;&nbsp;审批意见："+param.getComment()));
		}else{
			dc.setMessage(param.getComment());
		}
		dc.setObjId(id);
		int type = 0;
		if (classType.startsWith("ProjectProductLendInfo")) {
//			type = BusinessType.PROJECT_PRODUCT_LEND_INFO;
			vars.put("marketPower",param.getMarketPower());
		}else if(classType.equals("PlVerifiApply")){
//			type = BusinessType.PL_VERIFIAPPLY;
		}else if(classType.equals("PlToProject")){
//			type = BusinessType.PL_TO_PROJECT;
		}else if(classType.equals("ProjectProductLendTurnDutys")){
//			type = BusinessType.PROJECT_PRODUCT_LEND_TURN_DUTYS;
		}else if(classType.equals("BusinessBeforeApply")){
//			type = BusinessType.BUSINESS_BEFORE_APPLY;
		}else if(classType.equals("ProjectInvalid")){
//			type = BusinessType.PROJECT_INVALID_APPLY;
		}else if(classType.equals("MajorProject")){
//			type = BusinessType.MAJOR_PROJECT;
		}else if(classType.startsWith("BusinessApplyBid")){
//			type = BusinessType.BUSINESS_APPLY_BID;
			vars.put("isBusinessBeforeFit", param.getIsBusinessBeforeFit());//是否与商务报备一致
		}else if(classType.startsWith("BusinessApplyLost")){
//			type = BusinessType.BUSINESS_APPLY_LOST;
		}else if(classType.startsWith("BusinessOrder")){
//			type = BusinessType.BUSINESS_ORDER;
		}
		//任务委派设置
		if(!task.getAssignee().equals(username)){
//			ProcessDefinition def = this.getProcessDefinitionByTaskId(taskId);
//			ProcdefDelegate pd = new ProcdefDelegate();
//			pd.setOwner(task.getAssignee());
//			pd.setAssignee(username);
//			pd.setProcdefId(def.getKey());
//			ProcdefDelegate pdBean = projectService.findProcdefDelegateCause(pd);
			
//			taskService.delegateTask(taskId, username);
//			dc.setOwner(task.getAssignee());
//			if(pdBean != null){
//				dc.setCause(pdBean.getCause());
//			}
		}
		
		dc.setType(type);
		
		
		
		
		/*taskService.addComment(taskId, null, param.getComment());
		taskService.complete(taskId, vars);*/
		boolean condition = false;
		do {
			condition = false;
//			projectService.insertintoallapprove(dc);
			taskService.addComment(taskId, null, param.getComment());
			taskService.setVariablesLocal(taskId, vars);
			taskService.complete(taskId, vars);
			if (param.getOutcome() != null && !"1".equals(param.getOutcome())&& !"10".equals(param.getOutcome())) {
				//不同意
			}else {
				//当前任务和下一任务是同一办理人情况的处理方式。
				List<Task> nextTaskList = this.getTaskByInstId(pi.getId());
				for (Task t : nextTaskList) {
					if(username.equals(t.getAssignee())){
						String msg = "与上环节办理人相同，系统默认办理";
						String nextTaskId = t.getId();
						param.setComment(msg);
						vars.put("outcome", "1");
						dc.setMessage(msg);
						taskId = nextTaskId;
						dc.setTaskId(nextTaskId);
						condition = true;
						break;
					}
				}
			}
		} while (condition);
		
		//�����������ʶ
//		projectService.updateActRuTask(pi.getId(),username);
		/**
		 * 发送邮件提醒
		 */
//		int mailNum = 0;//根据num标识对发送邮件提醒选择对应的模版
		
		List<Task> taskList = this.getTaskByInstId(pi.getId());
		String usernames = "";
		for (Task t : taskList) {
			usernames = usernames + "'" + t.getAssignee() + "'" + ",";
		}
		if(!usernames.equals("")){
//			mailNum = 1;
			usernames = usernames.substring(0,usernames.length() -1);
		}else {//流程结束
			String aplyMen = "";//申请人用户名
//			if(classType.equals("LendTurnDuty")){
//				LendTurnDuty ltd = projectService.queryLendTurnDuty(id);
//				aplyMen = ltd.getTurnMen();
//			}else if (classType.startsWith("ProjectProductLendInfo")) {
//				ProjectProductLendInfo ppli = projectService.findProjectProductLendInfo(id);
//				aplyMen = ppli.getApplyName();
//			}else if(classType.equals("PlVerifiApply")){
//				PlVerifiApply pva = new PlVerifiApply();
//				pva.setId(id);
//				pva = projectService.queryplverifiapplybyid(pva);
//				aplyMen = pva.getApplyMan();
//			}else if(classType.equals("PlToProject")){
//				PlToProject ptp = new PlToProject();
//				ptp.setId(id);
//				projectService.queryPlToProjectById(ptp);
//				aplyMen = ptp.getApplyMan();
//			}
//			mailNum = 2;
			usernames = "'"+aplyMen+"'";
		}
		if (param.getOutcome() != null && !param.getOutcome().equals("1")&& !param.getOutcome().equals("10")) {//驳回
//			mailNum = 3;
		}
//		List<User> userList = userManageService.queryUsersByUsername(usernames);
//		MailUtil.sendmail(userList, "【SMS系统待办事项提醒】", task.getDescription(),mailNum);
	}

	@Override
	public void submitTaskNoComment(WorkflowCommonParam param,Map<String, Object> vars) {
		String taskId = param.getTaskId();
		String username = UserContext.getUserContext().getUser().getUsername();
		Authentication.setAuthenticatedUserId(username);
		taskService.complete(taskId, vars);
	}

	@Override
	public void submitSelfTask(WorkflowCommonParam param, Map<String, Object> vars) {
		String taskId = param.getTaskId();
		String username = UserContext.getUserContext().getUser().getUsername();
		Authentication.setAuthenticatedUserId(username);
		
	/*	DpComment dc = new DpComment();
		dc.setTaskId(taskId);
		dc.setIsagree(Integer.parseInt(param.getOutcome()));
		dc.setUserId(username);
		dc.setMessage(param.getComment());
		dc.setObjId(param.getObjId());
		dc.setType(param.getType());*/
		taskService.addComment(taskId, null, param.getComment());
		taskService.setVariablesLocal(taskId, vars);
		taskService.complete(taskId, vars);
	}
	@Override
	public List<SelfComment> getProcessComments(String taskId,String instId) {
		List<SelfComment> historyCommnets = new ArrayList<SelfComment>();
		//List<Comment> historyCommnets = new ArrayList<Comment>();
		// 1) 获取流程实例
		List<HistoricActivityInstance> hai;
		if(taskId != null){
			Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
			// ProcessInstance pi = (ProcessInstance)
			// runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
			// 2）通过流程实例查询所有的(用户任务类型)历史活动
			hai = historyService
					.createHistoricActivityInstanceQuery()
					.processInstanceId(task.getProcessInstanceId())
					.activityType("userTask").list();
		}else {
			hai = historyService
					.createHistoricActivityInstanceQuery()
					.processInstanceId(instId)
					.activityType("userTask").list();
		}
		
		// 3）查询每个历史任务的批注
		for (HistoricActivityInstance h : hai) {
			String historytaskId = h.getTaskId();
			List<Comment> comments = taskService.getTaskComments(historytaskId);
//			DpComment aa = projectService.queryallapprovebyhistaskid(historytaskId);
			// 4）如果当前任务有批注信息，添加到集合中
			if (comments != null && comments.size() > 0) {
				SelfComment sc = new SelfComment();
				for (Comment comment : comments) {
					User utemp = userManageService.queryUserByUserName(comment.getUserId());
					sc.setId(comment.getId());
					sc.setUserId(comment.getUserId());
					if(utemp != null){
						sc.setRealName(utemp.getRealName());
					}
					sc.setTaskId(comment.getTaskId());
					sc.setProcessInstanceId(comment.getProcessInstanceId());
					sc.setTime(comment.getTime());
					sc.setFullMessage(comment.getFullMessage());
//					if(aa != null){
//						sc.setIsagree(aa.getIsagree()+"");
//					}
				}
				historyCommnets.add(sc);
				//historyCommnets.addAll(comments);
			}
		}

		return historyCommnets;
	}

	@Override
	public ProcessDefinition getProcessDefinitionByTaskId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		ProcessDefinition pd = repositoryService.getProcessDefinition(task
				.getProcessDefinitionId());
		return pd;
	}

	@Override
	public Map<String, Object> getCurrentActivityCoordinates(String taskId) {
		Map<String, Object> coordinates = new HashMap<String, Object>();
		TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		ExecutionEntity pi = (ExecutionEntity) runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		
		String currentActivitiId = pi.getActivityId() != null ? pi.getActivityId() : task.getTaskDefinitionKey();
		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(task.getProcessDefinitionId());

		ActivityImpl activity = pde.findActivity(currentActivitiId);
		// 4. 获取活动的坐标
		coordinates.put("x", activity.getX());
		coordinates.put("y", activity.getY());
		coordinates.put("width", activity.getWidth());
		coordinates.put("height", activity.getHeight());
		return coordinates;
	}

	@Override
	public Task getTaskIdByProcessInstanceId(String piid, String assignee) {
		List<Task> task = taskService.createTaskQuery().processInstanceId(piid)
				.taskAssignee(assignee).list();
		if(task!= null && task.size() >0){
			return task.get(0);
		}
		return null;
	}

	@Override
	public boolean isExistNextNode(String taskId, String nodeName) {
		boolean result = false;
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
		String currentActivitiId = pi.getActivityId();
		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());

		ActivityImpl ai = pde.findActivity(currentActivitiId);
		List<PvmTransition> outTransitions = ai.getOutgoingTransitions();
		for (PvmTransition tr : outTransitions) {
			PvmActivity ac = tr.getDestination(); // 获取线路的终点节点
			String nowNodeName = (String) ac.getProperty("name");
			System.out.println("下一步任务任务：" + ac.getProperty("name"));
			if(nowNodeName != null && nowNodeName.equals(nodeName)){
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public List<Task> findAllRunTask() {
		return taskService.createTaskQuery().orderByTaskCreateTime().desc().list();
	}

	@Override
	public List<HistoricProcessInstance> findHisProcess() {
		return historyService.createHistoricProcessInstanceQuery().list();
	}

	@Override
	public List<Task> getTaskByInstId(String procInstId) {
		List<Task> list = taskService.createTaskQuery().processInstanceId(procInstId).list();
		return list;
	}

	@Override
	public void submitTaskSystemAuto(Task task) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("outcome", 1);
		Authentication.setAuthenticatedUserId(task.getAssignee());
		taskService.complete(task.getId(), vars);
	}
	
	@Override
	public void doSelfTask(Task task, String instId,String comment,  Map<String, Object> vars) {
		Authentication.setAuthenticatedUserId(task.getAssignee());
		taskService.addComment(task.getId() , instId , comment);
		taskService.setVariablesLocal(task.getId(), vars);
		taskService.complete(task.getId(),vars);
	}
	
	@Override
	public List<HistoricTaskInstance> findHistoricPersonalTask(String userId) {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().finished().taskAssignee(userId).orderByHistoricTaskInstanceEndTime().desc().list();
		return list;
	}

	@Override
	public String getHistBusinessObjId(String instId) {
		HistoricProcessInstanceEntity pi = (HistoricProcessInstanceEntity) historyService.createHistoricProcessInstanceQuery().processInstanceId(instId).singleResult();
		String businessKey = pi.getBusinessKey();
		String objId = null;
		if (StringUtils.isNotBlank(businessKey)) {
			objId = businessKey.split("\\.")[1];
		}
		return objId;
	}

	@Override
	public String getFormKey(String instId) {
		HistoricProcessInstanceEntity pi = (HistoricProcessInstanceEntity) historyService.createHistoricProcessInstanceQuery().processInstanceId(instId).singleResult();
		HistoricTaskInstanceEntity htiqi = (HistoricTaskInstanceEntity) historyService.createHistoricTaskInstanceQuery().processDefinitionId(pi.getProcessDefinitionId()).list().get(0);
		return htiqi.getFormKey();
	}

	@Override
	public ProcessDefinition getProcessDefinitionByClassType(String simpleName) {
		return repositoryService.createProcessDefinitionQuery().processDefinitionKey(simpleName).orderByProcessDefinitionVersion().desc().list().get(0);
	}

	@Override
	public List<Task> queryCurrentApprover(String instId) {
		return taskService.createTaskQuery().processInstanceId(instId).list();
	}

	@Override
	public int querymaxdeploymentidByBean(String string) {
		return Integer.parseInt(repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey(string).singleResult().getDeploymentId());
	}

	@Override
	public List<DpActProcDesc> findRunSelfTaskList(DisplayParam displayParam,
			DpActProcDesc dpActProcDesc) {
		
		//List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("users").list();
		//System.out.println(tasks);
//		return projectService.findRuTaskProcDescList(displayParam, dpActProcDesc);
		return null;
	}

	@Override
	public List<DpActProcType> findDpActProcTypeList() {
//		return projectService.findDpActProcTypeList();
		return null;
	}

	@Override
	public List<DpActProcDesc> findHisSelfTaskList(DisplayParam displayParam,
			DpActProcDesc dpActProcDesc) {
//		return projectService.findHisSelfTaskList(displayParam, dpActProcDesc);
		return null;
	}

	@Override
	public void insertProcdefDelegate(ProcdefDelegate procdefDelegate) {
//		projectService.insertProcdefDelegate(procdefDelegate);
	}

	@Override
	public List<ProcdefDelegate> findProcdefDelegateList(
			ProcdefDelegate procdefDelegate) {
//		return projectService.findProcdefDelegateList(procdefDelegate);
		return null;
	}

	@Override
	public ProcdefDelegate findProcdefDelegateById(int id) {
//		return projectService.findProcdefDelegateById(id);
		return null;
	}

	@Override
	public void updateProcdefDelegate(ProcdefDelegate procdefDelegate) {
//		projectService.updateProcdefDelegate(procdefDelegate);
	}

	@Override
	public Map<String, Integer> getWorkFlowCountMap(
			Map<String, Integer> countMap, List<DpActProcDesc> dapdlist,
			DpActProcDesc dpActProcDesc, DisplayParam displayParam) throws Exception{
		User user = UserContext.getUserContext().getUser();
		if(dpActProcDesc == null){
			dpActProcDesc = new DpActProcDesc();
		}
		dpActProcDesc.setAssignee(user.getUsername());
		displayParam.getParam();
		dapdlist = findRunSelfTaskList(displayParam, dpActProcDesc);
		Iterator<DpActProcDesc> it = dapdlist.iterator();
		while(it.hasNext()){
			DpActProcDesc desc = it.next();
			if(desc.getProcType() != 5 && desc.getProcType() != 7 && desc.getProcType() != 8){
				it.remove();
			}
		}
		countMap.put("dapdlistCount", dapdlist.size());//工作流审批
		return countMap;
	}

	@Override
	public List<DpActProcDesc> getRunTask(DpActProcDesc dpActProcDesc,
			DisplayParam displayParam) {
		User user = UserContext.getUserContext().getUser();
		dpActProcDesc.setAssignee(user.getUsername());
		try {
			displayParam.getParam();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return findRunSelfTaskList(displayParam, dpActProcDesc);
	}

	@Override
	public List<DpActProcDesc> getRunVariable(DpActProcDesc dpActProcDesc,
			DisplayParam displayParam) {
		User user = UserContext.getUserContext().getUser();
		dpActProcDesc.setAssignee(user.getUsername());
		try {
			displayParam.getParam();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

//		List<Integer> canseeList = userManageService.queryCanSeeVariable1();
//		if(canseeList.contains(user.getRole_id())){
//			return findRunVariableList(displayParam, dpActProcDesc);
//		}
		return new ArrayList<DpActProcDesc>();
	}
	
	@Override
	public List<DpActProcDesc> findRunVariableList(DisplayParam displayParam,
			DpActProcDesc dpActProcDesc) {
//		return projectService.findRuVariableDescList(displayParam, dpActProcDesc);
		return null;
	}
	
	@Override
	public Task queryTaskByBussinessKey(String businessKey) {
		return taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
	}
	@Override
	public void querymaxDefinitionObjByKey(String keyString,WorkflowCommonParam workflowCommonParam) {
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey(keyString).singleResult();
		if(processDefinition!=null){
			workflowCommonParam.setDeploymentId(processDefinition.getDeploymentId());
			workflowCommonParam.setImageName(processDefinition.getDiagramResourceName());
		}
		
	}
	@Override
	public Map<String, Object>queryProcessVarMap(String taskId){
		return taskService.getVariables(taskId);
	}

	@Override
	public Task queryTaskByBussinessKeyUser(String businessKey, String userId) {
		return taskService.createTaskQuery().processInstanceBusinessKey(businessKey).taskAssignee(userId).singleResult();
	}
	
	@Override
	public Task queryPubTaskByBussinessKeyUser(String businessKey, String userId) {
		return taskService.createTaskQuery().processInstanceBusinessKey(businessKey).taskCandidateUser(userId).singleResult();
	}
	
	@Override
	public void claimTask(String taskId, String userId) {
		taskService.claim(taskId, userId);
	}
	
	@Override
	public void assigneeTask(String taskId, String userId, String variableName) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task != null) {
			String owner = task.getAssignee();
			task.setOwner(owner);
			task.setAssignee(userId);
			taskService.saveTask(task);
		} else {
			taskService.setOwner(taskId, userId);
			taskService.setAssignee(taskId, userId);
		}
		taskService.setVariable(taskId, variableName, userId);
	}
	
	@Override
	public void setVariable(String instId, String variableName, String oldValue, String newValue) {
//		String taskId = queryTaskByInstIdAndVariable(instId, variableName, oldValue);
//		if (StringUtils.isNotBlank(taskId)) {
//			taskService.updateRunVariableById(taskId, newValue);
//		}
		updateRunVariableByInstIdAndVariable(instId, variableName, oldValue, newValue);
	}
	
	@SuppressWarnings("unused")
	private String queryTaskByInstIdAndVariable(String instId, String variableName, Object oldValue) {
		return workflowDao.queryTaskByInstIdAndVariable(instId, variableName, oldValue);
	}
	
	private void updateRunVariableByInstIdAndVariable(String instId,  String variableName, String oldValue, String newValue) {
		workflowDao.updateRunVariableByInstIdAndVariable(instId, variableName, oldValue, newValue);
	}
	
	@SuppressWarnings("unused")
	private void updateRunVariableById(String id, String newValue) {
		workflowDao.updateRunVariableById(id, newValue);
	}
	
	public List<Task> queryAllSelfTaskList(String userId){
		return taskService.createTaskQuery().taskAssignee(userId).list();		
	}
	
	public List<Task> queryAllPubTaskList(String userId){
		return taskService.createTaskQuery().taskCandidateUser(userId).list();
	}
	
	@Override
	public List<HistoricProcessInstance> queryHisProcessInstanceByIds(Set<String> processIdSet) {
		return historyService.createHistoricProcessInstanceQuery().processInstanceIds(processIdSet).list();
	}

	@Override
	public Procdef getProcdef(Procdef procdef) {
		return workflowDao.queryProcdef(procdef);
	}
	
	@Override
	public Integer addSelfActComment(int objId, String procdefKey, String taskId, String instId, int result, String message,
			String nextAssignee, String nextAssigneeName) {
		return addSelfActComment(objId, procdefKey, "", taskId, instId, result, message, nextAssignee, nextAssigneeName);
	}
	
    @Override
    public Integer addSelfActComment(Integer objId, String procdefKey, String taskKey, String taskId, String instId, int result, String message) {
        return addSelfActComment(objId, procdefKey, taskKey, taskId, instId, result, message, null, null);
    }
	
	@Override
	public Integer addSelfActComment(Integer objId, String procdefKey, String taskKey, String taskId,
			String instId, int result, String message, String nextAssignee, String nextAssigneeName) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("objId", objId);
		params.put("procdefKey", procdefKey);
		params.put("taskKey", taskKey);
		params.put("taskId", taskId);
		params.put("instId", instId);
		params.put("result", result);
		params.put("message", message);
		params.put("nextAssignee", nextAssignee);
		params.put("nextAssigneeName", nextAssigneeName);
		workflowDao.insertActComment(params);
		return (Integer) params.get("id");
	}

	@Override
	public void addSelfActComment(int objId, String key, String taskId,
			String instId, int result, String message) {
		workflowDao.insertActComment(objId ,key ,taskId , instId ,result , message);
	}

	
	@Override
	public void updateSelfActComment(int commentId, String taskId, String instId) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("commentId", commentId);
		params.put("taskId", taskId);
		params.put("instId", instId);
		workflowDao.updateSelfActComment(params);
	}

	@Override
	public List<ActComment> queryActComment(int callBackId , String procdefKey) {
		return workflowDao.queryActComment(callBackId ,procdefKey);
	}

	@Override
	public void updateApplytableInfo(String tableName, String instId, int objId , String objColumn) {
		workflowDao.updateApplytableInfo(tableName,instId ,objId ,objColumn);
	}

	@Override
	public void deleteProcessInstance(String proInstId, String comment) {
		runtimeService.deleteProcessInstance(proInstId, comment);
	}

}
