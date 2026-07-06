package com.dp.plat.common.aspect;

import com.dp.plat.common.annotation.Idempotent;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link IdempotentAspect} 单元测试：验证 Redis SETNX 幂等保护逻辑。
 *
 * <p>使用 Mockito Mock {@link ValueOperations}（接口，可被 inline mock maker 处理），
 * 配合手动实现的 {@link TestableStringRedisTemplate} 测试桩（避免 Mockito 在
 * Java 25+ 上无法 instrument {@link StringRedisTemplate} 具体类的问题）。
 * 通过 {@link MockHttpServletRequest} + {@link RequestContextHolder} 模拟
 * HTTP 请求上下文（提供 {@code X-Idempotent-Key} 请求头）。</p>
 *
 * <p>关键测试场景：</p>
 * <ul>
 *   <li>首次请求：SETNX 成功 → 业务方法执行 → 返回原结果</li>
 *   <li>重复请求（REJECT）：SETNX 失败 → 抛出 {@link BusinessException}（code=409）</li>
 *   <li>重复请求（RETURN_FIRST_RESULT）：SETNX 失败 → 反序列化 Redis 中首次结果返回</li>
 *   <li>业务异常：SETNX 成功 → 业务方法抛异常 → 删除 Redis key 允许重试</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IdempotentAspectTest {

    /** Mock 的 Redis ValueOperations（接口，可被 Mockito 正常 mock）。 */
    @Mock
    private ValueOperations<String, String> valueOperations;

    /** Mock 的 JoinPoint。 */
    @Mock
    private ProceedingJoinPoint joinPoint;

    /** Mock 的方法签名。 */
    @Mock
    private MethodSignature methodSignature;

    /**
     * 真实的 ObjectMapper（非 Mock，保证序列化/反序列化真实可用）。
     * 配置 {@code FAIL_ON_UNKNOWN_PROPERTIES=false} 以匹配 Spring Boot 自动配置的
     * ObjectMapper 默认行为（Result 类的 {@code isSuccess()} 会被序列化为
     * {@code "success"} 字段，反序列化时需忽略该未知字段）。
     */
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /** 可测试的 StringRedisTemplate 桩（绕过 Mockito 对具体类的 mock 限制）。 */
    private TestableStringRedisTemplate redisTemplate;

    /** 被测切面。 */
    private IdempotentAspect aspect;

    /** 测试用方法（默认 key 从请求头读取，REJECT 策略）。 */
    private Method rejectMethod;

    /** 测试用方法（RETURN_FIRST_RESULT 策略）。 */
    private Method returnFirstResultMethod;

    @BeforeEach
    void setUp() throws Exception {
        redisTemplate = new TestableStringRedisTemplate(valueOperations);
        aspect = new IdempotentAspect(redisTemplate, objectMapper);

        rejectMethod = TestService.class.getMethod("rejectAction");
        returnFirstResultMethod = TestService.class.getMethod("returnFirstResultAction");

        // 通用 Mock：JoinPoint 与 Signature
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(new TestService());
        when(joinPoint.getArgs()).thenReturn(new Object[0]);

        // 默认设置 RequestContextHolder（带 X-Idempotent-Key 头）
        setUpRequestContext("test-idempotent-key-001");
    }

    @AfterEach
    void tearDown() {
        // 清理 RequestContextHolder，避免测试间污染
        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * 设置 RequestContextHolder，携带指定的 X-Idempotent-Key 头。
     */
    private void setUpRequestContext(String idempotentKey) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (idempotentKey != null) {
            request.addHeader("X-Idempotent-Key", idempotentKey);
        }
        RequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);
    }

    @Test
    @DisplayName("首次请求：SETNX 成功 → 业务方法执行 → 返回原结果")
    void shouldAllowFirstRequestAndProceed() throws Throwable {
        // Given: SETNX 返回 true（首次抢占成功）
        when(methodSignature.getMethod()).thenReturn(rejectMethod);
        when(valueOperations.setIfAbsent(anyString(), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(true);
        when(joinPoint.proceed()).thenReturn(Result.ok("success"));

        // When
        Idempotent idempotent = rejectMethod.getAnnotation(Idempotent.class);
        Object result = aspect.around(joinPoint, idempotent);

        // Then: 业务方法被执行，原结果返回
        verify(joinPoint, times(1)).proceed();
        assertTrue(result instanceof Result);
        assertEquals("success", ((Result<?>) result).getData());

        // REJECT 策略不更新 Redis 值（保持 PROCESSING 标记）
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("重复请求（REJECT 策略）：SETNX 失败 → 抛出 BusinessException（code=409）")
    void shouldThrowBusinessExceptionOnDuplicateReject() throws Throwable {
        // Given: SETNX 返回 false（重复请求），Redis 中已有 PROCESSING 标记
        when(methodSignature.getMethod()).thenReturn(rejectMethod);
        when(valueOperations.setIfAbsent(anyString(), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(false);
        when(valueOperations.get(anyString())).thenReturn("PROCESSING");

        // When & Then: 抛出 BusinessException，code=409
        Idempotent idempotent = rejectMethod.getAnnotation(Idempotent.class);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> aspect.around(joinPoint, idempotent));

        assertEquals(409, ex.getCode());
        assertEquals("请勿重复提交", ex.getMessage());
        // 业务方法不应执行
        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("重复请求（RETURN_FIRST_RESULT 策略）：从 Redis 反序列化返回首次结果")
    void shouldReturnFirstResultOnDuplicateReturnFirstResult() throws Throwable {
        // Given: SETNX 返回 false，Redis 中已存首次结果 JSON
        when(methodSignature.getMethod()).thenReturn(returnFirstResultMethod);
        // 关键：stub getReturnType() 供 Jackson 反序列化使用
        when(methodSignature.getReturnType()).thenReturn((Class) Result.class);
        when(valueOperations.setIfAbsent(anyString(), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(false);
        // 模拟 Redis 中已存的首次结果（Result<String> JSON）
        String firstResultJson = objectMapper.writeValueAsString(Result.ok("first-result-data"));
        when(valueOperations.get(anyString())).thenReturn(firstResultJson);

        // When
        Idempotent idempotent = returnFirstResultMethod.getAnnotation(Idempotent.class);
        Object result = aspect.around(joinPoint, idempotent);

        // Then: 业务方法不执行，返回反序列化的首次结果
        verify(joinPoint, never()).proceed();
        assertTrue(result instanceof Result);
        assertEquals("first-result-data", ((Result<?>) result).getData());
    }

    @Test
    @DisplayName("业务方法异常：删除 Redis key 允许重试")
    void shouldDeleteKeyWhenBusinessMethodThrows() throws Throwable {
        // Given: SETNX 成功，但业务方法抛出异常
        when(methodSignature.getMethod()).thenReturn(rejectMethod);
        when(valueOperations.setIfAbsent(anyString(), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(true);
        RuntimeException businessError = new RuntimeException("业务异常");
        when(joinPoint.proceed()).thenThrow(businessError);

        // When & Then: 异常向上抛出，且 Redis key 被删除
        Idempotent idempotent = rejectMethod.getAnnotation(Idempotent.class);
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> aspect.around(joinPoint, idempotent));

        assertEquals("业务异常", thrown.getMessage());
        // 验证 delete 被调用（允许客户端重试）
        assertEquals(1, redisTemplate.getDeleteCount(),
                "业务异常后应删除幂等键以允许重试");
    }

    @Test
    @DisplayName("未提供 X-Idempotent-Key 头：跳过幂等校验，直接执行业务方法")
    void shouldSkipIdempotencyWhenKeyAbsent() throws Throwable {
        // Given: 请求未携带 X-Idempotent-Key 头
        RequestContextHolder.resetRequestAttributes();
        setUpRequestContext(null);
        when(methodSignature.getMethod()).thenReturn(rejectMethod);
        when(joinPoint.proceed()).thenReturn(Result.ok("skipped"));

        // When
        Idempotent idempotent = rejectMethod.getAnnotation(Idempotent.class);
        Object result = aspect.around(joinPoint, idempotent);

        // Then: 直接执行业务方法，不调用 Redis
        assertEquals(0, redisTemplate.getOpsForValueCount(),
                "无幂等键时不应调用 Redis opsForValue");
        verify(joinPoint, times(1)).proceed();
        assertEquals("skipped", ((Result<?>) result).getData());
    }

    @Test
    @DisplayName("RETURN_FIRST_RESULT 策略首次请求：成功后将结果序列化存入 Redis")
    void shouldCacheFirstResultForReturnFirstResultPolicy() throws Throwable {
        // Given: SETNX 成功，业务方法返回结果
        when(methodSignature.getMethod()).thenReturn(returnFirstResultMethod);
        when(valueOperations.setIfAbsent(anyString(), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(true);
        when(joinPoint.proceed()).thenReturn(Result.ok("cached-data"));

        // When
        Idempotent idempotent = returnFirstResultMethod.getAnnotation(Idempotent.class);
        Object result = aspect.around(joinPoint, idempotent);

        // Then: 业务方法执行，且首次结果被写入 Redis（覆盖 PROCESSING 标记）
        verify(joinPoint, times(1)).proceed();
        verify(valueOperations, times(1)).set(anyString(), anyString(), any(Duration.class));
        assertEquals("cached-data", ((Result<?>) result).getData());
    }

    @Test
    @DisplayName("RETURN_FIRST_RESULT 策略下读到 PROCESSING 标记：降级为 REJECT")
    void shouldRejectWhenReadingProcessingMarkerUnderReturnFirstResult() throws Throwable {
        // Given: SETNX 失败，Redis 中仍是 PROCESSING（首次请求未完成）
        when(methodSignature.getMethod()).thenReturn(returnFirstResultMethod);
        when(valueOperations.setIfAbsent(anyString(), eq("PROCESSING"), any(Duration.class)))
                .thenReturn(false);
        when(valueOperations.get(anyString())).thenReturn("PROCESSING");

        // When & Then: 降级为 REJECT，抛出 BusinessException
        Idempotent idempotent = returnFirstResultMethod.getAnnotation(Idempotent.class);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> aspect.around(joinPoint, idempotent));

        assertEquals(409, ex.getCode());
        verify(joinPoint, never()).proceed();
    }

    /**
     * 测试服务类：提供标注 {@link Idempotent} 的方法供切面拦截。
     */
    public static class TestService {

        @Idempotent
        public Result<String> rejectAction() {
            return Result.ok("reject");
        }

        @Idempotent(policy = Idempotent.Policy.RETURN_FIRST_RESULT, ttl = 120)
        public Result<String> returnFirstResultAction() {
            return Result.ok("first");
        }
    }

    /**
     * 可测试的 {@link StringRedisTemplate} 桩：绕过 Mockito 在 Java 25+
     * 上无法 mock StringRedisTemplate 具体类的限制。
     *
     * <p>重写 {@link #opsForValue()} 返回注入的 Mock {@link ValueOperations}，
     * 重写 {@link #delete(Object)} 累计调用次数，便于断言「业务异常后删除 key」。</p>
     */
    static class TestableStringRedisTemplate extends StringRedisTemplate {

        private final ValueOperations<String, String> valueOps;
        private final AtomicInteger opsForValueCount = new AtomicInteger(0);
        private final AtomicInteger deleteCount = new AtomicInteger(0);

        TestableStringRedisTemplate(ValueOperations<String, String> valueOps) {
            // 不调用 super(connectionFactory)，避免依赖真实 Redis 连接
            // 注意：这样实例化的对象不能用于真实 Redis 操作，仅适用于单元测试桩
            this.valueOps = valueOps;
        }

        @Override
        public ValueOperations<String, String> opsForValue() {
            opsForValueCount.incrementAndGet();
            return valueOps;
        }

        @Override
        public Boolean delete(String key) {
            deleteCount.incrementAndGet();
            return true;
        }

        int getOpsForValueCount() {
            return opsForValueCount.get();
        }

        int getDeleteCount() {
            return deleteCount.get();
        }
    }
}
