package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysMenu;
import com.dp.plat.system.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Menu management controller.
 */
@Tag(name = "菜单管理", description = "Menu management APIs")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final ISysMenuService sysMenuService;

    @Operation(summary = "List all menus")
    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        return Result.ok(sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getOrderNum)));
    }

    @Operation(summary = "Get menu by id")
    @GetMapping("/{id}")
    public Result<SysMenu> get(@PathVariable Long id) {
        return Result.ok(sysMenuService.getById(id));
    }

    @Operation(summary = "Create menu")
    @PostMapping
    public Result<Boolean> add(@RequestBody SysMenu menu) {
        return Result.ok(sysMenuService.save(menu));
    }

    @Operation(summary = "Update menu")
    @PutMapping
    public Result<Boolean> update(@RequestBody SysMenu menu) {
        return Result.ok(sysMenuService.updateById(menu));
    }

    @Operation(summary = "Delete menu")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysMenuService.removeById(id));
    }
}
