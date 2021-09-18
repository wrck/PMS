/**
 * 
 */
package com.dp.plat.core.exception.exceptionHandler;

import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.pojo.SysLog;
import com.dp.plat.core.realms.Principal;
import com.dp.plat.core.service.ISysLogService;
import com.dp.plat.core.util.DateUtil;

/**
 * 与 
 * @author w02611
 *
 */
public class ExceptionHandler implements HandlerExceptionResolver {
	@Resource
	private ISysLogService sysLogService;

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// HttpSession session = request.getSession();
		// Integer errorLogId = (Integer) session.getAttribute("errorLogId");
		Integer errorLogId = (Integer) request.getAttribute("errorLogId");
		ModelAndView modelAndView = new ModelAndView("redirect:/500.html");
//		String message = ex.getMessage();
//		if (StringUtils.isBlank(message)) {
//			message = ex.getClass().getSimpleName();
//		}
		modelAndView.addObject("error", StringUtils.defaultIfBlank(ex.getMessage(), ex.getClass().getSimpleName()));

		if (errorLogId != null) {
			// session.removeAttribute("errorLogId");
			modelAndView.addObject("errorLogId", errorLogId);
		} else {
			SysLog sysLog = new SysLog();
//			ex.printStackTrace();
			try {
				try {
					Principal currentUser = (Principal) SecurityUtils.getSubject().getPrincipal();
					if (currentUser != null) {
						sysLog.setCreateBy(currentUser.getUserName());
					} else {
						String uri = request.getContextPath();
						if (StringUtils.isNotBlank(uri)) {
							String[] path = uri.split("/");
							sysLog.setCreateBy(path[0]);
						}
					}
				} catch (Exception e) {
					sysLog.setCreateBy("system");
				}
				
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				Method method = handlerMethod.getMethod();
				sysLog.setDescription("统一异常解析器" + " -- " + ex.getClass().getName());
				sysLog.setMethod(getMethodString(method));
				// sysLog.setParams(ArrayUtils.toString(method.getParameters()));
				sysLog.setExceptionCode(ex.getClass().getName());
				sysLog.setExceptionDetail(ExceptionUtils.getStackTrace(ex));
				sysLog.setRequestIp(HttpContext.getCurrentIp(request));
				sysLog.setCreateDate(DateUtil.getTodayDateTime());
				sysLog.setType("1");
				sysLogService.insertSelective(sysLog);
			} catch (Throwable e) {
				ex.printStackTrace();
				e.printStackTrace();
			}
			modelAndView.addObject("errorLogId", sysLog.getId());
		}

		// 判断是否为ajax请求
		if (request != null && ((request.getHeader("accept") != null
				&& request.getHeader("accept").indexOf("application/json") > -1)
				|| (request.getHeader("X-Requested-With") != null
						&& request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
			try {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(500);
				// PrintWriter writer = response.getWriter();
				// writer.write(modelAndView.getModelMap().toString());
				// writer.flush();
				// writer.close();
				modelAndView.setViewName("");
			} catch (Exception e) {
			}
		}
		// String errorView = null;
		// if (ex instanceof DataAccessException || ex instanceof SQLException)
		// {
		// errorView = "redirect:/sqlError.html";
		// } else if (ex instanceof RuntimeException) {
		// errorView = "redirect:/RunTimeError.html";
		// } else {
		// errorView = "redirect:/error.html";
		// }
		return modelAndView;
	}

	private String getMethodString(Method method) {
		StringBuilder methodStr = new StringBuilder();
		methodStr.append(method.getReturnType().getName()).append(" ").append(method.getDeclaringClass().getName())
				.append(".").append(method.getName()).append("(");
		Class<?>[] paramTypes = method.getParameterTypes();
		for (Class<?> paramType : paramTypes) {
			methodStr.append(paramType.getSimpleName()).append(", ");
		}
		methodStr.delete(methodStr.length() - 2, methodStr.length());
		methodStr.append(")");
		return methodStr.toString();
	}
	
	public static Integer insertException(Throwable e) {
		ISysLogService sysLogService = (ISysLogService) SpringContext.getBean("sysLogService");
		SysLog sysLog = new SysLog();
		try{
			HttpServletRequest request = HttpContext.getCurrentRequest();
			try {
				Principal currentUser = (Principal) SecurityUtils.getSubject().getPrincipal();
				if (currentUser != null) {
					sysLog.setCreateBy(currentUser.getUserName());
				} else if (request != null){
					String uri = request.getContextPath();
					if (StringUtils.isNotBlank(uri)) {
						String[] path = uri.split("/");
						sysLog.setCreateBy(path[0]);
					}
				}
			} catch (Exception exception) {
				sysLog.setCreateBy("system");
			}
			
			if (request != null){
				sysLog.setParams(JSON.toJSONString(request.getParameterMap()));
				sysLog.setRequestIp(HttpContext.getCurrentIp(request) + " -> " + request.getServletPath());
			}
			
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			String methodName = trace[2].toString();
			sysLog.setDescription("自处理异常" + " -- " + e.getClass().getName());
			sysLog.setMethod(methodName);
			sysLog.setExceptionCode(e.getClass().getName());
			sysLog.setExceptionDetail(ExceptionUtils.getStackTrace(e));
			sysLog.setCreateDate(DateUtil.getTodayDateTime());
			sysLog.setType("1");
			sysLogService.insertSelective(sysLog);
		} catch (Throwable t) {
			e.printStackTrace();
			t.printStackTrace();
		}
		return sysLog.getId();
	}
}
