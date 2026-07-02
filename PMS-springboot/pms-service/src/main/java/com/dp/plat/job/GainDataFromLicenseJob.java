package com.dp.plat.job;

import com.dp.plat.service.DataOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** License数据同步 - 迁移自 GainDataFromLicense */
@Component
public class GainDataFromLicenseJob {
    @Autowired private DataOperationService dataOperationService;
    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() { dataOperationService.syncFromLicense(); }
}
