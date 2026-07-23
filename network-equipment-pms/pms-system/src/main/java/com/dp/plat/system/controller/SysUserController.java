package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysUser;
import com.dp.plat.system.service.ISysUserService;
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
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    public Result<Page<SysUser>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String username) {
        Page<SysUser> page = sysUserService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysUser>().like(username != null, SysUser::getUsername, username));
        return Result.ok(page);
    }

    /**
     * 用户搜索（@提及自动补全用）。
     *
     * <p>仅需登录（无需 system:user:list 权限），按用户名/真实姓名模糊匹配，
     * 返回最多 20 条，仅包含 id/username/realName 字段。</p>
     */
    @Operation(summary = "Search users for @mention autocomplete")
    @GetMapping("/search")
    public Result<List<SysUser>> search(@RequestParam(required = false) String keyword,
                                        @RequestParam(defaultValue = "20") Integer limit) {
        int safeLimit = limit == null || limit <= 0 ? 20 : Math.min(limit, 50);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .and(keyword != null && !keyword.isBlank(), w -> w
                        .like(SysUser::getUsername, keyword)
                        .or().like(SysUser::getRealName, keyword))
                .eq(SysUser::getStatus, "0")
                .last("LIMIT " + safeLimit);
        List<SysUser> users = sysUserService.list(wrapper);
        // 仅返回 @提及所需的字段，避免泄露 email/phone 等敏感信息
        List<SysUser> safe = users.stream().map(u -> {
            SysUser v = new SysUser();
            v.setId(u.getId());
            v.setUsername(u.getUsername());
            v.setRealName(u.getRealName());
            return v;
        }).toList();
        return Result.ok(safe);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    public Result<SysUser> get(@PathVariable Long id) {
        return Result.ok(sysUserService.getById(id));
    }

    @Operation(summary = "Create user")
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:user:add')")
    @OperLog(title = "用户管理", businessType = 1)
    public Result<Boolean> add(@Valid @RequestBody SysUser user) {
        return Result.ok(sysUserService.save(user));
    }

    @Operation(summary = "Update user")
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @OperLog(title = "用户管理", businessType = 2)
    public Result<Boolean> update(@Valid @RequestBody SysUser user) {
        return Result.ok(sysUserService.updateById(user));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('system:user:remove')")
    @OperLog(title = "用户管理", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysUserService.removeById(id));
    }
}
