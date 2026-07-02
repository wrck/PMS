package com.dp.plat.config;

import com.dp.plat.model.entity.SysOperateLog;
import com.dp.plat.service.OperateLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;

/** 操作日志AOP - 迁移自 PreformanceThresholdInterceptor */
@Aspect
@Component
public class OperationLogAspect {
    @Autowired private OperateLogService operateLogService;

    @Pointcut("execution(* com.dp.plat.controller..*.*(..))")
    public void controllerPointCut() {}

    @Around("controllerPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        long elapsed = System.currentTimeMillis() - start;
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                SysOperateLog log = new SysOperateLog();
                log.setOperateUrl(req.getRequestURI());
                log.setOperateMethod(req.getMethod());
                log.setExecuteTime(elapsed);
                log.setCreateTime(LocalDateTime.now());
                operateLogService.add(log);
            }
        } catch (Exception ignored) {}
        return result;
    }
}
