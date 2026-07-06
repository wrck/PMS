package com.dp.plat.system.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.exception.GlobalExceptionHandler;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.result.ResultCode;
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
 * Controller 权限/审计单元测试。
 *
 * <p>验证两个核心安全行为：</p>
 * <ol>
 *   <li>{@code @PreAuthorize} 拒绝访问时抛出 {@link AccessDeniedException}，
 *       由 {@link GlobalExceptionHandler} 统一转换为 HTTP 403。</li>
 *   <li>{@link OperLogAspect} 在方法成功/失败时均记录审计日志，
 *       失败时（如权限拒绝）记录 status=1 + errorMsg。</li>
 * </ol>
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
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        operLogAspect = new OperLogAspect(sysOperLogService, objectMapper);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("AccessDeniedException 应被 GlobalExceptionHandler 转换为 403")
    void shouldReturn403WhenAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("无权限访问");

        Result<Void> result = globalExceptionHandler.handleAccessDeniedException(ex);

        assertEquals(ResultCode.FORBIDDEN.getCode(), result.getCode());
        assertEquals(ResultCode.FORBIDDEN.getMessage(), result.getMessage());
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
