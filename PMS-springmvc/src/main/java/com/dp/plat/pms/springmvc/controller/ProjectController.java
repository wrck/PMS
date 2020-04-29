package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.data.bean.OrderDataFromSap;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectPlan;
import com.dp.plat.data.bean.ProjectPlanEvent;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProjectType;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectService;
import com.dp.plat.pms.springmvc.service.IProjectTaskService;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.pms.springmvc.vo.TaskVO;
import com.dp.plat.service.PresalesService;
import com.dp.plat.service.ProjectPlanService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.util.Util;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "project")
public class ProjectController extends AbstractController<IProjectService, com.dp.plat.pms.springmvc.entity.Project, ProjectVO> {
	private final static String VIEW_NAMESPACE = "project/";
	private final static String DATANAME_FORM = "projectForm";
	private final static String DATANAME_TABLE = "projectList";
	private final static String DATANAME_NAVTAB = "projectTab";

	@Autowired
	private IProjectService projectService;

	@Autowired
	private IProjectHeaderService projectHeaderService;

	@Autowired
	@Qualifier("projectService")
	private ProjectService oldProjectService;

	@Autowired
	private ProjectPlanService projectPlanService;

	@Autowired
	private IProjectTaskService projectTaskService;

	@RequestMapping
	public String home(Model model) {
		return VIEW_NAMESPACE + "list";
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, ProjectVO project, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		// project.setCompId(user.getCompId());
		PageParam<Object> tempParam = new PageParam<>();
		ProjectVO temp = new ProjectVO();
		// temp.setCompID(user.getCompId());
		tempParam.setModel(temp);
		pageParam.setModel(project);
		List<Object> list = null;
		// 待创建列表
		if (ProjectConstant.ProjectState.UNCREATED.equals(project.getProjectState())) {
			pageParam.setTotal(projectHeaderService.countUncreateProjectList(tempParam));
			pageParam.setFiltered(projectHeaderService.countUncreateProjectList(pageParam));
			list = projectHeaderService.selectUncreateProjectList(pageParam);
		} else {
			pageParam.setTotal(projectHeaderService.countBySelectivePageable(tempParam));
			pageParam.setFiltered(projectHeaderService.countBySelectivePageable(pageParam));
			list = projectHeaderService.selectBySelectivePageable(pageParam);
		}

		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		model.addAttribute("data", list);

		List<DataTableColumn> columns = this.findColumnList(DATANAME_TABLE);
		pageParam.setColumns(columns);
		return VIEW_NAMESPACE + "list";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			ProjectHeader project = projectHeaderService.selectByPrimaryKey(id);
			ProjectVO vo = new ProjectVO();
			BeanUtils.copyProperties(project, vo);
			if (!checkPermission(vo, model, "project:detail")) {
				model.addAttribute("status", false);
				model.addAttribute("message", "没有权限进行该操作！");
				return Consts.VIEW_UNAUTHORIZED;
			}
			if (project != null) {
				model.addAttribute("targetName", "projectVO");
				model.addAttribute("targetValue", project);

				List<Object> fieldList = this.findFieldList(project.getProjectType() + "_" + DATANAME_FORM,
						DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				List<?> navTavList = this.findNavTabList(project.getProjectType() + "_" + DATANAME_NAVTAB);
				model.addAttribute("tabList", navTavList);
			}
		} else {
			ProjectVO vo = new ProjectVO();
			vo.setProjectId(id);
			if (!checkPermission(vo, model, "project:detail")) {
				model.addAttribute("status", false);
				model.addAttribute("message", "没有权限进行该操作！");
				return Consts.VIEW_UNAUTHORIZED;
			}
		}
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping("/detail")
	public String detail(ProjectVO vo, Model model) {
		String projectType = vo.getProjectType();
		if (HttpContext.isJSON()) {
			String contractNo = vo.getContractNo();
			Project project = projectHeaderService.queryProjectByContractNoAndType(contractNo, projectType);
			if (project == null) {
				model.addAttribute("status", false);
				model.addAttribute("message", "该项目合同已存在");
				return VIEW_NAMESPACE + "detail";
			}
			project.setProjectType(projectType);
			project.setProjectCode(projectHeaderService.queryProjectCode(project));
			model.addAttribute("targetName", "project");
			model.addAttribute("targetValue", project);

			List<Object> fieldList = this.findFieldList(projectType + "_" + DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);

			List<?> navTavList = this.findNavTabList("create_" + DATANAME_NAVTAB);
			model.addAttribute("tabList", navTavList);
		}
		model.addAttribute("projectType", projectType);
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(ProjectVO project, Model model) {
		Boolean status = false;
		String message = null;
		// 如果当前合同号已经创建项目，则直接返回不再创建
		Integer count = projectHeaderService.queryProjectContractCountByContractNoAndType(
				Util.appendChar((String) project.getContractNo(), "'"), project.getProjectType());
		if (count != null && count != 0) {
			status = false;
			message = "该项目合同已存在！";
		} else {
			try {
				projectHeaderService.insertProject(project);
				model.addAttribute("targetName", "projectVO");
				status = true;
			} catch (Exception e) {
				status = false;
				Integer errorId = ExceptionHandler.insertException(e);
				model.addAttribute("errorId", errorId);
				message = e.getMessage();
			}
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, ProjectVO project, Model model) {
		Boolean status = true;
		String message = null;
		try {
			projectHeaderService.updateProjectByProjectId(project);// 工程管理部权限
			projectHeaderService.updateByPrimaryKeySelective(project);
			// projectHeaderService.updateProjectProgramManagerByProjectId(project,null);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
	}

	@RequestMapping(value = "/{id}/orderDetail")
	public void orderDetailByProjectId(@PathVariable("id") Integer id, Model model) {
		ProjectHeader temp = projectHeaderService.selectByPrimaryKey(id);
		if (temp == null) {
			return;
		}
		ProjectVO project = new ProjectVO();
		BeanUtils.copyProperties(temp, project);

		List<DataTableColumn> columns = null;
		List<Object> data = null;
		String projectType = project.getProjectType();
		if (ProjectType.AF_XX_PROJECT.equals(projectType)) {
			PresalesService presalesService = SpringContext.getBean("presalesService", PresalesService.class);
			List<Map<String, Object>> orderDataList = presalesService.queryPresaleLend2RmaInfo(project.getContractNo());
			data = new ArrayList<Object>(orderDataList.size());
			data.addAll(orderDataList);
			columns = findColumnList("lendDetailList");
		} else {
			List<OrderDataFromSap> orderDataList = projectHeaderService
					.queryOrderDataListByProjectId(project.getProjectId());// 查询产品列表
			List<OrderDataFromSap> rmaOrderDataList = projectHeaderService
					.queryRmaOrderDataByContractNo(project.getContractNo());
			orderDataList.addAll(rmaOrderDataList);
			data = new ArrayList<Object>(orderDataList.size());
			data.addAll(orderDataList);
			columns = findColumnList("orderDetailList");
		}
		model.addAttribute("columns", columns);
		model.addAttribute("data", data);
	}

	@RequestMapping(value = "/orderDetail")
	public void orderDetailByContractNo(@RequestParam(required = true) String projectType,
			@RequestParam(required = true) String contractNo, Model model) {
		List<DataTableColumn> columns = null;
		List<Object> data = null;
		if (ProjectType.AF_XX_PROJECT.equals(projectType)) {
			PresalesService presalesService = SpringContext.getBean("presalesService", PresalesService.class);
			List<Map<String, Object>> orderDataList = presalesService.queryPresaleLend2RmaInfo(contractNo);
			data = new ArrayList<Object>(orderDataList.size());
			data.addAll(orderDataList);
			columns = findColumnList("lendDetailList");
		} else {
			Project project = new Project();
			project.setContractNo(contractNo);
			List<OrderDataFromSap> orderDataList = projectHeaderService.queryOrderLineFromSapByContractNo(project);
			data = new ArrayList<Object>(orderDataList.size());
			data.addAll(orderDataList);
			columns = findColumnList("orderDetailList");
		}
		model.addAttribute("columns", columns);
		model.addAttribute("data", data);
	}

	@GetMapping(value = "/{projectId}/task")
	public void projectTask(@PathVariable(name = "projectId") Integer projectId, ProjectVO project, Model model) {
		project.setProjectId(projectId);
		if (!checkPermission(project, model, "project:detail", "projectTask:list")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return;
		}
		String projectType = project.getProjectType();
		if (StringUtils.isBlank(projectType)) {
			ProjectHeader projectHeader = projectHeaderService.selectByPrimaryKey(projectId);
			projectType = projectHeader.getProjectType();
			project.setProjectType(projectType);
		}
//		// 合同财务回款计划
//		List<ProjectPlan> projectPlanList = projectPlanService.queryProjectPlanListByContractNo(Util.appendChar(project.getContractNo(), "'"));
//		// 根据projectid查询项目计划列表
//		List<ProjectTask> projectTaskList = oldProjectService.queryProjectTaskByProjectId(project.getProjectId());
//		//根据项目类型生成事件节点列表
//		project.setColumn012(project.getProjectType());
//		List<ProjectPlanEvent> projectPlanEventList = oldProjectService.queryProjectPlanEventByProject(project);
//		if(projectTaskList == null || projectTaskList.size() == 0){
//			addPlanList2EventList(projectPlanList, projectPlanEventList);
//		}
		TaskVO t = new TaskVO(projectId);
		t.setVisibleFlag("1");
		t.setEffective(new Date());
		PageParam<Object> pageParam = new PageParam<Object>();
		pageParam.setPageSize(-1);
		pageParam.setModel(t);
		List<Object> projectTaskList = projectTaskService.selectBySelectivePageable(pageParam);
//		if (projectTaskList.isEmpty()) {
//			project.setColumn011(project.getProjectType());
//			List<ProjectPlanEvent> projectPlanEventList = oldProjectService.queryProjectPlanEventByProject(project);
//			projectTaskList = new ArrayList<Object>(projectPlanEventList.size());
//			for (ProjectPlanEvent projectPlanEvent : projectPlanEventList) {
//				ProjectTask task = new ProjectTask(projectId, projectType);
//				task.setTaskTypeCode(projectPlanEvent.getDataTypeCode());
//				task.setTaskTypeId(projectPlanEvent.getBasicDataId());
//				task.setEventPlanHappenDate(projectPlanEvent.getEventPlanHappenDate());
//				task.setEventActualFinishDate(projectPlanEvent.getEventActualFinishDate());
//				task.setEventKey(projectPlanEvent.getEventKey());
//				task.setEventValue(projectPlanEvent.getEventValue());
//				projectTaskList.add(task);
//			}
//		}
		List<DataTableColumn> columns = findColumnList("projectTaskList");
		model.addAttribute("columns", columns);
		model.addAttribute("data", projectTaskList);
	}

	/**
	 * planList的部分字段放到planeventList中
	 * 
	 * @param planlist
	 * @param planeventlist
	 */
	private void addPlanList2EventList(List<ProjectPlan> planlist, List<ProjectPlanEvent> planeventlist) {
		for (ProjectPlan plan : planlist) {
			for (ProjectPlanEvent event : planeventlist) {
				if (plan.getReferenceEventName().equals(event.getEventValue())) {
					event.setEventPlanHappenDate(plan.getEventPlanHappenDate());
					event.setEventActualFinishDate(plan.getEventActualFinishDate());
				}
			}
		}
	}

	@Override
	public boolean checkPermission(ProjectVO project, Model model, String... permissions) {
		PermissionResult result = projectHeaderService.checkPermission(project, permissions);
		model.addAllAttributes(result.getMap());
		return result.isPermit();
	}
}