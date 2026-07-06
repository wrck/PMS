package com.dp.plat.common.trace;

import com.dp.plat.common.util.SecurityUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用户上下文过滤器。
 *
 * <p>在每个 HTTP 请求处理过程中，从 Spring Security 上下文提取当前登录用户的
 * userId 与 username，写入 MDC，使日志输出能自动携带用户标识，便于：</p>
 * <ul>
 *   <li>按用户检索日志（user-centric troubleshooting）</li>
 *   <li>审计日志关联用户操作</li>
 *   <li>异常根因分析（识别具体用户触发的问题）</li>
 * </ul>
 *
 * <p>过滤器顺序：{@link Ordered#HIGHEST_PRECEDENCE} + 10，
 * 确保晚于 {@link TraceIdFilter}（HIGHEST_PRECEDENCE）执行，
 * 但早于业务过滤器，使得后续过滤器输出的日志也能携带用户上下文。</p>
 *
 * <p>说明：本过滤器在 Spring Security 链路之后执行时，
 * {@link SecurityUtils#getAuthentication()} 才能拿到已认证的 Authentication。
 * 由于 Spring Security 的 {@code JwtAuthenticationFilter} 通过
 * {@code addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)}
 * 注册，而 Servlet 容器级别的 Filter（本类）会在 Spring Security FilterChain 之前执行，
 * 因此本类实际读取的是请求线程在进入 Security 链之前的 SecurityContext（通常为空）。</p>
 *
 * <p>真正能在日志中携带 userId 的场景：</p>
 * <ol>
 *   <li>异步线程池任务（已通过 {@code DelegatingSecurityContextExecutor} 传播上下文）</li>
 *   <li>Spring Security 处理完成后由后续 Filter/Interceptor 再次执行 MDC 写入</li>
 * </ol>
 *
 * <p>对异步线程场景，可通过 {@code MDCContextTaskDecorator} 在任务提交时复制 MDC。
 * 本类提供基础的请求级 MDC 注入能力，是上述能力的入口。</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class UserContextFilter implements Filter {

    /** MDC 中 userId 的键名，需与 logback-spring.xml 中的 includeMdcKeyName 保持一致。 */
    public static final String MDC_USER_ID = "userId";

    /** MDC 中 username 的键名，需与 logback-spring.xml 中的 includeMdcKeyName 保持一致。 */
    public static final String MDC_USERNAME = "username";

    /** MDC 中 requestUri 的键名，便于按请求路径检索日志。 */
    public static final String MDC_REQUEST_URI = "requestUri";

    /** MDC 中 HTTP method 的键名，便于按请求方法检索日志。 */
    public static final String MDC_METHOD = "method";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // 1. 注入请求元信息（URI、HTTP 方法）
            injectRequestContext(request);
            // 2. 注入用户上下文（userId、username）
            injectUserContext();
            chain.doFilter(request, response);
        } finally {
            // 3. 必须清理 MDC，避免线程池复用时上下文串扰
            MDC.remove(MDC_USER_ID);
            MDC.remove(MDC_USERNAME);
            MDC.remove(MDC_REQUEST_URI);
            MDC.remove(MDC_METHOD);
        }
    }

    /**
     * 注入请求元信息（URI、HTTP 方法）到 MDC，便于按请求路径筛选日志。
     *
     * @param request Servlet 请求
     */
    private void injectRequestContext(ServletRequest request) {
        if (request instanceof jakarta.servlet.http.HttpServletRequest httpRequest) {
            MDC.put(MDC_REQUEST_URI, httpRequest.getRequestURI());
            MDC.put(MDC_METHOD, httpRequest.getMethod());
        }
    }

    /**
     * 从 Spring Security 上下文提取当前用户信息并写入 MDC。
     * 未认证时不写入，保持 MDC 中无 userId/username 字段。
     */
    private void injectUserContext() {
        if (!SecurityUtils.isAuthenticated()) {
            return;
        }
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            MDC.put(MDC_USER_ID, String.valueOf(userId));
        }
        String username = SecurityUtils.getCurrentUsername();
        if (username != null) {
            MDC.put(MDC_USERNAME, username);
        }
    }
}
