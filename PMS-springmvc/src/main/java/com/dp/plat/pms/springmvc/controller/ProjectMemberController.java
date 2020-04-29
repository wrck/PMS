package com.dp.plat.pms.springmvc.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.entity.ProjectMember;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectMemberService;
import com.dp.plat.pms.springmvc.vo.MemberVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.util.MessageUtil;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "member")
public class ProjectMemberController extends AbstractController<IProjectMemberService, ProjectMember, MemberVO> {

	@Autowired
	private IProjectHeaderService projectHeaderService;
	
	@PostConstruct
	public void init() {
		this.setViewModel("projectMember");
		this.setUseTemplate(true);
	}
	
	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, MemberVO v, Model model) {
		String view = super.update(id, v, model);
		
		if (v.getEffectiveTo() == null || v.getEffectiveTo().after(new Date())) {
			if (MessageUtil.MEMBER_PM.equals(v.getMemberRole())) {
				ProjectVO project = new ProjectVO();
				project.setProjectId(v.getProjectId());
				project.setCustomInfoByKey("programManagerCode", v.getMemberCode());
				project.setCustomInfoByKey("programManagerCodejson", v.getMemberName());
				projectHeaderService.updateByPrimaryKeySelective(project);
			}
		}
		return view;
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

	@Override
	public boolean checkPermission(MemberVO v, Model model, String... permissions) {
		if (!super.checkPermission(v, model, permissions)) {
			return false;
		}
		boolean isPermit = false;
		String permissionType = "";
		if (!UserContext.checkPermission("project:*") && v != null) {
			ProjectVO project = new ProjectVO();
			project.setProjectId(v.getProjectId());
			Map<String, Boolean> permission = projectHeaderService.checkPermission(project);
			Boolean allPerm = permission.get("all");
			if (Boolean.TRUE.equals(allPerm)) {
				isPermit = true;
				permissionType = "all";
			} else {
				String perms = StringUtils.join(permissions, ",");
				if (Boolean.TRUE.equals(permission.get("edit")) && perms.matches(".*projectMember:(list|detail)\\b,?.*")) {
					isPermit = true;
					permissionType = "edit";
				}
				if (Boolean.TRUE.equals(permission.get("view")) && perms.matches(".*projectMember:(list|detail)\\b,?.*")) {
					isPermit = true;
					permissionType = "view";
				}
			}
		} else {
			isPermit = true;
			permissionType = "all";
		}
		model.addAttribute("permissionType", permissionType);
		return isPermit;
	}

}