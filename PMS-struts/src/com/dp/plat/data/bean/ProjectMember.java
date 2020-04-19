package com.dp.plat.data.bean;

import java.util.Date;

/**
 * 项目成员
 * @author admin
 *
 */
public class ProjectMember {
	private Integer id;
	private Integer projectId;
	private String projectType;
	private String memberRole;
	private String memberRoleName;
	private String memberCode;
	private String memberName;
	private String phoneNum;
	private String email;
	private String fromFlag;
	private Date createTime;
	private String createBy;
	private Date updateTime;
	private String updateBy;
	private Date effectiveFrom;
	private Date effectiveTo;
	private int dataState;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProjectId() {
		if (projectId == null) {
			return 0;
		}
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getMemberRole() {
		return memberRole;
	}
	public void setMemberRole(String memberRole) {
		this.memberRole = memberRole;
	}
	public String getMemberRoleName() {
		return memberRoleName;
	}
	public void setMemberRoleName(String memberRoleName) {
		this.memberRoleName = memberRoleName;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}
	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	public Date getEffectiveTo() {
		return effectiveTo;
	}
	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}
	public int getDataState() {
		return dataState;
	}
	public void setDataState(int dataState) {
		this.dataState = dataState;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFromFlag() {
		return fromFlag;
	}
	public void setFromFlag(String fromFlag) {
		this.fromFlag = fromFlag;
	}
	
}
