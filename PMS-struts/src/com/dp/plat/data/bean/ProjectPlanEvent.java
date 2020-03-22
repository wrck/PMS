package com.dp.plat.data.bean;

import java.util.Date;

public class ProjectPlanEvent {

	private String dataTypeCode;
	private String basicDataId;
	private String eventKey;//dataTypeCode-basicDataId
	private String eventValue;
	private Date eventPlanHappenDate;
	private Date eventActualFinishDate;
	
	private String column010;
	private String column011;
	public String getDataTypeCode() {
		return dataTypeCode;
	}
	public void setDataTypeCode(String dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}
	public String getBasicDataId() {
		return basicDataId;
	}
	public void setBasicDataId(String basicDataId) {
		this.basicDataId = basicDataId;
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
	public String getColumn010() {
		return column010;
	}
	public void setColumn010(String column010) {
		this.column010 = column010;
	}
	public String getColumn011() {
		return column011;
	}
	public void setColumn011(String column011) {
		this.column011 = column011;
	}
	public Date getEventPlanHappenDate() {
		return eventPlanHappenDate;
	}
	public void setEventPlanHappenDate(Date eventPlanHappenDate) {
		this.eventPlanHappenDate = eventPlanHappenDate;
	}
	public Date getEventActualFinishDate() {
		return eventActualFinishDate;
	}
	public void setEventActualFinishDate(Date eventActualFinishDate) {
		this.eventActualFinishDate = eventActualFinishDate;
	}
	
}
