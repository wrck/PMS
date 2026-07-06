package com.dp.plat.integration.oauth;

import com.dp.plat.common.exception.IntegrationException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link RedisOAuthTokenCache} 单元测试。
 *
 * <p>使用 Mockito 模拟 {@link StringRedisTemplate} 与 {@link TokenRefreshLock}，
 * {@link SimpleMeterRegistry} 验证 Micrometer 指标。覆盖场景：</p>
 * <ul>
 *   <li>缓存命中：直接返回，不调用 Supplier</li>
 *   <li>缓存未命中：调用 Supplier 获取新 token 并缓存</li>
 *   <li>即将过期：触发刷新</li>
 *   <li>并发单飞：多线程仅 1 个调用 Supplier（防击穿）</li>
 *   <li>主动失效后重新获取</li>
 *   <li>Supplier 失败：递增失败计数 + Micrometer 指标</li>
 *   <li>getFailureCount 读取失败计数</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RedisOAuthTokenCacheTest {

    private static final String SYSTEM = "d365";
    private static final String CACHE_KEY = "oauth:token:d365";
    private static final String FAILCOUNT_KEY = "oauth:failcount:d365";

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private TokenRefreshLock refreshLock;

    @Mock
    private ObjectProvider<MeterRegistry> meterRegistryProvider;

    private SimpleMeterRegistry meterRegistry;

    private RedisOAuthTokenCache cache;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        when(meterRegistryProvider.getIfAvailable()).thenReturn(meterRegistry);
        cache = new RedisOAuthTokenCache(redisTemplate, refreshLock, meterRegistryProvider);
    }

    /**
     * 构建一个有效的缓存条目（过期时间在 1 小时后，不会触发提前刷新）。
     */
    private Map<Object, Object> validCacheEntry(String token) {
        long expiresAt = System.currentTimeMillis() / 1000 + 3600;
        Map<Object, Object> entries = new HashMap<>();
        entries.put("accessToken", token);
        entries.put("expiresAt", String.valueOf(expiresAt));
        entries.put("tokenType", "Bearer");
        return entries;
    }

    /**
     * 构建一个即将过期的缓存条目（过期时间在 100 秒后，小于 300 秒提前刷新阈值）。
     */
    private Map<Object, Object> expiringCacheEntry(String token) {
        long expiresAt = System.currentTimeMillis() / 1000 + 100;
        Map<Object, Object> entries = new HashMap<>();
        entries.put("accessToken", token);
        entries.put("expiresAt", String.valueOf(expiresAt));
        entries.put("tokenType", "Bearer");
        return entries;
    }

    @Test
    @DisplayName("缓存命中：直接返回缓存 token，不调用 Supplier")
    void getToken_cacheHit_returnsCachedWithoutSupplier() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries(CACHE_KEY)).thenReturn(validCacheEntry("cached-token"));

        Supplier<OAuthTokenCache.TokenInfo> supplier = MockitoSupplier.neverCalled();

        String token = cache.getToken(SYSTEM, supplier);

        assertEquals("cached-token", token);
        verify(refreshLock, never()).tryLock(anyString());
    }

    @Test
    @DisplayName("缓存未命中：调用 Supplier 获取新 token 并写入缓存")
    void getToken_cacheMiss_callsSupplierAndCaches() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        // 缓存为空（初始读 + 双重检查读都返回空）
        when(hashOps.entries(CACHE_KEY)).thenReturn(new HashMap<>());
        when(refreshLock.tryLock(SYSTEM)).thenReturn(true);
        doNothing().when(refreshLock).unlock(anyString());
        when(redisTemplate.delete(anyString())).thenReturn(true);

        Supplier<OAuthTokenCache.TokenInfo> supplier = () -> OAuthTokenCache.TokenInfo.builder()
                .accessToken("fresh-token")
                .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                .tokenType("Bearer")
                .build();

        String token = cache.getToken(SYSTEM, supplier);

        assertEquals("fresh-token", token);
        verify(hashOps).putAll(eq(CACHE_KEY), anyMap());
        verify(redisTemplate).expire(eq(CACHE_KEY), any(Duration.class));
    }

    @Test
    @DisplayName("缓存即将过期：距过期不足 5 分钟时触发刷新")
    void getToken_expiringSoon_triggersRefresh() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        // 第一次读：返回即将过期的 token → 触发刷新
        // 第二次读（双重检查）：返回空 → 确认需要刷新
        when(hashOps.entries(CACHE_KEY))
                .thenReturn(expiringCacheEntry("old-token"))
                .thenReturn(new HashMap<>());
        when(refreshLock.tryLock(SYSTEM)).thenReturn(true);
        doNothing().when(refreshLock).unlock(anyString());
        when(redisTemplate.delete(anyString())).thenReturn(true);

        AtomicInteger supplierCallCount = new AtomicInteger(0);
        Supplier<OAuthTokenCache.TokenInfo> supplier = () -> {
            supplierCallCount.incrementAndGet();
            return OAuthTokenCache.TokenInfo.builder()
                    .accessToken("refreshed-token")
                    .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                    .tokenType("Bearer")
                    .build();
        };

        String token = cache.getToken(SYSTEM, supplier);

        assertEquals("refreshed-token", token, "应返回刷新后的 token");
        assertEquals(1, supplierCallCount.get(), "Supplier 应被调用 1 次");
        verify(hashOps).putAll(eq(CACHE_KEY), anyMap());
    }

    @Test
    @DisplayName("并发请求只有 1 个调用 Supplier（单飞防击穿）")
    void getToken_concurrent_singleFlight() throws Exception {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            AtomicInteger supplierCallCount = new AtomicInteger(0);
            AtomicBoolean tokenWritten = new AtomicBoolean(false);
            AtomicBoolean lockHeld = new AtomicBoolean(false);

            // 缓存：tokenWritten 为 false 时返回空，为 true 时返回有效 token
            when(redisTemplate.opsForHash()).thenReturn(hashOps);
            when(hashOps.entries(CACHE_KEY)).thenAnswer(inv -> {
                if (tokenWritten.get()) {
                    return validCacheEntry("fresh-token");
                }
                return new HashMap<>();
            });

            // writeToCache → 标记 token 已写入
            doAnswer(inv -> {
                tokenWritten.set(true);
                return null;
            }).when(hashOps).putAll(eq(CACHE_KEY), anyMap());
            when(redisTemplate.expire(eq(CACHE_KEY), any(Duration.class))).thenReturn(true);

            // 锁：仅一个线程获取成功
            when(refreshLock.tryLock(SYSTEM)).thenAnswer(inv -> lockHeld.compareAndSet(false, true));
            doNothing().when(refreshLock).unlock(anyString());
            when(redisTemplate.delete(FAILCOUNT_KEY)).thenReturn(true);

            Supplier<OAuthTokenCache.TokenInfo> supplier = () -> {
                supplierCallCount.incrementAndGet();
                // 模拟 token 端点延迟，确保其他线程在等待
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return OAuthTokenCache.TokenInfo.builder()
                        .accessToken("fresh-token")
                        .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                        .tokenType("Bearer")
                        .build();
            };

            // 启动 5 个线程同时调用 getToken
            java.util.List<Future<String>> futures = new java.util.ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    readyLatch.countDown();
                    startLatch.await();
                    return cache.getToken(SYSTEM, supplier);
                }));
            }

            // 等待所有线程就绪后同时开始
            readyLatch.await(5, TimeUnit.SECONDS);
            startLatch.countDown();

            // 等待所有线程完成，验证都拿到同一个 token
            for (Future<String> future : futures) {
                String token = future.get(10, TimeUnit.SECONDS);
                assertEquals("fresh-token", token, "所有线程应拿到刷新后的 token");
            }

            assertEquals(1, supplierCallCount.get(),
                    "Supplier 应仅被调用 1 次（单飞防击穿）");
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    @DisplayName("invalidate：删除缓存键，下次 getToken 重新获取")
    void invalidate_clearsCache_thenRefetches() {
        when(redisTemplate.delete(CACHE_KEY)).thenReturn(true);

        // 1. 主动失效
        cache.invalidate(SYSTEM);
        verify(redisTemplate).delete(CACHE_KEY);

        // 2. 下次 getToken → 缓存未命中 → 调用 Supplier
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries(CACHE_KEY)).thenReturn(new HashMap<>());
        when(refreshLock.tryLock(SYSTEM)).thenReturn(true);
        doNothing().when(refreshLock).unlock(anyString());
        when(redisTemplate.delete(anyString())).thenReturn(true);

        Supplier<OAuthTokenCache.TokenInfo> supplier = () -> OAuthTokenCache.TokenInfo.builder()
                .accessToken("new-token")
                .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                .build();

        String token = cache.getToken(SYSTEM, supplier);

        assertEquals("new-token", token);
    }

    @Test
    @DisplayName("Supplier 失败：递增失败计数 + Micrometer 指标，异常重新抛出")
    void getToken_supplierThrows_incrementsFailureCounter() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries(CACHE_KEY)).thenReturn(new HashMap<>());
        when(refreshLock.tryLock(SYSTEM)).thenReturn(true);
        doNothing().when(refreshLock).unlock(anyString());

        // 失败计数器 increment 返回 1
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(FAILCOUNT_KEY)).thenReturn(1L);
        when(redisTemplate.expire(eq(FAILCOUNT_KEY), any(Duration.class))).thenReturn(true);

        Supplier<OAuthTokenCache.TokenInfo> supplier = () -> {
            throw new IntegrationException(SYSTEM, "token endpoint unavailable");
        };

        // 应抛出原始异常
        IntegrationException ex = assertThrows(IntegrationException.class,
                () -> cache.getToken(SYSTEM, supplier));
        assertEquals(SYSTEM, ex.getSystemName());

        // 验证失败计数器递增
        verify(valueOps).increment(FAILCOUNT_KEY);
        verify(redisTemplate).expire(eq(FAILCOUNT_KEY), any(Duration.class));

        // 验证 Micrometer 指标递增
        Counter failureCounter = meterRegistry.find("pms_oauth_failure_total")
                .tag("system", SYSTEM).counter();
        assertNotNull(failureCounter, "pms_oauth_failure_total 指标应已注册");
        assertEquals(1.0, failureCounter.count(), 0.001, "失败指标应递增 1 次");
    }

    @Test
    @DisplayName("getFailureCount：从 Redis 读取连续失败次数")
    void getFailureCount_readsFromRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(FAILCOUNT_KEY)).thenReturn("3");

        int count = cache.getFailureCount(SYSTEM);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("getFailureCount：无失败记录时返回 0")
    void getFailureCount_noRecord_returnsZero() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(FAILCOUNT_KEY)).thenReturn(null);

        int count = cache.getFailureCount(SYSTEM);

        assertEquals(0, count);
    }

    /**
     * 辅助类：生成一个「不应被调用」的 Supplier，若被调用则抛出 AssertionError。
     */
    private static final class MockitoSupplier {
        static Supplier<OAuthTokenCache.TokenInfo> neverCalled() {
            return () -> {
                throw new AssertionError("Supplier 不应被调用（缓存命中时直接返回）");
            };
        }
    }
}
