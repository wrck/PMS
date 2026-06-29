package com.dp.plat.plus.unifytask.sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.identity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;

import com.dp.plat.activiti.unifytask.entity.UnifyTask;
import com.dp.plat.activiti.unifytask.sender.AbstractUnifyTaskSender;
import com.dp.plat.activiti.unifytask.vo.UnifyDelegateTask;
import com.dp.plat.activiti.unifytask.vo.UnifyTaskResult;
import com.dp.plat.context.SpringContext;
import com.dp.plat.plus.unifytask.util.MapUtil;
import com.dp.plat.plus.unifytask.vo.SeeyonTask;
import com.dp.plat.service.BasicDataService;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;

/**
 * 致远统一待办任务工具类
 * 
 * @author user
 *
 */
public class UnifyTask2SeeyonSender extends AbstractUnifyTaskSender {

	/**
	 * 同意已办
	 */
	public static final Integer APPROVE_AGREE = 0;
	/**
	 * 不同意已办
	 */
	public static final Integer APPROVE_DISAGREE = 1;
	/**
	 * 取消
	 */
	public static final Integer APPROVE_CANCEL = 2;
	/**
	 * 驳回
	 */
	public static final Integer APPROVE_REJECT = 3;
	
	private Map<String, Integer> statusMap;
	
	private String taskTitlePrefix;
	private String taskPrefix;
	private String originUrl;

	private String targetUrl;
	private Integer timeout;

	private String restUser;
	private String restPassword;

	private String tokenPath;
	private String createPath;
	private String updatePath;

	private String registerCode;
	
	private String token;
	
	private String formUrl;
	
	public UnifyTask2SeeyonSender() {
		super();
		init();
	}
	
	public UnifyTask2SeeyonSender(Map<String, String> config) {
		super();
		init(config);
	}
	
	public void init() {
		BasicDataService basicDataService = SpringContext.getApplicationContext().getBean("basicDataService", BasicDataService.class);
		String pushConfig = basicDataService.querySysArg("sys.unify.task.push.config");
		Map<String, String> senderConfig = null;
		if (StringUtils.isNotBlank(pushConfig)) {
			Map<String, Object> config = JSON.parseObject(pushConfig, Map.class);
			senderConfig = (Map<String, String>) config.get(this.getClass().getName());
		}
		init(senderConfig);
	}
	
	public void init(Map<String, String> properties) {
		initStatusMap();
		if (properties == null) {
			properties = Collections.emptyMap();
		}
		taskTitlePrefix = MapUtil.getOrDefault(properties, "taskTitlePrefix", "【PMS】");
		taskPrefix = MapUtil.getOrDefault(properties, "taskPrefix", "PMS#");
		originUrl = MapUtil.getOrDefault(properties, "originUrl", "http://pms.dptech.com");
		targetUrl = MapUtil.getOrDefault(properties, "targetUrl", "https://oatest.dptech.com/");
		timeout = Integer.valueOf(MapUtil.getOrDefault(properties, "timeout", "30000"));

		restUser = MapUtil.getOrDefault(properties, "restUser", "rest");
		restPassword = MapUtil.getOrDefault(properties, "restPassword", "b59e0089-791a-4a83-b2c6-bb7ac7bcea12");

		tokenPath = MapUtil.getOrDefault(properties, "tokenPath", "/seeyon/rest/token");
		createPath = MapUtil.getOrDefault(properties, "createPath", "/seeyon/rest/thirdpartyPending/receive?token=");
		updatePath = MapUtil.getOrDefault(properties, "updatePath", "/seeyon/rest/thirdpartyPending/updatePendingState?token=");

		registerCode = MapUtil.getOrDefault(properties, "registerCode", "3006");
		formUrl = MapUtil.getOrDefault(properties, "formUrl", "/module/Workspace!task.action");
	}
	
