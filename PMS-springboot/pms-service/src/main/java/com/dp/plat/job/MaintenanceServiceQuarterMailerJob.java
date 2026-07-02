package com.dp.plat.job;

import com.dp.plat.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 维护季度报表 - 迁移自 MaintenanceServiceQuarterMailer, 每季度首月1日06:00 */
@Component
public class MaintenanceServiceQuarterMailerJob {
    @Autowired private MaintenanceService maintenanceService;
    @Scheduled(cron = "0 0 6 1 2/3 ?")
    public void execute() { maintenanceService.sendQuarterReport(); }
}
