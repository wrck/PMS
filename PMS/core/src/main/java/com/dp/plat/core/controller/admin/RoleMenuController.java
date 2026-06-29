package com.dp.plat.core.controller.admin;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IRoleMenuService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.PasswordUtil;

/**
 * 角色菜单管理
 * 
 * @author w02611
 *
 */

@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "rolemenu")
@Controller
public class RoleMenuController {

	@Autowired
	private IUserService userService;
	@Autowired
	private IUserInfoService userInfoService;

	@Autowired
	private IRoleMenuService roleMenuService;

	@RequestMapping
	public String listView() {

		return Consts.URLPath.SYSTEM_MANAGER + "rolemenu_list";
	}

	@RequestMapping("/list")
	public String list(Integer roleId, Model model) {
		model.addAttribute("data", roleMenuService.queryMenuWithCheckStateByRoleId(roleId));
		return null;
	}

	@RequestMapping(value = "/updateRoleMenu", method = RequestMethod.POST)
	public String updateRoleMenu(Integer roleId, String menuIds) {
		roleMenuService.deleteByRoleId(roleId);
		if (StringUtils.isNotBlank(menuIds)) {
			String[] menuIdArray = menuIds.split(",");
			roleMenuService.batchInsertRoleMenu(roleId, menuIdArray);
		}
		return null;
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		User user = userService.selectByPrimaryKey(id);
//		UserInfo userInfo = userInfoService.selectByUserId(id);
		UserInfo userInfo = userInfoService.selectOneByUserIdAndCompId(UserContext.getCurrentPrincipal().getUserInfo());
		model.addAttribute("user", user);
		model.addAttribute("userInfo", userInfo);
		return Consts.URLPath.SYSTEM_MANAGER + "user_detail";
	}

	@RequestMapping("/detail")
	public String create() {
		return Consts.URLPath.SYSTEM_MANAGER + "user_detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(User user, UserInfo userInfo) {
		user.setCreateTime(new Date());
		user.setPassword(PasswordUtil.encryptPassword(user.getUserName(), "123456"));
		userService.insertSelective(user);
		userInfo.setUserId(user.getUserId());
		userInfoService.insertSelective(userInfo);
		return "redirect:" + Consts.URLPath.SYSTEM_MANAGER + "user/" + user.getUserId() + ".json";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, User user, UserInfo userInfo) {
		user.setUpdateTime(new Date());
		userService.updateByPrimaryKeySelective(user);
		UserInfo info = userInfoService.selectByUserId(id);
		if (info == null) {
			userInfoService.insertSelective(userInfo);
		} else {
			userInfoService.updateByUserId(userInfo);
		}
		return "redirect:" + Consts.URLPath.SYSTEM_MANAGER + "user/" + id + ".json";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id) {
		userService.deleteByPrimaryKey(id);
		userInfoService.deleteByUserId(id);
	}
}
