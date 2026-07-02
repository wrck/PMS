package com.dp.plat.job;

import com.dp.plat.service.DataOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** ERP/D365订单同步 - 迁移自 GainOrderByERP, 每天23:50 */
@Component
public class OrderSyncJob {
    @Autowired private DataOperationService dataOperationService;
    @Scheduled(cron = "0 50 23 * * ?")
    public void execute() { dataOperationService.syncOrderFromERP(); }
}
