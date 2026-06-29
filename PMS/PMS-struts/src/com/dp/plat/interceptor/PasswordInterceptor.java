package com.dp.plat.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.springframework.util.StringUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.User;
import com.dp.plat.service.LoginService;
import com.dp.plat.util.StringEscUtil;


public class PasswordInterceptor extends com.dp.plat.security.interceptor.PasswordInterceptor {

    @Override
    public boolean isNeedRedirect(HttpServletRequest request) {
        UserContext userContext = UserContext.getUserContext();
        boolean authenticated = userContext.isLogin();
        if (!authenticated) {
            return false;
        }
        String servletPath = request.getServletPath();
        HttpSession session = request.getSession();
        Object needChangePwd = session.getAttribute("needChangePwd");
        String redirect = getRedirect();
        if (redirect == null || redirect.contains(servletPath)) {
            return false;
        }
        if (needChangePwd != null) {
            return Boolean.TRUE.equals(needChangePwd);
        }
        User user = userContext.getUser();
        Date currentDate = new Date();
        Date pwdoverdue = user.getPwdoverdue();
        pwdoverdue = pwdoverdue != null ? pwdoverdue : currentDate;
        needChangePwd = !currentDate.before(pwdoverdue);
        boolean isCas = userContext.isCas();
        AuthenticationFilter casFilter = null;
//        try {
//            casFilter = SpringContext.getBean(AuthenticationFilter.class);
//            isCas = casFilter != null;
//        } catch (NoSuchBeanDefinitionException e) {
//            isCas = false;
//        }
        String casStr = StringEscUtil.getText("sys.cas");
        if ("1".equals(casStr)) {
            isCas = true;
        } else {
            isCas = false;
        }
        Boolean isNeed = Boolean.TRUE.equals(needChangePwd) && !isCas;
        session.setAttribute("needChangePwd", isNeed && redirect != null && redirect.length() > 0);
        return isNeed;
    }

    @Override
    public String getRedirect() {
        return super.getRedirect();
    }

    @Override
    public void setRedirect(String redirect) {
        LoginService loginService = (LoginService) SpringContext.getBean("loginService");
        String url = loginService.querySysArg("sys.change.password.redirect");
        if (StringUtils.hasText(url)) {
            url = redirect;
        }
        super.setRedirect(redirect);
    }
    
}
