package com.dp.plat.core.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author w02611
 *
 */
public class MessageUtils {

	public static String getLocaleMessage(HttpServletRequest request, Object[] args, String code,
			String defaultMessage) {
		if (request == null) {
			request = getRequest();
		}
		WebApplicationContext ac = RequestContextUtils.findWebApplicationContext(request);
		return ac.getMessage(code, args, defaultMessage, RequestContextUtils.getLocale(request));
	}

	public static String getLocaleMessage(String code) {
		return getLocaleMessage(null, null, code, null);
	}

	public static String getLocaleMessage(String code, String defaultMessage) {
		return getLocaleMessage(null, null, code, defaultMessage);
	}

	public static String getLocaleMessage(String code, Object[] args) {
		return getLocaleMessage(null, args, code, null);
	}

	public static String getLocaleMessage(String code, Object[] args, String defaultMessage) {
		return getLocaleMessage(null, args, code, defaultMessage);
	}

	private static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

}
