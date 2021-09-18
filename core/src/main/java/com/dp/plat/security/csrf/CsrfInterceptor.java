package com.dp.plat.security.csrf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * csrf拦截器
 * 
 * @author j01441
 *
 */
public class CsrfInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(modelAndView != null) {
			Map<String, Object> model = modelAndView.getModel();
			String token = CSRFTokenManager.getTokenForSession(SecurityUtils.getSubject().getSession());
			model.put(CSRFTokenManager.CSRF_PARAM_NAME,token );
			response.addHeader(CSRFTokenManager.CSRF_PARAM_NAME, token);
		}
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String method = request.getMethod();
		
		Session session = SecurityUtils.getSubject().getSession();
		
		String serverCsrfToken = (String) session.getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
		
		if(StringUtils.isEmpty(serverCsrfToken)) {
			CSRFTokenManager.getTokenForSession(SecurityUtils.getSubject().getSession());
		}else {
			if(isNeedValidatorCsrfToken(method)) {
				String clientCsrfToken = CSRFTokenManager.getTokenFromRequest(request);
				if(StringUtils.isEmpty(clientCsrfToken) || !clientCsrfToken.equals(serverCsrfToken)) {
					throw new CsrfValidateFailedException("csrf token validate failed");
				}
			}
		}
		return super.preHandle(request, response, handler);
	}
	
	private boolean isNeedValidatorCsrfToken(String method) {
		return "POST".equals(method)||"DELETE".equals(method) || "PUT".equals(method);
	}
	
}
