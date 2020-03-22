package com.dp.plat.param;

public class LoginParam
{
	String username = "";
	String password = "";
	String validation = "";
	
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public void setValidation(String validation) {
		this.validation = validation;
	}
	public String getValidation() {
		return validation;
	}
}
