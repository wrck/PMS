package com.dp.plat.pms.springmvc.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.RoleConstant;
import com.dp.plat.pms.springmvc.entity.CommonRelatedData;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectTaskService;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import com.dp.plat.pms.springmvc.vo.CommonRelatedDataVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.pms.springmvc.vo.TaskVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "common/related")
public class CommonRelatedDataController
		extends AbstractController<ICommonRelatedDataService, CommonRelatedData, CommonRelatedDataVO> {

	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.PROJECT_MANAGER);
		this.setViewModel("commonRelated");
		this.setUseTemplate(true);
	}
	
	

	@Override
	public String list(PageParam<Object> pageParam, CommonRelatedDataVO relatedData, Model model) {
		try {
			if (relatedData.getObjId() == null || StringUtils.isBlank(relatedData.getType())) {
				model.addAttribute("data", Collections.emptyList());
				return "unauthorized";
			}
			setLocalVariables("dataPrefix", relatedData.getType());
			relatedData.setDisabled(false);
			model.addAttribute("model", relatedData.getType());
			return super.list(pageParam, relatedData, model);
		} finally {
			clearLocalVariables();
		}
	}

	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			CommonRelatedData relatedData = service.selectByPrimaryKey(id);
			if (relatedData != null) {
				CommonRelatedDataVO v = new CommonRelatedDataVO();
				BeanUtils.copyProperties(relatedData, v);
				
				setLocalVariables("dataPrefix", v.getType());
				if (!checkPermission(v, model, getDataName() + ":detail")) {
					model.addAttribute("status", false);
					model.addAttribute("message", "没有权限进行该操作！");
					return Consts.VIEW_UNAUTHORIZED;
				}
				model.addAttribute("targetValue", v);

				List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				List<?> navTavList = this.findNavTabList(getDataNameNavTab(), model);
				model.addAttribute("tabList", navTavList);
				clearLocalVariables();
			}
		} else {
			model.addAttribute("urlNamespace", URL_NAMESPACE);
			model.addAttribute("model", getViewModel());
			model.addAttribute("keyword", getKeyword());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = { "/detail", "/modals/detail" })
	public String detail(CommonRelatedDataVO v, Model model) {
		try {
			setLocalVariables("dataPrefix", v.getType());
			if (!checkPermission(v, model, getDataName() + ":detail")) {
				model.addAttribute("status", false);
				model.addAttribute("message", "没有权限进行该操作！");
				return Consts.VIEW_UNAUTHORIZED;
			}
			if (HttpContext.isJSON()) {
				model.addAttribute("targetValue", v);

				List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);
			} else {
				model.addAttribute("urlNamespace", URL_NAMESPACE);
				model.addAttribute("model", getViewModel());
				model.addAttribute("keyword", getKeyword());

				String servletPath = HttpContext.getCurrentRequest().getServletPath();
				model.addAttribute("isModals", servletPath.contains("/modals/"));
			}
		} finally {
			clearLocalVariables();
		}
		return getRealViewNameSpace() + "detail";
	}

	@PostMapping(value = "/detail")
	public String create(CommonRelatedDataVO v, Model model) {
		try {
			setLocalVariables("dataPrefix", v.getType());
			String objType = v.getObjType();
			Integer objId = v.getObjId();
			if ("project".equalsIgnoreCase(objType) && objId != null) {
				IProjectHeaderService projectHeaderService = SpringContext.getBean(IProjectHeaderService.class);
				ProjectHeader project = projectHeaderService.selectByPrimaryKey(objId);
				v.setField1(project.getProjectName());
				v.setField2(project.getColumn003());
				v.setCustomInfoByKey("project", project);
			}
			return super.create(v, model);
		} finally {
			clearLocalVariables();
		}
	}

	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, CommonRelatedDataVO v, Model model) {
		try {
			setLocalVariables("dataPrefix", v.getType());
			String objType = v.getObjType();
			Integer objId = v.getObjId();
			if ("project".equalsIgnoreCase(objType) && objId != null) {
				IProjectHeaderService projectHeaderService = SpringContext.getBean(IProjectHeaderService.class);
				ProjectHeader project = projectHeaderService.selectByPrimaryKey(objId);
				v.setField1(project.getProjectName());
				v.setField2(project.getColumn003());
				v.setCustomInfoByKey("project", project);
			}
			return super.update(id, v, model);
		} finally {
			clearLocalVariables();
		}
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			CommonRelatedData relatedData = service.selectByPrimaryKey(id);
			if (relatedData != null) {
				CommonRelatedDataVO v = new CommonRelatedDataVO();
				BeanUtils.copyProperties(relatedData, v);
				
				setLocalVariables("dataPrefix", v.getType());
				if (!checkPermission(v, model, getDataName() + ":detail")) {
					model.addAttribute("status", false);
					model.addAttribute("message", "没有权限进行该操作！");
					return;
				}
				relatedData.setId(id);
				relatedData.setDisabled(true);
				relatedData.setEffectiveTo(new Date());
				service.updateByPrimaryKeySelective(relatedData);
				clearLocalVariables();
			}
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
	public boolean checkPermission(CommonRelatedDataVO v, Model model, String... permissions) {
		if (!super.checkPermission(v, model, permissions)) {
			return false;
		}
		boolean isPermit = false;
		String permissionType = "";
		if (v != null) {
			if (("project".equals(v.getObjType()) || "projectTask".equals(v.getObjType()))
					&& !UserContext.checkPermission("project:*")) {
				Map<String, Object> permission = null;
				if ("project".equals(v.getObjType())) {
					ProjectVO project = new ProjectVO();
					project.setProjectId(v.getObjId());
					permission = SpringContext.getBean(IProjectHeaderService.class).checkPermissionMap(project,
							permissions);
				} else if ("projectTask".equals(v.getObjType())) {
					TaskVO task = new TaskVO();
					task.setTaskId(v.getObjId());
					permission = SpringContext.getBean(IProjectTaskService.class).checkPermissionMap(task, permissions);
				}
				// Boolean allPerm = Boolean.TRUE.equals(permission.get("all"));
				// if (Boolean.TRUE.equals(allPerm)) {
				// isPermit = true;
				// permissionType = "all";
				// } else {
				// String perms = StringUtils.join(permissions, ",");
				// Boolean editPerm =
				// Boolean.TRUE.equals(permission.get("edit"));
				// Boolean viewPerm =
				// Boolean.TRUE.equals(permission.get("view"));
				// if (editPerm && perms.matches(".*:(add|edit|delete)\\b,?.*"))
				// {
				// isPermit = true;
				// permissionType = "edit";
				// } else if ((viewPerm || editPerm) &&
				// perms.matches(".*:(list|detail)\\b,?.*")) {
				// isPermit = true;
				// permissionType = editPerm ? "edit" : "view";
				// }
				// }
				PermissionResult checkPermit = new PermissionUtils(getDataName() + ":",
						new String[] { RoleConstant.ROLE_PM_ADMIN, RoleConstant.ROLE_ADMIN, RoleConstant.ROLE_PM_SUB_ADMIN,
								RoleConstant.ROLE_PM_AREA_MANAGER }).checkPermit(permission, permissions);
				isPermit = checkPermit.isPermit();
				permissionType = checkPermit.getPermissionType();
				model.addAttribute("permissions", checkPermit.getMap().getOrDefault("permissions", model.getAttribute("permissions")));
			} else {
				isPermit = true;
				permissionType = "all";
			}
		} else {
			isPermit = true;
			permissionType = "all";
		}
		model.addAttribute("permissionType", permissionType);
		return isPermit;
	}

}