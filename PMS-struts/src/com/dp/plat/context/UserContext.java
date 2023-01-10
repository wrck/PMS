package com.dp.plat.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.data.bean.User;
import com.dp.plat.util.UserGroupUtil;

public class UserContext
{
	/**
	 * 普通用户
	 */
	public static final int ROLE_OPERATOR_AGENT = UserGroupUtil.AGENCY_USER;
	/**
	 * 超级管理员 5
	 */
	public static final int ROLE_OPERATOR_SUPERADMIN = UserGroupUtil.SUPPER_ADMINISTRATOR;

	private boolean login = false;

	private String username;
	private String ip;
	private Integer role;
	private Long lastoptime;
	private User user;
	private String option="";
	private boolean checked = false;
	private Map<String, Integer> permissionMap;
	private String defaultPage;
	private Map<Integer, Map<String, Integer>>roleMenuPowerMap;	//角色各菜单功能增删改权限
	private boolean cas=false; //是否使用cas登录
	private String url="";
	private Map<String, Object> extData;
	public String getOption()
	{
		return option;
	}

	public void setOption(String option)
	{
		this.option = option;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public boolean getChecked(){
		return this.checked;
	}
	
	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	private Long timeout = 60L;

	private static List<UserContext> onlinelist = new ArrayList<UserContext>();

	private static void ListAdd(UserContext c)
	{
		if (onlinelist.contains(c) == false)
		{
			onlinelist.add(c);
		}
	}

	private static void ListDel(UserContext c)
	{
		onlinelist.remove(c);
	}

	public static List<UserContext> getOnlineList()
	{
		return onlinelist;
	}

	public void login(User user, String ip,Map<String, Integer> permissionMap , String defaultPage,Map<Integer, Map<String, Integer>>roleMenuPowerMap){
		this.login = true;
		this.user = user;
		this.username = user.getUsername();
		this.ip = ip;
		this.permissionMap = permissionMap;
		this.timeout = 600L;
		this.lastoptime = new Date().getTime();
		this.defaultPage = defaultPage;
		this.roleMenuPowerMap=roleMenuPowerMap;
		UserContext.ListAdd(this);
	}
	
	public boolean isHasRole(int roleId){
	    try {
    		return getUser().isHasRole(roleId);
	    } catch(Exception e) {
	    }
		return false;
	}
	
	public boolean isHasAnyRole(Object... roleIds){
        try {
            return getUser().isHasAnyRole(roleIds);
        } catch(Exception e) {
        }
        return false;
    }
	
	public boolean isHasAnyRole(String roleIds){
	    try {
            return getUser().isHasAnyRole(roleIds);
        } catch(Exception e) {
        }
        return false;
    }
	
	public void loginFail(String username, String ip, Long logintime)
	{
		this.login = false;
		this.username = username;
		this.ip = ip;
		this.lastoptime = logintime;
	}

	public void logout()
	{
		this.login = false;
		this.checked = false;
		this.user = null;
        this.username = null;
        this.ip = null;
        this.permissionMap = null;
        this.timeout = 600L;
        this.lastoptime = new Date().getTime();
        this.defaultPage = null;
        this.roleMenuPowerMap = null;
        this.extData = null;
		
		UserContext.ListDel(this);
	}

	public String getUsername()
	{
		return username;
	}

	public String getIp()
	{
		return ip;
	}

	public Integer getRole()
	{
		return role;
	}

	public boolean isLogin()
	{
		return login;
	}

	public Long getLastoptime()
	{
		return lastoptime;
	}

	public void touch()
	{
		lastoptime = new Date().getTime() / 1000;
	}

	public boolean isValid()
	{
		return (new Date().getTime() / 1000 - lastoptime) <= timeout;
	}

	public static UserContext getUserContext()
	{
		return (UserContext) SpringContext.getBean("userContext");
	}

	public Map<String, Integer> getPermissionMap() {
		return permissionMap;
	}

	public void setPermissionMap(Map<String, Integer> permissionMap) {
		this.permissionMap = permissionMap;
	}
	
	public boolean isHasPermission(String... names) {
	    if (permissionMap == null || permissionMap.isEmpty()) {
	        return false;
	    }
	    if (extData != null) {
	        Map<String, List<String>> permissionNameMap = (Map<String, List<String>>) extData.getOrDefault("permissionNameMap", Collections.emptyMap());
	        // 初始根据名称集合来，如果为空则false,如果非空为true。便于后面鉴权
	        boolean isAllPermit = !permissionNameMap.isEmpty();
	        for (String name : names) {
	            name = StringUtils.trim(name);
	            List<String> permissionKeys = permissionNameMap.getOrDefault(name, Collections.emptyList());
	            boolean isPermit = false;
	            for (String key : permissionKeys) {
	                key = StringUtils.trim(key);
	                isPermit = isPermit || Integer.valueOf(1).equals(permissionMap.get(key));
	                if (isPermit) {
	                    break;
	                }
                }
	            isAllPermit = isAllPermit && isPermit;
	            if (!isAllPermit) {
	                break;
	            }
            }
	        return isAllPermit;
	    } 
	    return false;
	}

	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}

	public String getDefaultPage() {
		return defaultPage;
	}

	public Map<Integer, Map<String, Integer>> getRoleMenuPowerMap() {
		return roleMenuPowerMap;
	}

	public void setRoleMenuPowerMap(
			Map<Integer, Map<String, Integer>> roleMenuPowerMap) {
		this.roleMenuPowerMap = roleMenuPowerMap;
	}
	
	public boolean isCas(){
		return cas;
	}

	public void setCas(boolean cas) {
		this.cas = cas;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    public Map<String, Object> getExtData() {
        return extData;
    }

    public void setExtData(Map<String, Object> extData) {
        this.extData = extData;
    }
}
