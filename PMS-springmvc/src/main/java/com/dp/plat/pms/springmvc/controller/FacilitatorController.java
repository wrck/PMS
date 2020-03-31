package com.dp.plat.pms.springmvc.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IFacilitatorService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.vo.FacilitatorVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "facilitator")
public class FacilitatorController extends BaseController {
	private final static String VIEW_NAMESPACE = "facilitator/";
	private final static String DATANAME_FORM = "facilitatorForm";
	private final static String DATANAME_TABLE = "facilitatorList";

	@Autowired
	private IFacilitatorService facilitatorService;

	@Autowired
	private IProjectHeaderService projectHeaderService;

	@RequestMapping
	public String home() {
		return VIEW_NAMESPACE + "list";
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, FacilitatorVO facilitator, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		// facilitator.setCompId(user.getCompId());
		PageParam<Object> tempParam = new PageParam<>();
		FacilitatorVO temp = new FacilitatorVO();
		// temp.setCompID(user.getCompId());
		tempParam.setModel(temp);
		pageParam.setModel(facilitator);

		pageParam.setTotal(facilitatorService.countBySelectivePageable(tempParam));
		pageParam.setFiltered(facilitatorService.countBySelectivePageable(pageParam));
		List<Object> list = facilitatorService.selectBySelectivePageable(pageParam);

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
			Facilitator facilitator = facilitatorService.selectByPrimaryKey(id);
			if (facilitator != null) {
				model.addAttribute("targetValue", facilitator);

				List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);
			}
		}
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping("/detail")
	public String create(String projectIds, Model model) {
		if (HttpContext.isJSON()) {
			String[] projectIdArr = StringUtils.split(StringUtils.trimToEmpty(projectIds), ",");
			for (String projectId : projectIdArr) {
				projectId = StringUtils.trimToNull(projectId);
				if (projectId != null) {
					ProjectHeader project = projectHeaderService.selectByPrimaryKey(Integer.valueOf(projectId));
				}
			}
			List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
		}
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(FacilitatorVO facilitator, Model model) {
		Boolean status = true;
		String message = null;
		try {
			facilitatorService.insertSelective(facilitator);
			model.addAttribute("targetName", "facilitatorVO");
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

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, FacilitatorVO facilitator, Model model) {
		Boolean status = true;
		String message = null;
		try {
			facilitatorService.updateByPrimaryKeySelective(facilitator);
			model.addAttribute("targetName", "facilitatorVO");
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
		Boolean status = true;
		String message = null;
		try {
			Facilitator facilitator = new Facilitator();
			facilitator.setId(id);
			facilitator.setEffectiveTo(new Date());
			facilitatorService.updateByPrimaryKeySelective(facilitator);
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