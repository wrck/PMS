package com.dp.plat.job;

import com.dp.plat.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 验收邮件汇总 - 迁移自 ProjectInspectionMailer, 每周日14:00 */
@Component
public class ProjectInspectionMailerJob {
    @Autowired private ReportService reportService;
    @Scheduled(cron = "0 0 14 ? * SUN")
    public void execute() { reportService.sendInspectionMailSummary(); }
}
