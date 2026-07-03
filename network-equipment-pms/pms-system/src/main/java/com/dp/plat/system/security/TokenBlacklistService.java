package com.dp.plat.system.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Maintains a Redis-backed blacklist of JWT ids (jti) so that tokens can be
 * revoked before their natural expiration (e.g. on user logout).
 *
 * <p>Each blacklisted jti is stored under key {@code token:blacklist:{jti}} with
 * a TTL equal to the remaining lifetime of the corresponding token, so the
 * entry is auto-cleaned once the token would have expired anyway.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    /** Redis key prefix for blacklisted tokens. */
    public static final String KEY_PREFIX = "token:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Add a JWT id to the blacklist with the given remaining TTL (ms).
     *
     * @param jti              the JWT id
     * @param remainingMillis  the remaining lifetime of the token in ms
     */
    public void blacklist(String jti, long remainingMillis) {
        if (jti == null || jti.isBlank() || remainingMillis <= 0) {
            return;
        }
        String key = KEY_PREFIX + jti;
        stringRedisTemplate.opsForValue().set(key, "1", remainingMillis, TimeUnit.MILLISECONDS);
        log.debug("Blacklisted JWT jti={} for {} ms", jti, remainingMillis);
    }

    /**
     * Check whether the given JWT id is in the blacklist.
     */
    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }
        Boolean exists = stringRedisTemplate.hasKey(KEY_PREFIX + jti);
        return Boolean.TRUE.equals(exists);
    }
}
