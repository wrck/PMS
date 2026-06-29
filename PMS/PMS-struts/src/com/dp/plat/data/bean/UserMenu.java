package com.dp.plat.data.bean;

import java.util.List;

/**
 * 用户权限菜单bean
 * @author admin
 *
 */
public class UserMenu {
	private int id;
	private String menuCode;
	private String menuName;
	private int menuLevel;
	private int superId;
	private List<UserMenu> userMenuList;//如果本身是父菜单，此为子菜单集合
	private String path;	//访问路径，提供用户的defaultPage
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public int getMenuLevel() {
		return menuLevel;
	}
	public void setMenuLevel(int menuLevel) {
		this.menuLevel = menuLevel;
	}
	public List<UserMenu> getUserMenuList() {
		return userMenuList;
	}
	public void setUserMenuList(List<UserMenu> userMenuList) {
		this.userMenuList = userMenuList;
	}
	public int getSuperId() {
		return superId;
	}
	public void setSuperId(int superId) {
		this.superId = superId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}

