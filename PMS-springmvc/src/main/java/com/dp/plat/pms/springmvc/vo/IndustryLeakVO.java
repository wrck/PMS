package com.dp.plat.pms.springmvc.vo;

import java.util.Date;

import com.dp.plat.pms.springmvc.entity.IndustryLeak;

public class IndustryLeakVO extends IndustryLeak {

	private Integer projectId;
	private String projectName;
	private String projectType;
	
	private Integer assetId;
	private String assetName;
	
	private Date effectiveFrom;
	private Date effectiveTo;
	
	
	public IndustryLeakVO() {
		super();
	}

	public IndustryLeakVO(Integer projectId) {
		super();
		this.projectId = projectId;
	}
	
	public IndustryLeakVO(Integer projectId, Integer assetId) {
		super();
		this.projectId = projectId;
		this.assetId = assetId;
	}
	
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
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

	public void setEffective(Date date) {
		this.setEffectiveFrom(date);
		this.setEffectiveTo(date);
	}
}
