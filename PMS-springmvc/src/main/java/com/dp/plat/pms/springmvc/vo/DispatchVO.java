package com.dp.plat.pms.springmvc.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DispatchVO extends DispatchProject {

	private Integer projectId;

	private String typeName;
	private String stateName;
	private String createName;
	private String officeName;

	private String collectContractNos;
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

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public BigDecimal getCollectedAmount() {
		return collectedAmount;
	}

	public void setCollectedAmount(BigDecimal collectedAmount) {
		this.collectedAmount = collectedAmount;
	}

	public BigDecimal getDeliveredAmount() {
		return deliveredAmount;
	}

	public void setDeliveredAmount(BigDecimal deliveredAmount) {
		this.deliveredAmount = deliveredAmount;
	}

	public BigDecimal getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(BigDecimal contractAmount) {
		this.contractAmount = contractAmount;
	}

	public BigDecimal getSettledAmount() {
		return settledAmount;
	}

	public void setSettledAmount(BigDecimal settledAmount) {
		this.settledAmount = settledAmount;
	}

	public Double getCollectedRatio() {
		return collectedRatio;
	}

	public void setCollectedRatio(Double collectedRatio) {
		this.collectedRatio = collectedRatio;
	}

	public Double getSettleRatio() {
		return settleRatio;
	}

	public void setSettleRatio(Double settleRatio) {
		this.settleRatio = settleRatio;
	}

	public String getCollectContractNos() {
		return collectContractNos;
	}

	public void setCollectContractNos(String collectContractNos) {
		this.collectContractNos = collectContractNos;
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

	@Override
	public void setCustomInfo(Map<?, ?> customInfo) {
		Map info = this.getCustomInfo();
		if (info != null && customInfo != null) {
			info.putAll(customInfo);
		} else if (customInfo != null) {
			super.setCustomInfo(customInfo);
		}
	}

	public void setEffective(Date date) {
		this.setEffectiveFrom(date);
		this.setEffectiveTo(date);
	}
}
