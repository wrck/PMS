package com.dp.plat.pms.springmvc.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.CommonRelatedData;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.vo.CommonRelatedDataVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "common/related")
public class CommonRelatedDataController
		extends AbstractController<ICommonRelatedDataService, CommonRelatedData, CommonRelatedDataVO> {

	@PostConstruct
	public void init() {
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
			return super.list(pageParam, relatedData, model);
		} finally {
			clearLocalVariables();
		}
	}

	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			CommonRelatedData v = service.selectByPrimaryKey(id);
			if (v != null) {
				model.addAttribute("targetValue", v);

				setLocalVariables("dataPrefix", v.getType());
				List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				List<?> navTavList = this.findNavTabList(getDataNameNavTab());
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
		if (HttpContext.isJSON()) {
			model.addAttribute("targetValue", v);

			setLocalVariables("dataPrefix", v.getType());
			List<Object> fieldList = this.findFieldList(getDataNameForm(), DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
			clearLocalVariables();
		} else {
			model.addAttribute("urlNamespace", URL_NAMESPACE);
			model.addAttribute("model", getViewModel());
			model.addAttribute("keyword", getKeyword());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
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
			CommonRelatedData relatedData = new CommonRelatedData();
			relatedData.setId(id);
			relatedData.setDisabled(true);
			relatedData.setEffectiveTo(new Date());
			service.updateByPrimaryKeySelective(relatedData);
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
			if (("project".equals(v.getObjType()) || "projectTask".equals(v.getObjType())) && !UserContext.checkPermission("project:*")) {
				ProjectVO project = new ProjectVO();
				project.setProjectId(v.getObjId());
				Map<String, Object> permission = SpringContext.getBean(IProjectHeaderService.class).checkPermissionMap(project, permissions);
				Boolean allPerm = Boolean.TRUE.equals(permission.get("all"));
				if (Boolean.TRUE.equals(allPerm)) {
					isPermit = true;
					permissionType = "all";
				} else {
					String perms = StringUtils.join(permissions, ",");
					Boolean editPerm = Boolean.TRUE.equals(permission.get("edit"));
					Boolean viewPerm = Boolean.TRUE.equals(permission.get("view"));
					if (editPerm && perms.matches(".*:(add|edit|delete)\\b,?.*")) {
						isPermit = true;
						permissionType = "edit";
					} else if ((viewPerm || editPerm) && perms.matches(".*:(list|detail)\\b,?.*")) {
						isPermit = true;
						permissionType = editPerm ? "edit" : "view";
					}
				}
				
				model.addAttribute("permissions", permission.getOrDefault("permissions", model.getAttribute("permissions")));
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