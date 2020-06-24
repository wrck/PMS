package com.dp.plat.pms.springmvc.vo;

import java.util.Date;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ProjectDeliver extends com.dp.plat.data.bean.ProjectDeliver {

	private Integer taskId;
	
	private Object ids;
	
	private MultipartFile[] uploadFiles;
	
	@JsonSerialize(using=JsonSerializer.class)
	private Date uploadTime;
	
	private Integer orgId;
	
	private String taskName;
	
	private Map taskCustomInfo;

	public MultipartFile[] getUploadFiles() {
		return uploadFiles;
	}

	public void setUploadFiles(MultipartFile[] uploadFiles) {
		this.uploadFiles = uploadFiles;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}


	public Object getIds() {
		return ids;
	}

	public void setIds(Object ids) {
		this.ids = ids;
	}
	
	public Integer getOrgId() {
		if (this.orgId == null) {
			return UserContext.getOrgId();
		}
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Map getTaskCustomInfo() {
		return taskCustomInfo;
	}

	public void setTaskCustomInfo(Map taskCustomInfo) {
		this.taskCustomInfo = taskCustomInfo;
	}

}
