package com.dp.plat.core.aop;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dp.plat.core.config.DataSourceHolder;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.pojo.Menu;
import com.dp.plat.core.pojo.Resource;
import com.dp.plat.core.pojo.SyncState;
import com.dp.plat.core.pojo.SysLog;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.IResourceService;
import com.dp.plat.core.service.IShiroService;
import com.dp.plat.core.service.ISynchronizeService;
import com.dp.plat.core.service.ISysLogService;
import com.dp.plat.core.service.ISystemVariableService;
import com.dp.plat.core.util.DateUtil;
import com.dp.plat.core.util.MenuUtil;

/**
 * 已拆分为ExceptionAspect、SyncCheckAspect、SystemCoreFunctionAspect
 * 
 * @author w02611
 *
 * @see ExceptionAspect
 * @see SyncCheckAspect
 * @see SystemCoreFunctionAspect
 */
// @Aspect
// @Component
@Deprecated
public class AspectUtils {
	private static Logger logger = LoggerFactory.getLogger(AspectUtils.class);

	@Inject
	private ShiroFilterFactoryBean shiroFilterFactoryBean;

	@Inject
	private IShiroService shiroService;

	@Inject
	private ISynchronizeService synchronizeService;

	@Inject
	private IResourceService resourceService;

	@Inject
	private ISystemVariableService systemVariableService;

	@Inject
	private ISysLogService sysLogService;

	@Inject
	private SessionDAO sessionDAO;

	/**
	 * 权限更新后，自动更新shrio的权限验证拦截Map，
	 */
	// TODO 权限表更新方法
	@After("(target(com.dp.plat.core.service.IResourceService) && (execution(* com.dp.plat.core.service.IAbstractBaseService.insert*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IAbstractBaseService.update*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IAbstractBaseService.delete*(..)))) ||"
			+ "execution(* com.dp.plat.core.service.IResourceService.insert*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IResourceService.update*(..)) ||"
			+ "execution(* com.dp.plat.core.service.IResourceService.delete*(..))")
	public void updateFilterChainDefinitionMap() {
		// TODO 数据库查询实现,以下为测试
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
			LinkedHashMap<String, String> newMap = new LinkedHashMap<>();
			Resource record = new Resource();
			record.setStatus(1);
			List<com.dp.plat.core.pojo.Resource> resources = resourceService.selectBySelective(record);
			for (Resource tempResource : resources) {
				String url = tempResource.getUrl();
				if (StringUtils.isEmpty(url)) {
					continue;
				}
				String authc = StringUtils.isEmpty(tempResource.getAuthc()) ? "anon" : tempResource.getAuthc();
				newMap.put(url, authc);
			}
			/*
			 * newMap.put("/index", "anon"); newMap.put("/login", "anon");
			 * newMap.put("/static/**", "anon"); newMap.put("/webservice/*",
			 * "anon"); newMap.put("/servlet/captchaCode", "anon");
			 * 
			 * newMap.put("/logout", "logout"); newMap.put("/user/list",
			 * "authc,perms[user:query]"); newMap.put("/**", "authc");
			 */
			shiroFilterFactoryBean.setFilterChainDefinitionMap(newMap);
			Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();

			for (Map.Entry<String, String> entry : chains.entrySet()) {
				String url = entry.getKey();
				String chainDefinition = entry.getValue().trim().replace(" ", "");
				manager.createChain(url, chainDefinition);
			}

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
			+ " || execution(* com.dp.plat.core.service.IUserRoleService.deleteByPrimaryKey(..) )"
			+ " || execution(* com.dp.plat.core.service.IRoleMenuService.batchInsertRoleMenu(..) )")
	public void updateActiveUserMenu(JoinPoint point) {
		Collection<Session> sessions = sessionDAO.getActiveSessions();
		for (Session session : sessions) {
			SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if (principalCollection == null) {
				continue;
			}
			Principal principal = (Principal) principalCollection.getPrimaryPrincipal();
			List<Menu> nodes = shiroService.queryUserMenuByUsername(principal.getUserName());
			principal.setMenus(MenuUtil.drow(nodes, (String) session.getAttribute("contextPath")));
		}
	}

