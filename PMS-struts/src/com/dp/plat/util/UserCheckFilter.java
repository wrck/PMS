package com.dp.plat.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.alibaba.fastjson.JSON;
import com.dp.plat.context.SpringContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.UserMenu;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.LoginService;
import com.dp.plat.service.UserManageService;

public class UserCheckFilter implements Filter{

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		UserContext userContext = (UserContext) SpringContext.getBean("userContext");
		LoginService loginService=(LoginService)SpringContext.getBean("loginService");
		String url = req.getRequestURI().substring(req.getContextPath().length());
		// 检查是否需要强制修改密码
		String changePasswordRedirect = getChangePasswordRedirect(req);
		if (StringUtils.isNotBlank(changePasswordRedirect)) {
		    resp.sendRedirect(req.getContextPath()+ changePasswordRedirect);
		    return;
		}
		if(url.contains("module/DownloadFile.action")){
			chain.doFilter(request, response);
			return;
		}
//		String method = req.getMethod();
//		if ("GET".equalsIgnoreCase(method)) {
			String queryString = req.getQueryString();
			StringBuffer requestURL = req.getRequestURL();
			if (StringUtils.isNotBlank(queryString)) {
				requestURL.append("?").append(queryString);
			}
			userContext.setUrl(requestURL.toString());
//		}
		int pos = url.indexOf("/", 0);
		if(!userContext.isLogin()){
//		    String serverName = StringEscUtil.getText("sys.server.name");
//            String casStr = loginService.querySysArg("sys." + serverName + ".cas");
//            if (StringUtils.isBlank(casStr)) {
//                casStr = loginService.querySysArg("sys.cas");
//            }
		    String casStr = StringEscUtil.getText("sys.cas");
			if(casStr.equals("0")){
				userContext.setCas(false);			
			}else if(casStr.equals("1")){
				userContext.setCas(true);
			}
		}
        // 未登录
        if (!userContext.isLogin() && !(url.contains("index.jsp")||url.contains("login.jsp")||url.contains("Login.action") ||url.contains("Login!start.action"))) {
            userContext.setDefaultPage(userContext.getUrl());
            resp.sendRedirect(req.getContextPath() + "/Login.action");
            return;
        } else if (userContext.isCas() && (url.contains("index.jsp") || url.contains("login.jsp") || url.contains("Login!start.action"))) {
            // CAS登录
            resp.sendRedirect(req.getContextPath() + "/Login.action");
            return;
        }
//		if(pos>=0){
//			if (!userContext.isLogin())	//未登录
//			{
//				userContext.setDefaultPage(userContext.getUrl());
//				if(userContext.isCas()){
//					resp.sendRedirect(req.getContextPath()+ "/Login.action");
//					return;
//				}else{
//					if(url.indexOf("Login") == -1){
//						resp.sendRedirect(req.getContextPath()+ "/index.jsp?errmsg=Login Timeout!");
//						return;
//					}
//				}
//			} 
//		}else{
//			if(userContext.isCas()&&(url.contains("index.jsp")||url.contains("login.jsp")||url.contains("Login!start.action"))){
//				resp.sendRedirect(req.getContextPath()+ "/Login.action");
//				return;
//			}
//		}
		
        // 权限判断
        if (userContext.isLogin()) {
            if(!checkHandlerMapping(url)) {
                resp.sendRedirect(req.getContextPath()+ "/404.action");
                return;
            }
        }
        
