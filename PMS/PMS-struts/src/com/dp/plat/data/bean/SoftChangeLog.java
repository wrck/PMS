package com.dp.plat.data.bean;

import java.util.Date;

/**
 * 软件版本更新记录
 * @author j01441
 *
 */
public class SoftChangeLog {
	private int id;
	private int projectId;//项目ID
	private String changeVersion;//软件变更版本
	private String versionAndCreateTime;
	private String changeRemark;//变更说明
	private int latest;//是否为最新版本
	private String createBy;
	private Date createTime;
	private String updateBy;
	private Date updateTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getChangeVersion() {
		return changeVersion;
	}
	public void setChangeVersion(String changeVersion) {
		this.changeVersion = changeVersion;
	}
	public String getChangeRemark() {
		return changeRemark;
	}
	public void setChangeRemark(String changeRemark) {
		this.changeRemark = changeRemark;
	}
	public int getLatest() {
		return latest;
	}
	public void setLatest(int latest) {
		this.latest = latest;
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
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getVersionAndCreateTime() {
		return versionAndCreateTime;
	}
	public void setVersionAndCreateTime(String versionAndCreateTime) {
		this.versionAndCreateTime = versionAndCreateTime;
	}
	public SoftChangeLog() {
		// TODO Auto-generated constructor stub
	}
	public SoftChangeLog(int id, String versionAndCreateTime) {
		super();
		this.id = id;
		this.versionAndCreateTime = versionAndCreateTime;
	}
	
	
}
