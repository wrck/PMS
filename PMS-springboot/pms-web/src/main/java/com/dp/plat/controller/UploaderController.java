package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysFileInfo;
import com.dp.plat.service.UploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 上传 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/upload")
public class UploaderController {

    @Autowired
    private UploaderService uploaderService;

    @GetMapping("/list")
    public R<IPage<SysFileInfo>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(uploaderService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysFileInfo> detail(@PathVariable Long id) {
        return R.ok(uploaderService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysFileInfo entity) {
        uploaderService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysFileInfo entity) {
        uploaderService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        uploaderService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysFileInfo>> listAll() {
        return R.ok(uploaderService.listAll());
    }
}
