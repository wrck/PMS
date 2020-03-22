package com.dp.plat.data.bean;

import java.util.Date;
import java.util.List;

public class Notification {
	private int notifyId;
	private String notifySubject;
	private String notifyContent;
	private int projectId;
	private int notifyStateId;
	private String notifyObject;
	private int notifyState;
	private String createBy;
	private Date createTime;
	private Date checkTime;
	private List<Notification> notifyObjectList;
	
	private String projectName;
	private String officeName;
	private String programManager;
	
	
	public int getNotifyId() {
		return notifyId;
	}
	public void setNotifyId(int notifyId) {
		this.notifyId = notifyId;
	}
	public String getNotifySubject() {
		return notifySubject;
	}
	public void setNotifySubject(String notifySubject) {
		this.notifySubject = notifySubject;
	}
	public String getNotifyContent() {
		return notifyContent;
	}
	public void setNotifyContent(String notifyContent) {
		this.notifyContent = notifyContent;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getNotifyStateId() {
		return notifyStateId;
	}
	public void setNotifyStateId(int notifyStateId) {
		this.notifyStateId = notifyStateId;
	}
	public String getNotifyObject() {
		return notifyObject;
	}
	public void setNotifyObject(String notifyObject) {
		this.notifyObject = notifyObject;
	}
	public int getNotifyState() {
		return notifyState;
	}
	public void setNotifyState(int notifyState) {
		this.notifyState = notifyState;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public List<Notification> getNotifyObjectList() {
		return notifyObjectList;
	}
	public void setNotifyObjectList(List<Notification> notifyObjectList) {
		this.notifyObjectList = notifyObjectList;
	}
	public Date getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public String getProgramManager() {
		return programManager;
	}
	public void setProgramManager(String programManager) {
		this.programManager = programManager;
	}
	
	
}
