/**
 * 
 */
package com.dp.plat.core.vo;

import com.dp.plat.core.pojo.UserRole;

/**
 * @author w02611
 *
 */
public class UserRoleInfo extends UserRole {
	private String userName;
	private String realName;
	/**
	 * 用户状态
	 */
	private Short status;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

}
