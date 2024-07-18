package com.dp.plat.pms.springmvc.controller;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
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
import com.dp.plat.core.annotation.SystemControllerLog;
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
import com.dp.plat.pms.springmvc.job.DispatchSettlementSEEPaymentJob;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.util.DocUtil;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
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
		this.setUrlNameSpace(ProjectConstant.URLPath.PROJECT_MANAGER);
		this.setViewModel("settlement");
		this.setUseTemplate(true);
	}

	@RequestMapping("/list")
	@SystemControllerLog(description = "查看转包结算列表")
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
			if (!UserContext.hasAnyRoles(RoleConstant.ROLE_PM_SUB_ADMIN, RoleConstant.ROLE_FINANCIAL_AP)) {
				temp.setOfficeCodes(officeCodes);
				settlement.setOfficeCodes(officeCodes);
				
			}
			// 添加指派的项目成员
			temp.setMemberCode(user.getUserName());
			settlement.setMemberCode(user.getUserName());
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
		return getRealViewNameSpace() + "list";
	}

	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	@SystemControllerLog(description = "查看结算编号【$targetValue.settleSeq$】的详情页面")
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
				
				List<Object> buttonList = this.findFieldList(getDataNameForm() + "Btn", DATATYPE_FORM);
				model.addAttribute("buttonList", buttonList);
				
				List<?> navTavList = this.findNavTabList(getDataNameNavTab(), model);
				model.addAttribute("tabList", navTavList);
			}
		} else {
			model.addAttribute("model", getViewModel());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value= {"/detail", "/modals/detail"})
	@SystemControllerLog(description = "打开转包合同【$targetValue.dispatch.dispatchNo$】的结算页面")
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
					settlement.setDispatchId(dispatch.getId());
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
					
//					// 补充实施进度和验收进展
//					ProjectVO project = projectHeaderService.selectVOByProjectId(Integer.valueOf(dispatch.getProjectIds()));
//					if (null != project) {
////						ProjectVO pvo = new ProjectVO();
////						BeanUtils.copyProperties(project, pvo);
////						String projectProgress = (String) project.getCustomInfoByKey("projectProgress");
////						if (StringUtils.isNotBlank(projectProgress)) {
////							projectProgress += "%"; 
////						}
//						settlement.setProgressDesc((String) project.getCustomInfoByKey("projectProgress"));
//						settlement.setAcceptanceDesc(project.getProjectStateName());
//					}
				} else {
					status = false;
					message = "没有找到满足条件的转包记录";
				}
			}
			model.addAttribute("targetValue", settlement);
			
			List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
			
			List<Object> buttonList = this.findFieldList(getDataNameForm() + "Btn", DATATYPE_FORM);
			model.addAttribute("buttonList", buttonList);
			
			model.addAttribute("status", status);
			model.addAttribute("message", message);
		} else {
			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@SystemControllerLog(description = "新增转包合同【$settlementVO.dispatch.dispatchNo$】的结算记录")
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
//					message = String.format("没有找到转包编号[%s]的转包记录", settlement.getDispatchSeq());
					message = String.format("没有找到转包合同号[%s]的转包记录", settlement.getDispatch().getDispatchNo());
				}
			} else {
				message = "请选择需要结算的转包编号！";
			}
		} catch (Exception e) {
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@SystemControllerLog(description = "更新转包合同【$settlementVO.dispatch.dispatchNo$】的结算记录")
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
//		return getRealViewNameSpace() + "detail";
	}
	
	@RequestMapping(value = "submit", method = RequestMethod.POST)
    @SystemControllerLog(description = "转包合同【$settlementVO.dispatch.dispatchNo$】确认结算")
    public void settlementSubmit(SettlementVO settlement, Model model) {
        Boolean status = true;
        String message = null;
        if (!checkPermission(settlement, model, getDataName() + ":submit")) {
            model.addAttribute("status", false);
            model.addAttribute("message", "没有权限进行该操作！");
            return;
        }
        try {
            dispatchSettlementService.insertOrUpdateSelective(settlement);
            model.addAttribute("targetName", "settlementVO");
            dispatchSettlementService.settlementSubmit(settlement.getId(), settlement);
        } catch (Exception e) {
            status = false;
            Integer errorId = ExceptionHandler.insertException(e);
            model.addAttribute("errorId", errorId);
            message = e.getMessage();
            if (message.contains("Duplicate entry")) {
                message = "结算编号已存在";
            }
        }
        model.addAttribute("status", status);
        model.addAttribute("message", message);
    }
	
	
	@PostMapping("{id}/projectInfoDoc")
	@SystemControllerLog(description = "生成【$settlementVO.dispatch.dispatchName$】项目信息单", ignoreParams = {"request", "response"})
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
			model.addAttribute("settlementVO", temp);
			
			String templateFileName = SystemConfig.systemVariables.getOrDefault("af.export.settlement.projectInfo", "02项目信息单-%s.doc");
			String fileName = String.format(templateFileName, dispatch.getDispatchName());
			File doc = new DocUtil().createDoc((Map<String, Object>) JSON.toJSON(temp), "/template/", "项目信息单.ftl", fileName, request);
			DownloadUtils.downFile(response, request, doc.getAbsolutePath(), doc.getName());
		}
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@SystemControllerLog(description = "删除【$settlementVO.settlementSeq$】结算记录")
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
		    if (Boolean.TRUE.equals(vo.getSettled()) || Boolean.TRUE.equals(vo.hasTask())) {
		        model.addAttribute("status", false);
	            model.addAttribute("message", "已结算或者流程中的结算记录不允许删除！");
	            return;
		    }
		    
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
		model.addAttribute("settlementVO", vo);
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
			PermissionResult permissionResult = dispatchProjectService.checkPermission(dispatchVO, "dispatch:list", "dispatch:detail", "settlement:list", "settlement:detail");
			
			// 项目转包结算人员，如果有权限查看，则允许进行编辑
			if (permissionResult.isPermit() && UserContext.hasAnyRoles(RoleConstant.ROLE_PM_DISPATCH_SETTLE_STAFF)) {
			    permissionResult.setPermissionType("edit");
//			    Collection<? extends String> currentPermistions = (Collection<? extends String>) model.getAttribute("permissions");
//			    permissionResult.getPermissions().addAll(currentPermistions != null ? currentPermistions : Collections.emptyList());
			}
			
			model.addAllAttributes(permissionResult.getMap());
			// 如果开启结算后只允许查询，则调整为view
			String readOnlyWhenSettled = SystemConfig.systemVariables.getOrDefault("pm.dispatch.settlement.settled.readonly", "1");
			if ("1".equals(readOnlyWhenSettled) && Boolean.TRUE.equals(v.getSettled()) || Boolean.TRUE.equals(v.hasTask())) {
			    model.addAttribute("permissionType", "view");
			}
			return permissionResult.isPermit();
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