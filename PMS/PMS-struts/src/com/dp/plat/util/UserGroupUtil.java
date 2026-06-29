package com.dp.plat.util;

/**
 * 用户组工具类,用于用户权限的定义和获取
 * 
 * @author 
 * 
 */
public class UserGroupUtil {
	private static String[] userGroupNames = { "代理商", "财务人员", "商务人员", "超级管理员" };
	/**
	 * 超级管理员
	 */
	public static int SUPPER_ADMINISTRATOR = 5;
	/**
	 * 普通用户
	 */
	public static int AGENCY_USER = 1;

	public static String getUserGroupName(int groupId) {
		return userGroupNames[groupId];
	}
}
