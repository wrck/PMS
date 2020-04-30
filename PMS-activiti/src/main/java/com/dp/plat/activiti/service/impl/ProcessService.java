package com.dp.plat.activiti.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.NativeProcessInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.entity.CommentVO;
import com.dp.plat.activiti.entity.ProcessInstanceEntity;
import com.dp.plat.activiti.entity.Vacation;
import com.dp.plat.activiti.process.cmd.DeleteActiveTaskCmd;
import com.dp.plat.activiti.process.cmd.RevokeTaskCmd;
import com.dp.plat.activiti.process.cmd.StartActivityCmd;
import com.dp.plat.activiti.process.cmd.WithdrawTaskCmd;
import com.dp.plat.activiti.service.IPerformanceService;
import com.dp.plat.activiti.service.IProcessService;
import com.dp.plat.activiti.service.IVacationService;
import com.dp.plat.activiti.service.IWorkflowService;
import com.dp.plat.activiti.utils.BeanUtils;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.param.RoleConstant;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;

/**
 * 流程相关Service
 * 
 * @author w02611
 *
 */
@Service("processService")
public class ProcessService implements IProcessService {

	@Autowired
	protected RuntimeService runtimeService;

	@Autowired
	protected IdentityService identityService;

	@Autowired
	protected TaskService taskService;

	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected HistoryService historyService;

	@Autowired
	protected IUserService userService;

	@Autowired
	private ManagementService managementService;

	@Autowired
	ProcessEngineFactoryBean processEngineFactory;

	@Autowired
	ProcessEngineConfiguration processEngineConfiguration;

	@Autowired
	protected IWorkflowService workflowService;

	@Autowired
	private IVacationService vacationService;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private IPerformanceService performanceService;

	/**
	 * 查询代办任务
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@Override
	public List<BaseVO> findTodoTask(User user, PageParam<BaseVO> page) {
		// taskCandidateOrAssigned查询某个人的待办任务，包含已签收、候选任务<候选人范围和候选组范围>
		// TaskQuery taskQuery =
		// this.taskService.createTaskQuery().taskCandidateOrAssigned(user.getUserId().toString()).active();
		TaskQuery taskQuery = this.taskService.createTaskQuery().active();
		if (UserContext.hasRole(RoleConstant.ROLE_ADMIN)) {
			if (StringUtils.isNotBlank(page.getFuzzy())) {
				taskQuery = taskQuery.or().taskDescriptionLike("%" + page.getFuzzy() + "%")
						.taskNameLike("%" + page.getFuzzy() + "%").endOr();
			}
		} else {
			taskQuery = taskQuery.taskCandidateOrAssigned(user.getUserId().toString());
		}
		Integer totalSum = taskQuery.list().size();
		page.setTotal(totalSum);
		List<Task> tasks = taskQuery.orderByTaskCreateTime().desc().listPage(page.getStart(), (int) page.getPageSize());
		List<BaseVO> taskList = getBaseVOList(tasks);
		return taskList;
	}

	/**
	 * 读取已结束中的流程(admin查看)
	 *
	 * @return
	 */
	@Override
	public List<BaseVO> findFinishedProcessInstances(PageParam<BaseVO> page) {
		HistoricProcessInstanceQuery historQuery = historyService.createHistoricProcessInstanceQuery().finished();

		Integer totalSum = historQuery.list().size();
		page.setTotal(totalSum);
		List<HistoricProcessInstance> list = historQuery.orderByProcessInstanceEndTime().desc()
				.listPage(page.getStart(), (int) page.getPageSize());
		List<BaseVO> processList = new ArrayList<BaseVO>();

		for (HistoricProcessInstance historicProcessInstance : list) {
			String processInstanceId = historicProcessInstance.getId();
			List<HistoricVariableInstance> listVar = this.historyService.createHistoricVariableInstanceQuery()
					.processInstanceId(processInstanceId).list();
			for (HistoricVariableInstance var : listVar) {
				if ("serializable".equals(var.getVariableTypeName()) && "entity".equals(var.getVariableName())) {
					BaseVO base = (BaseVO) var.getValue();
					base.setHistoricProcessInstance(historicProcessInstance);
					base.setProcessDefinition(getProcessDefinition(historicProcessInstance.getProcessDefinitionId()));
					base.setHistoricProcessInstance(this.historyService.createHistoricProcessInstanceQuery()
							.processInstanceId(processInstanceId).singleResult());
					processList.add(base);
					break;
				}
			}
		}

		return processList;
	}

