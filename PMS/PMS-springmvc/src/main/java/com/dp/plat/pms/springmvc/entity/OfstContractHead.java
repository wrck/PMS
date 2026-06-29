package com.dp.plat.pms.springmvc.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class OfstContractHead {
	@Id
	private Integer id;

	private String contractNum;

	private String batchCode;

	private String projectName;

	private String orderNum;

	private String clientSupplierCode;

	private String clientSupplierName;

	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal contractMoneyAmount;

	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal deliveredMoneyAmount;

	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal collectedMoneyAmount;

	@JsonSerialize(using = JsonSerializer.class)
	private Double collectedMoneyRatio;

	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal receivablesMoneyAmount;

	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal overDueMoneyAmount;

	private String maketingDepartmentName;

	private String officeName;

	private String industryName;

	private String marketingRepresentativeName;

	private String currencyName;

	private String createBy;

	@JsonSerialize(using = JsonSerializer.class)
	private Date createTime;

	private String updateBy;

	@JsonSerialize(using = JsonSerializer.class)
	private Date updateTime;

	@JsonSerialize(using = JsonSerializer.class)
	private Date effectiveFrom;

	@JsonSerialize(using = JsonSerializer.class)
	private Date effectiveTo;

	private String importBatchNum;

	@JsonSerialize(using = JsonSerializer.class)
	private Date contractCreateDate;

	private String projectcode;

	private String marketcode;

	private Integer systemid;

	private Integer industryid;

	private String officecode;

	private Integer expendid;

	private String usernamec;

	@DateTimeFormat(iso = ISO.DATE)
	private Date latestShipDate;

	private String usernamec2;

	private Integer systemidO;

	private Integer expendidO;

	private String industryNameO;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContractNum() {
		return contractNum;
	}

	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getClientSupplierCode() {
		return clientSupplierCode;
	}

	public void setClientSupplierCode(String clientSupplierCode) {
		this.clientSupplierCode = clientSupplierCode;
	}

	public String getClientSupplierName() {
		return clientSupplierName;
	}

	public void setClientSupplierName(String clientSupplierName) {
		this.clientSupplierName = clientSupplierName;
	}

	public BigDecimal getContractMoneyAmount() {
		return contractMoneyAmount;
	}

	public void setContractMoneyAmount(BigDecimal contractMoneyAmount) {
		this.contractMoneyAmount = contractMoneyAmount;
	}

	public BigDecimal getDeliveredMoneyAmount() {
		return deliveredMoneyAmount;
	}

	public void setDeliveredMoneyAmount(BigDecimal deliveredMoneyAmount) {
		this.deliveredMoneyAmount = deliveredMoneyAmount;
	}

	public BigDecimal getCollectedMoneyAmount() {
		return collectedMoneyAmount;
	}

	public void setCollectedMoneyAmount(BigDecimal collectedMoneyAmount) {
		this.collectedMoneyAmount = collectedMoneyAmount;
	}

	public Double getCollectedMoneyRatio() {
		return collectedMoneyRatio;
	}

	public void setCollectedMoneyRatio(Double collectedMoneyRatio) {
		this.collectedMoneyRatio = collectedMoneyRatio;
	}

	public BigDecimal getReceivablesMoneyAmount() {
		return receivablesMoneyAmount;
	}

	public void setReceivablesMoneyAmount(BigDecimal receivablesMoneyAmount) {
		this.receivablesMoneyAmount = receivablesMoneyAmount;
	}

	public BigDecimal getOverDueMoneyAmount() {
		return overDueMoneyAmount;
	}

	public void setOverDueMoneyAmount(BigDecimal overDueMoneyAmount) {
		this.overDueMoneyAmount = overDueMoneyAmount;
	}

	public String getMaketingDepartmentName() {
		return maketingDepartmentName;
	}

	public void setMaketingDepartmentName(String maketingDepartmentName) {
		this.maketingDepartmentName = maketingDepartmentName;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public String getMarketingRepresentativeName() {
		return marketingRepresentativeName;
	}

	public void setMarketingRepresentativeName(String marketingRepresentativeName) {
		this.marketingRepresentativeName = marketingRepresentativeName;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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

	public String getImportBatchNum() {
		return importBatchNum;
	}

	public void setImportBatchNum(String importBatchNum) {
		this.importBatchNum = importBatchNum;
	}

	public Date getContractCreateDate() {
		return contractCreateDate;
	}

	public void setContractCreateDate(Date contractCreateDate) {
		this.contractCreateDate = contractCreateDate;
	}

	public String getProjectcode() {
		return projectcode;
	}

	public void setProjectcode(String projectcode) {
		this.projectcode = projectcode;
	}

	public String getMarketcode() {
		return marketcode;
	}

	public void setMarketcode(String marketcode) {
		this.marketcode = marketcode;
	}

	public Integer getSystemid() {
		return systemid;
	}

	public void setSystemid(Integer systemid) {
		this.systemid = systemid;
	}

	public Integer getIndustryid() {
		return industryid;
	}

	public void setIndustryid(Integer industryid) {
		this.industryid = industryid;
	}

	public String getOfficecode() {
		return officecode;
	}

	public void setOfficecode(String officecode) {
		this.officecode = officecode;
	}

	public Integer getExpendid() {
		return expendid;
	}

	public void setExpendid(Integer expendid) {
		this.expendid = expendid;
	}

	public String getUsernamec() {
		return usernamec;
	}

	public void setUsernamec(String usernamec) {
		this.usernamec = usernamec;
	}

	public Date getLatestShipDate() {
		return latestShipDate;
	}

	public void setLatestShipDate(Date latestShipDate) {
		this.latestShipDate = latestShipDate;
	}

	public String getUsernamec2() {
		return usernamec2;
	}

	public void setUsernamec2(String usernamec2) {
		this.usernamec2 = usernamec2;
	}

	public Integer getSystemidO() {
		return systemidO;
	}

	public void setSystemidO(Integer systemidO) {
		this.systemidO = systemidO;
	}

	public Integer getExpendidO() {
		return expendidO;
	}

	public void setExpendidO(Integer expendidO) {
		this.expendidO = expendidO;
	}

	public String getIndustryNameO() {
		return industryNameO;
	}

	public void setIndustryNameO(String industryNameO) {
		this.industryNameO = industryNameO;
	}
}