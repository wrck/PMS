/**
 * 
 */
package com.dp.plat.activiti.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.IdentityService;
import org.apache.commons.beanutils.BeanMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.service.IProcessService;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.param.Consts.URLPath;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;

/**
 * @author w02611
 *
 */
@Controller
@RequestMapping(URLPath.WORKFLOW_MANAGER + "task")
public class TaskController {

	@Autowired
	protected IUserService userService;

	@Autowired
	private IProcessService processService;
	
	@Autowired
	private IdentityService identityService;

	@RequestMapping
	public String list() {
		return URLPath.WORKFLOW_MANAGER + "task_list";
	}

	/**
	 * 查询待办任务
	 * 
	 * @param session
	 * @param redirectAttributes
	 * @param model
	 * @return
	 * @throws Exception
	 */
	// @RequiresPermissions("user:task:todoTask")
	@RequestMapping(value = "/todoTask")
	public String todoTask(PageParam<BaseVO> pageParam, Model model) throws Exception {
		Integer userId = UserContext.getCurrentPrincipal().getUserId();
		User user = new User(userId);
		List<BaseVO> taskList = this.processService.findTodoTask(user, pageParam);
		List<Object> jsonList = new ArrayList<Object>();
		for (BaseVO base : taskList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("businessKey", base.getBusinessKey());
			map.put("businessType", base.getBusinessType());
			map.put("userName", base.getApplyUserName() != null ? base.getApplyUserName() : base.getUserName());
			map.put("taskId", base.getTask().getId());
			map.put("taskName", base.getTask().getDescription() != null ? base.getTask().getDescription() : base.getTask().getName());
			map.put("createTime", base.getTask().getCreateTime());
			String assign = base.getTask().getAssignee();
			if (assign != null) {
				org.activiti.engine.identity.User u = identityService.createUserQuery().userId(assign).singleResult();
				if (u != null) {
					assign = u.getLastName() + "-" + u.getFirstName();
				}
//				User u = this.userService.selectByPrimaryKey(new Integer(assign));
//				assign = u.getUserName();
			}
			String owner = base.getTask().getOwner();
			if (owner != null) {
				org.activiti.engine.identity.User u = identityService.createUserQuery().userId(owner).singleResult();
				if (u != null) {
					owner = u.getLastName() + "-" + u.getFirstName();
				}
//				User u = this.userService.selectByPrimaryKey(new Integer(owner));
//				owner = u.getUserName();
			}

			User taskEntity = (User) base.getTaskEntity();
			if (taskEntity != null) {
				User u = this.userService.selectByPrimaryKey(new Integer(taskEntity.getUserId()));
				map.put("taskName", base.getTask().getName() + " -- " + u.getUserName());
			}
			map.put("assign", assign);
			map.put("owner", owner);
			map.put("taskDefinitionKey", base.getTask().getTaskDefinitionKey());
			map.put("processInstanceId", base.getProcessInstance().getId());
			map.put("processInstanceName", base.getProcessInstance().getName() != null ? base.getProcessInstance().getName() : base.getProcessInstance().getProcessDefinitionName());
			map.put("processDefinitionId", base.getProcessInstance().getProcessDefinitionId());
			map.put("processDefinitionKey", base.getProcessDefinition().getKey()); // 任务跳转用
			map.put("suspended", base.getProcessInstance().isSuspended());
			map.put("version", base.getProcessDefinition().getVersion());
			map.put("formUrl", base.getTask().getFormKey());
			jsonList.add(map);
		}
		model.addAttribute("data", jsonList);
		return Consts.URLPath.WORKFLOW_MANAGER + "todoTask";
	}

	/**
	 * 查看已完成任务列表
	 *
	 * @return
	 * @throws Exception
	 */
	// @RequiresPermissions("user:process:finished")
	@RequestMapping(value = "/endTask")
	public String findFinishedTaskInstances(PageParam<BaseVO> pageParam, Model model) throws Exception {
		User user = UserContext.getCurrentUser();
		Boolean isAdmin = SecurityUtils.getSecurityManager().hasRole(SecurityUtils.getSubject().getPrincipals(),
				"admin");
		List<BaseVO> taskList = new ArrayList<BaseVO>();
		if (isAdmin) {
			taskList = this.processService.findFinishedTaskInstances(null, pageParam);
		} else {
			taskList = this.processService.findFinishedTaskInstances(user, pageParam);
		}
		List<Object> jsonList = new ArrayList<Object>();
		for (BaseVO base : taskList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("businessType", base.getBusinessType());
			map.put("userName", base.getUserName());
			map.put("title", base.getTitle());
			map.put("taskId", base.getHistoricTaskInstance().getId());
			map.put("taskName", base.getHistoricTaskInstance().getName());
			map.put("taskDefinitionKey", base.getHistoricTaskInstance().getTaskDefinitionKey());
			map.put("processInstanceId", base.getHistoricTaskInstance().getProcessInstanceId());
			map.put("processInstanceName", base.getHistoricProcessInstance().getName());
			map.put("processDefinitionId", base.getHistoricTaskInstance().getProcessDefinitionId());
			map.put("processDefinitionName", base.getProcessDefinition().getName());
			map.put("startTime", base.getHistoricTaskInstance().getStartTime());
			map.put("claimTime", base.getHistoricTaskInstance().getClaimTime());
			map.put("endTime", base.getHistoricTaskInstance().getEndTime());
			map.put("deleteReason", base.getHistoricTaskInstance().getDeleteReason());
			map.put("version", base.getProcessDefinition().getVersion());
			jsonList.add(map);
		}
		model.addAttribute("data", jsonList);
		return Consts.URLPath.WORKFLOW_MANAGER + "endTask";
	}

