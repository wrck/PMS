package com.dp.plat.data.bean;

import java.util.Date;

public class ProjectTask {

	private Integer taskId;
	private Integer projectId;
	private String contractNo;
	private String contractNoStr;
	private String taskTypeId;
	private String taskTypeCode;
	private String eventKey;
	private String eventKeyStr;
	private String eventValue;
	private String eventValueStr;
	private Date eventPlanHappenDate;
	private String eventPlanHappenDateStr;
	private Date eventPlanHappenDateENG;
	private String eventPlanHappenDateENGStr;
	private Date eventActualFinishDate;
	private String eventActualFinishDateStr;
	private Date createTime;
	private String createBy;
	private Date updateTime;
	private String updateBy;
	private Date effectiveFrom;
	private Date effectiveTo;
	private String visibleFlag;
	private String projectName;
	
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public Integer getProjectId() {
		if (projectId == null) {
			return 0;
		}
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public String getTaskTypeId() {
		return taskTypeId;
	}
	public void setTaskTypeId(String taskTypeId) {
		this.taskTypeId = taskTypeId;
	}
	public String getTaskTypeCode() {
		return taskTypeCode;
	}
	public void setTaskTypeCode(String taskTypeCode) {
		this.taskTypeCode = taskTypeCode;
	}
	public Date getEventPlanHappenDate() {
		return eventPlanHappenDate;
	}
	public void setEventPlanHappenDate(Date eventPlanHappenDate) {
		this.eventPlanHappenDate = eventPlanHappenDate;
	}
	public Date getEventPlanHappenDateENG() {
		return eventPlanHappenDateENG;
	}
	public void setEventPlanHappenDateENG(Date eventPlanHappenDateENG) {
		this.eventPlanHappenDateENG = eventPlanHappenDateENG;
	}
	public Date getEventActualFinishDate() {
		return eventActualFinishDate;
	}
	public void setEventActualFinishDate(Date eventActualFinishDate) {
		this.eventActualFinishDate = eventActualFinishDate;
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
	public String getEventPlanHappenDateENGStr() {
		return eventPlanHappenDateENGStr;
	}
	public void setEventPlanHappenDateENGStr(String eventPlanHappenDateENGStr) {
		this.eventPlanHappenDateENGStr = eventPlanHappenDateENGStr;
	}
	public String getEventActualFinishDateStr() {
		return eventActualFinishDateStr;
	}
	public void setEventActualFinishDateStr(String eventActualFinishDateStr) {
		this.eventActualFinishDateStr = eventActualFinishDateStr;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getEventKeyStr() {
		return eventKeyStr;
	}
	public void setEventKeyStr(String eventKeyStr) {
		this.eventKeyStr = eventKeyStr;
	}
	public String getEventValueStr() {
		return eventValueStr;
	}
	public void setEventValueStr(String eventValueStr) {
		this.eventValueStr = eventValueStr;
	}
	public String getEventPlanHappenDateStr() {
		return eventPlanHappenDateStr;
	}
	public void setEventPlanHappenDateStr(String eventPlanHappenDateStr) {
		this.eventPlanHappenDateStr = eventPlanHappenDateStr;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getContractNoStr() {
		return contractNoStr;
	}
	public void setContractNoStr(String contractNoStr) {
		this.contractNoStr = contractNoStr;
	}
	public String getVisibleFlag() {
		return visibleFlag;
	}
	public void setVisibleFlag(String visibleFlag) {
		this.visibleFlag = visibleFlag;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
