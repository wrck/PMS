package com.dp.plat.pms.springmvc.controller;

import static com.dp.plat.core.param.RoleConstant.ROLE_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_SUB_ADMIN;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.DailyReport;
import com.dp.plat.pms.springmvc.entity.DailyReport;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IDailyReportService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.vo.DailyReportVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "/daily/report")
public class DailyReportController extends AbstractController<IDailyReportService, DailyReport, DailyReportVO> {

	@Autowired
	private IProjectHeaderService projectHeaderService;
	
	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.PROJECT_MANAGER);
		this.setViewModel("dailyReport");
		this.setUseTemplate(true);
	}
	
	@Override
	public String home(Model model) {
		String view = super.home(model);
		return getRealViewNameSpace() + "list";
	}
	
	@Override
	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, DailyReportVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":list")) {
			model.addAttribute("data", Collections.emptyList());
			return Consts.VIEW_UNAUTHORIZED;
		}
		List<Object> list = Collections.emptyList();
		try {
			// Principal user = UserContext.getCurrentPrincipal();
			// v.setCompId(user.getCompId());
			v.setDisabled(false);
			PageParam<Object> tempParam = new PageParam<>();
			DailyReportVO temp = new DailyReportVO();
			Principal user = UserContext.getCurrentPrincipal();
			// 允许访问的项目类型
			if (!UserContext.hasAnyRoles(ROLE_PM_ADMIN, ROLE_ADMIN)) {
				String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
				temp.setProjectTypes(projectTypes);
				v.setProjectTypes(projectTypes);
				
				// 非子项目管理员，添加允许访问的办事处权限
				String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
				if (!UserContext.hasRole(ROLE_PM_SUB_ADMIN)) {
					temp.setOfficeCodes(officeCodes);
					v.setOfficeCodes(officeCodes);
				}
			}
//			String officeCode = StringUtils.trimToEmpty(v.getOfficeCode());
//            ProjectHeader project = null;
//			if (v.getProjectId() != null) {
//                project = projectHeaderService.selectByPrimaryKey(v.getProjectId());
//            }
//            if (project != null) {
//                officeCode = StringUtils.trimToEmpty(project.getColumn001());
//                v.setOfficeCode(officeCode);
//                v.setProjectName(project.getProjectName());
//            }
			// temp.setCompID(user.getCompId());
			temp.setDisabled(false);
			tempParam.setModel(temp);
			pageParam.setModel(v);

			pageParam.setTotal(service.countBySelectivePageable(tempParam));
			pageParam.setFiltered(service.countBySelectivePageable(pageParam));
			list = service.selectBySelectivePageable(pageParam);

			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		model.addAttribute("data", list);

		List<DataTableColumn> columns = this.findColumnList(getDataNameTable());
		pageParam.setColumns(columns);
		return getRealViewNameSpace() + "list";
	}
	
	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			DailyReport v = service.selectByPrimaryKey(id);
			DailyReportVO vo = new DailyReportVO();
			BeanUtils.copyProperties(v, vo);
			if (!checkPermission(vo, model, getDataName() + ":detail")) {
				model.addAttribute("status", false);
				model.addAttribute("message", "没有权限进行该操作！");
				return Consts.VIEW_UNAUTHORIZED;
			}
			if (v != null) {
				model.addAttribute("targetValue", v);
				
				List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

//				String navDataName = getDataNameNavTab();
//				if (Boolean.TRUE.equals(v.getDispatched())) {
//					navDataName += "_dispatched";
//				}
				List<?> navTavList = this.findNavTabList(getDataNameNavTab(), model);
				model.addAttribute("tabList", navTavList);
			}
		}  else {
			model.addAttribute("model", getViewModel());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = { "/detail", "/modals/detail" })
	public String detail(DailyReportVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":detail")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			Integer projectId = v.getProjectId();
			if (projectId != null) {
				ProjectHeader temp = projectHeaderService.selectByPrimaryKey(projectId);
				ProjectVO project = new ProjectVO();
				BeanUtils.copyProperties(temp, project);
				project.setCustomInfo(temp.getCustomInfo());
				v = new DailyReportVO();
				v.setProjectId(project.getProjectId());
				v.setProjectType(project.getProjectType());
				v.setProjectCode(project.getProjectCode());
				v.setProjectName(project.getProjectName());
				v.setOfficeCode(project.getColumn001());
				v.setContractNo(project.getContractNo());
				v.setOfficeName((String) project.getCustomInfoByKey("officeName"));
				v.setCustomInfoByKey("project", project);
				model.addAttribute("targetValue", v);
			}
			List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
		} else {
			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(DailyReportVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":add")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		Boolean status = true;
		String message = null;
		try {
			Integer projectId = v.getProjectId();
			if (projectId != null) {
				ProjectHeader temp = projectHeaderService.selectByPrimaryKey(projectId);
				ProjectVO project = new ProjectVO();
				BeanUtils.copyProperties(temp, project);
				project.setCustomInfo(temp.getCustomInfo());
				v.setProjectId(project.getProjectId());
				v.setProjectType(project.getProjectType());
				v.setProjectCode(project.getProjectCode());
				v.setProjectName(project.getProjectName());
				v.setOfficeCode(project.getColumn001());
				v.setContractNo(project.getContractNo());
				v.setCustomInfoByKey("officeName", (String) project.getCustomInfoByKey("officeName"));
				v.setCustomInfoByKey("project", project);
			}
			service.insertSelective(v);
			model.addAttribute("targetName", "dailyReportVO");
		} catch (Exception e) {
			status = false;
			message = StringUtils.trimToEmpty(e.getMessage());
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getRealViewNameSpace() + "detail";
	}

	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, DailyReportVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":edit")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		
//		// 终止正在进行中的任务
//		PmWorkFlowVO workflow = new PmWorkFlowVO();
//		workflow.setDataId(id);
//		workflow.setDataType(DataType.INDUSTRY_ASSET);
//		workflow.setStatus(PmWorkFlowVO.PENDING);
//		pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
		
		v.setStatus("0");
		return super.update(id, v, model);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(null, model, getDataName() + ":delete")) {
			return;
		}
		Boolean status = true;
		String message = null;
		try {
//			// 终止正在进行中的任务
//			PmWorkFlowVO workflow = new PmWorkFlowVO();
//			workflow.setDataId(id);
//			workflow.setDataType(DataType.INDUSTRY_ASSET);
//			workflow.setStatus(PmWorkFlowVO.PENDING);
//			pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
			
			DailyReportVO v = new DailyReportVO();
			v.setId(id);
			v.setDisabled(true);
			service.updateByPrimaryKeySelective(v);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}
	
	@Override
	public boolean checkPermission(DailyReportVO v, Model model, String... permissions) {
		return super.checkPermission(v, model, permissions);
	}

}