	/**
	 * 各个审批人员查看自己完成的任务
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<BaseVO> findFinishedTaskInstances(User user, PageParam<BaseVO> page) throws Exception {
		HistoricTaskInstanceQuery historQuery = historyService.createHistoricTaskInstanceQuery().finished();
		if (user != null) {
			historQuery = historQuery.taskAssignee(user.getUserId().toString());
		}
		// HistoricTaskInstanceQuery historQuery =
		// historyService.createHistoricTaskInstanceQuery()
		// .taskAssignee(user.getUserId().toString()).finished();
		Integer totalSum = historQuery.list().size();
		page.setTotal(totalSum);
		List<HistoricTaskInstance> list = historQuery.orderByHistoricTaskInstanceEndTime().desc()
				.listPage(page.getStart(), (int) page.getPageSize());
		List<BaseVO> taskList = new ArrayList<BaseVO>();

		for (HistoricTaskInstance historicTaskInstance : list) {
			String processInstanceId = historicTaskInstance.getProcessInstanceId();
			List<HistoricVariableInstance> listVar = this.historyService.createHistoricVariableInstanceQuery()
					.processInstanceId(processInstanceId).list();
			for (HistoricVariableInstance var : listVar) {
				if ("serializable".equals(var.getVariableTypeName()) && "entity".equals(var.getVariableName())) {
					BaseVO base = (BaseVO) var.getValue();
					base.setHistoricTaskInstance(historicTaskInstance);
					base.setProcessDefinition(getProcessDefinition(historicTaskInstance.getProcessDefinitionId()));
					base.setHistoricProcessInstance(this.historyService.createHistoricProcessInstanceQuery()
							.processInstanceId(processInstanceId).singleResult());
					taskList.add(base);
					break;
				}
			}
		}
		return taskList;
	}

	/**
	 * 将Task集合转为BaseVO集合
	 * 
	 * @param tasks
	 * @return
	 */
	protected List<BaseVO> getBaseVOList(List<Task> tasks) {
		List<BaseVO> taskList = new ArrayList<BaseVO>();
		for (Task task : tasks) {
			String processInstanceId = task.getProcessInstanceId();
			ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
					.processInstanceId(processInstanceId).active().singleResult();
			if (BeanUtils.isBlank(processInstance)) {
				// 如果有挂起的流程则continue
				continue;
			}
			// 获取当前流程下的key为entity的variable
			BaseVO base = (BaseVO) this.runtimeService.getVariable(task.getProcessInstanceId(), "entity");
			if (base == null) {
				base = new BaseVO();
			}
			base.setTask(task);
			base.setProcessInstance(processInstance);
			base.setProcessDefinition(getProcessDefinition(processInstance.getProcessDefinitionId()));
			taskList.add(base);
		}
		return taskList;
	}

	/**
	 * 查询流程定义对象
	 *
	 * @param processDefinitionId
	 *            流程定义ID
	 * @return
	 */
	protected ProcessDefinition getProcessDefinition(String processDefinitionId) {
		ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		return processDefinition;
	}

	/**
	 * 查询流程实例对象
	 *
	 * @param processInstanceId
	 *            流程实例ID
	 * @return
	 */
	protected ProcessInstance getProcessInstance(String processInstanceId) {
		ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		return processInstance;
	}

	/**
	 * 签收任务
	 * 
	 * @param user
	 * @param taskId
	 */
	@Override
	public void claim(User user, String taskId) {
		this.identityService.setAuthenticatedUserId(user.getUserId().toString());
		this.taskService.claim(taskId, user.getUserId().toString());
	}

	/**
	 * 签收任务
	 * 
	 * @param user
	 * @param taskId
	 */
	@Override
	public void unclaim(String taskId) {
		this.identityService.setAuthenticatedUserId(null);
		this.taskService.unclaim(taskId);
	}

	/**
	 * 委派任务
	 */
	@Override
	public void delegateTask(String userId, String taskId) throws Exception {
		// API: If no owner is set on the task, the owner is set to the current
		// assignee of the task.
		// OWNER_（委托人）：受理人委托其他人操作该TASK的时候，受理人就成了委托人OWNER_，其他人就成了受理人ASSIGNEE_
		// assignee容易理解，主要是owner字段容易误解，owner字段就是用于受理人委托别人操作的时候运用的字段
		this.taskService.delegateTask(taskId, userId);
	}

