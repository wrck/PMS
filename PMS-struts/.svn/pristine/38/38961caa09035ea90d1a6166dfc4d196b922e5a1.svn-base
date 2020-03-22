package com.dp.plat.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.dp.plat.data.bean.MailSenderInfo;

public class EmailBean implements ApplicationContextAware {

    private ApplicationContext ctx;  
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {  
        this.ctx = applicationContext;  
    }  
    public void sendEmail(MailSenderInfo mailSenderInfo) {  
        BlackListEvent event = new BlackListEvent(mailSenderInfo);  
        ctx.publishEvent(event);
        return;  
    }  

}
