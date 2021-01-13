package com.dp.plat.service;

import java.util.List;

import com.dp.plat.data.bean.Role;
import com.dp.plat.data.bean.RoleMenuPower;
import com.dp.plat.param.DisplayParam;

public interface RoleManageService {
	List<Role> queryRoleList(DisplayParam displayParam,Role role);
	int addRoleSubmit(Role role,List<RoleMenuPower>rolemenuidList);
	int updateRoleSubmit(Role role,List<RoleMenuPower>rolemenuidList);
	List<RoleMenuPower>queryRoleMenuPowerList(Role role);

}
