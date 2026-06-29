package com.dp.plat.ehr.vo;

/**
 * 往前台传值的对象，只保留常用字段，避免字段过多页面加载慢<br>
 * 从前台或者值请使用EmployeeVO
 * 
 * @author w02611
 * @see EmployeeVO
 */
public class SimpleEmployeeVO {
	// 员工ID，外键
	private Integer empID;

	// 工号
	private String workNo;

	// 姓名
	private String name;

	// 邮箱
	private String email;
	
	private String mobile;

	private String compName;
	private String depName;
	private String jobName;
	private String depAllName;
	private String account;

	public Integer getEmpID() {
		return empID;
	}

	public void setEmpID(Integer empID) {
		this.empID = empID;
	}

	public String getWorkNo() {
		return workNo;
	}

	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

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

}
