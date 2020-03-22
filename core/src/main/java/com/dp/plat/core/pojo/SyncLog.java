package com.dp.plat.core.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author w02611
 *
 */
/**
 * @author w02611
 *
 */
public class SyncLog {
	private Integer id;

	private String targetMethod;

	private String tableObject;

	private String dataFrom;

	private String dataTo;

	private String syncParams;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date syncStartTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date syncEndTime;

	private Boolean isSuccess;

	private Integer dataCount;

	private Short syncType;

	private String exception;

	public SyncLog() {
	}

	/**
	 * @param targetMethod
	 * @param tableObject
	 *            <br>
	 * @param syncStartTime
	 *            = new Date()
	 */
	public SyncLog(String targetMethod, String tableObject) {
		this.targetMethod = targetMethod;
		this.tableObject = tableObject;
		this.syncStartTime = new Date();
	}

	/**
	 * @param targetMethod
	 * @param tableObject
	 * @param syncType
	 *            <br>
	 * @param syncStartTime
	 *            = new Date()
	 */
	public SyncLog(String targetMethod, String tableObject, Short syncType) {
		this.targetMethod = targetMethod;
		this.tableObject = tableObject;
		this.syncType = syncType;
		this.syncStartTime = new Date();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}

	public String getTableObject() {
		return tableObject;
	}

	public void setTableObject(String tableObject) {
		this.tableObject = tableObject;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

	public String getDataTo() {
		return dataTo;
	}

	public void setDataTo(String dataTo) {
		this.dataTo = dataTo;
	}

	public String getSyncParams() {
		return syncParams;
	}

	public void setSyncParams(String syncParams) {
		this.syncParams = syncParams;
	}

	public Date getSyncStartTime() {
		return syncStartTime;
	}

	public void setSyncStartTime(Date syncStartTime) {
		this.syncStartTime = syncStartTime;
	}

	public Date getSyncEndTime() {
		return syncEndTime;
	}

	public void setSyncEndTime(Date syncEndTime) {
		this.syncEndTime = syncEndTime;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	public Short getSyncType() {
		return syncType;
	}

	public void setSyncType(Short syncType) {
		this.syncType = syncType;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

}