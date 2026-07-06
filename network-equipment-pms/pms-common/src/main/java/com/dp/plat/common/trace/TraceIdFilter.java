package com.dp.plat.common.trace;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * 链路追踪 ID 过滤器。
 *
 * <p>负责在每个 HTTP 请求生命周期内为日志统一注入 {@code traceId}：</p>
 * <ol>
 *   <li>优先从请求头 {@code X-Trace-Id} 提取上游传入的 traceId，便于跨服务串联链路。</li>
 *   <li>若上游未传入或值为空白，则自动生成 32 位无连字符 UUID 作为本次请求的 traceId。</li>
 *   <li>将 traceId 写入 MDC（{@value #MDC_TRACE_ID}），使所有日志输出自动携带该字段。</li>
 *   <li>同时将 traceId 回写到响应头 {@code X-Trace-Id}，便于前端/调用方排查问题。</li>
 *   <li>请求结束后在 {@code finally} 中清理 MDC，避免线程池场景下上下文串扰。</li>
 * </ol>
 *
 * <p>过滤器顺序：{@link Ordered#HIGHEST_PRECEDENCE}，确保在所有业务过滤器
 * （含安全、限流、XSS 等）之前完成 traceId 注入，使后续过滤器产生的日志
 * 也能携带链路标识。</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements Filter {

    /** 请求头 / 响应头名称：链路追踪 ID。 */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /** MDC 中 traceId 的键名，需与 logback-spring.xml 中的 includeMdcKeyName 保持一致。 */
    public static final String MDC_TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. 提取或生成 traceId
        String traceId = resolveTraceId(httpRequest);

        // 2. 写入 MDC，使后续日志输出自动携带该字段
        MDC.put(MDC_TRACE_ID, traceId);

        // 3. 回写响应头，便于客户端/前端关联日志
        httpResponse.setHeader(TRACE_ID_HEADER, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            // 4. 必须清理 MDC，避免线程池复用时上下文串扰
            MDC.remove(MDC_TRACE_ID);
        }
    }

    /**
     * 从请求头提取 traceId，缺失或空白则生成 32 位无连字符 UUID。
     *
     * @param request HTTP 请求
     * @return 非空 traceId
     */
    private String resolveTraceId(HttpServletRequest request) {
        String headerTraceId = request.getHeader(TRACE_ID_HEADER);
        if (StringUtils.hasText(headerTraceId)) {
            // 沿用上游 traceId，保证全链路一致
            return headerTraceId.trim();
        }
        // 生成 32 位无连字符 UUID（去除 -），更紧凑且适合作为日志键
        return UUID.randomUUID().toString().replace("-", "");
    }
}
