package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysDepartment;
import com.dp.plat.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公司 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/list")
    public R<IPage<SysDepartment>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(companyService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysDepartment> detail(@PathVariable Long id) {
        return R.ok(companyService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysDepartment entity) {
        companyService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysDepartment entity) {
        companyService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        companyService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysDepartment>> listAll() {
        return R.ok(companyService.listAll());
    }
}
