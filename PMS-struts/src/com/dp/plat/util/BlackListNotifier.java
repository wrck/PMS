package com.dp.plat.util;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.dp.plat.util.test.SimpleMailSender;

public class BlackListNotifier implements ApplicationListener{

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		System.out.println(arg0);
		if(arg0 instanceof BlackListEvent){
			 System.out.println(((BlackListEvent) arg0).getMailSenderInfo());  
			 SimpleMailSender.sendMail(((BlackListEvent) arg0).getMailSenderInfo());
		}
	}

}
