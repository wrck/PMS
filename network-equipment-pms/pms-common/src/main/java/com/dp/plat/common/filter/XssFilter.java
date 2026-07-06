package com.dp.plat.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * XSS 过滤器。
 *
 * <p>对进入应用的 HTTP 请求进行 XSS 清洗包装：将原始 {@link HttpServletRequest}
 * 包装为 {@link XssHttpServletRequestWrapper}，从而在后续读取参数或请求体时
 * 自动剥离 HTML 标签。</p>
 *
 * <p>排除路径：文件上传端点 {@code /api/file/upload} 不进行包装，避免破坏
 * 二进制文件内容。</p>
 */
@WebFilter(filterName = "xssFilter", urlPatterns = "/*")
@Component
public class XssFilter implements Filter {

    /** 排除清洗的路径前缀（如文件上传，包装会破坏 multipart 二进制内容）。 */
    private static final String[] EXCLUDE_PATH_PREFIXES = {
            "/api/file/upload"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            if (isExcluded(httpRequest.getRequestURI())) {
                chain.doFilter(request, response);
                return;
            }
            chain.doFilter(new XssHttpServletRequestWrapper(httpRequest), response);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * 判断请求路径是否在排除列表中。
     *
     * @param uri 请求 URI
     * @return true 表示跳过 XSS 清洗
     */
    private boolean isExcluded(String uri) {
        if (uri == null) {
            return false;
        }
        for (String prefix : EXCLUDE_PATH_PREFIXES) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
