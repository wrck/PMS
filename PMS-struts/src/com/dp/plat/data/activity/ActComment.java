package com.dp.plat.data.activity;

import java.util.Date;

/**
 * activity自定义实现的意见表
 * @author admin
 *
 */
public class ActComment {
	private int id;
	private int objId;
	private String procdefKey;
	private String taskId;
	private String instId;
	private String assignee;
	private Date assigneeTime;
	private int result;//-1 驳回 1同意 0发起申请
	private String message;
	
	//非数据库字段
	private String resultName;
	private String assigneeName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getProcdefKey() {
		return procdefKey;
	}
	public void setProcdefKey(String procdefKey) {
		this.procdefKey = procdefKey;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getInstId() {
		return instId;
	}
	public void setInstId(String instId) {
		this.instId = instId;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Date getAssigneeTime() {
		return assigneeTime;
	}
	public void setAssigneeTime(Date assigneeTime) {
		this.assigneeTime = assigneeTime;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getResultName() {
		return resultName;
	}
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}
	
	public String getAssigneeName() {
		return assigneeName;
	}
	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}
	public ActComment(int objId, String procdefKey, String taskId,
			String instId, String assignee, Date assigneeTime, int result,
			String message) {
		super();
		this.objId = objId;
		this.procdefKey = procdefKey;
		this.taskId = taskId;
		this.instId = instId;
		this.assignee = assignee;
		this.assigneeTime = assigneeTime;
		this.result = result;
		this.message = message;
	}
	public ActComment() {
	}
}
