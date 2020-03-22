package com.dp.plat.core.pojo;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UsernamePasswordCaptchaToken extends UsernamePasswordToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 验证码
	 */
	private String captcha;

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public UsernamePasswordCaptchaToken() {
		super();

	}
	
	public UsernamePasswordCaptchaToken(String username ,String password ,String captcha){
		super(username, password);
		this.captcha = captcha;
	}
	
}
