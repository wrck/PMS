package com.dp.plat.core.filter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.util.StringUtils;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.util.IpUtil;

public class HostFilter extends org.apache.shiro.web.filter.authz.HostFilter {
	
	private String cachedAuthoriezdHosts = "";
	private String cachedDeniedHosts = "";
	
	Map<String, String> authorizedIps = new ConcurrentHashMap<String, String>(); //user-configured IP (which can be wildcarded) to constructed regex mapping
    Map<String, String> deniedIps = new ConcurrentHashMap<String, String>();
    Map<String, String> authorizedHostnames = new ConcurrentHashMap<String, String>();
    Map<String, String> deniedHostnames = new ConcurrentHashMap<String, String>();
    
	@Override
	public void setAuthorizedHosts(String authorizedHosts) {
		// 如果传入的值与前一次缓存的值相同，则直接返回
		if (cachedAuthoriezdHosts.equals(authorizedHosts)) {
			return;
		}
		authorizedIps.clear();
		if (!StringUtils.hasText(authorizedHosts)) {
			return;
        }
		cachedAuthoriezdHosts = authorizedHosts;
        String[] hosts = StringUtils.tokenizeToStringArray(authorizedHosts, ", \t");

        for (String host : hosts) {
            //replace any periods with \\. to ensure the regex works:
            String periodsReplaced = host.replace(".", "\\.");
            //check for IPv4:
            String wildcardsReplaced = periodsReplaced.replace("*", IPV4_QUAD_REGEX);

            if (IPV4_PATTERN.matcher(host).matches()) {
                authorizedIps.put(host, wildcardsReplaced);
            } else if (isIpv4Candidate(host)) {
            	authorizedIps.put(host, wildcardsReplaced);
            } else if (isIpv4Range(host)) {
            	authorizedIps.put(host, host);
            } else if (isIpv4MarkRange(host)) {
            	authorizedIps.put(host, host);
            }
        }
    }

	@Override
    public void setDeniedHosts(String deniedHosts) {
		// 如果传入的值与前一次缓存的值相同，则直接返回
		if (cachedDeniedHosts.equals(deniedHosts)) {
			return;
		}
		deniedIps.clear();
        if (!StringUtils.hasText(deniedHosts)) {
            return;
        }
 		cachedDeniedHosts = deniedHosts;
        String[] hosts = StringUtils.tokenizeToStringArray(deniedHosts, ", \t");
        for (String host : hosts) {
            //replace any periods with \\. to ensure the regex works:
            String periodsReplaced = host.replace(".", "\\.");
            //check for IPv4:
            String wildcardsReplaced = periodsReplaced.replace("*", IPV4_QUAD_REGEX);

            if (IPV4_PATTERN.matcher(host).matches()) {
            	deniedIps.put(host, wildcardsReplaced);
            } else if (isIpv4Candidate(host)) {
            	deniedIps.put(host, wildcardsReplaced);
            } else if (isIpv4Range(host)) {
            	deniedIps.put(host, host);
            } else if (isIpv4MarkRange(host)) {
            	deniedIps.put(host, host);
            }
        }
    }

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		String authorizedHosts = SystemConfig.systemVariables.getOrDefault("sys.admin.allow.hosts", "");
		String deniedHosts = SystemConfig.systemVariables.getOrDefault("sys.admin.deny.hosts", "");
		this.setAuthorizedHosts(authorizedHosts);
		this.setDeniedHosts(deniedHosts);
		
		String ip = request.getRemoteAddr();
		try {
			ip = HttpContext.getCurrentIp((HttpServletRequest) request);
		} catch (Exception e) {
			ip = request.getRemoteAddr();
		}
		String host = request.getRemoteHost();
		boolean allowed = (authorizedIps.isEmpty() && authorizedHostnames.isEmpty());
		for (Entry<String, String> matchRule : authorizedIps.entrySet()) {
			String rule = matchRule.getValue();
			if (Pattern.matches(rule, ip)) {
				return true;
			} else if (isIpv4Range(rule)) {
				if (IpUtil.isInRange(ip, rule)) {
					return true;
				}
            } else if (isIpv4MarkRange(rule)) {
            	if (IpUtil.isInMarkRange(ip, rule)) {
					return true;
				}
            }
		}
		for (Entry<String, String> matchRule : authorizedHostnames.entrySet()) {
			String rule = matchRule.getValue();
			if (Pattern.matches(rule, host)) {
				return true;
			}
		}
		for (Entry<String, String> matchRule : deniedIps.entrySet()) {
			String rule = matchRule.getValue();
			if (Pattern.matches(rule, ip)) {
				return false;
			} else if (isIpv4Range(rule)) {
				if (IpUtil.isInRange(ip, rule)) {
					return false;
				}
            } else if (isIpv4MarkRange(rule)) {
            	if (IpUtil.isInMarkRange(ip, rule)) {
					return false;
				}
            }
		}
		for (Entry<String, String> matchRule : deniedHostnames.entrySet()) {
			String rule = matchRule.getValue();
			if (Pattern.matches(rule, host)) {
				return false;
			}
		}
		
