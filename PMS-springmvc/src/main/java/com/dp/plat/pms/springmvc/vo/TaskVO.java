package com.dp.plat.pms.springmvc.vo;

import java.util.Date;

import com.dp.plat.pms.springmvc.entity.ProjectTask;

public class TaskVO extends ProjectTask {
	
	private String eventKey;
	private String eventValue;
	
	
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
	
	public void setEffective(Date date) {
		this.setEffectiveFrom(date);
		this.setEffectiveTo(date);
	}
}
