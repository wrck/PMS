package com.dp.plat.pms.springmvc.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.process.exception.CustomActivitiException;
import com.dp.plat.activiti.service.IRuntimePageService;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.pojo.NotifyTemplate;
import com.dp.plat.core.service.INotifyTemplateService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.vo.UserInfoVO;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.TaskType;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import com.dp.plat.pms.springmvc.entity.ProjectMember;
import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.service.IPmWorkFlowService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectMemberService;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;
import com.dp.plat.pms.springmvc.vo.MemberVO;
import com.dp.plat.pms.springmvc.vo.SettlementVO;
import com.dp.plat.support.mail.MailUtil;

/**
 * 项目转包流程任务监听器
 * 
 * @author w02611
 *
 */
@Component("subcontractInspectionListener")
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class SubcontractInspectionListener {

	private final static String definedVariablesKey = "pm.workflow.subcontractInspection.defineVariable";
	private final static String defaultDefinedVariables = "{}";
	private final static String defaultTaskMailTemplateKey = "pm.workflow.subcontractInspection.mail";

	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IRuntimePageService runtimePageService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private IPmWorkFlowService pmFlowService;
	@Autowired
	private IProjectHeaderService projectHeaderService;
	@Autowired
	private IDispatchProjectService dispatchProjectService;
	@Autowired
	private IDispatchSettlementService dispatchSettlementService;
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
		String processInstanceId = delegateExecution.getProcessInstanceId();
		String executionId = delegateExecution.getId();
		String processKey = delegateExecution.getProcessDefinitionId();
//		PmWorkFlow pmWorkFlow = delegateExecution.getVariable("entity", PmWorkFlow.class);
		PmWorkFlow pmWorkFlow = getCurrentEntity(delegateExecution);

		String dataType = pmWorkFlow.getDataType();
		Map<String, Object> taskDefinedVariables = getTaskDefinedVariable(dataType);
		ProcessDefinitionImpl processDefinition = ((ExecutionEntity)delegateExecution).getProcessDefinition();
		List<ActivityImpl> activityList = processDefinition.getActivities();
		String nextTaskKey = "";
		for (Iterator<ActivityImpl> iterator = activityList.iterator(); iterator.hasNext();) {
			ActivityImpl activityVo = iterator.next();
			if (delegateExecution.getCurrentActivityId().equals(activityVo.getId())) {
				PvmTransition transition = activityVo.getOutgoingTransitions().get(0);
				PvmActivity destination = transition.getDestination();
				nextTaskKey = destination.getId();
				break;
			}
		}
		// 审批节点的候选角色组
		List<Map<String, Object>> approverList = (List<Map<String, Object>>) taskDefinedVariables.getOrDefault(nextTaskKey, Collections.emptyList());
		if (approverList.isEmpty()) {
//			PlanObjectiveAppraiserRelationship noAssigenee = new PlanObjectiveAppraiserRelationship();
//			noAssigenee.setAppraiserName("无");
//			objectiveAppraiserList.add(noAssigenee);
			throw new CustomActivitiException("验收审批关系不能为空！");
		}
		runtimeService.setVariable(executionId, "approverList", approverList);

		String processName = (String) taskDefinedVariables.getOrDefault("title", "");
		if (StringUtils.isNotBlank(processName)) {
			processName += " -- ";
		}
		runtimeService.setVariable(executionId, "processName", processName);

		if (DataType.PROJECT_DISPATCH.equals(dataType)) {
			DispatchProject dispatch = new DispatchVO();
			dispatch.setId(pmWorkFlow.getDataId());
			dispatch.setState(10);
			dispatch.setCustomInfoByKey("currentProcInstId", delegateExecution.getProcessInstanceId());
			dispatchProjectService.updateByPrimaryKeySelective(dispatch);
		} else if (DataType.DISPATCH_SETTLEMENT.equals(dataType)) {
			SettlementVO settlement = new SettlementVO();
			settlement.setId(pmWorkFlow.getDataId());
			settlement.setState(10);
			settlement.setCustomInfoByKey("currentProcInstId", delegateExecution.getProcessInstanceId());
			dispatchSettlementService.updateByPrimaryKeySelective(settlement);
		}
	}

	/**
	 * 绩效计划目标审批流程完成后执行结束监听器
	 */
	public void processEndExecution(DelegateExecution delegateExecution) throws Exception {
		String taskId = delegateExecution.getId();
		ExecutionEntity executionEntity = (ExecutionEntity) delegateExecution;
		String processKey = executionEntity.getProcessDefinitionKey();
//			PmWorkFlow pmWorkFlow = delegateExecution.getVariable("entity", PmWorkFlow.class);
		PmWorkFlow pmWorkFlow = getCurrentEntity(delegateExecution);

		Boolean isPass = Boolean.TRUE.equals(delegateExecution.getVariable("isPass"));
		String dataType = pmWorkFlow.getDataType();
		String workflowStatus = isPass ? BaseVO.APPROVAL_SUCCESS : BaseVO.APPROVAL_FAILED;
		String dataStatus = isPass ? TaskType.END : TaskType.REJECT;
		if (DataType.PROJECT_DISPATCH.equals(dataType)) {
			DispatchVO dispatch = new DispatchVO();
			dispatch.setId(pmWorkFlow.getDataId());
			dispatch.setState(isPass ? 20 : 0);
			dispatch.setCustomInfoByKey("approveState", isPass ? "审批通过" : "审批驳回");
			dispatch.setCustomInfoByKey("currentTaskId", null);
			dispatch.setCustomInfoByKey("currentTaskKey", null);
			dispatch.setCustomInfoByKey("currentProcInstId", null);
			dispatchProjectService.updateByPrimaryKeySelective(dispatch);
		} else if (DataType.DISPATCH_SETTLEMENT.equals(dataType)) {
			SettlementVO settlement = new SettlementVO();
			settlement.setId(pmWorkFlow.getDataId());
			settlement.setState(isPass ? 20 : 0);
			settlement.setCustomInfoByKey("approveState", isPass ? "审批通过" : "审批驳回");
			settlement.setCustomInfoByKey("currentTaskId", null);
			settlement.setCustomInfoByKey("currentTaskKey", null);
			settlement.setCustomInfoByKey("currentProcInstId", null);
			dispatchSettlementService.updateByPrimaryKeySelective(settlement);
			// 审批通过后，进行结算确认
			if (isPass) {
			    dispatchSettlementService.settlementSubmit(settlement.getId(), settlement);
			}
		} else if (DataType.INDUSTRY_ASSET.equals(dataType)) {
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

		// String businessKey = delegateExecution.getProcessBusinessKey();
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

//		PmWorkFlow pmWorkFlow = delegateTask.getVariable("entity", PmWorkFlow.class);
		PmWorkFlow pmWorkFlow = getCurrentEntity(delegateTask);
		String objType = pmWorkFlow.getObjType();
		String dataType = pmWorkFlow.getDataType();
//		List<Map<String, Object>> taskDefinedVariables = getTaskDefinedVariable(dataType, taskKey);
		Map<String, Object> taskDefinedVariables = (Map<String, Object>) delegateTask.getVariable("approver");
		String assignee = null;
		Set<String> candidates = null;
		String candidateGroup = null;
		String memberRole = null;
		String candidateRole = null;
		String permissionProjectTypes = "all";
		String areaPower = "all";
		boolean checkArea = Boolean.TRUE.equals(taskDefinedVariables.getOrDefault("checkArea", false));
		assignee = (String) taskDefinedVariables.getOrDefault("assignee", "");
        candidates = new HashSet(Arrays.asList(StringUtils.split((String) taskDefinedVariables.getOrDefault("candidates", ""), ",")));
		memberRole = (String) taskDefinedVariables.getOrDefault("memberRole", "");
		candidateRole = (String) taskDefinedVariables.getOrDefault("candidateRole", "");
		if (DataType.PROJECT.equals(objType) || DataType.PROJECT_DISPATCH.equals(objType)) {
			permissionProjectTypes = StringUtils.defaultIfBlank((String) pmWorkFlow.getCustomInfoByKey("projectTypes"), permissionProjectTypes);
		}
		if (TaskType.ACCEPTANCE_TASK.equals(taskKey)) {
			if (checkArea) {
				if (DataType.PROJECT_DISPATCH.equals(dataType)) {
					DispatchProject entity = (DispatchProject) pmWorkFlow.getEntity();
					areaPower = entity.getProfitDepCode();
				} else if (DataType.DISPATCH_SETTLEMENT.equals(dataType)) {
					DispatchSettlement entity = (DispatchSettlement) pmWorkFlow.getEntity();
					Map customInfo = (Map) entity.getCustomInfo();
					if (customInfo != null && customInfo.containsKey(pmWorkFlow.getObjType())) {
						Map dispatch = (Map) customInfo.getOrDefault(pmWorkFlow.getObjType(), new HashMap<>(0));
						areaPower = (String) dispatch.getOrDefault("profitDepCode", areaPower);
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
			if (StringUtils.isNotBlank(assignee) || (candidates != null && !candidates.isEmpty())) {
                // 指定了具体的办理人，或者候选人
			    if (StringUtils.isNotBlank(assignee)) {
			        // 将用户名转化为userId
			        UserInfoVO assigneeUser = userInfoService.selectOneByUserNameAndCompId(assignee);
			        if (assigneeUser != null) {
			            assignee = assigneeUser.getId().toString();
			        }
			    }
			    if (candidates != null && !candidates.isEmpty()) {
			        // 将用户名转化为userId
			        Set<String> newCandidates = new HashSet(candidates.size());
			        for (String candidate : candidates) {
			            UserInfoVO candidateUser = userInfoService.selectOneByUserNameAndCompId(candidate);
			            if (candidateUser != null) {
			                newCandidates.add(candidateUser.getId().toString());
			            } else {
			                newCandidates.add(candidate);
			            }
                    }
			        candidates = newCandidates;
                }
            } else if (!members.isEmpty() && members.size() == 1) {
				assignee = members.get(0).getMemberCode();
				UserInfoVO user = userInfoService.selectOneByUserNameAndCompId(assignee);
				if (user != null) {
				    assignee = user.getId().toString();
				}
			} else if (!members.isEmpty()) {
				candidates = new HashSet<String>(members.size());
				for (ProjectMember member : members) {
					// candidates.add(member.getMemberCode());
					UserInfoVO user = userInfoService.selectOneByUserNameAndCompId(member.getMemberCode());
					if (user != null) {
					    candidates.add(user.getId().toString());
					}
				}
			} else {
				candidateGroup = candidateRole;
			}
		} else {
			candidateGroup = candidateRole;
		}
		// 更新任务状态,补充流程信息
		if (DataType.PROJECT_DISPATCH.equals(dataType)) {
			DispatchVO dispatch = new DispatchVO();
			dispatch.setId(pmWorkFlow.getDataId());
//			dispatch.setStatus(taskKey);
			dispatch.setCustomInfoByKey("approveState", taskDefinedVariables.getOrDefault("taskName", "") + "审批中");
			dispatch.setCustomInfoByKey("currentTaskId", taskId);
			dispatch.setCustomInfoByKey("currentTaskKey", taskKey);
			dispatch.setCustomInfoByKey("currentProcInstId", procInstId);
			dispatchProjectService.updateByPrimaryKeySelective(dispatch);
		} else if (DataType.DISPATCH_SETTLEMENT.equals(dataType)) {
			SettlementVO settlement = new SettlementVO();
			settlement.setId(pmWorkFlow.getDataId());
//			settlement.setStatus(taskKey);
			settlement.setCustomInfoByKey("approveState", taskDefinedVariables.getOrDefault("taskName", "") + "审批中");
			settlement.setCustomInfoByKey("currentTaskId", taskId);
			settlement.setCustomInfoByKey("currentTaskKey", taskKey);
			settlement.setCustomInfoByKey("currentProcInstId", procInstId);
			dispatchSettlementService.updateByPrimaryKeySelective(settlement);
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
		if (StringUtils.isNotBlank(assignee)) {
			delegateTask.setAssignee(assignee);
		} else if (candidates != null && !candidates.isEmpty()) {
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
			List<String> tos = selectActivitiUserMails(assignee, candidates, candidateGroup, areaPower,
					permissionProjectTypes);
			context.put("tos", StringUtils.join(tos, ";"));
			context.put("ccs", taskDefinedVariables.get("ccs"));
			context.put("dataSource", new Object[] { pmWorkFlow, delegateTask });
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
//		PmWorkFlow pmWorkFlow = delegateTask.getVariable("entity", PmWorkFlow.class);
        PmWorkFlow pmWorkFlow = getCurrentEntity(delegateTask);

        String dataType = pmWorkFlow.getDataType();
        List<Map<String, Object>> taskDefinedVariables = getTaskDefinedVariable(dataType, taskKey);
        Boolean isPass = (Boolean) delegateTask.getVariableLocal("isPass");
        String data = (String) delegateTask.getVariableLocal("data");
        HashMap<String, Object> customData = JSON.parseObject(data, HashMap.class);
        Integer flowState = Boolean.TRUE.equals(isPass) ? 1 : -1;
        String status = null;
        // 处理数据
        Map<String, Object> filtedCustomData = new HashMap<String, Object>(customData.size());
        for (Entry<String, Object> entry : customData.entrySet()) {
            filtedCustomData.put(entry.getKey(), Boolean.TRUE.equals(isPass) ? entry.getValue() : "");
        }
        if (DataType.PROJECT_DISPATCH.equals(dataType)) {
            DispatchVO dispatch = new DispatchVO();
            dispatch.setId(pmWorkFlow.getDataId());
            dispatch.setCustomInfo(filtedCustomData);
            dispatchProjectService.updateByPrimaryKeySelective(dispatch);
        } else if (DataType.DISPATCH_SETTLEMENT.equals(dataType)) {
            SettlementVO settlement = new SettlementVO();
            settlement.setId(pmWorkFlow.getDataId());
            settlement.setCustomInfo(filtedCustomData);
            dispatchSettlementService.updateByPrimaryKeySelective(settlement);
        }
        // 将自定义审批变量保存到当前示例中
        PmWorkFlow temp = new PmWorkFlow();
        temp.setId(pmWorkFlow.getId());
        for (Entry<String, Object> entry : filtedCustomData.entrySet()) {
            pmWorkFlow.setCustomInfoByKey(entry.getKey(), entry.getValue());
            temp.setCustomInfoByKey(entry.getKey(), entry.getValue());
        }
        pmFlowService.updateByPrimaryKeySelective(temp);
        runtimeService.setVariable(delegateTask.getProcessInstanceId(), "entity", pmWorkFlow);
	}

	private Map<String, Object> getTaskDefinedVariable(String dataType) {
		String definedVars = SystemConfig.systemVariables.get(definedVariablesKey);
		if (StringUtils.isBlank(definedVars)) {
			INotifyTemplateService notifyTemplateService = SpringContext.getBean(INotifyTemplateService.class);
			NotifyTemplate template = notifyTemplateService.selectByTemplateCode(definedVariablesKey);
			if (template != null) {
				definedVars = template.getContent();
			}
		}
		definedVars = StringUtils.defaultIfBlank(definedVars, defaultDefinedVariables);
		Map<String, Object> definedVariables = JSON.parseObject(definedVars, Map.class);
		Map<String, Object> taskDefinedVariables = (Map<String, Object>) definedVariables.getOrDefault(dataType,
				new HashMap<>());
		return taskDefinedVariables;
	}

	private List<Map<String, Object>> getTaskDefinedVariable(String dataType, String taskType) {
		Map<String, Object> definedVariable = this.getTaskDefinedVariable(dataType);

		List<Map<String, Object>> taskDefinedVariables = (List<Map<String, Object>>) definedVariable.getOrDefault(taskType,
				new ArrayList<Map<String, Object>>());
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
			groupIds = new String[] { "empty" };
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
//		String dataType = pmWorkFlow.getDataType();
//		Map<String, Object> taskDefinedVariable = getTaskDefinedVariable(dataType);
//		PlanObjectiveAppraiserRelationship temp = new PlanObjectiveAppraiserRelationship();
//		temp.setState(true);
//		temp.setIsWhole(true);
//		temp.setPriority(PerfConstant.AppraiserPriority.APPROVER);
//		temp.setParticipantId(pmWorkFlow.getParticipantId());
//		List<PlanObjectiveAppraiserRelationship> approverList = planObjectiveAppraiserRelationshipService.selectBySelective(temp);
//		
//		
//		if (approverList.isEmpty()) {
////			PlanObjectiveAppraiserRelationship noAssigenee = new PlanObjectiveAppraiserRelationship();
////			noAssigenee.setAppraiserName("无");
////			objectiveAppraiserList.add(noAssigenee);
//			throw new CustomActivitiException("验收审批关系不能为空！");
//		}
//		taskService.setVariable(taskId, "approverList", approverList);
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
	public void approveAcceptanceCreateTask(DelegateTask delegateTask) throws Exception {
		UserEntity approver = (UserEntity) delegateTask.getVariable("approver");
		String approverId = approver.getId();
		String candidateGroups = SystemConfig.systemVariables.getOrDefault("perf.activiti.candidate.groups", "");
		// 无审批人标识
		String ignoreAssignee = SystemConfig.systemVariables.getOrDefault("perf.activiti.ignoreAssignee", "无");
		String approverName = StringUtils.trimToNull(approver.getLastName());
		if ((approverId == null || "0".equals(approverId)) && approverName != null && candidateGroups.matches("(.*)\\b" + approverName + "\\b(.*)")) {
			Group group = identityService.createGroupQuery().groupType(approverName).singleResult();
			delegateTask.addCandidateGroup(group.getId());
		} else if (approverName!= null && ignoreAssignee.contains(approverName)) {
			delegateTask.setAssignee(approverName);
		} else {
			delegateTask.setAssignee(approverId == null ? "0" : String.valueOf(approverId));
		}
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
	public void appealCreateExecution(DelegateExecution delegateExecution) {
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
	public void appealCompleteTask(DelegateTask delegateTask) {
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
	public void confirmResultExecution(DelegateExecution delegateExecution) {
//		Integer summaryId = (Integer) delegateExecution.getVariable("summaryId");
//		PlanParticipantSummary participantSummary = new PlanParticipantSummary();
//		participantSummary.setId(summaryId);
//		participantSummary.setAppealTime(new Date());
//		participantSummary.setHasAppeal(2);
//		planParticipantSummaryService.updateByPrimaryKeySelective(participantSummary);
	}

	private PmWorkFlow getCurrentEntity(VariableScope variableScope) {
		PmWorkFlow pmWorkFlow = variableScope.getVariable("entity", PmWorkFlow.class);
		if (variableScope instanceof DelegateTask) {
			pmWorkFlow.setProcInstId(StringUtils.defaultIfBlank(pmWorkFlow.getProcInstId(),
					((DelegateTask) variableScope).getProcessInstanceId()));
		} else if (variableScope instanceof DelegateExecution) {
			pmWorkFlow.setProcInstId(StringUtils.defaultIfBlank(pmWorkFlow.getProcInstId(),
					((DelegateExecution) variableScope).getProcessInstanceId()));
		}
		pmWorkFlow = pmFlowService.decoratorEntity(pmWorkFlow);
		return pmWorkFlow;
	}
}
