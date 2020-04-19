package com.dp.plat.core.vo;

import java.util.Date;

import com.dp.plat.core.pojo.User;

public class UserDetail extends User {

	private static final long serialVersionUID = 2124698802978069182L;

	private Integer userInfoId;

	private Integer compID;
	
	private String realName;

	private String mobile;

	private String telphone;

	private String avatar;

	private Date birthday;

	private String email;

	private Short sex;

	private String roles;

	private Integer roleId;
	
	private String roleCodes;

	public Integer getUserInfoId() {
		return userInfoId;
	}

	public void setUserInfoId(Integer userInfoId) {
		this.userInfoId = userInfoId;
	}

	public Integer getCompID() {
		return compID;
	}

	public void setCompID(Integer compID) {
		this.compID = compID;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
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

	public Short getSex() {
		return sex;
	}

	public void setSex(Short sex) {
		this.sex = sex;
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

	@Override
	public String toString() {
		return "UserDetail [" + (userInfoId != null ? "userInfoId=" + userInfoId + ", " : "")
				+ (realName != null ? "realName=" + realName + ", " : "")
				+ (mobile != null ? "mobile=" + mobile + ", " : "")
				+ (telphone != null ? "telphone=" + telphone + ", " : "")
				+ (avatar != null ? "avatar=" + avatar + ", " : "")
				+ (birthday != null ? "birthday=" + birthday + ", " : "")
				+ (email != null ? "email=" + email + ", " : "") + (sex != null ? "sex=" + sex + ", " : "")
				+ (roles != null ? "roles=" + roles + ", " : "") + (roleId != null ? "roleId=" + roleId + ", " : "")
				+ (super.toString() != null ? "toString()=" + super.toString() : "") + "]";
	}

}