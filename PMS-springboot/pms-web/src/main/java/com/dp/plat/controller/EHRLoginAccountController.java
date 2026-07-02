package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.EHRLoginAccount;
import com.dp.plat.service.EHRLoginAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EHR登录账号 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/ehr/login-account")
public class EHRLoginAccountController {

    @Autowired
    private EHRLoginAccountService eHRLoginAccountService;

    @GetMapping("/list")
    public R<IPage<EHRLoginAccount>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(eHRLoginAccountService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<EHRLoginAccount> detail(@PathVariable Long id) {
        return R.ok(eHRLoginAccountService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody EHRLoginAccount entity) {
        eHRLoginAccountService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody EHRLoginAccount entity) {
        eHRLoginAccountService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        eHRLoginAccountService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<EHRLoginAccount>> listAll() {
        return R.ok(eHRLoginAccountService.listAll());
    }
}
