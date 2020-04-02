package com.dp.plat.pms.springmvc.controller;

import java.util.List;

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
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "settlement")
public class DispatchSettlementController extends BaseController {
	private final static String VIEW_NAMESPACE = "settlement/";
	private final static String DATANAME_FORM = "settlementForm";
	private final static String DATANAME_TABLE = "settlementList";

	@Autowired
	private IDispatchProjectService dispatchProjectService;
	
	@Autowired
	private IDispatchSettlementService dispatchSettlementService;

	@Autowired
	private IProjectHeaderService projectHeaderService;

	@RequestMapping
	public String home() {
		return VIEW_NAMESPACE + "list";
	}

	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, SettlementVO settlement, Model model) {
		Principal user = UserContext.getCurrentPrincipal();
		settlement.setDisabled(false);
		// settlement.setCompId(user.getCompId());
		PageParam<Object> tempParam = new PageParam<>();
		SettlementVO temp = new SettlementVO();
		temp.setDisabled(false);
		// temp.setCompID(user.getCompId());
		tempParam.setModel(temp);
		pageParam.setModel(settlement);

		pageParam.setTotal(dispatchSettlementService.countBySelectivePageable(tempParam));
		pageParam.setFiltered(dispatchSettlementService.countBySelectivePageable(pageParam));
		List<Object> list = dispatchSettlementService.selectBySelectivePageable(pageParam);

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
			DispatchSettlement settlement = dispatchSettlementService.selectByPrimaryKey(id);
			if (settlement != null) {
				model.addAttribute("targetValue", settlement);

				List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
				model.addAttribute("fieldList", fieldList);
			}
		}
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping(value= {"/detail", "/modals/detail"})
	public String create(Integer dispatchId, Model model) {
		if (HttpContext.isJSON()) {
			if (dispatchId != null) {
				DispatchProject dispatch = dispatchProjectService.selectByPrimaryKey(dispatchId);
				SettlementVO settlement = new SettlementVO();
				settlement.setDispatchId(dispatch.getId());
				settlement.setDispatchSeq(dispatch.getDispatchSeq());
				settlement.setSmsProjectCode(dispatch.getSmsProjectCode());
				settlement.setSmsProjectName(dispatch.getDispatchName());
				settlement.setSmsSubmitTime(dispatch.getSmsSubmitTime());
				settlement.setSmsProjectAmount(dispatch.getSmsProjectAmount());
				model.addAttribute("targetValue", settlement);
			}
			List<Object> fieldList = this.findFieldList(DATANAME_FORM, DATATYPE_FORM);
			model.addAttribute("fieldList", fieldList);
		} else {
			String servletPath = HttpContext.getCurrentRequest().getServletPath();
			model.addAttribute("isModals", servletPath.contains("/modals/"));
		}
		return VIEW_NAMESPACE + "detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(SettlementVO settlement, Model model) {
		Boolean status = true;
		String message = null;
		try {
			dispatchSettlementService.insertSelective(settlement);
			model.addAttribute("targetName", "settlementVO");
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
	public String update(@PathVariable("id") Integer id, SettlementVO settlement, Model model) {
		Boolean status = true;
		String message = null;
		try {
			dispatchSettlementService.updateByPrimaryKeySelective(settlement);
			model.addAttribute("targetName", "settlementVO");
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