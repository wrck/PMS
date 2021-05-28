package com.dp.plat.pms.springmvc.controller;

import static com.dp.plat.core.param.RoleConstant.ROLE_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_SUB_ADMIN;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.NotifyTemplate;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.impl.NotifyTemplateService;
import com.dp.plat.core.util.DateUtil;
import com.dp.plat.core.util.DownloadUtils;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.DailyReport;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IDailyReportService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.util.DocUtil;
import com.dp.plat.pms.springmvc.vo.DailyReportVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.support.mail.MailUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

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
				// 添加指派的项目成员
				temp.setMemberCode(user.getUserName());
				v.setMemberCode(user.getUserName());
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
			temp.setCreateBy(v.getCreateBy());
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

		if (v.getProjectId() != null) {
			setLocalVariables("dataPrefix", "project");
		}
		List<DataTableColumn> columns = this.findColumnList(getDataNameTable());
		pageParam.setColumns(columns);
		clearLocalVariables();
		return getRealViewNameSpace() + "list";
	}
	
	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			DailyReportVO vo = new DailyReportVO();
			vo.setId(id);
			if (!checkPermission(vo, model, getDataName() + ":detail")) {
				model.addAttribute("status", false);
				model.addAttribute("message", "没有权限进行该操作！");
				return Consts.VIEW_UNAUTHORIZED;
			}
			DailyReport v = service.selectByPrimaryKey(id);
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
		if (!super.checkPermission(v, model, getDataName() + ":detail")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			Integer projectId = v.getProjectId();
			// 复制新增
			if (v.getId() != null) {
				DailyReport dailyReport = service.selectByPrimaryKey(v.getId());
				BeanUtils.copyProperties(dailyReport, v, "id", "createBy", "createTime", "processTime", "isReported", "status", "disabled");
				v.setCustomInfoByKey("createName", null);
				if ("view".equalsIgnoreCase(String.valueOf(model.getAttribute("permissionType")))) {
					model.addAttribute("permissionType", "edit");
				}
			} else {
				v = new DailyReportVO();
			}
			if (projectId != null) {
				ProjectHeader temp = projectHeaderService.selectByPrimaryKey(projectId);
				if(temp != null) {
					ProjectVO project = new ProjectVO();
					BeanUtils.copyProperties(temp, project);
					project.setCustomInfo(temp.getCustomInfo());
					v.setProjectId(project.getProjectId());
					v.setProjectType(project.getProjectType());
					v.setProjectCode(project.getProjectCode());
					v.setProjectName(project.getProjectName());
					v.setOfficeCode(project.getColumn001());
					v.setContractNo(project.getContractNo());
					v.setOfficeName((String) project.getCustomInfoByKey("officeName"));
					v.setCustomInfoByKey("project", project);
				}
			}
			model.addAttribute("targetValue", v);
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
		if (!super.checkPermission(v, model, getDataName() + ":add")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		Boolean status = true;
		String message = null;
		try {
			Integer projectId = v.getProjectId();
			if (projectId != null && projectId > 0) {
				ProjectHeader temp = projectHeaderService.selectByPrimaryKey(projectId);
				if (temp != null) {
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
		
		Integer projectId = v.getProjectId();
		if (projectId != null && projectId > 0) {
			ProjectHeader temp = projectHeaderService.selectByPrimaryKey(projectId);
			if (temp != null) {
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
		}
		return super.update(id, v, model);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		DailyReport dailyReport = service.selectByPrimaryKey(id);
		DailyReportVO v = new DailyReportVO();
		BeanUtils.copyProperties(dailyReport, v);
		if (!checkPermission(v, model, getDataName() + ":delete")) {
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
			
//			String permissionType = (String) model.getAttribute("permissionType");
//			if ("all".equals(permissionType) || "edit".equals(permissionType)) {
				v = new DailyReportVO();
				v.setId(id);
				v.setDisabled(true);
				service.updateByPrimaryKeySelective(v);
//			}
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}
	
	@PostMapping("export/{exportType}/report")
	public String exportDailyReportDoc(@PathVariable("exportType") String exportType, DailyReportVO vo, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (!checkPermission(vo, model, getDataName() + ":detail")) {
			return "redirect:" + Consts.VIEW_UNAUTHORIZED;
		}
		if (StringUtils.isBlank(exportType)) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return "redirect:" + Consts.VIEW_UNAUTHORIZED;
		}
		Principal user = UserContext.getCurrentPrincipal();
		// 日报导出
		Date processStartDate = vo.getProcessStartTime();
		Date processEndDate = vo.getProcessEndTime();
		Date processDate = vo.getProcessTime();
		Date weekStartDate = processStartDate != null ? processStartDate : DateUtil.getWeekStartDay(processDate);
		Date weekEndDate = processEndDate != null ? processEndDate : DateUtil.getWeekEndDay(processDate);
		Date nextWeekStartDate = processStartDate != null ? processStartDate : DateUtil.getWeekStartDay(processDate, -1);
		Date nextWeekEndDate = processEndDate != null ? processEndDate : DateUtil.getWeekEndDay(processDate, -1);
		List<Object> list = new ArrayList<Object>();
		if ("daily".equals(exportType) || "week".equals(exportType)) {
			PageParam<DailyReportVO> pageParam = new PageParam<DailyReportVO>();
			pageParam.setPageSize(-1);
			
			DailyReportVO dailyReportVO = new DailyReportVO();
			dailyReportVO.setDisabled(false);
			dailyReportVO.setCreateBy(vo.getCreateBy());
			// 允许访问的项目类型
			if (!UserContext.hasAnyRoles(ROLE_PM_ADMIN, ROLE_ADMIN)) {
				String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
				dailyReportVO.setProjectTypes(projectTypes);
				
				// 非子项目管理员，添加允许访问的办事处权限
				String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
				if (!UserContext.hasRole(ROLE_PM_SUB_ADMIN)) {
					dailyReportVO.setOfficeCodes(officeCodes);
					
				}
				// 添加指派的项目成员
				dailyReportVO.setMemberCode(user.getUserName());
			}
			pageParam.setModel(dailyReportVO);
			
			// 当周日报
			dailyReportVO.setType("report");
			dailyReportVO.setProcessStartTime(weekStartDate);
			dailyReportVO.setProcessEndTime(weekEndDate);
			List<Object> temp = service.selectBySelectivePageable(pageParam);
			list.addAll(temp);
			
			// 下周计划
			dailyReportVO.setType("plan");
			dailyReportVO.setProcessStartTime(nextWeekStartDate);
			dailyReportVO.setProcessEndTime(nextWeekEndDate);
			temp = service.selectBySelectivePageable(pageParam);
			list.addAll(temp);
		}
		Map<String, String> weekDayMap = new LinkedHashMap<String, String>();
		weekDayMap.put("W1", "周一");
		weekDayMap.put("W2", "周二");
		weekDayMap.put("W3", "周三");
		weekDayMap.put("W4", "周四");
		weekDayMap.put("W5", "周五");
		weekDayMap.put("W6", "周六");
		weekDayMap.put("W7", "周日");
		if (list != null) {
			Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
			dataMap.put("monthWeek", DateUtil.getMonthWeek(weekEndDate, -2));
			dataMap.put("weekStartDate", weekStartDate);
			dataMap.put("weekEndDate", weekEndDate);
			dataMap.put("nextWeekDate", nextWeekStartDate);
			dataMap.put("nextWeekStartDate", nextWeekStartDate);
			dataMap.put("nextWeekEndDate", nextWeekEndDate);
			dataMap.put("weekDayMap", weekDayMap);
			Map<String, Object> reportUsersMap = new LinkedHashMap<String, Object>();
			// {nextWeekDate, reportUsers:{reportUser: {reportName, report:[], plan:[]}}}
			for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
				DailyReportVO report = (DailyReportVO) iterator.next();
				String type = report.getType();
				Date processTime = report.getProcessTime();
				// 用于周内分组
				int day = processTime.getDay();
				String weekDay = "W" + (day == 0 ? 7 : day);
				String reportUser = report.getCreateBy();
				String reprotName = (String) report.getCustomInfoByKey("createName");
				Integer projectId = report.getProjectId();
				String projectName = report.getProjectName();
				String helpOrChance = report.getRemark(); //求助/挖掘到新项目机会
				
				// 按人员进行分组
				Map<String, Object> userMap = (Map<String, Object>) reportUsersMap.getOrDefault(reportUser, new LinkedHashMap<String, Object>());
				userMap.put("reportUser", reportUser);
				userMap.put("reportName", reprotName);
				
//				// 按项目名称进行分组
//				Map<String, Map<String, List>> projectMap= (Map<String, Map<String, List>>) userMap.getOrDefault("projectMap", new LinkedHashMap<String, Object>());
//				Map<String, List> projectListMap = (Map<String, List>) projectMap.getOrDefault(type, new LinkedHashMap<String, List>());
//				List projectReportList = (List) projectListMap.getOrDefault(projectName, new ArrayList());
//				projectReportList.add(report);
//				projectListMap.put(projectName, projectReportList);
//				projectMap.put(type, projectListMap);
//				userMap.put("projectMap", projectMap);
				
//				// 存储所有的求助/挖掘到新项目机会
//				LinkedHashSet<String> helpOrChanceSet = (LinkedHashSet<String>) userMap.getOrDefault("helpOrChanceSet", new LinkedHashSet<String>());
//				helpOrChanceSet.add(helpOrChance);
//				userMap.put("helpOrChanceSet", helpOrChanceSet);
				
//				Map<String, Object> helpOrChanceMap= (Map<String, Object>) userMap.getOrDefault("helpOrChanceMap", new LinkedHashMap<String, List>());
//				Set<String> helpOrChanceSet = (Set<String>) helpOrChanceMap.getOrDefault(type, new LinkedHashSet<String>());
//				helpOrChanceSet.add(helpOrChance);
//				helpOrChanceMap.put(type, helpOrChanceSet);
//				userMap.put("helpOrChanceMap", helpOrChanceMap);
				
				// 根据日报类型，将工作记录拆分为两个Map
				Map<String, Object> listMap =  (Map<String, Object>) userMap.getOrDefault(type, new LinkedHashMap<String, Object>());
				// 按周内星期几汇总日报
				List reportList = (List) listMap.getOrDefault(weekDay, new ArrayList());
				reportList.add(report);
				// Map主键为周内天，值为List
				listMap.put(weekDay, reportList);
				
				// 按项目名称进行分组
				Map<String, List> projectListMap = (Map<String, List>) listMap.getOrDefault("projectMap", new LinkedHashMap<String, List>());
				List projectReportList = (List) projectListMap.getOrDefault(projectName, new ArrayList());
				projectReportList.add(report);
				projectListMap.put(projectName, projectReportList);
				listMap.put("projectMap", projectListMap);
				
				// 存储所有的求助/挖掘到新项目机会
				Set<String> helpOrChanceSet = (Set<String>) listMap.getOrDefault("helpOrChanceSet", new LinkedHashSet<String>());
				helpOrChanceSet.add(helpOrChance);
				listMap.put("helpOrChanceSet", helpOrChanceSet);

				userMap.put(type, listMap);
				reportUsersMap.put(reportUser, userMap);
			}
			String weekStart = DateUtil.getDateTime("yyyy.MM.dd", weekStartDate);
			String weekEnd = DateUtil.getDateTime("yyyy.MM.dd", weekEndDate);
			String zipName = "工作记录";
			List<FileInfo> files = new ArrayList<FileInfo>(reportUsersMap.size());
			if ("week".equals(exportType)) {
				zipName = String.format("【周报】安服工作周报%s-%s.zip", weekStart, weekEnd);
				dataMap.put("reportUsers", reportUsersMap);
				String fileName = String.format("【周报】安服工作周报%s-%s.xlsx", weekStart, weekEnd);
				File doc = new DocUtil().createDoc(dataMap, "/template/", "安服工作周报.ftl", fileName, request);
				FileInfo fileInfo = new FileInfo();
				fileInfo.setName(doc.getName());
				fileInfo.setPath(doc.getAbsolutePath());
				files.add(fileInfo);
			} else if ("daily".equals(exportType)) {
				zipName = String.format("【日报】%s-%s一周工作记录.zip", weekStart, weekEnd);
				for (Entry<String, Object> reportUserMap : reportUsersMap.entrySet()) {
					String reporyUser = reportUserMap.getKey();
					Map<String, Object> userMap = (Map<String, Object>) reportUserMap.getValue();
					String reportName = (String) userMap.get("reportName");
					Map<String, Object> tempMap = new HashMap<String, Object>(1);
					tempMap.put(reporyUser, userMap);
					dataMap.put("reportUsers", tempMap);
					String fileName = String.format("【日报】%s-%s%s一周工作记录.xlsx", weekStart, weekEnd, reportName);
					File doc = new DocUtil().createDoc(dataMap, "/template/", "安服工作日报.ftl", fileName, request);
					FileInfo fileInfo = new FileInfo();
					fileInfo.setName(doc.getName());
					fileInfo.setPath(doc.getAbsolutePath());
					files.add(fileInfo);
				}
			}
			if (files.size() == 1) {
				DownloadUtils.downFile(response, request, files.get(0).getPath(), files.get(0).getName());
			} else {
				DownloadUtils.downTempZip("/dowload/temp/report", zipName, files, request, response);
			}
		} else {
			return "redirect:" + Consts.VIEW_UNAUTHORIZED;
		}
		return null;
	}
	
	@GetMapping(value = {"/mail/{mailType}/select", "/modals/mail/{mailType}/select"})
	public String mailSelectList(@PathVariable("mailType") String mailType, PageParam<Object> pageParam, DailyReportVO v, Model model) {
		String viewName = null;
		try {
			model.addAttribute("mailType", mailType);
			v.setType("report");
			v.setCreateBy(UserContext.getUsername());
//			if (v.getProcessStartTime() == null) {
//				v.setProcessStartTime(new Date());
//				v.setProcessEndTime(new Date());
//			}
			if (v.getIsReported() == null) {
				v.setIsReported(false);
			}
			if (HttpContext.isJSON()) {
				viewName = this.list(pageParam, v, model);
				
				setLocalVariables("dataPrefix", "mailSelect");
				List<DataTableColumn> columnList = this.findColumnList(getDataNameTable());
				pageParam.setColumns(columnList);
//				model.addAttribute("columns", columnList);
			}  else {
				model.addAttribute("model", getViewModel());
		
				String servletPath = HttpContext.getCurrentRequest().getServletPath();
				model.addAttribute("isModals", servletPath.contains("/modals/"));
			}
		} finally {
			clearLocalVariables();
		}
		return Consts.VIEW_UNAUTHORIZED.equals(viewName) ? Consts.VIEW_UNAUTHORIZED : getViewNameSpace() + "mailSelect";
	}
	
	@PostMapping("/mail/{mailType}/report")
	public String mailDailyReport(@PathVariable("mailType") String mailType, DailyReportVO vo, Model model) {
		if (!checkPermission(vo, model, getDataName() + ":detail")) {
			return "redirect:" + Consts.VIEW_UNAUTHORIZED;
		}
		if (StringUtils.isBlank(mailType)) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return "redirect:" + Consts.VIEW_UNAUTHORIZED;
		}
		Principal user = UserContext.getCurrentPrincipal();
		// 日报导出
		Date processStartDate = vo.getProcessStartTime();
		Date processEndDate = vo.getProcessEndTime();
		Date processDate = vo.getProcessTime() != null ? vo.getProcessTime() : new Date();
		Date weekStartDate = processStartDate != null ? processStartDate : DateUtil.getWeekStartDay(processDate);
		Date weekEndDate = processEndDate != null ? processEndDate : DateUtil.getWeekEndDay(processDate, -1);
		List<Object> list = null;
		if ("daily".equals(mailType)) {
			PageParam<DailyReportVO> pageParam = new PageParam<DailyReportVO>();
			pageParam.setPageSize(-1);
			
			DailyReportVO dailyReportVO = new DailyReportVO();
//			dailyReportVO.setProcessStartTime(weekStartDate);
//			dailyReportVO.setProcessEndTime(weekEndDate);
			dailyReportVO.setIds(vo.getIds());
			dailyReportVO.setDisabled(false);
			dailyReportVO.setIsReported(false);
			dailyReportVO.setCreateBy(user.getUserName());
			// 允许访问的项目类型
			if (!UserContext.hasAnyRoles(ROLE_PM_ADMIN, ROLE_ADMIN)) {
				String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
				dailyReportVO.setProjectTypes(projectTypes);
				
				// 非子项目管理员，添加允许访问的办事处权限
				String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
				if (!UserContext.hasRole(ROLE_PM_SUB_ADMIN)) {
					dailyReportVO.setOfficeCodes(officeCodes);
					
				}
				// 添加指派的项目成员
				dailyReportVO.setMemberCode(user.getUserName());
			}
			pageParam.setModel(dailyReportVO);
			list = service.selectBySelectivePageable(pageParam);
		}
		Map<String, String> weekDayMap = new LinkedHashMap<String, String>();
		weekDayMap.put("W1", "周一");
		weekDayMap.put("W2", "周二");
		weekDayMap.put("W3", "周三");
		weekDayMap.put("W4", "周四");
		weekDayMap.put("W5", "周五");
		weekDayMap.put("W6", "周六");
		weekDayMap.put("W7", "周日");
		if (list != null && !list.isEmpty()) {
			NotifyTemplateService notifyTemplateService = SpringContext.getBean(NotifyTemplateService.class);
			NotifyTemplate notifyTemplate = notifyTemplateService.selectByTemplateCode("pm.af.dailyReport.mail.template");
			Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
			Date reportDate = new Date();
			int day = reportDate.getDay();
			String weekDay = "W" + (day == 0 ? 7 : day);
			dataMap.put("reportDate", DateUtil.getDateTime("yyyy-MM-dd", reportDate));
			dataMap.put("weekDay", weekDayMap.get(weekDay));
			dataMap.put("reportName", user.getRealName());
			dataMap.put("reportList", list);
			
			// 加载需要装填的模板
			Template template = null;
			try {
				Configuration configure = new Configuration(Configuration.getVersion());
				configure.setDefaultEncoding("utf-8");
				StringTemplateLoader templateLoader = new StringTemplateLoader();
				templateLoader.putTemplate(notifyTemplate.getTemplateCode(), notifyTemplate.getContent());
				templateLoader.putTemplate(notifyTemplate.getTemplateCode() + ".subject", notifyTemplate.getSubject());
				configure.setTemplateLoader(templateLoader);
				// 设置异常处理器
				configure.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
				// 定义Template对象，注意模板类型名字与downloadType要一致
				template = configure.getTemplate(notifyTemplate.getTemplateCode() + ".subject");
				String subject = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);
				template = configure.getTemplate(notifyTemplate.getTemplateCode());
				String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);
				
				// 获取自定义邮件主送，抄送人员
				Set<Integer> reportIds = new HashSet<Integer>();
				Set<String> tos = new HashSet<String>();
				Set<String> ccs = new HashSet<String>();
				// 主送当前人员
				tos.add(user.getEmail());
				for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
					DailyReport dailyReport = (DailyReport) iterator.next();
					reportIds.add(dailyReport.getId());
					
					String customTos = StringUtils.trimToEmpty(dailyReport.getCustomTos());
					String[] splitTos = StringUtils.split(customTos, ";,");
					tos.addAll(Arrays.asList(splitTos));
					
					String customCcs = StringUtils.trimToEmpty(dailyReport.getCustomCcs());
					String[] splitCcs = StringUtils.split(customCcs, ";,");
					ccs.addAll(Arrays.asList(splitCcs));
				}
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("tos", StringUtils.join(tos, ";"));
				context.put("ccs", StringUtils.join(ccs, ";"));
				context.put("subject", subject);
				context.put("content", content);
				MailUtil.keepMail(context , false);
				
				for (Integer id : reportIds) {
					DailyReport t = new DailyReport();
					t.setId(id);
					t.setIsReported(true);
					service.updateByPrimaryKeySelective(t);
				}
			} catch (Exception e) {
				ExceptionHandler.insertException(e);
			}
		} else {
			model.addAttribute("status", false);
			model.addAttribute("message", "请选择需要发送的日报！");
		}
		return null;
	}
	
	@Override
	public boolean checkPermission(DailyReportVO v, Model model, String... permissions) {
		if (!super.checkPermission(v, model, permissions)) {
			return false;
		}
		PermissionResult result = service.checkPermission(v, permissions);
		model.addAllAttributes(result.getMap());
		return result.isPermit();
	}

}