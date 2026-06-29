package com.dp.plat.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cas.CasFilter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.realms.Principal;

/**
 * 强制修改密码拦截器
 * 
 * @author w02611
 *
 */
public class PasswordInterceptor extends HandlerInterceptorAdapter {

	private String redirect;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (isNeedRedirect(request)) {
			response.sendRedirect(request.getContextPath() + redirect);
			return false;
		}
		return super.preHandle(request, response, handler);
	}

	private boolean isNeedRedirect(HttpServletRequest request) {
		boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
		if (!authenticated) {
			return false;
		}
		String servletPath = request.getServletPath();
		HttpSession session = request.getSession();
		Object needChangePwd = session.getAttribute("needChangePwd");
		if (redirect == null || redirect.contains(servletPath)) {
			return false;
		}
		if (needChangePwd != null) {
			return Boolean.TRUE.equals(needChangePwd);
		}
		Principal principal = UserContext.getCurrentPrincipal();
		needChangePwd = principal.getNeedChangePwd();
		String isCas = SystemConfig.systemVariables.getOrDefault("sys.cas", "0");
		CasFilter casFilter = null;
		try {
			casFilter = SpringContext.getBean(CasFilter.class);
		} catch (NoSuchBeanDefinitionException e) {
			isCas = "0";
		}
		Boolean isNeed = Boolean.TRUE.equals(needChangePwd) && "0".equals(isCas);
		session.setAttribute("needChangePwd", isNeed && redirect != null && redirect.length() > 0);
		return isNeed;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

}
