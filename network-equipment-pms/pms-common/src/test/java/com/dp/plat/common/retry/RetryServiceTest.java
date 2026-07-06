package com.dp.plat.common.retry;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 单元测试：验证 {@link RetryService} 通用编程式重试逻辑。
 *
 * <p>测试覆盖：
 * <ol>
 *   <li>测试1：首次成功（无重试）—— action 首次即返回，不触发重试</li>
 *   <li>测试2：失败后重试成功 —— 前 N 次失败，后续成功</li>
 *   <li>测试3：重试耗尽后抛异常 —— 所有尝试均失败</li>
 *   <li>测试4：指数退避延迟验证 —— delay = initialDelay × multiplier^n</li>
 *   <li>测试5：非可重试异常直接上抛 —— retryExceptions 不包含的异常不触发重试</li>
 *   <li>测试6：maxDelayMs 上限验证 —— 延迟不超过配置上限</li>
 * </ol>
 *
 * <p>使用 {@link SimpleMeterRegistry} 验证 Micrometer 指标记录。</p>
 */
class RetryServiceTest {

    private RetryService retryService;

    @BeforeEach
    void setUp() {
        retryService = new RetryService(new SimpleMeterRegistry());
    }

    @Test
    @DisplayName("测试1: 首次成功（无重试）—— action 首次即返回")
    void executeWithRetry_firstAttemptSuccess() {
        AtomicInteger callCount = new AtomicInteger(0);
        RetryConfig config = RetryConfig.builder()
                .maxAttempts(3)
                .initialDelayMs(10)
                .build();

        String result = retryService.executeWithRetry("testOp", () -> {
            callCount.incrementAndGet();
            return "OK";
        }, config);

        assertEquals("OK", result, "应返回 action 的结果");
        assertEquals(1, callCount.get(), "首次成功不应触发重试");
    }

    @Test
    @DisplayName("测试2: 失败后重试成功 —— 前 2 次失败，第 3 次成功")
    void executeWithRetry_failThenRetrySuccess() {
        AtomicInteger callCount = new AtomicInteger(0);
        RetryConfig config = RetryConfig.builder()
                .maxAttempts(3)
                .initialDelayMs(10)
                .multiplier(1.0)
                .build();

        String result = retryService.executeWithRetry("testOp", () -> {
            int attempt = callCount.incrementAndGet();
            if (attempt < 3) {
                throw new RuntimeException("transient failure " + attempt);
            }
            return "RECOVERED";
        }, config);

        assertEquals("RECOVERED", result, "重试后应返回成功结果");
        assertEquals(3, callCount.get(), "应尝试 3 次（2 次失败 + 1 次成功）");
    }

    @Test
    @DisplayName("测试3: 重试耗尽后抛异常 —— 所有尝试均失败")
    void executeWithRetry_retryExhausted_throwsException() {
        AtomicInteger callCount = new AtomicInteger(0);
        RetryConfig config = RetryConfig.builder()
                .maxAttempts(3)
                .initialDelayMs(10)
                .multiplier(1.0)
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                retryService.executeWithRetry("testOp", () -> {
                    callCount.incrementAndGet();
                    throw new RuntimeException("permanent failure");
                }, config));

