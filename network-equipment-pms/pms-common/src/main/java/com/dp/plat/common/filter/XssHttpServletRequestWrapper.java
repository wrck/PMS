package com.dp.plat.common.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * XSS 防护请求包装器。
 *
 * <p>对请求参数与 application/json 请求体进行 HTML 标签清洗，剥离所有
 * HTML 标签仅保留文本内容，从而阻断 XSS 注入。富文本字段（如
 * {@code richContent}、{@code description}）通过白名单保留原始内容，
 * 由业务侧自行做富文本安全处理。</p>
 *
 * <p>清洗策略：</p>
 * <ul>
 *   <li>使用 {@link Jsoup#clean(String, Safelist)} 配合 {@link Safelist#none()}
 *       移除全部 HTML 标签并保留文本。</li>
 *   <li>由于 {@code <script>}/{@code <style>} 的内容以 DataNode 形式存储，
 *       会被 Cleaner 丢弃，因此先将其内容转换为 TextNode 以保留可见文本。</li>
 *   <li>JSON 请求体缓存后递归清洗字符串节点，再通过 {@link #getInputStream()}
 *       回放清洗后的字节，保证后续 Controller 可重复读取。</li>
 * </ul>
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /** Jsoup 安全白名单：不允许任何 HTML 标签，仅保留纯文本。 */
    private static final Safelist NONE_SAFELIST = Safelist.none();

    /** 富文本白名单字段名（小写匹配），这些字段保留原始内容不进行清洗。 */
    private static final Set<String> RICH_TEXT_FIELDS = Set.of(
            "richcontent", "description", "content", "remark", "notice", "html"
    );

    /** Jackson ObjectMapper，用于 JSON 请求体的解析与重新序列化。 */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** 缓存的（已清洗）请求体字节，懒加载，仅对 application/json 请求生效。 */
    private byte[] cachedBody;

    /**
     * 构造包装器。
     *
     * @param request 原始请求
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 清洗单个参数值，剥离 HTML 标签仅保留文本。
     *
     * @param value 原始值
     * @return 清洗后的值，入参为 null/空则原样返回
     */
    private static String cleanValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        // 使用 parseBodyFragment 将内容解析到 body 中（Jsoup.parse 会把 <script> 放入 head，
        // 导致 body().html() 取不到内容）。再将 script/style 的 DataNode 内容转为 TextNode，
        // 避免 Jsoup.clean 清洗时丢弃其文本内容。
        Document doc = Jsoup.parseBodyFragment(value);
        for (Element el : doc.select("script, style")) {
            String data = el.data();
            el.replaceWith(new org.jsoup.nodes.TextNode(data == null ? "" : data));
        }
        // 使用 Jsoup.clean 配合 Safelist.none() 移除全部标签并保留文本
        String cleaned = Jsoup.clean(doc.body().html(), NONE_SAFELIST);
        return cleaned;
    }

    /**
     * 判断字段是否属于富文本白名单（不进行清洗）。
     *
     * @param fieldName 字段名
     * @return true 表示保留原始内容
     */
    private static boolean isRichTextField(String fieldName) {
        return fieldName != null && RICH_TEXT_FIELDS.contains(fieldName.toLowerCase());
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null || isRichTextField(name)) {
            return value;
        }
        return cleanValue(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null || isRichTextField(name)) {
            return values;
        }
        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleaned[i] = cleanValue(values[i]);
        }
        return cleaned;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> original = super.getParameterMap();
        Map<String, String[]> result = new LinkedHashMap<>(original.size());
        for (Map.Entry<String, String[]> entry : original.entrySet()) {
            String name = entry.getKey();
            if (isRichTextField(name)) {
                result.put(name, entry.getValue());
            } else {
                String[] values = entry.getValue();
                String[] cleaned = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    cleaned[i] = cleanValue(values[i]);
                }
                result.put(name, cleaned);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 仅对 application/json 请求体进行清洗，其它类型（如文件上传）原样透传
        String contentType = getContentType();
        if (contentType == null || !contentType.toLowerCase().contains("application/json")) {
            return super.getInputStream();
        }
        if (cachedBody == null) {
            cachedBody = readAndCleanBody();
        }
        return new CachedServletInputStream(cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 读取原始请求体并清洗 JSON 中的字符串节点。
     *
     * @return 清洗后的字节
     * @throws IOException 读取失败
     */
    private byte[] readAndCleanBody() throws IOException {
        try (InputStream is = super.getInputStream()) {
            byte[] raw = is.readAllBytes();
            if (raw.length == 0) {
                return raw;
            }
            String body = new String(raw, StandardCharsets.UTF_8);
            try {
                JsonNode root = OBJECT_MAPPER.readTree(body);
                JsonNode cleaned = cleanJsonNode(root, null);
                return OBJECT_MAPPER.writeValueAsBytes(cleaned);
            } catch (Exception e) {
                // JSON 解析失败时保留原始请求体，避免阻断非标准 JSON 请求
                return raw;
            }
        }
    }

    /**
     * 递归清洗 JSON 节点中的字符串值。
     *
     * @param node      当前节点
     * @param fieldName 当前字段名（对象属性时为属性名，数组元素时为父级字段名）
     * @return 清洗后的节点
     */
    private JsonNode cleanJsonNode(JsonNode node, String fieldName) {
        if (node == null || node.isNull()) {
            return node;
        }
        if (node.isObject()) {
            ObjectNode result = OBJECT_MAPPER.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String name = entry.getKey();
                if (isRichTextField(name)) {
                    result.set(name, entry.getValue());
                } else {
                    result.set(name, cleanJsonNode(entry.getValue(), name));
                }
            }
            return result;
        } else if (node.isArray()) {
            ArrayNode result = OBJECT_MAPPER.createArrayNode();
            for (JsonNode element : node) {
                result.add(cleanJsonNode(element, fieldName));
            }
            return result;
        } else if (node.isTextual()) {
            return TextNode.valueOf(cleanValue(node.asText()));
        }
        return node;
    }

    /**
     * 基于缓存字节的 ServletInputStream 实现，支持重复读取。
     */
    private static class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        CachedServletInputStream(byte[] body) {
            this.buffer = new ByteArrayInputStream(body);
        }

        @Override
        public int read() throws IOException {
            return buffer.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return buffer.read(b, off, len);
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }
}
