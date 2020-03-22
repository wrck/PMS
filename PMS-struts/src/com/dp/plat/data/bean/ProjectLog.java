package com.dp.plat.data.bean;

import java.util.Date;

/**
 * 对具体项目的操作日志
 * @author admin
 *
 */
public class ProjectLog {
	private int id;
	private int projectId;//项目ID
	private String handleName;//操作名称
	private String handleDesc;//操作描述
	private String handleUser;//操作用户
	private Date handStartTime;//操作开始时间
	private Date handEndTime;//操作结束时间
	private int handleState;//操作状态
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getHandleName() {
		return handleName;
	}
	public void setHandleName(String handleName) {
		this.handleName = handleName;
	}
	public String getHandleDesc() {
		return handleDesc;
	}
	public void setHandleDesc(String handleDesc) {
		this.handleDesc = handleDesc;
	}
	public String getHandleUser() {
		return handleUser;
	}
	public void setHandleUser(String handleUser) {
		this.handleUser = handleUser;
	}
	public Date getHandStartTime() {
		return handStartTime;
	}
	public void setHandStartTime(Date handStartTime) {
		this.handStartTime = handStartTime;
	}
	public Date getHandEndTime() {
		return handEndTime;
	}
	public void setHandEndTime(Date handEndTime) {
		this.handEndTime = handEndTime;
	}
	public int getHandleState() {
		return handleState;
	}
	public void setHandleState(int handleState) {
		this.handleState = handleState;
	}
	
	
	
}
