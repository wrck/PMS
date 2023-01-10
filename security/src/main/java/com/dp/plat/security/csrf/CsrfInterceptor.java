package com.dp.plat.security.csrf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * csrf拦截器
 * 
 * @author j01441
 */
public class CsrfInterceptor implements AsyncHandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) 
            throws Exception {
        if (modelAndView != null) {
            Map<String, Object> model = modelAndView.getModel();
            String token = CSRFTokenManager.getTokenForSession(request.getSession());
            model.put(CSRFTokenManager.getTokenName(), token);
            response.addHeader(CSRFTokenManager.getTokenName(), token);
        }
//        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String method = request.getMethod();

        HttpSession session = request.getSession();

        String serverCsrfToken = (String) session.getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);

        if (StringUtils.isEmpty(serverCsrfToken)) {
            CSRFTokenManager.getTokenForSession(session);
        } else {
            if (isNeedValidatorCsrfToken(method)) {
                String clientCsrfToken = CSRFTokenManager.getTokenFromRequest(request);
                if (StringUtils.isEmpty(clientCsrfToken) || !clientCsrfToken.equals(serverCsrfToken)) {
                    throw new CsrfValidateFailedException("csrf token validate failed");
                }
            }
        }
        return true;
//        return super.preHandle(request, response, handler);
    }

    private boolean isNeedValidatorCsrfToken(String method) {
        return "POST".equals(method) || "DELETE".equals(method) || "PUT".equals(method);
    }

}
