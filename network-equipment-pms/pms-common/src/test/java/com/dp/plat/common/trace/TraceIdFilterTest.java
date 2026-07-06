package com.dp.plat.common.trace;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TraceIdFilter} 的单元测试。
 *
 * <p>覆盖三个核心场景：</p>
 * <ol>
 *   <li>无 {@code X-Trace-Id} 请求头：自动生成 32 位无连字符 UUID 并回写响应头</li>
 *   <li>有 {@code X-Trace-Id} 请求头：沿用上游 traceId 并回写响应头</li>
 *   <li>请求结束后 MDC 已清理，避免线程池污染</li>
 * </ol>
 */
class TraceIdFilterTest {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private TraceIdFilter traceIdFilter;

    @BeforeEach
    void setUp() {
        traceIdFilter = new TraceIdFilter();
        // 测试间清理 MDC，避免相互污染
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    /**
     * 测试 1：请求未携带 X-Trace-Id 时，过滤器自动生成 32 位无连字符 UUID，
     * 写入 MDC，并回写到响应头。
     */
    @Test
    @DisplayName("无 X-Trace-Id 请求头：自动生成 UUID 并写入响应头")
    void generateTraceIdWhenAbsent() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // 不设置 X-Trace-Id 头
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        traceIdFilter.doFilter(request, response, chain);

        String responseTraceId = response.getHeader(TRACE_ID_HEADER);
        assertNotNull(responseTraceId, "响应头应包含 X-Trace-Id");
        assertFalse(responseTraceId.contains("-"), "traceId 应为无连字符的 UUID");
        assertEquals(32, responseTraceId.length(), "traceId 应为 32 位无连字符 UUID");
        // 链路未被中断
        assertNotNull(chain.getRequest(), "过滤器链应继续向下传递");
    }

    /**
     * 测试 2：请求携带 X-Trace-Id 时，过滤器沿用上游 traceId，
     * 写入 MDC，并回写到响应头。
     */
    @Test
    @DisplayName("有 X-Trace-Id 请求头：沿用上游 traceId 并写入响应头")
    void reuseTraceIdWhenPresent() throws IOException, ServletException {
        String upstreamTraceId = "abcdef0123456789abcdef0123456789";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(TRACE_ID_HEADER, upstreamTraceId);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        traceIdFilter.doFilter(request, response, chain);

        // 沿用上游 traceId，不重新生成
        assertEquals(upstreamTraceId, response.getHeader(TRACE_ID_HEADER),
                "响应头 X-Trace-Id 应与上游一致");
        // 过滤器链继续
        assertNotNull(chain.getRequest());
        // 在 doFilter 执行过程中 traceId 应已注入 MDC（此处响应已返回，验证回写值即可）
        assertNotEquals("", response.getHeader(TRACE_ID_HEADER));
    }

    /**
     * 测试 3：请求处理结束后，MDC 中的 traceId 必须被清理，
     * 避免线程池场景下上下文串扰。
     */
    @Test
    @DisplayName("请求结束后 MDC 已清理")
    void mdcClearedAfterRequest() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // 在过滤器链下游验证 MDC 已被填充
        AtomicReference<String> traceIdDuringRequest = new AtomicReference<>(null);
        Filter verifyingFilter = new Filter() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
                    throws IOException, ServletException {
                traceIdDuringRequest.set(MDC.get("traceId"));
                chain.doFilter(req, resp);
            }
        };
        MockFilterChain chain = new MockFilterChain(new NoOpServlet(), verifyingFilter);

        traceIdFilter.doFilter(request, response, chain);

        // 请求处理过程中 MDC 应被填充
        assertNotNull(traceIdDuringRequest.get(),
                "请求处理过程中 MDC 应包含 traceId");
        assertFalse(traceIdDuringRequest.get().isEmpty(),
                "请求处理过程中 MDC.traceId 不应为空");
        // 请求结束后 MDC 应被清理
        assertNull(MDC.get("traceId"), "请求结束后 MDC.traceId 必须被清理");
    }

    /**
     * 测试 4：空字符串 X-Trace-Id 应视为缺失，自动生成新值。
     */
    @Test
    @DisplayName("空字符串 X-Trace-Id 视为缺失，自动生成新 traceId")
    void blankTraceIdTreatedAsAbsent() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(TRACE_ID_HEADER, "  ");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        traceIdFilter.doFilter(request, response, chain);

        String responseTraceId = response.getHeader(TRACE_ID_HEADER);
        assertNotNull(responseTraceId);
        assertFalse(responseTraceId.isBlank());
        assertEquals(32, responseTraceId.length(), "应生成 32 位 UUID");
        assertNotEquals("  ", responseTraceId);
    }

    /**
     * 空操作的 Servlet，仅用于满足 {@link MockFilterChain} 的构造参数要求。
     * 实际请求处理逻辑由注入的 {@link Filter} 完成。
     */
    private static class NoOpServlet extends jakarta.servlet.http.HttpServlet {
        // 故意空实现，仅占位
    }
}
