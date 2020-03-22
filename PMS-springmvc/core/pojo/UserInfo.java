package com.dp.plat.core.pojo;

import java.util.Date;

public class UserInfo {
	private Integer id;

	private String realName;

	private String mobile;

	private String telphone;

	private String avatar;

	private Integer userId;

	private Date birthday;

	private String email;

	private Short sex;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	@Override
	public String toString() {
		return "UserInfo [" + (id != null ? "id=" + id + ", " : "")
				+ (realName != null ? "realName=" + realName + ", " : "")
				+ (mobile != null ? "mobile=" + mobile + ", " : "")
				+ (telphone != null ? "telphone=" + telphone + ", " : "")
				+ (avatar != null ? "avatar=" + avatar + ", " : "") + (userId != null ? "userId=" + userId + ", " : "")
				+ (birthday != null ? "birthday=" + birthday + ", " : "")
				+ (email != null ? "email=" + email + ", " : "") + (sex != null ? "sex=" + sex : "") + "]";
	}
}