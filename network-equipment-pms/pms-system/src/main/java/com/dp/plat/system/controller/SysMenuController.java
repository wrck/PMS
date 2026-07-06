package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.system.entity.SysMenu;
import com.dp.plat.system.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<SysMenu>> list() {
        return Result.ok(sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getOrderNum)));
    }

    @Operation(summary = "Get the full menu tree")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<SysMenu>> getMenuTree() {
        List<SysMenu> menus = sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getOrderNum));
        return Result.ok(sysMenuService.buildTree(menus));
    }

    @Operation(summary = "Get the menu tree for the current user (routers)")
    @GetMapping("/routers")
    public Result<List<SysMenu>> getRouters() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<SysMenu> menus = sysMenuService.listMenusByUserId(userId);
        return Result.ok(sysMenuService.buildTree(menus));
    }

    @Operation(summary = "Get menu by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<SysMenu> get(@PathVariable Long id) {
        return Result.ok(sysMenuService.getById(id));
    }

    @Operation(summary = "Create menu")
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    @OperLog(title = "菜单管理", businessType = 1)
    public Result<Boolean> add(@Valid @RequestBody SysMenu menu) {
        return Result.ok(sysMenuService.save(menu));
    }

    @Operation(summary = "Update menu")
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @OperLog(title = "菜单管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody SysMenu menu) {
        return Result.ok(sysMenuService.updateById(menu));
    }

    @Operation(summary = "Delete menu")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @OperLog(title = "菜单管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysMenuService.removeById(id));
    }
}
