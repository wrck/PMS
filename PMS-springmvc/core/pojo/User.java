package com.dp.plat.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class User implements Serializable {

	private static final long serialVersionUID = -390316076987435315L;
	
	// 用户ID
	private Integer userId;

	// 用户名称
	private String userName;

	// 密码
	private String password;

	private String createBy;

	@JsonSerialize(using = JsonSerializer.class)
	private Date createTime;

	private String updateBy;

	@JsonSerialize(using = JsonSerializer.class)
	private Date updateTime;

	// 用户状态，0：失效，1有效，2：锁定
	private Short status;

	// 用户创建后需要修改密码判断
	private Boolean needChangePwd;

	// 用户密码输入错误次数
	private Integer loginErrorCount;

	// 用户自定义字段1
	private String userCustom1;

	// 用户自定义字段2
	private String userCustom2;

	// 用户自定义字段3
	private String userCustom3;

	// 用户自定义字段4
	private Integer userCustom4;

	// 用户自定义字段5
	private Integer userCustom5;

	/**
	 * 
	 */
	public User() {
	}
	
	/**
	 * @param userId2
	 */
	public User(Integer userId) {
		super();
		this.userId = userId;
	}

	/**
	 * 获取用户ID
	 *
	 * @return user_id - 用户ID
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * 设置用户ID
	 *
	 * @param userId
	 *            用户ID
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * 获取用户名称
	 *
	 * @return user_name - 用户名称
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 设置用户名称
	 *
	 * @param userName
	 *            用户名称
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 获取密码
	 *
	 * @return password - 密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码
	 *
	 * @param password
	 *            密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return create_by
	 */
	public String getCreateBy() {
		return createBy;
	}

	/**
	 * @param createBy
	 */
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	/**
	 * @return create_time
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return update_by
	 */
	public String getUpdateBy() {
		return updateBy;
	}

	/**
	 * @param updateBy
	 */
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	/**
	 * @return update_time
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 获取用户状态，0：失效，1有效，2：锁定
	 *
	 * @return status - 用户状态，0：失效，1有效，2：锁定
	 */
	public Short getStatus() {
		return status;
	}

	/**
	 * 设置用户状态，0：失效，1有效，2：锁定
	 *
	 * @param status
	 *            用户状态，0：失效，1有效，2：锁定
	 */
	public void setStatus(Short status) {
		this.status = status;
	}

	/**
	 * 获取用户创建后需要修改密码判断
	 *
	 * @return needChangePwd - 用户创建后需要修改密码判断
	 */
	public Boolean getNeedChangePwd() {
		return needChangePwd;
	}

	/**
	 * 设置用户创建后需要修改密码判断
	 *
	 * @param needChangePwd
	 *            用户创建后需要修改密码判断
	 */
	public void setNeedChangePwd(Boolean needChangePwd) {
		this.needChangePwd = needChangePwd;
	}

	/**
	 * 获取用户密码输入错误次数
	 *
	 * @return loginErrorCount - 用户密码输入错误次数
	 */
	public Integer getLoginErrorCount() {
		return loginErrorCount;
	}

	/**
	 * 设置用户密码输入错误次数
	 *
	 * @param loginErrorCount
	 *            用户密码输入错误次数
	 */
	public void setLoginErrorCount(Integer loginErrorCount) {
		this.loginErrorCount = loginErrorCount;
	}

	/**
	 * 获取用户自定义字段1
	 *
	 * @return userCustom1 - 用户自定义字段1
	 */
	public String getUserCustom1() {
		return userCustom1;
	}

	/**
	 * 设置用户自定义字段1
	 *
	 * @param userCustom1
	 *            用户自定义字段1
	 */
	public void setUserCustom1(String userCustom1) {
		this.userCustom1 = userCustom1;
	}

	/**
	 * 获取用户自定义字段2
	 *
	 * @return userCustom2 - 用户自定义字段2
	 */
	public String getUserCustom2() {
		return userCustom2;
	}

	/**
	 * 设置用户自定义字段2
	 *
	 * @param userCustom2
	 *            用户自定义字段2
	 */
	public void setUserCustom2(String userCustom2) {
		this.userCustom2 = userCustom2;
	}

	/**
	 * 获取用户自定义字段3
	 *
	 * @return userCustom3 - 用户自定义字段3
	 */
	public String getUserCustom3() {
		return userCustom3;
	}

	/**
	 * 设置用户自定义字段3
	 *
	 * @param userCustom3
	 *            用户自定义字段3
	 */
	public void setUserCustom3(String userCustom3) {
		this.userCustom3 = userCustom3;
	}

	/**
	 * 获取用户自定义字段4
	 *
	 * @return userCustom4 - 用户自定义字段4
	 */
	public Integer getUserCustom4() {
		return userCustom4;
	}

	/**
	 * 设置用户自定义字段4
	 *
	 * @param userCustom4
	 *            用户自定义字段4
	 */
	public void setUserCustom4(Integer userCustom4) {
		this.userCustom4 = userCustom4;
	}

	/**
	 * 获取用户自定义字段5
	 *
	 * @return userCustom5 - 用户自定义字段5
	 */
	public Integer getUserCustom5() {
		return userCustom5;
	}

	/**
	 * 设置用户自定义字段5
	 *
	 * @param userCustom5
	 *            用户自定义字段5
	 */
	public void setUserCustom5(Integer userCustom5) {
		this.userCustom5 = userCustom5;
	}

	@Override
	public String toString() {
		return "User [" + (userId != null ? "userId=" + userId + ", " : "")
				+ (userName != null ? "userName=" + userName + ", " : "")
				+ (password != null ? "password=" + password + ", " : "")
				+ (createBy != null ? "createBy=" + createBy + ", " : "")
				+ (createTime != null ? "createTime=" + createTime + ", " : "")
				+ (updateBy != null ? "updateBy=" + updateBy + ", " : "")
				+ (updateTime != null ? "updateTime=" + updateTime + ", " : "")
				+ (status != null ? "status=" + status + ", " : "")
				+ (needChangePwd != null ? "needChangePwd=" + needChangePwd + ", " : "")
				+ (loginErrorCount != null ? "loginErrorCount=" + loginErrorCount : "") + "]";
	}
}
