package com.dp.plat.job;

import com.dp.plat.service.PmSynchronizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** SMS数据同步 - 迁移自 TaskBySMS, 每天23:30 */
@Component
public class SmsSyncJob {
    @Autowired private PmSynchronizeService pmSynchronizeService;
    @Scheduled(cron = "0 30 23 * * ?")
    public void execute() { pmSynchronizeService.syncFromSMS(); }
}
