package com.dp.plat.core.aop;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.pojo.SysLog;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.service.ISysLogService;
import com.dp.plat.core.util.DateUtil;
/**
 * 异常捕获切面
 * @author w02611
 *
 */
@Aspect
@Component
public class ExceptionAspect {

	@Autowired
	private ISysLogService sysLogService;

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
				// HttpSession session = request.getSession();
				// 读取session中的用户
				// XXX 用UserContext替换
				user = UserContext.getCurrentUser();
				// user = (User) session.getAttribute("user");

				// 获取请求ip
				ip = HttpContext.getCurrentIp(request);
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
			log.setExceptionDetail(log.getExceptionDetail() + "\r\n" + ex.getMessage());
		} finally {
			sysLogService.insertSelective(log);
			if (request != null) {
				request.setAttribute("errorLogId", log.getId());
				request.setAttribute("error", StringUtils.defaultIfBlank(e.getMessage(), log.getExceptionCode()));
			}
		}
	}

}
