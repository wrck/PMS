package com.dp.plat.pms.springmvc.controller;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.ProjectMember;
import com.dp.plat.pms.springmvc.service.IProjectMemberService;
import com.dp.plat.pms.springmvc.vo.MemberVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "member")
public class ProjectMemberController extends AbstractController<IProjectMemberService, ProjectMember, MemberVO> {

	@PostConstruct
	public void init() {
		this.setViewModel("member");
		this.setUseTemplate(true);
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		Boolean status = true;
		String message = null;
		try {
			ProjectMember member = new ProjectMember();
			member.setId(id);
			member.setEffectiveTo(new Date());
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

}