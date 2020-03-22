package com.dp.plat.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import com.dp.plat.context.HttpContext;

public class StringEscUtil {
	public static String htmlEscape(String str) {
		return HtmlUtils.htmlEscape(str);
	}

	public static String sqlEscape(String str) {
		return StringEscapeUtils.escapeSql(str);
	}

	public static String urlEscape(String input) {
		try {
			return java.net.URLEncoder.encode(input, "utf-8");
		} catch (Exception e) {
			return "";
		}
	}

	public static String jsEscape(String str) {
		return JavaScriptUtils.javaScriptEscape(str);
	}

	public static String toUtf8(String str) {
		return toUtf8(null, str);
	}

	public static String toUtf8(String encoding, String str) {
		String result = "";
		if (null != str) {
			try {
				if (null == encoding) {
					result = new String(new String(str.getBytes("iso-8859-1"),
							"utf-8"));
				} else if (encoding.equalsIgnoreCase("utf-8")) {
					result = str;
				} else if (encoding.equalsIgnoreCase("gbk")) {
					result = new String(new String(str.getBytes("iso-8859-1"),
							"gbk"));
				} else {
					result = new String(new String(str.getBytes("iso-8859-1"),
							"utf-8"));
				}
			} catch (Exception e) {
			}
		}
		return result;
	}

	public static String getText(String key) {
		return HttpContext.getMessage(key);
	}

	public static String getText(String key, Object... params) {
		return HttpContext.getMessage(key, params);
	}
}
