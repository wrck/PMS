/**
 * 
 */
package com.dp.plat.core.context;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dp.plat.core.util.PropertyUtil;

/**
 * 
 * @author w02611
 *
 */
public class HttpContext {

	public static HttpServletRequest getCurrentRequest() {
		try {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			if (servletRequestAttributes != null) {
				return servletRequestAttributes.getRequest();
			}
		} catch (Throwable e) {
		}
		return null;
	}
	
	public static HttpSession getCurrentSession() {
		HttpServletRequest currentRequest = getCurrentRequest();
		HttpSession session = null;
		if (currentRequest != null) {
			session = currentRequest.getSession();
		}
		return session;
	}

	/**
	 * 判断请求时候为ajax请求
	 * @return
	 */
	public static boolean isAjax() {
		HttpServletRequest request = getCurrentRequest();
		if (request != null) {
			if ((request.getHeader("accept") != null && request.getHeader("accept").indexOf("application/json") > -1)
					|| (request.getHeader("X-Requested-With") != null
							&& request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1)) {
				return true;
			}
		}
		return false;
	}
	
	/**
     * 判断请求时候为JSON请求
     * @return
     */
    public static boolean isJSON() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            if ((request.getHeader("accept") != null && request.getHeader("accept").indexOf("application/json") > -1)
                    || (request.getServletPath() != null
                    && request.getServletPath().endsWith(".json"))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断请求时候为HTML请求
     * @return
     */
    public static boolean isHTML() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
        	String servletPath = request.getServletPath();
			if ((request.getHeader("accept") != null && request.getHeader("accept").indexOf("text/plain") > -1)
					|| (servletPath != null
							&& (servletPath.endsWith(".html") || servletPath.endsWith(".htm")
									|| servletPath.indexOf(".") == -1))) {
				return true;
			}
        }
        return false;
    }
    
    public static String baseUri() {
    	HttpServletRequest request = getCurrentRequest();
        if (request != null) {
        	return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }
        return "";
    }
    
    public static boolean isExcel() {
		HttpServletRequest request = getCurrentRequest();
		if (request != null) {
			String servletPath = request.getServletPath();
			if ((servletPath != null && (servletPath.endsWith(".xlsx") || servletPath.endsWith(".xls")))) {
				return true;
			}
		}
		return false;
    }
    
    public static String getCurrentIp(HttpServletRequest request) {
    	if (request == null) {
    		request = getCurrentRequest();
    	}
    	if (request == null) {
    		return "";
    	}
    	Boolean hasProxy = false;
		try {
			String hasProxyProp = PropertyUtil.getProperty("sys.has.proxy");
			if (StringUtils.isBlank(hasProxyProp)) {
				Properties loadAllProperties = PropertiesLoaderUtils.loadAllProperties("system.properties");
				hasProxyProp = loadAllProperties.getProperty("sys.has.proxy");
			}
			hasProxy = Boolean.parseBoolean(hasProxyProp);
		} catch (Exception e) {
		}
		String ip = request.getRemoteAddr();
		// 如果存在代理，则从x-forwarded-for中取
		if (Boolean.TRUE.equals(hasProxy)) {
			ip = request.getHeader("x-forwarded-for");
		}
		// 从请求头中获取容易被伪造
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
    }
    
    public static String getCurrentIp() {
    	return getCurrentIp(null);
    }
}
