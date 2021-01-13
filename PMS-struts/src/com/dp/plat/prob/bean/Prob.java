package com.dp.plat.prob.bean;

import java.util.Date;

/**
 * 技术公告信息
 * 
 * @author j01441
 *
 */
public class Prob {
	private int probId;// 主键
	private String probNum;//
	private String watch;// 跟踪
	private String watchName;// 名称
	private String theme;// 主题
	private String desc;// 技术公告描述
	private String solution;// 解决方案
	private String status;// 状态
	private String statusName;// 状态名称
	private Date startdate;// 开始日期
	private Date duedate;// 计划完成日期
	private String attachmentNames;// 文件名称
	private String attachments;// 文件
	private String priority;// 严重级别
	private String priorityName;// 级别名称
	private String affectedVersion;// 影响的版本
	private String productType;// 产品类型
	private String trackingUser;// 跟踪用户
	private String trackingUsername;// 跟踪用户名称
	private String createBy;
	private Date createTime;
	private String updateBy;
	private Date updateTime;
	private String effectiveFrom;
	private String effectiveTo;
	private String remark;
	private int visibleRange;// 可见范围
	private String reader;
	private int readStatus;

	public int getProbId() {
		return probId;
	}

	public void setProbId(int probId) {
		this.probId = probId;
	}

	public String getProbNum() {
		return probNum;
	}

	public void setProbNum(String probNum) {
		this.probNum = probNum;
	}

	public String getWatch() {
		return watch;
	}

	public void setWatch(String watch) {
		this.watch = watch;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getDuedate() {
		return duedate;
	}

	public void setDuedate(Date duedate) {
		this.duedate = duedate;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public String getAffectedVersion() {
		return affectedVersion;
	}

	public void setAffectedVersion(String affectedVersion) {
		this.affectedVersion = affectedVersion;
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

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(String effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public String getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(String effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getAttachmentNames() {
		return attachmentNames;
	}

	public void setAttachmentNames(String attachmentNames) {
		this.attachmentNames = attachmentNames;
	}

	public String getWatchName() {
		return watchName;
	}

	public void setWatchName(String watchName) {
		this.watchName = watchName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getPriorityName() {
		return priorityName;
	}

	public void setPriorityName(String priorityName) {
		this.priorityName = priorityName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getTrackingUser() {
		return trackingUser;
	}

	public void setTrackingUser(String trackingUser) {
		this.trackingUser = trackingUser;
	}

	public String getTrackingUsername() {
		return trackingUsername;
	}

	public void setTrackingUsername(String trackingUsername) {
		this.trackingUsername = trackingUsername;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getVisibleRange() {
		return visibleRange;
	}

	public void setVisibleRange(int visibleRange) {
		this.visibleRange = visibleRange;
	}

	public String getReader() {
		return reader;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public int getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}

}
