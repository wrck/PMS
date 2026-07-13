package com.dp.plat.implementation.service.impl;

import com.dp.plat.implementation.service.NotificationService;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 实施域通知服务实现：委托给 pms-notification 的通知中心。
 *
 * <p>该实现将实施域的简单通知调用（{@link NotificationService#notifyUser}）
 * 转换为通知中心的多通道投递（站内信 + WebSocket），确保实施域的通知
 * 能进入统一的消息中心并被实时推送。</p>
 */
@Slf4j
@Service("implementationNotificationServiceImpl")
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    /** 投递通道：站内信 + WebSocket 实时推送。 */
    private static final Set<String> CHANNELS = Set.of("IN_APP", "WS");

    private final INotificationService notificationCenterService;

    @Override
    public void notifyUser(Long userId, String title, String content) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .category("TASK")
                .readStatus("UNREAD")
                .channel("IN_APP")
                .build();
        notificationCenterService.multiChannelSend(notification, CHANNELS);
        log.debug("实施域通知已委托至通知中心：userId={}, title={}", userId, title);
    }
}
