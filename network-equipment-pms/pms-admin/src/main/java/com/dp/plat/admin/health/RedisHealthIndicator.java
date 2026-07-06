package com.dp.plat.admin.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Redis 健康检查指示器。
 *
 * <p>通过 {@link RedisConnection#ping()} 验证 Redis 连接可用性。
 * 连接获取或 ping 失败时报告 DOWN 并附带异常信息。</p>
 *
 * <p>Bean 名称 {@code pmsRedisHealthIndicator} 对应健康检查名称 {@code pmsRedis}，
 * 不覆盖 Spring Boot 默认的 {@code redis} 指标，二者并存互补。</p>
 */
@Component("pmsRedisHealthIndicator")
public class RedisHealthIndicator extends AbstractHealthIndicator {

    private final RedisConnectionFactory connectionFactory;

    public RedisHealthIndicator(RedisConnectionFactory connectionFactory) {
        super("Redis 健康检查失败");
        this.connectionFactory = connectionFactory;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Map<String, Object> details = new LinkedHashMap<>();
        try (RedisConnection conn = connectionFactory.getConnection()) {
            String pong = conn.ping();
            details.put("ping", pong);
            builder.up().withDetails(details);
        } catch (Exception e) {
            // 连接获取失败或 ping 超时均视为 Redis 不可用
            details.put("error", e.getMessage());
            builder.down().withDetails(details);
        }
    }
}
