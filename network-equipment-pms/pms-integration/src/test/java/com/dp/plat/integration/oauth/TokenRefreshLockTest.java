package com.dp.plat.integration.oauth;

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
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TokenRefreshLock} 单元测试。
 *
 * <p>使用 Mockito 模拟 {@link StringRedisTemplate}，验证：</p>
 * <ul>
 *   <li>tryLock 在锁可用时返回 true 并设置正确 TTL</li>
 *   <li>tryLock 在锁被持有时返回 false（互斥）</li>
 *   <li>unlock 持有锁时通过 Lua 脚本释放并清理 ThreadLocal</li>
 *   <li>unlock 未持有锁时空操作</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenRefreshLockTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    private TokenRefreshLock lock;

    @BeforeEach
    void setUp() {
        lock = new TokenRefreshLock(redisTemplate);
    }

    @Test
    @DisplayName("tryLock: 锁可用时返回 true，使用 SETNX + 30s TTL")
    void tryLock_available_returnsTrueWithCorrectTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(
                eq("oauth:lock:d365"), anyString(), eq(Duration.ofSeconds(30))))
                .thenReturn(true);

        boolean result = lock.tryLock("d365");

        assertTrue(result, "锁可用时应返回 true");
        verify(valueOps).setIfAbsent(
                eq("oauth:lock:d365"), anyString(), eq(Duration.ofSeconds(30)));
    }

    @Test
    @DisplayName("tryLock: 锁已被持有时返回 false（互斥）")
    void tryLock_alreadyHeld_returnsFalse() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(
                anyString(), anyString(), any(Duration.class)))
                .thenReturn(false);

        boolean result = lock.tryLock("d365");

        assertFalse(result, "锁已被持有时应返回 false");
    }

    @Test
    @DisplayName("tryLock: 锁键包含系统名前缀 oauth:lock:")
    void tryLock_usesCorrectKeyPrefix() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        lock.tryLock("fp");

        verify(valueOps).setIfAbsent(
                eq("oauth:lock:fp"), anyString(), eq(Duration.ofSeconds(30)));
    }

    @Test
    @DisplayName("unlock: 持有锁时执行 Lua 脚本释放并清理 ThreadLocal")
    @SuppressWarnings("unchecked")
    void unlock_holdingLock_executesScriptAndCleansThreadLocal() {
        // 1. 先获取锁
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any()))
                .thenReturn(1L);

        lock.tryLock("d365");

        // 2. 释放锁
        lock.unlock("d365");

        // 3. 验证 Lua 脚本被执行
        verify(redisTemplate, times(1)).execute(
                any(RedisScript.class), anyList(), any());

        // 4. 再次 unlock 应为空操作（ThreadLocal 已清理）
        lock.unlock("d365");
        verify(redisTemplate, times(1)).execute(
                any(RedisScript.class), anyList(), any());
    }

    @Test
    @DisplayName("unlock: 未持有锁时空操作（不调用 Redis）")
    void unlock_notHoldingLock_isNoOp() {
        // 未调用 tryLock，ThreadLocal 为空
        lock.unlock("d365");

        // 验证未执行任何 Redis 操作
        verify(redisTemplate, never()).execute(
                any(RedisScript.class), anyList(), any());
    }

    @Test
    @DisplayName("unlock: Redis 异常时不抛出（锁会自动过期），ThreadLocal 仍被清理")
    @SuppressWarnings("unchecked")
    void unlock_redisException_doesNotThrow() {
        // 获取锁
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any()))
                .thenThrow(new RuntimeException("Redis connection lost"));

        lock.tryLock("d365");

        // unlock 不应抛出异常（锁有 TTL 会自动过期）
        lock.unlock("d365");

        // ThreadLocal 应已清理：再次 unlock 为空操作
        lock.unlock("d365");
        verify(redisTemplate, times(1)).execute(
                any(RedisScript.class), anyList(), any());
    }
}
