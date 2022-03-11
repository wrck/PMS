package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.BeanMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.activiti.process.exception.CustomActivitiException;
import com.dp.plat.activiti.service.IProcessService;
import com.dp.plat.activiti.service.impl.RuntimePageService;
import com.dp.plat.activiti.vo.ActivityVo;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.service.IPmWorkFlowService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectTaskService;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.WORKFLOW_MANAGER)
public class WorkFlowController extends AbstractController<IPmWorkFlowService, PmWorkFlow, PmWorkFlowVO> {

	@Autowired
	private IProcessService processService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private RuntimePageService runtimePageService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private IPmWorkFlowService pmWorkFlowService;

	@Autowired
	private IProjectHeaderService projectHeaderService;

	@Autowired
	private IProjectTaskService projectTaskService;

	@Autowired
	private IIndustryAssetService industryAssetService;

	@Autowired
	private IIndustryLeakService industryLeakService;

	@PostConstruct
	private void init() {
		this.setUrlNameSpace("/");
		this.setViewModel("workflow");
	}

	@Override
	@RequestMapping
	public String home(Model model) {
		return super.home(model);
	}

	@Override
	@RequestMapping(value = { "/list"})
	public String list(PageParam<Object> pageParam, PmWorkFlowVO v, Model model) {
		return super.list(pageParam, v, model);
	}
	
	@RequestMapping(value = { "/info/list"})
	public String info(PmWorkFlowVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":list")) {
			model.addAttribute("data", Collections.emptyList());
			return Consts.VIEW_UNAUTHORIZED;
		}
		List<PmWorkFlow> workFlows = service.selectBySelective(v);
		Set<String> procInstIds = new HashSet<String>(workFlows.size());
		for (PmWorkFlow pmWorkFlow : workFlows) {
			procInstIds.add(pmWorkFlow.getProcInstId());
		}
		List<ActivityVo> data = runtimePageService.getActivityList(procInstIds);
		model.addAttribute("data", data);
		model.addAttribute("columns", findColumnList("workflowInfoList"));
		return getRealViewNameSpace() + "";
	}

	@Override
	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(null, model, getDataName() + ":detail")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			PmWorkFlow v = pmWorkFlowService.selectByPrimaryKey(id);
			if (v != null) {
				Principal currentUser = UserContext.getCurrentPrincipal();
				String assignee = currentUser.getUserInfoId().toString();
				String taskId = (String) HttpContext.getCurrentRequest().getParameter("taskId");
				String processInstanceId = v.getProcInstId();
				TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstanceId);
