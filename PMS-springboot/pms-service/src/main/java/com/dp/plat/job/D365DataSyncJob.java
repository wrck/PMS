package com.dp.plat.job;

import com.dp.plat.service.DataOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** D365数据同步 - 迁移自 D365DataJob */
@Component
public class D365DataSyncJob {
    @Autowired private DataOperationService dataOperationService;
    @Scheduled(cron = "0 0 23 * * ?")
    public void execute() { dataOperationService.syncFromD365(); }
}
