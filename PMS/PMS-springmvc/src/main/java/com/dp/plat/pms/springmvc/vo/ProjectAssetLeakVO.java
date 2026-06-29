package com.dp.plat.pms.springmvc.vo;

public class ProjectAssetLeakVO extends IndustryLeakVO {

	private Integer leakId;
//	private Integer projectId;
//	private String projectName;
//	private String projectType;
//	
//	private Date effectiveFrom;
//	private Date effectiveTo;
	
//	private Integer assetId;
	
	public ProjectAssetLeakVO() {
		super();
	}

	public ProjectAssetLeakVO(Integer projectId) {
		super();
		this.setProjectId(projectId);
	}
	
	public ProjectAssetLeakVO(Integer projectId, Integer assetId) {
		super();
		this.setProjectId(projectId);
		this.setAssetId(assetId);
	}
	
	public ProjectAssetLeakVO(Integer projectId, Integer assetId, Integer leakId) {
		super();
		this.setProjectId(projectId);
		this.setAssetId(assetId);
		this.setLeakId(leakId);
	}

	public Integer getLeakId() {
		return leakId;
	}

	public void setLeakId(Integer leakId) {
		this.leakId = leakId;
	}

	
//	public Integer getAssetId() {
//		return assetId;
//	}
//
//	public void setAssetId(Integer assetId) {
//		this.assetId = assetId;
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
