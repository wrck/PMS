package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.core.pojo.User;

public class UserDetail extends com.dp.plat.core.vo.UserDetail {

	private User user;
	
	private String roleIds;
	
	private String compName;
	
	private String projectTypes;
	
	private String officeCodes;
	
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

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getProjectTypes() {
		return projectTypes;
	}

	public void setProjectTypes(String projectTypes) {
		this.projectTypes = projectTypes;
	}

	public String getOfficeCodes() {
		return officeCodes;
	}

	public void setOfficeCodes(String officeCodes) {
		this.officeCodes = officeCodes;
	}

}
