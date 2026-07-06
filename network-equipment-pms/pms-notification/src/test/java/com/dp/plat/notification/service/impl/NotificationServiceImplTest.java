package com.dp.plat.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.mapper.NotificationMapper;
import com.dp.plat.notification.template.NotificationTemplateEngine;
import com.dp.plat.notification.ws.NotificationPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link NotificationServiceImpl} 的单元测试。
 *
 * <p>覆盖通知创建、已读管理、未读统计、分页查询、多通道并发投递及模板化发送等核心逻辑。</p>
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private NotificationTemplateEngine templateEngine;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    // ==================== create ====================

    @Test
    @DisplayName("create: readStatus 为 null 时默认置 UNREAD")
    void create_nullReadStatus_setsUnread() {
        Notification n = Notification.builder()
                .userId(1L).title("t").content("c").build();
        when(notificationMapper.insert(any(Notification.class))).thenReturn(1);

        notificationService.create(n);

        assertEquals("UNREAD", n.getReadStatus());
        verify(notificationMapper, times(1)).insert(any(Notification.class));
    }

    @Test
    @DisplayName("create: channel 为 null 时默认置 IN_APP")
    void create_nullChannel_setsInApp() {
        Notification n = Notification.builder()
                .userId(1L).title("t").content("c").build();
        when(notificationMapper.insert(any(Notification.class))).thenReturn(1);

        notificationService.create(n);

        assertEquals("IN_APP", n.getChannel());
    }

    @Test
    @DisplayName("create: createdAt 为 null 时填充当前时间")
    void create_nullCreatedAt_setsNow() {
        Notification n = Notification.builder()
                .userId(1L).title("t").content("c").build();
        when(notificationMapper.insert(any(Notification.class))).thenReturn(1);
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        notificationService.create(n);

        assertNotNull(n.getCreatedAt());
        assertTrue(n.getCreatedAt().isAfter(before));
    }

    @Test
    @DisplayName("create: 显式字段值不被默认值覆盖")
    void create_keepsExplicitValues() {
        LocalDateTime customTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Notification n = Notification.builder()
                .userId(1L).title("t").content("c")
                .readStatus("READ").channel("WS").createdAt(customTime).build();
        when(notificationMapper.insert(any(Notification.class))).thenReturn(1);

        notificationService.create(n);

        assertEquals("READ", n.getReadStatus());
        assertEquals("WS", n.getChannel());
        assertEquals(customTime, n.getCreatedAt());
    }

    @Test
    @DisplayName("create: 落库后返回同一对象")
    void create_insertsAndReturnsSame() {
        Notification n = Notification.builder()
                .userId(1L).title("t").content("c").build();
        when(notificationMapper.insert(any(Notification.class))).thenReturn(1);

        Notification returned = notificationService.create(n);

        assertSame(n, returned);
    }

    // ==================== markAsRead ====================

    @Test
    @DisplayName("markAsRead: updateById 返回 > 0 时返回 true，且仅更新 id 与 readStatus")
    void markAsRead_success() {
        when(notificationMapper.updateById(any(Notification.class))).thenReturn(1);

        boolean result = notificationService.markAsRead(10L);

        assertTrue(result);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationMapper).updateById(captor.capture());
        Notification update = captor.getValue();
        assertEquals(10L, update.getId());
        assertEquals("READ", update.getReadStatus());
    }

    @Test
    @DisplayName("markAsRead: updateById 返回 0 时返回 false")
    void markAsRead_failure() {
        when(notificationMapper.updateById(any(Notification.class))).thenReturn(0);

        boolean result = notificationService.markAsRead(10L);

        assertFalse(result);
    }

    // ==================== markAllRead ====================

    @Test
    @DisplayName("markAllRead: update 返回 > 0 时返回 true")
    void markAllRead_success() {
        when(notificationMapper.update(any(Notification.class), any(Wrapper.class))).thenReturn(5);

        boolean result = notificationService.markAllRead(100L);

        assertTrue(result);
    }

    @Test
    @DisplayName("markAllRead: update 返回 0 时返回 false")
    void markAllRead_failure() {
        when(notificationMapper.update(any(Notification.class), any(Wrapper.class))).thenReturn(0);

        boolean result = notificationService.markAllRead(100L);

        assertFalse(result);
    }

    // ==================== unreadCount ====================

    @Test
    @DisplayName("unreadCount: 返回 mapper 统计的未读数")
    void unreadCount_normal_returnsCount() {
        when(notificationMapper.selectCount(any(Wrapper.class))).thenReturn(7L);

        int count = notificationService.unreadCount(100L);

        assertEquals(7, count);
    }

    @Test
    @DisplayName("unreadCount: mapper 返回 null 时返回 0")
    void unreadCount_nullCount_returnsZero() {
        when(notificationMapper.selectCount(any(Wrapper.class))).thenReturn(null);

        int count = notificationService.unreadCount(100L);

        assertEquals(0, count);
    }

    // ==================== list ====================

    @Test
    @DisplayName("list: 带 filter 返回分页结果")
    void list_withFilter_returnsPage() {
        Notification filter = Notification.builder()
                .userId(100L).category("TASK").readStatus("UNREAD").build();
        Page<Notification> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
                Notification.builder().id(1L).build(),
                Notification.builder().id(2L).build()));
        mockPage.setTotal(2L);
        when(notificationMapper.selectPage(any(Page.class), any(Wrapper.class))).thenReturn(mockPage);

        IPage<Notification> result = notificationService.list(1, 10, filter);

        assertEquals(2, result.getRecords().size());
        assertEquals(2L, result.getTotal());
    }

    @Test
    @DisplayName("list: filter 为 null 时不报错并返回分页结果")
    void list_nullFilter_returnsPage() {
        Page<Notification> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.emptyList());
        mockPage.setTotal(0L);
        when(notificationMapper.selectPage(any(Page.class), any(Wrapper.class))).thenReturn(mockPage);

        IPage<Notification> result = notificationService.list(1, 10, null);

        assertNotNull(result);
        assertTrue(result.getRecords().isEmpty());
    }

    // ==================== multiChannelSend ====================

    @Test
    @DisplayName("multiChannelSend: channels 为空集合时直接返回，不触发任何操作")
    void multiChannelSend_emptyChannels_returns() {
        Notification n = Notification.builder().userId(1L).title("t").content("c").build();

        notificationService.multiChannelSend(n, new HashSet<>());

        verify(notificationMapper, never()).insert(any(Notification.class));
        verify(notificationPublisher, never()).publish(anyLong(), anyLong());
    }

    @Test
    @DisplayName("multiChannelSend: channels 为 null 时直接返回")
    void multiChannelSend_nullChannels_returns() {
        Notification n = Notification.builder().userId(1L).title("t").content("c").build();

        notificationService.multiChannelSend(n, null);

        verify(notificationMapper, never()).insert(any(Notification.class));
        verify(notificationPublisher, never()).publish(anyLong(), anyLong());
    }

    @Test
    @DisplayName("multiChannelSend: 仅 IN_APP 通道时落库且不推送 WS")
    void multiChannelSend_inAppOnly_persists() {
        Notification n = Notification.builder().userId(1L).title("t").content("c").build();
        when(notificationMapper.insert(any(Notification.class))).thenAnswer(invocation -> {
            Notification arg = invocation.getArgument(0);
            arg.setId(99L);
            return 1;
        });

        notificationService.multiChannelSend(n,
                new HashSet<>(Collections.singletonList("IN_APP")));

        verify(notificationMapper, times(1)).insert(any(Notification.class));
        verify(notificationPublisher, never()).publish(anyLong(), anyLong());
        assertEquals("UNREAD", n.getReadStatus(), "readStatus 为 null 应被置为 UNREAD");
        assertNotNull(n.getCreatedAt());
    }

    @Test
    @DisplayName("multiChannelSend: 多通道并发投递，IN_APP 落库且 WS 推送被调用")
    void multiChannelSend_multiChannel_allDelivered() {
        Notification n = Notification.builder().userId(100L).title("t").content("c").build();
        when(notificationMapper.insert(any(Notification.class))).thenAnswer(invocation -> {
            Notification arg = invocation.getArgument(0);
            arg.setId(99L);
            return 1;
        });

        Set<String> channels = new HashSet<>(Arrays.asList("IN_APP", "WS", "EMAIL", "OA"));
        notificationService.multiChannelSend(n, channels);

        verify(notificationMapper, times(1)).insert(any(Notification.class));
        verify(notificationPublisher, times(1)).publish(eq(100L), eq(99L));
    }

    @Test
    @DisplayName("multiChannelSend: WS 通道异常不阻塞其他通道，方法正常返回")
    void multiChannelSend_wsFailure_doesNotPropagate() {
        Notification n = Notification.builder().userId(100L).title("t").content("c").build();
        when(notificationMapper.insert(any(Notification.class))).thenAnswer(invocation -> {
            Notification arg = invocation.getArgument(0);
            arg.setId(99L);
            return 1;
        });
        doThrow(new RuntimeException("Redis 宕机"))
                .when(notificationPublisher).publish(anyLong(), anyLong());

        Set<String> channels = new HashSet<>(Arrays.asList("IN_APP", "WS"));
        // 不应抛出异常：WS 通道内部已吞掉异常
        notificationService.multiChannelSend(n, channels);

        verify(notificationMapper, times(1)).insert(any(Notification.class));
        verify(notificationPublisher, times(1)).publish(anyLong(), anyLong());
    }

    @Test
    @DisplayName("multiChannelSend: 未知通道仅记录日志，不落库不推送")
    void multiChannelSend_unknownChannel_warned() {
        Notification n = Notification.builder().userId(1L).title("t").content("c").build();

        notificationService.multiChannelSend(n,
                new HashSet<>(Collections.singletonList("UNKNOWN")));

        verify(notificationMapper, never()).insert(any(Notification.class));
        verify(notificationPublisher, never()).publish(anyLong(), anyLong());
    }

    @Test
    @DisplayName("multiChannelSend: notification.id 已存在时不重复落库")
    void multiChannelSend_idAlreadySet_skipsPersist() {
        Notification n = Notification.builder()
                .id(50L).userId(1L).title("t").content("c").readStatus("READ").build();

        notificationService.multiChannelSend(n,
                new HashSet<>(Collections.singletonList("IN_APP")));

        verify(notificationMapper, never()).insert(any(Notification.class));
        verify(notificationPublisher, never()).publish(anyLong(), anyLong());
    }

    @Test
    @DisplayName("multiChannelSend: 仅 WS 通道时也需要先落库以生成 id 供推送使用")
    void multiChannelSend_wsOnly_persistsForId() {
        Notification n = Notification.builder().userId(100L).title("t").content("c").build();
        when(notificationMapper.insert(any(Notification.class))).thenAnswer(invocation -> {
            Notification arg = invocation.getArgument(0);
            arg.setId(77L);
            return 1;
        });

        notificationService.multiChannelSend(n,
                new HashSet<>(Collections.singletonList("WS")));

        verify(notificationMapper, times(1)).insert(any(Notification.class));
        verify(notificationPublisher, times(1)).publish(eq(100L), eq(77L));
    }

    // ==================== sendByTemplate ====================

    @Test
    @DisplayName("sendByTemplate: 渲染模板后调用 multiChannelSend 投递，通知字段正确填充")
    void sendByTemplate_success() {
        NotificationTemplateEngine.RenderedTemplate rendered =
                new NotificationTemplateEngine.RenderedTemplate("任务通知", "您有新任务");
        when(templateEngine.render(eq("TASK_ASSIGNED"), any())).thenReturn(rendered);
        when(notificationMapper.insert(any(Notification.class))).thenAnswer(invocation -> {
            Notification arg = invocation.getArgument(0);
            arg.setId(1L);
            return 1;
        });

        Map<String, Object> variables = Map.of("taskName", "实施任务");
        notificationService.sendByTemplate("TASK_ASSIGNED", variables, 100L,
                new HashSet<>(Arrays.asList("IN_APP", "WS")));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationMapper, times(1)).insert(captor.capture());
        Notification sent = captor.getValue();
        assertEquals(100L, sent.getUserId());
        assertEquals("任务通知", sent.getTitle());
        assertEquals("您有新任务", sent.getContent());
        assertEquals("TASK_ASSIGNED", sent.getBizType());
        assertEquals("UNREAD", sent.getReadStatus());
        verify(templateEngine, times(1)).render(eq("TASK_ASSIGNED"), eq(variables));
        verify(notificationPublisher, times(1)).publish(eq(100L), eq(1L));
    }

    @Test
    @DisplayName("sendByTemplate: 模板不存在时抛出 BusinessException 且不调用 multiChannelSend")
    void sendByTemplate_templateNotFound_throws() {
        when(templateEngine.render(eq("NOT_EXIST"), any()))
                .thenThrow(new BusinessException("通知模板不存在: NOT_EXIST"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> notificationService.sendByTemplate("NOT_EXIST", Map.of(), 100L,
                        new HashSet<>(Collections.singletonList("IN_APP"))));

        assertTrue(ex.getMessage().contains("通知模板不存在"));
        verify(notificationMapper, never()).insert(any(Notification.class));
        verify(notificationPublisher, never()).publish(anyLong(), anyLong());
    }
}
