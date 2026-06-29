package com.dp.plat.support;

import javax.servlet.jsp.PageContext;

public interface LeftMenuGroupInterface {
	public void drow(PageContext pageContext);

	/**
	 * 获取功能菜单的权限
	 * 
	 * @param permissionKey
	 * @return
	 */
	public int gainPermission(String permissionKey);

	public String getTitlesrc();
}