        assertEquals(3, callCount.get(), "应尝试 3 次（全部失败）");
        assertTrue(ex.getMessage().contains("重试次数已耗尽"), "异常消息应包含「重试次数已耗尽」");
        assertTrue(ex.getCause() instanceof RuntimeException, "异常 cause 应为原始 RuntimeException");
        assertTrue(ex.getCause().getMessage().contains("permanent failure"),
                "cause 消息应包含原始异常信息");
    }

    @Test
    @DisplayName("测试4: 指数退避延迟验证 —— delay 逐次递增")
    void executeWithRetry_exponentialBackoff() {
        AtomicInteger callCount = new AtomicInteger(0);
        // initialDelayMs=50, multiplier=2.0 → 50ms, 100ms, 200ms ...
        // 但 maxAttempts=3，所以只有 2 次等待（50ms + 100ms = 150ms）
        RetryConfig config = RetryConfig.builder()
                .maxAttempts(3)
                .initialDelayMs(50)
                .multiplier(2.0)
                .maxDelayMs(10_000)
                .build();

        long startTime = System.currentTimeMillis();
        assertThrows(RuntimeException.class, () ->
                retryService.executeWithRetry("testOp", () -> {
                    callCount.incrementAndGet();
                    throw new RuntimeException("fail");
                }, config));
        long elapsed = System.currentTimeMillis() - startTime;

        assertEquals(3, callCount.get(), "应尝试 3 次");
        // 2 次重试等待：50ms + 100ms = 150ms（允许 ±50ms 容差）
        assertTrue(elapsed >= 100,
                "2 次指数退避等待应 ≥100ms，实际=" + elapsed + "ms");
    }

    @Test
    @DisplayName("测试5: 非可重试异常直接上抛 —— 不触发重试")
    void executeWithRetry_nonRetryableException_noRetry() {
        AtomicInteger callCount = new AtomicInteger(0);
        // 配置仅对 IllegalStateException 重试；抛出 IllegalArgumentException 时不重试
        RetryConfig config = RetryConfig.builder()
                .maxAttempts(3)
                .initialDelayMs(10)
                .retryExceptions(Set.of(IllegalStateException.class))
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                retryService.executeWithRetry("testOp", () -> {
                    callCount.incrementAndGet();
                    throw new IllegalArgumentException("non-retryable");
                }, config));

        assertEquals(1, callCount.get(), "非可重试异常不应触发重试");
        assertEquals("non-retryable", ex.getMessage(), "应直接抛出原始异常");
    }

    @Test
    @DisplayName("测试6: maxDelayMs 上限验证 —— 延迟不超过配置上限")
    void executeWithRetry_maxDelayCap() {
        AtomicInteger callCount = new AtomicInteger(0);
        // initialDelayMs=1000, multiplier=10.0, maxDelayMs=50
        // 第 1 次重试延迟 = min(1000, 50) = 50ms
        // 第 2 次重试延迟 = min(1000*10, 50) = 50ms
        RetryConfig config = RetryConfig.builder()
                .maxAttempts(3)
                .initialDelayMs(1000)
                .multiplier(10.0)
                .maxDelayMs(50)
                .build();

        long startTime = System.currentTimeMillis();
        assertThrows(RuntimeException.class, () ->
                retryService.executeWithRetry("testOp", () -> {
                    callCount.incrementAndGet();
                    throw new RuntimeException("fail");
                }, config));
        long elapsed = System.currentTimeMillis() - startTime;

        assertEquals(3, callCount.get(), "应尝试 3 次");
        // 2 次重试等待：50ms + 50ms = 100ms（允许 ±50ms 容差）
        assertTrue(elapsed < 500,
                "maxDelayMs 上限应使总等待 <500ms，实际=" + elapsed + "ms");
    }

    @Test
    @DisplayName("测试7: 默认配置验证 —— defaultConfig() 返回合理默认值")
    void defaultConfig_hasValidDefaults() {
        RetryConfig config = RetryConfig.defaultConfig();

        assertEquals(RetryConfig.DEFAULT_MAX_ATTEMPTS, config.getMaxAttempts());
        assertEquals(RetryConfig.DEFAULT_INITIAL_DELAY_MS, config.getInitialDelayMs());
        assertEquals(RetryConfig.DEFAULT_MULTIPLIER, config.getMultiplier());
        assertEquals(RetryConfig.DEFAULT_MAX_DELAY_MS, config.getMaxDelayMs());
    }

    @Test
    @DisplayName("测试8: RetryConfig.shouldRetry —— 异常类型匹配逻辑")
    void shouldRetry_exceptionTypeMatching() {
        RetryConfig config = RetryConfig.builder()
                .retryExceptions(Set.of(IllegalStateException.class))
                .build();

        assertTrue(config.shouldRetry(new IllegalStateException("match")),
                "配置的异常类型应触发重试");
        assertTrue(!config.shouldRetry(new IllegalArgumentException("no match")),
                "未配置的异常类型不应触发重试");

        // 默认配置（无 retryExceptions）：所有 RuntimeException 触发重试
        RetryConfig defaultConfig = RetryConfig.defaultConfig();
        assertTrue(defaultConfig.shouldRetry(new RuntimeException("any runtime")),
                "默认配置应重试所有 RuntimeException");
    }
}
