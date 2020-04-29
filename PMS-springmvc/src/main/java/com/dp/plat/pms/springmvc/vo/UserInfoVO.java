package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.core.pojo.User;

public class UserInfoVO extends com.dp.plat.core.vo.UserInfoVO {

	private String userName;
	private User user;
	
	private String roleIds;

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
	
}