        // handlerMapping不匹配的url直接放行
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	/**
	 * 获取修改密码的重定向地址
	 * @param request
	 * @return 如果需要修改密码返回，否则不返回
	 */
	public String getChangePasswordRedirect(HttpServletRequest request) {
        UserContext userContext = UserContext.getUserContext();
        boolean authenticated = userContext.isLogin();
        if (!authenticated) {
            return null;
        }
        String servletPath = request.getServletPath();
        HttpSession session = request.getSession();
        Object needChangePwd = session.getAttribute("needChangePwd");
        if (Boolean.FALSE.equals(needChangePwd)) {
            return null;
        }
        User user = userContext.getUser();
        Date currentDate = new Date();
        Date pwdoverdue = user.getPwdoverdue();
        pwdoverdue = pwdoverdue != null ? pwdoverdue : currentDate;
        needChangePwd = !currentDate.before(pwdoverdue);
        boolean isCas = userContext.isCas();
//        AuthenticationFilter casFilter = null;
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
        if (!isNeed) {
            return null;
        }
        
        LoginService loginService = (LoginService) SpringContext.getBean("loginService");
        String redirect = loginService.querySysArg("sys.change.password.redirect");
        String excludeUrls = loginService.querySysArg("sys.change.password.redirect.excludeUrls");
        if (redirect == null || redirect.contains(servletPath) || 
                (excludeUrls != null && excludeUrls.contains(servletPath))) {
            return null;
        }
        isNeed = isNeed && redirect != null && redirect.length() > 0;
        session.setAttribute("needChangePwd", isNeed);
        return isNeed ? redirect : null;
    }
	
	public boolean checkHandlerMapping(String url) {
	    UserContext userContext = UserContext.getUserContext();
	    if (!userContext.isLogin()) {
	        return true;
	    }
	    // 获取缓存的用户handlerMaping
        Map<String, Object> extData = userContext.getExtData();
        Map<String, Object> cachedUserHandlerMapping = (Map<String, Object>) extData.get("cachedUserHandlerMapping");
        if (cachedUserHandlerMapping == null) {
            cachedUserHandlerMapping = new ConcurrentHashMap<String, Object>();
            extData.put("cachedUserHandlerMapping", cachedUserHandlerMapping);
        }
        if (cachedUserHandlerMapping.containsKey(url)) {
            return Boolean.valueOf(String.valueOf(cachedUserHandlerMapping.get(url)));
        }
	    
	    // 是否是排除的校验链接
	    BasicDataService basicDataService = SpringContext.getBean("basicDataService", BasicDataService.class);
	    Map<String, Object> excludeHandlerMapping = JSON.parseObject(StringUtils.defaultIfBlank(basicDataService.querySysArg("sys.handler.mapping.check.exclude.mapping"), "{}"), HashMap.class);
	    boolean isValid = checkHandlerMapping(url, false, null, excludeHandlerMapping);
	    if (isValid) {
            cachedUserHandlerMapping.put(url, isValid);
	        return isValid;
	    }
	    
        // 当前用户拥有的菜单ID
	    UserManageService userManageService = SpringContext.getBean("userManageService", UserManageService.class);
        String menuIds = userManageService.queryUserMenuidsByUserid(userContext.getUser().getId());
        List<String> menuIdList = Arrays.asList(menuIds.split(","));
        // 将系统菜单进行解析
        Map<String, Object> menuHandlerMapping = handlerMapping();
        
        try {
            isValid = checkHandlerMapping(url, true, menuIdList, menuHandlerMapping);
            return isValid;
        } finally {
            cachedUserHandlerMapping.put(url, isValid);
        }
	}

    /**
     * 判断是否满足指定HandlerMapping、菜单的条件
     * @param url 待匹配的url
     * @param isValid 默认未匹配到时的结果
     * @param menuIdList 菜单权限判断的
     * @param handlerMapping 
     * @return
     */
    public boolean checkHandlerMapping(String url, boolean isValid, List<String> menuIdList, Map<String, Object> handlerMapping) {
        menuIdList = menuIdList != null ? menuIdList : Collections.emptyList();
        // handlerMapping匹配url
        for (Entry<String, Object> handler : handlerMapping.entrySet()) {
            String path = handler.getKey();
            Object value = handler.getValue();
            // 匹配到的url，判断用户是否有权限
            if (FilenameUtils.wildcardMatch(url, path)) {
                isValid = true;
                // 如果是菜单则判断是否有该菜单的权限
                if (value instanceof UserMenu) {
                    UserMenu menu = (UserMenu) handler.getValue();
                    // 如果有权限则跳转，如果没权限转到404
                    if (menuIdList.contains(String.valueOf(menu.getId()))) {
                        isValid = true;
                    } else {
                        isValid = false;
                    }
                }
                return isValid;
            }
        }
        return isValid;
    }
	
	
	
