package com.dp.plat.common.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link XssFilter} 与 {@link SecurityHeadersFilter} 的单元测试。
 *
 * <p>覆盖：参数清洗、JSON 请求体清洗、富文本白名单保留、
 * 文件上传路径排除以及 7 个安全响应头注入。</p>
 */
class XssFilterTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private XssFilter xssFilter;
    private SecurityHeadersFilter securityHeadersFilter;

    @BeforeEach
    void setUp() {
        xssFilter = new XssFilter();
        securityHeadersFilter = new SecurityHeadersFilter();
    }

    /**
     * 测试 1：普通请求参数中的 HTML 标签被清洗，仅保留文本。
     * <p>{@code <script>alert('xss')</script>} → {@code alert('xss')}</p>
     */
    @Test
    @DisplayName("普通参数被清洗：script 标签剥离保留文本")
    void parameterCleaning() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "<script>alert('xss')</script>");
        request.addParameter("title", "<b>hello</b> world");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);

        assertEquals("alert('xss')", wrapper.getParameter("name"));
        assertEquals("hello world", wrapper.getParameter("title"));
    }

    /**
     * 测试 getParameterValues 与 getParameterMap 同样完成清洗。
     */
    @Test
    @DisplayName("getParameterValues 与 getParameterMap 完成清洗")
    void parameterValuesAndMapCleaning() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("tags", new String[]{"<b>one</b>", "<i>two</i>"});
        request.addParameter("desc", "<script>alert(1)</script>");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);

        assertArrayEquals(new String[]{"one", "two"}, wrapper.getParameterValues("tags"));
        Map<String, String[]> map = wrapper.getParameterMap();
        assertEquals("one", map.get("tags")[0]);
        assertEquals("two", map.get("tags")[1]);
        assertEquals("alert(1)", map.get("desc")[0]);
    }

    /**
     * 测试 2：application/json 请求体中的字符串值被清洗，非字符串值保持不变。
     */
    @Test
    @DisplayName("JSON 请求体被清洗：字符串值剥离标签")
    void jsonBodyCleaning() throws Exception {
        String json = "{\"name\":\"<script>alert('xss')</script>\",\"age\":18,\"active\":true}";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(json.getBytes(StandardCharsets.UTF_8));

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);

        byte[] cleaned = wrapper.getInputStream().readAllBytes();
        JsonNode node = OBJECT_MAPPER.readTree(cleaned);

        assertEquals("alert('xss')", node.get("name").asText());
        assertEquals(18, node.get("age").asInt());
        assertTrue(node.get("active").asBoolean());
    }

    /**
     * JSON 请求体可重复读取（缓存生效），第二次读取结果一致。
     */
    @Test
    @DisplayName("JSON 请求体可重复读取")
    void jsonBodyRepeatableRead() throws Exception {
        String json = "{\"name\":\"<b>x</b>\"}";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(json.getBytes(StandardCharsets.UTF_8));

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);

        String first = new String(wrapper.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String second = new String(wrapper.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        assertEquals(first, second);
        JsonNode node = OBJECT_MAPPER.readTree(first);
        assertEquals("x", node.get("name").asText());
    }

    /**
     * 嵌套 JSON 对象与数组中的字符串值同样被递归清洗。
     */
    @Test
    @DisplayName("嵌套 JSON 字符串递归清洗")
    void nestedJsonCleaning() throws Exception {
        String json = "{\"user\":{\"name\":\"<script>x</script>\"},\"tags\":[\"<b>a</b>\",\"<i>b</i>\"]}";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(json.getBytes(StandardCharsets.UTF_8));

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);

        JsonNode node = OBJECT_MAPPER.readTree(wrapper.getInputStream().readAllBytes());
        assertEquals("x", node.get("user").get("name").asText());
        assertEquals("a", node.get("tags").get(0).asText());
        assertEquals("b", node.get("tags").get(1).asText());
    }

    /**
     * 测试 3：富文本白名单字段（description 等）保留原始内容不进行清洗。
     */
    @Test
    @DisplayName("白名单字段保留原始内容")
    void whitelistFieldPreservation() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("description", "<b>rich text</b>");
        request.addParameter("richContent", "<script>alert(1)</script>");
        request.addParameter("content", "<p>html</p>");
        request.addParameter("name", "<script>alert(1)</script>");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);

        // 白名单字段保留原始 HTML
        assertEquals("<b>rich text</b>", wrapper.getParameter("description"));
        assertEquals("<script>alert(1)</script>", wrapper.getParameter("richContent"));
        assertEquals("<p>html</p>", wrapper.getParameter("content"));
        // 非白名单字段被清洗
        assertEquals("alert(1)", wrapper.getParameter("name"));
    }

    /**
     * JSON 请求体中白名单字段保留原始内容。
     */
    @Test
    @DisplayName("JSON 白名单字段保留原始内容")
    void jsonWhitelistFieldPreservation() throws Exception {
        String json = "{\"description\":\"<b>html</b>\",\"name\":\"<script>x</script>\"}";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(json.getBytes(StandardCharsets.UTF_8));

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);

        JsonNode node = OBJECT_MAPPER.readTree(wrapper.getInputStream().readAllBytes());
        assertEquals("<b>html</b>", node.get("description").asText());
        assertEquals("x", node.get("name").asText());
    }

    /**
     * 测试 4：SecurityHeadersFilter 注入 7 个安全响应头。
     */
    @Test
    @DisplayName("SecurityHeadersFilter 注入 7 个安全响应头")
    void securityHeaders() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        securityHeadersFilter.doFilter(request, response, chain);

        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
        assertEquals("SAMEORIGIN", response.getHeader("X-Frame-Options"));
        assertEquals("1; mode=block", response.getHeader("X-XSS-Protection"));
        assertEquals("max-age=31536000; includeSubDomains", response.getHeader("Strict-Transport-Security"));
        String csp = response.getHeader("Content-Security-Policy");
        assertNotNull(csp);
        assertTrue(csp.contains("default-src 'self'"));
        assertTrue(csp.contains("script-src 'self' 'unsafe-inline'"));
        assertEquals("strict-origin-when-cross-origin", response.getHeader("Referrer-Policy"));
        assertEquals("geolocation=(), microphone=(), camera=()", response.getHeader("Permissions-Policy"));
        // 确保过滤链继续向下传递
        assertNotNull(chain.getRequest());
    }

    /**
     * XssFilter 对文件上传端点排除清洗，请求不被包装。
     */
    @Test
    @DisplayName("文件上传端点排除 XSS 清洗")
    void fileUploadExclusion() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/file/upload");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        xssFilter.doFilter(request, response, chain);

        // 排除路径下请求未被 XssHttpServletRequestWrapper 包装
        assertNotNull(chain.getRequest());
        assertFalse(chain.getRequest() instanceof XssHttpServletRequestWrapper);
    }

    /**
     * XssFilter 对普通端点包装请求。
     */
    @Test
    @DisplayName("普通端点请求被 XssHttpServletRequestWrapper 包装")
    void xssFilterWrapsRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        xssFilter.doFilter(request, response, chain);

        assertNotNull(chain.getRequest());
        assertTrue(chain.getRequest() instanceof XssHttpServletRequestWrapper);
    }

    /**
     * 普通文本参数（不含 HTML）清洗后保持不变。
     */
    @Test
    @DisplayName("普通文本参数清洗后保持不变")
    void plainTextUnchanged() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "normal-text-123");

        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);

        assertEquals("normal-text-123", wrapper.getParameter("name"));
    }
}
