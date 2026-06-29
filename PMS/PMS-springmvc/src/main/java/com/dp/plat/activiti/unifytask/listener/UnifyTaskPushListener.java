package com.dp.plat.activiti.unifytask.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dp.plat.activiti.unifytask.entity.UnifyTask;
import com.dp.plat.activiti.unifytask.sender.UnifyTaskSender;
import com.dp.plat.activiti.unifytask.service.IUnifyTaskService;
import com.dp.plat.activiti.unifytask.vo.UnifyDelegateTask;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.SystemLogUtil;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;

/**
 * 统一待办任务推送监听器
 * 
 * @author w02611
 *
 */
public class UnifyTaskPushListener extends AbstractUnifyTaskActivitiEventListener {

	@Lazy
	@Autowired
	protected RepositoryService repositoryService;

	@Lazy
	@Autowired
	protected RuntimeService runtimeService;

	@Lazy
	@Autowired
	protected TaskService taskService;

	@Lazy
	@Autowired
	protected IdentityService identityService;

	@Autowired
	protected IUserService userService;

	@Autowired
	protected IUnifyTaskService unifyTaskService;

	@Override
	public void onEvent(ActivitiEvent event) {
		super.onEvent(event);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void dispatchEvent(UnifyDelegateTask delegateTask, ActivitiEventType eventEnum, String taskEventName) {
		super.dispatchEvent(delegateTask, eventEnum, taskEventName);
	}
	
	@Override
	public void beforePush(UnifyDelegateTask delegateTask, String type) {
		super.beforePush(delegateTask, type);
		DelegateTask task = delegateTask.getDelegateTask();
		UnifyTask temp = new UnifyTask();
		temp.setOriginTaskId(task.getId());
		temp.setProcInstId(task.getProcessInstanceId());
		temp.setCreateTime(new Date());
		temp.setLatest(false);
		try {
			temp.setUpdateBy(UserContext.getCurrentUser().getUserName());
		} catch (Exception e) {
			temp.setUpdateBy("sys");
		}
		unifyTaskService.updateBySelective(temp);
	}

	@Override
	public void afterPush(UnifyDelegateTask delegateTask, String type) {
		super.afterPush(delegateTask, type);
	}

	@Override
	public void beforePush(UnifyTask unifyTask, UnifyTaskSender unifyTaskSender, String type) {
		super.beforePush(unifyTask, unifyTaskSender, type);
//		UnifyTask temp = new UnifyTask();
//		temp.setOriginTaskId(unifyTask.getOriginTaskId());
//		temp.setProcInstId(unifyTask.getProcInstId());
//		temp.setCreateTime(unifyTask.getCreateTime());
//		temp.setLatest(false);
//		try {
//			temp.setUpdateBy(UserContext.getCurrentUser().getUserName());
//		} catch (Exception e) {
//			temp.setUpdateBy("sys");
//		}
//		unifyTaskService.updateBySelective(temp);
		unifyTask.setLatest(false);
		unifyTaskService.insertSelective(unifyTask);
	}

	@Override
	public void afterPush(UnifyTask unifyTask, UnifyTaskSender unifyTaskSender, String type) {
		super.afterPush(unifyTask, unifyTaskSender, type);
//		unifyTaskService.insertSelective(unifyTask);
		unifyTask.setLatest(true);
		unifyTaskService.updateByPrimaryKeySelective(unifyTask);
	}

	@Override
	public void pushUnifyTask(UnifyDelegateTask unifyDelegateTask, List<User> receiverUsers, String type, String state,
			String subState) {
		super.pushUnifyTask(unifyDelegateTask, receiverUsers, type, state, subState);
	}

	@Override
	public UnifyDelegateTask createDelegateTask(DelegateTask task, String processDefinitionKey,
			String taskDefinitionKey, Enum<ActivitiEventType> eventEnum) {
		// 获取任务工作流记录
		PmWorkFlow pmWorkFlow = (PmWorkFlow) task.getVariable("entity");
		// 如果task关联的不存在，则查找流程实例的
		if (pmWorkFlow == null) {
			pmWorkFlow = (PmWorkFlow) runtimeService.getVariable(task.getProcessInstanceId(), "entity");
		}
		UnifyDelegateTask delegateTask = new UnifyDelegateTask(task, processDefinitionKey, taskDefinitionKey,
				eventEnum.name());
		delegateTask.setFormUrl(task.getFormKey());
		String sender = Authentication.getAuthenticatedUserId();
		sender = StringUtils.defaultIfBlank(sender, String.valueOf(UserContext.getCurrentPrincipal().getUserInfoId()));
		delegateTask.setSender(sender);
		// 自定义待办标题模板
		Map<String, Object> dataTitleTemplates = JSON.parseObject(SystemConfig.systemVariables.getOrDefault("sys.unify.task.push.data.tiltle.templates", "{}"), HashMap.class);
		Map<String, Object> dataTitleTemplate = (Map<String, Object>) dataTitleTemplates.getOrDefault(pmWorkFlow.getDataType(), Collections.emptyMap());
		String title = StringUtils.defaultIfBlank(task.getDescription(), task.getName());
		if (!dataTitleTemplate.isEmpty()) {
		    String template = (String) dataTitleTemplate.getOrDefault("template", "");
		    String separator = (String) dataTitleTemplate.getOrDefault("separator", "");
		    String temp = SystemLogUtil.format(template, pmWorkFlow.getEntity());
		    if (StringUtils.isNotBlank(separator)) {
		        List<String> splits = Arrays.asList(StringUtils.splitByWholeSeparator(temp, separator));
		        splits = splits.stream().filter(split -> {
		            return StringUtils.isNotBlank(separator);
		        }).collect(Collectors.toList());
		        temp = StringUtils.join(splits, separator);
		    }
		    title += " -- " + temp;
		}
		delegateTask.setTitle(title);
		Object isPass = task.getVariableLocal("isPass");
		delegateTask.setApproveStatus(
				isPass != null ? Boolean.parseBoolean(String.valueOf(isPass)) ? UnifyTaskSender.STATUS_AGREE
						: UnifyTaskSender.STATUS_REJECT : null);
		return delegateTask;
	}

	public List<User> getReceiverUser(DelegateTask task) {
		String assignee = task.getAssignee();
		String ignoreAssignee = SystemConfig.systemVariables.getOrDefault("perf.activiti.ignoreAssignee", "无");
		if (ignoreAssignee.equals(assignee)) {
			return Collections.emptyList();
		}
		List<User> receiverUsers = new ArrayList<>();
		if (StringUtils.isNotBlank(assignee)) {
			User userInfo = identityService.createUserQuery().userId(assignee).singleResult();
			if (userInfo != null) {
				receiverUsers.add(userInfo);
			}
		}
		Set<IdentityLink> candidates = task.getCandidates();
		for (IdentityLink identityLink : candidates) {
			String userId = identityLink.getUserId();
			String groupId = identityLink.getGroupId();
			if (StringUtils.isNotBlank(userId)) {
				User userInfo = getUserByUserId(userId);
				if (userInfo != null) {
					receiverUsers.add(userInfo);
				}
			}
			if (StringUtils.isNotBlank(groupId)) {
				List<User> userInfos = getUsersByGroupId(groupId);
				if (userInfos != null && !userInfos.isEmpty()) {
					receiverUsers.addAll(userInfos);
				}
			}
		}
		Set<String> userIds = new HashSet<String>();
		for (Iterator<User> iterator = receiverUsers.iterator(); iterator.hasNext();) {
			User receiverUser = iterator.next();
			if (userIds.contains(receiverUser.getId())) {
				iterator.remove();
			} else {
				userIds.add(receiverUser.getId());
			}
		}
		return receiverUsers;
	}

	@Override
	public void handleException(Throwable e) {
		ExceptionHandler.insertException(e);
	}

	public User getUserByUserId(String userId) {
		return identityService.createUserQuery().userId(userId).singleResult();
	}

	public List<User> getUsersByGroupId(String groupId) {
		return identityService.createUserQuery().memberOfGroup(groupId).list();
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}
	
	@Override
	public Map<String, Object> getUnifyTaskSenderInstancesConfig() {
		Map<String, Object> config = null;
		try {
			String pushConfig = SystemConfig.systemVariables.getOrDefault("sys.unify.task.push.config", "{}");
			if (StringUtils.isNotBlank(pushConfig)) {
				config = JSON.parseObject(pushConfig, Map.class);
			}
		} catch (Exception e) {
			handleException(e);
		}
		if (config == null) {
			config = Collections.emptyMap();
		}
		return config;
	}

	@Override
	public List<UnifyTaskSender> getUnifyTaskSenderInstances() {
		return super.getUnifyTaskSenderInstances();
	}

}
