package com.dp.plat.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.constant.CommonConstants;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.result.ResultCode;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.system.dto.LoginRequest;
import com.dp.plat.system.dto.LoginResponse;
import com.dp.plat.system.entity.SysRole;
import com.dp.plat.system.entity.SysUser;
import com.dp.plat.system.entity.SysUserRole;
import com.dp.plat.system.mapper.SysMenuMapper;
import com.dp.plat.system.mapper.SysRoleMapper;
import com.dp.plat.system.mapper.SysUserRoleMapper;
import com.dp.plat.system.security.JwtTokenProvider;
import com.dp.plat.system.security.TokenBlacklistService;
import com.dp.plat.system.security.UserAuthorityService;
import com.dp.plat.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication controller: login, logout, current user info.
 */
@Slf4j
@Tag(name = "认证管理", description = "Authentication APIs")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ISysUserService sysUserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserAuthorityService userAuthorityService;
    private final SysMenuMapper sysMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    @Operation(summary = "Login with username/password and return a JWT token")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = sysUserService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        Map<String, Object> userInfo = buildUserInfo(user);
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .userInfo(userInfo)
                .build();
        return Result.ok(response);
    }

    @Operation(summary = "Logout and blacklist the current JWT token until it expires")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader(CommonConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(CommonConstants.TOKEN_PREFIX)) {
            String token = bearerToken.substring(CommonConstants.TOKEN_PREFIX.length());
            if (jwtTokenProvider.validateToken(token)) {
                String jti = jwtTokenProvider.getJtiFromToken(token);
                long expAt = jwtTokenProvider.getExpirationFromToken(token);
                long remaining = expAt - System.currentTimeMillis();
                if (remaining > 0) {
                    tokenBlacklistService.blacklist(jti, remaining);
                }
            }
        }
        return Result.ok();
    }

    @Operation(summary = "Get current logged-in user info (including roles and permissions)")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        String username = SecurityUtils.getCurrentUsername();
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return Result.ok(null);
        }
        return Result.ok(buildUserInfo(user));
    }

    /**
     * Build the user info map expected by the frontend:
     * {@code { id, username, nickname, email, phone, deptId, roles, permissions }}.
     */
    private Map<String, Object> buildUserInfo(SysUser user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getRealName());
        info.put("email", user.getEmail());
        info.put("phone", user.getPhone());
        info.put("deptId", user.getDeptId());

        // 查询用户角色
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
        Set<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
        List<String> roleCodes;
        if (!roleIds.isEmpty()) {
            List<SysRole> roles = sysRoleMapper.selectList(
                    new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds));
            roleCodes = roles.stream().map(SysRole::getRoleCode).toList();
        } else {
            roleCodes = List.of();
        }
        info.put("roles", roleCodes);

        // 查询用户权限
        boolean isAdmin = roleCodes.contains(CommonConstants.SUPER_ADMIN_ROLE);
        List<String> permissions;
        if (isAdmin) {
            // 超级管理员：加载全部权限
            permissions = sysMenuMapper.listAllPerms();
        } else {
            permissions = sysMenuMapper.listPermsByUserId(user.getId());
        }
        info.put("permissions", permissions);

        return info;
    }
}
