package com.dp.plat.job;

import com.dp.plat.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 邮件发送 - 迁移自 Mailer, 每5分钟 */
@Component
public class MailerJob {
    @Autowired private SendMailService sendMailService;
    @Scheduled(cron = "0 0/5 * * * ?")
    public void execute() { sendMailService.sendWaitingMails(); }
}
