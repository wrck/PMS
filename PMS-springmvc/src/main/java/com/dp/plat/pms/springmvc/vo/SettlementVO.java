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

	private static final long serialVersionUID = -1876732175283061850L;
	
    private Integer projectId;
	private String officeCodes;
	private String projectTypes;
	private String memberCode;

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
	private Double settledRatio;
	
	private DispatchProject dispatch;
	private Boolean dispatched;
	private Integer checkCollectAndSettle;
	
	private String dateType;
	private Date dateStartTime;
	private Date dateEndTime;

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

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
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

	public Double getSettledRatio() {
		return settledRatio;
	}

	public void setSettledRatio(Double settledRatio) {
		this.settledRatio = settledRatio;
		this.setCustomInfoByKey("settledRatio", settledRatio);
	}
	
	public DispatchProject getDispatch() {
		return dispatch;
	}

	public void setDispatch(DispatchProject dispatch) {
		this.dispatch = dispatch;
	}
	
	public Boolean getDispatched() {
		return dispatched;
	}

	public void setDispatched(Boolean dispatched) {
		this.dispatched = dispatched;
	}

	public Integer getCheckCollectAndSettle() {
        return checkCollectAndSettle;
    }

    public void setCheckCollectAndSettle(Integer checkCollectAndSettle) {
        this.checkCollectAndSettle = checkCollectAndSettle;
    }
    
    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public Date getDateStartTime() {
        return dateStartTime;
    }

    public void setDateStartTime(Date dateStartTime) {
        this.dateStartTime = dateStartTime;
    }

    public Date getDateEndTime() {
        return dateEndTime;
    }

    public void setDateEndTime(Date dateEndTime) {
        this.dateEndTime = dateEndTime;
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
			customInfo =  (Map<String, Object>) this.getCustomInfo();
		}
		customInfo.put(key, value);
	}

}
