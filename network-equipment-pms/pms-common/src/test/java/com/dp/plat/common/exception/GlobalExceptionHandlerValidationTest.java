package com.dp.plat.common.exception;

import com.dp.plat.common.result.Result;
import com.dp.plat.common.result.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link GlobalExceptionHandler} 参数校验单元测试。
 *
 * <p>验证非法入参时返回 HTTP 400 + 统一 Result（code=400）：</p>
 * <ul>
 *   <li>{@code @Valid @RequestBody} 校验失败 → {@link org.springframework.web.bind.MethodArgumentNotValidException} → 400</li>
 *   <li>{@code @Size} 约束违反 → 400</li>
 *   <li>{@link ConstraintViolationException} 直接调用 handler → 400</li>
 *   <li>{@link org.springframework.security.access.AccessDeniedException} → 403</li>
 * </ul>
 */
class GlobalExceptionHandlerValidationTest {

    private MockMvc mockMvc;
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("@Valid @RequestBody @NotBlank 校验失败应返回 400")
    void shouldReturn400WhenNotBlankValidationFails() throws Exception {
        mockMvc.perform(post("/test/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    @DisplayName("@Valid @RequestBody @Size 校验失败应返回 400")
    void shouldReturn400WhenSizeValidationFails() throws Exception {
        mockMvc.perform(post("/test/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ab\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.PARAM_ERROR.getCode()));
    }

    @Test
    @DisplayName("ConstraintViolationException 应返回 400 + 错误消息")
    void shouldReturn400WhenConstraintViolationThrown() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<?> violation = org.mockito.Mockito.mock(ConstraintViolation.class);
        org.mockito.Mockito.when(violation.getMessage()).thenReturn("关键字不能为空");
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        Result<Void> result = handler.handleConstraintViolationException(ex);

        assertEquals(ResultCode.PARAM_ERROR.getCode(), result.getCode());
        assertTrue(result.getMessage().contains("关键字不能为空"));
    }

    @Test
    @DisplayName("AccessDeniedException 应返回 403")
    void shouldReturn403WhenAccessDenied() {
        Result<Void> result = handler.handleAccessDeniedException(
                new org.springframework.security.access.AccessDeniedException("forbidden"));

        assertEquals(ResultCode.FORBIDDEN.getCode(), result.getCode());
    }

    /**
     * 测试用 Controller。
     */
    @RestController
    static class TestController {

        @PostMapping("/test/create")
        public String create(@Valid @RequestBody TestDto dto) {
            return "ok";
        }
    }

    @Data
    static class TestDto {

        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 个字符之间")
        private String username;
    }
}
