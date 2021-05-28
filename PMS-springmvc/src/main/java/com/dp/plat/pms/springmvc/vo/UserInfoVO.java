package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.core.pojo.User;

public class UserInfoVO extends com.dp.plat.core.vo.UserInfoVO {

	private User user;
	
	private String roleIds;
	
	private String roles;

	private Integer roleId;
	
	private String roleCodes;
	
	private Integer maxRolePriority;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}

	public Integer getMaxRolePriority() {
		return maxRolePriority;
	}

	public void setMaxRolePriority(Integer maxRolePriority) {
		this.maxRolePriority = maxRolePriority;
	}
	
}
