package com.dp.plat.pms.springmvc.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.service.impl.IndustryLeakService;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.AF_MANAGER + "/industry/leak")
public class IndustryLeakController extends AbstractController<IIndustryLeakService, IndustryLeak, IndustryLeakVO> {

	@Autowired
	private IIndustryLeakService industryLeakService;
	
	@Autowired
	private IIndustryAssetService industryAssetService;
	
	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.AF_MANAGER);
		this.setViewModel("industryLeak");
		this.setUseTemplate(true);
		this.setViewNameSpace("industry/leak/");
	}
	

	@Override
	public String home(Model model) {
		String view = super.home(model);
		return getViewNameSpace() + "list";
	}
	
	
	@Override
	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, IndustryLeakVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":list")) {
			model.addAttribute("data", Collections.emptyList());
			return Consts.VIEW_UNAUTHORIZED;
		}
		List<Object> list = Collections.emptyList();
		try {
			// Principal user = UserContext.getCurrentPrincipal();
			// v.setCompId(user.getCompId());
			v.setDisabled(false);
			PageParam<Object> tempParam = new PageParam<>();
			IndustryLeakVO temp = new IndustryLeakVO();
			// temp.setCompID(user.getCompId());
			temp.setDisabled(false);
			tempParam.setModel(temp);
			pageParam.setModel(v);

			pageParam.setTotal(service.countBySelectivePageable(tempParam));
			pageParam.setFiltered(service.countBySelectivePageable(pageParam));
			list = service.selectBySelectivePageable(pageParam);

			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		model.addAttribute("data", list);

		List<DataTableColumn> columns = this.findColumnList(getDataNameTable());
		pageParam.setColumns(columns);
		return getRealViewNameSpace() + "list";
	}


	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, IndustryLeakVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":delete")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		
		// 终止正在进行中的任务
		PmWorkFlowVO workflow = new PmWorkFlowVO();
		workflow.setDataId(id);
		workflow.setDataType(DataType.INDUSTRY_LEAK);
		workflow.setStatus(PmWorkFlowVO.PENDING);
		pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");

		v.setStatus("0");
		v.setTrackStatus(0);
		return super.update(id, v, model);
	}


	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(null, model, getDataName() + ":delete")) {
			return;
		}
		Boolean status = true;
		String message = null;
		try {
			// 终止正在进行中的任务
			PmWorkFlowVO workflow = new PmWorkFlowVO();
			workflow.setDataId(id);
			workflow.setDataType(DataType.INDUSTRY_LEAK);
			workflow.setStatus(PmWorkFlowVO.PENDING);
			pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
						
			IndustryLeakVO vo = new IndustryLeakVO();
			vo.setId(id);
			vo.setDisabled(true);
			service.updateByPrimaryKeySelective(vo);
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
	public boolean checkPermission(IndustryLeakVO v, Model model, String... permissions) {
		return super.checkPermission(v, model, permissions);
	}

	
}