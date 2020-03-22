package com.dp.plat.data.bean;

import java.util.List;

public class UserLogin {
	private int userId;
	private String username;
	private String password;
	private List<String> roles;
	private List<Object> authorities ;
	
	
	
	public UserLogin() {
	}
	
	public UserLogin(int userId,String password,List<Object> authorities){
		this.userId = userId;
		this.password = password;
		this.authorities = authorities;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
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
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public List<Object> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<Object> authorities) {
		this.authorities = authorities;
	}
}
