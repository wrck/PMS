/**
 * 
 */
package com.dp.plat.core.vo;

import com.dp.plat.core.pojo.UserInfo;

/**
 * @author w02611
 *
 */
public class UserInfoVO extends UserInfo {
	
	private String userName;

	private String compName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}
	
}
