package com.dp.plat.core.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.MenuMapper;
import com.dp.plat.core.dao.RoleMenuMapper;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.RoleMenu;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IRoleMenuService;
import com.dp.plat.core.util.MenuUtil;
import com.dp.plat.core.vo.RoleParam;
import com.dp.plat.core.vo.TreeNode;
import com.dp.plat.core.vo.TreeNodeState;

@Service("roleMenuService")
public class RoleMenuService implements IRoleMenuService {

	@Resource
	private RoleMenuMapper roleMenuMapper;

	@Resource
	private MenuMapper menuMapper;
	@Override
	public int updateByPrimaryKeySelective(RoleMenu roleMenu) {
		return roleMenuMapper.updateByPrimaryKeySelective(roleMenu);
	}

	@Override
	public int deleteByPrimaryKey(Integer userId) {
		return roleMenuMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(RoleMenu record) {
		return roleMenuMapper.insert(record);
	}

	@Override
	public int insertSelective(RoleMenu record) {
		return roleMenuMapper.insertSelective(record);
	}

	public RoleMenu selectByPrimaryKey(Integer userId) {
		return roleMenuMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKey(RoleMenu record) {
		return roleMenuMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteByMenuId(Integer menuId) {
		return roleMenuMapper.deleteByMenuId(menuId);
	}

	@Override
	public long countBySelective(RoleParam pageParam) {
		return roleMenuMapper.countBySelective(pageParam);
	}

	@Override
	public List<RoleMenu> selectAllRole() {
		return roleMenuMapper.selectAllRole();
	}

	@Override
	public List<RoleMenu> selectBySelective(RoleMenu roleMenu, RoleParam pageParam) {
		return roleMenuMapper.selectBySelective(roleMenu, pageParam);
	}

	@Override
	public List<RoleMenu> selectBySelective(RoleParam pageParam) {
		return roleMenuMapper.selectBySelective(pageParam);
	}

	@Override
	public List<TreeNode> queryMenuWithCheckStateByRoleId(Integer roleId){
		List<RoleMenu> roleMenus = roleMenuMapper.selectByRoleId(roleId);
		HashMap<Integer, TreeNodeState> stateMap = new HashMap<Integer, TreeNodeState>();
		for (RoleMenu roleMenu : roleMenus) {
			// 存在role-menu关系将treenode state 改为 checked：true
			TreeNodeState state = new TreeNodeState(true,false,false,false);
			stateMap.put(roleMenu.getMenuId(), state);
		}
		List<Menu> menus =  menuMapper.selectBySelective(null);
		return MenuUtil.constructTreeNodeData(menus, stateMap);
	}

	@Override
	public int deleteByRoleId(Integer roleId) {
		return roleMenuMapper.deleteByRoleId(roleId);
	}
	
	@Override
	public List<RoleMenu> selectByRoleId(Integer roleId) {
		return roleMenuMapper.selectByRoleId(roleId);
	}

	@Override
	public void batchInsertRoleMenu(Integer roleId, String[] menuIds) {
		HashMap<String, Object> params = new HashMap<>();
		Principal currentUser= (Principal) SecurityUtils.getSubject().getPrincipal();
		params.put("roleId", roleId);
		params.put("menuIds", menuIds);
		params.put("createBy", currentUser.getUserName());
		roleMenuMapper.batchInsertRoleMenu(params);
	}
	
}
