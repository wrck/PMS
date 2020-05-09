package com.dp.plat.pms.springmvc.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SettlementVO extends DispatchSettlement {

	private Integer projectId;
	private String officeCodes;
	private String projectTypes;

	private String smsProjectCode;
	@JsonSerialize(using = JsonSerializer.class)
	private Date smsSubmitTime;
	private String smsProjectAmount;
	private String smsProjectName;
	private String smsOrderExecNumber;
	private String contractNos;

	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal collectedAmount;
	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal deliveredAmount;
	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal contractAmount;
	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal settledAmount;
	@JsonSerialize(using = JsonSerializer.class)
	private Double collectedRatio;
	@JsonSerialize(using = JsonSerializer.class)
	private Double settleRatio;
	
	private DispatchProject dispatch;

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getOfficeCodes() {
		return officeCodes;
	}

	public void setOfficeCodes(String officeCodes) {
		this.officeCodes = officeCodes;
	}

	public String getProjectTypes() {
		return projectTypes;
	}

	public void setProjectTypes(String projectTypes) {
		this.projectTypes = projectTypes;
	}

	public String getSmsProjectCode() {
		return smsProjectCode;
	}

	public void setSmsProjectCode(String smsProjectCode) {
		this.smsProjectCode = smsProjectCode;
		this.setCustomInfoByKey("smsProjectCode", smsProjectCode);
	}

	public Date getSmsSubmitTime() {
		return smsSubmitTime;
	}

	public void setSmsSubmitTime(Date date) {
		this.smsSubmitTime = date;
		this.setCustomInfoByKey("smsSubmitTime", date);
	}

	public String getSmsProjectAmount() {
		return smsProjectAmount;
	}

	public void setSmsProjectAmount(String smsProjectAmount) {
		this.smsProjectAmount = smsProjectAmount;
		this.setCustomInfoByKey("smsProjectAmount", smsProjectAmount);
	}

	public String getSmsProjectName() {
		return smsProjectName;
	}

	public void setSmsProjectName(String smsProjectName) {
		this.smsProjectName = smsProjectName;
		this.setCustomInfoByKey("smsProjectName", smsProjectName);
	}

	public String getSmsOrderExecNumber() {
		return smsOrderExecNumber;
	}

	public void setSmsOrderExecNumber(String smsOrderExecNumber) {
		this.smsOrderExecNumber = smsOrderExecNumber;
		this.setCustomInfoByKey("smsOrderExecNumber", smsOrderExecNumber);
	}

	public String getContractNos() {
		return contractNos;
	}

	public void setContractNos(String contractNos) {
		this.contractNos = contractNos;
		this.setCustomInfoByKey("contractNos", contractNos);
	}

	public BigDecimal getCollectedAmount() {
		return collectedAmount;
	}

	public void setCollectedAmount(BigDecimal collectedAmount) {
		this.collectedAmount = collectedAmount;
		this.setCustomInfoByKey("collectedAmount", collectedAmount);
	}

	public BigDecimal getDeliveredAmount() {
		return deliveredAmount;
	}

	public void setDeliveredAmount(BigDecimal deliveredAmount) {
		this.deliveredAmount = deliveredAmount;
		this.setCustomInfoByKey("deliveredAmount", deliveredAmount);
	}

	public BigDecimal getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(BigDecimal contractAmount) {
		this.contractAmount = contractAmount;
		this.setCustomInfoByKey("contractAmount", contractAmount);
	}

	public BigDecimal getSettledAmount() {
		return settledAmount;
	}

	public void setSettledAmount(BigDecimal settledAmount) {
		this.settledAmount = settledAmount;
		this.setCustomInfoByKey("settledAmount", settledAmount);
	}

	public Double getCollectedRatio() {
		return collectedRatio;
	}

	public void setCollectedRatio(Double collectedRatio) {
		this.collectedRatio = collectedRatio;
		this.setCustomInfoByKey("collectedRatio", collectedRatio);
	}

	public Double getSettleRatio() {
		return settleRatio;
	}

	public void setSettleRatio(Double settleRatio) {
		this.settleRatio = settleRatio;
		this.setCustomInfoByKey("settleRatio", settleRatio);
	}
	
	public DispatchProject getDispatch() {
		return dispatch;
	}

	public void setDispatch(DispatchProject dispatch) {
		this.dispatch = dispatch;
	}
	
	public Object getCustomInfoByKey(String key) {
		Map<?, ?> customInfo = getCustomInfo();
		if (customInfo != null && !customInfo.isEmpty()) {
			return customInfo.get(key);
		}
		return null;
	}

	public void setCustomInfoByKey(String key, Object value) {
		Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
		if (customInfo == null) {
			customInfo = new HashMap<>();
			this.setCustomInfo(customInfo);
		}
		customInfo.put(key, value);
	}

}
