package com.dp.plat.ehr.vo;

import com.dp.plat.ehr.entity.Employee;

public class EmployeeVO extends Employee {
	private String compName;
	private String depName;
	private String jobName;
	private String reportToWorkNo;
	private String reportToName;
	private String wfreportToWorkNo;
	private String wfreportToName;
	private String workNos;
	private String workNosExclude;
	private String depGrade;
	private String depLV1ID;
	private String depLV1Code;
	private String depLV1Name;
	private String depLV2ID;
	private String depLV2Code;
	private String depLV2Name;
	private String depLV3ID;
	private String depLV3Code;
	private String depLV3Name;
	private String depAllName;
	
	private String depIDs;
	private String depCodes;
	private String jobIDs;
	private String jobCodes;

	// 主管部门ids
	private String adminDepIDs;

	private String account;
	
	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getDepName() {
		return depName;
	}

	public void setDepName(String depName) {
		this.depName = depName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getReportToWorkNo() {
		return reportToWorkNo;
	}

	public void setReportToWorkNo(String reportToWorkNo) {
		this.reportToWorkNo = reportToWorkNo;
	}

	public String getReportToName() {
		return reportToName;
	}

	public void setReportToName(String reportToName) {
		this.reportToName = reportToName;
	}

	public String getWfreportToWorkNo() {
		return wfreportToWorkNo;
	}

	public void setWfreportToWorkNo(String wfreportToWorkNo) {
		this.wfreportToWorkNo = wfreportToWorkNo;
	}

	public String getWfreportToName() {
		return wfreportToName;
	}

	public void setWfreportToName(String wfreportToName) {
		this.wfreportToName = wfreportToName;
	}

	public String getWorkNos() {
		return workNos;
	}

	public void setWorkNos(String workNos) {
		this.workNos = workNos;
	}

	public String getWorkNosExclude() {
		return workNosExclude;
	}

	public void setWorkNosExclude(String workNosExclude) {
		this.workNosExclude = workNosExclude;
	}
	
	public String getDepGrade() {
		return depGrade;
	}

	public void setDepGrade(String depGrade) {
		this.depGrade = depGrade;
	}

	public String getDepLV1ID() {
		return depLV1ID;
	}

	public void setDepLV1ID(String depLV1ID) {
		this.depLV1ID = depLV1ID;
	}

	public String getDepLV1Code() {
		return depLV1Code;
	}

	public void setDepLV1Code(String depLV1Code) {
		this.depLV1Code = depLV1Code;
	}

	public String getDepLV1Name() {
		return depLV1Name;
	}

	public void setDepLV1Name(String depLV1Name) {
		this.depLV1Name = depLV1Name;
	}

	public String getDepLV2ID() {
		return depLV2ID;
	}

	public void setDepLV2ID(String depLV2ID) {
		this.depLV2ID = depLV2ID;
	}

	public String getDepLV2Code() {
		return depLV2Code;
	}

	public void setDepLV2Code(String depLV2Code) {
		this.depLV2Code = depLV2Code;
	}

	public String getDepLV2Name() {
		return depLV2Name;
	}

	public void setDepLV2Name(String depLV2Name) {
		this.depLV2Name = depLV2Name;
	}

	public String getDepLV3ID() {
		return depLV3ID;
	}

	public void setDepLV3ID(String depLV3ID) {
		this.depLV3ID = depLV3ID;
	}

	public String getDepLV3Code() {
		return depLV3Code;
	}

	public void setDepLV3Code(String depLV3Code) {
		this.depLV3Code = depLV3Code;
	}

	public String getDepLV3Name() {
		return depLV3Name;
	}

	public void setDepLV3Name(String depLV3Name) {
		this.depLV3Name = depLV3Name;
	}

	public String getDepAllName() {
		return depAllName;
	}

	public void setDepAllName(String depAllName) {
		this.depAllName = depAllName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getDepIDs() {
		return depIDs;
	}

	public void setDepIDs(String depIDs) {
		this.depIDs = depIDs;
	}

	public String getDepCodes() {
		return depCodes;
	}

	public void setDepCodes(String depCodes) {
		this.depCodes = depCodes;
	}

	public String getJobIDs() {
		return jobIDs;
	}

	public void setJobIDs(String jobIDs) {
		this.jobIDs = jobIDs;
	}

	public String getJobCodes() {
		return jobCodes;
	}

	public void setJobCodes(String jobCodes) {
		this.jobCodes = jobCodes;
	}

	/**
	 * 获取主管部门IDs，对应PlanEmpPower的adminDepIDs
	 * @param adminDepIDs
	 */
	public String getAdminDepIDs() {
		return adminDepIDs;
	}

	/**
	 * 设置主管部门IDs，对应PlanEmpPower的adminDepIDs
	 * @param adminDepIDs
	 */
	public void setAdminDepIDs(String adminDepIDs) {
		this.adminDepIDs = adminDepIDs;
	}
	
}
