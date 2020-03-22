package com.dp.plat.core.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.dp.plat.core.annotation.SystemControllerLog;
import com.dp.plat.core.annotation.SystemServiceLog;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.pojo.SysLog;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.service.ISysLogService;
import com.dp.plat.core.util.DateUtil;

/**
 * 切点类
 * 
 */
@Aspect
@Component
@SuppressWarnings("unchecked")
public class SystemLogAspect {
	// 注入Service用于把日志保存数据库
	@Autowired
	private ISysLogService sysLogService;

	// 本地异常日志记录对象
	private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

	private static final String DEFAULT_BEFORE_SPLIT = "$";
	private static final String DEFAULT_AFTER_SPLIT = "$";

	private String beforeSplit = DEFAULT_BEFORE_SPLIT;
	private String afterSplit = DEFAULT_AFTER_SPLIT;

	private Map<String, Object> params = new HashMap<>();

	private static final ConcurrentLinkedQueue<SystemLogUtil> queue = new ConcurrentLinkedQueue<SystemLogUtil>();

	/**
	 * 优先级越往前越高,service层日志注解高于Controller层日志注解
	 */
	private static final Class<? extends Annotation>[] LOG_ANNOTATION_TYPE = new Class[] { SystemServiceLog.class,
			SystemControllerLog.class };

	// Service层切点
	@Pointcut("@within(com.dp.plat.core.annotation.SystemServiceLog) || @annotation(com.dp.plat.core.annotation.SystemServiceLog)")
	public void serviceAspect() {
	}

	// Controller层切点
	@Pointcut("@within(com.dp.plat.core.annotation.SystemControllerLog) || @annotation(com.dp.plat.core.annotation.SystemControllerLog)")
	public void controllerAspect() {
	}

	/**
	 * 后置通知 用于记录Controller层的操作
	 * 
	 * @param joinPoint
	 *            切点
	 */
	@After("serviceAspect() || controllerAspect()")
	public void doAfter(JoinPoint joinPoint) {
		final HttpServletRequest request = getRequest(joinPoint);
		// HttpSession session = request != null ? request.getSession() : null;
		// 读取session中的用户
		// XXX 用UserContext替换
		final User user = UserContext.getCurrentUser();
		// User user = (User) session.getAttribute("user");
		// 请求的IP
		final String ip = request != null ? request.getRemoteAddr() + ":" + request.getRemotePort() : null;
		try {
			SystemLogUtil systemLogUtil = new SystemLogUtil(joinPoint, user, ip, sysLogService);
			
			queue.add(systemLogUtil);
			
			ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
			LogThread thread = new LogThread();
			singleThreadExecutor.submit(thread);
		} catch (Exception ex) {
			ExceptionHandler.insertException(ex);
			// 记录本地异常日志
			logger.error("异常信息:{}", ex);
		}
	}

	
	/**
	 * 异常通知 用于拦截service层记录异常日志
	 * 
	 * @param joinPoint
	 * @param e
	 */
	@AfterThrowing(pointcut = "serviceAspect() || controllerAspect()", throwing = "e")
	public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
		HttpServletRequest request = getRequest(joinPoint);
		// HttpSession session = request != null ? request.getSession() : null;
		// 读取session中的用户
		// XXX 用UserContext替换
		User user = UserContext.getCurrentUser();
		// User user = (User) session.getAttribute("user");

		// 获取请求ip
		String ip = request != null ? request.getRemoteAddr() + ":" + request.getRemotePort() : null;

