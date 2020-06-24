package com.dp.plat.pms.springmvc.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.util.DownloadUtils;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.job.DispatchSettlementSEEPaymentJob;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.util.DocUtil;
import com.dp.plat.pms.springmvc.vo.CommonRelatedDataVO;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.ProjectDeliver;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "settlement")
public class DispatchSettlementController
		extends AbstractController<IDispatchSettlementService, DispatchSettlement, SettlementVO> {

	@Autowired
	private IProjectHeaderService projectHeaderService;
	
	@Autowired
	private IDispatchProjectService dispatchProjectService;

	@Autowired
	private IDispatchSettlementService dispatchSettlementService;

	@PostConstruct
	public void init() {
		this.setViewModel("settlement");
		this.setUseTemplate(false);
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, SettlementVO settlement, Model model) {
		if (!checkPermission(settlement, model, getDataName() + ":list")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		
		Principal user = UserContext.getCurrentPrincipal();
		settlement.setDispatched(true);
//		settlement.setDisabled(false);
		// settlement.setCompId(user.getCompId());
		PageParam<Object> tempParam = new PageParam<>();
		SettlementVO temp = new SettlementVO();
		temp.setDispatched(true);
//		temp.setDisabled(false);
		// temp.setCompID(user.getCompId());
		// 允许访问的项目类型
		if (!UserContext.hasAnyRoles(RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN)) {
			String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
			temp.setProjectTypes(projectTypes);
			settlement.setProjectTypes(projectTypes);
			
			// 非子项目管理员，添加允许访问的办事处权限
			String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
			if (!UserContext.hasRole(RoleConstant.ROLE_PM_SUB_ADMIN)) {
				temp.setOfficeCodes(officeCodes);
				settlement.setOfficeCodes(officeCodes);
			}
		}
		tempParam.setModel(temp);
		pageParam.setModel(settlement);

		pageParam.setTotal(dispatchSettlementService.countSettlementWidthDispatchPageable(tempParam));
		pageParam.setFiltered(dispatchSettlementService.countSettlementWidthDispatchPageable(pageParam));
		List<Object> list = dispatchSettlementService.selectSettlementWidthDispatchPageable(pageParam);

		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		model.addAttribute("data", list);

		List<DataTableColumn> columns = this.findColumnList(DATANAME_TABLE);
		pageParam.setColumns(columns);
		return getViewNameSpace() + "list";
	}

	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			DispatchSettlement settlement = dispatchSettlementService.selectByPrimaryKey(id);
			if (settlement != null) {
				SettlementVO temp = new SettlementVO();
				BeanUtils.copyProperties(settlement, temp);
				if (!checkPermission(temp, model, getDataName() + ":detail")) {
					model.addAttribute("status", false);
					model.addAttribute("message", "没有权限进行该操作！");
					return Consts.VIEW_UNAUTHORIZED;
				}
				DispatchProject dispatch = new DispatchVO();
				dispatch.setId(settlement.getDispatchId());
				dispatch.setDisabled(false);
				dispatch.setDispatched(true);
				List<DispatchVO> list = dispatchProjectService.selectDispatchVOWithAmountBySelective((DispatchVO) dispatch);
				if (!list.isEmpty()) {
					dispatch = list.get(0);
				} else {
					dispatch = dispatchProjectService.selectByPrimaryKey(settlement.getDispatchId());
				}
					
				temp.setDispatch(dispatch);
				model.addAttribute("targetValue", temp);

				List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);
				
				List<?> navTavList = this.findNavTabList(getDataNameNavTab(), model);
				model.addAttribute("tabList", navTavList);
			}
		} else {
			model.addAttribute("model", getViewModel());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value= {"/detail", "/modals/detail"})
	public String detail(SettlementVO settlement, Model model) {
		if (!checkPermission(settlement, model, getDataName() + ":detail")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			Boolean status = true;
			String message = null;
			Integer dispatchId = settlement.getDispatchId();
			if (dispatchId != null) {
				
				DispatchVO temp = new DispatchVO();
				temp.setId(dispatchId);
				temp.setDisabled(false);
				temp.setDispatched(true);
//				List<DispatchProject> list = dispatchProjectService.selectBySelective(temp);
				List<DispatchVO> list = dispatchProjectService.selectDispatchVOWithAmountBySelective(temp);
				if (!list.isEmpty()) {
					DispatchVO dispatch = list.get(0);
					settlement = new SettlementVO();
					settlement.setDispatch(dispatch);
					settlement.setDispatchId(dispatch .getId());
					settlement.setDispatchSeq(dispatch.getDispatchSeq());
//					settlement.setContractNos(dispatch.getContractNos());
//					settlement.setSmsProjectCode(dispatch.getSmsProjectCode());
//					settlement.setSmsProjectName(dispatch.getDispatchName());
//					settlement.setSmsSubmitTime(dispatch.getSmsSubmitTime());
//					settlement.setSmsProjectAmount(dispatch.getSmsProjectAmount());
//					settlement.setCollectedAmount(dispatch.getCollectedAmount());
//					settlement.setDeliveredAmount(dispatch.getDeliveredAmount());
//					settlement.setContractAmount(dispatch.getContractAmount());
//					settlement.setSettledAmount(dispatch.getSettledAmount());
//					settlement.setSmsOrderExecNumber((String) dispatch.getCustomInfoByKey("smsOrderExecNumber"));
////					settlement.setCustomInfoByKey("collectedAmount", dispatch.getCollectedAmount());
////					settlement.setCustomInfoByKey("deliveredAmount", dispatch.getDeliveredAmount());
////					settlement.setCustomInfoByKey("contractAmount", dispatch.getContractAmount());
////					settlement.setCustomInfoByKey("settledAmount", dispatch.getSettledAmount());
					
					// 补充实施进度和验收进展
					ProjectVO project = projectHeaderService.selectVOByProjectId(Integer.valueOf(dispatch.getProjectIds()));
					if (null != project) {
//						ProjectVO pvo = new ProjectVO();
//						BeanUtils.copyProperties(project, pvo);
						settlement.setProgressDesc((String) project.getCustomInfoByKey("projectProgress"));
						settlement.setAcceptanceDesc(project.getProjectStateName());
					}
					model.addAttribute("targetValue", settlement);
				} else {
					status = false;
					message = "没有找到满足条件的派单记录";
				}
			}
			List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
			
			model.addAttribute("status", status);
			model.addAttribute("message", message);
		} else {
			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(SettlementVO settlement, Model model) {
		if (!checkPermission(settlement, model, getDataName() + ":add")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		Boolean status = false;
		String message = null;
		try {
			Integer dispatchId = settlement.getDispatchId();
			if (dispatchId != null) {
				DispatchVO temp = new DispatchVO();
				temp.setId(dispatchId);
				temp.setDisabled(false);
				temp.setDispatched(true);
				long count = dispatchProjectService.countBySelective(temp);
				if (count > 0) {
					dispatchSettlementService.insertSelective(settlement);
					model.addAttribute("targetName", "settlementVO");
					status = true;
				} else {
					message = String.format("没有找到派单编号[%s]的派单记录", settlement.getDispatchSeq());
				}
			} else {
				message = "请选择需要结算的派单编号！";
			}
		} catch (Exception e) {
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, SettlementVO settlement, Model model) {
		return super.update(id, settlement, model);
//		if (!checkPermission(settlement, model, getDataName() + ":edit")) {
//			model.addAttribute("status", false);
//			model.addAttribute("message", "没有权限进行该操作！");
//			return Consts.VIEW_UNAUTHORIZED;
//		}
//		Boolean status = true;
//		String message = null;
//		try {
//			dispatchSettlementService.updateByPrimaryKeySelective(settlement);
//			model.addAttribute("targetName", "settlementVO");
//		} catch (Exception e) {
//			status = false;
//			Integer errorId = ExceptionHandler.insertException(e);
//			model.addAttribute("errorId", errorId);
//			message = e.getMessage();
//		}
//		model.addAttribute("status", status);
//		model.addAttribute("message", message);
//		return getViewNameSpace() + "detail";
	}
	
	
	@PostMapping("{id}/projectInfoDoc")
	public void exportProjectInfoDoc(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response, Model model) {
		DispatchSettlement settlement = dispatchSettlementService.selectByPrimaryKey(id);
		if (settlement != null) {
			SettlementVO temp = new SettlementVO();
			BeanUtils.copyProperties(settlement, temp);
			if (!checkPermission(temp, model, getDataName() + ":detail")) {
				model.addAttribute("status", false);
				model.addAttribute("message", "没有权限进行该操作！");
				return;
			}
			DispatchProject dispatch = new DispatchVO();
			dispatch.setId(settlement.getDispatchId());
			dispatch.setDisabled(false);
			dispatch.setDispatched(true);
			List<DispatchVO> list = dispatchProjectService.selectDispatchVOWithAmountBySelective((DispatchVO) dispatch);
			if (!list.isEmpty()) {
				dispatch = list.get(0);
			} else {
				dispatch = dispatchProjectService.selectByPrimaryKey(settlement.getDispatchId());
			}
				
			temp.setDispatch(dispatch);
			
			String templateFileName = SystemConfig.systemVariables.getOrDefault("af.export.settlement.projectInfo", "02项目信息单-%s.doc");
			String fileName = String.format(templateFileName, dispatch.getDispatchName());
			File doc = new DocUtil().createDoc((Map<String, Object>) JSON.toJSON(temp), "/template/", "项目信息单.ftl", fileName, request);
			DownloadUtils.downFile(response, request, doc.getAbsolutePath(), doc.getName());
		}
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		DispatchSettlement dispatchSettlement = service.selectByPrimaryKey(id);
		SettlementVO vo = new SettlementVO();
		BeanUtils.copyProperties(dispatchSettlement, vo);
		if (!checkPermission(vo, model, getDataName() + ":delete")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return;
		}
		Boolean status = true;
		String message = null;
		try {
			DispatchSettlement settlement = new DispatchSettlement();
			settlement.setId(id);
			settlement.setDisabled(true);
			dispatchSettlementService.updateByPrimaryKeySelective(settlement);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}
	
	@RequestMapping("/syncPayment")
	public void syncSettlementPayment(Model model) {
		DispatchSettlementSEEPaymentJob settlementSEEPaymentJob = new DispatchSettlementSEEPaymentJob();
		settlementSEEPaymentJob.execute();
	}
	
	@Override
	public boolean checkPermission(SettlementVO v, Model model, String... permissions) {
		if (!super.checkPermission(v, model, permissions)) {
			return false;
		}
		boolean isPermit = false;
		String permissionType = "";
		if (!UserContext.checkPermission("project:*") && v != null) {
			Integer projectId = v.getProjectId();
			DispatchVO dispatchVO = new DispatchVO();
			dispatchVO.setProjectId(projectId);
			dispatchVO.setId(v.getDispatchId());
//			Map<String, Boolean> permission = new HashMap<String, Boolean>();
			PermissionResult permissionResult = dispatchProjectService.checkPermission(dispatchVO, "dispatch:list", "dispatch:detail");
			model.addAllAttributes(permissionResult.getMap());
			return permissionResult.isPermit();
//			Boolean allPerm = permission.get("all");
//			if (Boolean.TRUE.equals(allPerm)) {
//				isPermit = true;
//				permissionType = "all";
//			} else {
//				String perms = StringUtils.join(permissions, ",");
//				if (Boolean.TRUE.equals(permission.get("edit")) && perms.matches(".*settlement:(add|edit|delete)\\b,?.*")) {
//					isPermit = true;
//					permissionType = "edit";
//				}
//				if (Boolean.TRUE.equals(permission.get("view")) && perms.matches(".*settlement:(list|detail)\\b,?.*")) {
//					isPermit = true;
//					permissionType = "view";
//				}
//			}
		} else {
			isPermit = true;
			permissionType = "all";
		}
		model.addAttribute("permissionType", permissionType);
		return isPermit;
	}

//	@RequestMapping(value = "submit", method = RequestMethod.POST)
//	public void dispatchSubmit(SettlementVO settlement, Model model) {
//		Boolean status = true;
//		String message = null;
//		try {
//			dispatchSettlementService.insertOrUpdateSelective(settlement);
//			dispatchSettlementService.dispatchSubmit(settlement.getId(), settlement);
//			model.addAttribute("targetName", "settlementVO");
//		} catch (Exception e) {
//			status = false;
//			Integer errorId = ExceptionHandler.insertException(e);
//			model.addAttribute("errorId", errorId);
//			message = e.getMessage();
//		}
//		model.addAttribute("status", status);
//		model.addAttribute("message", message);
//	}

}