package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.EhrEmpPower;
import com.dp.plat.service.EhrEmpPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EHR员工权限 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/ehr/emp-power")
public class EhrEmpPowerController {

    @Autowired
    private EhrEmpPowerService ehrEmpPowerService;

    @GetMapping("/list")
    public R<IPage<EhrEmpPower>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(ehrEmpPowerService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<EhrEmpPower> detail(@PathVariable Long id) {
        return R.ok(ehrEmpPowerService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody EhrEmpPower entity) {
        ehrEmpPowerService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody EhrEmpPower entity) {
        ehrEmpPowerService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        ehrEmpPowerService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<EhrEmpPower>> listAll() {
        return R.ok(ehrEmpPowerService.listAll());
    }
}
