package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.PmSynchronizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * PM同步控制器 - 迁移自老系统SMS同步Job
 */
@RestController
@RequestMapping("/api/sync/pm")
public class PmSynchronizeController {

    @Autowired
    private PmSynchronizeService pmSynchronizeService;

    /** 执行全部SMS同步 */
    @PostMapping("/sms")
    public R<Void> syncFromSMS() {
        pmSynchronizeService.syncFromSMS();
        return R.ok();
    }

    /** 同步项目属性 */
    @PostMapping("/sms/property")
    public R<Void> syncProperty() {
        pmSynchronizeService.syncProjectPropertyFromSMS();
        return R.ok();
    }

    /** 同步设备清单 */
    @PostMapping("/sms/product-line")
    public R<Void> syncProductLine() {
        pmSynchronizeService.syncProjectProductLineFromSMS();
        return R.ok();
    }

    /** 同步售前数据 */
    @PostMapping("/sms/presales")
    public R<Void> syncPresales() {
        pmSynchronizeService.syncPresalesFromSMS();
        return R.ok();
    }

    /** 同步收款计划 */
    @PostMapping("/sms/payment-plan")
    public R<Void> syncPaymentPlan() {
        pmSynchronizeService.syncPaymentPlanFromSMS();
        return R.ok();
    }

    /** 同步市场关系 */
    @PostMapping("/sms/market-relation")
    public R<Void> syncMarketRelation() {
        pmSynchronizeService.syncMarketRelationsFromSMS();
        return R.ok();
    }
}
