/**
 * 
 */
package com.dp.plat.activiti.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.el.UelExpressionCondition;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.dp.plat.activiti.entity.Constants;
import com.dp.plat.activiti.service.IRuntimePageService;
import com.dp.plat.activiti.vo.ActivityVo;

/**
 * @author w02611
 *
 */
@Service("runtimePageService")
public class RuntimePageService implements IRuntimePageService {
	private static final Logger logger = LoggerFactory.getLogger(RuntimePageService.class);

	@Resource
	private HistoryService historyService;

	@Resource
	private RuntimeService runtimeService;

	@Resource
	private RepositoryService repositoryService;

	@Resource
	private IdentityService identityService;

	@Resource
	private TaskService taskService;
	
	@Override
	public List<ActivityVo> getActivityList(Collection<String> processInstanceIdSet) {
		List<ActivityVo> voList = new ArrayList<>();
		for (String processInstanceId : processInstanceIdSet) {
			List<ActivityVo> activityList = this.getActivityList(processInstanceId);
			voList.addAll(activityList);
		}
		return voList;
	}

	@Override
	public List<ActivityVo> getActivityList(String processInstanceId) {
		// 已执行的流程节点
//		List<HistoricActivityInstance> historicActivityInstanceList = historyService
//				.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
//				.orderByHistoricActivityInstanceStartTime().asc().list();
		// FIXME 目标承诺驳回时，会数据重复，RES.TASK_ID_ = TSK.ID_使用该条件，会导致无审批人的情况下，ASSIGNEE赋值出错的问题；
		// 使用AND RES.ID_ + 1 = TSK.ID_条件暂时解决该问题，待评估
		List<HistoricActivityInstance> historicActivityInstanceList = historyService
				.createNativeHistoricActivityInstanceQuery()
				.sql("SELECT CASE WHEN TSK.ID_ IS NULL THEN RES.TASK_ID_ ELSE TSK.ID_ END AS TASK_ID_, CASE WHEN TSK.ID_ IS NULL THEN RES.ASSIGNEE_ ELSE TSK.ASSIGNEE_ END AS ASSIGNEE_, IFNULL(RES.END_TIME_, TSK.END_TIME_) AS END_TIME_, RES.* FROM ACT_HI_ACTINST RES LEFT JOIN `act_hi_taskinst` TSK ON RES.`ACT_ID_` = TSK.TASK_DEF_KEY_ AND RES.`PROC_INST_ID_` = TSK.PROC_INST_ID_ AND RES.`EXECUTION_ID_` = TSK.EXECUTION_ID_ AND RES.ID_ + 1 = TSK.ID_ WHERE RES.PROC_INST_ID_ = #{procInstId} ORDER BY START_TIME_ ASC ")
				.parameter("procInstId", processInstanceId).list();
		// 活动的节点ID
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		/* String activityId=processInstance.getActivityId(); */
		List<String> activeIds = new ArrayList<>();
		ProcessDefinitionEntity processDefinition = null;
		// 已完成后processInstance为null
		String startUserId = null;
		if (processInstance != null) {
			activeIds = runtimeService.getActiveActivityIds(processInstanceId);
			processDefinition = (ProcessDefinitionEntity) repositoryService
					.getProcessDefinition(processInstance.getProcessDefinitionId());
		} else {
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			if (historicProcessInstance != null) {
				processDefinition = (ProcessDefinitionEntity) repositoryService
						.getProcessDefinition(historicProcessInstance.getProcessDefinitionId());
				startUserId = historicProcessInstance.getStartUserId();
			}
		}
		List<ActivityImpl> activityList;
		if (processDefinition != null) {
			activityList = processDefinition.getActivities();
		} else {
			activityList = new ArrayList<ActivityImpl>(0);
		}

		List<ActivityVo> voList = new ArrayList<>();
		for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
			// 过滤掉非用户任务
			if (!historicActivityInstance.getActivityType().equals("userTask")
					&& !historicActivityInstance.getActivityType().equals("startEvent"))
				continue;
			ActivityVo vo = new ActivityVo();
			BeanUtils.copyProperties(historicActivityInstance, vo);
			if (historicActivityInstance.getActivityType().equals("startEvent")) {
				String userId = startUserId != null ? startUserId : getStartUserId(processInstance);
				if (userId != null) {
					User user = identityService.createUserQuery().userId(userId).singleResult();
					vo.setAssigneeName(user != null ? user.getFirstName() : userId);
				}
			} else {
				if (!StringUtils.isEmpty(vo.getAssignee())) {
					vo.setAssigneeName(getUserNamesByUserIds(vo.getAssignee()));
				} else {
					// 未指定审批审批人获取
					vo.setAssigneeName(getCandidateUserNames(getActivity(historicActivityInstance, activityList),
							processInstanceId, historicActivityInstance.getExecutionId(),
							historicActivityInstance.getTaskId()));
				}
			}
			// 节点状态
			if (vo.getEndTime() != null) {
				vo.setActivityState(Constants.STATE_DONE);
			} else {
				vo.setActivityState(Constants.STATE_DOING);
			}

			// 获取审批结果和审批意见
			Map<String, String> approveMap = getApproveMap(historicActivityInstance);
			if (!approveMap.isEmpty()) {
				vo.setApproved(approveMap.get(Constants.APPROVE_RESULT));
				vo.setSuggestion(approveMap.get(Constants.APPROVE_SUGGESTION));
			}
			if (Constants.STATE_TERMINATE.equals(vo.getApproved())) {
				vo.setActivityState(Constants.STATE_TERMINATE);
			}
			voList.add(vo);
		}

