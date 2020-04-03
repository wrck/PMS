package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.data.bean.OrderDataFromSap;
import com.dp.plat.data.bean.Project;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProjectType;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.job.SMSDataJob;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectService;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.service.PresalesService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.util.Util;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "project")
public class ProjectController extends BaseController {
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

	@RequestMapping
	public String home() {
		new SMSDataJob().execute();
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
			if (project != null) {
				model.addAttribute("targetName", "project");
				model.addAttribute("targetValue", project);

				List<Object> fieldList = this.findFieldList(project.getProjectType() + "_" + DATANAME_FORM,
						DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);
				
				List<?> navTavList = this.findNavTabList(project.getProjectType() + "_" + DATANAME_NAVTAB);
				model.addAttribute("tabList", navTavList);
			}
		}
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping("/detail")
	public String create(String projectType, String contractNo, Model model) {
		if (HttpContext.isJSON()) {
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
			projectHeaderService.updateByPrimaryKeySelective(project);
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
		ProjectHeader project = projectHeaderService.selectByPrimaryKey(id);
		if (project == null) {
			return;
		}
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
			List<OrderDataFromSap> orderDataList = projectHeaderService.queryOrderDataListByProjectId(project.getProjectId());// 查询产品列表
			List<OrderDataFromSap> rmaOrderDataList = projectHeaderService.queryRmaOrderDataByContractNo(project.getContractNo());
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
		}
		model.addAttribute("columns", columns);
		model.addAttribute("data", data);
	}
}