package com.dp.plat.pms.springmvc.vo;

import java.util.Map;

import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryLeakWarning;
import com.dp.plat.security.annotation.EncryptEntity;
import com.dp.plat.security.annotation.EncryptField;

@EncryptEntity
public class LeakWarningVO extends IndustryLeakWarning {
	private static final long serialVersionUID = 1986996124682806028L;

	private IndustryAsset asset;
	
	// 资产ID
	private Integer assetId;
	// 资产名称
	@EncryptField
	private String assetName;
	// 资产IP/域名
	@EncryptField
	private String assetHost;
	// 项目ID
	private Integer projectId;
	// 涉及项目名称
	private String projectName;
	// 涉及客户单位
	@EncryptField
	private String customerName;
	// 资产自定义信息
	private Map assetCustomInfo;
	
	private Boolean checkProject;
	private String projectTypes;
	private String officeCodes;
	private String memberCode;

	public IndustryAsset getAsset() {
		return asset;
	}

	public void setAsset(IndustryAsset asset) {
		this.asset = asset;
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

	public String getAssetHost() {
		return assetHost;
	}

	public void setAssetHost(String assetHost) {
		this.assetHost = assetHost;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Map getAssetCustomInfo() {
		return assetCustomInfo;
	}

	public void setAssetCustomInfo(Map assetCustomInfo) {
		this.assetCustomInfo = assetCustomInfo;
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

}
