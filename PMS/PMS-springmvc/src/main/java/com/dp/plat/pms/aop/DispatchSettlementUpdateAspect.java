
package com.dp.plat.pms.aop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import com.dp.plat.aop.annotation.AopParam;
import com.dp.plat.aop.util.AspectUtil;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.service.IFileInfoService;
import com.dp.plat.core.service.IUploaderService;
import com.dp.plat.core.util.SystemLogUtil;
import com.dp.plat.exception.CustomRuntimeException;
import com.dp.plat.pms.extend.fp.util.InvoiceUtil;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.service.ICommonRelatedDataService;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.util.AviatorUtils;
import com.dp.plat.util.JSONUtil;

import cn.hutool.core.map.MapUtil;

/**
 * 项目各类状态检查切面类
 * 用于在特定业务操作前后检查项目各类状态
 */
@Aspect
@Component
public class DispatchSettlementUpdateAspect {

    protected final static Logger logger = LoggerFactory.getLogger(DispatchSettlementUpdateAspect.class);

    private static final String TAG = "【项目转包结算更新AOP】";
    private static final String PM_DISPATCH_SETTLEMENT_UPDATE_RULES_CONFIG = "pm.dispatch.settlement.update.rules.config";
    private static final Supplier<Map<String, Object>> configSupplier = new Supplier<Map<String, Object>>() {
        @Override
        public Map<String, Object> get() {
            String config = SystemConfig.systemVariables.getOrDefault(PM_DISPATCH_SETTLEMENT_UPDATE_RULES_CONFIG, "{}");
            return JSON.parseObject(config);
        }
    };

    @Autowired
    private IDispatchSettlementService dispatchSettlementService;
    
    @Autowired
    private IDispatchProjectService dispatchProjectService;
    
    @Autowired
    private IUploaderService uploaderService;
    
    @Autowired
    private IFileInfoService fileInfoService;
    
    @Autowired
    private ICommonRelatedDataService commonRelatedDataService;

    /**
     * 用于存储方法参数的线程局部变量
     */
    private ThreadLocal<Map<String, Object>> config = ThreadLocal.withInitial(configSupplier);

    /**
     * 用于存储方法参数的线程局部变量
     */
    private ThreadLocal<Map<String, Object>> params = new ThreadLocal<>();

    /**
     * 用于存储参数关系的线程局部变量
     */
    private ThreadLocal<Map<String, String>> paramRelations = new ThreadLocal<>();
    
    public DispatchSettlementUpdateAspect() {
        InvoiceUtil.initConfig(configSupplier);
    }

    /**
     * 定义切点，匹配标注了EInvoiceFlag注解的服务方法
     */
    @Pointcut("(target(com.dp.plat.pms.springmvc.service.IDispatchSettlementService) "
              + " && (execution(* com.dp.plat.core.service.IAbstractBaseService.insert*(..))"
              + "     || execution(* com.dp.plat.core.service.IAbstractBaseService.update*(..))" 
              + "    )"
              + ") "
              + " || execution(* com.dp.plat.pms.springmvc.service.IDispatchSettlementService.insert*(..))"
              + " || execution(* com.dp.plat.pms.springmvc.service.IDispatchSettlementService.update*(..))"
    )
    public void verifyAspect() {
        // 切点逻辑为空，仅用于标记切点
    }
    
    /**
     * 环绕通知，用于在目标方法执行前后执行电子发票标记的检查
     *
     * @param joinPoint 切点对象，提供关于正在执行的方法和其他有用的信息。
     * @return 返回目标方法的执行结果。
     * @throws Throwable 如果目标方法执行过程中出现异常
     */
    @Around("verifyAspect()")
    public Object updateDispatchSettlementAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        // 方法执行前的逻辑
        log("开始");
        
