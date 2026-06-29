package com.dp.plat.security.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 对每个post请求的参数过滤一些关键字，替换成安全的，例如：< > ' " \ / # &
 * 防止SQL注入和XSS攻击
 * @author j01441
 *
 * @deprecated @RequestBody application/json请求时无法处理，只能处理表单提交和get请求
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		if("password".equals(parameter)) {
			return values;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = this.escapeHtml(values[i]);
		}
		return encodedValues;
	}

	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		if("password".equals(parameter)) {
			return value;
		}
		// return StringEscapeUtils.escapeHtml(value);
		return this.escapeHtml(value);
	}

	/**
	 * 重写StringEscapeUtils.escapeHtml()方法，避免过滤中文
	 * 
	 * @param s
	 * @return
	 */
	private String escapeHtml(String s) {
		if (s == null || s.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '>':
				sb.append("&gt;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '&':
				sb.append('＆');
				break;
//			case ';':
//				sb.append('；');
//				break;	
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
}
