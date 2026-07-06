package com.dp.plat.common.exception;

import com.dp.plat.common.result.Result;
import com.dp.plat.common.result.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 乐观锁冲突异常处理单元测试（SubTask 14.4）。
 *
 * <p>验证 {@link GlobalExceptionHandler#handleOptimisticLock} 正确处理
 * {@link OptimisticLockingFailureException} 并返回 HTTP 409 + 统一 Result（code=409）：</p>
 * <ul>
 *   <li>直接调用 handler 方法验证返回 code=409 + 提示消息</li>
 *   <li>MockMvc 端到端验证 HTTP 状态码 409 + JSON body code=409</li>
 *   <li>验证 ResultCode.CONFLICT 枚举值为 409</li>
 * </ul>
 */
class OptimisticLockExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(new OptimisticLockThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("OptimisticLockingFailureException 直接调用 handler 应返回 code=409")
    void shouldReturn409WhenOptimisticLockThrownDirectly() {
        OptimisticLockingFailureException ex =
                new OptimisticLockingFailureException("乐观锁冲突：更新影响 0 行");

        Result<Void> result = handler.handleOptimisticLock(ex);

        assertNotNull(result);
        assertEquals(ResultCode.CONFLICT.getCode(), result.getCode());
        assertEquals("数据已被其他用户修改，请刷新后重试", result.getMessage());
    }

    @Test
    @DisplayName("Controller 抛出 OptimisticLockingFailureException 应返回 HTTP 409")
    void shouldReturnHttpStatus409WhenControllerThrowsOptimisticLock() throws Exception {
        mockMvc.perform(get("/test/optimistic-lock"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ResultCode.CONFLICT.getCode()))
                .andExpect(jsonPath("$.message").value("数据已被其他用户修改，请刷新后重试"));
    }

    @Test
    @DisplayName("ResultCode.CONFLICT 应为 409")
    void resultCodeConflictShouldBe409() {
        assertEquals(409, ResultCode.CONFLICT.getCode());
        assertEquals("数据冲突", ResultCode.CONFLICT.getMessage());
    }

    @Test
    @DisplayName("@ResponseStatus 注解应映射到 HTTP 409 CONFLICT")
    void responseStatusAnnotationShouldMapTo409() {
        // 通过反射验证 handleOptimisticLock 方法的 @ResponseStatus 注解值
        try {
            java.lang.reflect.Method method = GlobalExceptionHandler.class
                    .getDeclaredMethod("handleOptimisticLock", OptimisticLockingFailureException.class);
            ResponseStatus rs = method.getAnnotation(ResponseStatus.class);
            assertNotNull(rs, "handleOptimisticLock 必须标注 @ResponseStatus");
            assertEquals(HttpStatus.CONFLICT, rs.value());
        } catch (NoSuchMethodException e) {
            throw new AssertionError("找不到 handleOptimisticLock 方法", e);
        }
    }

    /**
     * 测试用 Controller：模拟业务层抛出 {@link OptimisticLockingFailureException}。
     */
    @RestController
    static class OptimisticLockThrowingController {

        @GetMapping("/test/optimistic-lock")
        public String triggerOptimisticLock() {
            // 模拟 MyBatis-Plus OptimisticLockerInnerInterceptor 检测到 version 不匹配后
            // Service 层抛出的乐观锁冲突异常
            throw new OptimisticLockingFailureException("更新影响 0 行，version 不匹配");
        }
    }
}
