package com.dp.plat.common.exception;

import com.dp.plat.common.result.Result;
import com.dp.plat.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler that converts exceptions into unified {@link Result} responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Retry-After HTTP 响应头名称（RFC 7231）。 */
    private static final String HEADER_RETRY_AFTER = "Retry-After";

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("Business exception on {}: {}", request.getRequestURI(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理集成服务异常（D365 / FP / OA）：返回 HTTP 503。
     *
     * <p>{@link IntegrationException} 通常由 Resilience4j 熔断器 OPEN 后的
     * fallback 方法抛出，或由集成服务内部捕获 IOException / TimeoutException
     * 后包装抛出。{@code systemName} 字段标识是哪个外部系统，便于按系统聚合
     * 告警与统计失败率。</p>
     *
     * @param ex 集成异常
     * @return 统一 Result（code=503）
     */
    @ExceptionHandler(IntegrationException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Result<Void> handleIntegrationException(IntegrationException ex) {
        log.error("集成异常 system={} msg={}", ex.getSystemName(), ex.getMessage(), ex);
        return Result.fail(ResultCode.INTEGRATION_FAILURE.getCode(), ex.getMessage());
    }

    /**
     * 处理限流超限异常：返回 HTTP 429 + {@code Retry-After} 响应头。
     *
     * <p>{@link RateLimitExceededException} 携带的 {@code retryAfterSeconds}
     * 写入 {@code Retry-After} 头（单位：秒），便于客户端按建议间隔重试。</p>
     *
     * @param e        限流异常
     * @param request  HTTP 请求
     * @param response HTTP 响应（用于写入响应头）
     * @return 统一 Result（code=429）
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public Result<Void> handleRateLimitExceededException(RateLimitExceededException e,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        long retryAfter = e.getRetryAfterSeconds();
        response.setIntHeader(HEADER_RETRY_AFTER, (int) retryAfter);
        // 直接设置 HTTP 状态码为 429（Servlet 6.0 无 SC_TOO_MANY_REQUESTS 常量）
        response.setStatus(429);
        log.warn("Rate limit exceeded on {}: {}, retryAfter={}s",
                request.getRequestURI(), e.getMessage(), retryAfter);
        return Result.fail(ResultCode.TOO_MANY_REQUESTS.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Parameter validation failed: {}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Parameter bind failed: {}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理 {@link ConstraintViolationException}：JSR-380 在方法参数/返回值
     * 级别（{@code @Validated} 标注的 Controller 类 + {@code @RequestParam}/{@code @PathVariable}
     * 上的 {@code @NotNull}/{@code @Size} 等约束）校验失败时抛出，统一返回 400。
     *
     * @param e 约束违反异常
     * @return 统一 Result（code=400）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("Constraint violation: {}", message);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return Result.fail(ResultCode.FORBIDDEN);
    }

    /**
     * 处理乐观锁冲突异常：返回 HTTP 409。
     *
     * <p>当 MyBatis-Plus {@code OptimisticLockerInnerInterceptor} 检测到
     * {@code version} 不匹配（更新影响行数为 0）时，由 Service 层抛出
     * {@link OptimisticLockingFailureException}。此 handler 统一返回
     * code=409 并提示用户刷新后重试。</p>
     *
     * @param e 乐观锁冲突异常
     * @return 统一 Result（code=409）
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleOptimisticLock(OptimisticLockingFailureException e) {
        log.warn("乐观锁冲突: {}", e.getMessage());
        return Result.fail(ResultCode.CONFLICT.getCode(), "数据已被其他用户修改，请刷新后重试");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported: {}", e.getMessage());
        return Result.fail(ResultCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception on {}: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }
}
