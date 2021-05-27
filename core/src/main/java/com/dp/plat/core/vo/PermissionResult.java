package com.dp.plat.core.vo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PermissionResult {
	
	/**
	 * 执行状态
	 */
	private boolean status;

	/**
	 * 返回信息
	 */
	private String message;

	/**
	 * 权限类型
	 */
	private String permissionType;
	
	/**
	 * 权限集合
	 */
	private Collection<String> permissions;
	
	/**
	 * 角色集合
	 */
	private Collection<String> roles;
	
	private Map<String, Object> permissionMap;
	
	private String[] allPermitRoles;
	
	/**
	 * 额外数据
	 */
	private Object data;

	public PermissionResult() {
		this.status = true;
	}
	
	public PermissionResult(boolean status) {
		this.status = status;
	}
	
	public PermissionResult(boolean status, Object data) {
		this.status = status;
		this.data = data;
	}

	public PermissionResult(boolean status, Object data, String message) {
		this.status = status;
		this.data = data;
		this.message = message;
	}

	public PermissionResult(boolean status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public PermissionResult(boolean status, String message, String permissionType) {
		this.status = status;
		this.message = message;
		this.permissionType = permissionType;
	}
	
	public PermissionResult(boolean status, String message, String permissionType, Collection<String> permissions) {
		this.status = status;
		this.message = message;
		this.permissionType = permissionType;
		this.permissions = permissions;
	}
	
	public PermissionResult(boolean status, String permissionType, Collection<String> permissions) {
		this.status = status;
		this.permissionType = permissionType;
		this.permissions = permissions;
	}
	
	public PermissionResult(boolean status, String permissionType, Collection<String> permissions, Map<String, Object> permissionMap, String[] allPermitRoles) {
		this.status = status;
		this.permissionType = permissionType;
		this.permissions = permissions;
		this.permissionMap =permissionMap;
		this.allPermitRoles = allPermitRoles;
	}
	
	public PermissionResult(boolean status, String permissionType, Collection<String> permissions, Collection<String> roles, Map<String, Object> permissionMap, String[] allPermitRoles) {
		this.status = status;
		this.permissionType = permissionType;
		this.permissions = permissions;
		this.roles = roles;
		this.permissionMap =permissionMap;
		this.allPermitRoles = allPermitRoles;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}
	
	public Collection<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Collection<String> permissions) {
		this.permissions = permissions;
	}
	
	public Collection<String> getRoles() {
		return roles;
	}

	public void setRoles(Collection<String> roles) {
		this.roles = roles;
	}

	public Map<String, Object> getPermissionMap() {
		return permissionMap;
	}

	public void setPermissionMap(Map<String, Object> permissionMap) {
		this.permissionMap = permissionMap;
	}

	public String[] getAllPermitRoles() {
		return allPermitRoles;
	}

	public void setAllPermitRoles(String[] allPermitRoles) {
		this.allPermitRoles = allPermitRoles;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public boolean isPermit() {
		return this.status;
	}

	public Map<String, Object> getMap() {
	    Map<String, Object> map = new HashMap<>();
    	map.put("status", this.status);
	    if (this.message != null) {
	    	map.put("message", this.message);
	    }
	    if (this.permissionType != null) {
	    	map.put("permissionType", this.permissionType);
	    }
	    if (this.permissions != null) {
	    	map.put("permissions", this.permissions);
	    }
	    if (this.roles != null) {
	    	map.put("roles", this.roles);
	    }
//	    if (this.permissionMap != null) {
//	    	map.put("permissionMap", this.permissionMap);
//	    }
//	    if (this.allPermitRoles != null) {
//	    	map.put("allPermitRoles", this.allPermitRoles);
//	    }
    	if (this.data != null) {
    		map.put("data", this.data);
    	}
	    return map;
	}
}

