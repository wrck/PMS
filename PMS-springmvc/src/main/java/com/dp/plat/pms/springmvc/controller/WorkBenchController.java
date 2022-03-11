package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.TaskType;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.URLPath;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import com.dp.plat.pms.springmvc.service.IPmWorkBenchService;
import com.dp.plat.pms.springmvc.service.IPmWorkFlowService;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;

@RequestMapping(URLPath.WORKFLOW_MANAGER + "workbench")
@Controller
public class WorkBenchController {
	
	@Autowired
	private IPmWorkBenchService pmWorkBenchService;
	
	@Autowired
	private IPmWorkFlowService pmWorkFlowService;

	@RequestMapping
	public String listView() {
		return URLPath.WORKFLOW_MANAGER + "workbench";
	}
	
	/**
	 * 用户自己的任务都在此tab
	 * @return
	 */
	@RequestMapping("toDoList")
	public String listToDoTask(PageParam<PmWorkFlow> pageParam, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		List<String> processKeyList = new ArrayList<>();
		processKeyList.add(ProcessType.QUALITY_APPROVE_TRACK);
		List<String> taskKeyList = new ArrayList<>();
		taskKeyList.add(TaskType.AF_APPROVE_TASK);
		taskKeyList.add(TaskType.YF_APPROVE_TASK);
		taskKeyList.add(TaskType.TRACK_TASK);
		
//		List<PmWorkFlow> pmWorkFlowList = pmWorkBenchService.selectRunTasksByAssigneeAndProcessKeyAndTaskKey(
//				pageParam, user.getUserCustom4(), processKeyList, taskKeyList);
		PmWorkFlowVO workFlow = new PmWorkFlowVO();
		workFlow.setTaskKey(StringUtils.join(taskKeyList, ","));
		workFlow.setProcessKey(StringUtils.join(processKeyList, ","));
		workFlow.setAssignee(String.valueOf(user.getUserCustom4()));
		workFlow.setBeginTime(new Date());
		workFlow.setStatus(PmWorkFlowVO.PENDING);
		String areaPower = StringUtils.trimToEmpty(user.getUserInfo().getCustom5());
		List<String> areaList = new ArrayList(Arrays.asList(StringUtils.split(areaPower, ",")));
		areaList.add("all");
		workFlow.setAreaPower(StringUtils.join(areaList, ","));
		List<PmWorkFlow> pmWorkFlowList = pmWorkBenchService.selectRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam, workFlow);
		model.addAttribute("data", pmWorkFlowList);
		return URLPath.WORKFLOW_MANAGER + "workbench";
	}
	
	/**
	 * 待办任务（不含自己）
	 * @return
	 */
	@RequestMapping("listOthersTask")
	public String listOthersTask(PageParam<PmWorkFlow> pageParam, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		List<String> processKeyList = new ArrayList<>();
		processKeyList.add(ProcessType.QUALITY_APPROVE_TRACK);
		List<String> taskKeyList = new ArrayList<>();
		taskKeyList.add(TaskType.AF_APPROVE_TASK);
		taskKeyList.add(TaskType.YF_APPROVE_TASK);
		taskKeyList.add(TaskType.TRACK_TASK);
//		List<PmWorkFlow> pmWorkFlowList = pmWorkBenchService.selectRunTasksByAssigneeAndProcessKeyAndTaskKey(
//				pageParam, user.getUserCustom4(), processKeyList, taskKeyList);
//		List<PmWorkFlow> pmWorkFlowList = pmWorkBenchService.selectRunTasksByAssigneeAndProcessKeyAndTaskKey(
//		pageParam, user.getUserCustom4(), processKeyList, taskKeyList);
		PmWorkFlowVO workFlow = new PmWorkFlowVO();
		workFlow.setTaskKey(StringUtils.join(taskKeyList, ","));
		workFlow.setProcessKey(StringUtils.join(processKeyList, ","));
		workFlow.setAssignee(String.valueOf(user.getUserCustom4()));
		workFlow.setBeginTime(new Date());
		workFlow.setStatus(PmWorkFlowVO.PENDING);
		String areaPower = StringUtils.trimToEmpty(user.getUserInfo().getCustom5());
		List<String> areaList = new ArrayList(Arrays.asList(StringUtils.split(areaPower, ",")));
		areaList.add("all");
		workFlow.setAreaPower(StringUtils.join(areaList, ","));
		List<PmWorkFlow> pmWorkFlowList = pmWorkBenchService.selectRunTasksByAssigneeAndProcessKeyAndTaskKey(pageParam, workFlow);

		model.addAttribute("data", pmWorkFlowList);
		return URLPath.WORKFLOW_MANAGER + "workbench";
	}

	/**
	 * 用户目标已办任务列表
	 * 
	 * @return
	 */
	@RequestMapping("finishedTaskList")
	public String finishedTask(PageParam<PmWorkFlow> pageParam, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
//		List<PmWorkFlow> pmWorkFlowList = pmWorkBenchService.selectFinishedTasksByAssignee(pageParam,
//				user.getUserCustom4());
		
		PmWorkFlowVO workFlow = new PmWorkFlowVO();
		workFlow.setAssignee(String.valueOf(user.getUserCustom4()));
		String areaPower = StringUtils.trimToEmpty(user.getUserInfo().getCustom5());
		List<String> areaList = new ArrayList(Arrays.asList(StringUtils.split(areaPower, ",")));
		areaList.add("all");
		workFlow.setAreaPower(StringUtils.join(areaList, ","));
		List<PmWorkFlow> pmWorkFlowList = pmWorkBenchService.selectFinishedTasksByAssignee(pageParam, workFlow);
		model.addAttribute("data", pmWorkFlowList);
		return URLPath.WORKFLOW_MANAGER + "workbench";
	}
}
