package com.dp.plat.pms.springmvc.vo;

import org.springframework.web.multipart.MultipartFile;

public class ProjectDeliver extends com.dp.plat.data.bean.ProjectDeliver {

	private Integer taskId;
	
	private Object ids;
	
	private MultipartFile[] uploadFiles;

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
	
}
