package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysRole;
import com.dp.plat.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public R<IPage<SysRole>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(roleService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysRole> detail(@PathVariable Long id) {
        return R.ok(roleService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysRole entity) {
        roleService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysRole entity) {
        roleService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysRole>> listAll() {
        return R.ok(roleService.listAll());
    }
}
