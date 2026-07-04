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
 * 推送到对应用户的 WebSocket 队列 {@code /user/{userId}/queue/notifications}。</p>
 *
 * <p>注意：Redis 频道订阅本身由 pms-system 的 RedisConfig 配置
 * （将本 Bean 绑定到 {@link NotificationPublisher#BROADCAST_CHANNEL}），
 * 本类只负责消息处理。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

    private static final String WS_DESTINATION = "/queue/notifications";

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
            // 推送到当前实例上与该用户建立的 WebSocket 会话
            messagingTemplate.convertAndSendToUser(String.valueOf(userId), WS_DESTINATION, notification);
            log.debug("已推送 WebSocket 通知 userId={} notificationId={}", userId, notificationId);
        } catch (Exception e) {
            log.error("处理通知广播消息失败 body={}", new String(message.getBody(), StandardCharsets.UTF_8), e);
        }
    }
}
