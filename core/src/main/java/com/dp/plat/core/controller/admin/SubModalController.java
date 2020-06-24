package com.dp.plat.core.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.User;

/**
 * 系统模态框页面控制器
 * 
 * @author w02611
 *
 */
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "modals")
@Controller
public class SubModalController {

	@RequestMapping("/avatar")
	public String avatar(User user) {
		return Consts.URLPath.SYSTEM_MANAGER + "modals/user_avatar";
	}

	@RequestMapping("/icon_selector")
	public String iconSelector(String iconName, Model model) {
		model.addAttribute("iconName", iconName);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/icon_selector";
	}

	@RequestMapping("/userrole_selector")
	public String userRoleSelect(Integer roleId, Model model) {
		model.addAttribute("roleId", roleId);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/userrole_select";
	}

	@RequestMapping("/role_detail")
	public String roleDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/role_detail";
	}

	@RequestMapping("/resource_detail")
	public String resourceDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/resource_detail";
	}
	
	@RequestMapping("/password")
	public String modifyPassword(Boolean needChangePwd, Model model) {
		model.addAttribute("needChangePwd", needChangePwd);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/modifyPassword";
	}
	
	@RequestMapping("/sysVariable_detail")
	public String sysVariableDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/sysVariable_detail";
	}
	
	@RequestMapping("/notifyTemplate_detail")
	public String notifyTemplateDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/notifyTemplate_detail";
	}
	
	@RequestMapping("/dataOperation_detail")
	public String dataOpeartionDetail(Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/data_operation_detail";
	}
}
