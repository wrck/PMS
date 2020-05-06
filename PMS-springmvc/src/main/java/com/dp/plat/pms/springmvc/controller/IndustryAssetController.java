package com.dp.plat.pms.springmvc.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryAssetProjectRelation;
import com.dp.plat.pms.springmvc.service.IIndustryAssetProjectRelationService;
import com.dp.plat.pms.springmvc.service.IIndustryAssetService;
import com.dp.plat.pms.springmvc.vo.IndustryAssetVO;

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

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			IndustryAssetVO member = new IndustryAssetVO();
			member.setId(id);
			member.setDisabled(true);
			service.updateByPrimaryKeySelective(member);
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