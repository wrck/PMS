package com.dp.plat.data.bean;

import java.util.Date;


public class User {

	private int id;
	private String username;
	private String password;
	private String email;
	private String dpNo;
	private String dpName;
	private String realName;
	private int status;
	private String roleids;
	private String roleName;
	private int isemail;
	private Date pwdoverdue;
	private String jobDesc;//岗位审核描述
	private String defaultPage;
	private String areapower;
	
	private String createBy;
	private Date createTime;
	private String updateBy;
	private Date updateTime;
	private Date effectiveFrom;
	private Date effectiveTo;
	private int size;
	
	public boolean isHasRole(int roleId){
		String role = ";" + roleId +  ";";
		String roleIds = getRoleids();
		if(roleIds.indexOf(role) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 返回当前用户有角色数量
	 * @return
	 */
	public int returnSize(){
		String roleIds = getRoleids();
		if(roleIds == null){
			size = 0;
		}else if(roleIds.indexOf(",") == -1){
			size = 1;
		}else{
			size = roleIds.indexOf(",");
		}
		return size;
	}

	public String getJobDesc() {
		return jobDesc;
	}
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	public Date getPwdoverdue() {
		return pwdoverdue;
	}
	public void setPwdoverdue(Date pwdoverdue) {
		this.pwdoverdue = pwdoverdue;
	}
	public int getIsemail() {
		return isemail;
	}
	public void setIsemail(int isemail) {
		this.isemail = isemail;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getDpNo() {
		return dpNo;
	}
	public void setDpNo(String dpNo) {
		this.dpNo = dpNo;
	}
	public String getDpName() {
		return dpName;
	}
	public void setDpName(String dpName) {
		this.dpName = dpName;
	}
	public Date getEffectiveTo() {
		return effectiveTo;
	}
	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}
	public String getDefaultPage() {
		return defaultPage;
	}
	public void setDefaultPage(String defaultPage) {
		this.defaultPage = defaultPage;
	}
	public String getAreapower() {
		return areapower;
	}
	public void setAreapower(String areapower) {
		this.areapower = areapower;
	}
	public String getRoleids() {
		return roleids;
	}
	public void setRoleids(String roleids) {
		this.roleids = roleids;
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
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}
	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
