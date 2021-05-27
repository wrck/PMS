package com.dp.plat.pms.springmvc.listener;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.task.Comment;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.pojo.NotifyTemplate;
import com.dp.plat.core.service.INotifyTemplateService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.vo.UserInfoVO;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.TaskType;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import com.dp.plat.pms.springmvc.entity.ProjectMember;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.service.IPmWorkFlowService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectMemberService;
import com.dp.plat.pms.springmvc.service.IProjectTaskService;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;
import com.dp.plat.pms.springmvc.vo.MemberVO;
import com.dp.plat.pms.springmvc.vo.TaskVO;
import com.dp.plat.support.mail.MailUtil;

/**
 * 质量审核跟踪流程任务监听器
 * 
 * @author w02611
 *
 */
@Component("qualityApproveTrackListener")
@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public class QualityApproveTrackListener {
	
	private final static String definedVariablesKey = "pm.workflow.qualityApproveTrack.defineVariable";
	private final static String defaultDefinedVariables = "{}";
	private final static String defaultTaskMailTemplateKey = "pm.workflow.qualityApproveTrack.mail";
	
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private IPmWorkFlowService pmFlowService;
	@Autowired
	private IProjectHeaderService projectHeaderService;
	@Autowired
	private IProjectTaskService projectTaskService;
	@Autowired
	private IProjectMemberService projectMemberService;
	@Autowired
	private ICommonRelatedDataService commonRelatedDataService;
	@Autowired
	private IIndustryAssetService industryAssetService;
	@Autowired
	private IIndustryLeakService industryLeakService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserInfoService userInfoService;
	@Autowired
	private INotifyTemplateService notifyTemplateService;
	
	/**
	 * 流程开始执行监听器
	 */
	public void processStartExecution(DelegateExecution delegateExecution) throws Exception {
		String executionId = delegateExecution.getId();
		String processKey = delegateExecution.getProcessDefinitionId();
		PmWorkFlow pmWorkFlow = delegateExecution.getVariable("entity", PmWorkFlow.class);
		
		String dataType = pmWorkFlow.getDataType();
		Map<String, Object> taskDefinedVariables = getTaskDefinedVariable(dataType);
		boolean needApprove = Boolean.TRUE.equals(taskDefinedVariables.getOrDefault("needApprove", false));
		runtimeService.setVariable(executionId, "needApprove", needApprove);
		
		String processName = (String) taskDefinedVariables.getOrDefault("title", "");
		if (StringUtils.isNotBlank(processName)) {
			processName += " -- ";
		}
		runtimeService.setVariable(executionId, "processName", processName);
		
		if (DataType.PROJECT_TASK.equals(dataType)) {
			TaskVO projectTask = new TaskVO();
			projectTask.setTaskId(pmWorkFlow.getDataId());
			projectTask.setCustomInfoByKey("currentProcInstId", delegateExecution.getProcessInstanceId());
			projectTaskService.updateByPrimaryKeySelective(projectTask);
		}
	}
	
	/**
		 * 绩效计划目标审批流程完成后执行结束监听器
		 */
		public void processEndExecution(DelegateExecution delegateExecution) throws Exception {
			String taskId = delegateExecution.getId();
			ExecutionEntity executionEntity = (ExecutionEntity) delegateExecution;
			String processKey = executionEntity.getProcessDefinitionKey();
			PmWorkFlow pmWorkFlow = delegateExecution.getVariable("entity", PmWorkFlow.class);
			Boolean isPass = Boolean.TRUE.equals(delegateExecution.getVariable("isPass"));
			String dataType = pmWorkFlow.getDataType();
			String workflowStatus = isPass ? BaseVO.APPROVAL_SUCCESS : BaseVO.APPROVAL_FAILED;
			String dataStatus = isPass ? TaskType.END : TaskType.REJECT;
			if (DataType.PROJECT_TASK.equals(dataType)) {
				TaskVO projectTask = new TaskVO();
				projectTask.setTaskId(pmWorkFlow.getDataId());
				projectTask.setStatus(dataStatus);
				projectTask.setCustomInfoByKey("currentTaskId", null);
				projectTask.setCustomInfoByKey("currentTaskKey", null);
				projectTask.setCustomInfoByKey("currentProcInstId", null);
				projectTaskService.updateByPrimaryKeySelective(projectTask);
			}  else if (DataType.INDUSTRY_ASSET.equals(dataType)) {
				// 项目资产，更新入库状态和入库时间
				IndustryAssetVO industryAsset = new IndustryAssetVO();
				industryAsset.setId(pmWorkFlow.getDataId());
				industryAsset.setStatus(dataStatus);
				industryAsset.setCustomInfoByKey("currentTaskId", null);
				industryAsset.setCustomInfoByKey("currentTaskKey", null);
				industryAsset.setCustomInfoByKey("currentProcInstId", null);
//					industryAsset.setCustomInfoByKey("trackedComments", taskComments);
				industryAssetService.updateByPrimaryKeySelective(industryAsset);
			} else if (DataType.INDUSTRY_LEAK.equals(dataType)) {
				// 行业漏洞，更新入库状态和入库时间
				IndustryLeakVO industryLeak = new IndustryLeakVO();
				industryLeak.setId(pmWorkFlow.getDataId());
				industryLeak.setStatus(dataStatus);
				industryLeak.setCustomInfoByKey("currentTaskId", null);
				industryLeak.setCustomInfoByKey("currentTaskKey", null);
				industryLeak.setCustomInfoByKey("currentProcInstId", null);
//					industryAsset.setCustomInfoByKey("trackedComments", taskComments);
				industryLeakService.updateByPrimaryKeySelective(industryLeak);
			}
			
			//			String businessKey = delegateExecution.getProcessBusinessKey();
//			pmWorkFlow = pmFlowService.selectByPrimaryKey(Integer.valueOf(businessKey));
			PmWorkFlow temp = new PmWorkFlow();
			temp.setId(pmWorkFlow.getId());
			temp.setStatus(workflowStatus);
			temp.setEndTime(new Date());
			pmFlowService.updateByPrimaryKeySelective(temp);
			
			pmWorkFlow.setStatus(workflowStatus);
			pmWorkFlow.setTaskKey(dataStatus);
			pmWorkFlow.setEndTime(new Date());
			runtimeService.setVariable(delegateExecution.getProcessInstanceId(), "entity", pmWorkFlow);
	//		
	//		PlanParticipant oldParticipant = (PlanParticipant) delegateExecution.getVariable("participant");
	//		// XXX 直接创建对象进行更新
	//		PlanParticipant planParticipant = new PlanParticipant();
	//		planParticipant.setId(oldParticipant.getId());
	//		// 更新考核阶段状态值
	//		ExecutionEntity executionEntity = (ExecutionEntity) delegateExecution;
	//		if (PerfConstant.PlanProcessKey.EVALUATE_OBJECTIVE_KEY.equals(executionEntity.getProcessDefinitionKey())) {
	//			planParticipant.setStatus(PlanParticipantStatus.END);
	//		} else {
	//			planParticipant.setStatus(PlanParticipantStatus.APPROVAL_FINISHED);
	//			//更新目标审批通过时间
	//			planParticipant.setApproveGoalTime(new Date());
	//			
	//			// 绩效考核中途补发的被挂起流程，进行激活
	//			List<Task> suspendedTaskList = taskService.createTaskQuery().taskAssignee(oldParticipant.getEmpID().toString()).suspended().list();
	//			for (Task task : suspendedTaskList) {
	//				runtimeService.activateProcessInstanceById(task.getProcessInstanceId());
	//			}
	//		}
	//		planParticipantService.updateByPrimaryKeySelective(planParticipant);
	//		
	//		// 查询是否所有流程都办理完成，如果办理完成将绩效步骤置为已完成
	//		PmWorkFlow temp = new PmWorkFlow();
	//		temp.setPlanId(pmWorkFlow.getPlanId());
	//		temp.setProcessKey(executionEntity.getProcessDefinitionKey());
	//		temp.setStatus(BaseVO.PENDING);
	//		long count = pmWorkFlowService.countBySelective(temp);
	//		if (count == 0) {
	//			List<PlanStep> planSteps = planStepService.selectByPlanIdLikeStepCode(pmWorkFlow.getPlanId(), executionEntity.getProcessDefinitionKey());
	//			for (PlanStep planStep : planSteps) {
	//				planStep.setStatus((short) 2);
	//				planStep.setUpdateBy(UserContext.getCurrentUser().getUserName());
	//				planStepService.updateByPrimaryKeySelective(planStep);
	//			}
	//			Plan plan = planService.selectByPrimaryKey(pmWorkFlow.getPlanId());
	//			plan.setStatus((short) (plan.getStatus() + 1));
	//			planService.updateByPrimaryKeySelective(plan);
	//		}
			return;
		}

	/**
	 * 任务创建监听器
	 */
	public void createTask(DelegateTask delegateTask) throws Exception {
		String procInstId = delegateTask.getProcessInstanceId();
		String taskId = delegateTask.getId();
		String taskKey = delegateTask.getTaskDefinitionKey();
		String processKey = delegateTask.getProcessDefinitionId();
		
		PmWorkFlow pmWorkFlow = delegateTask.getVariable("entity", PmWorkFlow.class);
		String objType = pmWorkFlow.getObjType();
		String dataType = pmWorkFlow.getDataType();
		Map<String, Object> taskDefinedVariables = getTaskDefinedVariable(dataType, taskKey);
		String assignee = null;
		Set<String> candidates = null;
		String candidateGroup = null;
		String memberRole = null;
		String candidateRole = null;
//		if (TaskType.AF_APPROVE_TASK.equals(taskKey)) {
//			memberRole = ProjectConstant.MemberRole.MEMBER_QC;
//			candidateRole = RoleConstant.ROLE_PM_AFQC;
//		} else if (TaskType.YF_APPROVE_TASK.equals(taskKey)) {
//			memberRole = ProjectConstant.MemberRole.MEMBER_QC;
//			candidateRole = RoleConstant.ROLE_PM_YFQC;
//		} else if (TaskType.TRACK_TASK.equals(taskKey)) {
//			candidateRole = (String) taskDefinedVariables.getOrDefault("trackRole", "");
//		}
		String permissionProjectTypes = "all";
		String areaPower = "all";
		boolean checkArea = Boolean.TRUE.equals(taskDefinedVariables.getOrDefault("checkArea", false));
		memberRole = (String) taskDefinedVariables.getOrDefault("memberRole", "");
		candidateRole = (String) taskDefinedVariables.getOrDefault("candidateRole", "");
		if (DataType.PROJECT.equals(objType)) {
			permissionProjectTypes = StringUtils.defaultIfBlank((String) pmWorkFlow.getCustomInfoByKey("projectTypes"), permissionProjectTypes);
		}
		if (TaskType.AF_APPROVE_TASK.equals(taskKey) || TaskType.YF_APPROVE_TASK.equals(taskKey)) {
			if (checkArea) {
				if (DataType.PROJECT_TASK.equals(dataType)) {
					ProjectTask entity = (ProjectTask) pmWorkFlow.getEntity();
					Map customInfo = (Map) entity.getCustomInfo();
					if (customInfo != null && customInfo.containsKey(pmWorkFlow.getObjType())) {
						Map project = (Map) customInfo.getOrDefault(pmWorkFlow.getObjType(), new HashMap<>(0));
						areaPower = (String) project.getOrDefault("column001", areaPower);
					}
				} else if (DataType.INDUSTRY_LEAK.equals(dataType)) {
					IndustryLeak entity = (IndustryLeak) pmWorkFlow.getEntity();
					Map customInfo = (Map) entity.getCustomInfo();
					if (customInfo != null && customInfo.containsKey(pmWorkFlow.getObjType())) {
						Map project = (Map) customInfo.getOrDefault(pmWorkFlow.getObjType(), new HashMap<>(0));
						areaPower = (String) project.getOrDefault("column001", areaPower);
					}
				} else if (DataType.INDUSTRY_ASSET.equals(dataType)) {
					IndustryAsset entity = (IndustryAsset) pmWorkFlow.getEntity();
					Map customInfo = (Map) entity.getCustomInfo();
					if (customInfo != null && customInfo.containsKey(pmWorkFlow.getObjType())) {
						Map project = (Map) customInfo.getOrDefault(pmWorkFlow.getObjType(), new HashMap<>(0));
						areaPower = (String) project.getOrDefault("column001", areaPower);
					}
				}
			}
			List<ProjectMember> members = Collections.emptyList();
			if (StringUtils.isNotBlank(memberRole)) {
				MemberVO t = new MemberVO();
				t.setProjectId(pmWorkFlow.getObjId());
				t.setMemberRole(memberRole);
				t.setEffective(new Date());
				members = projectMemberService.selectBySelective(t);
			}
			if (!members.isEmpty() && members.size() == 1) {
				assignee = members.get(0).getMemberCode();
				UserInfoVO user = userInfoService.selectOneByUserNameAndCompId(assignee);
				assignee = user.getId().toString();
			} else if (!members.isEmpty()) {
				candidates = new HashSet<String>(members.size());
				for (ProjectMember member : members) {
	//				candidates.add(member.getMemberCode());
					UserInfoVO user = userInfoService.selectOneByUserNameAndCompId(member.getMemberCode());
					candidates.add(user.getId().toString());
				}
			} else {
				candidateGroup = candidateRole;
			}
		} else {
			candidateGroup = candidateRole;
		}
		// 更新任务状态,补充流程信息
		if (DataType.PROJECT_TASK.equals(dataType)) {
			TaskVO projectTask = new TaskVO();
			projectTask.setTaskId(pmWorkFlow.getDataId());
			projectTask.setStatus(taskKey);
			projectTask.setCustomInfoByKey("currentTaskId", taskId);
			projectTask.setCustomInfoByKey("currentTaskKey", taskKey);
			projectTask.setCustomInfoByKey("currentProcInstId", procInstId);
			projectTaskService.updateByPrimaryKeySelective(projectTask);
		} else if (DataType.INDUSTRY_ASSET.equals(dataType)) {
			// 项目资产，更新入库状态和入库时间
			IndustryAssetVO industryAsset = new IndustryAssetVO();
			industryAsset.setId(pmWorkFlow.getDataId());
			industryAsset.setStatus(taskKey);
			industryAsset.setCustomInfoByKey("currentTaskId", taskId);
			industryAsset.setCustomInfoByKey("currentTaskKey", taskKey);
			industryAsset.setCustomInfoByKey("currentProcInstId", procInstId);
			industryAssetService.updateByPrimaryKeySelective(industryAsset);
		} else if (DataType.INDUSTRY_LEAK.equals(dataType)) {
			// 行业漏洞，更新入库状态和入库时间
			IndustryLeakVO industryLeak = new IndustryLeakVO();
			industryLeak.setId(pmWorkFlow.getDataId());
			industryLeak.setStatus(taskKey);
			industryLeak.setCustomInfoByKey("currentTaskId", taskId);
			industryLeak.setCustomInfoByKey("currentTaskKey", taskKey);
			industryLeak.setCustomInfoByKey("currentProcInstId", procInstId);
			industryLeakService.updateByPrimaryKeySelective(industryLeak);
		}
		if (assignee != null) {
			delegateTask.setAssignee(assignee);
		} else if (candidates != null) {
			delegateTask.addCandidateUsers(candidates);
		} else {
			delegateTask.addCandidateGroup(candidateGroup);
		}
		delegateTask.setVariableLocal("startTime", new Date());
		delegateTask.setVariableLocal("areaPower", areaPower);
		delegateTask.setVariableLocal("projectTypes", permissionProjectTypes);
//		delegateTask.setVariable("assignee", assignee);
//		delegateTask.setVariable("candidates", candidates);
//		delegateTask.setVariable("candidateGroup", candidateGroup);
		
		PmWorkFlow temp = new PmWorkFlow();
		temp.setId(pmWorkFlow.getId());
		temp.setTaskKey(taskKey);
		pmFlowService.updateByPrimaryKeySelective(temp);
		
		pmWorkFlow.setTaskId(delegateTask.getId());
		pmWorkFlow.setTaskKey(taskKey);
		runtimeService.setVariable(delegateTask.getProcessInstanceId(), "entity", pmWorkFlow);
		
		// 任务创建时的邮件提醒
		String templateCode = (String) taskDefinedVariables.getOrDefault("mailCode", defaultTaskMailTemplateKey);
		if (StringUtils.isNotBlank(templateCode)) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", templateCode);
			List<String> tos = selectActivitiUserMails(assignee, candidates, candidateGroup, areaPower, permissionProjectTypes);
			context.put("tos", StringUtils.join(tos, ";"));
			context.put("ccs", taskDefinedVariables.get("ccs"));
			context.put("dataSource", new Object[] {pmWorkFlow, delegateTask});
			MailUtil.keepMailWithTemplate(context);
		}
	}
	
	/**
	 * 任务完成监听器
	 */
	public void completeTask(DelegateTask delegateTask) throws Exception {
		String taskId = delegateTask.getId();
		String taskKey = delegateTask.getTaskDefinitionKey();
		String processKey = delegateTask.getProcessDefinitionId();
		PmWorkFlow pmWorkFlow = delegateTask.getVariable("entity", PmWorkFlow.class);
		
		String dataType = pmWorkFlow.getDataType();
		Map<String, Object> taskDefinedVariables = getTaskDefinedVariable(dataType, taskKey);
		Boolean isPass = (Boolean) delegateTask.getVariableLocal("isPass");
		Integer flowState = Boolean.TRUE.equals(isPass) ? 1 : -1;
		String status = null;
		if (Boolean.TRUE.equals(isPass)) {
			boolean hasNext = false;
//			if (ProcessType.TaskType.AF_APPROVE_TASK.equals(taskKey)) {
//				hasNext = Boolean.TRUE.equals(taskDefinedVariables.getOrDefault("needYFApprove", false));
//			} else if (ProcessType.TaskType.YF_APPROVE_TASK.equals(taskKey)) {
//				hasNext = Boolean.TRUE.equals(taskDefinedVariables.getOrDefault("needTrack", false));
//			}
			hasNext = Boolean.TRUE.equals(taskDefinedVariables.getOrDefault("hasNext", false));
			
			// 项目任务特殊处理，根据任务保存的变量来觉得是否走研发质量审核
			if (DataType.PROJECT_TASK.equals(dataType) && TaskType.AF_APPROVE_TASK.equals(taskKey)) {
				ProjectTask entity = (ProjectTask) pmWorkFlow.getEntity();
				Map customInfo = (Map) entity.getCustomInfo();
				if (customInfo != null) {
					Object needYFApprove = customInfo.get("needYFApprove");
					if (needYFApprove != null) {
						hasNext = Boolean.parseBoolean(String.valueOf(needYFApprove));
					}
				}
			}
			if (hasNext) {
				flowState++;
			}
		}
		if (TaskType.TRACK_TASK.equals(taskKey)) {
			List<Comment> taskComments = taskService.getTaskComments(taskId, CommentEntity.TYPE_COMMENT);
			// 项目资产，更新入库状态和入库时间
			if ("industryAsset".equals(dataType)) {
				IndustryAssetVO industryAsset = new IndustryAssetVO();
				industryAsset.setId(pmWorkFlow.getDataId());
				industryAsset.setTrackStatus(flowState);
				industryAsset.setTrackedTime(new Date());
				industryAsset.setCustomInfoByKey("trackedUser", UserContext.getUsername());
				industryAsset.setCustomInfoByKey("trackedUserName", UserContext.getCurrentPrincipal().getRealName());
//				industryAsset.setCustomInfoByKey("trackedComments", taskComments);
				industryAssetService.updateByPrimaryKeySelective(industryAsset);
			} else if ("industryLeak".equals(dataType)) {
				IndustryLeakVO industryLeak = new IndustryLeakVO();
				industryLeak.setId(pmWorkFlow.getDataId());
				industryLeak.setTrackStatus(flowState);
				industryLeak.setTrackedTime(new Date());
				industryLeak.setCustomInfoByKey("trackedUser", UserContext.getUsername());
				industryLeak.setCustomInfoByKey("trackedUserName", UserContext.getCurrentPrincipal().getRealName());
//				industryLeak.setCustomInfoByKey("trackedComments", taskComments);
				industryLeakService.updateByPrimaryKeySelective(industryLeak);
			}
		}
		delegateTask.setVariable("flowState", flowState);
	}
	
	private Map<String, Object> getTaskDefinedVariable(String dataType) {
		String definedVars = SystemConfig.systemVariables.get(definedVariablesKey);
		if(StringUtils.isBlank(definedVars)) {
			INotifyTemplateService notifyTemplateService = SpringContext.getBean(INotifyTemplateService.class);
			NotifyTemplate template = notifyTemplateService.selectByTemplateCode(definedVariablesKey);
			if (template != null) {
				definedVars = template.getContent();
			}
		}
		definedVars = StringUtils.defaultIfBlank(definedVars, defaultDefinedVariables);
		Map<String, Object> definedVariables = JSON.parseObject(definedVars, Map.class);
		Map<String, Object> taskDefinedVariables = (Map<String, Object>) definedVariables.getOrDefault(dataType, new HashMap<>());
		return taskDefinedVariables;
	}
	
	private Map<String, Object> getTaskDefinedVariable(String dataType, String taskType) {
		Map<String, Object> definedVariable = this.getTaskDefinedVariable(dataType);
		
		Map<String, Object> taskDefinedVariables = (Map<String, Object>) definedVariable.getOrDefault(taskType, new HashMap<>());
		return taskDefinedVariables;
	}
	
	private List<String> selectActivitiUserMails(String assignee, Set<String> candidates, String candidateGroup,
			String areaPower, String projectTypes) {
		Set<String> userIds = new HashSet<String>();
		if (assignee != null) {
			userIds.add(assignee);
		} else if (candidates != null) {
			userIds.addAll(candidates);
		}
		if (userIds.isEmpty()) {
			userIds.add(String.valueOf(Integer.MIN_VALUE));
		}
		String[] groupIds = StringUtils.split(candidateGroup, ",");
		if (groupIds == null || groupIds.length == 0) {
			groupIds = new String[] {"empty"};
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userIds", userIds);
		params.put("groupIds", groupIds);
		if (!"all".equals(areaPower)) {
			params.put("areaPower", areaPower);
		}
		if (!"all".equals(projectTypes)) {
			params.put("projectType", projectTypes);
		}
		List<String> mails = pmFlowService.selectActivitiUserMails(params);
		return mails;
	}
	
	/**
	 * 员工制定目标任务监听器
	 */
	public void makeGoalCompleteTask(DelegateTask delegateTask) throws Exception {
//		String taskId = delegateTask.getId();
//		PmWorkFlow pmWorkFlow = (PmWorkFlow) delegateTask.getVariable("entity");
//		
//		// TODO 目标审批人位priority：-1,后续多级并行审批需拓展，串行按添加的顺序审批
//		PlanObjectiveAppraiserRelationship temp = new PlanObjectiveAppraiserRelationship();
//		temp.setState(true);
//		temp.setIsWhole(true);
//		temp.setPriority(PerfConstant.AppraiserPriority.APPROVER);
//		temp.setParticipantId(pmWorkFlow.getParticipantId());
//		List<PlanObjectiveAppraiserRelationship> objectiveAppraiserList = planObjectiveAppraiserRelationshipService.selectBySelective(temp);
//		if (objectiveAppraiserList.isEmpty()) {
////			PlanObjectiveAppraiserRelationship noAssigenee = new PlanObjectiveAppraiserRelationship();
////			noAssigenee.setAppraiserName("无");
////			objectiveAppraiserList.add(noAssigenee);
//			throw new CustomActivitiException("目标审批关系不能为空！");
//		}
//		taskService.setVariable(taskId, "approverList", objectiveAppraiserList);
//		
//		//更新目标制定时间到ehr_participant表
//		PlanParticipant planParticipant = (PlanParticipant) delegateTask.getVariable("participant");
//		// XXX 直接创建对象进行更新，无需查询
//		PlanParticipant newTemp = new PlanParticipant();
//		newTemp.setId(planParticipant.getId());
//		newTemp.setMakeGoalTime(new Date());
//		planParticipantService.updateByPrimaryKeySelective(newTemp);
		return;
	}

	/**
	 * 目标审批任务创建时出给任务增加办理人，分组或指定人任务监听器
	 */
	public void approveGoalCreateTask(DelegateTask delegateTask) throws Exception {
//		PlanObjectiveAppraiserRelationship appraiser = (PlanObjectiveAppraiserRelationship) delegateTask
//				.getVariable("approver");
//		Integer appraiserId = appraiser.getAppraiserId();
//		String candidateGroups = SystemConfig.systemVariables.getOrDefault("perf.activiti.candidate.groups", "");
//		// 无审批人标识
//		String ignoreAssignee = SystemConfig.systemVariables.getOrDefault("perf.activiti.ignoreAssignee", "无");
//		String appraiserName = StringUtils.trimToNull(appraiser.getAppraiserName());
//		if ((appraiserId == null || appraiserId == 0) && appraiserName != null && candidateGroups.matches("(.*)\\b" + appraiserName + "\\b(.*)")) {
//			Group group = identityService.createGroupQuery().groupType(appraiserName).singleResult();
//			delegateTask.addCandidateGroup(group.getId());
//		} else if (appraiserName!= null && ignoreAssignee.contains(appraiserName)) {
//			delegateTask.setAssignee(appraiserName);
//		} else {
//			delegateTask.setAssignee(appraiserId == null ? "0" : String.valueOf(appraiserId));
//		}
		return;
	}
	
	/**
	 * 员工自评任务监听器
	 */
	public void selfSummaryCompleteTask(DelegateTask delegateTask) throws Exception {
//		String taskId = delegateTask.getId();
//		PmWorkFlow pmWorkFlow = (PmWorkFlow) delegateTask.getVariable("entity");
//		Integer participantId = pmWorkFlow.getParticipantId();
//		PlanParticipantObjective participantObjective = new PlanParticipantObjective();
//		participantObjective.setParticipantId(participantId);
//		participantObjective.setState(true);
//		List<PlanParticipantObjective> participantObjectiveList = planParticipantObjectiveService
//				.selectBySelective(participantObjective);
//		taskService.setVariable(taskId, "participantObjectiveList", participantObjectiveList);
//
//		String dataJSON = HttpContext.getCurrentRequest().getParameter("data");
//		List<PlanObjectiveEvaluation> objectiveEvaluations = JSON.parseArray(dataJSON, PlanObjectiveEvaluation.class);
//		for (PlanObjectiveEvaluation planObjectiveEvaluation : objectiveEvaluations) {
//			planObjectiveEvaluationService.invalidEvaluationByRelationshipId(planObjectiveEvaluation.getRelationshipId());
//			planObjectiveEvaluationService.insertSelective(planObjectiveEvaluation);
//		}
//		// 更新考核阶段状态值
//		PlanParticipant planParticipant = (PlanParticipant) delegateTask.getVariable("participant");
//		planParticipant.setStatus(PlanParticipantStatus.EVALUATE);
//		planParticipantService.updateStatusByPrimaryKey(planParticipant);
		return;
	}

	/**
	 * 指标评价开始任务监听器，赋值指标评价人
	 */
	public void objectiveEvaluateCreateTask(DelegateTask delegateTask) throws Exception {
//		PlanParticipantObjective participantObjective = (PlanParticipantObjective) delegateTask
//				.getVariable("participantObjective");
//		Byte priority = (Byte) delegateTask.getVariable("priority");
//		priority = priority == null ? 1 : priority;
//		PlanObjectiveAppraiserRelationship temp = new PlanObjectiveAppraiserRelationship();
//		temp.setParticipantObjectiveId(participantObjective.getId());
//		temp.setState(true);
//		// Priority 0 表示自评，自评分在自我总结时评论
//		temp.setPriority(Byte.valueOf(String.valueOf(priority + 1)));
//		long count = planObjectiveAppraiserRelationshipService.countBySelective(temp);
//		Boolean hasNext = false;
//		if (count > 0) {
//			hasNext = true;
//		}
//		delegateTask.setVariableLocal("hasNext", hasNext);
		return;
	}

	/**
	 * 指标评价开始任务监听器，赋值指标评价人
	 */
	public void objectiveEvaluateCreateExecution(DelegateExecution delegateExecution) throws Exception {
//		String executionId = delegateExecution.getId();
//		PlanParticipantObjective participantObjective = (PlanParticipantObjective) delegateExecution
//				.getVariable("participantObjective");
//		Byte priority = (Byte) delegateExecution.getVariable("priority");
//		priority = priority == null ? 0 : priority;
//
//		PlanObjectiveAppraiserRelationship temp = new PlanObjectiveAppraiserRelationship();
//		temp.setParticipantObjectiveId(participantObjective.getId());
//		temp.setState(true);
//		// Priority 0 表示自评，自评分在自我总结时评论
//		temp.setPriority(Byte.valueOf(String.valueOf(priority + 1)));
//		List<PlanObjectiveAppraiserRelationship> objectiveAppraiserList = planObjectiveAppraiserRelationshipService
//				.selectBySelective(temp);
//		runtimeService.setVariableLocal(executionId, "priority", temp.getPriority());
//		runtimeService.setVariableLocal(executionId, "objectiveAppraiserList", objectiveAppraiserList);
		return;
	}

	/**
	 * 指标评价完成任务监听器，插入评价人评分结果
	 */
	public void objectiveEvaluateCompleteTask(DelegateTask delegateTask) throws Exception {
//		PlanObjectiveAppraiserRelationship relation = (PlanObjectiveAppraiserRelationship) delegateTask
//				.getVariable("appraiser");
//		String dataJSON = (String) delegateTask.getVariable("data");
//		List<PlanObjectiveEvaluationVO> objectiveEvaluations = JSON.parseArray(dataJSON, PlanObjectiveEvaluationVO.class);
//		boolean flag = false;
//		Principal user = UserContext.getCurrentPrincipal();
//		for (PlanObjectiveEvaluationVO planObjectiveEvaluation : objectiveEvaluations) {
//			if (planObjectiveEvaluation.getRelationshipId().equals(relation.getId()) 
//					|| (relation.getPriority() != null && Integer.valueOf(relation.getPriority().intValue()).equals(planObjectiveEvaluation.getPriority())
//						&& String.valueOf(user.getUserCustom4()).equals(delegateTask.getAssignee()))) {
//				if (planObjectiveEvaluation.getAppraiserId() == null) {
//					planObjectiveEvaluation.setAppraiserId(user.getUserCustom4());
////					planObjectiveEvaluation.setAppraiserId(user.getUserInfoId());
//				}
//				planObjectiveEvaluation.setCreateBy(user.getUserName());
//				planObjectiveEvaluationService.invalidEvaluationByRelationshipId(planObjectiveEvaluation.getRelationshipId());
//				planObjectiveEvaluationService.insertSelective(planObjectiveEvaluation);
//				flag = true;
//			}
//		}
//		if (!flag) {
//			if (relation.getId() == null) {
//				throw new CustomActivitiException("考核评定关系不能为空！");
//			} else {
//				throw new CustomActivitiException("评定级别[" + relation.getPriority() + "]没有对应的评定结果！");
//			}
//		}
////		String taskKey = delegateTask.getTaskDefinitionKey();
////		if (PerfConstant.PlanTaskKey.DIRECT_SUMMARY_KEY.equals(taskKey)) {
////			// 判断主管审批的活动任务是否为1，若为1则认为是最终评定
////			Integer nrOfActiveInstances = (Integer) delegateTask.getVariable("nrOfActiveInstances");
////			if (nrOfActiveInstances == 1) {
////				PlanParticipantSummary participantSummary = new PlanParticipantSummary();
////				
////			}
////		}
		return;
	}

	/**
	 * 检查指标评价人时候还有下级任务监听器
	 */
	public void checkNextAppraiserExecution(DelegateExecution delegateExecution) throws Exception {
//		String executionId = delegateExecution.getId();
//		PlanParticipantObjective participantObjective = (PlanParticipantObjective) delegateExecution
//				.getVariable("participantObjective");
//		Byte priority = (Byte) delegateExecution.getVariable("priority");
//		priority = priority == null ? 1 : priority;
//		PlanObjectiveAppraiserRelationship temp = new PlanObjectiveAppraiserRelationship();
//		temp.setParticipantObjectiveId(participantObjective.getId());
//		temp.setState(true);
//		// Priority 0 表示自评，自评分在自我总结时评论
//		temp.setPriority(Byte.valueOf(String.valueOf(priority + 1)));
//		List<PlanObjectiveAppraiserRelationship> objectiveAppraiserList = planObjectiveAppraiserRelationshipService
//				.selectBySelective(temp);
//		Boolean hasNext = false;
//		if (!objectiveAppraiserList.isEmpty()) {
//			hasNext = true;
//			runtimeService.setVariableLocal(executionId, "objectiveAppraiserList", objectiveAppraiserList);
//		}
//		runtimeService.setVariableLocal(executionId, "priority", temp.getPriority());
//		runtimeService.setVariableLocal(executionId, "hasNext", hasNext);
//		return;
	}

	/**
	 * 串行主管审批任务创建时传入所有审批主管任务监听器
	 */
	public void directSummaryCreateExecution(DelegateExecution delegateExecution) throws Exception {
//		//String executionId = delegateExecution.getId();
//		PmWorkFlow pmWorkFlow = (PmWorkFlow) delegateExecution.getVariable("entity");
//		Integer participantId = pmWorkFlow.getParticipantId();
//		// 判断现有考核对象的评估人是否与评估人关系中维护的一致，如果不一致则刷新评估人关系
////		boolean needRefresh = planObjectiveAppraiserRelationshipService.checkAppraiserRelationship(participantId);
////		if (needRefresh) {
////			List<PlanParticipant> participantList = planParticipantService.selectByParticipantIds(String.valueOf(participantId));
////			// 重新生成评估关系，忽略自评
////			planParticipantService.generateParticipantAppraiserRelationship(participantList, "0");
////		}
//		
//		String ignorePriorities = planObjectiveAppraiserRelationshipService.getIgnorePriorities(participantId);
//		if (ignorePriorities != null) {
//			List<PlanParticipant> participantList = planParticipantService.selectByParticipantIds(String.valueOf(participantId));
//			// 重新生成评估关系，忽略自评
//			planParticipantService.generateParticipantAppraiserRelationship(participantList, ignorePriorities + ",0");
//		}
//		PlanObjectiveAppraiserRelationshipVO temp = new PlanObjectiveAppraiserRelationshipVO();
//		temp.setParticipantId(participantId);
//		temp.setIsWhole(true);
//		temp.setState(true);
//		// Priority 0 表示自评，自评分在自我总结时评论
//		List<PlanObjectiveAppraiserRelationshipVO> approverList = planObjectiveEvaluationService
//				.selectRelationshipWithEvaluation(temp, true);
//		if (!approverList.isEmpty()) {
//			// 除去自评
//			temp = approverList.get(0);
//			if (temp.getPriority() == 0) {
//				approverList.remove(0);
//			}
//		}
//		if (approverList.isEmpty()) {
//			PlanObjectiveAppraiserRelationshipVO noAssignee = new PlanObjectiveAppraiserRelationshipVO();
//			noAssignee.setAppraiserName("无");
//			approverList.add(noAssignee);
//		}
//		delegateExecution.setVariableLocal("appraiserList", approverList);
//		// runtimeService.setVariable(executionId, "appraiserList",
//		// approverList);
//		// 更新考核阶段状态值
//		PlanParticipant planParticipant = (PlanParticipant) delegateExecution.getVariable("participant");
//		planParticipant.setStatus(PlanParticipantStatus.DIRECT_SUMMARY);
//		planParticipantService.updateStatusByPrimaryKey(planParticipant);
		return;
	}

	/**
	 * 主管评价任务创建时出给任务增加办理人，分组或指定人任务监听器
	 */
	public void directSummaryCreateTask(DelegateTask delegateTask) throws Exception {
//		PlanObjectiveAppraiserRelationship appraiser = (PlanObjectiveAppraiserRelationship) delegateTask
//				.getVariable("appraiser");
//		
//		// FIXME 考核关系刷新后，无法更新到最新的考核人
////		Byte priority = appraiser.getPriority();
////		PlanObjectiveAppraiserRelationship temp = new PlanObjectiveAppraiserRelationship();
////		temp.setParticipantId(appraiser.getParticipantId());
////		temp.setPriority(priority);
////		temp.setIsWhole(true);
////		temp.setState(true);
////		
////		List<PlanObjectiveAppraiserRelationship> tempAppraiserList = planObjectiveAppraiserRelationshipService.selectBySelective(temp);
////		if (!tempAppraiserList.isEmpty()) {
////			appraiser = tempAppraiserList.get(0);
////		}
//		// FIXME
//		
//		Integer appraiserId = appraiser.getAppraiserId();
//		String candidateGroups = SystemConfig.systemVariables.getOrDefault("perf.activiti.candidate.groups", "");
//		// 无审批人标识
//		String ignoreAssignee = SystemConfig.systemVariables.getOrDefault("perf.activiti.ignoreAssignee", "无");
//		String appraiserName = StringUtils.trimToNull(appraiser.getAppraiserName());
//		if ((appraiserId == null || appraiserId == 0) && appraiserName != null && candidateGroups.matches("(.*)\\b" + appraiserName + "\\b(.*)")) {
//			Group group = identityService.createGroupQuery().groupType(appraiserName).singleResult();
//			delegateTask.addCandidateGroup(group.getId());
//		} else if (appraiserName!= null && ignoreAssignee.contains(appraiserName)) {
//			delegateTask.setAssignee(appraiserName);
//		} else {
//			delegateTask.setAssignee(appraiserId == null ? "0" : String.valueOf(appraiserId));
//		}
		return;
	}

	/**
	 * 沟通反馈增加办理人任务监听器
	 */
	public void communicationCreateExecution(DelegateExecution delegateExecution) throws Exception {
//		PlanParticipant planParticipant = (PlanParticipant) delegateExecution.getVariable("participant");
//
//		PlanObjectiveAppraiserRelationship temp = new PlanObjectiveAppraiserRelationship();
//		temp.setParticipantId(planParticipant.getId());
//		temp.setPriority(PerfConstant.AppraiserPriority.COMMUNICATOR);
//		temp.setState(true);
//		List<PlanObjectiveAppraiserRelationship> objectiveAppraiserList = planObjectiveAppraiserRelationshipService
//				.selectBySelective(temp);
//		PlanObjectiveAppraiserRelationship communicator = new PlanObjectiveAppraiserRelationship();
//		if (!objectiveAppraiserList.isEmpty()) {
//			communicator = objectiveAppraiserList.get(0);
//		} else {
//			// 假如维护的沟通人不存在，判断是否允许无沟通人
//			boolean ignoreBlankAssignee = Boolean.valueOf(SystemConfig.systemVariables.getOrDefault("perf.activiti.communicate.ignoreBlankAssignee", "false"));
//			if (!ignoreBlankAssignee) {
//				// 不允许则查询直接主管
//				Employee employee = employeeService.selectByPrimaryKey(planParticipant.getEmpID());
//				Employee director = employeeService.selectByPrimaryKey(employee.getReportTo());
//				communicator = new PlanObjectiveAppraiserRelationship();
//				communicator.setAppraiserId(director.getEmpID());
//				communicator.setAppraiserName(director.getName());
//			}
//		}
//		delegateExecution.setVariableLocal("communicator", communicator);
//		// 查询被考核人最终评价人的评分结果，初始化PlanParticipantSummary，插入到最终结果表中
//		PlanParticipantSummary participantSummary = planParticipantSummaryService.selectFinalEvaluationByParticipantId(planParticipant.getId());
//		participantSummary.setCommunicatorId(communicator.getAppraiserId());
//		planParticipantSummaryService.insertSelective(participantSummary);
//		delegateExecution.setVariableLocal("summaryId", participantSummary.getId());
//		// 更新考核阶段状态值
//		planParticipant.setStatus(PlanParticipantStatus.COMMUNICATION);
//		planParticipantService.updateStatusByPrimaryKey(planParticipant);
		return;
	}
	
	/**
	 * 沟通反馈增加办理人任务监听器
	 */
	public void communicationCreateTask(DelegateTask delegateTask) throws Exception {
//		PlanObjectiveAppraiserRelationship appraiser = (PlanObjectiveAppraiserRelationship) delegateTask
//				.getVariable("communicator");
//		Integer appraiserId = appraiser.getAppraiserId();
//		String candidateGroups = SystemConfig.systemVariables.getOrDefault("perf.activiti.candidate.groups", "");
//		// 无审批人标识
//		String ignoreAssignee = SystemConfig.systemVariables.getOrDefault("perf.activiti.ignoreAssignee", "无");
//		String appraiserName = StringUtils.trimToNull(appraiser.getAppraiserName());
//		boolean ignoreBlankAssignee = Boolean.valueOf(SystemConfig.systemVariables.getOrDefault("perf.activiti.communicate.ignoreBlankAssignee", "false"));
//		if ((appraiserId == null || appraiserId == 0) && appraiserName != null && candidateGroups.matches("(.*)\\b" + appraiserName + "\\b(.*)")) {
//			Group group = identityService.createGroupQuery().groupType(appraiserName).singleResult();
//			delegateTask.addCandidateGroup(group.getId());
//		} else if ((appraiserName != null && ignoreAssignee.contains(appraiserName)) || (ignoreBlankAssignee && StringUtils.isBlank(appraiserName))) {
//			delegateTask.setAssignee(StringUtils.isBlank(appraiserName) ? "无" : appraiserName);
//		} else {
//			delegateTask.setAssignee(appraiserId == null ? "0" : String.valueOf(appraiserId));
//		}
		return;
	}
	
	public void noAssigneeAssignmentTask(DelegateTask delegateTask) throws Exception {
		String assignee = delegateTask.getAssignee();
		String ignoreAssignee = SystemConfig.systemVariables.getOrDefault("perf.activiti.ignoreAssignee", "无");
		if (StringUtils.isBlank(assignee) || ignoreAssignee.equals(assignee)) {
			taskService.addComment(delegateTask.getId(), delegateTask.getProcessInstanceId(), "无任务办理人，自动完成该任务！");
			taskService.complete(delegateTask.getId());
		}
	}

	/**
	 * 沟通反馈完成任务监听器
	 */
	public void communicationCompleteTask(DelegateTask delegateTask) throws Exception {
//		PlanObjectiveAppraiserRelationship communicator = (PlanObjectiveAppraiserRelationship) delegateTask.getVariable("communicator");
//		Integer summaryId = (Integer) delegateTask.getVariable("summaryId");
//		PlanParticipant planParticipant = (PlanParticipant) delegateTask.getVariable("participant");
//		Integer appraiserId = communicator.getAppraiserId();
////		String dataJson = HttpContext.getCurrentRequest().getParameter("data");
//		String dataJson = (String) delegateTask.getVariableLocal("data");
//		PlanParticipantSummary participantSummary = JSON.parseObject(dataJson, PlanParticipantSummary.class);
//		if (participantSummary == null) {
//			participantSummary = new PlanParticipantSummary();
//		}
//		participantSummary.setId(summaryId);
//		// 避免沟通人篡改最终评定结果
//		if (appraiserId != null && appraiserId.equals(participantSummary.getAppraiserId())) {
//			PlanParticipantSummary temp = planParticipantSummaryService.selectByPrimaryKey(participantSummary.getId());
//			if (!appraiserId.equals(temp.getAppraiserId())) {
//				participantSummary.setScore(null);
//			}
//		}
//		participantSummary.setAppraiserId(null);
//		participantSummary.setCommunicatorId(appraiserId);
//		participantSummary.setParticipantId(planParticipant.getId());
//		participantSummary.setCommunicateTime(new Date());
//		planParticipantSummaryService.updateByPrimaryKeySelective(participantSummary);
		return;
	}

	/**
	 * 设置申诉处理人监听器
	 */
	public void appealCreateExecution(DelegateExecution delegateExecution){
//		PlanParticipant planParticipant = (PlanParticipant) delegateExecution.getVariable("participant");
//		//查询申诉人（不同部门可能有所差异）
//		//Employee employee = employeeService.selectByPrimaryKey(planParticipant.getEmpID());
////		Employee appealHandler = employeeService.selectByPrimaryKey(employee.getReportTo());
//
//		// 查询申诉人（不同部门可能有所差异）
//		PlanObjectiveAppraiserRelationship temp = new PlanObjectiveAppraiserRelationship();
//		temp.setState(true);
//		temp.setIsWhole(true);
//		temp.setPriority(PerfConstant.AppraiserPriority.APPEAL_HANDLER);
//		temp.setParticipantId(planParticipant.getId());
//		List<PlanObjectiveAppraiserRelationship> objectiveAppraiserList = planObjectiveAppraiserRelationshipService.selectBySelective(temp);
//		Employee appealHandler = null;
//		// 找不到维护的申诉人时，找直接主管
//		if (objectiveAppraiserList.isEmpty()) {
//			Employee employee = employeeService.selectVOByPrimaryKey(planParticipant.getEmpID());
//			appealHandler = employeeService.selectByPrimaryKey(employee.getReportTo());
//		} else {
//			temp = objectiveAppraiserList.get(0);
//			appealHandler = employeeService.selectByPrimaryKey(temp.getAppraiserId());
//		}
//		delegateExecution.setVariableLocal("appealHandler", appealHandler);
//		
//		//保存申诉原因
//		Integer summaryId = (Integer) delegateExecution.getVariable("summaryId");
//		PlanParticipantSummary participantSummary = new PlanParticipantSummary();
//		participantSummary.setAppealReason(HttpContext.getCurrentRequest().getParameter("content"));
//		participantSummary.setId(summaryId);
//		participantSummary.setAppealTime(new Date());
//		participantSummary.setAppealHandler(appealHandler.getEmpID());
//		participantSummary.setHasAppeal(1);
//		planParticipantSummaryService.updateByPrimaryKeySelective(participantSummary);
//		
//		// 更新考核阶段状态值
//		planParticipant.setStatus(PlanParticipantStatus.APPEAL);
//		planParticipantService.updateStatusByPrimaryKey(planParticipant);
		return;
	}
	
	/**
	 * 处理申诉任务完成监听器
	 */
	public void appealCompleteTask(DelegateTask delegateTask){
//		Employee appealHandler = (Employee) delegateTask.getVariable("appealHandler");
//		Integer summaryId = (Integer) delegateTask.getVariable("summaryId");
//		PlanParticipant planParticipant = (PlanParticipant) delegateTask.getVariable("participant");
//		Integer appealHandlerId = appealHandler.getEmpID();
//		Integer assignee = UserContext.getCurrentUser().getUserCustom4();
////		Integer assignee = UserContext.getCurrentPrincipal().getUserInfoId();
//		if(appealHandlerId.equals(assignee)){//申诉处理人可以修改最终结果，并将相关信息存在结果表中
//			String dataJson = HttpContext.getCurrentRequest().getParameter("data");
//			PlanParticipantSummary participantSummary = JSON.parseObject(dataJson, PlanParticipantSummary.class);
//			participantSummary.setId(summaryId);
//			participantSummary.setParticipantId(planParticipant.getId());
//			participantSummary.setAppealHandleTime(new Date());
//			planParticipantSummaryService.updateByPrimaryKeySelective(participantSummary);
//		}
		return;
	}
	/**
	 * 员工同意考核结果
	 */
	public void confirmResultExecution(DelegateExecution delegateExecution){
//		Integer summaryId = (Integer) delegateExecution.getVariable("summaryId");
//		PlanParticipantSummary participantSummary = new PlanParticipantSummary();
//		participantSummary.setId(summaryId);
//		participantSummary.setAppealTime(new Date());
//		participantSummary.setHasAppeal(2);
//		planParticipantSummaryService.updateByPrimaryKeySelective(participantSummary);
	}
	
}