		try {
			/* ========控制台输出========= */
			String description = processSystemLogDescription(joinPoint, SystemControllerLog.class);
			String paramsJsonStr = JSON.toJSONString(params);

			logger.debug("=====异常通知开始=====");
			logger.debug("异常代码:" + e.getClass().getName());
			logger.debug("异常信息:" + ExceptionUtils.getStackTrace(e));
			logger.debug("异常方法:" + joinPoint.getSignature().toString());
			// logger.debug("方法描述:" + getSystemLogDescription(joinPoint));
			// logger.debug("请求参数:" + getParamsJson(joinPoint));
			logger.debug("方法描述:" + description);
			logger.debug("方法参数:" + paramsJsonStr);
			logger.debug("请求人:" + (user != null ? user.getUserName() : "NULL"));
			logger.debug("请求IP:" + ip);
			/* ==========数据库日志========= */
			SysLog log = new SysLog();
			// log.setDescription(getSystemLogDescription(joinPoint));
			log.setDescription(description);
			log.setExceptionCode(e.getClass().getName());
			log.setType("1");// 1代表异常
			log.setExceptionDetail(ExceptionUtils.getStackTrace(e));
			log.setMethod(joinPoint.getSignature().toString());
			// log.setParams(getParamsJson(joinPoint));
			log.setParams(paramsJsonStr);
			log.setCreateBy(user != null ? user.getUserName() : "NULL");
			log.setCreateDate(DateUtil.getTodayDateTime());
			log.setRequestIp(ip);
			// 保存数据库
			sysLogService.insert(log);
			logger.debug("=====异常通知结束=====");
		} catch (Exception ex) {
			ExceptionHandler.insertException(ex);
			// 记录本地异常日志
			logger.error("==异常通知异常==");
			logger.error("异常信息:{}", ex);
		}
		/* ==========记录本地异常日志========== */
		logger.error("异常方法:{}\r\n异常代码:{}\r\n异常信息:{}\r\n参数:{}", joinPoint.getSignature().toString(),
				e.getClass().getName(), ExceptionUtils.getStackTrace(e), ArrayUtils.toString(joinPoint.getArgs()));

	}

	/**
	 * 获取注解中对方法的描述信息 用于service层注解
	 * 
	 * @param joinPoint
	 *            切点
	 * @return 方法描述
	 * @throws Exception
	 * @deprecated use getSystemLogDescription() instead
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static String getServiceMthodDescription(JoinPoint joinPoint) throws Exception {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		String description = "";
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					description = method.getAnnotation(SystemServiceLog.class).description();
					break;
				}
			}
		}
		return description;
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 * 
	 * @param joinPoint
	 *            切点
	 * @return 方法描述
	 * @throws Exception
	 * @deprecated use getSystemLogDescription() instead
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		String description = "";
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					description = method.getAnnotation(SystemControllerLog.class).description();
					break;
				}
			}
		}
		return description;
	}

	/**
	 * 根据注解类型，获取注解中对方法的描述信息
	 * 
	 * @param joinPoint
	 * @param specialType
	 *            systemLog annotion type
	 * @return description
	 * @throws Exception
	 * 
	 */
	private static Annotation getSystemLogAnnotation(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		Class<?> targetClass = joinPoint.getTarget().getClass();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Annotation annotation = null;
		// 根据特定的注解获取描述
		if (specialType.length > 0) {
			Class<? extends Annotation> annotationType = specialType[0];
			annotation = signature.getMethod().getAnnotation(annotationType);
			if (annotation == null) {
				annotation = targetClass.getAnnotation(annotationType);
			}
		}
		// 假如指定的注解不存在，则从LOG_ANNOTATION_TYPE中匹配
		if (annotation == null) {
			for (Class<? extends Annotation> annotationType : LOG_ANNOTATION_TYPE) {
				annotation = signature.getMethod().getAnnotation(annotationType);
				if (annotation == null) {
					annotation = targetClass.getAnnotation(annotationType);
				}
				// 优先级高的找到后跳出循环，若LOG_ANNOTATION_TYPE反序，注释该部分
				if (annotation != null) {
					break;
				}
			}
		}
		return annotation;
	}

	/**
	 * 根据注解类型，获取注解中对方法的描述信息
	 * 
	 * @param joinPoint
	 * @param specialType
	 *            systemLog annotion type
	 * @return description
	 * @throws Exception
	 * 
	 */
	private String getSystemLogDescription(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		Annotation annotation = getSystemLogAnnotation(joinPoint, specialType);
		String description = null;
		if (annotation != null) {
			Method method = annotation.getClass().getDeclaredMethod("description");
			description = (String) method.invoke(annotation);
		}
		return description;
	}

	/**
	 * 处理注解的描述信息，替换掉存在的变量
	 * 
	 * @param joinPoint
	 * @param specialType
	 * @return
	 * @throws Exception
	 */
	private String processSystemLogDescription(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		String description = getSystemLogDescription(joinPoint, specialType);
		if (StringUtils.isNotBlank(description)) {
			initSystemLogSplitFlags(joinPoint, specialType);
			params = getParamsMap(joinPoint);
			// 判断描述中是否有变量替换标识位
			if (description.contains(beforeSplit) && description.contains(beforeSplit)) {
				Map<String, Object> newParams = mergerSubMap(params);
				// for (Entry<String, Object> entry : newParams.entrySet()) {
				// String key = entry.getKey();
				// Object value = entry.getValue();
				// value = (value == null) ? "" : value;
				// String regex = "\\Q" + key + "\\E";
				// if (key.contains(beforeSplit) && key.contains(afterSplit)) {
				// regex = "\\Q" + key + "\\E";
				// } else if (key.contains(beforeSplit) &&
				// !key.contains(afterSplit)) {
				// regex = "\\Q" + key + afterSplit + "\\E";
				// } else if (!key.contains(beforeSplit) &&
				// key.contains(afterSplit)) {
				// regex = "\\Q" + beforeSplit + key + "\\E";
				// } else {
				// regex = "\\Q" + beforeSplit + key + afterSplit + "\\E";
				// }
				// description = description.replaceAll(regex,
				// value.toString());
				// }
				// description = description.replaceAll("\\" + beforeSplit +
				// "(\\w+)\\" + afterSplit, "");

				// 将所有变量参数都转化为map
				String objStr = JSON.toJSONString(newParams);
				newParams = JSON.parseObject(objStr, HashMap.class);

				Set<String> fieldSet = new HashSet<String>();
				String regex = "\\" + beforeSplit + "([^\\" + beforeSplit + "\\" + afterSplit + "]*)" + "\\"
						+ afterSplit;
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(description);
				while (matcher.find()) {
					fieldSet.add(matcher.group());
				}
				for (String field : fieldSet) {
					if (StringUtils.isBlank(field)) {
						continue;
					}

					Object value = parseObjectValue(field, newParams);
					description = description.replaceAll("\\Q" + field + "\\E", value.toString());
				}
				description = description.replaceAll("\\[( )*\\]", "");
			}
		}
		return description;
	}

	/**
	 * 根据注解类型，获取注解中对方法的替换参数标识
	 * 
	 * @param joinPoint
	 * @param specialType
	 *            systemLog annotion type
	 * @return description
	 * @throws Exception
	 * 
	 */
	private void initSystemLogSplitFlags(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		Annotation annotation = getSystemLogAnnotation(joinPoint, specialType);
		String[] splitFlags = {};
		if (annotation != null) {
			Method method = annotation.getClass().getDeclaredMethod("splitFlags");
			splitFlags = (String[]) method.invoke(annotation);
		}
		if (splitFlags.length == 1) {
			String splitFlag = splitFlags[0];
			if (StringUtils.isNotBlank(splitFlag)) {
				this.beforeSplit = splitFlag;
				this.afterSplit = splitFlag;
			}
		} else if (splitFlags.length >= 2) {
			String beforeSplit = splitFlags[0];
			String afterSplit = splitFlags[1];
			if (StringUtils.isNotBlank(beforeSplit)) {
				this.beforeSplit = beforeSplit;
			}
			if (StringUtils.isNotBlank(afterSplit)) {
				this.afterSplit = afterSplit;
			}
		}
		return;
	}

	/**
	 * 根据注解类型，获取注解中需要忽略的参数
	 * 
	 * @param joinPoint
	 * @param specialType
	 *            systemLog annotion type
	 * @return description
	 * @throws Exception
	 * 
	 */

	private static String[] getSystemLogIgnoreParams(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		Annotation annotation = getSystemLogAnnotation(joinPoint, specialType);
		String[] ignoreParams = {};
		if (annotation != null) {
			Method method = annotation.getClass().getDeclaredMethod("ignoreParams");
			ignoreParams = (String[]) method.invoke(annotation);
		}
		return ignoreParams;
	}

	/**
	 * 将请求参数封装成JSON字符串
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Exception
	 */
	public static String getParamsJson(JoinPoint joinPoint) throws Exception {
		Map<String, Object> params = getParamsMap(joinPoint);
		return JSON.toJSONString(params);
	}

	/**
	 * 将请求参数封装成Map
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> getParamsMap(JoinPoint joinPoint) throws Exception {
		Map<String, Object> params = new HashMap<>();
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String[] paramsName = methodSignature.getParameterNames();
		Object[] paramsValue = joinPoint.getArgs();
		String[] ignoreParams = getSystemLogIgnoreParams(joinPoint);
		for (int i = 0; i < paramsValue.length; i++) {
			Object value = paramsValue[i];
			if (paramsName[i] == null || paramsName[i].toLowerCase().indexOf("request") >= 0
					|| paramsName[i].toLowerCase().indexOf("response") >= 0
					|| ArrayUtils.contains(ignoreParams, paramsName[i])) {
				continue;
			}
			if (paramsName[i].indexOf("model") >= 0 && value instanceof Model) {
				Map<String, Object> model = ((Model) value).asMap();
				Map<String, Object> result = new HashMap<String, Object>(model.size());
				Set<String> renderedAttributes = model.keySet();
				for (Map.Entry<String, Object> entry : model.entrySet()) {
					if (!(entry.getValue() instanceof BindingResult) && renderedAttributes.contains(entry.getKey())) {
						result.put(entry.getKey(), entry.getValue());
					}
				}
				value = result;
			}
			params.put(paramsName[i], value);
		}
		if (ignoreParams != null && ignoreParams.length > 0) {
			params.put("ignoreParams", ignoreParams);
		}
		return params;
	}

	/**
	 * 获取当前的HttpRequest请求，如果不是HttpRequest请求，则查询是否有webservice上下文
	 * 
	 * @param joinPoint
	 * @return
	 */
	private HttpServletRequest getRequest(JoinPoint joinPoint) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();

		HttpServletRequest request = requestAttributes != null ? requestAttributes.getRequest() : null;
		// 无法获取Http请求时，查询是否为Webservice 请求
