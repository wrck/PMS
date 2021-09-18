package com.dp.plat.param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 从SMS刷新的项目属性信息
 * @author admin
 *
 */
public class PrjProperty {
	private String orderExecNumber;
	private String projectCode;
	private String projectName;
	private String salesManCode;
	private String salesManName;
	private String marketCode;
	private String marketName;
	private int systemId;
	private String systemName;
	private int expendId;
	private String expendName;
	private int industryId;
	private String industryName;
	private String officeCode;
	private String officeName;
	private String serviceTypeName;
	private String channelName;
	private String engineeFee;
	private String objId;
	private String applyType;
	private String corporationCode;
	private String customerProjectName;
	private String finalCustomerName;
	private String agentName;
	private String majorProjectLevel;
	// 出货价
    private BigDecimal projectMoney;
    // 提交时间
    private Date submitTime;
    // 项目投标时间
    private Date predBidDate;
    // 客户联系人
    private String linkmanName;
    private String linkmanTel;
	
	public String getOrderExecNumber() {
		return orderExecNumber;
	}
	public void setOrderExecNumber(String orderExecNumber) {
		this.orderExecNumber = orderExecNumber;
	}
	public String getSalesManCode() {
		return salesManCode;
	}
	public void setSalesManCode(String salesManCode) {
		this.salesManCode = salesManCode;
	}
	public String getSalesManName() {
		return salesManName;
	}
	public void setSalesManName(String salesManName) {
		this.salesManName = salesManName;
	}
	public String getMarketCode() {
		return marketCode;
	}
	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public int getSystemId() {
		return systemId;
	}
	public void setSystemId(int systemId) {
		this.systemId = systemId;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public int getExpendId() {
		return expendId;
	}
	public void setExpendId(int expendId) {
		this.expendId = expendId;
	}
	public String getExpendName() {
		return expendName;
	}
	public void setExpendName(String expendName) {
		this.expendName = expendName;
	}
	public int getIndustryId() {
		return industryId;
	}
	public void setIndustryId(int industryId) {
		this.industryId = industryId;
	}
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
	public String getOfficeCode() {
		return officeCode;
	}
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getServiceTypeName() {
		return serviceTypeName;
	}
	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getEngineeFee() {
		return engineeFee;
	}
	public void setEngineeFee(String engineeFee) {
		this.engineeFee = engineeFee;
	}
	public String getObjId() {
		return objId;
	}
	public void setObjId(String objId) {
		this.objId = objId;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
    public String getCorporationCode() {
        return corporationCode;
    }
    public void setCorporationCode(String corporationCode) {
        this.corporationCode = corporationCode;
    }
    public String getCustomerProjectName() {
        return customerProjectName;
    }
    public void setCustomerProjectName(String customerProjectName) {
        this.customerProjectName = customerProjectName;
    }
    public String getFinalCustomerName() {
        return finalCustomerName;
    }
    public void setFinalCustomerName(String finalCustomerName) {
        this.finalCustomerName = finalCustomerName;
    }
    public String getAgentName() {
        return agentName;
    }
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
	public String getMajorProjectLevel() {
		return majorProjectLevel;
	}
	public void setMajorProjectLevel(String majorProjectLevel) {
		this.majorProjectLevel = majorProjectLevel;
	}
	public BigDecimal getProjectMoney() {
		return projectMoney;
	}
	public void setProjectMoney(BigDecimal projectMoney) {
		this.projectMoney = projectMoney;
	}
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	public Date getPredBidDate() {
		return predBidDate;
	}
	public void setPredBidDate(Date predBidDate) {
		this.predBidDate = predBidDate;
	}
	public String getLinkmanName() {
		return linkmanName;
	}
	public void setLinkmanName(String linkmanName) {
		this.linkmanName = linkmanName;
	}
	public String getLinkmanTel() {
		return linkmanTel;
	}
	public void setLinkmanTel(String linkmanTel) {
		this.linkmanTel = linkmanTel;
	}
}
