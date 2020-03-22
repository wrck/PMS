package com.dp.plat.data.bean;

import java.util.Date;

public class ReportLineData {
	private String dataTypeCode;
	private String officeCode;//办事处编码
	private String conditionValue;//条件值
	private String totalValue;//被比值
	private String specificValue;//比例值
	private Date settingTime;//统计时间
	private String settings;//统计时间字符串
	
	private String officeName;//办事处名称

	public String getOfficeCode() {
		return officeCode;
	}
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public String getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}
	public String getSpecificValue() {
		return specificValue;
	}
	public void setSpecificValue(String specificValue) {
		this.specificValue = specificValue;
	}
	public Date getSettingTime() {
		return settingTime;
	}
	public void setSettingTime(Date settingTime) {
		this.settingTime = settingTime;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public ReportLineData() {
	}
	public ReportLineData(String officeCode , String officeName, String conditionValue,
			String totalValue, String specificValue, Date settingTime) {
		super();
		this.officeCode = officeCode;
		this.officeName = officeName;
		this.conditionValue = conditionValue;
		this.totalValue = totalValue;
		this.specificValue = specificValue;
		this.settingTime = settingTime;
	}
	public String getDataTypeCode() {
		return dataTypeCode;
	}
	public void setDataTypeCode(String dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}
	public String getSettings() {
		return settings;
	}
	public void setSettings(String settings) {
		this.settings = settings;
	}
}
