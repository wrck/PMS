package com.dp.plat.core.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

/**
 * 系统核心功能切面<br>
 * 系统URL资源更新时，同时更新Shrio权限拦截器<br>
 * 系统参数更新，菜单更新，角色更新，角色菜单更新时更新已登录用户的菜单权限
 * @author w02611
 *
 */
@Aspect
@Component
public class SystemCoreFunctionAspect {
	private static Logger logger = LoggerFactory.getLogger(SystemCoreFunctionAspect.class);

	@Autowired
	private ShiroFilterFactoryBean shiroFilterFactoryBean;

	@Autowired
	private IShiroService shiroService;

	@Autowired
	private ISystemVariableService systemVariableService;

	@Autowired
	private SessionDAO sessionDAO;

	/**
	 * 权限更新后，自动更新shrio的权限验证拦截Map，
	 */
	@After("(target(com.dp.plat.core.service.IResourceService) && (execution(* com.dp.plat.core.service.IAbstractBaseService.insert*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IAbstractBaseService.update*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IAbstractBaseService.delete*(..)))) ||"
			+ "execution(* com.dp.plat.core.service.IResourceService.insert*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IResourceService.update*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IResourceService.delete*(..))")
	public void updateFilterChainDefinitionMap() {
		synchronized (shiroFilterFactoryBean) {

			AbstractShiroFilter shiroFilter = null;

			try {
				shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			// 获取过滤管理器
			PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
					.getFilterChainResolver();
			DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();

			// 清空初始权限配置
			manager.getFilterChains().clear();
			shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();

			// 重新构建生成
			//	LinkedHashMap<String, String> newMap = new LinkedHashMap<>();
			//	Resource record = new Resource();
			//	record.setStatus(1);
			//	List<com.dp.plat.core.pojo.Resource> resources = resourceService.selectBySelective(record);
			//	HashMap<String, String> systemVariables = SystemConfig.systemVariables;
			//	if (systemVariables == null) {
			//		systemVariables = systemVariableService.querySystemVariables();
			//		SystemConfig.systemVariables = systemVariables;
			//	}
			//	String isCas = systemVariables.getOrDefault("sys.cas", "0");
			//	for (Resource tempResource : resources) {
			//		String url = tempResource.getUrl();
			//		if (StringUtils.isEmpty(url)) {
			//			continue;
			//		}
			//		String authc = StringUtils.isEmpty(tempResource.getAuthc()) ? "anon" : tempResource.getAuthc();
			//		newMap.put(url, authc + ("1".equals(isCas) ? ",casFilter" : ""));
			//	}
			
			FilterChainDefinitionMapBuilder filterChainDefinitionMapBuilder = SpringContext.getBean("filterChainDefinitionMapBuilder", FilterChainDefinitionMapBuilder.class);
			LinkedHashMap<String, String> newMap = filterChainDefinitionMapBuilder.buildFilterChainDefinitionMap();

			shiroFilterFactoryBean.setFilterChainDefinitionMap(newMap);
			Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();

			for (Map.Entry<String, String> entry : chains.entrySet()) {
				String url = entry.getKey();
				String chainDefinition = entry.getValue().trim().replace(" ", "");
				manager.createChain(url, chainDefinition);
			}
			
			// 清空缓存的权限信息
			clearActiveUserAuthorizationInfoCache();
			
			logger.debug("update shiro permission success...");
		}
	}

	/**
	 * 当用户角色，菜单以及角色菜单权限变更时，更新用户菜单
	 * 
	 * @param point
	 */
	@After("execution(* com.dp.plat.core.service.IMenuService.delete*(..) )"
			+ " || execution(* com.dp.plat.core.service.IMenuService.update*(..) )"
			+ " || execution(* com.dp.plat.core.service.IUserRoleService.batch*UserRole(..) )"
			+ " || (target(com.dp.plat.core.service.IUserRoleService) && execution(* com.dp.plat.core.service.IAbstractBaseService.deleteByPrimaryKey(..)) )"
			+ " || execution(* com.dp.plat.core.service.IRoleMenuService.batchInsertRoleMenu(..) )")
	public void updateActiveUserMenu(JoinPoint point) {
		// 清空缓存的权限信息
		clearActiveUserAuthorizationInfoCache();
		
		// 更新菜单
		Collection<Session> sessions = sessionDAO.getActiveSessions();
		for (Session session : sessions) {
			SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if (principalCollection == null) {
				continue;
			}
			HttpServletRequest request = HttpContext.getCurrentRequest();
			if (request != null) {
				Principal principal = (Principal) principalCollection.getPrimaryPrincipal();
				//List<Menu> nodes = shiroService.queryUserMenuByUsername(principal.getUserName());
				List<Menu> nodes = shiroService.queryUserMenuByUserIdAndCompId(principal.getUserInfo());
				principal.setMenus(MenuUtil.drow(nodes, request.getContextPath()));
			}
		}
	}

	/**
	 * 单系统参数更新时，同时更新缓存的系统参数
	 * 
	 * @param point
	 */
	@After("(target(com.dp.plat.core.service.ISystemVariableService) && (execution(* com.dp.plat.core.service.IAbstractBaseService.insert*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IAbstractBaseService.update*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IAbstractBaseService.delete*(..)))) ||"
			+ "execution(* com.dp.plat.core.service.ISystemVariableService.insert*(..)) ||"
			+ "execution(* com.dp.plat.core.service.ISystemVariableService.update*(..)) ||"
			+ "execution(* com.dp.plat.core.service.ISystemVariableService.delete*(..))")
	public void updateSystemVariables(JoinPoint point) {
		SystemConfig.systemVariables = systemVariableService.querySystemVariables();
		//System.out.println(SystemConfig.systemVariables);
	}
	
	/**
	 * 清空在线用户的权限缓存
	 */
	private void clearActiveUserAuthorizationInfoCache() {
		RealmSecurityManager realmSecurityManager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
		Realm shiroRealm = realmSecurityManager.getRealms().iterator().next();
		try {
			Method method = shiroRealm.getClass().getDeclaredMethod("doClearCache", PrincipalCollection.class);
			if (method != null) {
				Collection<Session> sessions = sessionDAO.getActiveSessions();
				for (Session session : sessions) {
					SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
							.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
					if (principalCollection == null) {
						continue;
					}
					method.invoke(shiroRealm, principalCollection);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			ExceptionHandler.insertException(e);
		}
	}

}
