package com.dp.plat.pms.springmvc.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dp.plat.core.converter.DateConverter;
import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.core.util.DateUtil;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ProjectVO extends ProjectHeader {
	
	@JsonSerialize(using = JsonSerializer.class)
	private Date smsSubmitTime;
	private String smsProjectAmount;
	private String smsProjectName;
	private String smsOrderExecNumber;
	
	public String getSmsProjectCode() {
		return (String) getCustomInfoByKey("smsProjectCode");
	}

	public void setSmsProjectCode(String smsProjectCode) {
		super.setSmsProjectCode(smsProjectCode);
		this.setCustomInfoByKey("smsProjectCode", smsProjectCode);
	}

	@JsonSerialize(using = JsonSerializer.class)
	public Date getSmsSubmitTime() {
		Object object = getCustomInfoByKey("smsSubmitTime");
		if (object instanceof String) {
			smsSubmitTime = DateConverter.covert((String) object);
		} else if (object instanceof Date){
			smsSubmitTime = (Date) object;
		}
		return smsSubmitTime;
	}

	public void setSmsSubmitTime(Date date) {
		this.smsSubmitTime = date;
		this.setCustomInfoByKey("smsSubmitTime", date);
	}

	public String getSmsProjectAmount() {
		return (String) getCustomInfoByKey("smsProjectAmount");
	}

	public void setSmsProjectAmount(String smsProjectAmount) {
		this.smsProjectAmount = smsProjectAmount;
		this.setCustomInfoByKey("smsProjectAmount", smsProjectAmount);
	}

	public String getSmsProjectName() {
		return (String) getCustomInfoByKey("smsProjectName");
	}

	public void setSmsProjectName(String smsProjectName) {
		this.smsProjectName = smsProjectName;
		this.setCustomInfoByKey("smsProjectName", smsProjectName);
	}

	public String getSmsOrderExecNumber() {
		return (String) getCustomInfoByKey("smsOrderExecNumber");
	}

	public void setSmsOrderExecNumber(String smsOrderExecNumber) {
		this.smsOrderExecNumber = smsOrderExecNumber;
		this.setCustomInfoByKey("smsOrderExecNumber", smsOrderExecNumber);
	}
	
	public String getContractNo() {
		return (String) getCustomInfoByKey("contractNo");
	}

	public void setContractNo(String contractNo) {
		super.setContractNo(contractNo);
		setCustomInfoByKey("contractNo", contractNo);
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
