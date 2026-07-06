package com.dp.plat.common.exception;

import com.dp.plat.common.result.ResultCode;

import java.io.Serial;

/**
 * 限流超限异常：由 {@code RateLimitAspect} 在令牌桶消费失败时抛出，
 * 由 {@code GlobalExceptionHandler} 统一转换为 HTTP 429 + {@code Retry-After} 响应头。
 *
 * <p>继承 {@link BusinessException} 以保留统一 Result 结构，
 * 同时携带 {@link #retryAfterSeconds} 供响应头写入。</p>
 */
public class RateLimitExceededException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 建议客户端重试等待的秒数（写入 Retry-After 响应头）。 */
    private final long retryAfterSeconds;

    /**
     * 构造限流异常。
     *
     * @param message           返回给前端的提示消息
     * @param retryAfterSeconds 建议重试等待秒数（≥1）
     */
    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(ResultCode.TOO_MANY_REQUESTS, message);
        this.retryAfterSeconds = Math.max(1L, retryAfterSeconds);
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
