package com.dp.plat.data.activity;

import com.dp.plat.data.bean.BaseCustomInfoBean;

public class ActivityBaseBean extends BaseCustomInfoBean {
	private String taskAssignee;
	private String taskAssigneeName;
	private String taskId;
	private String taskName;
	private String taskDefKey;//标记当前流程走到哪一步
	
	public String getTaskAssignee() {
		return taskAssignee;
	}

	public void setTaskAssignee(String taskAssignee) {
		this.taskAssignee = taskAssignee;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskAssigneeName() {
		return taskAssigneeName;
	}

	public void setTaskAssigneeName(String taskAssigneeName) {
		this.taskAssigneeName = taskAssigneeName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDefKey() {
		return taskDefKey;
	}

	public void setTaskDefKey(String taskDefKey) {
		this.taskDefKey = taskDefKey;
	}
	
}
