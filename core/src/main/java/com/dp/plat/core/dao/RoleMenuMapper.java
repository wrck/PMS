package com.dp.plat.core.dao;

import java.util.HashMap;
import java.util.List;

import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.RoleMenu;
import com.dp.plat.core.vo.RoleParam;

public interface RoleMenuMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(RoleMenu record);

	int insertSelective(RoleMenu record);

	RoleMenu selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(RoleMenu record);

	int updateByPrimaryKey(RoleMenu record);

	/**
	 * 根据menuId 删除所有的role-menu关联关系
	 * 
	 * @param menuId
	 * @return
	 */
	int deleteByMenuId(Integer menuId);

	/**
	 * 根据roleId 删除所有的role-menu关联关系
	 * 
	 * @param roleId
	 * @return
	 */
	int deleteByRoleId(Integer roleId);

	/**
	 * @param pageParam
	 * @return
	 */
	long countBySelective(RoleParam pageParam);

	/**
	 * @return
	 */
	List<RoleMenu> selectAllRole();

	/**
	 * @param roleMenu
	 * @param pageParam
	 * @return
	 */
	List<RoleMenu> selectBySelective(RoleMenu roleMenu, RoleParam pageParam);

	/**
	 * @param pageParam
	 * @return
	 */
	List<RoleMenu> selectBySelective(RoleParam pageParam);

	/**
	 * 根据RoleId 查询角色的菜单权限，返回所有菜单，带checked状态
	 * 
	 * @param roleId
	 * @return
	 */
	List<Menu> queryMenuWithCheckStateByRoleId(Integer roleId);

	/**
	 * 根据roleId 查询所有的role-menu关联关系
	 * 
	 * @param roleId
	 * @return 
	 */
	List<RoleMenu> selectByRoleId(Integer roleId);

	/**
	 * 批量插入role-menu关联关系
	 * @param params
	 */
	void batchInsertRoleMenu(HashMap<String, Object> params);
}