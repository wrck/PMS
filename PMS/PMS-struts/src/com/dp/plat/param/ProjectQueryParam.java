package com.dp.plat.param;

import java.util.Date;

/**
 * 项目管理列表查询参数
 * @author admin
 */
public class ProjectQueryParam {
	private Date createStartTime;//项目创建-开始时间
	private Date createEndTime;//项目创建-结束时间
	
	private Date refreshStartTime;//项目刷新-开始时间
	private Date refreshEndTime;//项目刷新-结束时间
	
	private Date closeStartTime;//项目闭环-开始时间
	private Date closeEndTime;//项目闭环-结束时间
	
	private String itemModel;
	
	public Date getCreateStartTime() {
		return createStartTime;
	}
	public void setCreateStartTime(Date createStartTime) {
		this.createStartTime = createStartTime;
	}
	public Date getCreateEndTime() {
		return createEndTime;
	}
	public void setCreateEndTime(Date createEndTime) {
		this.createEndTime = createEndTime;
	}
	public Date getRefreshStartTime() {
		return refreshStartTime;
	}
	public void setRefreshStartTime(Date refreshStartTime) {
		this.refreshStartTime = refreshStartTime;
	}
	public Date getRefreshEndTime() {
		return refreshEndTime;
	}
	public void setRefreshEndTime(Date refreshEndTime) {
		this.refreshEndTime = refreshEndTime;
	}
	public Date getCloseStartTime() {
		return closeStartTime;
	}
	public void setCloseStartTime(Date closeStartTime) {
		this.closeStartTime = closeStartTime;
	}
	public Date getCloseEndTime() {
		return closeEndTime;
	}
	public void setCloseEndTime(Date closeEndTime) {
		this.closeEndTime = closeEndTime;
	}
	public String getItemModel() {
		return itemModel;
	}
	public void setItemModel(String itemModel) {
		this.itemModel = itemModel;
	}
}
