package com.dp.plat.data.bean;


/**
 * 邮件或消息通知模板
 * @author admin
 *
 */
public class NotificationTemplate {
	private String templateCode;
	private String notificationSubject;
	private String notificationContent;
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	public String getNotificationSubject() {
		return notificationSubject;
	}
	public void setNotificationSubject(String notificationSubject) {
		this.notificationSubject = notificationSubject;
	}
	public String getNotificationContent() {
		return notificationContent;
	}
	public void setNotificationContent(String notificationContent) {
		this.notificationContent = notificationContent;
	}
	
}
