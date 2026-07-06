package com.dp.plat.common.exception;

import com.dp.plat.common.result.ResultCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 集成异常：外部系统（D365 / FP / OA）调用失败时抛出，例如：
 *
 * <ul>
 *   <li>Resilience4j 熔断器 OPEN 时，{@code @CircuitBreaker(fallbackMethod = ...)}
 *       的 fallback 方法包装本异常向上抛出，由 {@link GlobalExceptionHandler}
 *       统一转为 HTTP 503 响应。</li>
 *   <li>调用超时 / 连接失败 / OAuth2 token 获取失败等可重试场景。</li>
 *   <li>调用响应非预期，需要业务侧感知并触发补偿。</li>
 * </ul>
 *
 * <p>携带 {@code systemName}（{@code d365} / {@code fp} / {@code oa}）便于
 * 告警与日志按系统集成维度聚合统计。</p>
 */
@Getter
public class IntegrationException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 触发异常的外部系统标识：{@code d365} / {@code fp} / {@code oa}。 */
    private final String systemName;

    public IntegrationException(String systemName, String message) {
        super(ResultCode.INTEGRATION_FAILURE.getCode(), message);
        this.systemName = systemName;
    }

    public IntegrationException(String systemName, String message, Throwable cause) {
        super(ResultCode.INTEGRATION_FAILURE.getCode(), message);
        this.systemName = systemName;
        super.initCause(cause);
    }
}
