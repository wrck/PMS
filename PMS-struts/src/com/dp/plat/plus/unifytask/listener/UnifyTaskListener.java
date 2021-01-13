package com.dp.plat.plus.unifytask.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.dp.plat.activiti.unifytask.entity.UnifyTask;
import com.dp.plat.activiti.unifytask.listener.AbstractUnifyTaskListener;
import com.dp.plat.activiti.unifytask.sender.UnifyTaskSender;
import com.dp.plat.activiti.unifytask.service.UnifyTaskService;
import com.dp.plat.activiti.unifytask.vo.UnifyDelegateTask;
import com.dp.plat.context.UserContext;
import com.dp.plat.plus.unifytask.util.MapUtil;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.UserUtil;

public class UnifyTaskListener extends AbstractUnifyTaskListener {

	@Autowired
	protected UnifyTaskService unifyTaskService;
	
	@Autowired
	private UserManageService userManageService;
	
	@Autowired
	private BasicDataService basicDataService;
	

	@Override
	public void notify(DelegateTask delegate) {
		super.notify(delegate);
	}

	@Override
	public void dispatchEvent(UnifyDelegateTask delegateTask, String taskEventName) {
		super.dispatchEvent(delegateTask, taskEventName);
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
		unifyTaskService.insertSelective(unifyTask);
	}

