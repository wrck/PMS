package com.dp.plat.data.bean;

import java.util.Date;
import java.util.List;

/**
 * 针对项目的总部批示
 * @author admin
 *
 */
public class Instruction {
	private  int id ;
	private int projectId;
	private String instructionsInfo;
	private Date instructionsTime;
	private String instructionsUser;
	private int dataType;
	private int instructionsId;
	private String createBy;
	private Date createTime;
	private List<Instruction> feedbackList;//反馈信息
	
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
	public String getInstructionsInfo() {
		return instructionsInfo;
	}
	public void setInstructionsInfo(String instructionsInfo) {
		this.instructionsInfo = instructionsInfo;
	}
	public Date getInstructionsTime() {
		return instructionsTime;
	}
	public void setInstructionsTime(Date instructionsTime) {
		this.instructionsTime = instructionsTime;
	}
	public String getInstructionsUser() {
		return instructionsUser;
	}
	public void setInstructionsUser(String instructionsUser) {
		this.instructionsUser = instructionsUser;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public int getInstructionsId() {
		return instructionsId;
	}
	public void setInstructionsId(int instructionsId) {
		this.instructionsId = instructionsId;
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
	public List<Instruction> getFeedbackList() {
		return feedbackList;
	}
	public void setFeedbackList(List<Instruction> feedbackList) {
		this.feedbackList = feedbackList;
	}
}
