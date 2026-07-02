package com.dp.plat.job;

import com.dp.plat.service.DispatchSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** SSE付款同步 - 迁移自 DispatchSettlementSEEPaymentJob */
@Component
public class DispatchSettlementSEEPaymentJob {
    @Autowired private DispatchSettlementService dispatchSettlementService;
    @Scheduled(cron = "0 0 8 * * ?")
    public void execute() { dispatchSettlementService.syncSEEPayment(); }
}
