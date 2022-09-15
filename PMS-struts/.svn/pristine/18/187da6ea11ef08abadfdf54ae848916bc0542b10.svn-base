package com.dp.plat.test;

import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.util.test.SimpleMailSender;

public class TestMail {
	
	public static void main(String[] args) {
		MailSenderInfo info = new MailSenderInfo();
		info.setMailServerPort("25");
		info.setMailServerHost("mail.dptech.com");
		info.setTos("wenrencaike@dptech.com");
		info.setValidate(true);
		StringBuilder html = new StringBuilder();
		
		info.setContent(html.toString());
		info.setSubject("Test");
		info.setUserName("pms@dptech.com");
		info.setPassword("2Bk29UamZr");
		info.setFromAddress("pms@dptech.com");
		SimpleMailSender.sendMail(info);
	}
}