        try {
            Map<String, String> paramsRelation = new HashMap<>();
            String[] dataParams = AspectUtil.getMethodParameterNamesByAnnotation(joinPoint, paramsRelation, AopParam.class);
            Map<String, Object> paramsMap = SystemLogUtil.getParamsMap(joinPoint);
            this.paramRelations.set(paramsRelation);
            this.params.set(paramsMap);
    
            updateDispatchSettlementBefore(joinPoint);
            
            // 执行目标方法
            Object result = joinPoint.proceed();
    
            updateDispatchSettlementAfter(joinPoint);
            
            // 方法执行后的逻辑
            log("结束");
            return result;
        } finally {
            this.config.remove();
            this.paramRelations.remove();
            this.params.remove();
        }
    }

    /**
     * 后置返回通知，用于处理方法正常返回后的逻辑
     *
     * @param joinPoint 切点对象
     * @param result 方法的返回结果
     */
//    @AfterReturning(pointcut = "serviceAspect()", returning = "result")
    public void updateDispatchSettlementAfterReturning(JoinPoint joinPoint, Object result) {
        if (result != null && result instanceof Map) {
            params.get().putAll((Map) result);
        }
        updateDispatchSettlementAfter(joinPoint);
    }
    
    /**
     * 后置通知，用于执行方法执行后的通用逻辑
     *
     * @param joinPoint 切点对象
     */
//    @Before("serviceAspect()")
    public void updateDispatchSettlementBefore(JoinPoint joinPoint) {
        log("前置条件触发开始");
        try {
            updateDispatchSettlementByRule(joinPoint, "before");
        } finally {
            log("前置条件触发结束");
        }
    }

    /**
     * 后置通知，用于执行方法执行后的通用逻辑
     *
     * @param joinPoint 切点对象
     */
