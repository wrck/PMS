package com.dp.plat.pms.springmvc.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dp.plat.pms.springmvc.entity.ProjectTask;

public class TaskVO extends ProjectTask {
	
	// 流程当前任务ID
	private String currentTaskId;
	private String eventKey;
	private String eventValue;
	private String projectIds;
	
	private String projectTypes;
	private String officeCodes;
	private String memberCode;
	
	public TaskVO() {
		super();
	}
	public TaskVO(Integer projectId, String projectType) {
		super(projectId, projectType);
	}
	public TaskVO(Integer projectId) {
		super(projectId);
	}
	
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	public String getEventValue() {
		return eventValue;
	}
	public void setEventValue(String eventValue) {
		this.eventValue = eventValue;
	}
	
	public String getProjectIds() {
		return projectIds;
	}
	public void setProjectIds(String projectIds) {
		this.projectIds = projectIds;
	}
	public void setEffective(Date date) {
		this.setEffectiveFrom(date);
		this.setEffectiveTo(date);
	}
	public String getProjectTypes() {
		return projectTypes;
	}
	public void setProjectTypes(String projectTypes) {
		this.projectTypes = projectTypes;
	}
	public String getOfficeCodes() {
		return officeCodes;
	}
	public void setOfficeCodes(String officeCodes) {
		this.officeCodes = officeCodes;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
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
	public void setCustomInfo(Map customInfo) {
		Map info = this.getCustomInfo();
		if (info != null && customInfo != null) {
			info.putAll(customInfo);
		} else if (customInfo != null) {
			super.setCustomInfo(customInfo);
		}
	}

}
