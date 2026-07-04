package com.dp.plat.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.mapper.NotificationMapper;
import com.dp.plat.notification.service.INotificationService;
import com.dp.plat.notification.template.NotificationTemplateEngine;
import com.dp.plat.notification.ws.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 通知中心服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    /** 站内信通道。 */
    private static final String CHANNEL_IN_APP = "IN_APP";
    /** WebSocket 实时推送通道。 */
    private static final String CHANNEL_WS = "WS";
    /** 邮件通道（占位）。 */
    private static final String CHANNEL_EMAIL = "EMAIL";
    /** OA 待办通道（占位）。 */
    private static final String CHANNEL_OA = "OA";

    private static final String READ_STATUS_UNREAD = "UNREAD";
    private static final String READ_STATUS_READ = "READ";

    private final NotificationMapper notificationMapper;
    private final NotificationPublisher notificationPublisher;
    private final NotificationTemplateEngine templateEngine;

    @Override
    public Notification create(Notification notification) {
        if (notification.getReadStatus() == null) {
            notification.setReadStatus(READ_STATUS_UNREAD);
        }
        if (notification.getChannel() == null) {
            notification.setChannel(CHANNEL_IN_APP);
        }
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        notificationMapper.insert(notification);
        return notification;
    }

    @Override
    public boolean markAsRead(Long id) {
        Notification update = new Notification();
        update.setId(id);
        update.setReadStatus(READ_STATUS_READ);
        return notificationMapper.updateById(update) > 0;
    }

    @Override
    public boolean markAllRead(Long userId) {
        Notification entity = new Notification();
        entity.setReadStatus(READ_STATUS_READ);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getReadStatus, READ_STATUS_UNREAD);
        return notificationMapper.update(entity, wrapper) > 0;
    }

    @Override
    public int unreadCount(Long userId) {
        Long count = notificationMapper.selectCount(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getReadStatus, READ_STATUS_UNREAD));
        return count == null ? 0 : count.intValue();
    }

    @Override
    public IPage<Notification> list(int page, int size, Notification filter) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        if (filter != null) {
            wrapper.eq(filter.getUserId() != null, Notification::getUserId, filter.getUserId())
                    .eq(filter.getCategory() != null, Notification::getCategory, filter.getCategory())
                    .eq(filter.getReadStatus() != null, Notification::getReadStatus, filter.getReadStatus())
                    .eq(filter.getBizType() != null, Notification::getBizType, filter.getBizType())
                    .eq(filter.getBizId() != null, Notification::getBizId, filter.getBizId());
        }
        wrapper.orderByDesc(Notification::getCreatedAt);
        return notificationMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void multiChannelSend(Notification notification, Set<String> channels) {
        if (channels == null || channels.isEmpty()) {
            return;
        }
        // 默认值
        if (notification.getReadStatus() == null) {
            notification.setReadStatus(READ_STATUS_UNREAD);
        }
        // IN_APP 通道：落库（同步，确保获得 id 供 WS 通道使用）。
        // WS 通道也需要 id，因此只要包含 IN_APP 或 WS 就先持久化。
        boolean needPersist = channels.contains(CHANNEL_IN_APP) || channels.contains(CHANNEL_WS);
        if (needPersist && notification.getId() == null) {
            if (notification.getCreatedAt() == null) {
                notification.setCreatedAt(LocalDateTime.now());
            }
            notificationMapper.insert(notification);
        }

        // 并发发送其余通道：任一失败仅记录日志，不阻塞其他通道。
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String channel : channels) {
            switch (channel) {
                case CHANNEL_IN_APP -> {
                    // 已通过上面的 insert 落库，站内信投递完成。
                    log.debug("IN_APP 通道已落库 notificationId={}", notification.getId());
                }
                case CHANNEL_WS -> futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        notificationPublisher.publish(notification.getUserId(), notification.getId());
                    } catch (Exception e) {
                        // WS 失败不阻塞其他通道
                        log.error("WS 通道发送失败 notificationId={}", notification.getId(), e);
                    }
                }));
                case CHANNEL_EMAIL -> futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        // 生产环境可接入 EmailService 发送邮件
                        log.info("[EMAIL 占位] 发送通知 userId={} title={}", notification.getUserId(), notification.getTitle());
                    } catch (Exception e) {
                        log.error("EMAIL 通道发送失败 notificationId={}", notification.getId(), e);
                    }
                }));
                case CHANNEL_OA -> futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        // 生产环境可接入 OaIntegrationService 创建 OA 待办
                        log.info("[OA 占位] 发送通知 userId={} title={}", notification.getUserId(), notification.getTitle());
                    } catch (Exception e) {
                        log.error("OA 通道发送失败 notificationId={}", notification.getId(), e);
                    }
                }));
                default -> log.warn("未知通知通道: {}", channel);
            }
        }
        // 等待所有通道完成（每个任务内部已吞掉异常，join 不会抛出）
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Override
    public void sendByTemplate(String templateCode, Map<String, Object> variables, Long userId, Set<String> channels) {
        NotificationTemplateEngine.RenderedTemplate rendered = templateEngine.render(templateCode, variables);
        Notification notification = Notification.builder()
                .userId(userId)
                .title(rendered.subject())
                .content(rendered.body())
                .bizType(templateCode)
                .readStatus(READ_STATUS_UNREAD)
                .createdAt(LocalDateTime.now())
                .build();
        multiChannelSend(notification, channels);
    }
}
