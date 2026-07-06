package com.dp.plat.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 幂等键透传拦截器：从 {@code X-Idempotent-Key} 请求头读取幂等键，
 * 写入 request attribute，供 SpEL 表达式（如 {@code #request.getAttribute('idempotentKey')})
 * 或后续组件引用。
 *
 * <p>设计目的：</p>
 * <ul>
 *   <li>统一在请求入口捕获幂等键，避免下游组件重复读取 header</li>
 *   <li>为非 Controller 入口（如 Filter、Servlet）提供幂等键的统一访问点</li>
 *   <li>记录幂等键日志，便于全链路追踪</li>
 * </ul>
 *
 * <p>注意：{@link IdempotentAspect} 默认直接通过 {@code RequestContextHolder}
 * 读取请求头，本拦截器并非切面运行的必要条件，但提供了 attribute 形式的
 * 备用访问方式与日志追踪能力。</p>
 *
 * <p>注册方式：由 {@code WebMvcConfig} 通过
 * {@code registry.addInterceptor(new IdempotentKeyInterceptor()).addPathPatterns("/**")}
 * 注册到 Spring MVC。</p>
 */
@Slf4j
@Component
public class IdempotentKeyInterceptor implements HandlerInterceptor {

    /** Request attribute key，存储从请求头读取的幂等键。 */
    public static final String ATTR_IDEMPOTENT_KEY = "idempotentKey";

    /** 请求头名称。 */
    public static final String HEADER_IDEMPOTENT_KEY = "X-Idempotent-Key";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String idempotentKey = request.getHeader(HEADER_IDEMPOTENT_KEY);
        if (idempotentKey != null && !idempotentKey.isBlank()) {
            // 写入 request attribute，供 SpEL 或后续组件引用
            request.setAttribute(ATTR_IDEMPOTENT_KEY, idempotentKey);
            if (log.isDebugEnabled()) {
                log.debug("捕获幂等键: uri={}, key={}", request.getRequestURI(), idempotentKey);
            }
        }
        return true;
    }
}
