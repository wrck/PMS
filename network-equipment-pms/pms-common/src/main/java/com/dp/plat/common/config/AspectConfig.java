package com.dp.plat.common.config;

import com.dp.plat.common.aspect.IdempotentAspect;
import com.dp.plat.common.aspect.RateLimitAspect;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/**
 * AOP 配置：启用 AspectJ 自动代理 + 注册限流/幂等相关 Bean。
 *
 * <p>功能：</p>
 * <ul>
 *   <li>{@link EnableAspectJAutoProxy} 启用 AspectJ 注解驱动（暴露代理对象，支持同类方法互调）</li>
 *   <li>注册 {@link RateLimitAspect} 限流切面 Bean</li>
 *   <li>注册 {@link IdempotentAspect} 幂等切面 Bean（依赖 {@link StringRedisTemplate} + {@link ObjectMapper}）</li>
 *   <li>注册 Bucket4j {@link ProxyManager}（基于 Lettuce + Redis 分布式存储）</li>
 * </ul>
 *
 * <p>当 Classpath 不存在 Lettuce / Redis 时，{@link ProxyManager} Bean 不创建，
 * {@link RateLimitAspect} 也不会强依赖（通过 {@code ObjectProvider} 注入），
 * 但生产环境通常都启用 Redis，此处采用 {@link ConditionalOnClass} 守卫。</p>
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class AspectConfig {

    /**
     * 创建独立的 Bucket4j {@link RedisClient} Bean。
     *
     * <p>从 Spring 的 {@link LettuceConnectionFactory} 读取 host/port/password/database，
     * 构建一个独立管理的 {@link RedisClient}（与 Spring Data Redis 共享连接池配置但独立连接），
     * 避免与 Spring 的共享连接竞争，且可独立关闭。</p>
     *
     * @param connectionFactory Spring Data Redis 连接工厂（Lettuce 实现）
     * @return Bucket4j 专用的 Lettuce {@link RedisClient}
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnClass({RedisClient.class, LettuceConnectionFactory.class})
    @ConditionalOnMissingBean(name = "bucket4jRedisClient")
    public RedisClient bucket4jRedisClient(LettuceConnectionFactory connectionFactory) {
        RedisURI.Builder uriBuilder = RedisURI.builder()
                .withHost(connectionFactory.getHostName())
                .withPort(connectionFactory.getPort())
                .withDatabase(connectionFactory.getDatabase())
                .withTimeout(Duration.ofSeconds(5));
        String password = connectionFactory.getPassword();
        if (password != null && !password.isEmpty()) {
            uriBuilder.withPassword(password.toCharArray());
        }
        RedisClient client = RedisClient.create(uriBuilder.build());
        log.info("Bucket4j 专用 RedisClient 已创建: {}:{}/{}",
                connectionFactory.getHostName(),
                connectionFactory.getPort(),
                connectionFactory.getDatabase());
        return client;
    }

    /**
     * 创建 Bucket4j 专用的 Lettuce {@link StatefulRedisConnection}。
     * 使用 {@link ByteArrayCodec} 以 byte[] 形式读写，匹配 Bucket4j 序列化需求。
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnClass({RedisClient.class, StatefulRedisConnection.class})
    @ConditionalOnMissingBean(name = "bucket4jRedisConnection")
    public StatefulRedisConnection<byte[], byte[]> bucket4jRedisConnection(RedisClient bucket4jRedisClient) {
        return bucket4jRedisClient.connect(ByteArrayCodec.INSTANCE);
    }

    /**
     * 创建 Bucket4j {@link ProxyManager}（Lettuce + Redis 分布式代理）。
     *
     * <p>使用「基于最大填充时间的过期策略」：
     * 当桶长时间未被访问且达到最大填充时间后，Redis 中的桶状态自动过期，
     * 避免无活跃用户的桶状态永久驻留 Redis 造成内存泄漏。</p>
     */
    @Bean
    @ConditionalOnClass({LettuceBasedProxyManager.class})
    @ConditionalOnMissingBean(ProxyManager.class)
    public ProxyManager<byte[]> bucket4jProxyManager(
            StatefulRedisConnection<byte[], byte[]> bucket4jRedisConnection) {
        LettuceBasedProxyManager<byte[]> proxyManager = LettuceBasedProxyManager.builderFor(
                bucket4jRedisConnection)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy
                                .basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1)))
                .build();
        log.info("Bucket4j 分布式 ProxyManager（Lettuce + Redis）已初始化");
        return proxyManager;
    }

    /**
     * 注册 {@link RateLimitAspect} 切面 Bean。
     */
    @Bean
    @ConditionalOnMissingBean(RateLimitAspect.class)
    public RateLimitAspect rateLimitAspect(ProxyManager<byte[]> bucket4jProxyManager) {
        return new RateLimitAspect(bucket4jProxyManager);
    }

    /**
     * 注册 {@link IdempotentAspect} 幂等切面 Bean。
     *
     * <p>依赖 Spring Data Redis 自动配置的 {@link StringRedisTemplate} 与
     * Spring Boot 自动配置的 {@link ObjectMapper}。当 Classpath 不存在 Redis
     * 时该 Bean 仍会创建（仅在调用时抛错），由 {@code @ConditionalOnClass}
     * 守卫 StringRedisTemplate 类避免强依赖。</p>
     *
     * @param stringRedisTemplate Redis 字符串模板
     * @param objectMapper        Jackson 序列化器
     * @return 幂等切面实例
     */
    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    @ConditionalOnMissingBean(IdempotentAspect.class)
    public IdempotentAspect idempotentAspect(StringRedisTemplate stringRedisTemplate,
                                             ObjectMapper objectMapper) {
        log.info("IdempotentAspect 幂等切面已初始化");
        return new IdempotentAspect(stringRedisTemplate, objectMapper);
    }
}
