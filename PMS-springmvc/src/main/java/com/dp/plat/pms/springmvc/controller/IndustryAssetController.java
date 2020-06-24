package com.dp.plat.pms.springmvc.controller;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;
import com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.AF_MANAGER + "/industry/asset")
public class IndustryAssetController extends AbstractController<IIndustryAssetService, IndustryAsset, IndustryAssetVO> {

	@Autowired
	private IIndustryAssetProjectRelationService industryAssetProjectRelationService;
	
	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.AF_MANAGER);
		this.setViewModel("industryAsset");
		this.setUseTemplate(true);
		this.setViewNameSpace("industry/asset/");
	}
	
	@Override
	public String home(Model model) {
		String view = super.home(model);
		return getViewNameSpace() + "list";
	}
	
	@Override
	@RequestMapping("/list")
	public String list(PageParam<Object> pageParam, IndustryAssetVO v, Model model) {
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
			IndustryAssetVO temp = new IndustryAssetVO();
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
	public String update(@PathVariable("id") Integer id, IndustryAssetVO v, Model model) {
		if (!checkPermission(v, model, getDataName() + ":edit")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		
		// 终止正在进行中的任务
		PmWorkFlowVO workflow = new PmWorkFlowVO();
		workflow.setDataId(id);
		workflow.setDataType(DataType.INDUSTRY_ASSET);
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
			workflow.setDataType(DataType.INDUSTRY_ASSET);
			workflow.setStatus(PmWorkFlowVO.PENDING);
			pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
			
			IndustryAssetVO v = new IndustryAssetVO();
			v.setId(id);
			v.setDisabled(true);
			service.updateByPrimaryKeySelective(v);
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
	public boolean checkPermission(IndustryAssetVO v, Model model, String... permissions) {
		return super.checkPermission(v, model, permissions);
	}

}