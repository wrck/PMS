package com.dp.plat.security.csrf;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

/**
 * csrf拦截器
 * 
 * @author w02611
 */
public class CsrfFilter implements Filter {
    
    FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        String servletPath = httpRequest.getServletPath();
        if (filterConfig != null) {
            String excludePattern = filterConfig.getInitParameter("excludePattern");
            if (StringUtils.isNotBlank(excludePattern) && servletPath.matches(excludePattern)) {
                chain.doFilter(request, response);
                return;
            }
        }
        String contextPath = httpRequest.getContextPath();
        
        if (isValid(httpRequest, httpResponse)) {
            String token = CSRFTokenManager.getTokenForSession(httpRequest.getSession());
            String tokenName = CSRFTokenManager.getTokenName();
            httpResponse.addHeader("CSRF_TOKEN", tokenName);
            httpResponse.addHeader(tokenName, token);
            Cookie cookie = new Cookie("CSRF_TOKEN", tokenName);
            cookie.setPath(contextPath);
            cookie.setHttpOnly(true);
            httpResponse.addCookie(cookie);
            cookie = new Cookie(tokenName, token);
            cookie.setPath(contextPath);
            cookie.setHttpOnly(true);
            httpResponse.addCookie(cookie);
            chain.doFilter(request, response);
            return;
        }
        
        request.getRequestDispatcher("/404.jsp").forward(request, response);
    }

    public boolean isValid(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();

        HttpSession session = request.getSession();

        String serverCsrfToken = (String) session.getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);

        if (StringUtils.isEmpty(serverCsrfToken)) {
            CSRFTokenManager.getTokenForSession(session);
        } else {
            if (isNeedValidatorCsrfToken(method)) {
                String clientCsrfToken = CSRFTokenManager.getTokenFromRequest(request);
                if (StringUtils.isEmpty(clientCsrfToken) || !clientCsrfToken.equals(serverCsrfToken)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNeedValidatorCsrfToken(String method) {
        return true;
//        return "POST".equals(method) || "DELETE".equals(method) || "PUT".equals(method);
    }

}
