package com.dp.plat.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.data.bean.MailSenderInfo;

public class Test {
	public static void main(String[] args) {  
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");  
        EmailBean email = (EmailBean) ctx.getBean("emailer");  
        
        MailSenderInfo mailSenderInfo = new MailSenderInfo();
        email.sendEmail(mailSenderInfo);  
    }  
}
