package com.dp.plat.pms.springmvc.controller;

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
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryLeak;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.service.IIndustryLeakService;
import com.dp.plat.pms.springmvc.service.impl.IndustryLeakService;
import com.dp.plat.pms.springmvc.vo.IndustryLeakVO;

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



	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		this.setUseTemplate(false);
		this.setViewNameSpace("industry/leak/");
		Boolean status = true;
		String message = null;
		try {
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