	/**
	 * 转办任务
	 */
	@Override
	@Transactional
	public void transferTask(String userId, String taskId) throws Exception {
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task != null) {
			String assign = task.getAssignee();
			if (!userId.equals(assign)) {
				this.taskService.setAssignee(taskId, userId);
				this.taskService.setOwner(taskId, assign);
				org.activiti.engine.identity.User assignUser = identityService.createUserQuery().userId(userId)
						.singleResult();
				org.activiti.engine.identity.User ownerUser = identityService.createUserQuery().userId(assign)
						.singleResult();
				taskService.addComment(taskId, null, "【" + ownerUser.getLastName() + "-" + ownerUser.getFirstName()
						+ "】转办任务给【" + assignUser.getLastName() + "-" + assignUser.getFirstName() + "】");
			} else {
				throw new ActivitiIllegalArgumentException("转办后的办理人相同！");
			}
		} else {
			throw new ActivitiObjectNotFoundException("此任务不存在！转办任务失败！", this.getClass());
		}
	}

	/**
	 * 获取评论
	 * 
	 * @param processInstanceId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<CommentVO> getComments(String processInstanceId) throws Exception {
		// 查询一个任务所在流程的全部评论
		List<Comment> comments = this.taskService.getProcessInstanceComments(processInstanceId);
		List<CommentVO> commnetList = new ArrayList<CommentVO>();
		for (Comment comment : comments) {
			User user = this.userService.selectByPrimaryKey(new Integer(comment.getUserId()));
			CommentVO vo = new CommentVO();
			vo.setContent(comment.getFullMessage());
			vo.setTime(comment.getTime());
			vo.setUserName(user.getUserName());
			commnetList.add(vo);
		}
		return commnetList;
	}

	/**
	 * 显示流程图,带流程跟踪
	 * 
	 * @param processInstanceId
	 * @return
	 */
	@Override
	public InputStream getDiagram(String processInstanceId) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		String procDefId = processInstance != null ? processInstance.getProcessDefinitionId() : "";
		if (StringUtils.isBlank(procDefId)) {
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			procDefId = historicProcessInstance != null ? historicProcessInstance.getProcessDefinitionId() : "";
		}
		BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(procDefId);
		List<String> highLightedActivities = Collections.emptyList();
		List<String> highLightedFlows = Collections.emptyList();
		try {
			// 流程结束后无法获取活动节点，会报异常
			highLightedActivities = runtimeService.getActiveActivityIds(processInstanceId);
			highLightedFlows = getHighLightedFlows(processInstanceId, processDefinition);
		} catch (Exception e) {
		}
		// 不使用spring请使用下面的两行代码
		// ProcessEngineImpl defaultProcessEngine = (ProcessEngineImpl)
		// ProcessEngines.getDefaultProcessEngine();
		// Context.setProcessEngineConfiguration(defaultProcessEngine.getProcessEngineConfiguration());

		// 使用spring注入引擎请使用下面的这行代码
		processEngineConfiguration = processEngineFactory.getProcessEngineConfiguration();
		Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

		// 通过引擎生成png图片，并标记当前节点,并把当前节点用红色边框标记出来，弊端和直接部署流程文件生成的图片问题一样-乱码！。
		ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
		String activityFontName = processEngineConfiguration.getActivityFontName();
		String labelFontName = processEngineConfiguration.getLabelFontName();
		String annotationFontName = processEngineConfiguration.getAnnotationFontName();
		InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivities,
				highLightedFlows, activityFontName, labelFontName, annotationFontName, this.getClass().getClassLoader(),
				1);
