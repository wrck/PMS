package com.dp.plat.job;

import com.dp.plat.service.SubcontractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 转包付款自动完成 - 迁移自 SubcontractPaymentAutoComplete, 每天08:00和13:00 */
@Component
public class SubcontractPaymentJob {
    @Autowired private SubcontractService subcontractService;
    @Scheduled(cron = "0 0 8,13 * * ?")
    public void execute() { subcontractService.autoCompletePayment(); }
}
