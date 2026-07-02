package com.dp.plat.job;

import com.dp.plat.service.DataOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** OA售前数据同步 - 迁移自 GainPresalesInfoFromOA */
@Component
public class GainPresalesInfoFromOAJob {
    @Autowired private DataOperationService dataOperationService;
    @Scheduled(cron = "0 0 22 * * ?")
    public void execute() { dataOperationService.syncPresalesFromOA(); }
}
