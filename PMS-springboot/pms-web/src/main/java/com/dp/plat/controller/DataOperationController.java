package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.DataOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据操作控制器 - 迁移自老系统数据同步Job
 */
@RestController
@RequestMapping("/api/data")
public class DataOperationController {

    @Autowired
    private DataOperationService dataOperationService;

    /** 手动触发ERP订单同步 */
    @PostMapping("/sync/erp")
    public R<Void> syncERP() {
        dataOperationService.syncOrderFromERP();
        return R.ok();
    }

    /** 手动触发ITR问题单同步 */
    @PostMapping("/sync/itr")
    public R<Void> syncITR() {
        dataOperationService.syncFromITR();
        return R.ok();
    }

    /** 手动触发SMS数据同步 */
    @PostMapping("/sync/sms")
    public R<Void> syncSMS() {
        dataOperationService.syncFromSMS();
        return R.ok();
    }

    /** 手动触发D365数据同步 */
    @PostMapping("/sync/d365")
    public R<Void> syncD365() {
        dataOperationService.syncFromD365();
        return R.ok();
    }

    /** 手动触发OA售前数据同步 */
    @PostMapping("/sync/oa")
    public R<Void> syncOA() {
        dataOperationService.syncPresalesFromOA();
        return R.ok();
    }

    /** 手动触发License数据同步 */
    @PostMapping("/sync/license")
    public R<Void> syncLicense() {
        dataOperationService.syncFromLicense();
        return R.ok();
    }

    /** 手动触发交付件推送到D365 */
    @PostMapping("/push/d365-delivery")
    public R<Void> pushDelivery() {
        dataOperationService.pushDeliveryToD365();
        return R.ok();
    }
}
