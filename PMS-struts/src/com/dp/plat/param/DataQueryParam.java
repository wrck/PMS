package com.dp.plat.param;

import java.util.Date;

/**
 * 数据统计查询参数
 * @author admin
 *
 */
public class DataQueryParam {
	private Date startTime;//开始时间
	private Date endTime;//开始时间
	private String officeCodes;//办事处
	private String pm;//项目经理
	private String projectType;//项目类型
	private String serviceType;//服务类型--实施方式
	private String projectPhase;//项目阶段
	private double finishingRate;//计划完成率
	private Date phaseStartTime;//阶段开始时间
	private Date phaseEndTime;//阶段结束时间
	private Date cbStartTime;
	private Date cbEndTime;
	
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getOfficeCodes() {
		return officeCodes;
	}
	public void setOfficeCodes(String officeCodes) {
		this.officeCodes = officeCodes;
	}
	public String getPm() {
		return pm;
	}
	public void setPm(String pm) {
		this.pm = pm;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getProjectPhase() {
		return projectPhase;
	}
	public void setProjectPhase(String projectPhase) {
		this.projectPhase = projectPhase;
	}
	public double getFinishingRate() {
		return finishingRate;
	}
	public void setFinishingRate(double finishingRate) {
		this.finishingRate = finishingRate;
	}
	public Date getPhaseStartTime() {
		return phaseStartTime;
	}
	public void setPhaseStartTime(Date phaseStartTime) {
		this.phaseStartTime = phaseStartTime;
	}
	public Date getPhaseEndTime() {
		return phaseEndTime;
	}
	public void setPhaseEndTime(Date phaseEndTime) {
		this.phaseEndTime = phaseEndTime;
	}
	public Date getCbStartTime() {
		return cbStartTime;
	}
	public void setCbStartTime(Date cbStartTime) {
		this.cbStartTime = cbStartTime;
	}
	public Date getCbEndTime() {
		return cbEndTime;
	}
	public void setCbEndTime(Date cbEndTime) {
		this.cbEndTime = cbEndTime;
	}
	
	
}
