package com.dp.plat.activiti.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.entity.ProcessInstanceEntity;
import com.dp.plat.activiti.service.IProcessService;
import com.dp.plat.activiti.service.IRuntimePageService;
import com.dp.plat.activiti.utils.ProcessDefinitionCache;
import com.dp.plat.activiti.vo.ActivityVo;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.vo.PageParam;

/**
 * 流程控制类
 * 
 * @author w02611
 *
 */
@Controller
@RequestMapping(Consts.URLPath.WORKFLOW_MANAGER + "instance")
public class ProcessInstanceController {

	@Autowired
	protected IUserService userService;

	// @Autowired
	// protected WorkflowService traceService;

	@Autowired
	private IProcessService processService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private IRuntimePageService runtimePageService;
	
	@RequestMapping
	public String list() {
		return Consts.URLPath.WORKFLOW_MANAGER + "running_process_list";
	}

	/**
	 * 显示流程图,带流程跟踪
	 * 
	 * @param processInstanceId
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/diagram/{processInstanceId}", method = RequestMethod.GET)
	public void showDiagram(@PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response)
			throws Exception {
		InputStream imageStream = this.processService.getDiagram(processInstanceId);
		// 输出资源内容到相应对象
		byte[] b = new byte[1024];
		int len;
		while ((len = imageStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}
	}

	/**
	 * 显示图片通过流程id，不带流程跟踪(没有乱码问题)
	 *
	 * @param resourceType
	 *            资源类型(xml|image)
	 * @param processInstanceId
	 *            流程实例ID
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resourceType}/{processInstanceId}")
	public void loadByProcessInstance(@PathVariable("resourceType") String resourceType,
			@PathVariable("processInstanceId") String processInstanceId, HttpServletResponse response)
			throws Exception {
		InputStream resourceAsStream = this.processService.getDiagramByProInstanceId_noTrace(resourceType,
				processInstanceId);
		byte[] b = new byte[1024];
		int len = -1;
		while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}
	}

	/**
	 * 显示流程明细
	 * 
	 * @param processInstanceId
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/info/{processInstanceId}/list", method = RequestMethod.POST)
	public void showInfo(@PathVariable("processInstanceId") String processInstanceId, Model model) {
		List<ActivityVo> list = runtimePageService.getActivityList(processInstanceId);
		model.addAttribute("data", list);
	}

	/**
	 * 跳转流程管理页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toListProcessManager")
	public String toListProcessRunning() throws Exception {
		return "workflow/list_process_manager";
	}

	/**
	 * 管理运行中的流程
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	// @RequiresPermissions("admin:process:*")
	@RequestMapping(value = "/runningProcess")
	public String listRuningProcess(PageParam<ProcessInstanceEntity> pageParam, Model model) throws Exception {
		List<ProcessInstance> list = this.processService.listRuningProcess(pageParam);
		List<ProcessInstanceEntity> pieList = new ArrayList<ProcessInstanceEntity>();
		for (ProcessInstance processInstance : list) {
			ProcessInstanceEntity pie = new ProcessInstanceEntity();
			pie.setId(processInstance.getId());
			pie.setProcessInstanceId(processInstance.getProcessInstanceId());
			pie.setProcessInstanceName(processInstance.getName());
			pie.setProcessDefinitionId(processInstance.getProcessDefinitionId());
			pie.setProcessDefinitionName(processInstance.getProcessDefinitionName());
			pie.setActivityId(processInstance.getActivityId());
			pie.setDeploymentId(processInstance.getDeploymentId());
			pie.setSuspended(processInstance.isSuspended());

			ProcessDefinitionCache.setRepositoryService(this.repositoryService);
			String taskName = ProcessDefinitionCache.getActivityName(processInstance.getProcessDefinitionId(),
					processInstance.getActivityId());
			HistoricProcessInstance hi = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstance.getProcessInstanceId()).singleResult();
			org.activiti.engine.identity.User user = identityService.createUserQuery().userId(hi.getStartUserId())
					.singleResult();
			// User user =
			// userService.selectByPrimaryKey(Integer.valueOf(hi.getStartUserId()));
			if (user != null) {
				pie.setStartUserName(user.getFirstName());
			}
			pie.setTaskName(taskName);
			pieList.add(pie);
		}
		model.addAttribute("data", pieList);
		return Consts.URLPath.WORKFLOW_MANAGER + "running_process_list";
	}

	/**
	 * 管理已结束的流程
	 *
	 * @return
	 * @throws Exception
	 */
	// @RequiresPermissions("admin:process:*")
	@RequestMapping(value = "/finishedProcess")
	public String findFinishedProcessInstances(PageParam<BaseVO> pageParam, Model model) throws Exception {
		// List<Object> jsonList = new ArrayList<Object>();
		// List<BaseVO> processList =
		// this.processService.findFinishedProcessInstances(pageParam);
		// for (BaseVO base : processList) {
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("businessType", base.getBusinessType());
		// map.put("userName", base.getUserName());
		// map.put("title", base.getTitle());
		// map.put("startTime",
		// base.getHistoricProcessInstance().getStartTime());
		// map.put("endTime", base.getHistoricProcessInstance().getEndTime());
		// map.put("deleteReason",
		// base.getHistoricProcessInstance().getDeleteReason());
		// map.put("version", base.getProcessDefinition().getVersion());
		// jsonList.add(map);
		// }
		// List<Comment> jsonList = ((TaskService)
		// SpringContext.getBean("taskService")).getProcessInstanceComments("202");
		// List<HistoricActivityInstance> jsonList =
		// historyService.createHistoricActivityInstanceQuery().processInstanceId("5034").activityType("userTask").list();
		// List<HistoricTaskInstance> jsonList =
		// historyService.createHistoricTaskInstanceQuery().processInstanceId("5034").list();
		List<Task> jsonList = ((TaskService) SpringContext.getBean("taskService")).createTaskQuery()
				.processInstanceId("5034").list();
		// List<Execution> jsonList =
		// ((RuntimeService)SpringContext.getBean("runtimeService")).createExecutionQuery().processInstanceId("202").list();
		// List<String> activityIds =
		// ((RuntimeService)SpringContext.getBean("runtimeService")).createExecutionQuery()..getActiveActivityIds(jsonList.get(0).getId());
		// NextTaskGetor nextTaskGetor = new NextTaskGetor();
		// List<TaskDefinition> jsonList =
		// nextTaskGetor.getNextTaskInfoList("5034");

		// List<TaskDefinition> jsonList = new ArrayList<>();
		// jsonList.add(taskDefinition);

		// List<UserTask> userTasks = new ArrayList<UserTask>(jsonList.size());
		// for (TaskDefinition taskDefinition : jsonList) {
		// UserTask userTask = new UserTask();
		// userTask.setTaskName(taskDefinition.getNameExpression().getExpressionText());
		// userTask.setTaskDefKey(taskDefinition.getKey());
		// userTasks.add(userTask);
		// }
		model.addAttribute("data", jsonList);
		return Consts.URLPath.WORKFLOW_MANAGER + "process/finishedProcess";
	}

