package com.dp.plat.activiti.unifytask.sender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.identity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.dp.plat.activiti.unifytask.entity.UnifyTask;
import com.dp.plat.activiti.unifytask.vo.SeeyonTask;
import com.dp.plat.activiti.unifytask.vo.UnifyDelegateTask;
import com.dp.plat.activiti.unifytask.vo.UnifyTaskResult;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.util.SystemLogUtil;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

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
	
	public UnifyTask2SeeyonSender() {
		super();
		init();
	}
	
	public UnifyTask2SeeyonSender(Map<String, String> map) {
		super();
		init(map);
	}

	public void init() {
		init(SystemConfig.systemVariables);
	}
	
	public void init(Map<String, String> properties) {
		initStatusMap();
		if (properties == null) {
			properties = Collections.emptyMap();
		}
		taskTitlePrefix = properties.getOrDefault("taskTitlePrefix", "【PMS2】");
		taskPrefix = properties.getOrDefault("taskPrefix", "PMS2#");
		originUrl = properties.getOrDefault("originUrl", "http://pms2.dptech.com");
		targetUrl = properties.getOrDefault("targetUrl", "https://oatest.dptech.com/");
		timeout = Integer.valueOf(properties.getOrDefault("timeout", "30000"));

		restUser = properties.getOrDefault("restUser", "rest");
		restPassword = properties.getOrDefault("restPassword", "b59e0089-791a-4a83-b2c6-bb7ac7bcea12");

		tokenPath = properties.getOrDefault("tokenPath", "/seeyon/rest/token");
		createPath = properties.getOrDefault("createPath", "/seeyon/rest/thirdpartyPending/receive?token=");
		updatePath = properties.getOrDefault("updatePath", "/seeyon/rest/thirdpartyPending/updatePendingState?token=");

		registerCode = properties.getOrDefault("registerCode", "3006");
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
//		seeyonTask.setTitle(taskTitlePerfix + unifyTask.getTitle());
//		seeyonTask.setTaskId(taskPerfix + unifyTask.getAssignee() + "_" + taskId);
		// 拼装url连接，跳转指定的页面
		if (seeyonTask.getUrl() == null && seeyonTask.getFormUrl() != null) {
			seeyonTask.setUrl(originUrl + seeyonTask.getFormUrl());
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
		String formKey = task.getFormKey();
		List<UnifyTask> unifyTaskList = new ArrayList<UnifyTask>(receiverUsers.size());
		Integer state = null;
		Integer subState = null;
		if (params != null && !params.isEmpty()) {
			String stateBF = String.valueOf(params.getOrDefault("state", 0));
			String subStateBF = String.valueOf(params.get("subState"));
			state = statusMap.getOrDefault(stateBF, 0);
			subState = statusMap.getOrDefault(subStateBF, 0);
		}
		UnifyTask common = initCommonUnifyTask(delegateTask);
		for (User receiverUser : receiverUsers) {
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
		try {
			return JSON.parseObject(responseBody, UnifyTaskResult.class);
		} catch (Exception e) {
			try {
				ExceptionHandler.insertException(e);
			} catch (Exception e2) {
			}
			return new UnifyTaskResult(false, e.getMessage());
		}
	}

	private String getToken() {
		if (token == null) {
			String url = URLUtil.normalize(String.format("%s/%s", targetUrl, tokenPath), false, true);
			Map<String, Object> paramMap = new HashMap<String, Object>(3);
			paramMap.put("userName", restUser);
			paramMap.put("password", restPassword);
			String responseBody = HttpUtil.post(url, JSON.toJSONString(paramMap), timeout);
			try {
				Map result = JSON.parseObject(responseBody, Map.class);
				token = (String) result.get("id");
			} catch (Exception e) {
				try {
					ExceptionHandler.insertException(e);
				} catch (Exception e2) {
				}
			}
		}
		return token; 
	}

	public static void main(String[] args) {
        SeeyonTask unifyTask = null;
        UnifyTaskResult result = null;
	    String vars = "{\r\n" + 
	            "      \"taskTitlePrefixs\": \"【安服】\",\r\n" + 
	            "      \"taskPrefix\": \"PMS2#\",\r\n" + 
	            "      \"originUrl\": \"http://pms2.dptech.com\",\r\n" + 
	            "      \"targetUrl\": \"https://oa.dptech.com/\",\r\n" + 
	            "      \"timeout\": \"30000\",\r\n" + 
	            "      \"restUser\": \"rest\",\r\n" + 
	            "      \"restPassword\": \"df5cd6e6-e663-4b7e-9c46-8588636a9060\",\r\n" + 
	            "      \"tokenPath\": \"/seeyon/rest/token\",\r\n" + 
	            "      \"createPath\": \"/seeyon/rest/thirdpartyPending/receive?token=\",\r\n" + 
	            "      \"updatePath\": \"/seeyon/rest/thirdpartyPending/updatePendingState?token=\",\r\n" + 
	            "      \"registerCode\": \"3006\",\r\n" + 
	            "      \"formUrl\": \"/workflow/workbench.html\"\r\n" + 
	            "    }";
	    SystemConfig.systemVariables = JSON.parseObject(vars, HashMap.class);
        UnifyTask2SeeyonSender unifyTask2SeeyonSender = new UnifyTask2SeeyonSender();
	    unifyTask = JSON.parseObject("{\r\n" + 
	            "  \"assignee\": \"969\",\r\n" + 
	            "  \"beginTime\": 1665463846251,\r\n" + 
	            "  \"createTime\": 1665463846000,\r\n" + 
	            "  \"creationDate\": 1665463846251,\r\n" + 
	            "  \"eventType\": \"TASK_CREATED\",\r\n" + 
	            "  \"formUrl\": \"/workflow/200.html\",\r\n" + 
	            "  \"noneBindingReceiver\": \"w02611\",\r\n" + 
	            "  \"noneBindingSender\": \"w02611\",\r\n" + 
	            "  \"originTaskId\": \"788097\",\r\n" + 
	            "  \"procInstId\": \"787907\",\r\n" + 
	            "  \"processKey\": \"SubcontractInspection:1:752804\",\r\n" + 
	            "  \"pushSender\": \"com.dp.plat.activiti.unifytask.sender.UnifyTask2SeeyonSender\",\r\n" + 
	            "  \"registerCode\": \"3006\",\r\n" + 
	            "  \"senderName\": \"w02611\",\r\n" + 
	            "  \"state\": \"1\",\r\n" + 
	            "  \"subState\": \"2\",\r\n" + 
	            "  \"taskId\": \"PMS2#969_78809711111\",\r\n" + 
	            "  \"taskKey\": \"acceptanceTask\",\r\n" + 
	            "  \"taskName\": \"验收材料审批\",\r\n" + 
	            "  \"thirdReceiverId\": \"969\",\r\n" + 
	            "  \"thirdSenderId\": \"969\",\r\n" + 
	            "  \"title\": \"【PMS2】验收材料审批-财务初审-2022-09-37-国家电网有限公司信息通信分公司2022年第二次服务招标采购-2022年安全检测专项技术支持服务-60-270000.00-实施中-80%\",\r\n" + 
	            "  \"url\": \"http://pms2.dptech.com/workflow/200.html\"\r\n" + 
	            "}", SeeyonTask.class);
	    String template = "$customInfo.purchId$-$smsProjectName$-$acceptanceDesc$-$progressDesc$";
	    String separator = "-";
	    SettlementVO vo = new SettlementVO();
	    vo.setSmsProjectName("国家电网有限公司信息通信分公司2022年第二次服务招标采购-2022年安全检测专项技术支持服务");
	    vo.setSettleSeq("2022-09-37-国家电网有限公司信息通信分公司2022年第二次服务招标采购-2022年安全检测专项技术支持服务-60-270000.00");
	    vo.setAcceptanceDesc("实施中");
	    vo.setProgressDesc("100%");
	    vo.setCustomInfoByKey("purchId", "PA062016");
	    String title = "安服项目转包结算 -- 验收材料审批";
        String temp = SystemLogUtil.format(template, vo);
        if (StringUtils.isNotBlank(separator)) {
            List<String> splits = Arrays.asList(StringUtils.splitByWholeSeparator(temp, separator));
            splits = splits.stream().filter(split -> {
                return StringUtils.isNotBlank(separator);
            }).collect(Collectors.toList());
            temp = StringUtils.join(splits, separator);
        }
        title += " -- " + temp;
        unifyTask.setTitle(unifyTask2SeeyonSender.taskTitlePrefix + title);
	    System.out.println(JSON.toJSONString(unifyTask));
        result = unifyTask2SeeyonSender.pushUnifyTask(unifyTask, CREATE);
        unifyTask.setSuccess(result.getSuccess());
        unifyTask.setMessage(result.getSuccess() ? null : JSON.toJSONString(result.getErrorMsgs()));
        System.out.println(JSON.toJSONString(result));
	}

	public static void main2(String[] args) {
		String vars = "{\"taskPerfix\":\"EHR2#\",\"originUrl\":\"http://ehr2.dptech.com\",\"targetUrl\":\"https://oa.dptech.com\",\"targetUrl2\":\"https://oa.dptech.com/\",\"timeout\":\"30000\",\"restUser\":\"rest\",\"restPassword\":\"df5cd6e6-e663-4b7e-9c46-8588636a9060\",\"tokenPath\":\"seeyon/rest/token\",\"createPath\":\"seeyon/rest/thirdpartyPending/receive?token=\",\"updatePath\":\"seeyon/rest/thirdpartyPending/updatePendingState?token=\",\"registerCode\":\"3005\",\"formUrl\":\"/perf/workbench.html\"}";
		SystemConfig.systemVariables = JSON.parseObject(vars, HashMap.class);
		UnifyTask2SeeyonSender unifyTask2SeeyonSender = new UnifyTask2SeeyonSender();
//		String taskId = "11112";
//		SeeyonTask unifyTask = new SeeyonTask();
//		unifyTask.setTaskId(taskId);
//		unifyTask.setCreationDate(new Date());
//		unifyTask.setNoneBindingReceiver("w02611");
//		unifyTask.setNoneBindingSender("j01441");
//		unifyTask.setSenderName("w02611");
//		unifyTask.setState("1");
//		unifyTask.setThirdReceiverId("969");
//		unifyTask.setThirdSenderId("9699");
//		unifyTask.setTitle("测试任务2");
//		unifyTask.setUrl("http://ehr2.dptech.com///perf/planParticipant/planList/20798.html");
		SeeyonTask unifyTask = null;
		UnifyTaskResult result = null;
//		SeeyonTask unifyTask = JSON.parseObject("{\r\n" + 
//				"  \"assignee\": \"969\",\r\n" + 
//				"  \"beginTime\": 1648639007933,\r\n" + 
//				"  \"createBy\": \"c02214\",\r\n" + 
//				"  \"createTime\": 1648639007000,\r\n" + 
//				"  \"creationDate\": 1648639007933,\r\n" + 
//				"  \"dueTime\": 1649260800000,\r\n" + 
//				"  \"eventType\": \"TASK_CREATED\",\r\n" + 
//				"  \"formUrl\": \"/perf/planParticipant/planList/33566.html\",\r\n" + 
//				"  \"id\": 124060,\r\n" + 
//				"  \"latest\": false,\r\n" + 
//				"  \"noneBindingReceiver\": \"w02611\",\r\n" + 
//				"  \"noneBindingSender\": \"c02214\",\r\n" + 
//				"  \"originTaskId\": \"2021614\",\r\n" + 
//				"  \"procInstId\": \"2021602\",\r\n" + 
//				"  \"processKey\": \"perfObjectiveEvaluation:1:8\",\r\n" + 
//				"  \"pushSender\": \"com.dp.plat.activiti.unifytask.sender.UnifyTask2SeeyonSender\",\r\n" + 
//				"  \"registerCode\": \"3005\",\r\n" + 
//				"  \"senderName\": \"c02214\",\r\n" + 
//				"  \"state\": \"1\",\r\n" + 
//				"  \"subState\": \"2\",\r\n" + 
//				"  \"taskId\": \"EHR2#969_2021614\",\r\n" + 
//				"  \"taskKey\": \"selfSummary\",\r\n" + 
//				"  \"taskName\": \"自我总结\",\r\n" + 
//				"  \"thirdReceiverId\": \"969\",\r\n" + 
//				"  \"thirdSenderId\": \"684\",\r\n" + 
//				"  \"title\": \"【绩效】市场部2022年一季度员工PBC承诺与考核 -- 自我总结\",\r\n" + 
//				"  \"url\": \"http://ehr2.dptech.com/perf/planParticipant/planList/33566.html\"\r\n" + 
//				"}", SeeyonTask.class);
//		System.out.println(JSON.toJSONString(unifyTask));
//		result = unifyTask2SeeyonSender.pushUnifyTask(unifyTask, CREATE);
//		unifyTask.setSuccess(result.getSuccess());
//		unifyTask.setMessage(result.getSuccess() ? null : JSON.toJSONString(result.getErrorMsgs()));
//		System.out.println(JSON.toJSONString(result));
//		
		unifyTask = JSON.parseObject("{\r\n" + 
				"  \"assignee\": \"969\",\r\n" + 
				"  \"beginTime\": 1648639007933,\r\n" + 
				"  \"createBy\": \"c02214\",\r\n" + 
				"  \"createTime\": 1648639008000,\r\n" + 
				"  \"creationDate\": 1648639007933,\r\n" + 
				"  \"dueTime\": 1649260800000,\r\n" + 
				"  \"eventType\": \"ENTITY_SUSPENDED\",\r\n" + 
				"  \"formUrl\": \"/perf/planParticipant/planList/33566.html\",\r\n" + 
				"  \"id\": 124061,\r\n" + 
				"  \"latest\": false,\r\n" + 
				"  \"noneBindingReceiver\": \"w02611\",\r\n" + 
				"  \"noneBindingSender\": \"c02214\",\r\n" + 
				"  \"originTaskId\": \"2021614\",\r\n" + 
				"  \"procInstId\": \"2021602\",\r\n" + 
				"  \"processKey\": \"perfObjectiveEvaluation:1:8\",\r\n" + 
				"  \"pushSender\": \"com.dp.plat.activiti.unifytask.sender.UnifyTask2SeeyonSender\",\r\n" + 
				"  \"registerCode\": \"3005\",\r\n" + 
				"  \"senderName\": \"c02214\",\r\n" + 
				"  \"state\": \"1\",\r\n" + 
				"  \"subState\": \"2\",\r\n" + 
				"  \"taskId\": \"EHR2#969_2021614\",\r\n" + 
				"  \"taskKey\": \"selfSummary\",\r\n" + 
				"  \"taskName\": \"自我总结\",\r\n" + 
				"  \"thirdReceiverId\": \"969\",\r\n" + 
				"  \"thirdSenderId\": \"684\",\r\n" + 
				"  \"title\": \"【绩效】市场部2022年一季度员工PBC承诺与考核 -- 自我总结\",\r\n" + 
				"  \"url\": \"http://ehr2.dptech.com/perf/planParticipant/planList/33566.html\"\r\n" + 
				"}", SeeyonTask.class);
		System.out.println(JSON.toJSONString(unifyTask));
		result = unifyTask2SeeyonSender.pushUnifyTask(unifyTask, CREATE);
		unifyTask.setSuccess(result.getSuccess());
		unifyTask.setMessage(result.getSuccess() ? null : JSON.toJSONString(result.getErrorMsgs()));
		System.out.println(JSON.toJSONString(result));
		

//		unifyTask.setState("1");
//		unifyTask.setSubState("0");
//		result = unifyTask2SeeyonSender.pushUnifyTask(unifyTask, UPDATE);
//		unifyTask.setSuccess(result.getSuccess());
//		unifyTask.setMessage(result.getSuccess() ? null : JSON.toJSONString(result.getErrorMsgs()));
//		System.out.println(JSON.toJSONString(result));
	}

}
