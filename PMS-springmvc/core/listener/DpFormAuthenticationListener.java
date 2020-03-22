package com.dp.plat.core.listener;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.pojo.UserLoginRecord;
import com.dp.plat.core.pojo.UsernamePasswordCaptchaToken;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IShiroService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserLoginRecordService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.MenuUtil;

/**
 * @author w02611
 *
 */
public class DpFormAuthenticationListener implements AuthenticationListener {

	@Resource
	private IShiroService shiroService;

	@Resource
	private IUserService userService;

	@Resource
	private IUserInfoService userInfoService;

	@Resource
	private IUserLoginRecordService userLoginRecordService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.shiro.authc.AuthenticationListener#onSuccess(org.apache.shiro.
	 * authc.AuthenticationToken, org.apache.shiro.authc.AuthenticationInfo)
	 */
	@Override
	public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
		// TODO Auto-generated method stub
		ServletRequestAttributes requestContainer = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = null;
		Principal principal = null;
		Object object = info.getPrincipals().getPrimaryPrincipal();
		if (object instanceof Principal) {
			principal = (Principal) object;
		} else {
			String userName = (String) object;
			User user = userService.selectByUserName(userName);
			principal = new Principal(user);
		}
//		HttpSession session = request.getSession();
		Session session = SecurityUtils.getSubject().getSession();
		if (requestContainer != null) {
			request = requestContainer.getRequest();
			Integer loginRecordId = saveLoginInfo(request, principal);
			principal.setLoginRecordId(loginRecordId);
		} else {
			Integer loginRecordId = saveLoginInfo(session, principal);
			principal.setLoginRecordId(loginRecordId);
		}

		if(session.getAttribute("contextPath") != null){
			List<Menu> nodes = shiroService.queryUserMenuByUsername(principal.getUserName());
			principal.setMenus(MenuUtil.drow(nodes, (String) session.getAttribute("contextPath")));
		}

		UserInfo userInfo = userInfoService.selectByUserId(principal.getUserId());
		principal.setUserInfo(userInfo);

		String homePage = userService.queryMaxRoleHomePageByUserId(principal.getUserId());
		principal.setHomePage(homePage);
		
		// 登录成功更新密码输入错误次数
		if (principal.getLoginErrorCount() > 0) {
			User user = new User(principal.getUserId());
			user.setLoginErrorCount(0);
			userService.updateByPrimaryKeySelective(user);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.shiro.authc.AuthenticationListener#onFailure(org.apache.shiro.
	 * authc.AuthenticationToken,
	 * org.apache.shiro.authc.AuthenticationException)
	 */
	@Override
	public void onFailure(AuthenticationToken token, AuthenticationException ae) {
		// TODO Auto-generated method stub
		if (token instanceof UsernamePasswordCaptchaToken) {
			String loginName = ((UsernamePasswordCaptchaToken) token).getUsername();
			saveLoginInfo(loginName);
		}
		throw ae;
		// System.out.println("onFailure");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.shiro.authc.AuthenticationListener#onLogout(org.apache.shiro.
	 * subject.PrincipalCollection)
	 */
	@Override
	public void onLogout(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		Principal principal = (Principal) principals.getPrimaryPrincipal();
		if (principal != null) {
			ServletRequestAttributes requestContainer = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			String ip = null;
			if (requestContainer != null) {
				HttpServletRequest request = requestContainer.getRequest();
				ip = getIpAddress(request);
			} else {
				ip = SecurityUtils.getSubject().getSession().getHost();
			}
		
			UserLoginRecord userLoginRecord = new UserLoginRecord();
			userLoginRecord.setId(principal.getLoginRecordId());
			userLoginRecord.setLogoutIP(ip);
			userLoginRecord.setLogoutTime(new Date());
			userLoginRecord.setLogoutSuccess(true);
			userLoginRecordService.updateByPrimaryKeySelective(userLoginRecord);
		}
	}

	/**
	 * 登录成功保存用户登录信息
	 * @param session
	 * @param principal
	 * @return loginId
	 */
	private Integer saveLoginInfo(Session session, Principal principal) {
		if (principal != null) {
			String ip = session.getHost();
			return saveLoginInfo(ip, principal);
		} else {
			return null;
		}
	}

	/**
	 * 登录成功保存用户登录信息
	 * @param request
	 * @param principal
	 * @return loginId
	 */
	private Integer saveLoginInfo(HttpServletRequest request, Principal principal) {
		if (principal != null) {
			String ip = this.getIpAddress(request);
			return saveLoginInfo(ip, principal);
		} else {
			return null;
		}
	}
	/**
	 * 登录成功保存用户登录信息
	 * @param ip
	 * @param principal
	 * @return loginId
	 */
	private Integer saveLoginInfo(String ip, Principal principal) {
		if (principal != null) {
			UserLoginRecord userLoginRecord = new UserLoginRecord();
			userLoginRecord.setUserId(principal.getUserId());
			userLoginRecord.setLoginName(principal.getUserName());
			userLoginRecord.setLoginTime(new Date());
			userLoginRecord.setLoginIP(ip);
			userLoginRecord.setLoginSuccess(true);
			this.userLoginRecordService.insertSelective(userLoginRecord);
			return userLoginRecord.getId();
		} else {
			return null;
		}
	}
	
	/**
	 * 登录失败时保存用户名登录信息
	 * @param request
	 * @param principal
	 * @return loginId
	 */
	private Integer saveLoginInfo(String loginName) {
		if (StringUtils.isNotBlank(loginName)) {
			ServletRequestAttributes requestContainer = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			HttpServletRequest request = requestContainer.getRequest();
			String ip = this.getIpAddress(request);
			UserLoginRecord userLoginRecord = new UserLoginRecord();
			userLoginRecord.setLoginName(loginName);
			userLoginRecord.setLoginTime(new Date());
			userLoginRecord.setLoginIP(ip);
			this.userLoginRecordService.insertSelective(userLoginRecord);
			return userLoginRecord.getId();
		} else {
			return null;
		}
	}

	public String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
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
}
