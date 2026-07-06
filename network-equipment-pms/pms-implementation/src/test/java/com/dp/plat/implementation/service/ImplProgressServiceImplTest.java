package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.implementation.entity.ImplProgress;
import com.dp.plat.implementation.mapper.ImplProgressMapper;
import com.dp.plat.implementation.service.impl.ImplProgressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ImplProgressServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ImplProgressServiceImplTest {

    @Mock
    private ImplProgressMapper implProgressMapper;

    private ImplProgressServiceImpl implProgressService;

    @BeforeEach
    void setUp() {
        implProgressService = Mockito.spy(new ImplProgressServiceImpl());
        ReflectionTestUtils.setField(implProgressService, "baseMapper", implProgressMapper);
    }

    private ImplProgress sampleProgress(Long id, Long taskId, Integer percent, LocalDateTime reportTime) {
        ImplProgress p = ImplProgress.builder()
                .taskId(taskId)
                .progressPercent(percent)
                .workLog("已完成配置")
                .reportTime(reportTime)
                .build();
        p.setId(id);
        return p;
    }

    @Test
    @DisplayName("listByTaskId: 返回任务下的进度日志列表")
    void listByTaskId_returnsList() {
        List<ImplProgress> list = Arrays.asList(
                sampleProgress(1L, 10L, 50, LocalDateTime.of(2024, 6, 1, 9, 0)),
                sampleProgress(2L, 10L, 100, LocalDateTime.of(2024, 6, 2, 9, 0)));
        when(implProgressMapper.selectList(any(Wrapper.class))).thenReturn(list);

        List<ImplProgress> result = implProgressService.listByTaskId(10L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("listByTaskId: 任务下无进度日志返回空列表")
    void listByTaskId_empty() {
        when(implProgressMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        List<ImplProgress> result = implProgressService.listByTaskId(10L);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("create: 缺省 reportTime/reportUser 时自动填充")
    void create_defaultFieldsFilled() {
        ImplProgress progress = ImplProgress.builder()
                .taskId(10L)
                .progressPercent(30)
                .workLog("开始部署")
                .build();
        Mockito.doReturn(true).when(implProgressService).save(any(ImplProgress.class));

        ImplProgress result = implProgressService.create(progress);

        assertSame(progress, result);
        assertNotNull(progress.getReportTime(), "reportTime 缺省时应自动填充");
        // SecurityUtils.getCurrentUserId() 在无认证上下文的单元测试中返回 null，但代码确实调用了它
        // reportUserName 来自 SecurityUtils.getCurrentUsername()，无认证时返回 "system"
        assertEquals("system", progress.getReportUserName(), "reportUserName 缺省时应填充为 system");
        verify(implProgressService, Mockito.times(1)).save(any(ImplProgress.class));
    }

    @Test
    @DisplayName("create: 显式传入 reportTime/reportUser 时不被覆盖")
    void create_keepsExplicitFields() {
        LocalDateTime fixed = LocalDateTime.of(2024, 1, 1, 9, 0);
        ImplProgress progress = ImplProgress.builder()
                .taskId(10L)
                .progressPercent(30)
                .reportTime(fixed)
                .reportUserId(999L)
                .reportUserName("alice")
                .build();
        Mockito.doReturn(true).when(implProgressService).save(any(ImplProgress.class));

        implProgressService.create(progress);

        assertEquals(fixed, progress.getReportTime(), "显式 reportTime 应被保留");
        assertEquals(999L, progress.getReportUserId(), "显式 reportUserId 应被保留");
        assertEquals("alice", progress.getReportUserName(), "显式 reportUserName 应被保留");
    }

    @Test
    @DisplayName("create: 返回保存后的 progress 对象")
    void create_returnsSavedProgress() {
        ImplProgress progress = ImplProgress.builder()
                .taskId(10L)
                .progressPercent(50)
                .build();
        Mockito.doReturn(true).when(implProgressService).save(any(ImplProgress.class));

        ImplProgress result = implProgressService.create(progress);

        assertEquals(50, result.getProgressPercent());
        assertEquals(10L, result.getTaskId());
    }
}