	public void initStatusMap() {
		statusMap = new HashMap<String, Integer>(6 * 4 / 3 + 2);
		statusMap.put(DONE, 1);
		statusMap.put(TODO, 0);
		statusMap.put(STATUS_AGREE, APPROVE_AGREE);
		statusMap.put(STATUS_DISAGREE, APPROVE_DISAGREE);
		statusMap.put(STATUS_CANCEL, APPROVE_CANCEL);
		statusMap.put(STATUS_REJECT, APPROVE_REJECT);
	}

	@Override
	public List<UnifyTaskResult> pushUnifyTask(List<UnifyTask> unifyTasks, String type) {
		if (unifyTasks == null || unifyTasks.isEmpty()) {
			return null;
		}
		List<UnifyTaskResult> results = new ArrayList<UnifyTaskResult>(unifyTasks.size());
		for (UnifyTask unifyTask : unifyTasks) {
			UnifyTaskResult result = null;
			try {
				result = pushUnifyTask(unifyTask, type);
			} catch (Exception e) {
				result = new UnifyTaskResult(false, e.getMessage());
			}
			unifyTask.setSuccess(result.getSuccess());
			unifyTask.setMessage(JSON.toJSONString(result.getErrorMsgs()));
			
			results.add(result);
		}
		return results;
	}

	@Override
	public UnifyTaskResult pushUnifyTask(UnifyTask unifyTask, String type) {
		SeeyonTask seeyonTask = (SeeyonTask) unifyTask;
		if (seeyonTask == null) {
			return new UnifyTaskResult(true);
		}
		if (seeyonTask.getRegisterCode() == null) {
			seeyonTask.setRegisterCode(registerCode);
		}
//		// 加工taskId，保证各系统统一代办任务id不重复且唯一
//		String taskId = seeyonTask.getOriginTaskId() == null ? seeyonTask.getTaskId() : seeyonTask.getOriginTaskId();
//		// 保存原始taskId
//		if (seeyonTask.getOriginTaskId() == null) {
//			seeyonTask.setOriginTaskId(taskId);
//		}
//		seeyonTask.setTitle(taskTitlePrefix + unifyTask.getTitle());
//		seeyonTask.setTaskId(taskPrefix + unifyTask.getAssignee() + "_" + taskId);
		// 拼装url连接，跳转指定的页面
		if (seeyonTask.getUrl() == null && seeyonTask.getFormUrl() != null) {
			seeyonTask.setUrl(originUrl + seeyonTask.getFormUrl());
		} else if (seeyonTask.getUrl() == null && formUrl != null) {
			seeyonTask.setUrl(originUrl + formUrl);
		}
		UnifyTaskResult result = null;
		if (CREATE.equalsIgnoreCase(type)) {
			result = this.createUnifyTask(seeyonTask);
		} else if (UPDATE.equalsIgnoreCase(type)) {
			result = this.updateUnifyTask(seeyonTask);
		} else {
			result = new UnifyTaskResult(false, "推送类型未指定！");
		}
		seeyonTask.setSuccess(result.getSuccess());
		seeyonTask.setMessage(JSON.toJSONString(result.getErrorMsgs()));
		return result;
	}
	
