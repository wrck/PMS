package com.dp.plat.data.bean;

import java.io.File;
import java.util.Date;

public class ProjectDeliver {

	private Integer id;
	private String column010;
	private String column011;
	private String dataTypeCode;
	private String basicDataId;
	private String eventKey;
	private String dataTypeCodeSon;
	private String basicDataIdSon;
	private int isNeed;
	
	private String deliverKey;
	private String deliverValue;
	
	private int projectId;
	private String projectType;
	private Integer taskId;
	private String contractNo;
	private String deliverId;
	private String deliverableName;
	private String deliverablePath;
	private String deliverableType;
	private String uploadUser;
	private Date uploadTime;
	
	private String basicDataName;
	
	private File[] uploaddelivery;
	private String uploaddeliveryFileName;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getColumn010() {
		return column010;
	}
	public void setColumn010(String column010) {
		this.column010 = column010;
	}
	public String getColumn011() {
		return column011;
	}
	public void setColumn011(String column011) {
		this.column011 = column011;
	}
	public String getDataTypeCode() {
		return dataTypeCode;
	}
	public void setDataTypeCode(String dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}
	public String getBasicDataId() {
		return basicDataId;
	}
	public void setBasicDataId(String basicDataId) {
		this.basicDataId = basicDataId;
	}
	public String getDataTypeCodeSon() {
		return dataTypeCodeSon;
	}
	public void setDataTypeCodeSon(String dataTypeCodeSon) {
		this.dataTypeCodeSon = dataTypeCodeSon;
	}
	public String getBasicDataIdSon() {
		return basicDataIdSon;
	}
	public void setBasicDataIdSon(String basicDataIdSon) {
		this.basicDataIdSon = basicDataIdSon;
	}
	public int getIsNeed() {
		return isNeed;
	}
	public void setIsNeed(int isNeed) {
		this.isNeed = isNeed;
	}
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	public String getDeliverKey() {
		return deliverKey;
	}
	public void setDeliverKey(String deliverKey) {
		this.deliverKey = deliverKey;
	}
	public String getDeliverValue() {
		return deliverValue;
	}
	public void setDeliverValue(String deliverValue) {
		this.deliverValue = deliverValue;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getDeliverableName() {
		return deliverableName;
	}
	public void setDeliverableName(String deliverableName) {
		this.deliverableName = deliverableName;
	}
	public String getDeliverablePath() {
		return deliverablePath;
	}
	public void setDeliverablePath(String deliverablePath) {
		this.deliverablePath = deliverablePath;
	}
	public String getDeliverableType() {
		return deliverableType;
	}
	public void setDeliverableType(String deliverableType) {
		this.deliverableType = deliverableType;
	}
	public String getUploadUser() {
		return uploadUser;
	}
	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(String deliverId) {
		this.deliverId = deliverId;
	}
	public File[] getUploaddelivery() {
		return uploaddelivery;
	}
	public void setUploaddelivery(File[] uploaddelivery) {
		this.uploaddelivery = uploaddelivery;
	}
	public String getUploaddeliveryFileName() {
		return uploaddeliveryFileName;
	}
	public void setUploaddeliveryFileName(String uploaddeliveryFileName) {
		this.uploaddeliveryFileName = uploaddeliveryFileName;
	}
	public String getBasicDataName() {
		return basicDataName;
	}
	public void setBasicDataName(String basicDataName) {
		this.basicDataName = basicDataName;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
    public String getProjectType() {
        return projectType;
    }
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	
}
