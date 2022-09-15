package com.dp.plat.pms.springmvc.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dp.plat.core.converter.DateConverter;
import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ProjectVO extends ProjectHeader {
	
	// 查询辅助参数
	private String projectIds;
	private String projectTypes;
	private String contractNos;
	private String projectTypeName;
	private Boolean matchCollectMoney;
	
	private String collectContractNos;
	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal collectedAmount;
	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal deliveredAmount;
	@JsonSerialize(using = JsonSerializer.class)
	private BigDecimal contractAmount;
	@JsonSerialize(using = JsonSerializer.class)
	private Double collectedRatio;
	
	// SMS项目相关信息
	/**
	 *  项目提交时间
	 */
	@JsonSerialize(using = JsonSerializer.class)
	private Date smsSubmitTime;
	/**
	 *  项目投标时间
	 */
	@JsonSerialize(using = JsonSerializer.class)
	private Date smsPredBidDate;
	
	// 项目投标时间，查询参数
	private Date smsPredBidStartDate;
	private Date smsPredBidEndDate;
	
	// SMS项目相关信息
	private String smsProjectAmount;
	private String smsAfProjectAmount;
	private String smsProjectName;
	private String smsOrderExecNumber;
	
	// 安服先行借货项目属性
	private String customerPerson;
	private String customerTel;
	private String customerAddress;
	private String afxxReason;
	private String requireInDate;
	private String pspm;
	private String pspmName;
	private String salesMenTel;
	private String decPath;
	
	public ProjectVO() {
		super();
	}
	
	public ProjectVO(Integer projectId) {
		super();
		this.setProjectId(projectId);
	}

	public String getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(String projectIds) {
		this.projectIds = projectIds;
	}
	
	public String getProjectTypes() {
		return projectTypes;
	}

	public void setProjectTypes(String projectTypes) {
		this.projectTypes = projectTypes;
	}

	public String getContractNos() {
		return contractNos;
	}

	public void setContractNos(String contractNos) {
		this.contractNos = contractNos;
	}
	
	public String getProjectTypeName() {
		return projectTypeName;
	}

	public void setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName;
	}
	
	public Boolean getMatchCollectMoney() {
		return matchCollectMoney;
	}

	public void setMatchCollectMoney(Boolean matchCollectMoney) {
		this.matchCollectMoney = matchCollectMoney;
	}
	
	public String getCollectContractNos() {
		return collectContractNos;
	}

	public void setCollectContractNos(String collectContractNos) {
		this.collectContractNos = collectContractNos;
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

	public Double getCollectedRatio() {
		return collectedRatio;
	}

	public void setCollectedRatio(Double collectedRatio) {
		this.collectedRatio = collectedRatio;
	}

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
	
	/**
	 * 获取投标时间
	 * @return
	 */
	@JsonSerialize(using = JsonSerializer.class)
	public Date getSmsPredBidDate() {
		Object object = getCustomInfoByKey("smsPredBidDate");
		if (object instanceof String) {
			smsPredBidDate = DateConverter.covert((String) object);
		} else if (object instanceof Date){
			smsPredBidDate = (Date) object;
		}
		return smsPredBidDate;
	}

	/**
	 * 设置投标时间
	 * @param date
	 */
	public void setSmsPredBidDate(Date date) {
		this.smsPredBidDate = date;
		this.setCustomInfoByKey("smsPredBidDate", date);
	}
	
	public Date getSmsPredBidStartDate() {
		return smsPredBidStartDate;
	}

	public void setSmsPredBidStartDate(Date smsPredBidStartDate) {
		this.smsPredBidStartDate = smsPredBidStartDate;
	}

	public Date getSmsPredBidEndDate() {
		return smsPredBidEndDate;
	}

	public void setSmsPredBidEndDate(Date smsPredBidEndDate) {
		this.smsPredBidEndDate = smsPredBidEndDate;
	}

	public String getSmsProjectAmount() {
		return (String) getCustomInfoByKey("smsProjectAmount");
	}

	public void setSmsProjectAmount(String smsProjectAmount) {
		this.smsProjectAmount = smsProjectAmount;
		this.setCustomInfoByKey("smsProjectAmount", smsProjectAmount);
	}
	
	public String getSmsAfProjectAmount() {
		return (String) getCustomInfoByKey("smsAfProjectAmount");
	}

	public void setSmsAfProjectAmount(String smsAfProjectAmount) {
		this.smsAfProjectAmount = smsAfProjectAmount;
		this.setCustomInfoByKey("smsAfProjectAmount", smsAfProjectAmount);
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

	@Override
	public String getSalesManCode() {
		return (String) getCustomInfoByKey("salesManCode");
//		return super.getSalesManCode();
	}

	@Override
	public void setSalesManCode(String salesManCode) {
		super.setSalesManCode(salesManCode);
		setCustomInfoByKey("salesManCode", salesManCode);
	}
	
	@Override
	public String getSalesManName() {
		return (String) getCustomInfoByKey("salesManName");
//		return super.getSalesManName();
	}

	@Override
	public void setSalesManName(String salesManName) {
		super.setSalesManName(salesManName);
		setCustomInfoByKey("salesManName", salesManName);
	}
	
	@Override
	public String getServiceManagerCode() {
		return (String) getCustomInfoByKey("serviceManagerCode");
//		return super.getServiceManagerCode();
	}

	@Override
	public void setServiceManagerCode(String serviceManagerCode) {
		super.setServiceManagerCode(serviceManagerCode);
		setCustomInfoByKey("serviceManagerCode", serviceManagerCode);
	}

	@Override
	public String getProgramManagerCode() {
		return (String) getCustomInfoByKey("programManagerCode");
//		return super.getProgramManagerCode();
	}

	@Override
	public void setProgramManagerCode(String programManagerCode) {
		super.setProgramManagerCode(programManagerCode);
		setCustomInfoByKey("programManagerCode", programManagerCode);
	}
	
	

	@Override
	public String getServiceManagerCodeforjson() {
		return (String) getCustomInfoByKey("serviceManagerCodeforjson");
//		return super.getServiceManagerCodeforjson();
	}

	@Override
	public String getProgramManagerCodeforjson() {
		return (String) getCustomInfoByKey("programManagerCodeforjson");
//		return super.getProgramManagerCodeforjson();
	}

	@Override
	public String getProgramManagerCodeforjsonB() {
		return (String) getCustomInfoByKey("programManagerCodeforjsonB");
//		return super.getProgramManagerCodeforjsonB();
	}
	

	@Override
	public void setServiceManagerCodeforjson(String serviceManagerCodeforjson) {
		super.setServiceManagerCodeforjson(serviceManagerCodeforjson);
		setCustomInfoByKey("serviceManagerCodeforjson", serviceManagerCodeforjson);
	}

	@Override
	public void setProgramManagerCodeforjson(String programManagerCodeforjson) {
		super.setProgramManagerCodeforjson(programManagerCodeforjson);
		setCustomInfoByKey("programManagerCodeforjson", programManagerCodeforjson);
	}

	@Override
	public void setProgramManagerCodeforjsonB(String programManagerCodeforjsonB) {
		super.setProgramManagerCodeforjsonB(programManagerCodeforjsonB);
		setCustomInfoByKey("programManagerCodeforjsonB", programManagerCodeforjsonB);
	}

	@Override
	public String getProgramManagerCodeB() {
		return (String) getCustomInfoByKey("programManagerCodeB");
//		return super.getProgramManagerCodeB();
	}

	@Override
	public void setProgramManagerCodeB(String programManagerCodeB) {
		super.setProgramManagerCodeB(programManagerCodeB);
		setCustomInfoByKey("programManagerCodeB", programManagerCodeB);
	}

	public Object getCustomInfoByKey(String key) {
		Map<?, ?> customInfo = getCustomInfo();
		if (customInfo != null && !customInfo.isEmpty()) {
			return customInfo.get(key);
		}
		return null;
	}
	
	public Object getCustomInfoByKey(String key, Object defaultValue) {
		Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
		if (customInfo != null && !customInfo.isEmpty()) {
			return customInfo.getOrDefault(key, defaultValue);
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

	@Override
	public void setCustomConfig(Map<?, ?> customConfig) {
		Map config = this.getCustomConfig();
		if (config != null && customConfig != null) {
			config.putAll(customConfig);
		} else if (customConfig != null) {
			super.setCustomConfig(customConfig);
		}
	}
	
	public void setEffective(Date date) {
		this.setEffectiveFrom(date);
		this.setEffectiveTo(date);
	}

}
