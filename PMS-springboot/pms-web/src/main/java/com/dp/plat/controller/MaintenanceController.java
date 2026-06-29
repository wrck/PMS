package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmsMaintenance;
import com.dp.plat.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {
    @Autowired
    private MaintenanceService service;

    @GetMapping("/list")
    public R<IPage<PmsMaintenance>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) Long projectId,
                                          @RequestParam(required = false) String maintenanceType) {
        return R.ok(service.queryPage(pageNum, pageSize, projectId, maintenanceType));
    }

    @GetMapping("/{id}")
    public R<PmsMaintenance> detail(@PathVariable Long id) {
        return R.ok(service.getDetail(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody PmsMaintenance m) {
        service.create(m);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody PmsMaintenance m) {
        service.update(m);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok();
    }
}
