package com.dp.plat.util;

import org.springframework.context.ApplicationEvent;

import com.dp.plat.data.bean.MailSenderInfo;

public class BlackListEvent extends ApplicationEvent{
	
	private MailSenderInfo mailSenderInfo;
	public BlackListEvent(MailSenderInfo mailSenderInfo) {
		super(mailSenderInfo);
		this.mailSenderInfo = mailSenderInfo;
	}
	private static final long serialVersionUID = 1L;
	
	public MailSenderInfo getMailSenderInfo() {
		return mailSenderInfo;
	}
	
	
	
}
