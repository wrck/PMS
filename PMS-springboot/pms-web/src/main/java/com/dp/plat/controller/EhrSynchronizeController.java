package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.EhrSynchronizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * EHR同步控制器 - 迁移自老系统EHR同步Job
 */
@RestController
@RequestMapping("/api/sync/ehr")
public class EhrSynchronizeController {

    @Autowired
    private EhrSynchronizeService ehrSynchronizeService;

    /** 执行EHR员工同步 */
    @PostMapping("/employee")
    public R<Void> syncEmployee() {
        ehrSynchronizeService.syncEmployeeFromEHR();
        return R.ok();
    }
}
