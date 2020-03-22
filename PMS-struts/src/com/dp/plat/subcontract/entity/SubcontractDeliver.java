package com.dp.plat.subcontract.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SubcontractDeliver {
	private Integer id;

	// 转包项目ID
	private Integer subcontractId;

	// 交付件名称
	private String fileName;

	// 交付件路径
	private String filePath;

	// 交付件类型,0:用服交付合同，1：用服服务单，2：工程合同
	private String type;

	// 上传者
	private String uploadBy;

	// 上传时间
	@JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
	private Date uploadTime;

	@JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
	private Date effectiveFrom;

	@JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
	private Date effectiveTo;

	public SubcontractDeliver() {
	}

	/**
	 * @param subcontractId
	 */
	public SubcontractDeliver(Integer subcontractId) {
		this.subcontractId = subcontractId;
	}

	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 获取转包项目ID
	 *
	 * @return subcontractId - 转包项目ID
	 */
	public Integer getSubcontractId() {
		return subcontractId;
	}

	/**
	 * 设置转包项目ID
	 *
	 * @param subcontractId
	 *            转包项目ID
	 */
	public void setSubcontractId(Integer subcontractId) {
		this.subcontractId = subcontractId;
	}

	/**
	 * 获取交付件名称
	 *
	 * @return fileName - 交付件名称
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置交付件名称
	 *
	 * @param fileName
	 *            交付件名称
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取交付件路径
	 *
	 * @return filePath - 交付件路径
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 设置交付件路径
	 *
	 * @param filePath
	 *            交付件路径
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 获取交付件类型,0:用服交付合同，1：用服服务单，2：工程合同
	 *
	 * @return type - 交付件类型,0:用服交付合同，1：用服服务单，2：工程合同
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置交付件类型,0:用服交付合同，1：用服服务单，2：工程合同
	 *
	 * @param type
	 *            交付件类型,0:用服交付合同，1：用服服务单，2：工程合同
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取上传者
	 *
	 * @return uploadBy - 上传者
	 */
	public String getUploadBy() {
		return uploadBy;
	}

	/**
	 * 设置上传者
	 *
	 * @param uploadBy
	 *            上传者
	 */
	public void setUploadBy(String uploadBy) {
		this.uploadBy = uploadBy;
	}

	/**
	 * 获取上传时间
	 *
	 * @return uploadTime - 上传时间
	 */
	public Date getUploadTime() {
		return uploadTime;
	}

	/**
	 * 设置上传时间
	 *
	 * @param uploadTime
	 *            上传时间
	 */
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	/**
	 * @return effectiveFrom
	 */
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	/**
	 * @param effectiveFrom
	 */
	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	/**
	 * @return effectiveTo
	 */
	public Date getEffectiveTo() {
		return effectiveTo;
	}

	/**
	 * @param effectiveTo
	 */
	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}
}
