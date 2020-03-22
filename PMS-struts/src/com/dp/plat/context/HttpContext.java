package com.dp.plat.context;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.jsp.TagUtils;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;

public class HttpContext
{
	/**
	 * 获取远端IP地址
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getRemoteAddr() throws Exception
	{
		HttpServletRequest request = ServletActionContext.getRequest();

		return request.getRemoteAddr();
	}

	/**
	 * 使当前会话无效
	 * 
	 * @throws Exception
	 */
	public static void invalidateSession() throws Exception
	{
		ServletActionContext.getRequest().getSession().invalidate();
	}

	/**
	 * 返回当前的url，/开头，不包括contextpath
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getUrl() throws Exception
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		return request.getRequestURI().substring(
				request.getContextPath().length());
	}

	public static HttpServletRequest getRequest()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		return request;
	}

	public static HttpServletResponse getResponse()
	{
		HttpServletResponse response = ServletActionContext.getResponse();
		return response;
	}

	public static HttpSession getSession()
	{
		HttpSession session = null;
		try
		{
			if (getRequest() == null)
			{
				return null;
			}
			session = getRequest().getSession();
		}
		catch (Exception e)
		{
			;
		}
		return session;
	}

	public static String getText(PageContext pageContext, String key,
			String... params)
	{
		ValueStack vs = TagUtils.getStack(pageContext);

		for (Iterator<?> iterator = vs.getRoot().iterator(); iterator.hasNext();)
		{
			Object o = iterator.next();

			if (o instanceof TextProvider)
			{
				TextProvider tp = (TextProvider) o;
				return tp.getText(key, key, params, vs);
			}
		}

		return key;
	}

	public static String getText(String key, String... params)
	{
		PageContext pageContext = ServletActionContext.getPageContext();
		ValueStack vs = TagUtils.getStack(pageContext);

		for (Iterator<?> iterator = vs.getRoot().iterator(); iterator.hasNext();)
		{
			Object o = iterator.next();

			if (o instanceof TextProvider)
			{
				TextProvider tp = (TextProvider) o;
				return tp.getText(key, key, params, vs);
			}
		}

		return key;
	}

	public static List<InetAddress> getAllServerIps()
	{
		ArrayList<InetAddress> ipList = new ArrayList<InetAddress>();
		try
		{
			Enumeration<NetworkInterface> ifEnum = NetworkInterface
					.getNetworkInterfaces();

			if (ifEnum != null)
			{
				while (ifEnum.hasMoreElements())
				{
					NetworkInterface localIf = ifEnum.nextElement();
					Enumeration<InetAddress> ipEnum = localIf
							.getInetAddresses();
					if (ipEnum != null)
					{
						while (ipEnum.hasMoreElements())
						{
							InetAddress ipAddr = ipEnum.nextElement();
							ipList.add(ipAddr);

						}
					}
				}
			}
		}
		catch (Exception e)
		{
			;
		}

		return ipList;
	}

	public static String getTempDir()
	{
		return System.getProperty("java.io.tmpdir");
	}

	public static String encode(String str)
	{
		try
		{
			return URLEncoder.encode(str, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return str;

	}

	private static Hashtable<String, ResourceBundle> res = new Hashtable<String, ResourceBundle>();
	private static String[] baseNames;
	
	static {
		ResourceBundle rb = PropertyResourceBundle.getBundle("system", Locale.CHINA);
		String baseNameStr = rb.getString("plat.config.resourses");
		if(StringUtils.isNotBlank(baseNameStr))
			baseNames = baseNameStr.split(";");
	}
	
	public static String getMessage(String key, Object... args)
	{
		String mod = key.substring(0, key.indexOf("."));
		try{
			ResourceBundle rb = res.get(mod);
			if (null == rb) {
				for (String baseName : baseNames) {
					rb = PropertyResourceBundle.getBundle(baseName, Locale.CHINA);
					if (rb.containsKey(key)) {
						res.put(mod, rb);
						break;
					}
				}
			}
			if(!rb.containsKey(key)){
				return key;
			}
			return MessageFormat.format(rb.getString(key), args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return key;
		}
	}

	public static String decode(String param)
	{
		try
		{
			return URLDecoder.decode(param, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return param;
	}
}
