package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.RoleMenuPower;
import com.dp.plat.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色菜单 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/system/role-menu")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

    @GetMapping("/list")
    public R<IPage<RoleMenuPower>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(roleMenuService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<RoleMenuPower> detail(@PathVariable Long id) {
        return R.ok(roleMenuService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody RoleMenuPower entity) {
        roleMenuService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody RoleMenuPower entity) {
        roleMenuService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        roleMenuService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<RoleMenuPower>> listAll() {
        return R.ok(roleMenuService.listAll());
    }
}
