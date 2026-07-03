package com.dp.plat.system.controller;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.result.ResultCode;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.system.dto.LoginRequest;
import com.dp.plat.system.dto.LoginResponse;
import com.dp.plat.system.entity.SysUser;
import com.dp.plat.system.security.JwtTokenProvider;
import com.dp.plat.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .build();
        return Result.ok(response);
    }

    @Operation(summary = "Logout (clears server-side session; client should discard token)")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // Stateless JWT: nothing to invalidate server-side. Client should discard the token.
        return Result.ok();
    }

    @Operation(summary = "Get current logged-in user info")
    @GetMapping("/info")
    public Result<SysUser> info() {
        String username = SecurityUtils.getCurrentUsername();
        SysUser user = sysUserService.getByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }
}