//		if (request == null) {
//			try {
//				Object target = joinPoint.getTarget();
//				Field field = target.getClass().getDeclaredField("wsContext");
//				if (field != null) {
//					field.setAccessible(true);
//					WebServiceContext wsContext = (WebServiceContext) field.get(target);
//					// Method method =
//					// target.getClass().getMethod("getWsContext");
//					// WebServiceContext wsContext = (WebServiceContext)
//					// method.invoke(target);
//					request = (HttpServletRequest) wsContext.getMessageContext()
//							.get(AbstractHTTPDestination.HTTP_REQUEST);
//				}
//			} catch (Throwable ex) {
//				ex.printStackTrace();
//			}
//		}
		return request;
	}

	/**
	 * 合并变量中的Map
	 * 
	 * @param params
	 * @return
	 */
	private Map<String, Object> mergerSubMap(Map<String, Object> params) {
		Map<String, Object> mergeredMap = new HashMap<>();
		mergeredMap.putAll(params);
		for (Entry<String, Object> entity : params.entrySet()) {
			Object value = entity.getValue();
			if (value != null && value instanceof String) {
				try {
					value = JSON.parseObject((String) value, HashMap.class);
				} catch (Exception e) {
				}
			}
			if (value != null && value instanceof Map) {
				mergeredMap.putAll((Map<? extends String, ? extends Object>) value);
			}
		}
		return mergeredMap;
	}

	/**
	 * 解析field字段的值，例如，user.username,获取user对象的username属性值
	 * 
	 * @param field
	 * @param newParams
	 * @return
	 */
	public Object parseObjectValue(String field, Map<String, Object> newParams) {
		if (StringUtils.isBlank(field)) {
			return "";
		}
		String key = field.replaceAll("\\" + beforeSplit + "|\\" + afterSplit, "");
		Object value = "";
		String[] relations = null;
		if (key.contains(".") && !newParams.containsKey(key)) {
			relations = key.split("\\.");
			StringBuilder prevRelation = new StringBuilder();
			for (int i = 0; i < relations.length; i++) {
				String relation = relations[i];
				value = newParams.getOrDefault(prevRelation + relation, "");
				try {
					if (value instanceof String || value instanceof Integer) {
						break;
					}

					Map<String, Object> parseMap = new HashMap<>();
					if (value instanceof Map) {
						parseMap = (Map<String, Object>) value;
					} else {
						String objStr = JSON.toJSONString(value);
						parseMap = JSON.parseObject(objStr, HashMap.class);
					}
					for (Entry<String, Object> entry : parseMap.entrySet()) {
						Object tempValue = entry.getValue();
						if (tempValue != null) {
							newParams.put(prevRelation + relation + "." + entry.getKey(), tempValue);
						}
					}
					value = parseMap.getOrDefault(relation, "");
					prevRelation.append(relation).append(".");
				} catch (Exception e) {
					ExceptionHandler.insertException(e);
				}
			}
		} else {
			value = newParams.getOrDefault(key, "");
		}
		return value;
	}

	/**
	 * 解析field字段的值，例如，user.username,获取user对象的username属性值
	 * 
	 * @param field
	 * @param newParams
	 *            已将所有map中的object转换为map后得到的新Map
	 * @return
	 */
	public Object parseMapValue(String field, Map<String, Object> newParams) {
		if (StringUtils.isBlank(field)) {
			return "";
		}
		String key = field.replaceAll("\\" + beforeSplit + "|\\" + afterSplit, "");
		Object value = "";
		String[] relations = null;
		if (key.contains(".") && !newParams.containsKey(key)) {
			relations = key.split("\\.");
			Map<String, Object> tempParams = newParams;
			for (String tempKey : relations) {
				Object tempValue = tempParams.getOrDefault(tempKey, "");
				if (tempValue instanceof Map) {
					tempParams = (Map<String, Object>) tempValue;
					continue;
				}
				value = tempValue;
				break;
			}
		} else {
			value = newParams.getOrDefault(key, "");
		}
		return value;
	}
	
	
	class LogThread extends Thread {
		private SystemLogAspect logAspect;
		
		@Override
		public void run() {
			while(!logAspect.queue.isEmpty()) {
				System.err.println(logAspect.queue.size());
				SystemLogUtil systemLogAspect = logAspect.queue.poll();
				if (systemLogAspect != null) {
					systemLogAspect.log();
				}
			}
		}
	}

}
class SystemLogUtil {
	private static final String DEFAULT_BEFORE_SPLIT = "$";
	private static final String DEFAULT_AFTER_SPLIT = "$";

