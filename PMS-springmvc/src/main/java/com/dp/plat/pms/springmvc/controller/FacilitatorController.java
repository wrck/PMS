package com.dp.plat.pms.springmvc.controller;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.URLPath;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import com.dp.plat.pms.springmvc.vo.FacilitatorVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "facilitator")
public class FacilitatorController extends AbstractController<IAbstractBaseService<Facilitator>, Facilitator, FacilitatorVO> {

	@PostConstruct
	public void init() {
		this.setUrlNameSpace(URLPath.PROJECT_MANAGER);
		this.setViewModel("facilitator");
		this.setUseTemplate(true);
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			Facilitator facilitator = new Facilitator();
			facilitator.setId(id);
			facilitator.setEffectiveTo(new Date());
			service.updateByPrimaryKeySelective(facilitator);
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
	public boolean checkPermission(FacilitatorVO v, Model model, String... permissions) {
		return true;
//		return super.checkPermission(v, model, permissions);
	}
	
}