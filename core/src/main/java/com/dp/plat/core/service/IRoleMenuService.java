package com.dp.plat.core.service;

import java.util.List;

import com.dp.plat.core.pojo.RoleMenu;
import com.dp.plat.core.vo.RoleParam;
import com.dp.plat.core.vo.TreeNode;

public interface IRoleMenuService {
	int deleteByPrimaryKey(Integer id);

	int insert(RoleMenu roleMenu);

	int insertSelective(RoleMenu roleMenu);

	RoleMenu selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(RoleMenu roleMenu);

	int updateByPrimaryKey(RoleMenu roleMenu);

	// 以下自定义查询方法
	long countBySelective(RoleParam pageParam);

	List<RoleMenu> selectAllRole();

	/**
	 * 根据roleMenu参数，以及pageParam参数，有选择的查询所有角色信息
	 * 
	 * @param roleMenu
	 * @param pageParam
	 * @return roleMenuList
	 */
	@Deprecated
	List<RoleMenu> selectBySelective(RoleMenu roleMenu, RoleParam pageParam);

	/**
	 * @param pageParam
	 * @return roleMenuList
	 */
	List<RoleMenu> selectBySelective(RoleParam pageParam);

	/**
	 * 根据menuId 删除所有的role-menu关联关系
	 * @param menuId
	 * @return
	 */
	int deleteByMenuId(Integer menuId);

	/**
	 * 根据roleId 删除所有的role-menu关联关系
	 * @param roleId
	 * @return
	 */
	int deleteByRoleId(Integer roleId);
	
	/**
	 * 根据roleId 查询所有的role-menu关联关系
	 * @param roleId
	 * @return
	 */
	List<RoleMenu> selectByRoleId(Integer roleId);
	
	/**
	 * 根据RoleId 查询角色的菜单权限，返回所有菜单，带checked状态
	 * @param roleId
	 * @return
	 */
	List<TreeNode> queryMenuWithCheckStateByRoleId(Integer roleId);

	/**
	 * 批量插入role-menu关联关系
	 * @param roleId
	 * @param menuIds
	 */
	void batchInsertRoleMenu(Integer roleId, String[] menuIds);

}