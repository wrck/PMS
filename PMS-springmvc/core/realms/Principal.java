/**
 * 
 */
package com.dp.plat.core.realms;

import java.io.Serializable;
import java.util.Date;

import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;

/**
 * 当前同步登陆对象
 * @author w02611
 *
 */
public class Principal implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer userId;

	private String userName;

	private Short status;

	private Integer userInfoId;

	private String realName;

	private String mobile;

	private String telphone;

	private String avatar;

	private Date birthday;

	private String email;

	private Short sex;

	private String menus;

	private Boolean needChangePwd;

	private Integer loginErrorCount;

	private Integer loginRecordId;
	
	private String homePage;
	
	/**
	 * user自定义字段
	 */
	private String userCustom1;
	private String userCustom2;
	private String userCustom3;
	private Integer userCustom4;
	private Integer userCustom5;
	
	public Principal(User user) {
		this.userId = user.getUserId();
		this.userName = user.getUserName();
		this.status = user.getStatus();
		this.needChangePwd = user.getNeedChangePwd();
		this.loginErrorCount = user.getLoginErrorCount();
		
		this.userCustom1 = user.getUserCustom1();
		this.userCustom2 = user.getUserCustom2();
		this.userCustom3 = user.getUserCustom3();
		this.userCustom4 = user.getUserCustom4();
		this.userCustom5 = user.getUserCustom5();
	}

	/**
	 * 设置用户详细信息
	 * @param userInfo
	 */
	public void setUserInfo(UserInfo userInfo) {
		if (userInfo != null) {
			this.userInfoId = userInfo.getId();
			this.realName = userInfo.getRealName();
			this.avatar = userInfo.getAvatar();
			this.birthday = userInfo.getBirthday();
			this.sex = userInfo.getSex();
			this.email = userInfo.getEmail();
			this.mobile = userInfo.getMobile();
			this.telphone = userInfo.getTelphone();
		}
	}
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Boolean getNeedChangePwd() {
		return needChangePwd;
	}

	public void setNeedChangePwd(Boolean needChangePwd) {
		this.needChangePwd = needChangePwd;
	}

	public Integer getLoginErrorCount() {
		return loginErrorCount;
	}

	public void setLoginErrorCount(Integer loginErrorCount) {
		this.loginErrorCount = loginErrorCount;
	}

	public Integer getUserInfoId() {
		return userInfoId;
	}

	public void setUserInfoId(Integer userInfoId) {
		this.userInfoId = userInfoId;
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

	public String getMenus() {
		return menus;
	}

	public void setMenus(String menus) {
		this.menus = menus;
	}

	public Integer getLoginRecordId() {
		return loginRecordId;
	}

	public void setLoginRecordId(Integer loginRecordId) {
		this.loginRecordId = loginRecordId;
	}
	
	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getUserCustom1() {
		return userCustom1;
	}

	public void setUserCustom1(String userCustom1) {
		this.userCustom1 = userCustom1;
	}

	public String getUserCustom2() {
		return userCustom2;
	}

	public void setUserCustom2(String userCustom2) {
		this.userCustom2 = userCustom2;
	}

	public String getUserCustom3() {
		return userCustom3;
	}

	public void setUserCustom3(String userCustom3) {
		this.userCustom3 = userCustom3;
	}

	public Integer getUserCustom4() {
		return userCustom4;
	}

	public void setUserCustom4(Integer userCustom4) {
		this.userCustom4 = userCustom4;
	}

	public Integer getUserCustom5() {
		return userCustom5;
	}

	public void setUserCustom5(Integer userCustom5) {
		this.userCustom5 = userCustom5;
	}

}
