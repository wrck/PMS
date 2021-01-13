package com.dp.plat.data.bean;

import java.util.List;

/**
 * 回访问卷信息
 * @author admin
 *
 */
public class CallBackQuesnaire extends BaseBean{
	private int id ;
	private int callBackId;
	private String taskId;//某个activity任务中的taskId
	private int quesnaireId;
	private int quesnaireVersion;
	private int quesnaireState;
	//非数据库字段
	private List<String> quesResultMarkList;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCallBackId() {
		return callBackId;
	}
	public void setCallBackId(int callBackId) {
		this.callBackId = callBackId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public int getQuesnaireVersion() {
		return quesnaireVersion;
	}
	public void setQuesnaireVersion(int quesnaireVersion) {
		this.quesnaireVersion = quesnaireVersion;
	}
	public int getQuesnaireState() {
		return quesnaireState;
	}
	public void setQuesnaireState(int quesnaireState) {
		this.quesnaireState = quesnaireState;
	}
	public int getQuesnaireId() {
		return quesnaireId;
	}
	public void setQuesnaireId(int quesnaireId) {
		this.quesnaireId = quesnaireId;
	}
	public List<String> getQuesResultMarkList() {
		return quesResultMarkList;
	}
	public void setQuesResultMarkList(List<String> quesResultMarkList) {
		this.quesResultMarkList = quesResultMarkList;
	}
}
