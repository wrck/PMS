package com.dp.plat.pms.springmvc.controller;

import java.util.Date;
import java.util.List;

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
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "dispatch")
public class DispatchProjectController extends BaseController {
	private final static String VIEW_NAMESPACE = "dispatch/";
	private final static String DATANAME_FORM = "dispatchForm";
	private final static String DATANAME_TABLE = "dispatchList";
	private static final String DATANAME_NAVTAB = null;

	@Autowired
	private IDispatchProjectService dispatchPojectService;

	@Autowired
	private IProjectHeaderService projectHeaderService;

	@RequestMapping
	public String home() {
		return VIEW_NAMESPACE + "list";
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, DispatchVO dispatch, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		dispatch.setDisabled(false);
		// dispatch.setCompId(user.getCompId());
		PageParam<Object> tempParam = new PageParam<>();
		DispatchVO temp = new DispatchVO();
		temp.setDisabled(false);
		// temp.setCompID(user.getCompId());
		tempParam.setModel(temp);
		pageParam.setModel(dispatch);

		pageParam.setTotal(dispatchPojectService.countBySelectivePageable(tempParam));
		pageParam.setFiltered(dispatchPojectService.countBySelectivePageable(pageParam));
		List<Object> list = dispatchPojectService.selectBySelectivePageable(pageParam);

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
			DispatchProject dispatch = dispatchPojectService.selectByPrimaryKey(id);
			if (dispatch != null) {
				model.addAttribute("targetValue", dispatch);

				List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				List<?> navTavList = this.findNavTabList(DATANAME_NAVTAB);
				model.addAttribute("tabList", navTavList);
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
					ProjectHeader temp = projectHeaderService.selectByPrimaryKey(Integer.valueOf(projectId));
					ProjectVO project = new ProjectVO();
					BeanUtils.copyProperties(temp, project);
					DispatchProject dispatch = new DispatchProject();
					dispatch.setProjectIds(project.getProjectId().toString());
					dispatch.setDispatchName(project.getProjectName());
					dispatch.setSmsProjectAmount(project.getSmsProjectAmount());
					dispatch.setSmsProjectCode(project.getSmsProjectCode());
					dispatch.setSmsSubmitTime(project.getSmsSubmitTime());
					dispatch.setContractNos(project.getContractNo());
					model.addAttribute("targetValue", dispatch);
				}
			}
			List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
		}
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(DispatchVO dispatch, Model model) {
		Boolean status = true;
		String message = null;
		try {
			dispatchPojectService.insertSelective(dispatch);
			model.addAttribute("targetName", "dispatchVO");
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
	public String update(@PathVariable("id") Integer id, DispatchVO dispatch, Model model) {
		Boolean status = true;
		String message = null;
		try {
			dispatchPojectService.updateByPrimaryKeySelective(dispatch);
			model.addAttribute("targetName", "dispatchVO");
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
			DispatchProject dispatch = new DispatchProject();
			dispatch.setId(id);
			dispatch.setDisabled(true);
			dispatch.setEffectiveTo(new Date());
			dispatchPojectService.updateByPrimaryKeySelective(dispatch);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	@RequestMapping(value = "submit", method = RequestMethod.POST)
	public void dispatchSubmit(DispatchVO dispatch, Model model) {
		Boolean status = true;
		String message = null;
		try {
			dispatchPojectService.insertOrUpdateSelective(dispatch);
			dispatchPojectService.dispatchSubmit(dispatch.getId(), dispatch);
			model.addAttribute("targetName", "dispatchVO");
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	@RequestMapping("/modals/payment")
	public void dispatchPayment(Integer id, Model model) {
	}
}