package com.dp.plat.pms.springmvc.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.CommonRelatedData;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.vo.CommonRelatedDataVO;

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
			setLocalVariables("dataPrefix", relatedData.getType() + "_");
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

				setLocalVariables("dataPrefix", v.getType() + "_");
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
			
			setLocalVariables("dataPrefix", v.getType() + "_");
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

}