	/**
	 * 激活、挂起流程实例-根据processInstanceId
	 * 
	 * @param status
	 * @param processInstanceId
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{status}/{processInstanceId}", method = RequestMethod.POST)
	public void updateProcessStatusByProInstanceId(@PathVariable("status") String status,
			@PathVariable("processInstanceId") String processInstanceId, Model model) throws Exception {
		if (status.equals("active")) {
			this.processService.activateProcessInstance(processInstanceId);
			// redirectAttributes.addFlashAttribute("message", "已激活ID为[ " +
			// processInstanceId + " ]的流程实例。");
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "已激活ID为[" + processInstanceId + "]的流程实例。");
		} else if (status.equals("suspend")) {
			this.processService.suspendProcessInstance(processInstanceId);
			// redirectAttributes.addFlashAttribute("message", "已挂起ID为[ " +
			// processInstanceId + " ]的流程实例。");
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "已挂起ID为[" + processInstanceId + "]的流程实例。");
		}
		return;
	}
	
	@RequestMapping(value = "/delete/{processInstanceId}", method = RequestMethod.POST)
	public void deleteProcessByProInstanceId(@PathVariable("processInstanceId") String processInstanceId, String deleteReason, Model model) throws Exception {
		try {
			this.processService.deleteProcess(processInstanceId, deleteReason);
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "已删除ID为[" + processInstanceId + "]的流程实例。");
		} catch(Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", e.getClass().getName() + ":" + e.getMessage());
		}
		return;
	}

	@RequestMapping(value = "/toListApply")
	public String toListApply() {
		return "apply/list_apply";
	}

}
