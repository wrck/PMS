package com.dp.plat.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** 密码过期检测拦截器 - 迁移自 PasswordInterceptor */
@Component
public class PasswordExpiredInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO: 检查用户密码是否过期，过期则跳转到修改密码页面
        return true;
    }
}
