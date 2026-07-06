package com.dp.plat.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 安全响应头过滤器。
 *
 * <p>在请求继续向下传递前为响应注入 7 个标准安全头，覆盖常见的
 * 内容嗅探、点击劫持、XSS、传输安全、内容来源、引用策略与浏览器权限
 * 等维度的防护。</p>
 *
 * <ul>
 *   <li>{@code X-Content-Type-Options: nosniff} —— 禁止 MIME 嗅探</li>
 *   <li>{@code X-Frame-Options: SAMEORIGIN} —— 仅允许同源 iframe 嵌入</li>
 *   <li>{@code X-XSS-Protection: 1; mode=block} —— 启用浏览器 XSS 过滤器</li>
 *   <li>{@code Strict-Transport-Security} —— 强制 HTTPS（含子域名，1 年）</li>
 *   <li>{@code Content-Security-Policy} —— 限制资源加载来源</li>
 *   <li>{@code Referrer-Policy} —— 跨源仅保留来源</li>
 *   <li>{@code Permissions-Policy} —— 禁用地理位置/麦克风/摄像头</li>
 * </ul>
 */
@Component
public class SecurityHeadersFilter implements Filter {

    private static final String HEADER_X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    private static final String HEADER_X_FRAME_OPTIONS = "X-Frame-Options";
    private static final String HEADER_X_XSS_PROTECTION = "X-XSS-Protection";
    private static final String HEADER_STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
    private static final String HEADER_CONTENT_SECURITY_POLICY = "Content-Security-Policy";
    private static final String HEADER_REFERRER_POLICY = "Referrer-Policy";
    private static final String HEADER_PERMISSIONS_POLICY = "Permissions-Policy";

    private static final String VALUE_NOSNIFF = "nosniff";
    private static final String VALUE_SAMEORIGIN = "SAMEORIGIN";
    private static final String VALUE_XSS_BLOCK = "1; mode=block";
    private static final String VALUE_HSTS = "max-age=31536000; includeSubDomains";
    private static final String VALUE_CSP = "default-src 'self'; script-src 'self' 'unsafe-inline'; "
            + "style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; "
            + "connect-src 'self' wss: ws:";
    private static final String VALUE_REFERRER = "strict-origin-when-cross-origin";
    private static final String VALUE_PERMISSIONS = "geolocation=(), microphone=(), camera=()";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            applySecurityHeaders(httpResponse);
        }
        chain.doFilter(request, response);
    }

    /**
     * 向响应中注入安全响应头。使用 {@link HttpServletResponse#setHeader} 确保
     * 头值唯一，避免重复执行时叠加。
     *
     * @param response HTTP 响应
     */
    private void applySecurityHeaders(HttpServletResponse response) {
        response.setHeader(HEADER_X_CONTENT_TYPE_OPTIONS, VALUE_NOSNIFF);
        response.setHeader(HEADER_X_FRAME_OPTIONS, VALUE_SAMEORIGIN);
        response.setHeader(HEADER_X_XSS_PROTECTION, VALUE_XSS_BLOCK);
        response.setHeader(HEADER_STRICT_TRANSPORT_SECURITY, VALUE_HSTS);
        response.setHeader(HEADER_CONTENT_SECURITY_POLICY, VALUE_CSP);
        response.setHeader(HEADER_REFERRER_POLICY, VALUE_REFERRER);
        response.setHeader(HEADER_PERMISSIONS_POLICY, VALUE_PERMISSIONS);
    }
}
