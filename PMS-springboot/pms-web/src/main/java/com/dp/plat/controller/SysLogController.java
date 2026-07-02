package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysOperateLog;
import com.dp.plat.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统日志 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @GetMapping("/list")
    public R<IPage<SysOperateLog>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(sysLogService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysOperateLog> detail(@PathVariable Long id) {
        return R.ok(sysLogService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysOperateLog entity) {
        sysLogService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysOperateLog entity) {
        sysLogService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        sysLogService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysOperateLog>> listAll() {
        return R.ok(sysLogService.listAll());
    }
}