	/**
	 * <table>
	 * <tr><th>参数名</th>					<th>必选</th>			<th>类型</th>			<th>说明</th><tr>
	 * <tr><td>taskId</td>				<td>必填</td>			<td>String</td>		<td>第三方待办主键（保证唯一）</td><tr>
	 * <tr><td>registerCode</td>		<td>必填</td>			<td>String</td>		<td>为第三方配置的系统注册编码</td><tr>
	 * <tr><td>title</td>				<td>必填</td>			<td>String</td>		<td>待办标题</td><tr>
	 * <tr><td>thirdSenderId</td>		<td>非必填</td>		<td>String</td>		<td>第三方待办发起人主键（保证唯一）</td><tr>
	 * <tr><td>senderName</td>			<td>必填</td>			<td>String</td>		<td>第三方待办发起人姓名</td><tr>
	 * <tr><td>thirdReceiverId</td>		<td>必填</td>			<td>String</td>		<td>第三方待办接收人主键（保证唯一）</td><tr>
	 * <tr><td>creationDate</td>		<td>必填</td>			<td>String</td>		<td>待办创建时间（格式：yyyy-MM-dd HH:mm:ss）</td><tr>
	 * <tr><td>state</td>				<td>必填</td>			<td>Integer</td>	<td>状态：0:未办理；1:已办理</td><tr>
	 * <tr><td>subState</td>			<td>非必填	</td>		<td>Integer</td>	<td>处理后状态：0/1/2/3同意已办/不同意已办/取消/驳回</td><tr>
	 * <tr><td>content</td>				<td>非必填</td>		<td>String</td>		<td>原生应用下载地址（仅3和6类型可选）</td><tr>
	 * <tr><td>url</td>					<td>非必填</td>		<td>String</td>		<td>PC端穿透链接</td><tr>
	 * <tr><td>h5url</td>				<td>非必填</td>		<td>String</td>		<td>移动端穿透链接</td><tr>
	 * <tr><td>appParam</td>			<td>非必填	</td>		<td>String</td>		<td>原生应用穿透命令，穿透命令需要按这个顺序：iphone|ipad|android|wp</td><tr>
	 * <tr><td>noneBindingSender</td>	<td>免绑定必填字段</td>	<td>String</td>		<td>登录名称/人员编码/手机号/电子邮件</td><tr>
	 * <tr><td>noneBindingReceiver</td>	<td>免绑定必填字段</td>	<td>String</td>		<td>登录名称/人员编码/手机号/电子邮件</td><tr>
	 * </table>
	 */
	@Override
	public List<UnifyTask> initUnifyTask(UnifyDelegateTask delegateTask, List<User> receiverUsers, Map<String, Object> params) {
		User senderUser = delegateTask.getSenderUser();
		DelegateTask task = delegateTask.getDelegateTask();
		String formKey = delegateTask.getFormUrl();
		List<UnifyTask> unifyTaskList = new ArrayList<UnifyTask>(receiverUsers.size());
		Integer state = null;
		Integer subState = null;
		if (params != null && !params.isEmpty()) {
			String stateBF = String.valueOf(MapUtil.getOrDefault(params, "state", 0));
			String subStateBF = String.valueOf(params.get("subState"));
			state = (Integer) MapUtil.getOrDefault(statusMap, stateBF, 0);
			subState = (Integer) MapUtil.getOrDefault(statusMap, subStateBF, 0);
		}
		Set<String> uniqueUser = new HashSet<String>();
		UnifyTask common = initCommonUnifyTask(delegateTask);
		for (User receiverUser : receiverUsers) {
			// 判断接收人是否已经存在
			if (uniqueUser.contains(receiverUser.getId())) {
				continue;
			}

			SeeyonTask unifyTask = new SeeyonTask();
			BeanUtils.copyProperties(common, unifyTask);
			unifyTask.setAssignee(receiverUser.getId());
			unifyTask.setFormUrl(formKey);
			
			unifyTask.setTaskId(task.getId());
//			unifyTask.setTitle(task.getName());
//			unifyTask.setTitle(task.getDescription());
			unifyTask.setTitle(delegateTask.getTitle());
			
			// 加工taskId，保证各系统统一代办任务id不重复且唯一
			String taskId = unifyTask.getOriginTaskId() == null ? unifyTask.getTaskId() : unifyTask.getOriginTaskId();
			// 保存原始taskId
			if (unifyTask.getOriginTaskId() == null) {
				unifyTask.setOriginTaskId(taskId);
			}
			unifyTask.setTitle(taskTitlePrefix + unifyTask.getTitle());
			unifyTask.setTaskId(taskPrefix + unifyTask.getAssignee() + "_" + taskId);
			
			unifyTask.setCreationDate(task.getCreateTime());
			unifyTask.setNoneBindingReceiver(receiverUser.getPassword());// 暂存userName
			unifyTask.setNoneBindingSender(senderUser.getPassword());
			unifyTask.setSenderName(senderUser.getPassword());
			unifyTask.setState(state != null ? state.toString() : null);
			unifyTask.setSubState(subState != null ? subState.toString() : null);
			unifyTask.setThirdReceiverId(receiverUser.getId());
			unifyTask.setThirdSenderId(String.valueOf(senderUser.getId()));
			unifyTask.setPushData(JSON.toJSONString(unifyTask));
			unifyTaskList.add(unifyTask);
			
			uniqueUser.add(receiverUser.getId());
		}
		return unifyTaskList;
	}
	
