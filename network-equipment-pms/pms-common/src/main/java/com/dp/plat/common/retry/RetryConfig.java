package com.dp.plat.common.retry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 编程式重试配置，由 {@link RetryService#executeWithRetry} 使用。
 *
 * <p>与 Resilience4j 声明式 {@code @Retry} 注解互补：当业务逻辑需要在代码中
 * 精确控制重试行为（如根据返回值决定是否重试、动态调整重试次数）时，使用
 * {@link RetryService} + 本配置类，而非注解。</p>
 *
 * <p><b>字段语义</b>：</p>
 * <ul>
 *   <li>{@code maxAttempts}：最大尝试次数（含首次调用），例如 3 = 首次 + 2 次重试</li>
 *   <li>{@code initialDelayMs}：首次重试前等待毫秒数</li>
 *   <li>{@code multiplier}：退避乘数，每次重试延迟 = 上次延迟 × multiplier</li>
 *   <li>{@code maxDelayMs}：单次重试延迟上限，避免指数退避导致过长等待</li>
 *   <li>{@code retryExceptions}：触发重试的异常类型集合；为空时所有
 *       {@link RuntimeException} 均触发重试</li>
 * </ul>
 *
 * <p>典型用法：</p>
 * <pre>{@code
 * RetryConfig config = RetryConfig.builder()
 *         .maxAttempts(3)
 *         .initialDelayMs(1000)
 *         .multiplier(2.0)
 *         .maxDelayMs(10000)
 *         .retryExceptions(Set.of(TimeoutException.class, IOException.class))
 *         .build();
 * String result = retryService.executeWithRetry("queryStock", () -> callApi(), config);
 * }</pre>
 *
 * @see RetryService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryConfig {

    /** 默认最大尝试次数（含首次调用）。 */
    public static final int DEFAULT_MAX_ATTEMPTS = 3;

    /** 默认首次重试延迟（毫秒）。 */
    public static final long DEFAULT_INITIAL_DELAY_MS = 1000L;

    /** 默认退避乘数。 */
    public static final double DEFAULT_MULTIPLIER = 2.0;

    /** 默认单次重试延迟上限（毫秒，10 秒）。 */
    public static final long DEFAULT_MAX_DELAY_MS = 10_000L;

    /**
     * 最大尝试次数（含首次调用）。
     * <p>例如 {@code maxAttempts=3} 表示首次调用失败后最多重试 2 次。</p>
     */
    @Builder.Default
    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

    /**
     * 首次重试前等待的毫秒数。
     */
    @Builder.Default
    private long initialDelayMs = DEFAULT_INITIAL_DELAY_MS;

    /**
     * 退避乘数：每次重试延迟 = 上次延迟 × multiplier。
     * <p>例如 {@code initialDelayMs=1000, multiplier=2.0} → 1s, 2s, 4s, 8s ...</p>
     */
    @Builder.Default
    private double multiplier = DEFAULT_MULTIPLIER;

    /**
     * 单次重试延迟上限（毫秒），避免指数退避导致过长等待。
     * <p>计算出的延迟超过此值时，使用此值作为实际延迟。</p>
     */
    @Builder.Default
    private long maxDelayMs = DEFAULT_MAX_DELAY_MS;

    /**
     * 触发重试的异常类型集合。
     * <p>为 {@code null} 或空时，所有 {@link RuntimeException} 均触发重试；
     * 非空时，仅集合中声明的异常类型（含子类）触发重试，其余异常直接上抛。</p>
     */
    private Set<Class<? extends Throwable>> retryExceptions;

    /**
     * 创建一个使用默认配置的 RetryConfig 实例。
     *
     * @return 默认配置（3 次尝试，1s 初始延迟，2.0 乘数，10s 上限）
     */
    public static RetryConfig defaultConfig() {
        return RetryConfig.builder().build();
    }

    /**
     * 判断给定异常是否应触发重试。
     *
     * @param throwable 异常实例
     * @return {@code true} 表示应重试；{@code false} 表示应直接上抛
     */
    public boolean shouldRetry(Throwable throwable) {
        if (retryExceptions == null || retryExceptions.isEmpty()) {
            // 未配置特定异常类型时，所有 RuntimeException 触发重试
            return throwable instanceof RuntimeException;
        }
        for (Class<? extends Throwable> retryType : retryExceptions) {
            if (retryType.isInstance(throwable)) {
                return true;
            }
        }
        return false;
    }
}