	@Override
	public void afterPush(UnifyTask unifyTask, UnifyTaskSender unifyTaskSender, String type) {
		super.afterPush(unifyTask, unifyTaskSender, type);
//		unifyTaskService.insertSelective(unifyTask);
		unifyTaskService.updateByPrimaryKeySelective(unifyTask);
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
			temp.setUpdateBy(UserContext.getUserContext().getUsername());
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
	public void pushUnifyTask(UnifyDelegateTask unifyDelegateTask, List<User> receiverUsers, String type, String state,
			String subState) {
		DelegateTask task = unifyDelegateTask.getDelegateTask();
		Map<String, Object> extParams = task .getVariablesLocal();
		if (extParams.isEmpty()) {
			extParams = task.getVariables();
		}
		List<User> fixedAssignees = getProcessTaskFixedAssignees(unifyDelegateTask, extParams);
		if (fixedAssignees != null && !fixedAssignees.isEmpty()) {
			receiverUsers = fixedAssignees;
		}
		super.pushUnifyTask(unifyDelegateTask, receiverUsers, type, state, subState);
	}

	@Override
	public UnifyDelegateTask createDelegateTask(DelegateTask task, String processDefinitionKey,
			String taskDefinitionKey, String taskEventName) {
		ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(task.getProcessDefinitionId()).singleResult();
		String processDefinitionName = processDefinition.getName(); // 流程定义的名称
		if (processDefinition != null) {
			processDefinitionKey = processDefinition.getKey(); // 流程定义的key
			taskDefinitionKey = task.getTaskDefinitionKey(); // 分配的代理
		}
		UnifyDelegateTask delegateTask = new UnifyDelegateTask(task, processDefinitionKey, taskDefinitionKey,
				taskEventName);
		delegateTask.setSender(UserContext.getUserContext().getUsername());
		
		// 初始化流程实例参数
		initProcessConfig(delegateTask);
		
		String formUrl = generateFormUrl(delegateTask);
		delegateTask.setFormUrl(formUrl);
		Map<String, Object> variables = task.getVariablesLocal();
		String[] approveKeys = new String[] {"approveStatus", "isPass", "result", "flowState", "evaluationResult", "projectProcessStatus"};
		String approveStatus = null;
		for (String key : approveKeys) {
			if (variables.containsKey(key)) {
				Object status = variables.get(key);
				if (status instanceof Boolean) {
					approveStatus = status != null ? Boolean.parseBoolean(String.valueOf(status)) ? UnifyTaskSender.STATUS_AGREE
							: UnifyTaskSender.STATUS_REJECT : null;
				} else if(status instanceof Integer) {
					approveStatus = status != null ? ((Integer) status).intValue() >= 0 ? UnifyTaskSender.STATUS_AGREE
							: UnifyTaskSender.STATUS_REJECT : null;
				}
				if (approveStatus != null) {
					break;
				}
			}
		}
		if(approveStatus == null && "TASK_COMPLETE".equals(taskEventName)) {
			approveStatus = UnifyTaskSender.STATUS_AGREE;
			task.setVariableLocal("approveStatus", 1);
		}
		delegateTask.setTitle(processDefinitionName + " -- " + task.getName());
		delegateTask.setApproveStatus(approveStatus);
		return delegateTask;
	}
	
	public List<User> getReceiverUser(DelegateTask task) {
		return getReceiverUser(task, null);
	}

	public List<User> getReceiverUser(DelegateTask task, Map<String, Object> extParams) {
		String assignee = task.getAssignee();
		List<User> receiverUsers = new ArrayList<>();
		
		if (extParams == null) {
			extParams = task.getVariablesLocal();
			if (extParams.isEmpty()) {
				extParams = task.getVariables();
			}
		}
		if (StringUtils.isNotBlank(assignee)) {
//			User userInfo = getUserByUserId(assignee, extParams);
//			if (userInfo != null) {
//				receiverUsers.add(userInfo);
//			}
			List<User> userInfos = getUsersByUserId(assignee, extParams);
			if (userInfos != null && !userInfos.isEmpty()) {
				receiverUsers.addAll(userInfos);
			}
		} 
		Set<IdentityLink> candidates = task.getCandidates();
		for (IdentityLink identityLink : candidates) {
			String userId = identityLink.getUserId();
			String groupId = identityLink.getGroupId();
			if (StringUtils.isNotBlank(userId)) {
				List<User> userInfos = getUsersByUserId(userId, extParams);
				if (userInfos != null && !userInfos.isEmpty()) {
					receiverUsers.addAll(userInfos);
				}
			}
			if (StringUtils.isNotBlank(groupId)) {
				List<User> userInfos = getUsersByGroupId(groupId, extParams);
				if (userInfos != null && !userInfos.isEmpty()) {
					receiverUsers.addAll(userInfos);
				}
			}
		}
		return receiverUsers;
	}

	@Override
	public User getUserByUserId(String userId) {
		return super.getUserByUserId(userId);
	}

	@Override
	public List<User> getUsersByGroupId(String groupId) {
		return super.getUsersByGroupId(groupId);
	}
	
	@Override
	public User getUserByUserId(String userId, Map<String, Object> extParams) {
//		User activiUser = super.getUserByUserId(userId, extParams);
		
		extParams = getRoleGroupMap(extParams);
//		Map<String, Object> roleGroupMap = getRoleGroupMap(extParams);
//		if (extParams == null) {
//			extParams = roleGroupMap;
//		} else {
//			extParams.putAll(roleGroupMap);
//		}
		User activiUser = null;
		if (activiUser == null) {
			if (extParams != null && extParams.containsKey(userId)) {
				userId = String.valueOf(MapUtil.getOrDefault(extParams, userId, userId));
			}
			if (extParams.containsKey(userId)) {
				userId = String.valueOf(extParams.get(userId));
			}
			com.dp.plat.data.bean.User user = userManageService.queryUserByUserName(userId);
			if (user != null) {
				activiUser = new UserEntity();
				activiUser.setId(user.getUsername());
				activiUser.setFirstName(user.getRealName());
				activiUser.setLastName(user.getUsername());
				activiUser.setPassword(user.getUsername());
			}
		}
		return activiUser;
	}
	
	public List<User> getUsersByUserId(String userId, Map<String, Object> extParams) {
		User activiUser = this.getUserByUserId(userId, extParams);
		List<User> activitiUsers = new ArrayList<User>();
		if (activiUser == null) {
			extParams = getRoleGroupMap(extParams);
//			Map<String, Object> roleGroupMap = getRoleGroupMap();
//			if (extParams == null) {
//				extParams = roleGroupMap;
//			} else {
////				extParams.putAll(roleGroupMap);
//				for (Entry<String, Object> roleGroup : roleGroupMap.entrySet()) {
//					if(!extParams.containsKey(roleGroup.getKey())) {
//						extParams.put(roleGroup.getKey(), roleGroup.getValue());
//					}
//				}
//			}
			String dpNo = (String) MapUtil.getOrDefault(extParams, "dpNo", "");
			userId = userId.replaceAll(dpNo, "");
			if (extParams != null && extParams.containsKey(userId)) {
				userId = String.valueOf(MapUtil.getOrDefault(extParams, userId, userId));
			}
			userId = userId.replaceAll(dpNo, "");
			if (extParams.containsKey(userId)) {
				userId = String.valueOf(extParams.get(userId));
			}
			String[] groupIds = StringUtils.split(userId, ",");
			for (String groupId : groupIds) {
				User userMember = getUserByUserId(userId, extParams);
				if (userMember != null) {
					activitiUsers.add(userMember);
				}
				List<User> groupMembers = getUsersByGroupId(groupId, extParams);
				activitiUsers.addAll(groupMembers);
			}
		} else {
			activitiUsers.add(activiUser);
		}
		return activitiUsers;
	}

	@Override
	public List<User> getUsersByGroupId(String groupId, Map<String, Object> extParams) {
//		List<User> members = super.getUsersByGroupId(groupId, extParams);
		List<User> members = new ArrayList<User>();
		// 获取有效的回访人员或工程人员
		Map<String, String> params = new HashMap<>();
		String newdpNo = null;
		if (extParams == null) {
			extParams = Collections.emptyMap();
		}
		String dpNo = null;
		boolean needCheckDep = false;
		if (extParams.containsKey("checkRoleDep")) {
			Collection checkRoles = (Collection) extParams.get("checkRoleDep");
			needCheckDep = checkRoles.contains(groupId);
		}
		if (needCheckDep) {
			dpNo = (String) MapUtil.getOrDefault(extParams, "dpNo", "NULL");
		}
		if (StringUtils.isNotBlank(dpNo)) {
			newdpNo = UserUtil.transferDepNo(dpNo);
		}
		params.put("roleid", String.valueOf(groupId));
		params.put("dpNo", dpNo);
		List<com.dp.plat.data.bean.User> userList = userManageService.queryUserWithRoleIdAndDpNo(params);
		
		if (userList.isEmpty()) {
			params.remove("dpNo");
			params.put("areaPower", dpNo);
			userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
		}
		// 如果没找到，则查找转换后的部门对应觉得人员
		if (userList.isEmpty() && StringUtils.isNotBlank(newdpNo) && !newdpNo.equals(dpNo)) {
			params.clear();
			params.put("roleid", String.valueOf(groupId));
			params.put("dpNo", newdpNo);
			userList = userManageService.queryUserWithRoleIdAndDpNo(params);
			
			if (userList.isEmpty()) {
				params.remove("dpNo");
				params.put("areaPower", newdpNo);
				userList = userManageService.queryUserWithRoleIdAndDpNoOrInAreaPower(params);
			}
		}
//		List<User> userAll = new ArrayList<User>(userList.size() + members.size());
//		userAll.addAll(members);
		for (com.dp.plat.data.bean.User user : userList) {
			UserEntity activiUser = new UserEntity();
			activiUser.setId(user.getUsername());
			activiUser.setFirstName(user.getRealName());
			activiUser.setLastName(user.getUsername());
			activiUser.setPassword(user.getUsername());
			members.add(activiUser);
		}
		return members;
	}

	@Override
	public void handleException(Throwable e) {
		super.handleException(e);
	}

	@Override
	public void setUnifyTaskSenders(List<Class<? extends UnifyTaskSender>> unifyTaskSenders) {
		super.setUnifyTaskSenders(unifyTaskSenders);
	}
	
	@Override
	public Map<String, Object> getUnifyTaskSenderInstancesConfig() {
		Map<String, Object> config = null;
		try {
			String pushConfig = basicDataService.querySysArg("sys.unify.task.push.config");
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
//		List<Class<? extends UnifyTaskSender>> unifyTaskSenders = super.getUnifyTaskSenders();
//		if (unifyTaskSenders == null) {
//			unifyTaskSenders = new ArrayList<Class<? extends UnifyTaskSender>>();
//		}
//		String pushConfig = basicDataService.querySysArg("sys.unify.task.push.config");
//		Map<String, Object> config = Collections.emptyMap();
//		if (StringUtils.isNotBlank(pushConfig)) {
//			config = JSON.parseObject(pushConfig, Map.class);
//			List<String> senders = (List<String>) config.get("senders");
//			if (senders != null) {
//				for (String sender : senders) {
//					try {
//						Class<?> senderClass = Class.forName(sender);
//						if (!unifyTaskSenders.contains(senderClass)) {
//							unifyTaskSenders.add((Class<? extends UnifyTaskSender>) senderClass);
//						}
//					} catch (ClassNotFoundException e) {
//					}
//				}
//			}
//		}
//		List<UnifyTaskSender> unifyTaskSenderInstances = new ArrayList<UnifyTaskSender>(unifyTaskSenders.size());
//		for (Class<? extends UnifyTaskSender> senderClass : unifyTaskSenders) {
//			UnifyTaskSender unifyTaskSender = null;
//			try {
//				Constructor<? extends UnifyTaskSender> constructor = senderClass.getConstructor(Map.class);
//				unifyTaskSender = constructor.newInstance(config.get(senderClass.getName()));
//			} catch (Exception e) {
//				try {
//					unifyTaskSender = senderClass.newInstance();
//				} catch (Exception e1) {
//				}
//			}
//			if (unifyTaskSender != null) {
//				unifyTaskSenderInstances.add(unifyTaskSender);
//			}
//		}
//		return unifyTaskSenderInstances;
	}

	private Map<String, Object> getRoleGroupMap() {
		return getRoleGroupMap(null);
	}
	
	private Map<String, Object> getRoleGroupMap(Map<String, Object> extParams) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cbRole", MessageUtil.ROLE_CALLBACKPER);
		map.put("callbackRole", MessageUtil.ROLE_CALLBACKPER);
		map.put("emRole", MessageUtil.ROLE_ENGINEEMANAGER);
		map.put("emlRole", MessageUtil.ROLE_ENGINEEMANAGER_LEADER);
		map.put("role_" + MessageUtil.ROLE_ENGINEEMANAGER_LEADER, MessageUtil.ROLE_ENGINEEMANAGER_LEADER);
		map.put("smRole", MessageUtil.ROLE_SERVICEMANAGER);
		map.put("profitSmRole", MessageUtil.ROLE_SERVICEMANAGER);
		map.put("pmRole", MessageUtil.ROLE_PROGRAMMANAGER);
		map.put("zrRole", MessageUtil.ROLE_AREA_LEADER);
		map.put("role_" + MessageUtil.ROLE_AREA_LEADER, MessageUtil.ROLE_AREA_LEADER);
		map.put("presalesStaffRole", MessageUtil.ROLE_PRESALES_STAFF);
		map.put("role_" + MessageUtil.ROLE_PRESALES_STAFF, MessageUtil.ROLE_PRESALES_STAFF);
		
		ArrayList<String> needCheckRoleList = new ArrayList<String>();
		Collections.addAll(needCheckRoleList, String.valueOf(MessageUtil.ROLE_SERVICEMANAGER),
				String.valueOf(MessageUtil.ROLE_PROGRAMMANAGER), String.valueOf(MessageUtil.ROLE_AREA_LEADER));
		map.put("checkRoleDep", needCheckRoleList);
		
		if (extParams != null && !extParams.isEmpty()) {
			for (Entry<String, Object> roleGroup : map.entrySet()) {
				if(!extParams.containsKey(roleGroup.getKey())) {
					extParams.put(roleGroup.getKey(), roleGroup.getValue());
				}
			}
		} else {
			extParams = map;
		}
		return extParams;
	}
	
	private List<User> getProcessTaskFixedAssignees(UnifyDelegateTask delegateTask, Map<String, Object> extParams) {
		List<User> receiverUsers = new ArrayList<User>();
		try {
			DelegateTask task = delegateTask.getDelegateTask();
			String processKey = delegateTask.getProcessKey();
			String taskKey = delegateTask.getTaskKey();
			Map<String, Map<String, Object>> configMap = delegateTask.getProcessConfig();
			if (configMap == null) {
				configMap = initProcessConfig(delegateTask);
			}
			Map<String, Object> processConfig = configMap.get(processKey);
			if (processConfig != null) {
				// 判断是否有生效日期，避免生效日期之前的流程无法取消待办
				Long effectiveFrom = (Long) processConfig.get("effectiveFrom");
				if (effectiveFrom != null && effectiveFrom > task.getCreateTime().getTime()) {
					return receiverUsers;
				}
				Map<String, Object> taskAssignees = (Map<String, Object>) MapUtil.getOrDefault(processConfig, "taskAssignees", Collections.emptyMap());
				Collection<String> taskAssignee = (Collection<String>) MapUtil.getOrDefault(taskAssignees, taskKey, null);
				if (taskAssignee != null && !taskAssignee.isEmpty()) {
					for (String assignee : taskAssignee) {
						List<User> userInfos = getUsersByUserId(assignee, extParams);
						if (userInfos != null && !userInfos.isEmpty()) {
							receiverUsers.addAll(userInfos);
						}
					}
				}
			}
		} catch (Exception e) {
			this.handleException(e);
		}
		return receiverUsers;
	}
	
	private String generateFormUrl(UnifyDelegateTask delegateTask) {
		String defaultUrl = "/module/Workspace!task.action?extObjId=%s";
		String formUrl = defaultUrl;
		try {
			DelegateTask task = delegateTask.getDelegateTask();
			String processKey = delegateTask.getProcessKey();
			String taskKey = delegateTask.getTaskKey();
			Map<String, Map<String, Object>> configMap = delegateTask.getProcessConfig();
			if (configMap == null) {
				configMap = initProcessConfig(delegateTask);
			}
			Map<String, Object> processConfig = configMap.get(processKey);
			if (processConfig != null) {
				String processUrl = (String) MapUtil.getOrDefault(processConfig, "processUrl", MapUtil.getOrDefault(processConfig, "defaultUrl", defaultUrl));
				Map<String, Object> taskUrls = (Map<String, Object>) MapUtil.getOrDefault(processConfig, "taskUrls", Collections.emptyMap());
				String taskUrl = (String) MapUtil.getOrDefault(taskUrls, taskKey, processUrl);
				Object objectId = task.getVariable((String) processConfig.get("objectId"));
				if (objectId != null) {
					boolean needEncodeBase64 = Boolean.TRUE.equals(processConfig.get("needEncodeBase64"));
					if (needEncodeBase64) {
						objectId = Base64Util.EncodeBase64(objectId);
					}
					formUrl = String.format(taskUrl, objectId);
				} else {
					formUrl = processUrl;
				}
			}
		} catch (Exception e) {
			this.handleException(e);
		}
		return formUrl;
	}

	private Map<String, Map<String, Object>> initProcessConfig(UnifyDelegateTask delegateTask) {
		Map<String, Map<String, Object>> configMap = new HashMap<String, Map<String,Object>>();
		try {
			String config = basicDataService.querySysArg("sys.unify.task.push.url.config");
			if (StringUtils.isBlank(config)) {
				config = "{}";
			}
			configMap = JSON.parseObject(config, Map.class);
			delegateTask.setProcessConfig(configMap);
		} catch (Exception e) {
			this.handleException(e);
		}
		return configMap;
	}
}
