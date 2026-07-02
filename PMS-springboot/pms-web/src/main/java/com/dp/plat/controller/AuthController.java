package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.dto.LoginDTO;
import com.dp.plat.model.vo.LoginVO;
import com.dp.plat.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器 - 迁移自老系统 LoginAction
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** 用户登录 */
    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO vo = authService.login(dto);
        return R.ok(vo);
    }

    /** 用户登出 */
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return R.ok();
    }

    /** 刷新Token */
    @PostMapping("/refresh")
    public R<String> refresh(@RequestHeader("Authorization") String token) {
        String newToken = authService.refreshToken(token);
        return R.ok(newToken);
    }

    /** 获取当前用户信息 */
    @GetMapping("/info")
    public R<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        Map<String, Object> info = authService.getCurrentUser(token);
        return R.ok(info);
    }

    /** 修改密码 */
    @PostMapping("/change-password")
    public R<Void> changePassword(@RequestBody Map<String, String> params) {
        authService.changePassword(params.get("oldPassword"), params.get("newPassword"));
        return R.ok();
    }
}
