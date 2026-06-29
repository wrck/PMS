package com.dp.plat.core.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.Role;
import com.dp.plat.core.service.IRoleService;
import com.dp.plat.core.vo.RoleParam;

/**
 * 角色管理
 * 
 * @author w02611
 *
 */

@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "role")
@Controller
public class RoleController {

	@Autowired
	private IRoleService roleService;

	@RequestMapping
	public String listView() {
		return Consts.URLPath.SYSTEM_MANAGER + "role_list";
	}

	@RequestMapping("/list")
	public String list(RoleParam pageParam, Model model) {
		pageParam.setTotal(roleService.countBySelective(null));
		pageParam.setFiltered(roleService.countBySelective(pageParam));
		List<Role> roleList = new ArrayList<>();
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		roleList = roleService.selectBySelective(pageParam);
		model.addAttribute("data", roleList);
		model.addAttribute("pageParam", pageParam);
		return Consts.URLPath.SYSTEM_MANAGER + "role_list";
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		Role role = roleService.selectByPrimaryKey(id);
		model.addAttribute("role", role);
		return Consts.URLPath.SYSTEM_MANAGER + "role_detail";
	}

	@RequestMapping("/detail")
	public String create() {
		return Consts.URLPath.SYSTEM_MANAGER + "modals/role_detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(Role role) {
		role.setCreateTime(new Date());
		roleService.insertSelective(role);
		return Consts.URLPath.SYSTEM_MANAGER + "role_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, Role role) {
		role.setUpdateTime(new Date());
		roleService.updateByPrimaryKeySelective(role);
		return Consts.URLPath.SYSTEM_MANAGER + "role_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id) {
		roleService.deleteByPrimaryKey(id);
	}
}