		return allowed;
	}
	
	protected boolean isIpv4Range(String host) {
		String[] ips = StringUtils.tokenizeToStringArray(host, "-");
        if (ips == null || ips.length != 2) {
            return false;
        }
        return isIpv4Candidate(ips[0]) && isIpv4Candidate(ips[1]);
    }
	
	protected boolean isIpv4MarkRange(String host) {
		String[] ipMark = StringUtils.tokenizeToStringArray(host, "/");
        if (ipMark == null || ipMark.length != 2) {
            return false;
        }
        String ip = ipMark[0];
        Integer mark = 0;
        try {
        	mark = Integer.parseInt(ipMark[1]);
        } catch(NumberFormatException e) {
        	return false;
        }
        return isIpv4Candidate(ip) && (0 < mark && mark <= 32);
    }

//	public Map<String, String> getAuthorizedIps() {
//		return authorizedIps;
//	}
//
//	public Map<String, String> getDeniedIps() {
//		return deniedIps;
//	}
//
//	public Map<String, String> getAuthorizedHostnames() {
//		return authorizedHostnames;
//	}
//
//	public Map<String, String> getDeniedHostnames() {
//		return deniedHostnames;
//	}
//
//	public static void main(String[] args) {
//		HostFilter hostFilter = new HostFilter();
//		String authorizedHosts = "10.101.0.*";
//		hostFilter.setAuthorizedHosts(authorizedHosts);
//		System.out.println(hostFilter.getAuthorizedIps());
//		authorizedHosts = "10.101.0.*";
//		hostFilter.setAuthorizedHosts(authorizedHosts);
//		System.out.println(hostFilter.getAuthorizedIps());
//		authorizedHosts = "10.102.0.106";
//		hostFilter.setAuthorizedHosts(authorizedHosts);
//		System.out.println(hostFilter.getAuthorizedIps());
//		authorizedHosts = "10.102.0.106-10.102.0.109";
//		hostFilter.setAuthorizedHosts(authorizedHosts);
//		System.out.println(hostFilter.getAuthorizedIps());
//		authorizedHosts = "10.102.0.106/24";
//		hostFilter.setAuthorizedHosts(authorizedHosts);
//		System.out.println(hostFilter.getAuthorizedIps());
//		authorizedHosts = "10.101.0.*,10.102.0.106,10.102.0.106-10.102.0.109,10.102.0.106/24";
//		hostFilter.setAuthorizedHosts(authorizedHosts);
//		System.out.println(hostFilter.getAuthorizedIps());
//		System.out.println(IpUtil.isInRange("192.168.1.127", "192.168.1.64/26"));  
//        System.out.println(IpUtil.isInRange("192.168.1.2", "192.168.0.0/23"));  
//        System.out.println(IpUtil.isInRange("192.168.0.1", "192.168.0.0/24"));  
//        System.out.println(IpUtil.isInRange("192.168.0.0", "192.168.0.0/32"));  
//        System.out.println(IpUtil.isInRange("10.102.0.106", "10.102.0.0/28"));  
//        
//        String ip = "10.102.0.106";
//        for (Entry<String, String> authorizedIp : hostFilter.getAuthorizedIps().entrySet()) {
//			String wildcardsReplaced = authorizedIp.getValue();
//			if (Pattern.matches(wildcardsReplaced, ip)) {
//				System.out.println(wildcardsReplaced + ":" + true);
//			} else if (hostFilter.isIpv4Range(wildcardsReplaced)) {
//				System.out.println(wildcardsReplaced + ":" + IpUtil.isInRange(ip, wildcardsReplaced));
//            } else if (hostFilter.isIpv4MarkRange(wildcardsReplaced)) {
//            	System.out.println(wildcardsReplaced + ":" + IpUtil.isInMarkRange(ip, wildcardsReplaced));
//            }
//		}
//	}
}	
