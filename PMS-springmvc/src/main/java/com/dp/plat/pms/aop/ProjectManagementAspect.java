package com.dp.plat.pms.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.IdentityService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.factory.FilterChainDefinitionMapBuilder;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IShiroService;
import com.dp.plat.core.service.ISystemVariableService;
import com.dp.plat.core.util.MenuUtil;
import com.dp.plat.pms.springmvc.service.IProjectManageUserService;

/**
 * 系统核心功能切面<br>
 * 系统URL资源更新时，同时更新Shrio权限拦截器<br>
 * 系统参数更新，菜单更新，角色更新，角色菜单更新时更新已登录用户的菜单权限
 * @author w02611
 *
 */
@Aspect
@Component
public class ProjectManagementAspect {
	private static Logger logger = LoggerFactory.getLogger(ProjectManagementAspect.class);

	@Autowired
	private ShiroFilterFactoryBean shiroFilterFactoryBean;

	@Autowired
	private IShiroService shiroService;

	@Autowired
	private IProjectManageUserService projectManageUserService;

	@Autowired
	private SessionDAO sessionDAO;
	
	/**
	 * 当用户角色，菜单以及角色菜单权限变更时，更新用户菜单
	 * 
	 * @param point
	 */
	@After("execution(* com.dp.plat.pms.springmvc.controller.ProjectManageUserController.create(..) )"
			+ " || execution(* com.dp.plat.pms.springmvc.controller.ProjectManageUserController.update(..) )"
			+ " || execution(* com.dp.plat.pms.springmvc.controller.ProjectManageUserController.delete(..) )")
	public void updateActiveUserPower(JoinPoint point) {
		Object[] args = point.getArgs();
		if (args != null && args.length > 0) {
			System.out.println(args);
		}
//		// 更新菜单
//		Collection<Session> sessions = sessionDAO.getActiveSessions();
//		for (Session session : sessions) {
//			SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
//					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
//			if (principalCollection == null) {
//				continue;
//			}
//			HttpServletRequest request = HttpContext.getCurrentRequest();
//			if (request != null) {
//				Principal principal = (Principal) principalCollection.getPrimaryPrincipal();
//				//List<Menu> nodes = shiroService.queryUserMenuByUsername(principal.getUserName());
//				List<Menu> nodes = shiroService.queryUserMenuByUserIdAndCompId(principal.getUserInfo());
//				principal.setMenus(MenuUtil.drow(nodes, request.getContextPath()));
//			}
//		}
	}

}
