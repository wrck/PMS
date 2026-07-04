package com.dp.plat.notification.ws;

import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.mapper.NotificationMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 通知订阅器。
 *
 * <p>实现 {@link MessageListener}，监听 Redis 频道 {@link NotificationPublisher#BROADCAST_CHANNEL}。
 * 收到广播消息后，根据 notificationId 从库中加载通知，通过 {@link SimpMessagingTemplate}
 * 推送到对应用户的广播频道 {@code /topic/notification/{userId}}。</p>
 *
 * <p>采用广播频道方案而非 {@code convertAndSendToUser}：前端为原生 WebSocket 客户端，
 * STOMP CONNECT 帧不携带 Principal，因此使用按 userId 划分的广播频道，前端订阅
 * {@code /topic/notification/{当前登录用户id}} 即可收到推送。该方案更简单可靠，
 * 无需自定义 UserDestinationResolver。</p>
 *
 * <p>注意：Redis 频道订阅本身由 pms-system 的 RedisConfig 配置
 * （将本 Bean 绑定到 {@link NotificationPublisher#BROADCAST_CHANNEL}），
 * 本类只负责消息处理。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

    /** 单用户通知广播频道前缀，完整目的地为 {@code /topic/notification/{userId}}。 */
    private static final String WS_DESTINATION_PREFIX = "/topic/notification/";

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(body);
            long userId = node.get("userId").asLong();
            long notificationId = node.get("notificationId").asLong();

            Notification notification = notificationMapper.selectById(notificationId);
            if (notification == null) {
                log.warn("广播消息对应的通知不存在 notificationId={}", notificationId);
                return;
            }
            // 推送到按 userId 划分的广播频道，前端订阅 /topic/notification/{userId}
            messagingTemplate.convertAndSend(WS_DESTINATION_PREFIX + userId, notification);
            log.debug("已推送 WebSocket 通知 userId={} notificationId={}", userId, notificationId);
        } catch (Exception e) {
            log.error("处理通知广播消息失败 body={}", new String(message.getBody(), StandardCharsets.UTF_8), e);
        }
    }
}