	/**
	 * 签收任务
	 * 
	 * @return
	 */
	// @RequiresPermissions("user:task:claim")
	@RequestMapping("/claim/{taskId}")
	public void claim(@PathVariable("taskId") String taskId, Model model) {
		try {
			User user = UserContext.getCurrentUser();
			this.processService.claim(user, taskId);
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "任务签收成功！");
		} catch (ActivitiObjectNotFoundException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务不存在！任务签收失败！");
			ExceptionHandler.insertException(e);
		} catch (ActivitiTaskAlreadyClaimedException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务已被其他组成员签收！请刷新页面重新查看！");
			ExceptionHandler.insertException(e);
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "任务签收失败！请联系管理员！");
			ExceptionHandler.insertException(e);
		}
		return;
	}
	
	/**
	 * 取消签收任务
	 * 
	 * @return
	 */
	// @RequiresPermissions("user:task:claim")
	@RequestMapping("/unclaim/{taskId}")
	public void unclaim(@PathVariable("taskId") String taskId, Model model) {
		try {
			this.processService.unclaim(taskId);
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "任务取消签收成功！");
		} catch (ActivitiObjectNotFoundException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务不存在！任务取消签收失败！");
			ExceptionHandler.insertException(e);
		} catch (ActivitiTaskAlreadyClaimedException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务已被其他组成员签收！请刷新页面重新查看！");
			ExceptionHandler.insertException(e);
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "任务取消签收失败！请联系管理员！");
			ExceptionHandler.insertException(e);
		}
		return;
	}

	/**
	 * 委派任务 委派也是代办、协办，你领导接到一个任务，让你代办，你办理完成后任务还是回归到你的领导，事情是你做的，功劳是你领导的，这就是代办。
	 * 所以代办人完成任务后，任务还会回到原执行人，流程不会发生变化。
	 * 
	 * @param taskId
	 *            代办人
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/delegate/{taskId}")
	public void delegateTask(@PathVariable("taskId") String taskId, @RequestParam("userId") String userId,
			Model model) {
		try {
			this.processService.delegateTask(userId, taskId);
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "委派任务成功！");
		} catch (ActivitiObjectNotFoundException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务不存在！委派任务失败！");
			ExceptionHandler.insertException(e);
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "委派任务失败，系统错误！");
			ExceptionHandler.insertException(e);
		}
		return;
	}

	/**
	 * 转办任务，办理完成后，流程会继续向下走。
	 * 
	 * @param taskId
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/transfer/{taskId}")
	public void transferTask(@PathVariable("taskId") String taskId, @RequestParam("userId") String userId,
			Model model) {
		try {
			this.processService.transferTask(userId, taskId);
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "转办任务成功！");
		} catch (ActivitiIllegalArgumentException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", e.getMessage());
			ExceptionHandler.insertException(e);
		} catch (ActivitiObjectNotFoundException e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "此任务不存在！转办任务失败！");
			ExceptionHandler.insertException(e);
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "转办任务失败，系统错误！");
			ExceptionHandler.insertException(e);
		}
		return;
	}

	/**
	 * 撤销任务
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/revoke/{processInstanceId}/{taskId}")
	public void revoke(@PathVariable("taskId") String taskId,
			@PathVariable("processInstanceId") String processInstanceId, Model model) throws Exception {
		try {
			Integer revokeFlag = this.processService.revoke(taskId, processInstanceId);
			// Integer revokeFlag = this.revokeTaskService.revoke(taskId,
			// processInstanceId);
			// Command<Integer> cmd = new RevokeTask(taskId, processInstanceId);
			// Integer revokeFlag =
			// this.processEngine.getManagementService().executeCommand(cmd);

			if (revokeFlag == 0) {
				model.addAttribute("status", Boolean.TRUE);
				model.addAttribute("message", "撤销任务成功！");
			} else if (revokeFlag == 1) {
				model.addAttribute("status", Boolean.FALSE);
				model.addAttribute("message", "撤销任务失败 - [ 此审批流程已结束! ]");
			} else if (revokeFlag == 2) {
				model.addAttribute("status", Boolean.FALSE);
				model.addAttribute("message", "撤销任务失败 - [ 下一结点已经通过,不能撤销! ]");
			}
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "撤销任务失败 - [ 内部错误！]");
			ExceptionHandler.insertException(e);
		}
		return;
	}

	/**
	 * 任务跳转（包括回退和向前）至指定活动节点
	 * 
	 * @param currentTaskId
	 * @param targetTaskDefinitionKey
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/jump")
	public void jumpTargetTask(@RequestParam("taskId") String currentTaskId,
			@RequestParam("taskDefinitionKey") String targetTaskDefinitionKey, Model model) throws Exception {
		try {
			this.processService.moveTo(currentTaskId, targetTaskDefinitionKey);
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "任务跳转成功！");
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "任务跳转失败！");
			ExceptionHandler.insertException(e);
		}
		return;
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
		Result result = (Result) processService.withdrawTask(instanceId, userId);
		Map properties = new BeanMap(result);
		model.mergeAttributes(properties);
		return;
	}

}