//				if (StringUtils.isNotBlank(taskId)) {
//					taskQuery.taskId(taskId);
//				}
				TaskInfo task = taskQuery.taskCandidateOrAssigned(assignee).active().singleResult();
				Boolean hasTask = task != null;
				if (task == null) {
					HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService
							.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId);
					if (StringUtils.isNotBlank(taskId)) {
						historicTaskInstanceQuery.taskId(taskId);
					}
					List<HistoricTaskInstance> list = historicTaskInstanceQuery.or().taskAssignee(assignee)
							.taskCandidateUser(assignee)
							/* .taskCandidateGroupIn(new ArrayList<String>(currentUser.getRoles())) */
							.taskInvolvedGroupsIn(new ArrayList<String>(currentUser.getRoles())) // 不加会使用inner join，直接指定办理人的历史任务无法查询出来
							.endOr().list();
					if (!list.isEmpty()) {
						task = list.get(Math.max(0, list.size() - 1));
					}
				} else {
					taskId = task.getId();
				}
				if (task == null) {
					return "redirect:/" + Consts.VIEW_UNAUTHORIZED + ".html";
				}

				v.setTaskId(task.getId());
				v.setTitle(task.getDescription() != null ? task.getDescription() : task.getName());
				v.setTaskKey(task.getTaskDefinitionKey());
				v.setProcInstId(task.getProcessInstanceId());
				v.setProcessKey(task.getProcessDefinitionId());
				v.setHasTask(hasTask);
				
				String dataType = v.getDataType();
				Boolean hideEntity = Boolean.valueOf(HttpContext.getCurrentRequest().getParameter("hideEntity"));
				if (!hideEntity) {
					PmWorkFlow pmWorkFlow = null;
					if (hasTask) {
						pmWorkFlow = taskService.getVariable(taskId, "entity", PmWorkFlow.class);
					} else {
						pmWorkFlow = (PmWorkFlow) historyService.createHistoricVariableInstanceQuery()
								.processInstanceId(processInstanceId).variableName("entity").singleResult().getValue();
					}
					Object entity = decoratorEntity(pmWorkFlow);
					List<Object> fieldList = this.findFieldList(dataType + "Form", DATATYPE_FORM);
					for (Iterator<?> iterator = fieldList.iterator(); iterator.hasNext();) {
						DataFieldRelation field = (DataFieldRelation) iterator.next();
						field.setReadonly(true);
						field.setDisabled(true);
					}
					model.addAttribute("fieldList", fieldList);
					model.addAttribute("targetValue", entity);
					model.addAttribute("tabList", this.findNavTabList(dataType + "Tab", model));
				}
				model.addAttribute("hideEntity", hideEntity);

				List<Object> fieldList = this.findFieldList(dataType + "_" + v.getTaskKey() + "_workflowForm", DATATYPE_FORM);
				if (fieldList == null || fieldList.isEmpty()) {
					fieldList = this.findFieldList(dataType + "_workflowForm", DATATYPE_FORM);
				}
				if (!hasTask) {
					for (Iterator<?> iterator = fieldList.iterator(); iterator.hasNext();) {
						DataFieldRelation field = (DataFieldRelation) iterator.next();
						field.setReadonly(true);
						field.setDisabled(true);
					}
				}
				model.addAttribute("workflowFieldList", fieldList);
				model.addAttribute("workflow", v);
				model.addAttribute("targetName", "workflow");

