package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.annotation.OperLog;
import com.dp.plat.system.entity.SysUser;
import com.dp.plat.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * User management controller.
 */
@Tag(name = "用户管理", description = "User management APIs")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService sysUserService;

    @Operation(summary = "Paginated user query")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<Page<SysUser>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String username) {
        Page<SysUser> page = sysUserService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysUser>().like(username != null, SysUser::getUsername, username));
        return Result.ok(page);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<SysUser> get(@PathVariable Long id) {
        return Result.ok(sysUserService.getById(id));
    }

    @Operation(summary = "Create user")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    @OperLog(title = "用户管理", businessType = 1)
    public Result<Boolean> add(@RequestBody SysUser user) {
        return Result.ok(sysUserService.save(user));
    }

    @Operation(summary = "Update user")
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperLog(title = "用户管理", businessType = 2)
    public Result<Boolean> update(@RequestBody SysUser user) {
        return Result.ok(sysUserService.updateById(user));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:remove')")
    @OperLog(title = "用户管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysUserService.removeById(id));
    }
}
