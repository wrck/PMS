package com.dp.plat.job;

import com.dp.plat.service.DispatchSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 发票同步 - 迁移自 DispatchSettlementInvoiceToFPJob */
@Component
public class DispatchSettlementInvoiceToFPJob {
    @Autowired private DispatchSettlementService dispatchSettlementService;
    @Scheduled(cron = "0 0 9 * * ?")
    public void execute() { dispatchSettlementService.syncInvoiceToFP(); }
}
