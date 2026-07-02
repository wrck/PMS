package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysDepartment;
import com.dp.plat.service.EhrCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EHR公司 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/ehr/company")
public class EhrCompanyController {

    @Autowired
    private EhrCompanyService ehrCompanyService;

    @GetMapping("/list")
    public R<IPage<SysDepartment>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(ehrCompanyService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysDepartment> detail(@PathVariable Long id) {
        return R.ok(ehrCompanyService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysDepartment entity) {
        ehrCompanyService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysDepartment entity) {
        ehrCompanyService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        ehrCompanyService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysDepartment>> listAll() {
        return R.ok(ehrCompanyService.listAll());
    }
}
