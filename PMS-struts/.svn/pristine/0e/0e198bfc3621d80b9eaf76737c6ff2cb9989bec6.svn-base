package com.dp.plat.data.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dp.plat.param.FileParam;

/**
 * 售前测试项目计划
 * @author admin
 *
 */
public class PresalesTask extends BaseBean{
	private int taskId;
	private int projectId;
	private int projectType;
	private String taskTypeCode;
	private String taskTypeId;
	private String taskName;
	private int taskState;
	private String taskStateName;
	private Date eventActualFinishDate;
	private String deliverFileIds;
	private String remark;
	
	//非数据库字段
	private Map<Integer, String> fileMap;
	private List<FileParam> fileParams; //对应confirmFileIds下的文件具体信息
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getProjectType() {
		return projectType;
	}
	public void setProjectType(int projectType) {
		this.projectType = projectType;
	}
	public String getTaskTypeCode() {
		return taskTypeCode;
	}
	public void setTaskTypeCode(String taskTypeCode) {
		this.taskTypeCode = taskTypeCode;
	}
	public String getTaskTypeId() {
		return taskTypeId;
	}
	public void setTaskTypeId(String taskTypeId) {
		this.taskTypeId = taskTypeId;
	}
	public Date getEventActualFinishDate() {
		return eventActualFinishDate;
	}
	public void setEventActualFinishDate(Date eventActualFinishDate) {
		this.eventActualFinishDate = eventActualFinishDate;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public int getTaskState() {
		return taskState;
	}
	public void setTaskState(int taskState) {
		this.taskState = taskState;
	}
	public String getTaskStateName() {
		return taskStateName;
	}
	public void setTaskStateName(String taskStateName) {
		this.taskStateName = taskStateName;
	}
	public String getDeliverFileIds() {
		return deliverFileIds;
	}
	public void setDeliverFileIds(String deliverFileIds) {
		this.deliverFileIds = deliverFileIds;
	}
	public Map<Integer, String> getFileMap() {
		return fileMap;
	}
	public void setFileMap(Map<Integer, String> fileMap) {
		this.fileMap = fileMap;
	}
	public String getEventActualFinishDateStr() {
		if(eventActualFinishDate != null){
			return sdf.format(eventActualFinishDate);
		}
		return null;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
    public List<FileParam> getFileParams() {
        return fileParams;
    }
    public void setFileParams(List<FileParam> fileParams) {
        this.fileParams = fileParams;
    }
	
}
