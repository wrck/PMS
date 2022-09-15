package com.dp.plat.subcontract.vo;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.dp.plat.subcontract.entity.SubcontractProject;

public class SubcontractProjectVO extends SubcontractProject {

	private String typeName;
	private String officeName;
	private String profitDepName;
	private String createName;
	private String stateName;
	private String callbackStateName;
	private String areaPower;
	private String searchTimeType;
	private Date searchStartTime;
	private Date searchEndTime;

	// 项目管理查询转包记录时传值
	private String projectId;

	// 转包列表数据导出额外字段
	private String paidRatio;
	private String paidAmount;
	private Date zrApproverTime;
	private Date confirmTime;
	private Date paymentTime;
	
	// 余款提醒额外字段
	/**
	 * 最后一次付款时间
	 */
	private Date lastPaymentTime;
	/**
	 * 距最后一次付款相隔月份
	 */
	private int monthDiff;
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getProfitDepName() {
		return profitDepName;
	}

	public void setProfitDepName(String profitDepName) {
		this.profitDepName = profitDepName;
	}
	
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCallbackStateName() {
		return callbackStateName;
	}

	public void setCallbackStateName(String callbackStateName) {
		this.callbackStateName = callbackStateName;
	}

	public String getAreaPower() {
		return areaPower;
	}

	public void setAreaPower(String areaPower) {
		this.areaPower = areaPower;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getSearchTimeType() {
		return searchTimeType;
	}

	public void setSearchTimeType(String searchTimeType) {
		this.searchTimeType = searchTimeType;
	}

	public Date getSearchStartTime() {
		return searchStartTime;
	}

	public void setSearchStartTime(Date searchStartTime) {
		this.searchStartTime = searchStartTime;
	}

	/**
	 * 获取结束日期会加一天
	 * @return
	 */
	public Date getSearchEndTime() {
		if (this.searchEndTime == null) {
			return null;
		}
		Date searchEndTime = DateUtils.addDays(this.searchEndTime, 1);
		return searchEndTime = DateUtils.addMilliseconds(searchEndTime, -1);
		//return this.searchEndTime;
	}

	public void setSearchEndTime(Date searchEndTime) {
		this.searchEndTime = searchEndTime;
	}

	public String getPaidRatio() {
		return paidRatio;
	}

	public void setPaidRatio(String paidRatio) {
		this.paidRatio = paidRatio;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Date getZrApproverTime() {
		return zrApproverTime;
	}

	public void setZrApproverTime(Date zrApproverTime) {
		this.zrApproverTime = zrApproverTime;
	}

	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Date getLastPaymentTime() {
		return lastPaymentTime;
	}

	public void setLastPaymentTime(Date lastPaymentTime) {
		this.lastPaymentTime = lastPaymentTime;
	}

	public int getMonthDiff() {
		return monthDiff;
	}

	public void setMonthDiff(int monthDiff) {
		this.monthDiff = monthDiff;
	}

}