	private String beforeSplit = DEFAULT_BEFORE_SPLIT;
	private String afterSplit = DEFAULT_AFTER_SPLIT;
	
	private Map<String, Object> params = new HashMap<>();

	private JoinPoint joinPoint;
	private User user;
	private String ip;
	private Throwable e;
	
	private ISysLogService sysLogService;
	
	// 本地异常日志记录对象
	private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);
	
	/**
	 * 优先级越往前越高,service层日志注解高于Controller层日志注解
	 */
	private static final Class<? extends Annotation>[] LOG_ANNOTATION_TYPE = new Class[] { SystemServiceLog.class,
			SystemControllerLog.class };
	
	
	public SystemLogUtil() {
		super();
	}

	public SystemLogUtil(JoinPoint joinPoint, User user, String ip, ISysLogService sysLogService) {
		super();
		this.joinPoint = joinPoint;
		this.user = user;
		this.ip = ip;
		this.sysLogService = sysLogService;
	}

	public SystemLogUtil(JoinPoint joinPoint, User user, String ip, Throwable e, ISysLogService sysLogService) {
		super();
		this.joinPoint = joinPoint;
		this.user = user;
		this.ip = ip;
		this.e = e;
		this.sysLogService = sysLogService;
	}

	public void log() {
		if (this.e != null) {
			this.logError();
		} else {
			this.logInfo();
		}
	}

	private void logInfo() {
		try {
			// *========控制台输出=========*//
			String description = this.processSystemLogDescription(joinPoint, SystemControllerLog.class);
			String paramsJsonStr = JSON.toJSONString(params);
			logger.debug("=====后置通知开始=====");
			logger.debug("请求方法:" + joinPoint.getSignature().toString());
			// logger.debug("方法描述:" + getSystemLogDescription(joinPoint,
			// SystemControllerLog.class));
			// logger.debug("方法参数:" + getParamsJson(joinPoint));
			logger.debug("方法描述:" + description);
			logger.debug("方法参数:" + paramsJsonStr);
			logger.debug("请求人:" + (user != null ? user.getUserName() : "NULL"));
			logger.debug("请求IP:" + ip);
			// *========数据库日志=========*//
			SysLog log = new SysLog();
			// log.setDescription(processSystemLogDescription(joinPoint,
			// SystemControllerLog.class));
			log.setDescription(description);
			log.setMethod(joinPoint.getSignature().toString());
			log.setType("0");// 0 代表正常日志
			log.setRequestIp(ip);
			log.setExceptionCode(null);
			log.setExceptionDetail(null);
			// log.setParams(getParamsJson(joinPoint));
			log.setParams(paramsJsonStr);
			log.setCreateBy(user != null ? user.getUserName() : "NULL");
			log.setCreateDate(DateUtil.getTodayDateTime());
			// 保存数据库
			sysLogService.insert(log);
			logger.debug("=====后置通知结束=====");
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
			// 记录本地异常日志
			logger.error("==后置通知异常==");
			logger.error("异常信息:{}", e);
		}
	}
	
	private void logError() {
		try {
			/* ========控制台输出========= */
			String description = processSystemLogDescription(joinPoint, SystemControllerLog.class);
			String paramsJsonStr = JSON.toJSONString(params);

			logger.debug("=====异常通知开始=====");
			logger.debug("异常代码:" + e.getClass().getName());
			logger.debug("异常信息:" + ExceptionUtils.getStackTrace(e));
			logger.debug("异常方法:" + joinPoint.getSignature().toString());
			// logger.debug("方法描述:" + getSystemLogDescription(joinPoint));
			// logger.debug("请求参数:" + getParamsJson(joinPoint));
			logger.debug("方法描述:" + description);
			logger.debug("方法参数:" + paramsJsonStr);
			logger.debug("请求人:" + (user != null ? user.getUserName() : "NULL"));
			logger.debug("请求IP:" + ip);
			/* ==========数据库日志========= */
			SysLog log = new SysLog();
			// log.setDescription(getSystemLogDescription(joinPoint));
			log.setDescription(description);
			log.setExceptionCode(e.getClass().getName());
			log.setType("1");// 1代表异常
			log.setExceptionDetail(ExceptionUtils.getStackTrace(e));
			log.setMethod(joinPoint.getSignature().toString());
			// log.setParams(getParamsJson(joinPoint));
			log.setParams(paramsJsonStr);
			log.setCreateBy(user != null ? user.getUserName() : "NULL");
			log.setCreateDate(DateUtil.getTodayDateTime());
			log.setRequestIp(ip);
			// 保存数据库
			sysLogService.insert(log);
			logger.debug("=====异常通知结束=====");
		} catch (Exception ex) {
			ExceptionHandler.insertException(ex);
			// 记录本地异常日志
			logger.error("==异常通知异常==");
			logger.error("异常信息:{}", ex);
		}
	}
	
	/**
	 * 根据注解类型，获取注解中对方法的描述信息
	 * 
	 * @param joinPoint
	 * @param specialType
	 *            systemLog annotion type
	 * @return description
	 * @throws Exception
	 * 
	 */
	private Annotation getSystemLogAnnotation(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		Class<?> targetClass = joinPoint.getTarget().getClass();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Annotation annotation = null;
		// 根据特定的注解获取描述
		if (specialType.length > 0) {
			Class<? extends Annotation> annotationType = specialType[0];
			annotation = signature.getMethod().getAnnotation(annotationType);
			if (annotation == null) {
				annotation = targetClass.getAnnotation(annotationType);
			}
		}
		// 假如指定的注解不存在，则从LOG_ANNOTATION_TYPE中匹配
		if (annotation == null) {
			for (Class<? extends Annotation> annotationType : LOG_ANNOTATION_TYPE) {
				annotation = signature.getMethod().getAnnotation(annotationType);
				if (annotation == null) {
					annotation = targetClass.getAnnotation(annotationType);
				}
				// 优先级高的找到后跳出循环，若LOG_ANNOTATION_TYPE反序，注释该部分
				if (annotation != null) {
					break;
				}
			}
		}
		return annotation;
	}

	/**
	 * 根据注解类型，获取注解中对方法的描述信息
	 * 
	 * @param joinPoint
	 * @param specialType
	 *            systemLog annotion type
	 * @return description
	 * @throws Exception
	 * 
	 */
	private String getSystemLogDescription(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		Annotation annotation = getSystemLogAnnotation(joinPoint, specialType);
		String description = null;
		if (annotation != null) {
			Method method = annotation.getClass().getDeclaredMethod("description");
			description = (String) method.invoke(annotation);
		}
		return description;
	}

	/**
	 * 处理注解的描述信息，替换掉存在的变量
	 * 
	 * @param joinPoint
	 * @param specialType
	 * @return
	 * @throws Exception
	 */
	private String processSystemLogDescription(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		String description = getSystemLogDescription(joinPoint, specialType);
		if (StringUtils.isNotBlank(description)) {
			initSystemLogSplitFlags(joinPoint, specialType);
			params = getParamsMap(joinPoint);
			// 判断描述中是否有变量替换标识位
			if (description.contains(beforeSplit) && description.contains(beforeSplit)) {
				Map<String, Object> newParams = mergerSubMap(params);
				// for (Entry<String, Object> entry : newParams.entrySet()) {
				// String key = entry.getKey();
				// Object value = entry.getValue();
				// value = (value == null) ? "" : value;
				// String regex = "\\Q" + key + "\\E";
				// if (key.contains(beforeSplit) && key.contains(afterSplit)) {
				// regex = "\\Q" + key + "\\E";
				// } else if (key.contains(beforeSplit) &&
				// !key.contains(afterSplit)) {
				// regex = "\\Q" + key + afterSplit + "\\E";
				// } else if (!key.contains(beforeSplit) &&
				// key.contains(afterSplit)) {
				// regex = "\\Q" + beforeSplit + key + "\\E";
				// } else {
				// regex = "\\Q" + beforeSplit + key + afterSplit + "\\E";
				// }
				// description = description.replaceAll(regex,
				// value.toString());
				// }
				// description = description.replaceAll("\\" + beforeSplit +
				// "(\\w+)\\" + afterSplit, "");

				// 将所有变量参数都转化为map
				String objStr = JSON.toJSONString(newParams);
				newParams = JSON.parseObject(objStr, HashMap.class);

				Set<String> fieldSet = new HashSet<String>();
				String regex = "\\" + beforeSplit + "([^\\" + beforeSplit + "\\" + afterSplit + "]*)" + "\\"
						+ afterSplit;
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(description);
				while (matcher.find()) {
					fieldSet.add(matcher.group());
				}
				for (String field : fieldSet) {
					if (StringUtils.isBlank(field)) {
						continue;
					}

					Object value = parseObjectValue(field, newParams);
					description = description.replaceAll("\\Q" + field + "\\E", value.toString());
				}
				description = description.replaceAll("\\[( )*\\]", "");
			}
		}
		return description;
	}

	/**
	 * 根据注解类型，获取注解中对方法的替换参数标识
	 * 
	 * @param joinPoint
	 * @param specialType
	 *            systemLog annotion type
	 * @return description
	 * @throws Exception
	 * 
	 */
	private void initSystemLogSplitFlags(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		Annotation annotation = getSystemLogAnnotation(joinPoint, specialType);
		String[] splitFlags = {};
		if (annotation != null) {
			Method method = annotation.getClass().getDeclaredMethod("splitFlags");
			splitFlags = (String[]) method.invoke(annotation);
		}
		if (splitFlags.length == 1) {
			String splitFlag = splitFlags[0];
			if (StringUtils.isNotBlank(splitFlag)) {
				this.beforeSplit = splitFlag;
				this.afterSplit = splitFlag;
			}
		} else if (splitFlags.length >= 2) {
			String beforeSplit = splitFlags[0];
			String afterSplit = splitFlags[1];
			if (StringUtils.isNotBlank(beforeSplit)) {
				this.beforeSplit = beforeSplit;
			}
			if (StringUtils.isNotBlank(afterSplit)) {
				this.afterSplit = afterSplit;
			}
		}
		return;
	}

	/**
	 * 根据注解类型，获取注解中需要忽略的参数
	 * 
	 * @param joinPoint
	 * @param specialType
	 *            systemLog annotion type
	 * @return description
	 * @throws Exception
	 * 
	 */

	private String[] getSystemLogIgnoreParams(JoinPoint joinPoint, Class<? extends Annotation>... specialType)
			throws Exception {
		Annotation annotation = getSystemLogAnnotation(joinPoint, specialType);
		String[] ignoreParams = {};
		if (annotation != null) {
			Method method = annotation.getClass().getDeclaredMethod("ignoreParams");
			ignoreParams = (String[]) method.invoke(annotation);
		}
		return ignoreParams;
	}

	/**
	 * 将请求参数封装成JSON字符串
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Exception
	 */
	public String getParamsJson(JoinPoint joinPoint) throws Exception {
		Map<String, Object> params = getParamsMap(joinPoint);
		return JSON.toJSONString(params);
	}

	/**
	 * 将请求参数封装成Map
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getParamsMap(JoinPoint joinPoint) throws Exception {
		Map<String, Object> params = new HashMap<>();
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String[] paramsName = methodSignature.getParameterNames();
		Object[] paramsValue = joinPoint.getArgs();
		String[] ignoreParams = getSystemLogIgnoreParams(joinPoint);
		for (int i = 0; i < paramsValue.length; i++) {
			Object value = paramsValue[i];
			if (paramsName[i] == null || paramsName[i].toLowerCase().indexOf("request") >= 0
					|| paramsName[i].toLowerCase().indexOf("response") >= 0
					|| ArrayUtils.contains(ignoreParams, paramsName[i])) {
				continue;
			}
			if (paramsName[i].indexOf("model") >= 0 && value instanceof Model) {
				Map<String, Object> model = ((Model) value).asMap();
				Map<String, Object> result = new HashMap<String, Object>(model.size());
				Set<String> renderedAttributes = model.keySet();
				for (Map.Entry<String, Object> entry : model.entrySet()) {
					if (!(entry.getValue() instanceof BindingResult) && renderedAttributes.contains(entry.getKey())) {
						result.put(entry.getKey(), entry.getValue());
					}
				}
				value = result;
			}
			params.put(paramsName[i], value);
		}
		if (ignoreParams != null && ignoreParams.length > 0) {
			params.put("ignoreParams", ignoreParams);
		}
		return params;
	}

	/**
	 * 获取当前的HttpRequest请求，如果不是HttpRequest请求，则查询是否有webservice上下文
	 * 
	 * @param joinPoint
	 * @return
	 */
	private HttpServletRequest getRequest(JoinPoint joinPoint) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();

		HttpServletRequest request = requestAttributes != null ? requestAttributes.getRequest() : null;
		// 无法获取Http请求时，查询是否为Webservice 请求
