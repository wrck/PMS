package com.dp.plat.core.vo;

import java.util.Date;

import com.dp.plat.core.pojo.User;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class UserDetail extends User {

	private static final long serialVersionUID = 2124698802978069182L;

	private Integer userInfoId;

	// 工号
    private String workNo;

    // 姓名
    private String realName;

    // 英文名
    private String eName;

    // 公司ID
    private Integer compID;

    // 部门ID
    private Integer depID;

    // 岗位ID
    private Integer jobID;

    // 直接上级
    private Integer reportTo;

    // 职能上级
    private Integer wfreportTo;

    // 员工状态，1：在职，2：离职
    private Integer empStatus;

    // 岗位状态
    private Integer jobStatus;

    // 聘用类型：1：正式，3：实习生
    private Integer empType;

    // 性别：1：男，0：女
    private Short sex;

    // 生日
    @JsonSerialize(using = JsonSerializer.class)
    private Date birthday;

    // 邮箱
    private String email;

    // 手机
    private String mobile;

    // 座机
    private String telphone;

    // 头像
    private String avatar;

    // 备注
    private String remark;

    // 状态
    private Integer state;

    // 预留字段1
    private Integer custom1;

    // 预留字段2
    private Integer custom2;

    // 预留字段3
    private String custom3;

    // 预留字段4
    private String custom4;

    // 预留字段5
    private String custom5;

	private String roles;

	private Integer roleId;
	
	private String roleCodes;
	
	private Integer maxRolePriority;

	public Integer getUserInfoId() {
		return userInfoId;
	}

	public void setUserInfoId(Integer userInfoId) {
		this.userInfoId = userInfoId;
	}

	public String getWorkNo() {
		return workNo;
	}

	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String geteName() {
		return eName;
	}

	public void seteName(String eName) {
		this.eName = eName;
	}

	public Integer getCompID() {
		return compID;
	}

	public void setCompID(Integer compID) {
		this.compID = compID;
	}

	public Integer getDepID() {
		return depID;
	}

	public void setDepID(Integer depID) {
		this.depID = depID;
	}

	public Integer getJobID() {
		return jobID;
	}

	public void setJobID(Integer jobID) {
		this.jobID = jobID;
	}

	public Integer getReportTo() {
		return reportTo;
	}

	public void setReportTo(Integer reportTo) {
		this.reportTo = reportTo;
	}

	public Integer getWfreportTo() {
		return wfreportTo;
	}

	public void setWfreportTo(Integer wfreportTo) {
		this.wfreportTo = wfreportTo;
	}

	public Integer getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(Integer empStatus) {
		this.empStatus = empStatus;
	}

	public Integer getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(Integer jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Integer getEmpType() {
		return empType;
	}

	public void setEmpType(Integer empType) {
		this.empType = empType;
	}

	public Short getSex() {
		return sex;
	}

	public void setSex(Short sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
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

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getCustom1() {
		return custom1;
	}

	public void setCustom1(Integer custom1) {
		this.custom1 = custom1;
	}

	public Integer getCustom2() {
		return custom2;
	}

	public void setCustom2(Integer custom2) {
		this.custom2 = custom2;
	}

	public String getCustom3() {
		return custom3;
	}

	public void setCustom3(String custom3) {
		this.custom3 = custom3;
	}

	public String getCustom4() {
		return custom4;
	}

	public void setCustom4(String custom4) {
		this.custom4 = custom4;
	}

	public String getCustom5() {
		return custom5;
	}

	public void setCustom5(String custom5) {
		this.custom5 = custom5;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}
	
	public Integer getMaxRolePriority() {
		return maxRolePriority;
	}

	public void setMaxRolePriority(Integer maxRolePriority) {
		this.maxRolePriority = maxRolePriority;
	}

	@Override
	public String toString() {
		return "UserDetail [" + (userInfoId != null ? "userInfoId=" + userInfoId + ", " : "")
				+ (workNo != null ? "workNo=" + workNo + ", " : "")
				+ (realName != null ? "realName=" + realName + ", " : "")
				+ (compID != null ? "compID=" + compID + ", " : "") + (sex != null ? "sex=" + sex + ", " : "")
				+ (birthday != null ? "birthday=" + birthday + ", " : "")
				+ (email != null ? "email=" + email + ", " : "") + (mobile != null ? "mobile=" + mobile + ", " : "")
				+ (telphone != null ? "telphone=" + telphone + ", " : "")
				+ (avatar != null ? "avatar=" + avatar + ", " : "") + (remark != null ? "remark=" + remark + ", " : "")
				+ (state != null ? "state=" + state + ", " : "") + (custom1 != null ? "custom1=" + custom1 + ", " : "")
				+ (custom2 != null ? "custom2=" + custom2 + ", " : "")
				+ (custom3 != null ? "custom3=" + custom3 + ", " : "")
				+ (custom4 != null ? "custom4=" + custom4 + ", " : "")
				+ (custom5 != null ? "custom5=" + custom5 + ", " : "") + (roles != null ? "roles=" + roles + ", " : "")
				+ (roleId != null ? "roleId=" + roleId + ", " : "")
				+ (roleCodes != null ? "roleCodes=" + roleCodes + ", " : "")
				+ (maxRolePriority != null ? "maxRolePriority=" + maxRolePriority : "") + "]"
				+ (super.toString() != null ? "toString()=" + super.toString() : "") + "]";
	}

}