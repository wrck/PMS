package com.dp.plat.pms.springmvc.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dp.plat.pms.springmvc.entity.DispatchSettlement;

public class SettlementVO extends DispatchSettlement {

	private String smsProjectCode;
	private Date smsSubmitTime;
	private String smsProjectAmount;
	private String smsProjectName;
	private String smsOrderExecNumber;
	private String contractNos;
	private String receiveAmount;
	private String receiveRatio;
	
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

	public String getReceiveAmount() {
		return receiveAmount;
	}

	public void setReceiveAmount(String receiveAmount) {
		this.receiveAmount = receiveAmount;
		this.setCustomInfoByKey("receiveAmount", receiveAmount);
	}

	public String getReceiveRatio() {
		return receiveRatio;
	}

	public void setReceiveRatio(String receiveRatio) {
		this.receiveRatio = receiveRatio;
		this.setCustomInfoByKey("receiveRatio", receiveRatio);
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
