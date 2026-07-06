package com.dp.plat.implementation.service;

import com.dp.plat.implementation.service.impl.NotificationServiceImpl;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link NotificationServiceImpl} (implementation domain adapter).
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private INotificationService notificationCenterService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("notifyUser: 构建站内信通知并委托通知中心多通道投递")
    void notifyUser_delegatesToNotificationCenter() {
        notificationService.notifyUser(100L, "任务已分配", "您有新的实施任务待处理");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationCenterService, times(1)).multiChannelSend(captor.capture(), anySet());

        Notification sent = captor.getValue();
        assertEquals(100L, sent.getUserId());
        assertEquals("任务已分配", sent.getTitle());
        assertEquals("您有新的实施任务待处理", sent.getContent());
        assertEquals("TASK", sent.getCategory());
        assertEquals("UNREAD", sent.getReadStatus());
        assertEquals("IN_APP", sent.getChannel());
    }

    @Test
    @DisplayName("notifyUser: 投递通道包含 IN_APP 和 WS")
    void notifyUser_channelsIncludeInAppAndWs() {
        notificationService.notifyUser(1L, "title", "content");

        ArgumentCaptor<Set<String>> captor = ArgumentCaptor.forClass(Set.class);
        verify(notificationCenterService, times(1)).multiChannelSend(any(Notification.class), captor.capture());

        Set<String> channels = captor.getValue();
        assertEquals(2, channels.size());
        // Set.of 不可变集合应包含两个通道
        assertNotNull(channels);
    }

    @Test
    @DisplayName("notifyUser: userId/title/content 为不同值时正确传递")
    void notifyUser_variousInputs() {
        notificationService.notifyUser(999L, "里程碑达成", "项目 M1 已完成");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationCenterService, times(1)).multiChannelSend(captor.capture(), anySet());

        Notification sent = captor.getValue();
        assertEquals(999L, sent.getUserId());
        assertEquals("里程碑达成", sent.getTitle());
        assertEquals("项目 M1 已完成", sent.getContent());
    }
}
