package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsProject;
import com.dp.plat.service.DataExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据导出 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/data/export")
public class DataExportController {

    @Autowired
    private DataExportService dataExportService;

    @GetMapping("/list")
    public R<IPage<PmsProject>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(dataExportService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<PmsProject> detail(@PathVariable Long id) {
        return R.ok(dataExportService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsProject entity) {
        dataExportService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsProject entity) {
        dataExportService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dataExportService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<PmsProject>> listAll() {
        return R.ok(dataExportService.listAll());
    }
}
