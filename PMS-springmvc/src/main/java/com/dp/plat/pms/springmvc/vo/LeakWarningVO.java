package com.dp.plat.pms.springmvc.vo;

import java.util.Map;

import com.dp.plat.pms.springmvc.entity.IndustryAsset;
import com.dp.plat.pms.springmvc.entity.IndustryLeakWarning;

public class LeakWarningVO extends IndustryLeakWarning {
	private static final long serialVersionUID = 1986996124682806028L;

	private IndustryAsset asset;
	
	// 资产ID
	private Integer assetId;
	// 资产名称
	private String assetName;
	// 资产IP/域名
	private String assetHost;
	// 涉及项目名称
	private String projectName;
	// 涉及客户单位
	private String customerName;
	// 资产自定义信息
	private Map assetCustomInfo;

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

}
