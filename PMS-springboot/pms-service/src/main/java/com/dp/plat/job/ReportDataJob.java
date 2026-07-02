package com.dp.plat.job;

import com.dp.plat.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 报表数据定时保存 - 迁移自 ReportDataTask, 每月最后一日23:10 */
@Component
public class ReportDataJob {
    @Autowired private ReportService reportService;
    @Scheduled(cron = "0 10 23 L * ?")
    public void execute() { reportService.saveReportData(); }
}
