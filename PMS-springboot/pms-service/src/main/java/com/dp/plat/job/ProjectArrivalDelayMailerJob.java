package com.dp.plat.job;

import com.dp.plat.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 到货超期邮件 - 迁移自 ProjectArrivalDelayMailer, 周一至周六08:00 */
@Component
public class ProjectArrivalDelayMailerJob {
    @Autowired private ReportService reportService;
    @Scheduled(cron = "0 0 8 ? * MON-SAT")
    public void execute() { reportService.sendArrivalDelayMail(); }
}
