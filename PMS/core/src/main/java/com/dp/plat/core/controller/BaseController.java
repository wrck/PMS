package com.dp.plat.core.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.User;

/**
 * 基础功能管理
 * 
 * @author w02611
 *
 */
@RequestMapping("/base")
@Controller
public class BaseController {

	@RequestMapping("/getServerTime")
	@ResponseBody
	public Date getServerTime() {
		return new Date();
	}

	@RequestMapping("/avatar")
	public String avatar(User user) {
		return "/base/user_avatar";
	}

	@RequestMapping("/icon_selector")
	public String iconSelector(String iconName, Model model) {
		model.addAttribute("iconName", iconName);
		return "/base/icon_selector";
	}

	@RequestMapping("/userrole_selector")
	public String userRoleSelect(Integer roleId, Model model) {
		model.addAttribute("roleId", roleId);
		return "/base/userrole_select";
	}

	@RequestMapping("/role_detail")
	public String roleDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return "/base/role_detail";
	}

	@RequestMapping("/resource_detail")
	public String resourceDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return "/base/resource_detail";
	}

	@RequestMapping("modals/password")
	public String modifyPassword(Boolean needChangePwd, Model model) {
		model.addAttribute("needChangePwd", needChangePwd);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/modifyPassword";
	}

	@RequestMapping("/sysVariable_detail")
	public String sysVariableDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return "/base/sysVariable_detail";
	}

	@RequestMapping("/notifyTemplate_detail")
	public String notifyTemplateDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return "/base/notifyTemplate_detail";
	}

	/**
	 * 获取完成进度值
	 * 
	 * @param session
	 * @param model
	 */
	@RequestMapping("/progress/{progressName}")
	public void importObjectiveProgress(HttpSession session, @PathVariable("progressName") String progressName,
			Model model) {
		String progress;
		Object attribute = session.getAttribute(progressName);
		if (attribute == null) {
			progress = null;
		} else {
			progress = (String) attribute;
		}
		model.addAttribute("progress", progress);
	}
}
