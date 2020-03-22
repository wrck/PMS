package com.dp.plat.param;
/**
 * 批量变更项目服务经理或项目经理的表单值对象
 * @author wenrencaike
 *
 */
public class ProjectBatchCgMbParam {
	/**
	 * 原项目成员用户名
	 */
	private String oldMemberCode;
	/**
	 * 新项目成员真实姓名（带用户名）
	 */
	private String newMemberCode;
	private String newMemberName;
	/**
	 * 变更类型，service 变更服务经理，porgram 变更项目经理，both 变更两者
	 */
	private String changeType;
	/**
	 * 项目所在部门名称
	 */
	private String dpName;
	/**
	 * 项目所在部门编号
	 */
	private String dpNo;
	
	public ProjectBatchCgMbParam() {
	}
	/**
	 * @return the oldMemberCode
	 */
	public String getOldMemberCode() {
		return oldMemberCode;
	}
	/**
	 * @param oldMemberCode the oldMemberCode to set
	 */
	public void setOldMemberCode(String oldMemberCode) {
		this.oldMemberCode = oldMemberCode;
	}
	/**
	 * @return the newMemberCode
	 */
	public String getNewMemberCode() {
		return newMemberCode;
	}
	/**
	 * @param newMemberCode the newMemberCode to set
	 */
	public void setNewMemberCode(String newMemberCode) {
		this.newMemberCode = newMemberCode;
	}
	/**
	 * @return the newMemberName
	 */
	public String getNewMemberName() {
		return newMemberName;
	}
	/**
	 * @param newMemberName the newMemberName to set
	 */
	public void setNewMemberName(String newMemberName) {
		this.newMemberName = newMemberName;
	}
	/**
	 * @return the changeType
	 */
	public String getChangeType() {
		return changeType;
	}
	/**
	 * @param changeType the changeType to set
	 */
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	/**
	 * @return the dpName
	 */
	public String getDpName() {
		return dpName;
	}
	/**
	 * @param dpName the dpName to set
	 */
	public void setDpName(String dpName) {
		this.dpName = dpName;
	}
	/**
	 * @return the dpNo
	 */
	public String getDpNo() {
		return dpNo;
	}
	/**
	 * @param dpNo the dpNo to set
	 */
	public void setDpNo(String dpNo) {
		this.dpNo = dpNo;
	}

	@Override
	public String toString() {
		return "ProjectBatchCgMbParam [oldMemberCode=" + oldMemberCode + ", newMemberCode=" + newMemberCode
				+ ", newMemberName=" + newMemberName + ", changeType=" + changeType + ", dpName=" + dpName + ", dpNo="
				+ dpNo + "]";
	}
}
