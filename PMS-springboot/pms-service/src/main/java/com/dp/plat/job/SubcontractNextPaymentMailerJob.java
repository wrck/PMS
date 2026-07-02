package com.dp.plat.job;

import com.dp.plat.service.SubcontractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 转包余款提醒 - 迁移自 SubcontractNextPaymentMailer, 每天08:25 */
@Component
public class SubcontractNextPaymentMailerJob {
    @Autowired private SubcontractService subcontractService;
    @Scheduled(cron = "0 25 8 * * ?")
    public void execute() { subcontractService.sendNextPaymentReminder(); }
}
