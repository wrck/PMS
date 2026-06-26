package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.dto.LoginDTO;
import com.dp.plat.model.vo.LoginVO;
import com.dp.plat.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO vo = authService.login(dto);
        return R.ok(vo);
    }

    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        authService.logout(username);
        return R.ok();
    }

    @GetMapping("/info")
    public R<LoginVO> getUserInfo(HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        LoginVO vo = authService.getUserInfo(username);
        return R.ok(vo);
    }
}