//		InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivities,
//				highLightedFlows, activityFontName, labelFontName, this.getClass().getClassLoader(),
//				1);
		// InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel,
		// "png", processEngineConfiguration.getActivityFontName(),
		// processEngineConfiguration.getLabelFontName(),processEngineConfiguration.getAnnotationFontName(),
		// processEngineConfiguration.getClassLoader());
		return imageStream;
	}

	/**
	 * 获取流程跟踪的线
	 * 
	 * @param processInstanceId
	 * @param processDefinition
	 * @return
	 */
	private List<String> getHighLightedFlows(String processInstanceId, ProcessDefinitionEntity processDefinition) {

		List<String> highLightedFlows = new ArrayList<String>();
		List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

		List<String> historicActivityInstanceList = new ArrayList<String>();
		for (HistoricActivityInstance hai : historicActivityInstances) {
			historicActivityInstanceList.add(hai.getActivityId());
		}

		// add current activities to list
		List<String> highLightedActivities = runtimeService.getActiveActivityIds(processInstanceId);
		historicActivityInstanceList.addAll(highLightedActivities);

		// activities and their sequence-flows
		for (ActivityImpl activity : processDefinition.getActivities()) {
			int index = historicActivityInstanceList.indexOf(activity.getId());

			if (index >= 0 && index + 1 < historicActivityInstanceList.size()) {
				List<PvmTransition> pvmTransitionList = activity.getOutgoingTransitions();
				for (PvmTransition pvmTransition : pvmTransitionList) {
					String destinationFlowId = pvmTransition.getDestination().getId();
					if (destinationFlowId.equals(historicActivityInstanceList.get(index + 1))) {
						highLightedFlows.add(pvmTransition.getId());
					}
				}
			}
		}
		return highLightedFlows;
	}

	/**
	 * 显示图片-通过流程ID，，不带流程跟踪(没有乱码问题)
	 * 
	 * @param resourceType
	 * @param processInstanceId
	 * @return
	 */
	@Override
	public InputStream getDiagramByProInstanceId_noTrace(String resourceType, String processInstanceId) {

		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();

		String resourceName = "";
		if (resourceType.equals("png") || resourceType.equals("image")) {
			resourceName = processDefinition.getDiagramResourceName();
		} else if (resourceType.equals("xml")) {
			resourceName = processDefinition.getResourceName();
		}
		InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
				resourceName);
		return resourceAsStream;
	}

	/**
	 * 显示图片-通过部署ID，不带流程跟踪(没有乱码啊问题)
	 * 
	 * @param resourceType
	 * @param processInstanceId
	 * @return
	 * @throws Exception
	 */
	@Override
	public InputStream getDiagramByProDefinitionId_noTrace(String resourceType, String processDefinitionId)
			throws Exception {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		String resourceName = "";
		if (resourceType.equals("png") || resourceType.equals("image")) {
			resourceName = processDefinition.getDiagramResourceName();
		} else if (resourceType.equals("xml")) {
			resourceName = processDefinition.getResourceName();
		}
		InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
				resourceName);
		return resourceAsStream;
	}

	/**
	 * 查看正在运行的请假流程
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<BaseVO> listRuningVacation(Vacation vacation, PageParam<Object> page) throws Exception {
		page.setModel(vacation);
		Integer userId = UserContext.getCurrentPrincipal().getUserId();
		page.setTotal(vacationService.countBySelectivePageable(null));
		page.setFiltered(vacationService.countBySelectivePageable(page));
		if (page.getPageSize() == -1L) {
			page.setPageSize(page.getTotal());
		}

		List<Object> listVacation = this.vacationService.selectBySelectivePageable(page);
		List<BaseVO> result = new ArrayList<BaseVO>();
		if (listVacation != null) {
			for (Object obj : listVacation) {
				Vacation vac = (Vacation) obj;
				if (vac.getProcInstId() == null) {
					continue;
				}
				// 查询流程实例
				ProcessInstance pi = this.runtimeService.createProcessInstanceQuery()
						.processInstanceId(vac.getProcInstId()).singleResult();
				Task task = this.taskService.createTaskQuery().processInstanceId(vac.getProcInstId()).singleResult();
				if (pi != null) {
					// 查询流程参数
					BaseVO base = (BaseVO) this.runtimeService.getVariable(pi.getId(), "entity");
					base.setTask(task);
					base.setProcessInstance(pi);
					base.setProcessDefinition(getProcessDefinition(pi.getProcessDefinitionId()));

					result.add(base);
				}
			}
		}
		return result;
	}

	/**
	 * 完成任务
	 */
	@Override
	@Transactional
	public void complete(String taskId, String content, String userid, Map<String, Object> variables) throws Exception {
		Task task = this.taskService.createTaskQuery().taskCandidateOrAssigned(userid).taskId(taskId).singleResult();
		if (task == null) {
			throw new ActivitiObjectNotFoundException("任务不存在！");
		}
		// 根据任务查询流程实例
		String processInstanceId = task.getProcessInstanceId();
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
				.singleResult();
		// 评论人的id 一定要写，不然查看的时候会报错，没有用户
		this.identityService.setAuthenticatedUserId(userid);

		if (content != null) {
			this.taskService.addComment(taskId, pi.getId(), content);
		}
		taskService.setVariablesLocal(task.getId(), variables);
		// 完成委派任务
		if (DelegationState.PENDING == task.getDelegationState()) {
			this.taskService.resolveTask(taskId, variables);
			// return;
		}
		// 正常完成任务
		this.taskService.complete(taskId, variables);
	}

	@Override
	public List<ProcessInstance> listRuningProcess(PageParam<ProcessInstanceEntity> page) throws Exception {
		// ProcessInstanceQuery processInstanceQuery =
		// runtimeService.createProcessInstanceQuery();
		// page.setTotal(processInstanceQuery.list().size());
		// List<ProcessInstance> list =
		// processInstanceQuery.orderByProcessInstanceId().desc().listPage(page.getStart(),
		// (int) page.getPageSize());

		NativeProcessInstanceQuery processInstanceQuery = runtimeService.createNativeProcessInstanceQuery()
				.sql("SELECT DISTINCT CASE WHEN RES.`ACT_ID_` IS NULL THEN RES2.`ACT_ID_` ELSE RES.`ACT_ID_` END AS ACT_ID_,"
						+ " RES.*, P.KEY_ AS ProcessDefinitionKey, P.ID_ AS ProcessDefinitionId, P.NAME_ AS ProcessDefinitionName,"
						+ " P.VERSION_ AS ProcessDefinitionVersion, P.DEPLOYMENT_ID_ AS DeploymentId FROM ACT_RU_EXECUTION RES "
						+ "INNER JOIN ACT_RE_PROCDEF P ON RES.PROC_DEF_ID_ = P.ID_ "
						+ "LEFT JOIN `act_ru_execution` RES2 ON res.`PROC_INST_ID_` = res2.`PROC_INST_ID_` AND res.`ID_` = res2.`PARENT_ID_` "
						+ "WHERE RES.PARENT_ID_ IS NULL ORDER BY RES.ID_ DESC");

		page.setTotal(processInstanceQuery.list().size());
		List<ProcessInstance> list = processInstanceQuery.listPage(page.getStart(), (int) page.getPageSize());
		return list;
	}

	@Override
	public void activateProcessInstance(String processInstanceId) throws Exception {
		runtimeService.activateProcessInstanceById(processInstanceId);
	}

	@Override
	public void suspendProcessInstance(String processInstanceId) throws Exception {
		runtimeService.suspendProcessInstanceById(processInstanceId);
	}

	@Override
	public void deleteProcess(String processInstanceId, String deleteReason) {
		runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
	}

	/**
	 * 撤回任务
	 */
	@Override
	@Transactional
	public Integer revoke(String historyTaskId, String processInstanceId) throws Exception {
		Command<Integer> cmd = new RevokeTaskCmd(historyTaskId, processInstanceId, this.runtimeService,
				this.workflowService, this.historyService);
		Integer revokeFlag = this.processEngine.getManagementService().executeCommand(cmd);
		return revokeFlag;
	}

	@Override
	public Object withdrawTask(String instanceId, String userId) {
		// runtimeService.deleteProcessInstance(instanceId, "revoke");
		// return null;
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(instanceId).singleResult();
		Result result = this.canWithdraw(processInstance, userId);
		if (!result.isSuccess()) {
			return new Result(false, "不可撤回", "该任务已经被签收或者办理，无法撤回，请查看流程明细");
		} else {
			HistoricTaskInstance taskInstance = (HistoricTaskInstance) result.getData();
			final TaskEntity task = (TaskEntity) taskService.createTaskQuery().processInstanceId(instanceId)
					.singleResult();
			try {
				this.jumpTask(task, taskInstance.getTaskDefinitionKey());
				// 删除历史记录，填充签收人
				this.deleteCurrentTaskInstance(task.getId(), taskInstance);
				return new Result(true);
			} catch (Exception ex) {
				return new Result(false, "撤回异常", "任务撤回发生异常,异常原因：" + ex.getMessage());
			}
		}
	}

	public Result canWithdraw(HistoricProcessInstance processInstance, String userId) {
		List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery().processUnfinished()
				.processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().orderByTaskId().desc()
				.list();
		// List<HistoricTaskInstance> taskInstances =
		// historyService.createHistoricTaskInstanceQuery()
		// .processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().orderByTaskId().desc()
		// .list();
		// Task
		// activeTask=taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		if (taskInstances.isEmpty() || taskInstances.size() < 2)
			return new Result(false, null, "已办理，不可撤回");
		else {
			HistoricTaskInstance taskInstance = taskInstances.get(1);
			HistoricTaskInstance taskCurrent = taskInstances.get(0);
			// 流程审批人未未指定（未签收+未办理）
			if (StringUtils.isEmpty(taskCurrent.getAssignee())) {
				if (taskInstance.getAssignee() != null && taskInstance.getAssignee().equals(userId)) {
					return new Result(true, taskInstance, "可以撤回");
				} else if (StringUtils.isEmpty(taskInstance.getAssignee())) {
					return new Result(true, taskInstance, "可以撤回");
				}
			}
			// 流程定义时指定了办理人，也可以撤回
			else if (getTaskState(taskCurrent.getId())) {
				if (taskInstance.getAssignee() != null && taskInstance.getAssignee().equals(userId)) {
					return new Result(true, taskInstance, "可以撤回");
				} else if (StringUtils.isEmpty(taskInstance.getAssignee())) {
					return new Result(true, taskInstance, "可以撤回");
				}
			}
		}
		return new Result(false, null, "任务被签收或办理，不可撤回");
	}

	@Override
	public Result canWithdraw(String processInstanceId, String userId) {
		List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery().processUnfinished()
				.processInstanceId(processInstanceId).orderByTaskCreateTime().desc().orderByTaskId().desc().list();
		// List<HistoricTaskInstance> taskInstances =
		// historyService.createHistoricTaskInstanceQuery()
		// .processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().orderByTaskId().desc()
		// .list();
		// Task
		// activeTask=taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		if (taskInstances.isEmpty() || taskInstances.size() < 2)
			return new Result(false, null, "已办理，不可撤回");
		else {
			HistoricTaskInstance taskInstance = taskInstances.get(1);
			HistoricTaskInstance taskCurrent = taskInstances.get(0);
			// 流程审批人未未指定（未签收+未办理）
			if (StringUtils.isEmpty(taskCurrent.getAssignee())) {
				if (taskInstance.getAssignee() != null && taskInstance.getAssignee().equals(userId)) {
					return new Result(true, taskInstance, "可以撤回");
				} else if (StringUtils.isEmpty(taskInstance.getAssignee())) {
					return new Result(true, taskInstance, "可以撤回");
				}
			}
			// 流程定义时指定了办理人，也可以撤回
			else if (getTaskState(taskCurrent.getId())) {
				if (taskInstance.getAssignee() != null && taskInstance.getAssignee().equals(userId)) {
					return new Result(true, taskInstance, "可以撤回");
				} else if (StringUtils.isEmpty(taskInstance.getAssignee())) {
					return new Result(true, taskInstance, "可以撤回");
				}
			}
		}
		return new Result(false, null, "任务被签收或办理，不可撤回");
	}

	// 获取流程状态，判断当前节点的办理人是指定的办理人还是签收的办理人
	// true=指定的审批人（可以撤回） false=签收后产生的审批人（不可撤回）
	public boolean getTaskState(String taskId) {
		try {
			List<IdentityLink> identiyLinks = taskService.getIdentityLinksForTask(taskId);
			for (IdentityLink identiyLink : identiyLinks) {
				if (IdentityLinkType.CANDIDATE.equals(identiyLink.getType())) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 流程跳跃到任意节点
	 *
	 * @param currentTaskEntity
	 *            当前任务实例
	 * @param targetTaskDefinitionKey
	 *            任务定义节点key(目标节点)
	 * @throws Exception
	 */
	@Transactional
	public void jumpTask(final TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception {
		((RuntimeServiceImpl) runtimeService).getCommandExecutor()
				.execute(new WithdrawTaskCmd(targetTaskDefinitionKey, currentTaskEntity));

		/*
		 * ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)
		 * repositoryService
		 * .getProcessDefinition(currentTaskEntity.getProcessDefinitionId());
		 * final ActivityImpl activity =
		 * processDefinition.findActivity(targetTaskDefinitionKey); final
		 * ActivityImpl currentActivity =
		 * processDefinition.findActivity(currentTaskEntity.getTaskDefinitionKey
		 * ()); final ExecutionEntity execution = (ExecutionEntity)
		 * runtimeService.createExecutionQuery()
		 * .executionId(currentTaskEntity.getExecutionId()).singleResult();
		 * 
		 * final Object nrOfInstances =
		 * runtimeService.getVariable(execution.getId(), "nrOfInstances"); final
		 * Object nrOfCompletedInstances =
		 * runtimeService.getVariable(execution.getId(),
		 * "nrOfCompletedInstances"); final Object loopCounter =
		 * runtimeService.getVariable(execution.getId(), "loopCounter"); final
		 * Object nrOfActiveInstances =
		 * runtimeService.getVariable(execution.getId(), "nrOfActiveInstances");
		 * System.out.println(nrOfInstances);
		 * System.out.println(nrOfCompletedInstances);
		 * System.out.println(loopCounter);
		 * System.out.println(nrOfActiveInstances);
		 * 
		 * // 包装一个Command对象 ((RuntimeServiceImpl)
		 * runtimeService).getCommandExecutor().execute(new Command<Void>() {
		 * 
		 * @Override public Void execute(CommandContext commandContext) {
		 * 
		 * // 删除当前的任务 // 不能删除当前正在执行的任务，所以要先清除掉关联
		 * currentTaskEntity.setExecutionId(null);
		 * taskService.saveTask(currentTaskEntity);
		 * taskService.deleteTask(currentTaskEntity.getId(), true);
		 * 
		 * if (nrOfActiveInstances != null && (Integer)loopCounter == 0) {
		 * runtimeService.removeVariable(execution.getId(), "nrOfInstances");
		 * runtimeService.removeVariable(execution.getId(),
		 * "nrOfCompletedInstances");
		 * runtimeService.removeVariable(execution.getId(), "loopCounter");
		 * runtimeService.removeVariable(execution.getId(),
		 * "nrOfActiveInstances"); } else if (nrOfActiveInstances != null &&
		 * (Integer) nrOfActiveInstances > 0) {
		 * runtimeService.setVariable(execution.getId(), "nrOfActiveInstances",
		 * 1); runtimeService.setVariable(execution.getId(), "loopCounter",
		 * (Integer) loopCounter - 1);
		 * runtimeService.setVariable(execution.getId(),
		 * "nrOfCompletedInstances", (Integer) nrOfCompletedInstances - 1); }
		 * else { }
		 * 
		 * // 创建新任务 if (StringUtils.isNotBlank(execution.getParentId()) &&
		 * currentActivity.getActivityBehavior() instanceof
		 * UserTaskActivityBehavior) { String execParentId =
		 * execution.getParentId(); ExecutionEntityManager
		 * executionEntityManager = commandContext.getExecutionEntityManager();
		 * execution.deleteCascade(UserContext.getCurrentUser().getUserName() +
		 * "撤回"); ExecutionEntity executionParent =
		 * executionEntityManager.findExecutionById(execParentId);
		 * executionParent.executeActivity(activity); } else {
		 * execution.executeActivity(activity); } return null; } });
		 */
	}

	/**
	 * 删除历史记录，回填签收人以保证流程明细显示正确
	 * 
	 * @param taskId
	 * @param taskInstance
	 * @return
	 */
	@Transactional
	public Result deleteCurrentTaskInstance(String taskId, HistoricTaskInstance taskInstance) {
		// 删除正在执行的任务
		// 删除HistoricTaskInstance
		String sql_task = "delete from " + managementService.getTableName(HistoricTaskInstance.class) + " where "
				+ "ID_='" + taskId + "' or ID_='" + taskInstance.getId() + "'";
		historyService.createNativeHistoricDetailQuery().sql(sql_task).singleResult();
		// 删除HistoricActivityInstance
		String sql_activity = "delete from " + managementService.getTableName(HistoricActivityInstance.class)
				+ " where " + "TASK_ID_='" + taskId + "' or TASK_ID_='" + taskInstance.getId() + "'";
		historyService.createNativeHistoricDetailQuery().sql(sql_activity).singleResult();
		// 获取当前的任务,保存签收人
		// Task task =
		// taskService.createTaskQuery().executionId(taskInstance.getExecutionId()).singleResult();
		// FIXME 逻辑存在问题，旧的任务办理人付给了新的任务
//		Task task = taskService.createTaskQuery().processInstanceId(taskInstance.getProcessInstanceId()).singleResult();
//		task.setAssignee(taskInstance.getAssignee());
//		task.setOwner(taskInstance.getOwner());
//		taskService.saveTask(task);
//		// 解决HistoricActivityInstance的Assignee为空的现象
//		if (!StringUtils.isEmpty(taskInstance.getAssignee())) {
//			String sql_update = "update " + managementService.getTableName(HistoricActivityInstance.class) + " set "
//					+ "ASSIGNEE_='" + taskInstance.getAssignee() + "' where TASK_ID_='" + task.getId() + "'";
//			historyService.createNativeHistoricDetailQuery().sql(sql_update).singleResult();
//		}
//
//		String sql_update_execution = "update " + managementService.getTableName(Execution.class) + " set "
//				+ "ACT_ID_='" + taskInstance.getTaskDefinitionKey() + "' where ID_='" + taskInstance.getExecutionId()
//				+ "'";
//		historyService.createNativeHistoricDetailQuery().sql(sql_update_execution).singleResult();
		return new Result(true);
	}

	private TaskEntity getCurrentTask(String processInstanceId) {
		return (TaskEntity) this.taskService.createTaskQuery().processInstanceId(processInstanceId).active()
				.singleResult();
	}

	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 */
	@Override
	public void moveTo(String currentTaskId, String targetTaskDefinitionKey) throws Exception {
		TaskEntity taskEntity = (TaskEntity) this.taskService.createTaskQuery().taskId(currentTaskId).singleResult();
		moveTo(taskEntity, targetTaskDefinitionKey);
	}

	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 */
	@Override
	public void moveTo(TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception {
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) this.repositoryService)
				.getDeployedProcessDefinition(currentTaskEntity.getProcessDefinitionId());
		ActivityImpl activity = (ActivityImpl) processDefinitionEntity.findActivity(targetTaskDefinitionKey);

		moveTo(currentTaskEntity, activity);
	}

	@Transactional
	private void moveTo(TaskEntity currentTaskEntity, ActivityImpl activity) {
		Command<Void> deleteCmd = new DeleteActiveTaskCmd(currentTaskEntity, "jump", true);
		Command<Void> StartCmd = new StartActivityCmd(currentTaskEntity.getExecutionId(), activity);
		this.processEngine.getManagementService().executeCommand(deleteCmd);
		this.processEngine.getManagementService().executeCommand(StartCmd);
	}

	/**
	 * 前进
	 * 
	 * @param currentTaskEntity
	 * @throws Exception
	 */
	public void moveForward(TaskEntity currentTaskEntity) throws Exception {
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) this.repositoryService)
				.getDeployedProcessDefinition(currentTaskEntity.getProcessDefinitionId());
		ActivityImpl activity = (ActivityImpl) processDefinitionEntity
				.findActivity(currentTaskEntity.getTaskDefinitionKey()).getOutgoingTransitions().get(0)
				.getDestination();

		moveTo(currentTaskEntity, activity);
	}

	/**
	 * 前进
	 * 
	 * @param currentTaskId
	 * @throws Exception
	 */
	public void moveForward(String currentTaskId) throws Exception {
		TaskEntity taskEntity = (TaskEntity) this.taskService.createTaskQuery().taskId(currentTaskId).singleResult();
		moveForward(taskEntity);
	}

	/**
	 * 回退上一步
	 * 
	 * @param currentTaskEntity
	 * @throws Exception
	 */
	public void moveBack(TaskEntity currentTaskEntity) throws Exception {
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) this.repositoryService)
				.getDeployedProcessDefinition(currentTaskEntity.getProcessDefinitionId());
		ActivityImpl activity = (ActivityImpl) processDefinitionEntity
				.findActivity(currentTaskEntity.getTaskDefinitionKey()).getIncomingTransitions().get(0).getSource();

		moveTo(currentTaskEntity, activity);
	}

	/**
	 * 回退上一步
	 * 
	 * @param currentTaskId
	 * @throws Exception
	 */
	public void moveBack(String currentTaskId) throws Exception {
		TaskEntity taskEntity = (TaskEntity) this.taskService.createTaskQuery().taskId(currentTaskId).singleResult();
		moveBack(taskEntity);
	}

	@Override
	public void addProcessByDynamic() throws Exception {
		// this.repositoryService.deleteDeployment("5003", true);
		// this.repositoryService.deleteDeployment("5007", true);

		final String PROCESSID = "process_test_1";
		BpmnModel model = new BpmnModel();
		Process process = new Process();

		process.setId(PROCESSID);
		process.setName("动态流程测试");

		process.addFlowElement(createStartEvent());
		process.addFlowElement(createUserTask("userTask1", "用户任务1"));
		process.addFlowElement(createExclusiveGateway("gateway1"));
		process.addFlowElement(createUserTask("userTask2", "用户任务2"));
		process.addFlowElement(createExclusiveGateway("gateway2"));
		process.addFlowElement(createUserTask("userTask3", "用户任务3"));
		process.addFlowElement(createEndEvent());

		process.addFlowElement(createSequenceFlow("startEvent", "userTask1", "flow1", "", ""));
		process.addFlowElement(createSequenceFlow("userTask1", "gateway1", "flow2", "", ""));
		process.addFlowElement(createSequenceFlow("gateway1", "userTask2", "flow3", "同意", "${isPass}"));
		process.addFlowElement(createSequenceFlow("gateway1", "userTask3", "flow4", "不同意", "${!isPass}"));
		process.addFlowElement(createSequenceFlow("userTask2", "endEvent", "flow5", "", ""));
		process.addFlowElement(createSequenceFlow("userTask3", "gateway2", "flow6", "", ""));
		process.addFlowElement(createSequenceFlow("gateway2", "userTask1", "flow7", "同意", "${reApply}"));
		process.addFlowElement(createSequenceFlow("gateway2", "endEvent", "flow8", "结束", "${!reApply}"));

		model.addProcess(process);

		// 生成流程图片信息
		BpmnAutoLayout bpmnAutoLayout = new BpmnAutoLayout(model);
		bpmnAutoLayout.execute();

		// 部署流程
		Deployment deployment = this.repositoryService.createDeployment().addBpmnModel(PROCESSID + ".bpmn", model)
				.name("动态流程测试").deploy();

		// // 启动流程 ProcessInstance processInstance =
		// this.runtimeService.startProcessInstanceByKey(PROCESSID);
		//
		// // 导出流程图片 InputStream processDiagram =
		// this.repositoryService.getProcessDiagram(processInstance.
		// getProcessDefinitionId());
		// FileUtils.copyInputStreamToFile(processDiagram, new
		// File("D:/deployments/"+PROCESSID+".png"));
		//
		// // 导出流程文件(BPMN xml) InputStream processBpmn =
		// this.repositoryService.getResourceAsStream(deployment.getId(),
		// PROCESSID+".bpmn"); FileUtils.copyInputStreamToFile(processBpmn,new
		// File("D:/deployments/"+PROCESSID+".bpmn"));

	}

	/**
	 * 创建开始节点
	 * 
	 * @return
	 */
	protected static StartEvent createStartEvent() {
		StartEvent startEvent = new StartEvent();
		startEvent.setId("startEvent");
		startEvent.setName("start");
		startEvent.setInitiator("startUserId");
		return startEvent;
	}

	/**
	 * 创建结束节点
	 * 
	 * @return
	 */
	protected static EndEvent createEndEvent() {
		EndEvent endEvent = new EndEvent();
		endEvent.setId("endEvent");
		endEvent.setName("end");
		return endEvent;
	}

	/**
	 * 创建用户任务节点
	 * 
	 * @param id
	 * @param name
	 * @return
	 */
	protected static UserTask createUserTask(String id, String name) {
		List<ActivitiListener> taskListeners = new ArrayList<ActivitiListener>();
		ActivitiListener listener = new ActivitiListener();
		listener.setId("");
		listener.setEvent("create");
		listener.setImplementationType("delegateExpression");
		listener.setImplementation("${userTaskListener}");
		taskListeners.add(listener);

		UserTask userTask = new UserTask();
		userTask.setId(id);
		userTask.setName(name);
		userTask.setTaskListeners(taskListeners);
		userTask.setDocumentation(""); // 说明
		return userTask;
	}

	/**
	 * 创建节点间的连线
	 * 
	 * @param from
	 * @param to
	 * @param id
	 * @param name
	 * @param conditionExpression
	 * @return
	 */
	protected static SequenceFlow createSequenceFlow(String from, String to, String id, String name,
			String conditionExpression) {
		SequenceFlow flow = new SequenceFlow();
		flow.setId(id);
		flow.setName(name);
		flow.setSourceRef(from);
		flow.setTargetRef(to);
		if (StringUtils.isNotBlank(conditionExpression)) {
			flow.setConditionExpression(conditionExpression);
		}
		return flow;
	}

	/**
	 * 创建排他网关
	 * 
	 * @param id
	 * @return
	 */
	protected static ExclusiveGateway createExclusiveGateway(String id) {
		ExclusiveGateway gateway = new ExclusiveGateway();
		gateway.setId(id);
		return gateway;
	}
}
