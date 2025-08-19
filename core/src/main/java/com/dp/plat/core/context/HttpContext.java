/**
 * 
 */
package com.dp.plat.core.context;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.util.PropertyUtil;

/**
 * 
 * @author w02611
 *
 */
public class HttpContext {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpContext.class);

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
		String ip = "";
		try {
			ip = request.getRemoteAddr();
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
		} catch (Exception e) {
		}
		return ip;
    }
    
    public static String getCurrentIp() {
    	return getCurrentIp(null);
    }
    
    public static HttpServletRequest createServletRequest() {
        return createServletRequest((User) null);
    }
    
    public static HttpServletRequest createServletRequest(User user) {
        Principal principal = createServletRequestPrincipal(user);
        return createServletRequest(principal);
    }

    public static HttpServletRequest createServletRequest(Principal principal) {
        try {
            // 获取webApplication上下文
            WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
            ServletContext servletContext = webApplicationContext.getServletContext();
    
            // 伪造请求，绑定用户，绑定当前线程请求上下文
            principal = createServletRequestPrincipal(principal);
            return HttpContext.createServletRequest(servletContext, principal);
        } catch (Throwable e) {
        }
        return null;
    }
    
    /**
     * 创建请求头
     * 
     * @param servletContext
     * @param principal
     * @return
     */
    public static HttpServletRequest createServletRequest(ServletContext servletContext, Principal principal) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        URL hostAndPort = getHostAndPort();
        request.addHeader("Host", hostAndPort.getAuthority());
        request.setScheme(hostAndPort.getProtocol());
        request.setServerPort(hostAndPort.getPort());
        request.setContextPath(servletContext.getContextPath());
        request.setUserPrincipal(principal);
        return request;
    }
    
    /**
     * 创建请求的绑定用户
     * @param user
     * @return
     */
    public static Principal createServletRequestPrincipal(String userName) {
        User user = new User();
        user.setUserName(userName);
		return createServletRequestPrincipal(user);
	}

    /**
     * 创建请求的绑定用户
     * @param user
     * @return
     */
    public static Principal createServletRequestPrincipal(User user) {
        if (user == null) {
            user = new User();
            user.setUserName("mock");
            user.setUserCustom3("mock");
        } else {
            user.setUserCustom3(StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(user.getUserCustom3(), user.getUserName()), "mock"));
        }
        Principal principal = new Principal(user);
        principal.setUserInfoId(user.getUserId());
        return principal;
    }

    /**
     * 创建请求的绑定用户
     * @param principal
     * @return
     */
    public static Principal createServletRequestPrincipal(Principal principal) {
    	// 创建请求的绑定用户
        if (principal == null) {
        	return createServletRequestPrincipal((User) null);
        }
        return principal;
    }
    public static URL getHostAndPort() {
        URL url = null;
        String host = null;
        String schemeName = "http";
        String port = "80";
        try {
            try {
                host = getHostAddress();
                url = new URL(host);
            } catch (MalformedURLException e) {
                Set<ObjectName> objectNames = queryMBeanServer(new ObjectName("*:type=Connector,*"),
                        Query.or(Query.match(Query.attr("protocol"), Query.value("HTTP/*")),
                                Query.match(Query.attr("scheme"), Query.value("https"))));
                for (ObjectName objectName : objectNames) {
                    port = objectName.getKeyProperty("port");
                    // 如果是https则直接返回
                    String scheme = objectName.getKeyProperty("scheme");
                    if (StringUtils.isNotBlank(scheme)) {
                        schemeName = scheme;
                        break;
                    }
                }
                url = new URL(schemeName + "://" + host + ":" + port);
            }
        } catch (Exception ex) {
            ExceptionHandler.insertException(ex);
        }
        LOGGER.debug("IpAddress:" + url);
        return url;
    }
    
    /**
     * 获取HostAddress
     * 
     * @return
     */
    public static String getHostAddress() {
        return getHostAddress(baseUri());
    }
    
    /**
     * 获取服务器的HostAddress
     * 
     * @return
     */
    public static String getSerivceHostAddress() {
        String defaultAddress = SystemConfig.systemVariables.get("sys.service.host");
        return getHostAddress(defaultAddress);
    }
    
    /**
     * 获取HostAddress
     * 
     * @return
     */
    public static String getHostAddress(String defaultAddress) {
        String hostAddress = null;
        boolean isValid = false;
        try {
            // 先找域名
            String hostName = null;
            try {
                // 查服务器配置
                hostName = queryMBeanServerByProperty(new ObjectName("*:type=Host,*"), null, "host");
                if (!(isValid = isValidAddress(hostName))) {
                    hostName = StringUtils.defaultIfBlank(
                            queryMBeanServerByProperty(new ObjectName("*:type=Engine,*"), null, "host"), hostName);
                }
            } catch (Exception e) {
                ExceptionHandler.insertException(e);
            }
            // 在找ip
            String ipHost = null;
            if (!(isValid = isValidAddress(hostName))) {
                InetAddress localHost = InetAddress.getLocalHost();
//              hostName = localHost.getCanonicalHostName();
                ipHost = localHost.getHostAddress();
                // 如果ipHost为空，则遍历网卡查找
                if (StringUtils.isBlank(ipHost)) {
                    ipHost = StringUtils.defaultIfBlank(getHostAddressByNetInterfaces(), ipHost);
                }
            }
            if ((isValid = isValidAddress(hostName))) {
                hostAddress = hostName;
            } else if ((isValid = isValidAddress(ipHost))) {
                hostAddress = ipHost;
            } else {
                hostAddress = ipHost;
            }
        } catch (Exception e) {
            ExceptionHandler.insertException(e);
        }
        if (StringUtils.isNotBlank(defaultAddress) && !isValid) {
            hostAddress = defaultAddress;
        }
        return hostAddress;
    }
    
    /**
     * 通过遍历网卡获取ip地址
     * 
     * @return
     */
    private static String getHostAddressByNetInterfaces() {
        Enumeration<NetworkInterface> netInterfaces = null;
        String hostAddress = "";
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                LOGGER.debug("DisplayName:" + ni.getDisplayName());
                LOGGER.debug("Name:" + ni.getName());
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    String address = ips.nextElement().getHostAddress();
                    if (!isValidAddress(address, true)) {
                        continue;
                    }
                    hostAddress = address;
                }
            }
        } catch (Exception e) {
            ExceptionHandler.insertException(e);
        }
        return hostAddress;
    }

    /**
     * 是否为有效地址, 同时过滤纯ip
     * 
     * @param address
     * @return
     */
    public static boolean isValidAddress(String address) {
        return isValidAddress(address, false);
    }

    /**
     * 是否为有效地址
     * 
     * @param address
     * @param ignoreIp 是否忽略纯ip校验
     * @return
     */
    public static boolean isValidAddress(String address, boolean ignoreIp) {
        if (StringUtils.isBlank(address) || "localhost".equals(address) || "127.0.0.1".equals(address)
                || "0:0:0:0:0:0:0:1".equals(address)) {
            return false;
        }
        if (!ignoreIp) {
            // 纯ip地址，或者为空时
            String pattern = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(address);
            return !m.find();
        }
        return true;
    }

    private static Set<ObjectName> queryMBeanServer(ObjectName name, QueryExp query) {
        try {
            MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
            return beanServer.queryNames(name, query);
        } catch (Exception e) {
            ExceptionHandler.insertException(e);
        }
        return Collections.emptySet();
    }

    private static String queryMBeanServerByProperty(ObjectName name, QueryExp query, String property) {
        Set<ObjectName> objectNames = queryMBeanServer(name, query);
        String result = null;
        for (ObjectName objectName : objectNames) {
            String value = objectName.getKeyProperty(property);
            if (StringUtils.isNotBlank(value)) {
                result = value;
            }
        }
        return result;
    }
}
