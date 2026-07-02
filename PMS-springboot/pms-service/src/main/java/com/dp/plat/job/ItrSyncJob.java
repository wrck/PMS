package com.dp.plat.job;

import com.dp.plat.service.DataOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** ITR问题单同步 - 迁移自 GainDataFromITR, 每10分钟 */
@Component
public class ItrSyncJob {
    @Autowired private DataOperationService dataOperationService;
    @Scheduled(cron = "0 0/10 * * * ?")
    public void execute() { dataOperationService.syncFromITR(); }
}
