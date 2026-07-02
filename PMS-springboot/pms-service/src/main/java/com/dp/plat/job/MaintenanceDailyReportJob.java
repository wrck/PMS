package com.dp.plat.job;

import com.dp.plat.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 维护日报 - 迁移自 MaintenanceDailyReportMailer, 每天05:00 */
@Component
public class MaintenanceDailyReportJob {
    @Autowired private MaintenanceService maintenanceService;
    @Scheduled(cron = "0 0 5 * * ?")
    public void execute() { maintenanceService.sendDailyReport(); }
}