	/**
	 * 当用户角色，菜单以及角色菜单权限变更时，更新用户菜单
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
		System.out.println(SystemConfig.systemVariables);
	}

	@SuppressWarnings("unchecked")
	@Around("execution(* com.dp.plat.core.service.impl.SynchronizeService.selectIncrement*(..) )")
	public void updateSyncState(ProceedingJoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		Map<String, Object> params = null;
		String tableObject = joinPoint.getSignature().getName().replace("selectIncrement", "");
		try {
			if (args == null || args[0] == null) {
				params = synchronizeService.selectSyncState(tableObject);
				if (params == null) {
					params = new HashMap<String, Object>();
					params.put("lastId", "");
					params.put("offset", 0);
					params.put("lastSyncTime", new Date(0));
				} else if (params.get("lastSyncTime") == null) {
					params.put("lastId", "");
					params.put("offset", 0);
					params.put("lastSyncTime", new Date(0));
				}
			} else {
				params = (Map<String, Object>) args[0];
			}
			DataSourceHolder.setDataSourceType("TMS");
			List<?> list = (List<?>) joinPoint.proceed(new Object[] { params });
			Method method = synchronizeService.getClass().getMethod("count" + tableObject);
			Integer offset = (int) method.invoke(synchronizeService);
			;
			String lastId = "";
			if (!list.isEmpty()) {
				Object object = list.get(list.size() - 1);
				Class<?> clazz = object.getClass();
				method = clazz.getDeclaredMethod("getId");
				if (method != null) {
					lastId = String.valueOf(method.invoke(object));
				}
				DataSourceHolder.setDataSourceType("Local");
				SyncState syncState = new SyncState(tableObject, lastId, offset);
				synchronizeService.insertSyncState(syncState);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Pointcut("@within(com.dp.plat.core.annotation.TableNameChanging) || @annotation(com.dp.plat.core.annotation.TableNameChanging)")
	public void serviceAspect() {
	}

	/**
	 * 判断CopyDataFromTemp拷贝数据程序是否正在进行t_main_data和t_node_info 与临时表表名变更
	 * 
	 * @param point
	 */
	@Before("@within(com.dp.plat.core.annotation.TableNameChanging) || @annotation(com.dp.plat.core.annotation.TableNameChanging)")
	public void checkTableNameIsChanging(JoinPoint point) {
		System.out.println(point.getSignature().getName() + ":触发检查");
//		int checkCount = 0;
//		while (CopyDataFromTemp.isChanging()) {
//			try {
//				Thread.sleep(100);
//				System.out.println(point.getSignature().getName() + ":检查中");
//				if (checkCount++ > 50) {
//					break;
//				}
//			} catch (InterruptedException e) {
//				break;
//			}
//		}
		System.out.println(point.getSignature().getName() + ":检查结束");
	}

	/**
	 * 异常捕获和记录，与<code>ExceptionHandler</code>结合使用，获取请求的参数和方法，并记录
	 * 
	 * @param joinPoint
	 * @param e
	 * @see ExceptionHandler
	 */
	@AfterThrowing(pointcut = "execution(* com.dp.plat..*.controller..*(..))", throwing = "e")
	public void exceptionHandler(JoinPoint joinPoint, Throwable e) {
		SysLog log = new SysLog();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		try {
			User user = null;
			String ip = null;
			if (request != null) {
				HttpSession session = request.getSession();
				// 读取session中的用户
				// XXX 用UserContext替换
				user = UserContext.getCurrentUser();
//				user = (User) session.getAttribute("user");

				// 获取请求ip
				ip = request.getRemoteAddr();
			}

			// 获取方法参数的json字符串
			String params = SystemLogAspect.getParamsJson(joinPoint);

			// 将异常记录数据库
			log.setDescription("统一异常解析器" + " -- " + e.getClass().getName());
			log.setExceptionCode(e.getClass().getName());
			log.setType("1");// 1代表异常
			log.setExceptionDetail(ExceptionUtils.getStackTrace(e));
			log.setMethod(joinPoint.getSignature().toString());
			log.setParams(params);
			log.setCreateBy(user != null ? user.getUserName() : "NULL");
			log.setCreateDate(DateUtil.getTodayDateTime());
			log.setRequestIp(ip);
			// 保存数据库
			// sysLogService.insertSelective(log);
			// request.setAttribute("errorLogId", log.getId());
			// session.setAttribute("errorLogId", log.getId());
		} catch (Exception ex) {
			log.setExceptionCode(log.getExceptionCode() + "\r\n" + ex.getClass().getName());
			log.setExceptionDetail(log.getExceptionDetail() + "\r\n" + log.getExceptionDetail());
		} finally {
			sysLogService.insertSelective(log);
			if (request != null) {
				request.setAttribute("errorLogId", log.getId());
				request.setAttribute("error", log.getExceptionCode());
			}
		}
	}

}
