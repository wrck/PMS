package com.dp.plat.core.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.PermissionMapper;
import com.dp.plat.core.dao.RoleMapper;
import com.dp.plat.core.dao.RolePermissionMapper;
import com.dp.plat.core.dao.UserMapper;
import com.dp.plat.core.dao.UserRoleMapper;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IShiroService;

@Service("shiroService")
public class ShiroService implements IShiroService{
	
	@Resource
	private UserMapper userDao;
	
	@Resource
	private UserRoleMapper userRoleDao;
	
	@Resource
	private RoleMapper roleDao;
	
	@Resource
	private RolePermissionMapper rolePermissionDao;
	
	@Resource
	private PermissionMapper permissionDao;

	
	@Override
	public User queryUserByName(String username) {
		return userDao.selectByUserName(username);
	}


	@Override
	public Set<String> queryUserRoleByName(String principal) {
		List<String> roleList = userRoleDao.queryUserRoleByName(principal);
		Set<String> roles = new LinkedHashSet<>();
		for(String role : roleList){
			roles.add(role);
		}
		return roles;
	}


	@Override
	public Set<String> queryPermissionByUsername(String principal) {
		List<String> permissionList = userRoleDao.queryPermissionByUsername(principal);
		Set<String> permissions = new HashSet<>();
		for(String permission : permissionList){
			permissions.add(permission);
		}
		return permissions;
	}

	@Override
	public Set<String> queryUserRoleByNameAndCompId(String userName, Integer compId) {
		Map<String, Object> params = new HashMap<>();
		params.put("username", userName);
		params.put("compId", compId);
		List<String> roleList = userRoleDao.queryUserRoleByNameAndCompId(params);
		Set<String> roles = new LinkedHashSet<>();
		for(String role : roleList){
			roles.add(role);
		}
		return roles;
	}

	@Override
	public Set<String> queryPermissionByUsernameAndCompId(String userName, Integer compId) {
		Map<String, Object> params = new HashMap<>();
		params.put("username", userName);
		params.put("compId", compId);
		List<String> permissionList = userRoleDao.queryPermissionByUsernameAndCompId(params);
		Set<String> permissions = new HashSet<>();
		for(String permission : permissionList){
			permissions.add(permission);
		}
		return permissions;
	}

	@Override
	public List<Menu> queryUserMenuByUsername(String username) {
		return userDao.queryUserMenuByUsername(username);
	}
	
	@Override
	public List<Menu> queryUserMenuByUserIdAndCompId(UserInfo userInfo) {
		return userDao.queryUserMenuByUserIdAndCompId(userInfo);
	}
	
}
 