	/**
     * 判断当前环境变量，如果不存在handlerMapping则解析用户菜单，转换为url通配地址
     * @param menuList
     * @return {
     *     url:menu
     * }
     */
    public Map<String, Object> handlerMapping() {
        UserManageService userManageService = SpringContext.getBean("userManageService", UserManageService.class);
        // 所有系统菜单
        List<UserMenu> menuList = userManageService.queryAllMenuList();
        return handlerMapping(menuList);
    }
	
	/**
	 * 判断当前环境变量，如果不存在handlerMapping则解析用户菜单，转换为url通配地址
	 * @param menuList
	 * @return {
	 *     url:menu
	 * }
	 */
	public Map<String, Object> handlerMapping(List<UserMenu> menuList) {
	    UserContext userContext = UserContext.getUserContext();
	    Map<String, Object> extData = userContext.getExtData();
	    if (extData == null) {
	        extData = new ConcurrentHashMap<String, Object>();
	    }
	    userContext.setExtData(extData);
	    Map<String, Object> handlerMapping = (Map<String, Object>) extData.get("handlerMapping");
	    if (handlerMapping != null && !handlerMapping.isEmpty()) {
	        return handlerMapping;
	    }
	    Map<String, Object> urlMap = parseUrlHandler(menuList);
	    handlerMapping = new ConcurrentHashMap<String, Object>(urlMap);
//	    handlerMapping.putAll(urlMap);
	    extData.put("handlerMapping", handlerMapping);
	    return handlerMapping;
	}
	
	/**
     * 解析用户菜单，转换为url通配地址
     * @param menuList
     * @return {
     *     url:menu
     * }
     */
	private Map<String, Object> parseUrlHandler(List<UserMenu> menuList) {
	    if (menuList == null || menuList.isEmpty()) {
	        return Collections.emptyMap();
	    }
	    Map<String, Object> urlMap = new HashMap<String, Object>(menuList.size() * 3 * 3 / 4);
        for (UserMenu userMenu : menuList) {
            Map<String, Object> subUrlMap = parseUrlHandler(userMenu.getUserMenuList());
            urlMap.putAll(subUrlMap);
            
            String url = userMenu.getPath();
            if (StringUtils.isBlank(url)) {
                continue;
            }
            URI uri;
            String path = url;
            try {
                uri = new URI(url);
                path = uri.getPath();
            } catch (URISyntaxException e) {
                path = url;
            }
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            boolean isAction = path.endsWith(".action");
            Set<String> pathList = new HashSet<String>();
            pathList.add(path);
            // 将这类的地址/module/Module_method.action /module/Module!method.action
            //    转换为/module/Module_*.action
            //        /module/Module!*.action 
            //        /module/Module/*.action
            pathList.add(path.replaceAll("([_])[^\\.|\\/|!]*", "$1*"));
            pathList.add(path.replaceAll("([!])[^\\.|\\/|!]*", "$1*"));
            pathList.add(path.replaceAll("([_|!])[^\\.|\\/|!]*", "/*"));
            // 将这类的地址/module/ModuleManage.action /module/ModuleManager.action /module/ModuleAction.action
            //    转换为/module/Module*.action
            //        /module/Module/*.action
            pathList.add(path.replaceAll("(Manager?)|(Action)[^\\.|\\/|!]*", "*"));
            pathList.add(path.replaceAll("(Manager?)|(Action)[^\\.|\\/|!]*", "/*"));
            // 将这类的地址/module/Module.action
            //    转换为/module/Module_*.action
            //        /module/Module!*.action 
            //        /module/Module/*.action
            if (isAction) {
                pathList.add(path.replaceAll(".action", "_*.action"));
                pathList.add(path.replaceAll(".action", "!*.action"));
                pathList.add(path.replaceAll(".action", "/*.action"));
            }
            
            for (String handler : pathList) {
                urlMap.put(handler, userMenu);
            }
        }
        return urlMap;
	}
}
