package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserRole;
import com.dp.plat.core.service.IUserRoleService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.UserRoleInfo;

/**
 * 用户角色管理
 * 
 * @author w02611
 *
 */
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "userrole")
@Controller
public class UserRoleController {

	@Autowired
	private IUserRoleService userRoleService;

	@RequestMapping
	public String listView() {
		return Consts.URLPath.SYSTEM_MANAGER + "userrole_list";
	}

	@RequestMapping("/list")
	public String list(PageParam<UserRoleInfo> pageParam, UserRoleInfo userRole, Boolean isSelected, Model model) {
		userRole.setCompId(UserContext.getCurrentPrincipal().getCompId());
		pageParam.setModel(userRole);
		List<User> userList = new ArrayList<>();
		if (isSelected == null || isSelected) {
			pageParam.setTotal(userRoleService.countUserRoleSelected(pageParam));
			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
			userList = userRoleService.selectUserRoleSelected(pageParam);
		} else {
			// pageParam.setTotal(userRoleService.countUserRoleUnselected(null));
			pageParam.setTotal(userRoleService.countUserRoleUnselected(pageParam));
			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
			userList = userRoleService.selectUserRoleUnselected(pageParam);
		}
		model.addAttribute("data", userList);
		model.addAttribute("pageParam", pageParam);
		return Consts.URLPath.SYSTEM_MANAGER + "modals/userrole_select";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		UserRole userRole = userRoleService.selectByPrimaryKey(id);
		model.addAttribute("user", userRole);
		return Consts.URLPath.SYSTEM_MANAGER + "userrole_detail";
	}

	@RequestMapping("/detail")
	public String create() {
		return Consts.URLPath.SYSTEM_MANAGER + "userrole_detail";
	}

	@RequestMapping(value = "/bind", method = RequestMethod.POST)
	public String batchBind(String userRoleListStr) {
		List<UserRole> userRoleList = JSON.parseArray(userRoleListStr, UserRole.class);
		for (UserRole userRole : userRoleList) {
			userRole.setCompId(UserContext.getOrgId());
		}
		userRoleService.batchInsertUserRole(userRoleList);
		return Consts.URLPath.SYSTEM_MANAGER + "userrole_detail";
	}

	@RequestMapping(value = "/unbind", method = RequestMethod.DELETE)
	public String batchUnbind(String ids) {
		if (!StringUtils.isBlank(ids)) {
			userRoleService.batchDeleteUserRole(JSON.parseArray(ids, Integer.class));
		}
		return Consts.URLPath.SYSTEM_MANAGER + "userrole_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id) {
		userRoleService.deleteByPrimaryKey(id);
	}

}
