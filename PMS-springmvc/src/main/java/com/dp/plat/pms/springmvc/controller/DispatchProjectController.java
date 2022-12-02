package com.dp.plat.pms.springmvc.controller;

import static com.dp.plat.core.param.RoleConstant.ROLE_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_FINANCIAL_AP;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_SUB_ADMIN;

import java.io.File;
import java.util.Collections;
import java.util.Date;
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
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.util.DownloadUtils;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.CommonRelatedData;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectManageUserService;
import com.dp.plat.pms.springmvc.util.DocUtil;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import com.dp.plat.pms.springmvc.vo.CommonRelatedDataVO;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "dispatch")
public class DispatchProjectController
		extends AbstractController<IDispatchProjectService, DispatchProject, DispatchVO> {

	@Autowired
	private IDispatchProjectService dispatchProjectService;
	
	@Autowired
	private IDispatchSettlementService dispatchSettlementService;

	@Autowired
	private IProjectHeaderService projectHeaderService;
	
	@Autowired
	private IProjectManageUserService projectManageUserService;
	

	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.PROJECT_MANAGER);
		this.setViewModel("dispatch");
		this.setUseTemplate(true);
	}

	@RequestMapping("/list")
	@SystemControllerLog(description = "查看转包记录", ignoreParams = {"request", "response"})
	public String list(PageParam<Object> pageParam, DispatchVO dispatch, Model model) {
		if (!checkPermission(dispatch, model, getDataName() + ":list")) {
			model.addAttribute("data", Collections.emptyList());
			return Consts.VIEW_UNAUTHORIZED;
		}
		
		Principal user = UserContext.getCurrentPrincipal();
		dispatch.setDisabled(false);
		dispatch.setEffectiveFrom(new Date());
		dispatch.setEffectiveTo(new Date());
		// dispatch.setCompId(user.getCompId());
		PageParam<Object> tempParam = new PageParam<>();
		DispatchVO temp = new DispatchVO();
		temp.setDisabled(false);
		temp.setEffectiveFrom(new Date());
		temp.setEffectiveTo(new Date());
		// temp.setCompID(user.getCompId());
		// 允许访问的项目类型
		if (!UserContext.hasAnyRoles(ROLE_PM_ADMIN, ROLE_ADMIN)) {
			String projectTypes = StringUtils.defaultString(user.getUserInfo().getCustom4(), "-1");
			temp.setProjectTypes(projectTypes);
			dispatch.setProjectTypes(projectTypes);
			
			// 非子项目管理员，添加允许访问的办事处权限
			String officeCodes = StringUtils.defaultString(user.getUserInfo().getCustom5(), "-1");
			if (!UserContext.hasAnyRoles(ROLE_PM_SUB_ADMIN, ROLE_FINANCIAL_AP)) {
				temp.setOfficeCodes(officeCodes);
				dispatch.setOfficeCodes(officeCodes);
				
				// 添加指派的项目成员
				temp.setMemberCode(user.getUserName());
				dispatch.setMemberCode(user.getUserName());
			}
		}
		tempParam.setModel(temp);
		pageParam.setModel(dispatch);

		pageParam.setTotal(dispatchProjectService.countBySelectivePageable(tempParam));
		pageParam.setFiltered(dispatchProjectService.countBySelectivePageable(pageParam));
		List<Object> list = dispatchProjectService.selectBySelectivePageable(pageParam);

		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		model.addAttribute("data", list);

		List<DataTableColumn> columns = this.findColumnList(DATANAME_TABLE);
		pageParam.setColumns(columns);
		return getRealViewNameSpace() + "list";
	}

	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	@SystemControllerLog(description = "查看【$targetValue.dispatchName$】转包详情")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			DispatchProject dispatch = dispatchProjectService.selectByPrimaryKey(id);
			if (dispatch != null) {
    			DispatchVO vo = new DispatchVO();
    			BeanUtils.copyProperties(dispatch, vo);
    			if (!checkPermission(vo, model, getDataName() + ":detail")) {
    				model.addAttribute("status", false);
    				model.addAttribute("message", "没有权限进行该操作！");
    				return Consts.VIEW_UNAUTHORIZED;
    			}
				model.addAttribute("targetValue", dispatch);
				
				List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);
				
				List<Object> buttonList = this.findFieldList(getDataNameForm() + "Btn", DATATYPE_FORM);
				model.addAttribute("buttonList", buttonList);

				String navDataName = DATANAME_NAVTAB;
				if (Boolean.TRUE.equals(dispatch.getDispatched())) {
					navDataName += "_dispatched";
				}
				List<?> navTavList = this.findNavTabList(navDataName, model);
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
	@SystemControllerLog(description = "打开【$targetValue.dispatchName$】转包详情页面")
	public String detail(DispatchVO dispatch, Model model) {
		if (!checkPermission(dispatch, model, getDataName() + ":detail")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		if (HttpContext.isJSON()) {
			String projectIds = dispatch.getProjectIds();
			ProjectVO project = new ProjectVO();
//			String[] projectIdArr = StringUtils.split(StringUtils.trimToEmpty(projectIds), ",");
//			for (String projectId : projectIdArr) {
				String projectId = StringUtils.trimToNull(projectIds);
				if (projectId != null) {
					ProjectHeader temp = projectHeaderService.selectByPrimaryKey(Integer.valueOf(projectId));
					if (temp != null) {
						BeanUtils.copyProperties(temp, project);
						project.setCustomInfo(temp.getCustomInfo());
					}
				}
				dispatch = new DispatchVO();
				dispatch.setProjectIds(project.getProjectId() != null ? project.getProjectId().toString() : null);
//					dispatch.setOfficeCode(project.getColumn001());
				dispatch.setDispatchName(project.getProjectName());
//					dispatch.setSmsProjectAmount(project.getSmsProjectAmount());
//					dispatch.setSmsAfProjectAmount(project.getSmsAfProjectAmount());
//					dispatch.setSmsProjectCode(project.getSmsProjectCode());
//					dispatch.setSmsSubmitTime(project.getSmsSubmitTime());
//					dispatch.setContractNos(project.getContractNo());
				dispatch.setCustomInfoByKey("project", project);
				dispatch.setCustomInfoByKey("smsOrderExecNumber", project.getSmsOrderExecNumber());
				model.addAttribute("targetValue", dispatch);
//			}
			List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
			
			List<Object> buttonList = this.findFieldList(getDataNameForm() + "Btn", DATATYPE_FORM);
			model.addAttribute("buttonList", buttonList);
		} else {
			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@SystemControllerLog(description = "新增【$dispatchVO.dispatchName$】转包记录")
	public String create(DispatchVO dispatch, Model model) {
		if (!checkPermission(dispatch, model, getDataName() + ":add")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		Boolean status = true;
		String message = null;
		try {
			dispatchProjectService.insertSelective(dispatch);
			model.addAttribute("targetName", "dispatchVO");
		} catch (Exception e) {
			status = false;
			message = StringUtils.trimToEmpty(e.getMessage());
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			if (message.contains("Duplicate entry")) {
				message = "派单编号已存在";
			}
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@SystemControllerLog(description = "更新【$dispatchVO.dispatchName$】转包记录")
	public String update(@PathVariable("id") Integer id, DispatchVO dispatch, Model model) {
//		return super.update(id, dispatch, model);
		if (!checkPermission(dispatch, model, getDataName() + ":edit")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return Consts.VIEW_UNAUTHORIZED;
		}
		Boolean status = true;
		String message = null;
		try {
			dispatchProjectService.updateByPrimaryKeySelective(dispatch);
			model.addAttribute("targetName", "dispatchVO");
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
			if (message.contains("Duplicate entry")) {
				message = "派单编号已存在";
			}
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@SystemControllerLog(description = "删除【$targetValue.dispatchName$】转包记录")
	public void delete(@PathVariable("id") Integer id, Model model) {
		DispatchProject dispatchProject = service.selectByPrimaryKey(id);
		DispatchVO vo = new DispatchVO();
		BeanUtils.copyProperties(dispatchProject, vo);
		if (!checkPermission(vo, model, getDataName() + ":delete")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return;
		}
		Boolean status = true;
		String message = null;
		try {
			DispatchSettlement settle = new DispatchSettlement();
			settle.setDispatchId(id);
			settle.setDisabled(false);
			long count = dispatchSettlementService.countBySelective(settle);
			if(count > 0) {
//				throw new RuntimeException("不允许删除关联结算单的派单记录");
				status = false;
				message = "不允许删除关联结算单的派单记录";
			} else {
				DispatchProject dispatch = new DispatchProject();
				dispatch.setId(id);
				dispatch.setDisabled(true);
				dispatch.setEffectiveTo(new Date());
				dispatchProjectService.updateByPrimaryKeySelective(dispatch);
			}
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("targetValue", vo);
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	@RequestMapping(value = "submit", method = RequestMethod.POST)
	@SystemControllerLog(description = "派单【$targetValue.dispatchName$】转包记录")
	public void dispatchSubmit(DispatchVO dispatch, Model model) {
		Boolean status = true;
		String message = null;
		if (!checkPermission(dispatch, model, getDataName() + ":submit")) {
			model.addAttribute("status", false);
			model.addAttribute("message", "没有权限进行该操作！");
			return;
		}
		try {
			dispatchProjectService.insertOrUpdateSelective(dispatch);
			dispatchProjectService.dispatchSubmit(dispatch.getId(), dispatch);
			model.addAttribute("targetName", "dispatchVO");
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = StringUtils.defaultIfBlank(e.getMessage(), e.getClass().getSimpleName());
			if (message != null && message.contains("Duplicate entry")) {
				message = "派单编号已存在";
			}
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	@RequestMapping("/modals/payment")
	public void dispatchPayment(Integer id, Model model) {
	}
	
	@PostMapping("{id}/{exportType}/info")
	public void exportProjectInfoDoc(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response, Model model) {
		DispatchProject dispatch = service.selectByPrimaryKey(id);
		if (dispatch != null) {
			DispatchVO vo = new DispatchVO();
			BeanUtils.copyProperties(dispatch, vo);
			// 只有框架协议有外派单
			if (!ProjectConstant.DispatchType.FRAMEWORK_AGREEMENT.equals(vo.getType()) || !checkPermission(vo, model, getDataName() + ":detail")) {
				model.addAttribute("status", false);
				model.addAttribute("message", "没有权限进行该操作！");
				return;
			}
			
			String dispatchName = (StringUtils.trimToEmpty(dispatch.getDispatchName()) + "项目").replace("项目项目", "项目");
			dispatch.setDispatchName(dispatchName);
			
			String fileName = String.format("%s[安全服务项目]%s外派.doc", dispatch.getDispatchSeq(), dispatchName);
			
			String dutyPerson = vo.getDutyPerson();
			UserInfo ui = new UserInfo();
			ui.setRealName(dutyPerson);
			List<UserInfo> users = projectManageUserService.selectBySelective(ui);
			if (!users.isEmpty()) {
				UserInfo info = users.get(0);
				String mobile = info.getMobile();
				if(StringUtils.isNotBlank(mobile)) {
					vo.setCustomInfoByKey("dutyPersonPhone", mobile);
				}
			}
			String officeDutyPerson = vo.getOfficeDutyPerson();
			ui.setRealName(officeDutyPerson);
			users = projectManageUserService.selectBySelective(ui);
			if (!users.isEmpty()) {
				UserInfo info = users.get(0);
				String mobile = info.getMobile();
				if(StringUtils.isNotBlank(mobile)) {
					vo.setCustomInfoByKey("officeDutyPersonPhone", mobile);
				}
			}
			
			Map dataMap = (Map<String, Object>) JSON.toJSON(vo);
			CommonRelatedDataVO t = new CommonRelatedDataVO();
			t.setDisabled(false);
			t.setEffectiveTo(new Date());
			t.setObjType("dispatch");
			t.setObjId(dispatch.getId());
			t.setType("dispatchWorkContent");
			List<CommonRelatedData> workContentList = commonRelatedDataService.selectBySelective(t);
			dataMap.put("workContentList", workContentList);
//			fileName = UploadUtils.getWebDir(request) + File.separator + "template" + File.separator + "Temp_安服框架协议外派单" + File.separator + fileName;
			File doc = new DocUtil().createDoc(dataMap, "template", "安服框架协议外派单.ftl", fileName, request);
			DownloadUtils.downFile(response, request, doc.getAbsolutePath(), doc.getName());
		}
	}
	
	/**
	 * 根据服务商编号生成派单编号
	 * @param facilitatorCode
	 * @param model
	 */
	@RequestMapping("/generateDispatchSeq")
	public void generateDispatchSeq(String facilitatorCode, Model model) {
		String dispatchSeq = dispatchProjectService.generateDispatchSeq(facilitatorCode);
		String dispatchNo = dispatchProjectService.generateDispatchNo(new Date(), dispatchSeq);
		model.addAttribute("dispatchSeq", dispatchSeq);
		model.addAttribute("dispatchNo", dispatchNo);
	}
	
	/**
	 * 用于结算时查询已派单记录的回款、结算情况
	 * @param dispatch
	 * @param model
	 */
	@RequestMapping("/listWithSettleInfo")
	public void listWithSettleInfo(PageParam<Object> pageParam, DispatchVO dispatch, Model model) {
		dispatch.setDispatched(true);
		dispatch.setDisabled(false);
		dispatch.setEffectiveFrom(new Date());
		dispatch.setEffectiveTo(new Date());
		pageParam.setModel(dispatch);
		pageParam.setTotal(dispatchProjectService.countBySelectivePageable(pageParam));
		List<DispatchVO> list = dispatchProjectService.selectDispatchVOWithAmountBySelectivePageable(pageParam);
//		for (DispatchVO dispatchVO : list) {
//			ProjectVO project = projectHeaderService.selectVOByProjectId(Integer.valueOf(dispatch.getProjectIds()));
//			if (null != project) {
//				dispatchVO.setCustomInfoByKey("projectProgress", (String) project.getCustomInfoByKey("projectProgress"));
//				dispatchVO.setCustomInfoByKey("projectStateName", project.getProjectStateName());
//			}
//		}
		model.addAttribute("data", list);
	}
	
	public boolean checkPermission(DispatchVO dispatch, Model model, String... permissions) {
		if (!super.checkPermission(dispatch, model, permissions)) {
			return false;
		}
		boolean isPermit = false;
		String permissionType = "";
		if (!UserContext.checkPermission("project:*") && dispatch != null) {
			Integer projectId = dispatch.getProjectId();
			String projectIds = dispatch.getProjectIds();
			ProjectVO project = new ProjectVO();
			project.setProjectId(projectId);
			project.setProjectIds(projectIds);
//			Map<String, Object> permission = projectHeaderService.checkPermissionMap(project, permissions);
//			PermissionResult checkPermit = new PermissionUtils(getDataName() + ":", new String[]{ROLE_ADMIN, ROLE_PM_ADMIN, ROLE_PM_SUB_ADMIN}).checkPermit(permission, permissions);
			PermissionResult projectPermit = projectHeaderService.checkPermission(project, permissions);
			String[] allPermitRoles = PermissionUtils.getRetainAllRoles(new String[] { ROLE_ADMIN, ROLE_PM_ADMIN, ROLE_PM_SUB_ADMIN }, projectPermit.getRoles());
			PermissionResult checkPermit = new PermissionUtils(getDataName() + ":", allPermitRoles)
					.checkPermit(projectPermit.getPermissionMap(), permissions);
			isPermit = checkPermit.isPermit();
			permissionType = checkPermit.getPermissionType();
//			model.addAttribute("permissions", checkPermit.getMap().getOrDefault("permissions", model.getAttribute("permissions")));
			model.addAllAttributes(checkPermit.getMap());
		} else {
			isPermit = true;
			permissionType = "all";
		}
		model.addAttribute("permissionType", permissionType);
		return isPermit;
	}
}