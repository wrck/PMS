package com.dp.plat.pms.springmvc.controller;

import java.util.Date;
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
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
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

	@PostConstruct
	public void init() {
		this.setViewModel("dispatch");
		this.setUseTemplate(false);
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, DispatchVO dispatch, Model model) {
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
		return getViewNameSpace() + "list";
	}

	@RequestMapping(value = { "/{id}", "/modals/{id}" })
	public String findOne(@PathVariable("id") Integer id, Model model) {
		if (HttpContext.isJSON()) {
			DispatchProject dispatch = dispatchProjectService.selectByPrimaryKey(id);
			if (dispatch != null) {
				model.addAttribute("targetValue", dispatch);
				
				List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);

				String navDataName = DATANAME_NAVTAB;
				if (Boolean.TRUE.equals(dispatch.getDispatched())) {
					navDataName += "_dispatched";
				}
				List<?> navTavList = this.findNavTabList(navDataName);
				model.addAttribute("tabList", navTavList);
			}
		}  else {
			model.addAttribute("model", getViewModel());

			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = { "/detail", "/modals/detail" })
	public String detail(DispatchVO dispatch, Model model) {
		if (HttpContext.isJSON()) {
			String projectIds = dispatch.getProjectIds();
			String[] projectIdArr = StringUtils.split(StringUtils.trimToEmpty(projectIds), ",");
			for (String projectId : projectIdArr) {
				projectId = StringUtils.trimToNull(projectId);
				if (projectId != null) {
					ProjectHeader temp = projectHeaderService.selectByPrimaryKey(Integer.valueOf(projectId));
					ProjectVO project = new ProjectVO();
					BeanUtils.copyProperties(temp, project);
					project.setCustomInfo(temp.getCustomInfo());
					dispatch = new DispatchVO();
					dispatch.setProjectIds(project.getProjectId().toString());
					dispatch.setOfficeCode(project.getColumn001());
					dispatch.setDispatchName(project.getProjectName());
					dispatch.setSmsProjectAmount(project.getSmsProjectAmount());
					dispatch.setSmsProjectCode(project.getSmsProjectCode());
					dispatch.setSmsSubmitTime(project.getSmsSubmitTime());
					dispatch.setContractNos(project.getContractNo());
					dispatch.setCustomInfoByKey("smsOrderExecNumber", project.getSmsOrderExecNumber());
					model.addAttribute("targetValue", dispatch);
				}
			}
			List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
		} else {
			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(DispatchVO dispatch, Model model) {
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
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, DispatchVO dispatch, Model model) {
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
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
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
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	@RequestMapping(value = "submit", method = RequestMethod.POST)
	public void dispatchSubmit(DispatchVO dispatch, Model model) {
		Boolean status = true;
		String message = null;
		try {
			dispatchProjectService.insertOrUpdateSelective(dispatch);
			dispatchProjectService.dispatchSubmit(dispatch.getId(), dispatch);
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
	
	/**
	 * 根据服务商编号生成派单编号
	 * @param facilitatorCode
	 * @param model
	 */
	@RequestMapping("/generateDispatchSeq")
	public void generateDispatchSeq(String facilitatorCode, Model model) {
		String dispatchSeq = dispatchProjectService.generateDispatchSeq(facilitatorCode);
		model.addAttribute("dispatchSeq", dispatchSeq);
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
		model.addAttribute("data", list);
	}
}