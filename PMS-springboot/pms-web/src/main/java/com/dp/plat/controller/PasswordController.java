package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 密码管理 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/password")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @GetMapping("/list")
    public R<IPage<SysUser>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(passwordService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysUser> detail(@PathVariable Long id) {
        return R.ok(passwordService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysUser entity) {
        passwordService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysUser entity) {
        passwordService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        passwordService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysUser>> listAll() {
        return R.ok(passwordService.listAll());
    }
}
