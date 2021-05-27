package com.dp.plat.pms.springmvc.vo;

import java.util.Date;

import com.dp.plat.pms.springmvc.entity.IndustryAsset;

public class IndustryAssetVO extends IndustryAsset {

//	private Integer projectId;
//	private String projectName;
//	private String projectType;
//	
//	private Date effectiveFrom;
//	private Date effectiveTo;
	
	private Boolean checkProject;
	private String projectTypes;
	private String officeCodes;
	private String memberCode;
	
	public IndustryAssetVO() {
		super();
	}

	public Boolean getCheckProject() {
		return checkProject;
	}

	public void setCheckProject(Boolean checkProject) {
		this.checkProject = checkProject;
	}

	public String getProjectTypes() {
		return projectTypes;
	}

	public void setProjectTypes(String projectTypes) {
		this.projectTypes = projectTypes;
	}

	public String getOfficeCodes() {
		return officeCodes;
	}

	public void setOfficeCodes(String officeCodes) {
		this.officeCodes = officeCodes;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

//	public IndustryAssetVO(Integer projectId) {
//		super();
//		this.projectId = projectId;
//	}
//	
//	public Integer getProjectId() {
//		return projectId;
//	}
//
//	public void setProjectId(Integer projectId) {
//		this.projectId = projectId;
//	}
//
//	public String getProjectName() {
//		return projectName;
//	}
//
//	public void setProjectName(String projectName) {
//		this.projectName = projectName;
//	}
//
//	public String getProjectType() {
//		return projectType;
//	}
//
//	public void setProjectType(String projectType) {
//		this.projectType = projectType;
//	}
//
//	public Date getEffectiveFrom() {
//		return effectiveFrom;
//	}
//
//	public void setEffectiveFrom(Date effectiveFrom) {
//		this.effectiveFrom = effectiveFrom;
//	}
//
//	public Date getEffectiveTo() {
//		return effectiveTo;
//	}
//
//	public void setEffectiveTo(Date effectiveTo) {
//		this.effectiveTo = effectiveTo;
//	}
//
//	public void setEffective(Date date) {
//		this.setEffectiveFrom(date);
//		this.setEffectiveTo(date);
//	}
	
}
