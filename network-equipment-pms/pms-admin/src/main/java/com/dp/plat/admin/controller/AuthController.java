package com.dp.plat.admin.controller;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.iocoder.yudao.module.system.service.auth.AdminAuthService;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.entity.SysUser;
import com.dp.plat.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * PMS 认证控制器（已弃用）。
 *
 * <p><b>@Deprecated</b>：前端已直接适配 yudao 原生认证接口
 * {@code /admin-api/system/auth/login}、{@code /admin-api/system/auth/logout}、
 * {@code /admin-api/system/auth/get-permission-info}。yudao 原生 AuthController
 * 已通过 {@link com.dp.plat.admin.PmsApplication} 的 ComponentScan 放开加载，
 * 本包装层不再需要，待下阶段完全清理后删除。</p>
 *
 * <p>历史说明：本控制器曾用于在 yudao AuthController 被排除加载时，
 * 直接调用 yudao AdminAuthService（Service 层）提供 /api/auth/* 认证入口。</p>
 *
 * <p>注：PMS SysUserService 中与 yudao AdminUserService 重叠的方法也已标记 @Deprecated，
 * 待完全迁移至 yudao 体系后清理。</p>
 */
@Deprecated
@Tag(name = "PMS 认证（已弃用，请使用 /admin-api/system/auth/*）")
@RestController("pmsAuthController")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final ISysUserService sysUserService;

    @Resource
    private AdminAuthService adminAuthService;

    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "账号密码登录")
    public Result<Map<String, Object>> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        AuthLoginRespVO loginResp = adminAuthService.login(reqVO);
        SysUser user = sysUserService.getById(loginResp.getUserId());
        if (user == null) {
            log.warn("登录成功但 PMS sys_user 中找不到 userId={}", loginResp.getUserId());
            throw new RuntimeException("用户信息不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getRealName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("deptId", user.getDeptId());
        userInfo.put("roles", Collections.singletonList("super_admin"));
        userInfo.put("permissions", Collections.singletonList("*"));

        Map<String, Object> data = new HashMap<>();
        data.put("token", loginResp.getAccessToken());
        data.put("userInfo", userInfo);

        return Result.ok(data);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<Map<String, Object>> info() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return Result.fail(401, "未登录");
        }
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getRealName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("deptId", user.getDeptId());
        userInfo.put("roles", Collections.singletonList("super_admin"));
        userInfo.put("permissions", Collections.singletonList("*"));

        return Result.ok(userInfo);
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出")
    public Result<Void> logout(jakarta.servlet.http.HttpServletRequest request) {
        try {
            String token = SecurityFrameworkUtils.obtainAuthorization(request, "Authorization", "token");
            if (token != null) {
                adminAuthService.logout(token, 100);
            }
        } catch (Exception e) {
            log.warn("登出时发生异常（忽略）: {}", e.getMessage());
        }
        return Result.ok();
    }
}
