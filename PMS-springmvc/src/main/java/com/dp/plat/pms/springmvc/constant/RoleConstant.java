package com.dp.plat.pms.springmvc.constant;

public class RoleConstant extends com.dp.plat.core.param.RoleConstant {
	
	/**
	 * 项目管理员
	 */
	public final static String ROLE_PM_ADMIN = "projectAdmin";
	
	/**
	 * 子项目管理员，拥有部分项目类型的管理权限
	 */
	public final static String ROLE_PM_SUB_ADMIN = "projectSubAdmin";
	
	/**
	 * 项目经理
	 */
	public final static String ROLE_PM_PROGRAM = "projectManager";
	
	/**
	 * 项目成员
	 */
	public final static String ROLE_PM_MEMBER = "projectMember";

	/**
	 * 安服质量监督员
	 */
	public static final String ROLE_PM_AFQC = "projectAFQC";
	
	/**
	 * 安服质量监督员
	 */
	public static final String ROLE_PM_YFQC = "projectAFQC";
	
	/**
	 * 销售人员
	 */
	public static final String ROLE_PM_SALES = "projectSales";
}
