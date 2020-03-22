/**
 * 
 */
package com.dp.plat.core.context;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
}
