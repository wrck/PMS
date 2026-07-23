package com.dp.plat.system.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.system.aop.OperLogAspect;
import com.dp.plat.system.entity.SysOperLog;
import com.dp.plat.system.service.ISysOperLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Controller 审计单元测试。
 *
 * <p>验证 {@link OperLogAspect} 在方法成功/失败时均记录审计日志，
 * 失败时（如权限拒绝）记录 status=1 + errorMsg。</p>
 *
 * <p>注意：原 GlobalExceptionHandler 已随 PMS 自建安全体系一并移除，
 * 异常 → HTTP 状态码的统一转换现由 yudao-spring-boot-starter-web 的
 * GlobalExceptionHandler 全局组件接管，不再在业务模块单独测试。</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ControllerPermissionTest {

    @Mock
    private ISysOperLogService sysOperLogService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private OperLogAspect operLogAspect;

    @BeforeEach
    void setUp() {
        operLogAspect = new OperLogAspect(sysOperLogService, objectMapper);
    }

    @Test
    @DisplayName("OperLogAspect 成功执行时应记录审计日志（status=0）")
    void shouldRecordAuditLogOnSuccess() throws Throwable {
        Method method = TestController.class.getMethod("createWithOperLog");
        OperLog operLog = method.getAnnotation(OperLog.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(methodSignature.getDeclaringTypeName()).thenReturn("TestController");
        when(methodSignature.getName()).thenReturn("createWithOperLog");
        when(joinPoint.proceed()).thenReturn("success");
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Object result = operLogAspect.around(joinPoint, operLog);

        assertEquals("success", result);
        ArgumentCaptor<SysOperLog> captor = ArgumentCaptor.forClass(SysOperLog.class);
        verify(sysOperLogService).save(captor.capture());
        SysOperLog log = captor.getValue();
        assertEquals("测试模块", log.getTitle());
        assertEquals(1, log.getBusinessType());
        assertEquals(0, log.getStatus());
    }

    @Test
    @DisplayName("OperLogAspect 在 AccessDeniedException 时应记录失败审计日志（status=1）")
    void shouldRecordAuditLogOnAccessDenied() throws Throwable {
        Method method = TestController.class.getMethod("createWithOperLog");
        OperLog operLog = method.getAnnotation(OperLog.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(methodSignature.getDeclaringTypeName()).thenReturn("TestController");
        when(methodSignature.getName()).thenReturn("createWithOperLog");
        when(joinPoint.proceed()).thenThrow(new AccessDeniedException("无权限访问"));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        assertThrows(AccessDeniedException.class, () -> operLogAspect.around(joinPoint, operLog));

        ArgumentCaptor<SysOperLog> captor = ArgumentCaptor.forClass(SysOperLog.class);
        verify(sysOperLogService).save(captor.capture());
        SysOperLog log = captor.getValue();
        assertEquals("测试模块", log.getTitle());
        assertEquals(1, log.getBusinessType());
        assertEquals(1, log.getStatus());
        assertEquals("无权限访问", log.getErrorMsg());
    }

    /**
     * 测试用 Controller：提供标注 {@link OperLog} 的方法供切面拦截。
     */
    public static class TestController {

        @OperLog(title = "测试模块", businessType = 1)
        public String createWithOperLog() {
            return "success";
        }
    }
}
