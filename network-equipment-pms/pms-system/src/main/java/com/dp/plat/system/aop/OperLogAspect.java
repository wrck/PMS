package com.dp.plat.system.aop;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.system.entity.SysOperLog;
import com.dp.plat.system.service.ISysOperLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * AOP aspect that records operation logs for methods annotated with {@link OperLog}.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final ISysOperLogService sysOperLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperLog operLog) throws Throwable {
        SysOperLog logEntity = new SysOperLog();
        logEntity.setTitle(operLog.title());
        logEntity.setBusinessType(operLog.businessType());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logEntity.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        logEntity.setOperName(SecurityUtils.getCurrentUsername());
        logEntity.setOperTime(LocalDateTime.now());

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logEntity.setOperUrl(request.getRequestURI());
            logEntity.setRequestMethod(request.getMethod());
        }
        if (operLog.isSaveRequestData()) {
            try {
                logEntity.setOperParam(objectMapper.writeValueAsString(joinPoint.getArgs()));
            } catch (Exception e) {
                log.warn("Failed to serialize operation params: {}", e.getMessage());
            }
        }

        Object result;
        try {
            result = joinPoint.proceed();
            logEntity.setStatus(0);
            if (operLog.isSaveResponseData()) {
                try {
                    logEntity.setJsonResult(objectMapper.writeValueAsString(result));
                } catch (Exception e) {
                    log.warn("Failed to serialize operation result: {}", e.getMessage());
                }
            }
        } catch (Throwable e) {
            logEntity.setStatus(1);
            logEntity.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            try {
                sysOperLogService.save(logEntity);
            } catch (Exception e) {
                log.error("Failed to save operation log: {}", e.getMessage());
            }
        }
        return result;
    }
}
