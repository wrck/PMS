package com.dp.plat.pms.springmvc.vo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.excel.converters.bigdecimal.BigDecimalStringConverter;
import com.dp.plat.core.converter.DateConverter;
import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SettlementVO2 extends DispatchSettlement {

	private Integer projectId;

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

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getSmsProjectCode() {
		return smsProjectCode != null ? smsProjectCode : (String) this.getCustomInfoByKey("smsProjectCode");
	}

	public void setSmsProjectCode(String smsProjectCode) {
		this.smsProjectCode = smsProjectCode;
		this.setCustomInfoByKey("smsProjectCode", smsProjectCode);
	}

	public Date getSmsSubmitTime() {
		if (smsSubmitTime != null) {
			return smsSubmitTime;
		}
		Object date = this.getCustomInfoByKey("smsSubmitTime");
		if (date instanceof String) {
			return new DateConverter().convert((String) date);
		} else if (date instanceof Long) {
			return new Date((long) date);
		}
		return (Date) date;
	}

	public void setSmsSubmitTime(Date date) {
		this.smsSubmitTime = date;
		this.setCustomInfoByKey("smsSubmitTime", date);
	}

	public String getSmsProjectAmount() {
		return smsProjectAmount != null ? smsProjectAmount : (String) this.getCustomInfoByKey("smsProjectAmount");
	}

	public void setSmsProjectAmount(String smsProjectAmount) {
		this.smsProjectAmount = smsProjectAmount;
		this.setCustomInfoByKey("smsProjectAmount", smsProjectAmount);
	}

	public String getSmsProjectName() {
		return smsProjectName != null ? smsProjectName : (String) this.getCustomInfoByKey("smsProjectName");
	}

	public void setSmsProjectName(String smsProjectName) {
		this.smsProjectName = smsProjectName;
		this.setCustomInfoByKey("smsProjectName", smsProjectName);
	}

	public String getSmsOrderExecNumber() {
		return smsOrderExecNumber != null ? smsOrderExecNumber : (String) this.getCustomInfoByKey("smsOrderExecNumber");
	}

	public void setSmsOrderExecNumber(String smsOrderExecNumber) {
		this.smsOrderExecNumber = smsOrderExecNumber;
		this.setCustomInfoByKey("smsOrderExecNumber", smsOrderExecNumber);
	}

	public String getContractNos() {
		return contractNos != null ? contractNos : (String) this.getCustomInfoByKey("contractNos");
	}

	public void setContractNos(String contractNos) {
		this.contractNos = contractNos;
		this.setCustomInfoByKey("contractNos", contractNos);
	}

	public BigDecimal getCollectedAmount() {
		if (collectedAmount != null) {
			return collectedAmount;
		}
		Object amount = this.getCustomInfoByKey("collectedAmount");
		if (amount instanceof String) {
			DecimalFormat decimalFormat = new DecimalFormat("##,##0.00");
			decimalFormat.setParseBigDecimal(true);
			try {
				return (BigDecimal) decimalFormat.parseObject((String) amount);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (amount instanceof Long) {
			return new BigDecimal((long) amount);
		}
		return (BigDecimal) amount;
	}

	public void setCollectedAmount(BigDecimal collectedAmount) {
		this.collectedAmount = collectedAmount;
		this.setCustomInfoByKey("collectedAmount", collectedAmount);
	}

	public BigDecimal getDeliveredAmount() {
		return (BigDecimal) this.getCustomInfoByKey("deliveredAmount");
	}

	public void setDeliveredAmount(BigDecimal deliveredAmount) {
		this.deliveredAmount = deliveredAmount;
		this.setCustomInfoByKey("deliveredAmount", deliveredAmount);
	}

	public BigDecimal getContractAmount() {
		return (BigDecimal) this.getCustomInfoByKey("contractAmount");
	}

	public void setContractAmount(BigDecimal contractAmount) {
		this.contractAmount = contractAmount;
		this.setCustomInfoByKey("contractAmount", contractAmount);
	}

	public BigDecimal getSettledAmount() {
		return (BigDecimal) this.getCustomInfoByKey("settledAmount");
	}

	public void setSettledAmount(BigDecimal settledAmount) {
		this.settledAmount = settledAmount;
		this.setCustomInfoByKey("settledAmount", settledAmount);
	}

	public Double getCollectedRatio() {
		Object ratio = this.getCustomInfoByKey("collectedRatio");
		return collectedRatio != null ? collectedRatio : (ratio != null ?  new Double((String) ratio) : null);
	}

	public void setCollectedRatio(Double collectedRatio) {
		this.collectedRatio = collectedRatio;
		this.setCustomInfoByKey("collectedRatio", collectedRatio);
	}

	public Double getSettledRatio() {
		Object ratio = this.getCustomInfoByKey("settledRatio");
		return settledRatio != null ? settledRatio : (ratio != null ?  new Double((String) ratio) : null);
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
