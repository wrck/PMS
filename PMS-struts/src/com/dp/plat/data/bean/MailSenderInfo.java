package com.dp.plat.data.bean;

import java.util.Date;
import java.util.Properties;

public class MailSenderInfo {
	private int id;
	// 发送邮件的服务器的IP和端口
	private String mailServerHost;
	private String mailServerPort;
	// 邮件发送昵称
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
	// 邮件主题
	private String subject;
	// 邮件的文本内容
	private String content;
	// 邮件附件的文件名
	private String attachFileNames;
	private String tos;//主送
	private String ccs;//抄送
	private String bcc;//密送
	private Date mailSendTime;//邮件发送时间
	private int sendFlag;//邮件发送状态
	private String createBy;
	private Date createTime;
	private Date effectiveFrom;
	private Date mailExpectSendTime;
	
	
	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {
		return getProperties(this);
	}
	
	public Properties getProperties(MailSenderInfo mailSenderInfo){
		Properties p = new Properties();
		p.put("mail.smtp.host", mailSenderInfo.getMailServerHost());
		p.put("mail.smtp.port", mailSenderInfo.getMailServerPort());
		p.put("mail.smtp.auth", "true");
		p.put("mail.smtp.localhost", "127.0.0.1");
		p.put("mail.smtp.starttls.enable", "true"); 
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getMailSendTime() {
		return mailSendTime;
	}

	public void setMailSendTime(Date mailSendTime) {
		this.mailSendTime = mailSendTime;
	}

	public int getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}

	public String getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String attachFileNames) {
		this.attachFileNames = attachFileNames;
	}

	public String getTos() {
		return tos;
	}

	public void setTos(String tos) {
		this.tos = tos;
	}

	public String getCcs() {
		return ccs;
	}

	public void setCcs(String ccs) {
		this.ccs = ccs;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getMailExpectSendTime() {
		return mailExpectSendTime;
	}

	public void setMailExpectSendTime(Date mailExpectSendTime) {
		this.mailExpectSendTime = mailExpectSendTime;
	}
	
}
