package com.dp.plat.pms.springmvc.util;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.core.vo.PermissionResult;

public class PermissionUtils {
	
	public static final String EDIT_PERMISSIONS = "add|edit|delete|upload|import";
	public static final String VIEW_PERMISSIONS = "list|detail|download|batchDownload";
	
	private String permissionPrefix = "";
	private String editPermissions = EDIT_PERMISSIONS;
	private String viewPermissions = VIEW_PERMISSIONS;
	private String editRegex;
	private String viewRegex;
	
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

	public PermissionResult checkPermit(Map<String, Object> permission, String... permissions) {
		Boolean isPermit = false;
		String permissionType = "";
		Collection<String> permissionSet = null;
		try {
			if (permission != null) {
				permissionSet = (Collection<String>) permission.get("permissions");
				Boolean allPerm = (Boolean) permission.get("all");
				if (Boolean.TRUE.equals(allPerm)) {
					isPermit = true;
					permissionType = "all";
				} else {
					String perms = StringUtils.join(permissions, ",");
					boolean editPermit = Boolean.TRUE.equals(permission.get("edit"));
					boolean viewPermit = Boolean.TRUE.equals(permission.get("view"));
					if (editPermit && perms.matches(getEditRegex())) {
						isPermit = true;
						permissionType = "edit";
					} else if ((editPermit || viewPermit) && perms.matches(getViewRegex())) {
						isPermit = true;
						permissionType = editPermit ? "edit" : "view";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new PermissionResult(isPermit, permissionType, permissionSet);
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
	
}
