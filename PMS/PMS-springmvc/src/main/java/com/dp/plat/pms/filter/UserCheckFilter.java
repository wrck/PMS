package com.dp.plat.pms.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.service.LoginService;

public class UserCheckFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		UserContext userContext = (UserContext) SpringContext.getBean("userContext");
		LoginService loginService = (LoginService) SpringContext.getBean("loginService");
		Subject currentUser = SecurityUtils.getSubject();
		Boolean isLogin = userContext.isLogin() || currentUser.isAuthenticated();
		String url = req.getRequestURI().substring(req.getContextPath().length());
		if (url.contains("module/DownloadFile.action")) {
			chain.doFilter(request, response);
			return;
		}
		userContext.setUrl(req.getRequestURL().toString());
		int pos = url.indexOf("/", 1);
		if (!userContext.isLogin()) {
			String casStr = loginService.querySysArg("sys.cas");
			if (casStr.equals("0")) {
				userContext.setCas(false);
			} else if (casStr.equals("1")) {
				userContext.setCas(true);
			}
		}

		if (pos >= 0) {
			if (!userContext.isLogin()) // 未登录
			{
				if (userContext.isCas()) {
					resp.sendRedirect(req.getContextPath() + "/Login.action");
					return;
				} else {
					if (url.indexOf("Login") == -1) {
						resp.sendRedirect(req.getContextPath() + "/index.jsp?errmsg=Login Timeout!");
						return;
					}
				}
			}
		} else {
			if (userContext.isCas()
					&& (url.contains("index.jsp") || url.contains("login.jsp") || url.contains("Login!start.action"))) {
				resp.sendRedirect(req.getContextPath() + "/Login.action");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
