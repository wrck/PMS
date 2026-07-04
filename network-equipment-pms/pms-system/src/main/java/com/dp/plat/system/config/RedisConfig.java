package com.dp.plat.system.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Redis 缓存配置：RedisTemplate 与 CacheManager。
 */
@Configuration
@EnableCaching
public class RedisConfig {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    private static final Duration JITTER_MAX = Duration.ofMinutes(5);
    private static final Duration NAMED_CACHE_TTL = Duration.ofMinutes(60);

    /**
     * RedisTemplate：key 使用 StringRedisSerializer，value 使用 GenericJackson2JsonRedisSerializer。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        StringRedisSerializer stringSerializer = StringRedisSerializer.UTF_8;

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * CacheManager：默认 TTL 30 分钟 + 0~5 分钟随机抖动防雪崩；
     * 命名缓存 sysDict、sysMenu、sysConfig、sysRole 各 60 分钟。
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Duration jitter = Duration.ofMillis(ThreadLocalRandom.current().nextLong(
                0L, JITTER_MAX.toMillis() + 1L));
        Duration defaultTtl = DEFAULT_TTL.plus(jitter);

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringSerializer = StringRedisSerializer.UTF_8;

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(defaultTtl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> initialCacheConfigs = new HashMap<>(4);
        RedisCacheConfiguration namedConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(NAMED_CACHE_TTL)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                .disableCachingNullValues();
        initialCacheConfigs.put("sysDict", namedConfig);
        initialCacheConfigs.put("sysMenu", namedConfig);
        initialCacheConfigs.put("sysConfig", namedConfig);
        initialCacheConfigs.put("sysRole", namedConfig);

        return RedisCacheManager.builder(
                        RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(initialCacheConfigs)
                .build();
    }
}