		// 活动节点对象
		List<ActivityImpl> nextActivities = new ArrayList<>();
		/*
		 * for (ActivityImpl activity : activityList) { for (String activeId :
		 * activeIds) { if (activity.getId().equals(activeId)) {
		 * nextActivities.add(activity); break; } } }
		 */
		PvmActivity curActivity = null;
		// 另一种写法
		for (String activeId : activeIds) {
			ActivityImpl activityImpl = processDefinition.findActivity(activeId);
			if (activityImpl != null) {
				nextActivities.add(activityImpl);
				curActivity = activityImpl;
			}
		}

		// 下一步节点(可能已经走过的节点：回退节点)
		/*
		 * for (ActivityImpl nextActivity : nextActivities) {
		 * List<PvmTransition> transitions =
		 * nextActivity.getOutgoingTransitions(); for (PvmTransition transition
		 * : transitions) { List<PvmActivity> activities =
		 * findNextUserTask(transition); if (!activities.isEmpty()) { for
		 * (PvmActivity activity : activities) { if
		 * (findInDoneActivityList(historicActivityInstanceList, activity)) {
		 * ActivityVo vo = new ActivityVo(); vo.setId(activity.getId());
		 * vo.setActivityId(activity.getId());
		 * vo.setActivityName(activity.getProperty("name").toString());
		 * vo.setAssigneeName(getCandidateUserNames((ActivityImpl) activity,
		 * processInstanceId)); vo.setActivityState(Constants.STATE_TODO);
		 * voList.add(vo); curActivity = activity; } } } } }
		 */
		if (processInstance != null)// 运行中
			findNextActivity(voList, processInstanceId, curActivity);
		// 尚未执行的流程节点
		/*
		 * if (processInstance != null) { for (ActivityImpl activity :
		 * activityList) { boolean done = false; for (ActivityVo activityVo :
		 * voList) { if (activity.getId().equals(activityVo.getActivityId())) {
		 * done = true; break; } } if (!done &&
		 * activity.getProperty("type").equals("userTask")) { ActivityVo vo =
		 * new ActivityVo(); vo.setId(activity.getId());
		 * vo.setActivityName(activity.getProperty("name").toString());
		 * vo.setAssigneeName(getCandidateUserNames(activity,
		 * processInstanceId)); vo.setActivityState(Constants.STATE_TODO);
		 * voList.add(vo); } } }
		 */
		return voList;
	}

	/**
	 * 从当前节点出发
	 * 
	 * @param voList
	 * @param processInstanceId
	 * @param curActivity
	 */
	public void findNextActivity(List<ActivityVo> voList, String processInstanceId, PvmActivity curActivity) {
		// 一条道走到黑（结束）
		List<PvmTransition> nextTrans = curActivity.getOutgoingTransitions();
		for (PvmTransition nextTran : nextTrans) {
			Object flowName = nextTran.getProperty("name");
			PvmActivity activity = nextTran.getDestination();
			if ("userTask".equals(activity.getProperty("type").toString())) {
				if (flowName != null && isInApprovedText(Constants.APPROVED_PASSED, flowName.toString())) {
					ActivityVo vo = new ActivityVo();
					vo.setId(activity.getId());
					vo.setActivityName(activity.getProperty("name").toString());
					vo.setAssigneeName(getCandidateUserNames((ActivityImpl) activity, processInstanceId));
					vo.setActivityState(Constants.STATE_TODO);
					voList.add(vo);
					findNextActivity(voList, processInstanceId, activity);
				} else if (flowName == null || !isInApprovedText(Constants.APPROVED_REJECT, flowName.toString())) {
					// 条件路径
					Object conditionText = nextTran.getProperty("conditionText");
					if (conditionText != null) {
						boolean targetTask = isTargetTask(conditionText.toString(), processInstanceId, nextTran);
						if (targetTask) {
							ActivityVo vo = new ActivityVo();
							vo.setId(activity.getId());
							vo.setActivityName(activity.getProperty("name").toString());
							vo.setAssigneeName(getCandidateUserNames((ActivityImpl) activity, processInstanceId));
							vo.setActivityState(Constants.STATE_TODO);
							voList.add(vo);
							findNextActivity(voList, processInstanceId, activity);
						}
					}
				}
			} else {
				findNextActivity(voList, processInstanceId, activity);
			}
		}
	}

	/**
	 * 获取历史审批结果和审批意见
	 *
	 * @param activityInstance
	 *            历史任务节点
	 * @return
	 */
	public Map<String, String> getApproveMap(HistoricActivityInstance activityInstance) {
		// 审批结果和审批意见为Local变量
		Map<String, String> map = new HashMap<>();
		if (StringUtils.isEmpty(activityInstance.getTaskId()))
			return map;
		List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(activityInstance.getProcessInstanceId()).taskId(activityInstance.getTaskId()).list();
		// .processInstanceId(activityInstance.getProcessInstanceId()).executionId(activityInstance.getExecutionId()).list();
		for (HistoricVariableInstance variableInstance : variableInstances) {
			if (variableInstance.getVariableName().equals("isPass")) {
				map.put(Constants.APPROVE_RESULT, variableInstance.getValue().toString());
			} else if (Constants.APPROVE_RESULT.equals(variableInstance.getVariableName())){
				map.put(Constants.APPROVE_RESULT, variableInstance.getValue().toString());
			} else {
				// map.put(Constants.APPROVE_SUGGESTION,
				// variableInstance.getValue().toString());
			}
		}
		List<String> suggestions = new ArrayList<>();
		List<Comment> comments = this.taskService.getTaskComments(activityInstance.getTaskId(),
				Constants.COMMENT_TYPE_COMMENT);
		Date commentTime = new Date(0);
//		SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
		for (Comment comment : comments) {
//			String timeStr = dateFormat.format(comment.getTime());
			String commentStr =  comment.getFullMessage();
			if (commentTime.after(comment.getTime())) {
				suggestions.add(0, commentStr);
			} else {
				suggestions.add(commentStr);
			}
			commentTime = comment.getTime();
		}
		String suggestion = StringUtils.join(suggestions, "<br>");
		map.put(Constants.APPROVE_SUGGESTION, suggestion);
		return map;
	}

	/**
	 * 获取尚未执行的节点的可能执行人姓名，以逗号分隔 需要使用命令模式动态执行表达式 不然报lazy load expression out of
	 * activiti异常
	 *
	 * @param activity
	 *            流程定义的用户活动节点
	 * @param processInstanceId
	 *            流程实例ID
	 * @return 可能执行人姓名
	 */
	public String getCandidateUserNames(final ActivityImpl activity, final String processInstanceId) {
		return this.getCandidateUserNames(activity, processInstanceId, null, null);
	}

	/**
	 * 获取尚未执行的节点的可能执行人姓名，以逗号分隔 需要使用命令模式动态执行表达式 不然报lazy load expression out of
	 * activiti异常
	 *
	 * @param activity
	 *            流程定义的用户活动节点
	 * @param processInstanceId
	 *            流程实例ID
	 * @param executionId
	 *            执行ID
	 * @return 可能执行人姓名
	 */
	public String getCandidateUserNames(final ActivityImpl activity, final String processInstanceId,
			final String executionId) {
		return this.getCandidateUserNames(activity, processInstanceId, executionId, null);
	}

	/**
	 * 获取尚未执行的节点的可能执行人姓名，以逗号分隔 需要使用命令模式动态执行表达式 不然报lazy load expression out of
	 * activiti异常
	 *
	 * @param activity
	 *            流程定义的用户活动节点
	 * @param processInstanceId
	 *            流程实例ID
	 * @param executionId
	 *            执行ID
	 * @param taskId
	 *            taskId
	 * @return 可能执行人姓名
	 */
	public String getCandidateUserNames(final ActivityImpl activity, final String processInstanceId,
			final String executionId, final String taskId) {
		String result = "待定";
		if (activity == null) {
			return result;
		}
		try {
		    result = ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new Command<String>() {
				@Override
				public String execute(CommandContext commandContext) {
					String retNames = "";
					ExecutionEntity execution = null;
					// ExecutionEntity execution = (ExecutionEntity)
					// runtimeService.createExecutionQuery()
					// .processInstanceId(processInstanceId).singleResult();
	//				if (executionId == null) {
	//					return retNames;
	//				}
	//				List<Execution> executionList = runtimeService.createExecutionQuery()
	//						.processInstanceId(processInstanceId).executionId(executionId).orderByProcessInstanceId().desc()
	//						.list();
					ExecutionQuery executionQuery = runtimeService.createExecutionQuery();
					executionQuery.processInstanceId(processInstanceId);
					if (StringUtils.isNotBlank(executionId)) {
						executionQuery.executionId(executionId);
					}
					List<Execution> executionList = executionQuery.orderByProcessInstanceId().desc().list();
					if (!executionList.isEmpty()) {
						execution = (ExecutionEntity) executionList.get(0);
					}
					TaskDefinition taskDefinition = (TaskDefinition) activity.getProperties().get("taskDefinition");
					if (taskDefinition == null)
						return retNames;
	
					// 代理人/审批人
					String assignee = null;
					if (taskDefinition.getAssigneeExpression() != null) {
						try {
							assignee = (String) taskDefinition.getAssigneeExpression().getValue(execution);
						} catch (Exception ex) {
							logger.error("获取受理人出错：" + ex.getMessage());
							assignee = null;
						}
						retNames = StringUtils.isNotBlank(assignee) ? getUserNamesByUserIds(assignee) : "待定";
					}
					// 委托人,同受理人同一人的情况下不显示
					if (taskDefinition.getOwnerExpression() != null) {
						String owner;
						try {
							owner = ((String) taskDefinition.getOwnerExpression().getValue(execution));
						} catch (Exception ex) {
							logger.error("获取委托人出错：" + ex.getMessage());
							owner = null;
						}
						if (assignee != null && !assignee.equals(owner)) {
							retNames = retNames + "(委托人:" + (StringUtils.isNotBlank(owner) ? getUserNamesByUserIds(owner) : "待定)");
						}
					}
	
					if (!StringUtils.isEmpty(retNames))
						return retNames;
					// 候选组
					if (!taskDefinition.getCandidateGroupIdExpressions().isEmpty()) {
						List<String> groupIdList = new ArrayList<String>();
						for (Expression groupIdExpr : taskDefinition.getCandidateGroupIdExpressions()) {
							Object value;
							try {
								value = groupIdExpr.getValue(execution);
							} catch (Exception ex) {
								logger.error("获取候选组出错：" + ex.getMessage());
								value = null;
							}
							if (value != null) {
								if (value instanceof String) {
									groupIdList.add(value.toString());
								} else if (value instanceof Collection) {
									groupIdList.addAll((Collection<String>) value);
								}
							}
						}
						if (!groupIdList.isEmpty()) {
							String[] groupIdArr = getStringArr(groupIdList.toArray());
							return getUserNamesByGroupIds(StringUtils.join(groupIdArr, ","));
						} else {
							return "待定";
						}
	
					} else if (!taskDefinition.getCandidateUserIdExpressions().isEmpty()) {
						List<String> userIdList = new ArrayList<String>();
						for (Expression userIdExpr : taskDefinition.getCandidateUserIdExpressions()) {
							Object value;
							try {
								value = userIdExpr.getValue(execution);
							} catch (Exception ex) {
								logger.error("获取候选人出错：" + ex.getMessage());
								value = null;
							}
							if (value != null) {
								if (value instanceof String) {
									userIdList.add((String) value);
								} else if (value instanceof Collection) {
									userIdList.addAll((Collection<String>) value);
								}
							}
						}
						if (!userIdList.isEmpty()) {
							String[] userIdArr = getStringArr(userIdList.toArray());
							return getUserNamesByUserIds(StringUtils.join(userIdArr, ","));
						} else {
							return "待定";
						}
					} else {
						if (execution != null) {
							List<TaskEntity> tasks = execution.getTasks();
							for (TaskEntity taskEntity : tasks) {
								if (taskEntity.getId().equals(taskId)) {
									List<IdentityLinkEntity> identityLinks = taskEntity.getIdentityLinks();
									if (identityLinks != null && !identityLinks.isEmpty()) {
										IdentityLinkEntity linkEntity = identityLinks.get(0);
										if (StringUtils.isNotBlank(linkEntity.getGroupId())) {
											Group group = identityService.createGroupQuery().groupId(linkEntity.getGroupId())
													.singleResult();
											if (group != null) {
												return group.getName();
											}
										}
									}
								}
							}
						}
						
						if (taskId != null) {
							List<HistoricIdentityLink> historicIdentityLinks = historyService
									.getHistoricIdentityLinksForTask(taskId);
							if (historicIdentityLinks != null && !historicIdentityLinks.isEmpty()) {
								HistoricIdentityLink linkEntity = historicIdentityLinks.get(0);
								if (StringUtils.isNotBlank(linkEntity.getGroupId())) {
									Group group = identityService.createGroupQuery().groupId(linkEntity.getGroupId())
											.singleResult();
									if (group != null) {
										return group.getName();
									}
								}
							}
						}
					}
					return retNames;
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 历史节点对应的流程定义节点
	 *
	 * @param activityInstance
	 *            历史节点
	 * @param activities
	 *            流程定义节点
	 * @return
	 */
	public ActivityImpl getActivity(HistoricActivityInstance activityInstance, List<ActivityImpl> activities) {
		for (ActivityImpl activity : activities) {
			if (activity.getId().equals(activityInstance.getActivityId())) {
				return activity;
			}
		}
		return null;
	}

	@Override
	public String getStartUserId(ProcessInstance processInstance) {
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processInstance.getProcessDefinitionId());
		String initiator = (String) processDefinition.getProperty(BpmnParse.PROPERTYNAME_INITIATOR_VARIABLE_NAME);
		if (initiator != null) {
			return (String) runtimeService.getVariable(processInstance.getProcessInstanceId(), initiator);
		}
		return null;
	}

	@Override
	public String getStartUserId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		return historicProcessInstance.getStartUserId();
	}

	private String getUserNamesByUserIds(String userId) {
		User user = identityService.createUserQuery().userId(userId).singleResult();
		String name = userId;
		if (user != null) {
			List<String> names = new ArrayList<>(2);
			if (StringUtils.isNotBlank(user.getLastName())) {
				names.add(user.getLastName());
			}
			if (StringUtils.isNotBlank(user.getFirstName())) {
				names.add(user.getFirstName());
			}
			name = StringUtils.join(names, "-");
		}
		return user != null ? name : userId;
	}

	/**
	 * @param join
	 * @return
	 */
	protected String getUserNamesByGroupIds(String groupIds) {
		String[] groupIdArr = groupIds.split(",");
		List<String> userNames = new ArrayList<>();
		for (String groupId : groupIdArr) {
			List<User> users = identityService.createUserQuery().memberOfGroup(groupId).list();
			for (User user : users) {
				userNames.add(user.getFirstName());
			}
		}
		return StringUtils.join(userNames, ",");
	}

	/**
	 * 节点上的文字是否在可选文字中
	 *
	 * @param type
	 *            同意/拒绝
	 * @param text
	 *            节点文字
	 * @return
	 */
	public boolean isInApprovedText(String type, String text) {
		if (StringUtils.isBlank(text)) {
			return false;
		}
		if (Constants.APPROVED_PASSED.equals(type)) {
			for (String s : Constants.APPROVED_PASSED_TEXT) {
				if (text.contains(s) && !text.contains("不" + s)) {
					return true;
				}
			}
			return false;
		} else if (Constants.APPROVED_REJECT.equals(type)) {
			for (String s : Constants.APPROVED_REJECT_TEXT) {
				if (text.contains(s)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * TODO 局部变量判断 判断当前是否是合适的路径
	 *
	 * @param expressionText
	 *            表达式
	 * @param processInstanceId
	 *            实例ID
	 * @return true符合条件条件的路径
	 */
	public boolean isTargetTask(final String expressionText, String processInstanceId, final PvmTransition transition) {

		final ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
				.executionId(processInstanceId).singleResult();
		Boolean result = ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new Command<Boolean>() {
			@Override
			public Boolean execute(CommandContext commandContext) {
				UelExpressionCondition flowCondition = (UelExpressionCondition) transition.getProperty("condition");
				boolean evel_ret = flowCondition.evaluate(transition.getId(), execution);
//				boolean evel_ret = flowCondition.evaluate(execution);
				return evel_ret;
			}
		});
		return result;
		// TODO 另一种写法 自定义juel解析
		/*
		 * ExpressionFactory factory = new ExpressionFactoryImpl();
		 * SimpleContext context = new SimpleContext(); Map<String, Object>
		 * variables = runtimeService.getVariables(processInstanceId); for
		 * (String key : variables.keySet()) { context.setVariable(key,
		 * factory.createValueExpression(variables.get(key),
		 * variables.get(key).getClass())); } try { ValueExpression e =
		 * factory.createValueExpression(context, expressionText,
		 * boolean.class); return (boolean) e.getValue(context); } catch
		 * (Exception ex) { return false; }
		 */
	}

	public String[] getStringArr(Object[] objArr) {
		String[] strArr = new String[objArr.length];
		for (int i = 0; i < objArr.length; i++) {
			strArr[i] = objArr[i].toString();
		}
		return strArr;
	}
}
