package com.dp.plat.activiti.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dp.plat.core.param.Consts;

/**
 * 系统模态框页面控制器
 * 
 * @author w02611
 *
 */
@RequestMapping(Consts.URLPath.WORKFLOW_MANAGER + "modals")
@Controller
public class WorkFlowSubModalController {

	@RequestMapping("/definition/{processDefinitionId}")
	public String showDefinition(@PathVariable("processDefinitionId") String processDefinitionId, Model model) {
		model.addAttribute("processDefinitionId", processDefinitionId);
		return Consts.URLPath.WORKFLOW_MANAGER + "modals/showDefinition";
	}

	@RequestMapping("/instance/{processInstanceId}")
	public String showInstance(@PathVariable("processInstanceId") String processInstanceId, Model model) {
		model.addAttribute("processInstanceId", processInstanceId);
		return Consts.URLPath.WORKFLOW_MANAGER + "modals/showInstance";
	}

	@RequestMapping("/task/{taskType}/{processInstanceId}/{taskId}")
	public String completeTask(@PathVariable("taskId") String taskId,
			@PathVariable("processInstanceId") String processInstanceId, @PathVariable("taskType") String taskType,
			String businessKey, String taskDefKey, Model model) {
		model.addAttribute("taskId", taskId);
		model.addAttribute("processInstanceId", processInstanceId);
		model.addAttribute("taskType", taskType);
		model.addAttribute("businessKey", businessKey);
		model.addAttribute("taskDefKey", taskDefKey);
		return Consts.URLPath.WORKFLOW_MANAGER + "modals/completeTask";
	}
}