//		if (request == null) {
//			try {
//				Object target = joinPoint.getTarget();
//				Field field = target.getClass().getDeclaredField("wsContext");
//				if (field != null) {
//					field.setAccessible(true);
//					WebServiceContext wsContext = (WebServiceContext) field.get(target);
//					// Method method =
//					// target.getClass().getMethod("getWsContext");
//					// WebServiceContext wsContext = (WebServiceContext)
//					// method.invoke(target);
//					request = (HttpServletRequest) wsContext.getMessageContext()
//							.get(AbstractHTTPDestination.HTTP_REQUEST);
//				}
//			} catch (Throwable ex) {
//				ex.printStackTrace();
//			}
//		}
		return request;
	}

	/**
	 * 合并变量中的Map
	 * 
	 * @param params
	 * @return
	 */
	private Map<String, Object> mergerSubMap(Map<String, Object> params) {
		Map<String, Object> mergeredMap = new HashMap<>();
		mergeredMap.putAll(params);
		for (Entry<String, Object> entity : params.entrySet()) {
			Object value = entity.getValue();
			if (value != null && value instanceof String) {
				try {
					value = JSON.parseObject((String) value, HashMap.class);
				} catch (Exception e) {
				}
			}
			if (value != null && value instanceof Map) {
				mergeredMap.putAll((Map<? extends String, ? extends Object>) value);
			}
		}
		return mergeredMap;
	}

	/**
	 * 解析field字段的值，例如，user.username,获取user对象的username属性值
	 * 
	 * @param field
	 * @param newParams
	 * @return
	 */
	public Object parseObjectValue(String field, Map<String, Object> newParams) {
		if (StringUtils.isBlank(field)) {
			return "";
		}
		String key = field.replaceAll("\\" + beforeSplit + "|\\" + afterSplit, "");
		Object value = "";
		String[] relations = null;
		if (key.contains(".") && !newParams.containsKey(key)) {
			relations = key.split("\\.");
			StringBuilder prevRelation = new StringBuilder();
			for (int i = 0; i < relations.length; i++) {
				String relation = relations[i];
				value = newParams.getOrDefault(prevRelation + relation, "");
				try {
					if (value instanceof String || value instanceof Integer) {
						break;
					}

					Map<String, Object> parseMap = new HashMap<>();
					if (value instanceof Map) {
						parseMap = (Map<String, Object>) value;
					} else {
						String objStr = JSON.toJSONString(value);
						parseMap = JSON.parseObject(objStr, HashMap.class);
					}
					for (Entry<String, Object> entry : parseMap.entrySet()) {
						Object tempValue = entry.getValue();
						if (tempValue != null) {
							newParams.put(prevRelation + relation + "." + entry.getKey(), tempValue);
						}
					}
					value = parseMap.getOrDefault(relation, "");
					prevRelation.append(relation).append(".");
				} catch (Exception e) {
					ExceptionHandler.insertException(e);
				}
			}
		} else {
			value = newParams.getOrDefault(key, "");
		}
		return value;
	}

	/**
	 * 解析field字段的值，例如，user.username,获取user对象的username属性值
	 * 
	 * @param field
	 * @param newParams
	 *            已将所有map中的object转换为map后得到的新Map
	 * @return
	 */
	public Object parseMapValue(String field, Map<String, Object> newParams) {
		if (StringUtils.isBlank(field)) {
			return "";
		}
		String key = field.replaceAll("\\" + beforeSplit + "|\\" + afterSplit, "");
		Object value = "";
		String[] relations = null;
		if (key.contains(".") && !newParams.containsKey(key)) {
			relations = key.split("\\.");
			Map<String, Object> tempParams = newParams;
			for (String tempKey : relations) {
				Object tempValue = tempParams.getOrDefault(tempKey, "");
				if (tempValue instanceof Map) {
					tempParams = (Map<String, Object>) tempValue;
					continue;
				}
				value = tempValue;
				break;
			}
		} else {
			value = newParams.getOrDefault(key, "");
		}
		return value;
	}
}