//    @After("serviceAspect()")
    public void updateDispatchSettlementAfter(JoinPoint joinPoint) {
        log("后置条件触发开始");
        try {
            updateDispatchSettlementByRule(joinPoint, "after");
        } finally {
            log("后置条件触发结束");
        }
    }
    
    /**
     * 用于执行方法执行后的通用逻辑
     *
     * @param joinPoint 切点对象
     */
    public void updateDispatchSettlementByRule(JoinPoint joinPoint, String type) {
        try {
            Map<String, Object> config = getConfig();
            // 从系统上下文中获取检查选项
            if (config != null && !config.isEmpty()) {
                Map<String, Object> typeRules = (Map<String, Object>) config.getOrDefault(type, Collections.emptyMap());
                Boolean enable = MapUtil.getBool(typeRules, "enable", true);
                if (typeRules == null || typeRules.isEmpty() || !enable) {
                    return;
                }
                Map<String, String> paramsRelation = this.paramRelations.get();
                Map<String, Object> paramsMap = this.params.get();
                
                DispatchSettlement settlement = (DispatchSettlement) paramsMap.getOrDefault("settlement", paramsMap.getOrDefault("settlementVO", paramsMap.getOrDefault("record", paramsMap.get("t"))));
                if (settlement == null) {
                    Collection<?> args = Collections.emptyList();
                    if (!paramsMap.isEmpty()) {
                        args = paramsMap.values();
                    } else {
                        args = Arrays.asList(joinPoint.getArgs());
                    }
                    for (Object paramValue : args) {
                        if (paramValue instanceof DispatchSettlement) {
                            settlement = (DispatchSettlement) paramValue;
                        }
                    }
                }
                if (settlement == null) {
                    return;
                }
                Integer dispatchId = settlement.getDispatchId();
                if (dispatchId == null || Integer.valueOf(0).equals(dispatchId)) {
                    return;
                }
                
                execScripts(settlement, typeRules);
                
//                dispatchSettlementService.verifySettlementInvoice(settlement);
            }
        } catch (Exception e) {
            logError("条件触发执行发生错误：{}", e);
        }
    }
    
    /**
     * 检查办理人是否存在启用条件，如果存在启用条件，进行校验
     * 
     * @param variableScope
     * @param variables
     * @return enableApprovers
     */
    public List<Map<String, Object>> checkRule(List<Map<String, Object>> ruleList, Map<String, Object> variables) {
        List<Map<String, Object>> enableRules = new ArrayList<Map<String, Object>>();
        for (Iterator iterator = ruleList.iterator(); iterator.hasNext();) {
            Map<String, Object> rule = (Map<String, Object>) iterator.next();
            // 判断是否满足启用条件，如果不满足进行移除
            if (!checkRule(rule, variables)) {
                iterator.remove();
            } else {
                enableRules.add(rule);
            }
        }
        return enableRules;
    }

    /**
     * 检查是否存在启用条件，如果存在启用条件，进行校验
     * 
     * @param rule
     * @param variables
     * @return
     */
    public boolean checkRule(Map<String, Object> rule, Map<String, Object> variables) {
        // 判断是否存在条件，无条件则默认启用
        boolean enable = !rule.containsKey("condition");
        if (enable) {
            return enable;
        }

        // 存在启用条件，进行校验
        String condition = (String) rule.get("condition");
        Object entity = null;
        if (entity == null) {
            entity = variables.get("entity");
        }
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("entity", entity);
        env.put("config", rule);
        env.put("context", this);

        Object result = false;
        try {
            result = AviatorUtils.exceute(condition, env);
        } catch (Exception e) {
            logError("规则脚本执行发生错误：{}", e);
        }

        return Boolean.TRUE.equals(result);
    }
    
    /**
     * 执行一些脚本
     * 
     * @param target
     * @param config
     * @return
     */
    public Object execScripts(Object target, Map<String, Object> config) {
        return execScripts(target, null, config, null);
    }
    
    /**
     * 执行一些脚本
     * 
     * @param target
     * @param config
     * @param scriptName
     * @return
     */
    public Object execScripts(Object target, Map<String, Object> config, String scriptName) {
        return execScripts(target, null, config, scriptName);
    }

    /**
     * 执行一些脚本
     * 
     * @param target
     * @param entity
     * @param config
     * @param scriptName
     * @return
     */
    public Object execScripts(Object target, Map<String, Object> entity, Map<String, Object> config, String scriptName) {
        if (entity == null) {
            entity = new HashMap<String, Object>();
        }
        entity.put("target", target);
        return execScripts(entity, config, scriptName);
    }

    /**
     * 执行一些脚本
     * 
     * @param presales
     * @param config
     * @return
     */
    public Object execScripts(Map<String, Object> entity, Map<String, Object> config, String scriptName) {
        // 售前测试触发脚本
        String scripts = JSON.toJSON(config.getOrDefault("scripts", "")).toString();
        if (StringUtils.isBlank(scripts)) {
            return null;
        }

        Map<String, Object> env = new HashMap<String, Object>();
        env.put("entity", entity);
        env.put("configs", config);
        env.put("context", this);

        Map<String, Object> scriptMap = JSON.parseObject(scripts, JSONUtil.MapTypeReference);
        List<Map<String, Object>> scriptList = new ArrayList<Map<String, Object>>(scriptMap.size());
        // 默认获取所有的脚本值
        Collection<Object> values = scriptMap.values();
        // 如果指定了具体的脚本名称，则只运行具体的脚本
        if (StringUtils.isNotBlank(scriptName)) {
            values = Collections.singletonList(scriptMap.get(scriptName));
        }
        // 循环解析脚本放入脚本列表
        for (Object value : values) {
            if (value != null) {
                Map<String, Object> script = null;
                if (value instanceof String) {
                    script = JSON.parseObject((String) value, JSONUtil.MapTypeReference);
                } else if (value instanceof Map) {
                    script = (Map<String, Object>) value;
                }
                scriptList.add(script);
            }
        }
        
        
        // 循环执行脚本
        List<Object> results = new ArrayList<Object>(scriptList.size());
        List<String> errorList = new ArrayList<String>(scriptList.size());
        for (Map<String, Object> script : scriptList) {
            try {
                if (ObjectUtils.isNotEmpty(script.get("script"))) {
                    boolean enable = checkRule(script, env);
                    if (enable) {
                        env.put("config", script);
                        Object result = AviatorUtils.exceute(String.valueOf(script.get("script")), env);
                        results.add(result);
                    }
                }
            } catch (Exception e) {
                logError("规则脚本执行发生错误：{}", e);
                errorList.add(e.getMessage());
            }
        }

        // 判断是否正确执行并返回，即使没有返回值的脚本，也会返回NULL
        if (!errorList.isEmpty()) {
            throw new CustomRuntimeException("规则脚本执行发生错误，请检查日志！");
        }

        return results;
    }
    
    public static Map<String, Object> getConfig() {
        try {
            return configSupplier.get();
        } catch (Exception e) {
            return (Map<String, Object>) MapUtils.getMap(SystemConfig.systemVariables, PM_DISPATCH_SETTLEMENT_UPDATE_RULES_CONFIG, new HashMap<String, Object>(0));
        }
    }

    /**
     * 返回交付件发票原件类型
     * @return
     */
    public static Integer getFileInvoiceType() {
        return InvoiceUtil.getFileInvoiceType(getConfig(), 9);
    }
    
    /**
     * 返回交付件验收材料类型
     * @return
     */
    public static Integer getFileInspectionType() {
        return InvoiceUtil.getFileInspectionType(getConfig(), 5);
    }
    
    /**
     * 检查是否是发票类型
     * @return
     */
    public static boolean checkFileInvoiceType(Map<String, Object> invoice) {
        return InvoiceUtil.checkFileInvoiceType(invoice, getConfig());
    }
    
    public static boolean checkFileInvoiceStatus(Map<String, Object> invoice) {
        return InvoiceUtil.checkFileInvoiceStatus(invoice, getConfig());
    }
    
    /**
     * 
     * @param format
     * @param arguments
     */
    public void logInfo(String format, Object... arguments) {
        log(format, false, arguments);
    }
    
    /**
     * 
     * @param format
     * @param arguments
     */
    public void logDebug(String format, Object... arguments) {
        log(format, true, arguments);
    }
    
    /**
     * 
     * @param format
     * @param arguments
     */
    public void logError(String format, Object... arguments) {
        format = new StringBuilder(getTag()).append("-").append(format).toString();
        logger.error(format, arguments);
    }
    
    /**
    *
    * @param format
    * @param arguments
    */
   public void log(String format) {
       log(format, new Object[] {});
   }

    /**
     *
     * @param format
     * @param arguments
     */
    public void log(String format, Object... arguments) {
        Map<String, Object> config = getConfig();
        boolean debug = Boolean.parseBoolean(String.valueOf(config.getOrDefault("debug", false)));
        format = new StringBuilder(getTag()).append("-").append(format).toString();
        if (debug) {
            logger.debug(format, arguments);
        }
    }
    
    /**
     * 
     * @param format
     * @param arguments
     */
    public void log(String format, boolean isDebug, Object... arguments) {
        Map<String, Object> config = getConfig();
        boolean debug = Boolean.parseBoolean(String.valueOf(config.getOrDefault("debug", true)));
        format = new StringBuilder(getTag()).append("-").append(format).toString();
        if (debug && isDebug) {
            logger.debug(format, arguments);
        } else if (!isDebug){
            logger.info(format, arguments);
        }
    }
    
    public static String getTag() {
        return TAG;
    }

    public IDispatchSettlementService getDispatchSettlementService() {
        return dispatchSettlementService;
    }

    public IDispatchProjectService getDispatchProjectService() {
        return dispatchProjectService;
    }

    public IUploaderService getUploaderService() {
        return uploaderService;
    }

    public IFileInfoService getFileInfoService() {
        return fileInfoService;
    }

    public ICommonRelatedDataService getCommonRelatedDataService() {
        return commonRelatedDataService;
    }

}