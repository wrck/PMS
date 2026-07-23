package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysRole;
import com.dp.plat.system.service.ISysRoleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PreAuthorize("@ss.hasPermission('system:role:list')")
    public Result<Page<SysRole>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String roleName) {
        Page<SysRole> page = sysRoleService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysRole>().like(roleName != null, SysRole::getRoleName, roleName));
        return Result.ok(page);
    }

    /**
     * 全部角色列表（审批链配置等场景下拉用）。
     *
     * <p>仅需登录（无需 system:role:list 权限），返回 id/roleName/roleCode 字段，
     * 供低代码审批链配置页选择 approverRole。</p>
     */
    @Operation(summary = "List all roles for dropdown")
    @GetMapping("/all")
    public Result<List<SysRole>> all() {
        List<SysRole> roles = sysRoleService.list(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, "0")
                .orderByAsc(SysRole::getId));
        // 仅返回下拉所需字段
        List<SysRole> safe = roles.stream().map(r -> {
            SysRole v = new SysRole();
            v.setId(r.getId());
            v.setRoleName(r.getRoleName());
            v.setRoleCode(r.getRoleCode());
            return v;
        }).toList();
        return Result.ok(safe);
    }

    @Operation(summary = "Get role by id")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:role:list')")
    public Result<SysRole> get(@PathVariable Long id) {
        return Result.ok(sysRoleService.getById(id));
    }

    @Operation(summary = "Create role")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:role:add')")
    @OperLog(title = "角色管理", businessType = 1)
    public Result<Boolean> add(@Valid @RequestBody SysRole role) {
        return Result.ok(sysRoleService.save(role));
    }

    @Operation(summary = "Update role")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @OperLog(title = "角色管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody SysRole role) {
        return Result.ok(sysRoleService.updateById(role));
    }

    @Operation(summary = "Delete role")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:role:remove')")
    @OperLog(title = "角色管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysRoleService.removeById(id));
    }

    @Operation(summary = "Assign menus to a role (replaces existing assignment)")
    @PostMapping("/{id}/menus")
    @PreAuthorize("@ss.hasPermission('system:role:edit')")
    @OperLog(title = "角色管理", businessType = 2)
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignMenus(id, menuIds);
        return Result.ok();
    }
}
