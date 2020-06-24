package com.dp.plat.security.xss;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;


/**
 * 自定义HttpServletRequestWrapper,让request输入流重复使用多次
 * 
 * @author w02611
 *
 */
public class XssPostHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private static final String UTF_8 = "UTF-8";
	
	private Map<String, String[]> paramsMap;

	private Charset charset;
	private byte[] body; // 报文

	public XssPostHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		if ("POST".equals(request.getMethod().toUpperCase())) {
			// 缓存请求body
			String requestBodyStr = null;
			try {
				requestBodyStr = getRequestBody(getInputStream());
				if (null != requestBodyStr && !"".equals(requestBodyStr)) {
					String temp = escapeHtml(requestBodyStr);
					JSONObject resultJson = JSONObject.parseObject(temp);
					body = resultJson.toString().getBytes(getCharset());
				} else {
					body = new byte[0];
				}
			} catch (Exception e) {
				if (null == body && null != requestBodyStr) {
					body = requestBodyStr.getBytes(getCharset());
				}
			}
		} else {
			body = readBytes(getInputStream());
		}
		
		// 首先从POST中获取数据
		if ("POST".equals(request.getMethod().toUpperCase())) {
			paramsMap = getParamMapFromPost(this);
		}
		Map<String, String[]> fromGet = getParamMapFromGet(this);
		if (null == paramsMap) {
			paramsMap = fromGet;
		} else {
			paramsMap.putAll(fromGet);
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return paramsMap;
	}

	@Override
	public String getParameter(String name) {// 重写getParameter，代表参数从当前类中的map获取
		String[] values = paramsMap.get(name);
		if (values == null || values.length == 0) {
			return null;
		}
		String value = values[0];
		if ("password".equals(name)) {
			return value;
		}
		System.err.println(name + ":" + StringUtils.join(values, ","));
		// return StringEscapeUtils.escapeHtml(value);
		return escapeHtml(value);
//		return values[0];
	}

	@Override
	public String[] getParameterValues(String name) {// 同上
		String[] values = paramsMap.get(name);
		if (values  == null) {
			return null;
		}
		if ("password".equals(name)) {
			return values;
		}
		System.err.println(name + ":" + StringUtils.join(values, ","));
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = escapeHtml(values[i]);
		}
		return encodedValues;
//		return paramsMap.get(name);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(paramsMap.keySet());
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (body == null) {
			return super.getInputStream();
		}
		final ByteArrayInputStream bais = new ByteArrayInputStream(body);
		return new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {

			}
		};
	}

	private Map<String, String[]> getParamMapFromGet(HttpServletRequest request) {
		return parseQueryString(request.getQueryString());
	}

	private Map<String, String[]> getParamMapFromPost(HttpServletRequest request) {

		String body = "";
		try {
			body = getRequestBody(getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String[]> result = new HashMap<String, String[]>();

		if (null == body || 0 == body.length()) {
			return result;
		}

		return parseQueryString(body);
	}

	private String getRequestBody(InputStream stream) throws IOException {
		if (stream == null) {
			return "";
		}

		StringBuilder out = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(stream, getCharset());
		char[] buffer = new char[4096];
		int bytesRead = -1;
		while ((bytesRead = reader.read(buffer)) != -1) {
			out.append(buffer, 0, bytesRead);
		}
		return out.toString();
	}

	public Map<String, String[]> parseQueryString(String s) {
		String valArray[] = null;
		Map<String, String[]> ht = new HashMap<String, String[]>();
		if (s == null) {
			return ht;
		}
		StringTokenizer st = new StringTokenizer(s, "&");
		while (st.hasMoreTokens()) {
			String pair = (String) st.nextToken();
			int pos = pair.indexOf('=');
			if (pos == -1) {
				continue;
			}
			String key = pair.substring(0, pos);
			String val = pair.substring(pos + 1, pair.length());
			if (key.contains("%") || key.contains("+")) {
				key = decodeValue(key);
			}
			if (val.contains("%") || val.contains("+")) {
				val = decodeValue(val);
			}
			if (ht.containsKey(key)) {
				String oldVals[] = (String[]) ht.get(key);
				valArray = new String[oldVals.length + 1];
				for (int i = 0; i < oldVals.length; i++) {
					valArray[i] = oldVals[i];
				}
				valArray[oldVals.length] = val;
			} else {
				valArray = new String[1];
				valArray[0] = val;
			}
			ht.put(key, valArray);
		}
		return ht;
	}

	private static byte[] readBytes(InputStream in) throws IOException {
		if (in == null) {
			return new byte[0];
		}
		BufferedInputStream bufin = new BufferedInputStream(in);
		final int buffSize = 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);

		byte[] temp = new byte[buffSize];
		int size = 0;
		while ((size = bufin.read(temp)) != -1) {
			out.write(temp, 0, size);
		}
		out.flush();
		byte[] content = out.toByteArray();
		bufin.close();
		out.close();
		return content;
	}

	/**
	 * 自定义解码函数
	 * 
	 * @param value
	 * @return
	 */
	private String decodeValue(String value) {
//		if (value.contains("%u")) {
//			return value;
//		} else {
			try {
				return URLDecoder.decode(value, getCharset().name());
			} catch (UnsupportedEncodingException e) {
				return "";// 非UTF-8编码
			}
//		}
	}

	/**
	 * 重写StringEscapeUtils.escapeHtml()方法，避免过滤中文
	 * 
	 * @param s
	 * @return
	 */
	private static String escapeHtml(String s) {
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
	
	private Charset getCharset() {
		if (null == charset) {
			String charSetStr = getCharacterEncoding();
			if (charSetStr == null) {
				charSetStr = UTF_8;
			}
			charset = Charset.forName(charSetStr);
		}
		return charset;
	}
}