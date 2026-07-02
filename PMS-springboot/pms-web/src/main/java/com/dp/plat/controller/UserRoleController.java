package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/user-role")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/list")
    public R<IPage<SysUser>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(userRoleService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<SysUser> detail(@PathVariable Long id) {
        return R.ok(userRoleService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody SysUser entity) {
        userRoleService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody SysUser entity) {
        userRoleService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        userRoleService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<SysUser>> listAll() {
        return R.ok(userRoleService.listAll());
    }
}
