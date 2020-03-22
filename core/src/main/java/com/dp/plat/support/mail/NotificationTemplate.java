package com.dp.plat.support.mail;

/**
 * 邮件或消息通知模板
 * 
 * @author admin
 *
 */
public class NotificationTemplate {
	private String templateCode;
	private String subject;
	private String content;

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
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

	public void setContent(String content) {
		this.content = content;
	}

}