package com.dp.plat.pms.springmvc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.vo.PermissionResult;

public class PermissionUtils {
	
	public static final String EDIT_PERMISSIONS = "add|edit|delete|upload|import|submit";
	public static final String VIEW_PERMISSIONS = "list|detail|download|batchDownload";
	
	private String permissionPrefix = "";
	private String editPermissions = EDIT_PERMISSIONS;
	private String viewPermissions = VIEW_PERMISSIONS;
	private String editRegex;
	private String viewRegex;
	private String[] allPermitRoles;
	private String lockedState;
	
	public PermissionUtils() {
		super();
	}

	public PermissionUtils(String permissionPrefix) {
		this();
		this.permissionPrefix = StringUtils.trimToEmpty(permissionPrefix);
	}
	
	public PermissionUtils(String editPermissions, String viewPermissions) {
		this();
		this.editPermissions = editPermissions;
		this.viewPermissions = viewPermissions;
	}
	
	public PermissionUtils(String permissionPrefix, String editPermissions, String viewPermissions) {
		this(editPermissions, viewPermissions);
		this.permissionPrefix = StringUtils.trimToEmpty(permissionPrefix);
	}
	
	public PermissionUtils(String[] allPermitRoles) {
		super();
		this.allPermitRoles = allPermitRoles;
	}

	public PermissionUtils(String permissionPrefix, String[] allPermitRoles) {
		this();
		this.permissionPrefix = StringUtils.trimToEmpty(permissionPrefix);
		this.allPermitRoles = allPermitRoles;
	}
	
	public PermissionUtils(String permissionPrefix, Collection<String> allPermitRoles) {
		this();
		this.permissionPrefix = StringUtils.trimToEmpty(permissionPrefix);
		this.allPermitRoles = allPermitRoles != null ? allPermitRoles.toArray(new String[] {}) : null;
	}
	
	public PermissionUtils(String editPermissions, String viewPermissions, String[] allPermitRoles) {
		this();
		this.editPermissions = editPermissions;
		this.viewPermissions = viewPermissions;
		this.allPermitRoles = allPermitRoles;
	}
	
	public PermissionUtils(String permissionPrefix, String editPermissions, String viewPermissions, String[] allPermitRoles) {
		this(editPermissions, viewPermissions);
		this.permissionPrefix = StringUtils.trimToEmpty(permissionPrefix);
		this.allPermitRoles = allPermitRoles;
	}
	
	public PermissionResult checkPermit(Map<String, Object> permission, String[] permissions) {
		Boolean isPermit = false;
		String permissionType = "";
		Collection<String> permissionSet = null;
		Collection<String> roleSet = null;
		try {
			if (permission != null) {
				permissionSet = (Collection<String>) permission.get("permissions");
				roleSet = (Collection<String>) permission.getOrDefault("roles", UserContext.getCurrentPrincipal().getRoles());
				Boolean disabled = Boolean.TRUE.equals(permission.get("disabled"));
				Boolean allPerm = (Boolean) permission.get("all");
				boolean isRolePermit = false;
				// 特殊角色权限,当具备访问权限时，进行角色权限增强
				if (allPermitRoles != null && allPermitRoles.length > 0) {
					if (UserContext.hasAnyRoles(allPermitRoles)) {
						permissionType = "all";
						isRolePermit = true;
						permissionSet = null;
					}
				}
				if (Boolean.TRUE.equals(allPerm)) {
					isPermit = true;
					permissionType = "all";
				} else {
					String perms = StringUtils.join(permissions, ",");
					boolean editPermit = Boolean.TRUE.equals(permission.get("edit"));
					boolean viewPermit = Boolean.TRUE.equals(permission.get("view"));
					// 特殊角色权限,当具备访问权限时，进行角色权限增强
					isRolePermit = isRolePermit && (editPermit || viewPermit);
					if ((isRolePermit || editPermit) && perms.matches(getEditRegex())) {
						isPermit = true;
						permissionType = isRolePermit ? "all" : "edit";
					} else if ((isRolePermit || editPermit || viewPermit) && perms.matches(getViewRegex())) {
						isPermit = true;
						permissionType = isRolePermit ? "all" : (editPermit ? "edit" : "view");
					}
				}
				// 如果已失效，则只允许查看
				if (Boolean.TRUE.equals(disabled) && isPermit) {
					permissionType = "view";
				}
			}
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
		}
		return new PermissionResult(isPermit, permissionType, permissionSet, roleSet, permission, allPermitRoles);
	}
	
	private String initRegex(String perms) {
		StringBuilder regex = new StringBuilder(".*");
		regex.append(permissionPrefix).append("(").append(perms).append(")\\b,?.*");
		return regex.toString();
	}

	public String getPermissionPrefix() {
		return permissionPrefix;
	}

	public void setPermissionPrefix(String permissionPrefix) {
		this.permissionPrefix = permissionPrefix;
	}

	public String getEditRegex() {
		if (StringUtils.isBlank(editRegex)) {
			editRegex = initRegex(getEditPermissions());
		}
		return editRegex;
	}

	public void setEditRegex(String editRegex) {
		this.editRegex = editRegex;
	}

	public String getViewRegex() {
		if (StringUtils.isBlank(viewRegex)) {
			viewRegex = initRegex(getViewPermissions());
		}
		return viewRegex;
	}

	public void setViewRegex(String viewRegex) {
		this.viewRegex = viewRegex;
	}

	public String getEditPermissions() {
		return editPermissions;
	}

	public void setEditPermissions(String editPermissions) {
		this.editPermissions = editPermissions;
	}

	public String getViewPermissions() {
		return viewPermissions;
	}

	public void setViewPermissions(String viewPermissions) {
		this.viewPermissions = viewPermissions;
	}

	public String[] getAllPermitRoles() {
		return allPermitRoles;
	}

	public void setAllPermitRoles(String... allPermitRoles) {
		this.allPermitRoles = allPermitRoles;
	}

	public String getLockedState() {
		return lockedState;
	}

	public void setLockedState(String lockedState) {
		this.lockedState = lockedState;
	}
	
	/**
	 * 获取权限交集
	 * @param allPermistRoles
	 * @param roles
	 * @return
	 */
	public static String[] getRetainAllRoles(String[] allPermistRoles, Collection<String> roles) {
		if (allPermistRoles == null) {
			allPermistRoles = new String[] {};
		}
		List<String> allPermitRoleList = new ArrayList<String>(Arrays.asList(allPermistRoles));
		if (roles != null) {
			allPermitRoleList.retainAll(roles);
			allPermistRoles = allPermitRoleList.toArray(new String[] {});
		}
		return allPermistRoles;
	}
}
