package com.dp.plat.config;

import com.dp.plat.common.utils.JwtUtil;
import com.dp.plat.common.result.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${jwt.header:Authorization}")
    private String header;

    @Value("${jwt.prefix:Bearer }")
    private String prefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 登录和公开接口跳过
        String uri = request.getRequestURI();
        if (uri.contains("/api/auth/login") || uri.contains("/api/auth/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(header);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(prefix)) {
            String token = authHeader.substring(prefix.length());
            try {
                String username = JwtUtil.getUsername(token);
                if (StringUtils.hasText(username)) {
                    request.setAttribute("currentUsername", username);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception ignored) {
                // token 无效，继续到下面返回 401
            }
        }

        // 未认证
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(R.fail(401, "未登录或Token已过期")));
    }
}
