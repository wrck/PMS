package com.dp.plat.job;

import com.dp.plat.service.DataOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** D365交付件同步 - 迁移自 PushContractAcceptanceDeliveryJob, 每天08:00和13:00 */
@Component
public class PushContractAcceptanceDeliveryJob {
    @Autowired private DataOperationService dataOperationService;
    @Scheduled(cron = "0 0 8,13 * * ?")
    public void execute() { dataOperationService.pushDeliveryToD365(); }
}
