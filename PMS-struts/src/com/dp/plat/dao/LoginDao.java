package com.dp.plat.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.RoleMenuPower;
import com.dp.plat.data.bean.User;

public interface LoginDao {
	User querUser(String username);
	
	/**
	 * 获取用户功能权限
	 * @param userId
	 * @return
	 */
	Map<String, Integer> queryUserMenuMap(int userId);
	/**
	 * 获取用户默认的登录系统首页
	 * @param userId
	 * @return
	 */
	String queryUserDefaultPage(int userId);
	/**
	 * 获取个功能菜单的增删改查权限
	 * @param username
	 * @return
	 */
	List<RoleMenuPower>queryRoleMenuPowerList(Role role);
	
	/**
	 *获取系统运行环境变量
	 * @param code
	 * @return
	 */
	String querySysArg(String code);
}
