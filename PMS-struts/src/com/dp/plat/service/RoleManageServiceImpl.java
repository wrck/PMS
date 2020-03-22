package com.dp.plat.service;

import java.util.List;

import com.dp.plat.dao.RoleManageDao;
import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.RoleMenuPower;
import com.dp.plat.param.DisplayParam;

public class RoleManageServiceImpl extends BaseServiceImpl implements RoleManageService{
	private RoleManageDao roleManageDao;
	
	@Override
	public List<Role> queryRoleList(DisplayParam displayParam,Role role) {
		log("查看角色信息");
		return roleManageDao.queryRoleList(displayParam, role);
	}
	
	public int addRoleSubmit(Role role,List<RoleMenuPower>rolemenuidList){
		log("新增角色信息");
		return roleManageDao.addRoleSubmit(role,rolemenuidList);
	}
	
	public int updateRoleSubmit(Role role,List<RoleMenuPower>rolemenuidList){
		log("更新角色信息");
		return roleManageDao.updateRoleSubmit(role,rolemenuidList);
	}

	public RoleManageDao getRoleManageDao() {
		return roleManageDao;
	}

	public void setRoleManageDao(RoleManageDao roleManageDao) {
		this.roleManageDao = roleManageDao;
	}
	
	public List<RoleMenuPower>queryRoleMenuPowerList(Role role){
		return roleManageDao.queryRoleMenuPowerList(role);
	}
}
