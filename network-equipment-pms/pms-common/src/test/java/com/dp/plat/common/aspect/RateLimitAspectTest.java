package com.dp.plat.common.aspect;

import com.dp.plat.common.annotation.RateLimit;
import com.dp.plat.common.exception.RateLimitExceededException;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link RateLimitAspect} 单元测试：验证令牌桶消费逻辑、429 异常抛出与 Retry-After 计算。
 *
 * <p>使用 Mockito 5（inline mock maker，Spring Boot 3.2 默认）Mock Bucket4j 分布式链路：
 * {@link ProxyManager} → BucketProxyBuilder → {@link BucketProxy} → {@link ConsumptionProbe}。</p>
 *
 * <p>关键测试场景：</p>
 * <ul>
 *   <li>令牌桶有令牌 → 请求正常放行</li>
 *   <li>令牌桶耗尽 → 抛出 {@link RateLimitExceededException}（code=429 + retryAfter）</li>
 *   <li>SpEL Key 解析正常 → 请求放行</li>
 *   <li>Retry-After 向上取整且最小为 1 秒</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RateLimitAspectTest {

    /** 使用 RETURNS_DEEP_STUBS 简化 builder 链 Mock。 */
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProxyManager<byte[]> proxyManager;

    /** Mock 的 JoinPoint。 */
    @Mock
    private ProceedingJoinPoint joinPoint;

    /** Mock 的方法签名。 */
    @Mock
    private MethodSignature methodSignature;

    /** 被测切面。 */
    private RateLimitAspect aspect;

    /** 测试用方法（标注 @RateLimit，默认 Key）。 */
    private Method rateLimitedMethod;

    /** 测试用方法（标注 @RateLimit，SpEL Key = "#userId"）。 */
    private Method rateLimitedMethodWithSpel;

    @BeforeEach
    void setUp() throws Exception {
        aspect = new RateLimitAspect(proxyManager);

        rateLimitedMethod = TestService.class.getMethod("rateLimitedDefaultKey");
        rateLimitedMethodWithSpel = TestService.class.getMethod("rateLimitedWithSpelKey");

        // 通用 JoinPoint Mock
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(new TestService());
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
    }

    @Test
    @DisplayName("令牌桶有令牌时请求正常放行")
    void shouldAllowRequestWhenBucketHasTokens() throws Throwable {
        // Given: 令牌桶返回「消费成功」
        when(methodSignature.getMethod()).thenReturn(rateLimitedMethod);
        ConsumptionProbe consumedProbe = mockProbe(true, 99L, 0L);
        stubBucketResult(consumedProbe);
        when(joinPoint.proceed()).thenReturn("success");

        // When
        RateLimit rateLimit = rateLimitedMethod.getAnnotation(RateLimit.class);
        Object result = aspect.around(joinPoint, rateLimit);

        // Then
        assertEquals("success", result);
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("令牌桶耗尽时抛出 RateLimitExceededException（code=429）")
    void shouldThrowRateLimitExceededExceptionWhenBucketEmpty() throws Throwable {
        // Given: 令牌桶返回「消费失败」，需等待 5 秒补充
        when(methodSignature.getMethod()).thenReturn(rateLimitedMethod);
        ConsumptionProbe rejectedProbe = mockProbe(false, 0L, 5_000_000_000L);
        stubBucketResult(rejectedProbe);

        // When & Then
        RateLimit rateLimit = rateLimitedMethod.getAnnotation(RateLimit.class);
        RateLimitExceededException ex = assertThrows(RateLimitExceededException.class,
                () -> aspect.around(joinPoint, rateLimit));

        // 验证异常码 = 429 且 retryAfter = 5
        assertEquals(429, ex.getCode());
        assertTrue(ex.getRetryAfterSeconds() >= 1,
                "retryAfterSeconds 应至少为 1 秒");
        assertEquals(5L, ex.getRetryAfterSeconds(),
                "5 秒 refill 应映射为 retryAfter=5");
        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("SpEL Key 表达式解析不报错且请求正常放行")
    void shouldResolveSpelKeyAndAllowRequest() throws Throwable {
        // Given: SpEL Key = "#userId"
        when(methodSignature.getMethod()).thenReturn(rateLimitedMethodWithSpel);
        ConsumptionProbe consumedProbe = mockProbe(true, 9L, 0L);
        stubBucketResult(consumedProbe);
        when(joinPoint.proceed()).thenReturn("ok");

        // When
        RateLimit rateLimit = rateLimitedMethodWithSpel.getAnnotation(RateLimit.class);
        Object result = aspect.around(joinPoint, rateLimit);

        // Then
        assertEquals("ok", result);
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("Retry-After 秒数向上取整且最小为 1 秒")
    void shouldComputeRetryAfterCeilingMinOne() throws Throwable {
        // Given: nanosToWaitForRefill = 1.5 秒 → 向上取整为 2 秒
        when(methodSignature.getMethod()).thenReturn(rateLimitedMethod);
        ConsumptionProbe partialProbe = mockProbe(false, 0L, 1_500_000_000L);
        stubBucketResult(partialProbe);

        // When & Then
        RateLimit rateLimit = rateLimitedMethod.getAnnotation(RateLimit.class);
        RateLimitExceededException ex = assertThrows(RateLimitExceededException.class,
                () -> aspect.around(joinPoint, rateLimit));

        assertEquals(2L, ex.getRetryAfterSeconds(),
                "1.5 秒应向上取整为 2 秒");
    }

    // ==================== Helper Methods ====================

    /**
     * Mock Bucket4j 链路：{@code proxyManager.builder().build(key, supplier)
     * .tryConsumeAndReturnRemaining(1L)} → probe
     *
     * <p>依赖 {@code RETURNS_DEEP_STUBS} 自动 Mock 中间 builder 与 BucketProxy，
     * 仅 stub 最终的 {@link ConsumptionProbe} 返回值。</p>
     *
     * @param probe 令牌桶消费探测结果
     */
    @SuppressWarnings("unchecked")
    private void stubBucketResult(ConsumptionProbe probe) {
        when(proxyManager.builder()
                .build(any(), (Supplier<BucketConfiguration>) any())
                .tryConsumeAndReturnRemaining(anyLong()))
                .thenReturn(probe);
    }

    /**
     * 创建 Mock {@link ConsumptionProbe}（final 类，依赖 Mockito 5 inline mock maker）。
     *
     * @param consumed             是否消费成功
     * @param remainingTokens      剩余令牌数
     * @param nanosToWaitForRefill 等待补充的纳秒数
     * @return Mock 探测对象
     */
    private ConsumptionProbe mockProbe(boolean consumed, long remainingTokens,
                                       long nanosToWaitForRefill) {
        ConsumptionProbe probe = mock(ConsumptionProbe.class);
        when(probe.isConsumed()).thenReturn(consumed);
        when(probe.getRemainingTokens()).thenReturn(remainingTokens);
        when(probe.getNanosToWaitForRefill()).thenReturn(nanosToWaitForRefill);
        return probe;
    }

    /**
     * 测试服务类：提供标注 {@link RateLimit} 的方法供切面拦截。
     */
    public static class TestService {

        @RateLimit(capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
        public String rateLimitedDefaultKey() {
            return "success";
        }

        @RateLimit(key = "#userId", capacity = 10, refillTokens = 10, refillPeriodSeconds = 60)
        public String rateLimitedWithSpelKey() {
            return "ok";
        }
    }
}
