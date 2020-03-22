package com.dp.plat.core.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dp.plat.core.pojo.Company;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserLoginRecord;
import com.dp.plat.core.pojo.UserRole;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.ICompanyService;
import com.dp.plat.core.service.IShiroService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserLoginRecordService;
import com.dp.plat.core.service.IUserRoleService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.MenuUtil;
import com.dp.plat.core.vo.UserInfoVO;

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
	private IUserRoleService userRoleService;

	@Resource
	private IUserInfoService userInfoService;

	@Resource
	private IUserLoginRecordService userLoginRecordService;

	@Resource
	private ICompanyService companyService;

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
		// HttpSession session = request.getSession();
		if (requestContainer != null) {
			request = requestContainer.getRequest();
			Integer loginRecordId = saveLoginInfo(request, principal);
			principal.setLoginRecordId(loginRecordId);
		} else {
			Session session = SecurityUtils.getSubject().getSession();
			Integer loginRecordId = saveLoginInfo(session, principal);
			principal.setLoginRecordId(loginRecordId);
		}

		// UserInfo userInfo = userInfoService.selectByUserId(principal.getUserId());
		// UserInfoVO userInfo = userInfoService.selectOneByUserId(principal.getUserId());

		List<UserInfoVO> userInfoVOList = userInfoService.selectVOsByUserId(principal.getUserId());
		UserInfoVO userInfo = userInfoVOList.isEmpty() ? null : userInfoVOList.get(0);
		principal.setUserInfo(userInfo);
		principal.setUserInfoList(userInfoVOList);
		if (principal.getIsSysUser() != 0) {
			if (userInfo == null) {
				UserRole userRole = new UserRole();
				userRole.setUserId(principal.getUserId());
				userRole.setCompId(-1);
				long count = userRoleService.countBySelective(userRole);
				if (count > 0) {
					userInfo = new UserInfoVO();
					userInfo.setCompID(-1);
					userInfo.setUserId(principal.getUserId());
				}
			}
			Company temp = new Company();
			temp.setState(true);
			List<Company> companies = companyService.selectBySelective(temp);
			userInfoVOList = new ArrayList<>();
			for (Company company : companies) {
				UserInfoVO userInfoVO = new UserInfoVO();
				BeanUtils.copyProperties(userInfo, userInfoVO);
				userInfoVO.setCompID(company.getId());
				// userInfoVO.setUserId(principal.getUserId());
				userInfoVO.setCompName(company.getCompName());
				userInfoVOList.add(userInfoVO);
			}
			principal.setUserInfo(userInfoVOList.isEmpty() ? null : userInfoVOList.get(0));
			principal.setUserInfoList(userInfoVOList);
		}
		// } else {
		// Company temp = new Company();
		// temp.setState(true);
		// List<Company> companies = companyService.selectBySelective(temp);
		// List<UserInfoVO> userInfoVOList = new ArrayList<>();
		// for (Company company : companies) {
		// UserInfoVO userInfoVO = new UserInfoVO();
		// userInfoVO.setCompID(company.getId());
		// userInfoVO.setUserId(principal.getUserId());
		// userInfoVO.setCompName(company.getCompName());
		// userInfoVOList.add(userInfoVO);
		// }
		// principal.setUserInfo(userInfoVOList.isEmpty() ? null :
		// userInfoVOList.get(0));
		// principal.setUserInfoList(userInfoVOList);
		// }

		// List<Menu> nodes =
		// shiroService.queryUserMenuByUsername(principal.getUserName());
		if (request != null) {
			List<Menu> nodes = shiroService.queryUserMenuByUserIdAndCompId(userInfo);
			HttpSession session = request.getSession();
			// principal.setMenus(MenuUtil.drow(nodes, (String)
			// session.getAttribute("contextPath")));
			principal.setMenus(MenuUtil.drow(nodes, (String) session.getServletContext().getContextPath()));
		} else {
			Session session = SecurityUtils.getSubject().getSession();
			if (session != null) {
				List<Menu> nodes = shiroService.queryUserMenuByUserIdAndCompId(userInfo);
				principal.setMenus(MenuUtil.drow(nodes, (String) session.getAttribute("contextPath")));
			}
		}

		// String homePage =
		// userService.queryMaxRoleHomePageByUserId(principal.getUserId());
		String homePage = userService.queryMaxRoleHomePageByUserIdAndCompId(userInfo);
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
		String loginName = (String) token.getPrincipal();
		// String loginName = ((UsernamePasswordCaptchaToken)
		// token).getUsername();
		if (StringUtils.isNotBlank(loginName)) {
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

	public void saveLoginInfo(HttpServletRequest request, AuthenticationToken token) {
//		String userName = (String) token.getPrincipal();
//		try {
//			String ip = this.getIpAddress(request);
//			User user = new User();
//			user.setUserName(userName);
//			user.setLastLoginTime(new Date());
//			user.setLastLoginIp(ip);
//			this.userService.updateLoginInfoByUserName(user);
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			log.debug("用户【" + token.getPrincipal() + "】于【" + simpleDateFormat.format(user.getLastLoginTime())
//					+ "】成功登陆，客户端Ip地址【" + ip + "】");
//		} catch (DataAccessException e) {
//			if (log.isWarnEnabled()) {
//				log.info("无法更新用户登录信息至数据库");
//			}
//		}
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
			String ip = null;
			ip = this.getIpAddress(request);
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
			String ip = null;
			if (requestContainer == null) {
				HttpServletRequest request = requestContainer.getRequest();
				ip = this.getIpAddress(request);
			} else {
				ip = SecurityUtils.getSubject().getSession().getHost();
			}

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
