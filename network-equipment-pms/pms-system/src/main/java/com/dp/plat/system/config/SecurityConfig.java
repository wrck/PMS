package com.dp.plat.system.config;

import com.dp.plat.common.filter.RateLimitFilter;
import com.dp.plat.common.filter.SecurityHeadersFilter;
import com.dp.plat.common.filter.XssFilter;
import com.dp.plat.common.result.Result;
import com.dp.plat.system.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 6 configuration.
 *
 * <p>过滤器链顺序：{@link SecurityHeadersFilter} → {@link RateLimitFilter} →
 * {@link XssFilter} → {@link JwtAuthenticationFilter} →
 * {@link UsernamePasswordAuthenticationFilter}，确保安全响应头最先注入、
 * 敏感端点 IP 限流在业务处理之前完成、XSS 清洗在鉴权之前完成。</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final XssFilter xssFilter;
    private final SecurityHeadersFilter securityHeadersFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        // 帮助中心：list/{id}/categories 公开访问（无需登录即可浏览帮助文档）
                        .requestMatchers(HttpMethod.GET,
                                "/api/system/help-content/list",
                                "/api/system/help-content/categories",
                                "/api/system/help-content/*").permitAll()
                        // Actuator 端点放行（由 Actuator 自身安全或网络层控制访问）
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/doc.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .anyRequest().authenticated())
                // 未认证返回 401 JSON，权限不足返回 403 JSON（默认行为返回空体 403，前端无法识别）
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(
                                    new ObjectMapper().writeValueAsString(
                                            Result.fail(401, "未登录或登录已过期，请重新登录")));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(
                                    new ObjectMapper().writeValueAsString(
                                            Result.fail(403, "权限不足，无法访问该资源")));
                        }))
                // 注册顺序（从后往前）：确保每次 addFilterBefore 引用的锚点过滤器已注册
                // 最终链顺序：SecurityHeadersFilter → RateLimitFilter → XssFilter → JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(xssFilter, JwtAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, XssFilter.class)
                .addFilterBefore(securityHeadersFilter, RateLimitFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
