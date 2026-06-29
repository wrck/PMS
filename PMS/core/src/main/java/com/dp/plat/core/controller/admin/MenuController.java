package com.dp.plat.core.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.service.IMenuService;
import com.dp.plat.core.service.IRoleMenuService;
import com.dp.plat.core.vo.TreeNode;

/**
 * 菜单管理
 * 
 * @author w02611
 *
 */
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "menu")
@Controller
public class MenuController {

	@Autowired
	private IMenuService menuService;

	@Autowired
	private IRoleMenuService roleMenuService;

	@RequestMapping
	public void listView() {
	}

	@RequestMapping("/getTreeData")
	@ResponseBody
	public List<TreeNode> getTreeData() {
		return menuService.getTreeData();
	}

	@RequestMapping("{id}")
	public String findOne(@PathVariable("id") Integer id, Model model) {
		Menu menu = menuService.selectByPrimaryKey(id);
		if (!StringUtils.isEmpty(menu.getPid()) && menu.getPid() != 0) {
			menu.setParentName(menuService.selectByPrimaryKey(menu.getPid()).getName());
		}
		model.addAttribute(menu);
		return Consts.URLPath.SYSTEM_MANAGER + "menu_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public String update(@PathVariable("id") Integer id, Menu menu, Model model) {
		menuService.updateByPrimaryKeySelective(menu);
		return Consts.URLPath.SYSTEM_MANAGER + "menu_detail";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(Menu menu, Model model) {
		menuService.insertSelective(menu);
		return Consts.URLPath.SYSTEM_MANAGER + "menu_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("id") Integer id, Model model) {
		menuService.deleteByPrimaryKey(id);
		roleMenuService.deleteByMenuId(id);
		return Consts.URLPath.SYSTEM_MANAGER + "menu_detail";
	}
}
