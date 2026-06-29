package com.dp.plat.security.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class XssFilter implements Filter {

	FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String servletPath = ((HttpServletRequest) request).getServletPath();
		if (filterConfig != null) {
			String excludePattern = filterConfig.getInitParameter("excludePattern");
			if (StringUtils.isNotBlank(excludePattern) && servletPath.matches(excludePattern)) {
				chain.doFilter(request, response);
				return;
			}
		}
//       chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
//       request = new XssHttpServletRequestWrapper((HttpServletRequest) request);
//    	request = new XssPostHttpServletRequestWrapper((HttpServletRequest) request);
		request = new XssRequestBodyHttpServletRequestWrapper((HttpServletRequest) request);
		chain.doFilter(request, response);
	}

}
