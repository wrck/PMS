package com.dp.plat.notification.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 通知发布器。
 *
 * <p>通过 Redis Pub/Sub 将通知事件（userId + notificationId）发布到广播频道
 * {@code pms:notification:broadcast}。集群中每个实例订阅该频道后，由本地
 * {@link NotificationSubscriber} 推送到与当前实例建立 WebSocket 连接的用户，
 * 从而实现多实例下的实时通知广播。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    /** Redis 广播频道。 */
    public static final String BROADCAST_CHANNEL = "pms:notification:broadcast";

    private final StringRedisTemplate redisTemplate;

    /**
     * 发布通知事件到 Redis 广播频道。
     *
     * @param notificationId 通知 id
     * @param userId         接收人用户 id
     */
    public void publish(Long userId, Long notificationId) {
        String payload = String.format("{\"userId\":%d,\"notificationId\":%d}", userId, notificationId);
        redisTemplate.convertAndSend(BROADCAST_CHANNEL, payload);
        log.debug("已发布通知广播 userId={} notificationId={}", userId, notificationId);
    }
}
