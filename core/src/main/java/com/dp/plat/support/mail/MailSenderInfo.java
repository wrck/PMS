package com.dp.plat.support.mail;

import java.util.Properties;

import com.dp.plat.support.mail.entity.MailInfo;

public class MailSenderInfo extends MailInfo {
	// 发送邮件的服务器的IP和端口
	private String mailServerHost;
	private String mailServerPort;
	// 邮件发送者的昵称
	private String fromNick;
	// 邮件发送者的地址
	private String fromAddress;
	// 邮件接收者的地址
	private String toAddress;
	// 登陆邮件发送服务器的用户名和密码
	private String userName;
	private String password;
	// 是否需要身份验证
	private boolean validate = true;

	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {
		Properties p = new Properties();
		p.put("mail.smtp.localhost", "127.0.0.1");
		p.put("mail.smtp.host", this.mailServerHost);
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.auth", validate ? "true" : "false");
		p.put("mail.smtp.starttls.enable", validate ? "true" : "false");
		return p;
	}

	public Properties getProperties(MailSenderInfo mailSenderInfo) {
		Properties p = new Properties();
		p.put("mail.smtp.localhost", "127.0.0.1");
		p.put("mail.smtp.host", mailSenderInfo.getMailServerHost());
		p.put("mail.smtp.port", mailSenderInfo.getMailServerPort());
		p.put("mail.smtp.auth", mailSenderInfo.validate ? "true" : "false");
		p.put("mail.smtp.starttls.enable",  mailSenderInfo.validate ? "true" : "false");
		return p;
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getFromNick() {
		return fromNick;
	}

	public void setFromNick(String fromNick) {
		this.fromNick = fromNick;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
