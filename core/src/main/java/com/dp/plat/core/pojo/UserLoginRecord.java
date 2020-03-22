package com.dp.plat.core.pojo;

import java.util.Date;

public class UserLoginRecord {
    private Integer id;

    private String loginName;

    private Date loginTime;

    private String loginIP;

    private Date logoutTime;

    private String logoutIP;

    private Boolean loginSuccess;

    private Boolean logoutSuccess;

    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIP() {
        return loginIP;
    }

    public void setLoginIP(String loginIP) {
        this.loginIP = loginIP;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getLogoutIP() {
        return logoutIP;
    }

    public void setLogoutIP(String logoutIP) {
        this.logoutIP = logoutIP;
    }

    public Boolean getLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(Boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public Boolean getLogoutSuccess() {
        return logoutSuccess;
    }

    public void setLogoutSuccess(Boolean logoutSuccess) {
        this.logoutSuccess = logoutSuccess;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}