	@Override
	public void beforePush(UnifyTask unifyTask, String type) {
		super.beforePush(unifyTask, type);
	}

	@Override
	public void afterPush(UnifyTask unifyTask, String type) {
		super.afterPush(unifyTask, type);
//		IUnifyTaskService unifyTaskService = SpringContext.getBean("unifyTaskService", IUnifyTaskService.class);
//		unifyTaskService.insertSelective(unifyTask);
	}

	private UnifyTaskResult createUnifyTask(SeeyonTask seeyonTask) {
		return post(seeyonTask, createPath);
	}

	private UnifyTaskResult updateUnifyTask(SeeyonTask seeyonTask) {
		return post(seeyonTask, updatePath);
	}

	private UnifyTaskResult post(SeeyonTask seeyonTask, String urlPath) {
		String token = getToken();
		String url = URLUtil.normalize(String.format("%s/%s%s", targetUrl, urlPath, token), false, true);
		String pushData = JSON.toJSONString(seeyonTask);
		seeyonTask.setPushData(pushData);
		String responseBody = HttpUtil.post(url, pushData, timeout);
		if (JSONValidator.from(responseBody).validate()) {
            return JSON.parseObject(responseBody, UnifyTaskResult.class);
        }
		return new UnifyTaskResult(false, responseBody);
	}

	public String getToken() {
		return getToken(false); 
	}
	
	public String getToken(boolean bindUser) {
        if (token == null) {
            String url = URLUtil.normalize(String.format("%s/%s", targetUrl, tokenPath), false, true);
            Map<String, Object> paramMap = new HashMap<String, Object>(3);
            paramMap.put("userName", restUser);
            paramMap.put("password", restPassword);
            if (bindUser) {
                paramMap.put("loginName", restUser);
            }
            String responseBody = HttpUtil.post(url, JSON.toJSONString(paramMap), timeout);
            if (JSONValidator.from(responseBody).validate()) {
                Map result = JSON.parseObject(responseBody, Map.class);
                token = (String) result.get("id");
            } else {
                token = null;
            }
        }
        return token; 
    }

	public static void main(String[] args) {
		UnifyTask2SeeyonSender unifyTask2SeeyonSender = new UnifyTask2SeeyonSender();
		String taskId = "11112333";
		SeeyonTask unifyTask = new SeeyonTask();
		unifyTask.setTaskId(taskId);
		unifyTask.setCreationDate(new Date());
		unifyTask.setNoneBindingReceiver("z03412");
		unifyTask.setNoneBindingSender("z03412");
		unifyTask.setSenderName("z03412");
		unifyTask.setState("0");
		unifyTask.setThirdReceiverId("z03412");
		unifyTask.setThirdSenderId("z03412");
		unifyTask.setTitle("测试任务2");
		unifyTask.setUrl("hxxx");
		System.out.println(JSON.toJSONString(unifyTask));
		UnifyTaskResult result = unifyTask2SeeyonSender.pushUnifyTask(unifyTask, CREATE);
		unifyTask.setSuccess(result.getSuccess());
		unifyTask.setMessage(result.getSuccess() ? null : JSON.toJSONString(result.getErrorMsgs()));
		System.out.println(JSON.toJSONString(result));

		unifyTask.setState("1");
		unifyTask.setSubState("0");
		result = unifyTask2SeeyonSender.pushUnifyTask(unifyTask, UPDATE);
		unifyTask.setSuccess(result.getSuccess());
		unifyTask.setMessage(result.getSuccess() ? null : JSON.toJSONString(result.getErrorMsgs()));
		System.out.println(JSON.toJSONString(result));
	}

}
