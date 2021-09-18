package com.dp.plat.core.service;

import java.util.List;
import java.util.Set;

import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;

public interface IShiroService {
	/**
	 * 根据用户名称查询用户信息
	 * @param username
	 * @return
	 */
	User queryUserByName(String username);
	/**
	 * 根据用户名称查询用户角色
	 * @param principal
	 * @return
	 */
	Set<String> queryUserRoleByName(String principal);
	/**
	 * 根据用户名称查询用户权限
	 * @param principal
	 * @return
	 */
	Set<String> queryPermissionByUsername(String principal);
	/**
	 * 根据用户名称查询用户菜单
	 * @param username
	 * @return
	 */
	List<Menu> queryUserMenuByUsername(String username);
	/**
	 * 根据用户名称,公司Id查询用户角色
	 * @param userName
	 * @param compId
	 * @return
	 */
	Set<String> queryUserRoleByNameAndCompId(String userName, Integer compId);
	/**
	 * 根据用户名称,公司Id查询用户权限
	 * @param userName
	 * @param compId
	 * @return
	 */
	Set<String> queryPermissionByUsernameAndCompId(String userName, Integer compId);
	/**
	 * 根据userId,compId查询用户菜单
	 * @param userInfo
	 * @return
	 */
	List<Menu> queryUserMenuByUserIdAndCompId(UserInfo userInfo);
	
}
