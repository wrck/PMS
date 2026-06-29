/**
 * 
 */
package com.dp.plat.core.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.realms.Principal;

/**
 * @author w02611
 *
 */
public class UserContext {
	
	public static SessionDAO getSessionDAO() {
		return SpringContext.getBean("sessionDAO", SessionDAO.class);
	}
	
	/**
	 * 获取生效的用户Session
	 * @return
	 */
	public static Collection<Session> getActiveSessions() {
		try {
			SessionDAO sessionDAO = getSessionDAO();
			return sessionDAO.getActiveSessions();
	//		Collection<Session> sessions = sessionDAO.getActiveSessions();
	//		Collection<Session> userSessions = new ArrayList<Session>();
	//		for (Session session : sessions) {
	//			SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
	//					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
	//			if (principalCollection == null) {
	//				continue;
	//			}
	//			userSessions.add(session);
	//		}
	//		return userSessions;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	/**
	 * 获取生效的指定用户Session
	 * @return
	 */
	public static Collection<Session> getActiveSessions(String userName) {
		userName = StringUtils.trimToNull(userName);
		if (userName == null) {
			return Collections.emptyList();
		}
		Collection<Session> sessions = getActiveSessions();
		Collection<Session> userSessions = new ArrayList<Session>();
		for (Session session : sessions) {
			SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if (principalCollection == null) {
				continue;
			}
			Principal principal = (Principal) principalCollection.getPrimaryPrincipal();
			String tempUserNmae = StringUtils.trimToEmpty(principal.getUserName());
			if (tempUserNmae.equals(userName)) {
				userSessions.add(session);
			}
		}
		return userSessions;
	}
	
	/**
	 * 获取当前所有在线登录信息
	 * @return
	 */
	public static Collection<Principal> getActivePrincipals() {
//		SessionDAO sessionDAO = SpringContext.getBean("sessionDAO", SessionDAO.class);
//		Collection<Session> sessions = sessionDAO.getActiveSessions();
		Collection<Session> sessions = getActiveSessions();
		Collection<Principal> principals = new ArrayList<Principal>();
		for (Session session : sessions) {
			SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if (principalCollection == null) {
				continue;
			}
			principals.add((Principal) principalCollection.getPrimaryPrincipal());
		}
		return principals;
	}
	
	/**
	 * 获取当前所有在线用户
	 * @return
	 */
	public static Collection<User> getActiveUsers() {
		Collection<Principal> principals = getActivePrincipals();
		Collection<User> users = new ArrayList<User>(principals.size());
		for (Principal principal : principals) {
			users.add(createUser(principal));
		}
		return users;
	}

	/**
	 * 获取当前用户的用户名
	 */
	public static String getCurrentUsername() {
		return getCurrentUser().getUserName();
	}
	/**
	 * 获取当前用户的登录信息
	 * 
	 * @return
	 */
	public static Principal getCurrentPrincipal() {
		Principal principal = null;
		try {
			principal = (Principal) SecurityUtils.getSubject().getPrincipal();
		} catch (Exception e) {
		}
		if (principal == null) {
			return new Principal(new User());
		}
		return principal;
	}

	/**
	 * 获取当前用户名称
	 * 
	 * @return
	 */
	public static String getUsername() {
		return getCurrentPrincipal().getUserName();
	}

	/**
	 * 获取当前用户所在组织ID
	 * 
	 * @return
	 */
	public static Integer getOrgId() {
		try {
			return getCurrentPrincipal().getCompId();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取当前用户信息
	 * 
	 * @return
	 */
	public static User getCurrentUser() {
		Principal principal = getCurrentPrincipal();
		return createUser(principal);
	}
	
	/**
	 * 判断当前用户是否有某个角色
	 * 
	 * @return
	 */
	public static boolean hasRole(String roleIdentifier) {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		return SecurityUtils.getSecurityManager().hasRole(principalCollection, roleIdentifier);
	}

	/**
	 * 判断当前用户是否有某些角色中的一个
	 *
	 * @return
	 */
	public static boolean hasAnyRoles(List<String> roleIdentifiers) {
		return hasAnyRoles((Collection<String>) roleIdentifiers);
	}
	/**
	 * 判断当前用户是否有某些角色中的一个
	 * 
	 * @return
	 */
	public static boolean hasAnyRoles(Collection<String> roleIdentifiers) {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		if (!(roleIdentifiers instanceof List)) {
			roleIdentifiers = new ArrayList<String>(roleIdentifiers);
		}
		boolean[] bs = SecurityUtils.getSecurityManager().hasRoles(principalCollection, (List<String>) roleIdentifiers);
		boolean hasAnyRoles = false;
		for (boolean b : bs) {
			hasAnyRoles = b || hasAnyRoles;
			if (hasAnyRoles) {
				break;
			}
		}
		return hasAnyRoles;
	}

	/**
	 * 判断当前用户是否有某些角色中的一个
	 * 
	 * @return
	 */
	public static boolean hasAnyRoles(String... roleIdentifiers) {
		return hasAnyRoles(Arrays.asList(roleIdentifiers));
	}

	/**
	 * 判断当前用户是否有所有指定角色
	 *
	 * @return
	 */
	public static boolean hasAllRoles(List<String> roleIdentifiers) {
		return hasAllRoles((Collection<String>) roleIdentifiers);
	}
	/**
	 * 判断当前用户是否有所有指定角色
	 * 
	 * @return
	 */
	public static boolean hasAllRoles(Collection<String> roleIdentifiers) {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		return SecurityUtils.getSecurityManager().hasAllRoles(principalCollection, roleIdentifiers);
	}

	/**
	 * 判断当前用户是否有所有指定权限
	 * 
	 * @return
	 */
	public static boolean checkPermission(String... permissions) {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		return SecurityUtils.getSecurityManager().isPermittedAll(principalCollection, permissions);
	}

	/**
	 * 判断当前用户是否有所有指定权限
	 *
	 * @return
	 */
	public static boolean checkPermission(List<String> permissions) {
		return checkPermission((Collection<String>) permissions);
	}
	/**
	 * 判断当前用户是否有所有指定权限
	 * 
	 * @return
	 */
	public static boolean checkPermission(Collection<String> permissions) {
		if (permissions == null || permissions.isEmpty()) {
			return true;
		}
		return checkPermission(permissions.toArray(new String[] {}));
	}

	/**
	 * 判断当前用户是否有任一指定权限
	 *
	 * @return
	 */
	public static boolean checkAnyPermission(List<String> permissions) {
		return checkAnyPermission((Collection<String>) permissions);
	}
	/**
	 * 判断当前用户是否有任一指定权限
	 * 
	 * @return
	 */
	public static boolean checkAnyPermission(Collection<String> permissions) {
		if (permissions == null || permissions.isEmpty()) {
			return true;
		}
		return checkAnyPermission(permissions.toArray(new String[] {}));
	}

	/**
	 * 判断当前用户是否有任一指定权限
	 * 
	 * @return
	 */
	public static boolean checkAnyPermission(String... permissions) {
//		if (permissions == null || permissions.length == 0) {
//			return true;
//		}
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		boolean[] permitted = SecurityUtils.getSecurityManager().isPermitted(principalCollection, permissions);
		boolean anyPermitted = false;
		for (boolean b : permitted) {
			anyPermitted = b || anyPermitted;
			if (anyPermitted) {
				break;
			}
		}
		return anyPermitted;
	}
	
	/**
	 * 根据Principal创建User
	 * @param principal
	 * @return user
	 */
	private static User createUser(Principal principal) {
		User user = new User();
		if (principal == null) {
			return user;
		}
		try {
			Field[] fields = User.class.getDeclaredFields();
			for (Field field : fields) {
				try {
					String fieldName = field.getName();
					String methodSubName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					Method method = principal.getClass().getDeclaredMethod("get" + methodSubName);
					Object value = method.invoke(principal);
					if (value == null) {
						continue;
					}

					method = User.class.getDeclaredMethod("set" + methodSubName, field.getType());
					method.invoke(user, value);
				} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					continue;
				}
			}
		} catch (SecurityException e) {
			user.setUserId(principal.getUserId());
			user.setUserName(principal.getUserName());
			user.setStatus(principal.getStatus());
			e.printStackTrace();
		}
		return user;
	}
	/**
	 * 绑定用户，绑定当前线程请求上下文
	 * @param user
	 * @param userInfo
	 * @param securityManager
	 * @param servletContext
	 */
	public static void bindThreadContextUser(User user, UserInfo userInfo, SecurityManager securityManager) {
		 try {
            // 获取webApplication上下文
            WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
            ServletContext servletContext = webApplicationContext.getServletContext();
            bindThreadContextUser(user, userInfo, securityManager, servletContext);
        } catch (Throwable e) {
        }
	}

	/**
	 * 绑定用户，绑定当前线程请求上下文
	 * @param user
	 * @param userInfo
	 * @param securityManager
	 * @param servletContext
	 */
	public static void bindThreadContextUser(User user, UserInfo userInfo, SecurityManager securityManager, ServletContext servletContext) {
		// 伪造请求，绑定用户，绑定当前线程请求上下文
		Principal principal = new Principal(user);
		principal.setUserInfo(userInfo);
		HttpServletRequest request = HttpContext.createServletRequest(servletContext, principal);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request), true);
		// 绑定shrio上下文
		try {
		    ThreadContext.bind(securityManager);
		    ThreadContext.bind(new Subject.Builder().principals(new SimplePrincipalCollection(principal, "jdbcRealm")).authenticated(true).buildSubject());
		} catch (Exception e) {
		    ExceptionHandler.insertException(e);
        }
	}

	/**
	 * 解绑用户，解绑当前线程请求上下文
	 */
	public static void unbindThreadContextUser() {
		// 解绑shiro上下文
		try {
			ThreadContext.unbindSubject();
			ThreadContext.unbindSecurityManager();
			// 清空伪造的请求
			RequestContextHolder.resetRequestAttributes();
		} catch (Exception e) {
		    ExceptionHandler.insertException(e);
        }
	}
}
