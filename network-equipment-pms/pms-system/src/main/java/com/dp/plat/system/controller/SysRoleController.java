package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysRole;
import com.dp.plat.system.service.ISysRoleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Role management controller.
 */
@Tag(name = "角色管理", description = "Role management APIs")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService sysRoleService;

    @Operation(summary = "Paginated role query")
    @GetMapping("/page")
    public Result<Page<SysRole>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String roleName) {
        Page<SysRole> page = sysRoleService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysRole>().like(roleName != null, SysRole::getRoleName, roleName));
        return Result.ok(page);
    }

    @Operation(summary = "Get role by id")
    @GetMapping("/{id}")
    public Result<SysRole> get(@PathVariable Long id) {
        return Result.ok(sysRoleService.getById(id));
    }

    @Operation(summary = "Create role")
    @PostMapping
    public Result<Boolean> add(@RequestBody SysRole role) {
        return Result.ok(sysRoleService.save(role));
    }

    @Operation(summary = "Update role")
    @PutMapping
    public Result<Boolean> update(@RequestBody SysRole role) {
        return Result.ok(sysRoleService.updateById(role));
    }

    @Operation(summary = "Delete role")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysRoleService.removeById(id));
    }
}