//				model.addAttribute("workflowTabList", this.findNavTabList(getDataNameNavTab(), model));
			}
		} else {
			// model.addAttribute("urlNamespace", URLPath.WORKFLOW_MANAGER);
			// model.addAttribute("model", "workflow");
			model.addAttribute("keyword", "id");

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return "workflow/detail";
	}
	
	@RequestMapping(value = { "/task/{taskId}", "/task/modals/{taskId}" })
	public String findOneByTaskId(@PathVariable("taskId") String taskId, Model model) {
		if (!checkPermission(null, model, getDataName() + ":detail")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			Principal currentUser = UserContext.getCurrentPrincipal();
			String assignee = currentUser.getUserInfoId().toString();
			TaskQuery taskQuery = taskService.createTaskQuery();
			taskQuery.taskId(taskId);
			TaskInfo task = taskQuery.taskCandidateOrAssigned(assignee).active().singleResult();
			Boolean hasTask = task != null;
			if (task == null) {
				HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService
						.createHistoricTaskInstanceQuery();
				historicTaskInstanceQuery.taskId(taskId);
				List<HistoricTaskInstance> list = historicTaskInstanceQuery.or().taskAssignee(assignee).taskCandidateUser(assignee)/*.taskCandidateGroupIn(new ArrayList<String>(currentUser.getRoles()))*/.endOr().list();
				if (!list.isEmpty()) {
					task = list.get(Math.max(0, list.size() - 1));
				}
			} else {
				taskId = task.getId();
			}
			if (task == null) {
				return "redirect:/" + Consts.VIEW_UNAUTHORIZED + ".html";
			}

			PmWorkFlow pmWorkFlow = null;
			if (hasTask) {
				pmWorkFlow = taskService.getVariable(taskId, "entity", PmWorkFlow.class);
			} else {
				pmWorkFlow = (PmWorkFlow) historyService.createHistoricVariableInstanceQuery()
						.processInstanceId(task.getProcessInstanceId()).variableName("entity").singleResult().getValue();
			}
			pmWorkFlow.setProcInstId(task.getProcessInstanceId());
			
			String dataType = pmWorkFlow.getDataType();
			Boolean hideEntity = Boolean.valueOf(HttpContext.getCurrentRequest().getParameter("hideEntity"));
			if (!hideEntity) {
				Object entity = decoratorEntity(pmWorkFlow);
				List<Object> fieldList = this.findFieldList(dataType + "Form", DATATYPE_FORM);
				for (Iterator<?> iterator = fieldList.iterator(); iterator.hasNext();) {
					DataFieldRelation field = (DataFieldRelation) iterator.next();
					field.setReadonly(true);
					field.setDisabled(true);
				}
				model.addAttribute("fieldList", fieldList);
				model.addAttribute("targetValue", entity);
				model.addAttribute("tabList", this.findNavTabList(dataType + "Tab", model));
			}
			model.addAttribute("hideEntity", hideEntity);

			PmWorkFlow v = new PmWorkFlow();
			v.setTaskId(task.getId());
			v.setTitle(task.getDescription() != null ? task.getDescription() : task.getName());
			v.setTaskKey(task.getTaskDefinitionKey());
			v.setProcInstId(task.getProcessInstanceId());
			v.setProcessKey(task.getProcessDefinitionId());
			v.setHasTask(hasTask);
			
			List<Object> fieldList = this.findFieldList(dataType + "_" + v.getTaskKey() + "_workflowForm", DATATYPE_FORM);
			if (fieldList == null || fieldList.isEmpty()) {
				fieldList = this.findFieldList(dataType + "_workflowForm", DATATYPE_FORM);
			}
			if (!hasTask) {
				for (Iterator<?> iterator = fieldList.iterator(); iterator.hasNext();) {
					DataFieldRelation field = (DataFieldRelation) iterator.next();
					field.setReadonly(true);
					field.setDisabled(true);
				}
			}
			model.addAttribute("workflowFieldList", fieldList);
			model.addAttribute("workflow", v);
			model.addAttribute("targetName", "workflow");
			model.addAttribute("workflowTabList", this.findNavTabList(getDataNameNavTab(), model));
		} else {
			// model.addAttribute("urlNamespace", URLPath.WORKFLOW_MANAGER);
			 model.addAttribute("model", "workflowTask");
			model.addAttribute("keyword", "id");
			model.addAttribute("id", taskId);

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return "workflow/detail";
	}
	
	@RequestMapping(value = { "/task/{taskId}/check" })
	public void checkTask(@PathVariable("taskId") String taskId, Model model) {
		if (HttpContext.isJSON()) {
			Principal currentUser = UserContext.getCurrentPrincipal();
			String assignee = currentUser.getUserInfoId().toString();
			String procInstId = HttpContext.getCurrentRequest().getParameter("procInstId");
			TaskQuery taskQuery = taskService.createTaskQuery();
			if (StringUtils.isNotBlank(procInstId)) {
				taskQuery.processInstanceId(procInstId);
			} else {
				taskQuery.taskId(taskId);
			}
			TaskInfo task = taskQuery.taskCandidateOrAssigned(assignee).active().singleResult();
			Boolean hasTask = task != null;
			if (hasTask) {
				model.addAttribute("currentTaskId", task.getId());
			}
			model.addAttribute("hasTask", hasTask);
		}
	}

	/**
	 * 完成任务controller
	 */
	@RequestMapping(value = "/complete/{taskId}", method = RequestMethod.POST)
	public void complete(Boolean isPass, String content, String data, @PathVariable("taskId") String taskId,
			Model model) throws Exception {
		User user = UserContext.getCurrentUser();
		// 判断当前登入用户是否为当前任务办理人
		// Task task =
		// taskService.createTaskQuery().taskId(taskId).singleResult();
		// if (!task.getAssignee().equals(user.getUserCustom4() + "")) {
		// return;
		// }
		// 考虑存在候选任务的办理问题
		Task task = taskService.createTaskQuery().taskId(taskId)
				.taskCandidateOrAssigned(String.valueOf(user.getUserCustom4())).singleResult();
		if (task == null) {
			return;
		}

		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			if (isPass != null) {
				variables.put("isPass", isPass);
			}
			// 接收审批意见之外的附加业务数据 TODO 考虑多参数的问题
			if (StringUtils.isNotBlank(data)) {
				variables.put("data", data);
			}
			// 完成任务
			// perf中userCustom4存放empID
			String empID = String.valueOf(user.getUserCustom4());
			// 如果empID不为空，则使用empID，如果为空则用userID
			String assigneeID = String.valueOf(StringUtils.isBlank(empID) ? user.getUserId() : empID);
			processService.complete(taskId, content, assigneeID, variables);

			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "任务办理完成！");
		} catch (ActivitiObjectNotFoundException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务不存在，请联系管理员！");
			ExceptionHandler.insertException(e);
			// throw e;
		} catch (ActivitiException e) {
			String defaultErrMsg = "此任务正在协办，您不能办理此任务！";
			String errorMsg = "";
			Exception ee = e;
			while (ee.getCause() != null) {
				ee = (Exception) ee.getCause();
				if (ee instanceof CustomActivitiException || e.getClass().equals(Exception.class)) {
					errorMsg = ee.getMessage();
					break;
				}
			}
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", StringUtils.isNotBlank(errorMsg) ? errorMsg : defaultErrMsg);
			ExceptionHandler.insertException(e);
			// throw e;
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "任务办理失败，请联系管理员！");
			ExceptionHandler.insertException(e);
			// throw e;
		}
		return;
	}

	/**
	 * 完成任务controller
	 */
	@RequestMapping(value = "/complete/batch", method = RequestMethod.POST)
	public void batchComplete(@RequestParam("approvalData") String approvalData, Model model) throws Exception {
		// User user = UserContext.getCurrentUser();
		// List<QuickApprovalVO> approvalVOs = JSON.parseArray(approvalData,
		// QuickApprovalVO.class);
		// int total = approvalVOs.size();
		// int success = 0;
		// for (QuickApprovalVO quickApprovalVO : approvalVOs) {
		// try {
		// Map<String, Object> variables = new HashMap<String, Object>();
		// String taskId = quickApprovalVO.getTaskId();
		// //任务所有权校验
		// Task task =
		// taskService.createTaskQuery().taskId(taskId).taskCandidateOrAssigned(String.valueOf(user.getUserCustom4())).singleResult();
		// if (task == null) {
		// return;
		// }
		// String content = quickApprovalVO.getContent();
		// Boolean isPass = quickApprovalVO.getIsPass();
		// if (isPass != null) {
		// variables.put("isPass", isPass);
		// }
		// // 完成任务
		// // perf中userCustom4存放empID
		// String empID = String.valueOf(user.getUserCustom4());
		// // 如果empID不为空，则使用empID，如果为空则用userID
		// String assigneeID = String.valueOf(StringUtils.isBlank(empID) ?
		// user.getUserId() : empID);
		// processService.complete(taskId, content, assigneeID, variables);
		// success++;
		// } catch (Exception e) {
		// ExceptionHandler.insertException(e);
		// }
		// }
		// model.addAttribute("message", "任务总数：" + total + "，成功办理：" + success +
		// "，失败：" + (total - success));
		// return;
	}

	/**
	 * 评价评估controller
	 */
	@RequestMapping(value = "/evaluate/batch", method = RequestMethod.POST)
	public void batchEvaluate(@RequestParam("approvalData") String approvalData, Model model) throws Exception {
		// User user = UserContext.getCurrentUser();
		// List<QuickApprovalVO> approvalVOs = JSON.parseArray(approvalData,
		// QuickApprovalVO.class);
		// int total = approvalVOs.size();
		// int success = 0;
		// for (QuickApprovalVO quickApprovalVO : approvalVOs) {
		// try {
		// Map<String, Object> variables = new HashMap<String, Object>();
		// String taskId = quickApprovalVO.getTaskId();
		// String content = quickApprovalVO.getContent();
		// String data = quickApprovalVO.getData();
		// Boolean isPass = quickApprovalVO.getIsPass();
		// if (isPass != null) {
		// variables.put("isPass", isPass);
		// }
		// variables.put("data", data);
		// // 完成任务
		// // perf中userCustom4存放empID
		// String empID = String.valueOf(user.getUserCustom4());
		// // 如果empID不为空，则使用empID，如果为空则用userID
		// String assigneeID = String.valueOf(StringUtils.isBlank(empID) ?
		// user.getUserId() : empID);
		// processService.complete(taskId, content, assigneeID, variables);
		// success++;
		// } catch (Exception e) {
		// ExceptionHandler.insertException(e);
		// }
		// }
		// model.addAttribute("message", "任务总数：" + total + "，成功办理：" + success +
		// "，失败：" + (total - success));
		// return;
	}

	// 关闭流程
	@RequestMapping("/test/closeProcess")
	public void closeProcess(String ids) {
		if (UserContext.hasRole(RoleConstant.ROLE_ADMIN)) {
			int n = 0;
			for (String id : ids.split(",")) {
				runtimeService.deleteProcessInstance(id, "关闭测试流程");
				n++;
			}
			System.out.println("成功删除" + n + "个流程！");
		}
		;
	}

	/**
	 * 撤回任务
	 *
	 * @param instanceId
	 *            历史流程节点中的ID
	 * @return
	 */
	@RequestMapping(value = "withdraw/{instanceId}/{userId}", method = RequestMethod.POST)
	public void withdrawTask(@PathVariable("instanceId") String instanceId, @PathVariable("userId") String userId,
			Model model) {
		Integer userInfoId = UserContext.getCurrentPrincipal().getUserInfoId();
		Result result = (Result) processService.withdrawTask(instanceId, userInfoId.toString());
		// PmWorkFlow pmWorkFlow = new PmWorkFlow();
		// pmWorkFlow.setProcInstId(instanceId);
		// List<PmWorkFlow> pmWorkFlows =
		// pmWorkFlowService.selectBySelective(pmWorkFlow);
		// Set<Integer> planIds = new HashSet<>();
		// for (PmWorkFlow temp : pmWorkFlows) {
		// if (!planIds.contains(temp.getPlanId())) {
		// planParticipantService.updatePlanStatusAndPlanStepStatus(temp.getPlanId());
		// planIds.add(temp.getPlanId());
		// }
		// }
		BeanMap properties = new BeanMap(result);
		model.mergeAttributes(properties);
		return;
	}

	@RequestMapping(value = "/startProcess", method = RequestMethod.POST)
	public void startProcess(PmWorkFlow pmWorkFlow, Model model) {

		try {
			// Plan plan = planService.selectByPrimaryKey(planId);
			// // 当前用户权限检查
			// if (!checkCurrentUserRolePermission(plan.getDepID())) {
			// model.addAttribute("status", false);
			// model.addAttribute("message", "没有权限进行该操作！");
			// return;
			// }
			// PmWorkFlow planWorkFlow = planService.initPmWorkFlow(planId,
			// planStepId);
			// List<PlanParticipant> planParticipants =
			// planParticipantService.selectBySelective(new
			// PlanParticipant(planId));
			// String processKey = planWorkFlow.getProcessKey();
			// if (PlanProcessKey.PERFORMANCE_ALL_KEY.equals(processKey)) {
			// //planWorkFlow.setProcessKey(PlanProcessKey.PERFORMANCE_ALL_KEY);
			// planService.startProcess(planWorkFlow, planParticipants);
			// } else if
			// (PlanProcessKey.APPROVE_OBJECTIVE_KEY.equals(processKey)) {
			// //planWorkFlow.setProcessKey(PlanProcessKey.APPROVE_OBJECTIVE_KEY);
			// planService.startProcess(planWorkFlow, planParticipants);
			// } else if
			// (PlanProcessKey.EVALUATE_OBJECTIVE_KEY.equals(processKey)) {
			// //planWorkFlow.setProcessKey(PlanProcessKey.EVALUATE_OBJECTIVE_KEY);
			// planService.startProcess(planWorkFlow, planParticipants);
			String processKey = pmWorkFlow.getProcessKey();
			if (ProjectConstant.ProcessType.QUALITY_APPROVE_TRACK.equals(processKey)) {
				String objType = pmWorkFlow.getObjType();
				Integer objId = pmWorkFlow.getObjId();
				String dataType = pmWorkFlow.getDataType();
				Integer dataId = pmWorkFlow.getDataId();
				// 对象类型为project,检查是否有操作权限
				if (DataType.PROJECT.equals(objType)) {
					ProjectVO project = new ProjectVO();
					project.setProjectId(objId);
					PermissionResult permission = projectHeaderService.checkPermission(project, "project:edit",
							"projectTask:edit");
					if (!permission.isPermit()) {
						model.addAllAttributes(permission.getMap());
						return;
					}
					pmWorkFlow.setCustomInfoByKey("projectTypes", permission.getData());
				}
				// 项目任务流程发起
				Object entity = null;
				if (DataType.PROJECT_TASK.equals(dataType)) {
					entity = projectTaskService.selectByPrimaryKey(dataId);
				} else if (DataType.INDUSTRY_ASSET.equals(dataType)) {
					entity = industryAssetService.selectByPrimaryKey(dataId);
				} else if (DataType.INDUSTRY_LEAK.equals(dataType)) {
					entity = industryLeakService.selectByPrimaryKey(dataId);
				}
				if (entity != null) {
					pmWorkFlowService.startProcess(pmWorkFlow, entity);
					model.addAttribute("currentTaskId", pmWorkFlow.getTaskId());
					model.addAttribute("currentProcInstId", pmWorkFlow.getProcInstId());
				}
			} else {
				model.addAttribute("status", false);
				model.addAttribute("message", processKey + "流程不存在！");
				return;
			}
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "流程已启动！");
		} catch (ActivitiException e) {
			Integer errorLogId = ExceptionHandler.insertException(e);
			model.addAttribute("status", Boolean.FALSE);
			String errorMessage = "<br>错误信息：" + e.getClass().getSimpleName() + "<br>错误ID:" + errorLogId;
			if (e.getMessage().indexOf("no processes deployed with key") != -1) {
				model.addAttribute("message", "没有部署流程，请联系系统管理员，在[流程定义]中部署相应流程文件！" + errorMessage);
			} else {
				model.addAttribute("message", "启动流程失败，系统内部错误！" + errorMessage);
			}
			// throw e;
		} catch (Exception e) {
			Integer errorLogId = ExceptionHandler.insertException(e);
			String errorMessage = "<br>错误信息：" + e.getClass().getSimpleName() + "<br>错误ID:" + errorLogId;
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "启动流程失败，系统内部错误！" + errorMessage);
			// throw e;
		}
	}

	/**
	 * 完成任务
	 * 
	 * @param content
	 * @param completeFlag
	 * @param taskId
	 * @param redirectAttributes
	 * @param session
	 * @return
	 * @throws Exception
	 * @see {@link WorkFlowController}.complete
	 */
	@RequestMapping(value = "{processKey}/complete/{taskId}", method = RequestMethod.POST)
	@Deprecated
	public void complete(@PathVariable("processKey") String processKey,
			@RequestParam("businessKey") Integer businessKey, @RequestParam("content") String content,
			@RequestParam("isPass") Boolean isPass, @PathVariable("taskId") String taskId, Model model)
			throws Exception {
		User user = UserContext.getCurrentUser();
		try {
			// PmWorkFlow pmWorkFlow =
			// pmWorkFlowService.selectByPrimaryKey(businessKey);
			// Map<String, Object> variables = new HashMap<String, Object>();
			// if (PlanProcessKey.APPROVE_OBJECTIVE_KEY.equals(processKey)) {
			// Employee employee =
			// employeeService.selectByPrimaryKey(pmWorkFlow.getEmpID());
			// Employee director =
			// employeeService.selectByPrimaryKey(employee.getReportTo());
			// List<Employee> approverList = new ArrayList<>();
			// approverList.add(director);
			// variables.put("approverList", approverList);
			// // TODO 审批人List，待完善，现只取员工的直接领导
			// // planService.approveObjective(pmWorkFlow);
			// } else if
			// (PlanProcessKey.EVALUATE_OBJECTIVE_KEY.equals(processKey)) {
			// pmWorkFlow.setProcessKey(PlanProcessKey.EVALUATE_OBJECTIVE_KEY);
			// } else {
			// model.addAttribute("status", false);
			// model.addAttribute("message", processKey + "流程不存在！");
			// }
			//
			// PmWorkFlow basePerformance = (PmWorkFlow)
			// runtimeService.getVariable(pmWorkFlow.getProcInstId(),
			// "entity");
			//
			// variables.put("isPass", isPass);
			// if (!isPass) {
			// basePerformance.setTitle(basePerformance.getUserName() + "
			// 的请假申请失败,需修改后重新提交！");
			// pmWorkFlow.setStatus(BaseVO.APPROVAL_FAILED);
			// variables.put("entity", basePerformance);
			// }
			//
			// // 完成任务
			// processService.complete(taskId, content,
			// user.getUserId().toString(), variables);
			//
			// if (isPass) {
			// // 此处需要修改，不能根据人来判断审批是否结束。应该根据流程实例id(processInstanceId)来判定。
			// // 判断指定ID的实例是否存在，如果结果为空，则代表流程结束，实例已被删除(移到历史库中)
			// ProcessInstance pi =
			// this.runtimeService.createProcessInstanceQuery()
			// .processInstanceId(pmWorkFlow.getProcInstId()).singleResult();
			// if (BeanUtils.isBlank(pi)) {
			// pmWorkFlow.setStatus(BaseVO.APPROVAL_SUCCESS);
			// pmWorkFlow.setEndTime(new Date());
			// }
			// }
			//
			// pmWorkFlowService.updateByPrimaryKeySelective(pmWorkFlow);
			// model.addAttribute("status", Boolean.TRUE);
			// model.addAttribute("message", "任务办理完成！");
		} catch (ActivitiObjectNotFoundException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务不存在，请联系管理员！");
			throw e;
		} catch (ActivitiException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务正在协办，您不能办理此任务！");
			throw e;
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "任务办理失败，请联系管理员！");
			throw e;
		}
		return;
	}

	@RequestMapping(value = "{id}/revokeProcess", method = RequestMethod.POST)
	public void batchComplete(@PathVariable("id") Integer planId, @RequestParam("planStepId") Integer planStepId,
			Model model) throws Exception {
		// 权限检查，时候为管理员或绩效专员
		// List<String> roles = new ArrayList<>();
		// roles.add(PerfRoleConstant.ROLE_ADMIN);
		// roles.add(PerfRoleConstant.ROLE_PERFADMIN);
		// roles.add(PerfRoleConstant.ROLE_PERFDEPADMIN);
		// if(UserContext.hasAnyRoles(roles)) {
		// Plan plan = planService.selectByPrimaryKey(planId);
		// PlanStep planStep = planStepService.selectByPrimaryKey(planStepId);
		// User currentUser = UserContext.getCurrentUser();
		// // 判断是否为本人创建的，或者管理员权限
		// if (!(currentUser.getUserName().equals(planStep.getCreateBy()) ||
		// currentUser.getUserName().equals(planStep.getUpdateBy()) ||
		// UserContext.hasRole(PerfRoleConstant.ROLE_ADMIN))) {
		// model.addAttribute("status", false);
		// model.addAttribute("message", "没有权限进行该操作！");
		// return;
		// }
		// Date date = planStep.getUpdateTime() != null ?
		// planStep.getUpdateTime() : planStep.getCreateTime();
		//// SystemConfig.systemVariables.getOrDefault("perf.planStep.revokeTime.",
		// defaultValue);
		// //if (date)
		// }
	}

	@Override
	public boolean checkPermission(PmWorkFlowVO v, Model model, String... permissions) {
		return super.checkPermission(v, model, permissions);
	}

	/**
	 * 装饰流程变量实体
	 * @param pmWorkFlow
	 * @return
	 */
	public Object decoratorEntity(PmWorkFlow pmWorkFlow) {
		PmWorkFlow entity = pmWorkFlowService.decoratorEntity(pmWorkFlow);
		return entity.getEntity();
	}
}
