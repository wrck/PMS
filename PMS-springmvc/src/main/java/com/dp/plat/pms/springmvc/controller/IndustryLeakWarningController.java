package com.dp.plat.pms.springmvc.controller;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.IndustryLeakWarning;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakWarningService;
import com.dp.plat.pms.springmvc.vo.LeakWarningVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.AF_MANAGER + "/industry/warning")
public class IndustryLeakWarningController extends AbstractController<IIndustryLeakWarningService, IndustryLeakWarning, IndustryLeakWarning> {

	@Autowired
	private IIndustryLeakService industryLeakService;
	
	@Autowired
	private IIndustryAssetService industryAssetService;
	
	@PostConstruct
	public void init() {
		this.setUrlNameSpace(ProjectConstant.URLPath.AF_MANAGER);
		this.setViewModel("industryWarning");
		this.setUseTemplate(true);
		this.setViewNameSpace("industry/warning");
	}
	

	@Override
	public String home(Model model) {
		String view = super.home(model);
		return view;
	}



	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			IndustryLeakWarning vo = new IndustryLeakWarning();
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

	@RequestMapping(value = {"/asset", "/asset/list"})
	public String warningAsset(PageParam<Object> pageParam, LeakWarningVO v, Model model) {
		if (HttpContext.isJSON()) {
			List<Object> list = Collections.emptyList();
			try {
				// Principal user = UserContext.getCurrentPrincipal();
				// v.setCompId(user.getCompId());
				PageParam<Object> tempParam = new PageParam<>();
				LeakWarningVO temp = new LeakWarningVO();
				// temp.setCompID(user.getCompId());
				tempParam.setModel(temp);
				pageParam.setModel(v);
				
				pageParam.setTotal(service.countWarningAssetBySelectivePageable(tempParam));
				pageParam.setFiltered(service.countWarningAssetBySelectivePageable(pageParam));
				list = service.selectWarningAssetBySelectivePageable(pageParam);
				
				if (pageParam.getPageSize() == -1L) {
					pageParam.setPageSize(pageParam.getTotal());
				}
			} catch (Exception e) {
				ExceptionHandler.insertException(e);
			}
			model.addAttribute("data", list);
			List<DataTableColumn> columns = this.findColumnList("industryWarningAssetList");
			pageParam.setColumns(columns);
			pageParam.setRowId("assetId");
		} else {
			model.addAttribute("urlNamespace", URL_NAMESPACE);
			model.addAttribute("model", "industryWarningAsset");
			model.addAttribute("keyword", getKeyword());
		}
		return getRealViewNameSpace() + "list";
	}
	
}