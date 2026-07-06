package com.dp.plat.common.retry;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 通用编程式重试服务，提供指数退避重试能力。
 *
 * <p>与 Resilience4j 声明式 {@code @Retry} 注解互补：
 * <ul>
 *   <li><b>声明式（注解）</b>：适用于方法级、配置驱动的重试场景，通过
 *       {@code @Retry(name="xxxRetry")} + {@code application.yml} 配置，
 *       由 AOP 代理自动织入重试逻辑。已用于 OA / D365 / FP 集成服务。</li>
 *   <li><b>编程式（本服务）</b>：适用于需要根据返回值 / 运行时上下文动态决定
 *       是否重试、或重试配置需在运行时计算的场景。调用方通过
 *       {@link #executeWithRetry} 显式传入 {@link RetryConfig}。</li>
 * </ul>
 *
 * <p><b>重试流程</b>：</p>
 * <ol>
 *   <li>执行 {@code action}，成功则记录指标后返回结果</li>
 *   <li>失败时检查 {@link RetryConfig#shouldRetry(Throwable)}：</li>
 *   <li>不应重试 → 直接抛出原异常</li>
 *   <li>应重试且未达上限 → 等待 {@code delayMs}（指数退避）后重试</li>
 *   <li>已达上限 → 记录失败指标并抛出包装异常</li>
 * </ol>
 *
 * <p><b>Micrometer 指标</b>：</p>
 * <ul>
 *   <li>{@code pms_retry_total{name=...,outcome=success}}：重试成功计数</li>
 *   <li>{@code pms_retry_total{name=...,outcome=retry}}：触发重试计数</li>
 *   <li>{@code pms_retry_total{name=...,outcome=exhausted}}：重试耗尽计数</li>
 *   <li>{@code pms_retry_attempts{name=...}}：每次调用的尝试次数分布（Timer）</li>
 * </ul>
 *
 * <p><b>线程安全</b>：无状态，Counter 缓存使用 {@link ConcurrentHashMap}，
 * 可被多线程并发调用。</p>
 *
 * @see RetryConfig
 */
@Slf4j
@Service
public class RetryService {

    /** 指标名：重试结果计数器。 */
    private static final String METRIC_RETRY_TOTAL = "pms_retry_total";

    /** 指标名：尝试次数分布。 */
    private static final String METRIC_RETRY_ATTEMPTS = "pms_retry_attempts";

    /** 指标 tag：操作名称。 */
    private static final String TAG_NAME = "name";

    /** 指标 tag：重试结果。 */
    private static final String TAG_OUTCOME = "outcome";

    /** 指标 tag 值：成功。 */
    private static final String OUTCOME_SUCCESS = "success";

    /** 指标 tag 值：触发重试。 */
    private static final String OUTCOME_RETRY = "retry";

    /** 指标 tag 值：重试耗尽。 */
    private static final String OUTCOME_EXHAUSTED = "exhausted";

    private final MeterRegistry meterRegistry;

    /** Counter 缓存：按 (name, outcome) 复用，避免重复注册。 */
    private final ConcurrentHashMap<String, Counter> counterCache = new ConcurrentHashMap<>();

    /**
     * 构造函数，由 Spring 自动注入 {@link MeterRegistry}。
     *
     * <p>当 pms-admin 模块引入 spring-boot-starter-actuator +
     * micrometer-registry-prometheus 时，运行时注入的是
     * PrometheusMeterRegistry。单元测试中可注入
     * {@link io.micrometer.core.instrument.simple.SimpleMeterRegistry}。</p>
     *
     * @param meterRegistry Micrometer 指标注册中心
     */
    public RetryService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * 使用默认配置执行带重试的操作。
     *
     * @param name   操作名称（用于日志与指标 tag）
     * @param action 待执行的操作
     * @param <T>    返回值类型
     * @return 操作返回值
     * @throws RuntimeException 重试耗尽后抛出最后一次异常的包装
     */
    public <T> T executeWithRetry(String name, Supplier<T> action) {
        return executeWithRetry(name, action, RetryConfig.defaultConfig());
    }

    /**
     * 使用自定义配置执行带重试的操作（指数退避）。
     *
     * <p>每次重试前等待 {@code delayMs}，下一次等待时间 = 上次 × {@code multiplier}，
     * 但不超过 {@code maxDelayMs}。等待期间调用 {@link Thread#sleep}，若被中断
     * 则恢复中断标志并抛出异常。</p>
     *
     * @param name   操作名称（用于日志与指标 tag）
     * @param action 待执行的操作
     * @param config 重试配置
     * @param <T>    返回值类型
     * @return 操作返回值
     * @throws RuntimeException 重试耗尽后抛出包装异常（cause 为最后一次异常）
     */
    public <T> T executeWithRetry(String name, Supplier<T> action, RetryConfig config) {
        int attempt = 0;
        // 初始延迟也受 maxDelayMs 上限约束，避免 initialDelayMs > maxDelayMs 时首次等待过长
        long delay = Math.min(config.getInitialDelayMs(), config.getMaxDelayMs());
        Exception lastException = null;
        long startTime = System.nanoTime();

        while (attempt < config.getMaxAttempts()) {
            attempt++;
            try {
                T result = action.get();
                recordSuccess(name, attempt);
                recordAttemptsDuration(name, attempt, System.nanoTime() - startTime);
                if (attempt > 1) {
                    log.info("操作重试成功 name={} attempt={}/{}", name, attempt, config.getMaxAttempts());
                }
                return result;
            } catch (Exception e) {
                lastException = e;
                // 检查该异常是否应触发重试
                if (!config.shouldRetry(e)) {
                    log.debug("操作异常不触发重试 name={} attempt={} exception={}",
                            name, attempt, e.getClass().getSimpleName());
                    throw e;
                }
                if (attempt >= config.getMaxAttempts()) {
                    recordExhausted(name, attempt);
                    recordAttemptsDuration(name, attempt, System.nanoTime() - startTime);
                    log.error("重试次数已耗尽 name={} attempt={}/{} err={}",
                            name, attempt, config.getMaxAttempts(), e.getMessage());
                    throw new RuntimeException(
                            "重试次数已耗尽 name=" + name + " attempts=" + attempt, e);
                }
                recordRetry(name, attempt);
                log.warn("操作失败，准备重试 name={} attempt={}/{} delayMs={} err={}",
                        name, attempt, config.getMaxAttempts(), delay, e.getMessage());
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试等待被中断 name=" + name, ie);
                }
                // 指数退避：delay = delay × multiplier，但不超过 maxDelayMs
                delay = Math.min((long) (delay * config.getMultiplier()), config.getMaxDelayMs());
            }
        }
        // 理论上不会到达此处（循环内已 return 或 throw）
        throw new RuntimeException("重试失败 name=" + name, lastException);
    }

    /**
     * 记录重试成功指标。
     *
     * @param name     操作名称
     * @param attempts 实际尝试次数（含首次）
     */
    private void recordSuccess(String name, int attempts) {
        counter(name, OUTCOME_SUCCESS).increment();
    }

    /**
     * 记录触发重试指标。
     *
     * @param name     操作名称
     * @param attempt  当前重试轮次
     */
    private void recordRetry(String name, int attempt) {
        counter(name, OUTCOME_RETRY).increment();
    }

    /**
     * 记录重试耗尽指标。
     *
     * @param name     操作名称
     * @param attempts 总尝试次数
     */
    private void recordExhausted(String name, int attempts) {
        counter(name, OUTCOME_EXHAUSTED).increment();
    }

    /**
     * 记录尝试次数分布（Timer，单位：次）。
     *
     * @param name        操作名称
     * @param attempts    实际尝试次数
     * @param durationNan 总耗时（纳秒）
     */
    private void recordAttemptsDuration(String name, int attempts, long durationNan) {
        if (meterRegistry == null) {
            return;
        }
        Timer.builder(METRIC_RETRY_ATTEMPTS)
                .description("重试操作尝试次数分布")
                .tag(TAG_NAME, name)
                .register(meterRegistry)
                .record(attempts, java.util.concurrent.TimeUnit.NANOSECONDS);
    }

    /**
     * 获取或创建指定 (name, outcome) 的 Counter。
     *
     * @param name    操作名称
     * @param outcome 结果标签
     * @return Counter 实例；当 meterRegistry 为 null 时返回 no-op Counter
     */
    private Counter counter(String name, String outcome) {
        if (meterRegistry == null) {
            return Counter.builder(METRIC_RETRY_TOTAL)
                    .tag(TAG_NAME, name)
                    .tag(TAG_OUTCOME, outcome)
                    .register(io.micrometer.core.instrument.Metrics.globalRegistry);
        }
        String key = name + ":" + outcome;
        return counterCache.computeIfAbsent(key, k -> Counter.builder(METRIC_RETRY_TOTAL)
                .description("编程式重试结果计数")
                .tag(TAG_NAME, name)
                .tag(TAG_OUTCOME, outcome)
                .register(meterRegistry));
    }
}
