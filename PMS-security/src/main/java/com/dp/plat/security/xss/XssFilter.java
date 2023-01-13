package com.dp.plat.security.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

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
		
		HttpServletRequest httpRequest = ((HttpServletRequest) request);
//		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
//		        httpRequest.getSession().getServletContext());
//        if (commonsMultipartResolver.isMultipart(httpRequest)) {
//            commonsMultipartResolver.setDefaultEncoding("UTF-8");
//            MultipartHttpServletRequest multipartRequest = commonsMultipartResolver.resolveMultipart(httpRequest);
//            request = new XssRequestBodyHttpServletRequestWrapper(multipartRequest);
//        } else {
            request = new XssRequestBodyHttpServletRequestWrapper(httpRequest);
//        }
		chain.doFilter(request, response);
	}

}
