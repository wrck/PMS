package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.context.SpringContext;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Project;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.DataFieldRelation;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IDataFieldRelationService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectService;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.util.Util;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "project")
public class ProjectController {
	private final static String VIEW_NAMESPACE = "project/";

	@Autowired
	private IProjectService projectService;
	
	@Autowired
	private IProjectHeaderService projectHeaderService;
	
	@Autowired
	@Qualifier("projectService")
	private ProjectService oldProjectService;
	
	@Autowired
	private IDataFieldRelationService dataFieldRelationService;

	@RequestMapping
	public String home() {
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
		pageParam.setTotal(projectHeaderService.countBySelectivePageable(tempParam));
		pageParam.setFiltered(projectHeaderService.countBySelectivePageable(pageParam));
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		List<Object> list = projectHeaderService.selectBySelectivePageable(pageParam);
		model.addAttribute("data", list);
		DataFieldRelation dataFieldRelation = new DataFieldRelation("projectList", "table");
		List<DataFieldRelation> fieldList = dataFieldRelationService.selectBySelective(dataFieldRelation);
		List<DataTableColumn> columns = new ArrayList<>();
		columns.addAll(fieldList);
		pageParam.setColumns(columns);
		return VIEW_NAMESPACE + "list";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			ProjectHeader project = projectHeaderService.selectByPrimaryKey(id);
//			Project project = oldProjectService.queryProjectById(id);
			if (project != null) {
//				project.setProjectType("10");
				model.addAttribute("targetName", "project");
				model.addAttribute("targetValue", project);
				
				Company company = new Company();
		        company.setStatus(1);
		        DepartmentManageService departmentManageService = SpringContext.getApplicationContext().getBean("departmentManageService", DepartmentManageService.class);
				List<Company> companyList = departmentManageService .queryCompanyList(company);
				model.addAttribute("companyList", companyList);
				model.addAttribute("departmentList", departmentManageService.queryDepartments());
				
				PageParam<Object> tPage = new PageParam<>();
				DataFieldRelation dataFieldRelation = new DataFieldRelation(project.getProjectType() + "_projectForm", "form");
				tPage.setPageSize(-1);
				tPage.setOrderBy("sort, id asc");
				tPage.setModel(dataFieldRelation);
				List<Object> fieldList = dataFieldRelationService.selectBySelectivePageable(tPage);
				model.addAttribute("fieldList", fieldList );
			}
		}
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping("/detail")
	public String create(String projectType, Map<String, Object> params, Model model) {
		if (HttpContext.isJSON()) {
			HttpServletRequest request = HttpContext.getCurrentRequest();
			String contractNo = request.getParameter("contractNo");
			String projectCode = request.getParameter("projectCode");
			Project project = oldProjectService.queryProjectByContractNo(contractNo);
			if (project == null) {
				model.addAttribute("status", false);
				model.addAttribute("message", "该项目合同已存在");
				return VIEW_NAMESPACE + "detail";
			}
			project.setProjectType(projectType);
			project.setProjectCode(oldProjectService.queryProjectCode(project));
			model.addAttribute("targetName", "project");
			model.addAttribute("targetValue", project);
			
			Company company = new Company();
	        company.setStatus(1);
	        DepartmentManageService departmentManageService = SpringContext.getApplicationContext().getBean("departmentManageService", DepartmentManageService.class);
			List<Company> companyList = departmentManageService .queryCompanyList(company);
			model.addAttribute("companyList", companyList);
			
			model.addAttribute("departmentList", departmentManageService.queryDepartments());
			
			PageParam<Object> tPage = new PageParam<>();
			DataFieldRelation dataFieldRelation = new DataFieldRelation(projectType + "_projectForm", "form");
			tPage.setPageSize(-1);
			tPage.setOrderBy("sort, id asc");
			tPage.setModel(dataFieldRelation);
			List<Object> fieldList = dataFieldRelationService.selectBySelectivePageable(tPage);
			model.addAttribute("fieldList", fieldList );
		}
		model.addAttribute("projectType", projectType);
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(ProjectVO project, Model model) {
		Boolean status = false;
		String message = null;
		//如果当前合同号已经创建项目，则直接返回不再创建
		Integer count = oldProjectService.queryProjectContractCountByContractNo(Util.appendChar((String) project.getCustomInfoByKey("contractNo"), "'"));
		if(count != null && count != 0){
			status = false;
			message = "该项目合同已存在！";
		} else {
			try {
				oldProjectService.insertProject(project);
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
		} catch(Exception e) {
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

	@RequestMapping(value = "checkUnique", method = RequestMethod.POST)
	public void checkUnique(@RequestParam("userName") String userName, Model model) {
		boolean isUnique = false;
		model.addAttribute("valid", isUnique);
	}

	@RequestMapping("/param")
	public void findUserInfoWithParam(HttpServletRequest request, Model model) {
	}
}