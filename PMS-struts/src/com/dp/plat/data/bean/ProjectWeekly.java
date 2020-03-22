package com.dp.plat.data.bean;

import java.util.Date;

public class ProjectWeekly {
	private int weeklyId;
	private int projectId;
	private String currentTask;
	private Date taskStartTime;
	private Date taskEndTime;
	private String taskDeviation;
	private String remark;
	private Date createTime;
	private String createBy;
	private Date updateTime;
	private String updateBy;
	private Date weeklyStartTime;
	private Date weeklyEndTime;
	private int weeklyState;
	private String weeklyStateName;
	public int getWeeklyId() {
		return weeklyId;
	}
	public void setWeeklyId(int weeklyId) {
		this.weeklyId = weeklyId;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getCurrentTask() {
		return currentTask;
	}
	public void setCurrentTask(String currentTask) {
		this.currentTask = currentTask;
	}
	public Date getTaskStartTime() {
		return taskStartTime;
	}
	public void setTaskStartTime(Date taskStartTime) {
		this.taskStartTime = taskStartTime;
	}
	public Date getTaskEndTime() {
		return taskEndTime;
	}
	public void setTaskEndTime(Date taskEndTime) {
		this.taskEndTime = taskEndTime;
	}
	public String getTaskDeviation() {
		return taskDeviation;
	}
	public void setTaskDeviation(String taskDeviation) {
		this.taskDeviation = taskDeviation;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Date getWeeklyStartTime() {
		return weeklyStartTime;
	}
	public void setWeeklyStartTime(Date weeklyStartTime) {
		this.weeklyStartTime = weeklyStartTime;
	}
	public Date getWeeklyEndTime() {
		return weeklyEndTime;
	}
	public void setWeeklyEndTime(Date weeklyEndTime) {
		this.weeklyEndTime = weeklyEndTime;
	}
	public int getWeeklyState() {
		return weeklyState;
	}
	public void setWeeklyState(int weeklyState) {
		this.weeklyState = weeklyState;
	}
	public String getWeeklyStateName() {
		return weeklyStateName;
	}
	public void setWeeklyStateName(String weeklyStateName) {
		this.weeklyStateName = weeklyStateName;
	}
	
}
