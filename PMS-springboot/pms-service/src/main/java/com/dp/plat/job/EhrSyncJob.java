package com.dp.plat.job;

import com.dp.plat.service.EhrSynchronizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** EHR员工同步 - 迁移自 GainPersonByEHR, 每天22:30 */
@Component
public class EhrSyncJob {
    @Autowired private EhrSynchronizeService ehrSynchronizeService;
    @Scheduled(cron = "0 30 22 * * ?")
    public void execute() { ehrSynchronizeService.syncEmployeeFromEHR(); }
}
