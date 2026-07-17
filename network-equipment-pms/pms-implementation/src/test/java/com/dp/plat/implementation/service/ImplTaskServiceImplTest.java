package com.dp.plat.implementation.service;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.implementation.entity.ImplProgress;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.implementation.mapper.TaskChecklistMapper;
import com.dp.plat.implementation.service.impl.ImplTaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ImplTaskServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ImplTaskServiceImplTest {

    private static final String TYPE_OEM = "OEM";
    private static final String TYPE_AGENT = "AGENT";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ACCEPTED = "ACCEPTED";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_CONFIRMED = "CONFIRMED";

    @Mock
    private ImplTaskMapper implTaskMapper;

    @Mock
    private IImplProgressService implProgressService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TaskChecklistMapper taskChecklistMapper;

    @InjectMocks
    private ImplTaskServiceImpl implTaskService;

    @BeforeEach
    void setUp() {
        // ServiceImpl.baseMapper (ImplTaskMapper) is wired via field injection at runtime;
        // @InjectMocks stops at constructor injection so set it manually.
        ReflectionTestUtils.setField(implTaskService, "baseMapper", implTaskMapper);
    }

    private ImplTask sampleTask(Long id, String status, Integer progress) {
        ImplTask task = ImplTask.builder()
                .projectId(10L)
                .taskName("部署任务")
                .taskType(TYPE_OEM)
                .engineerId(1L)
                .engineerName("Bob")
                .planStartDate(LocalDate.of(2024, 2, 1))
                .planEndDate(LocalDate.of(2024, 3, 1))
                .status(status)
                .progress(progress)
                .build();
        task.setId(id);
        return task;
    }

    @Test
    @DisplayName("assignOemTask: 创建 OEM 类型任务，状态为 PENDING")
    void assignOemTask_shouldCreateOemTaskWithPendingStatus() {
        ImplTask task = ImplTask.builder()
                .projectId(10L)
                .taskName("OEM 部署")
                .engineerId(1L)
                .build();
        when(implTaskMapper.insert(any(ImplTask.class))).thenAnswer(invocation -> {
            ImplTask t = invocation.getArgument(0);
            t.setId(1L);
            return 1;
        });

        ImplTask result = implTaskService.assignOemTask(task);

        assertNotNull(result);
        assertEquals(TYPE_OEM, result.getTaskType());
        assertEquals(STATUS_PENDING, result.getStatus());
        assertEquals(0, result.getProgress(), "progress 缺省应为 0");
        verify(implTaskMapper, times(1)).insert(any(ImplTask.class));
    }

    @Test
    @DisplayName("assignOemTask: 已有 progress 时保留原值")
    void assignOemTask_keepsExistingProgress() {
        ImplTask task = ImplTask.builder()
                .projectId(10L)
                .taskName("OEM 部署")
                .progress(30)
                .build();
        when(implTaskMapper.insert(any(ImplTask.class))).thenReturn(1);

        ImplTask result = implTaskService.assignOemTask(task);

        assertEquals(30, result.getProgress(), "已有 progress 不应被覆盖");
    }

    @Test
    @DisplayName("assignAgentTask: 创建 AGENT 类型任务，状态为 PENDING")
    void assignAgentTask_shouldCreateAgentTaskWithPendingStatus() {
        ImplTask task = ImplTask.builder()
                .projectId(10L)
                .taskName("代理商实施")
                .agentId(5L)
                .build();
        when(implTaskMapper.insert(any(ImplTask.class))).thenReturn(1);

        ImplTask result = implTaskService.assignAgentTask(task);

        assertNotNull(result);
        assertEquals(TYPE_AGENT, result.getTaskType());
        assertEquals(STATUS_PENDING, result.getStatus());
        assertEquals(0, result.getProgress());
        verify(implTaskMapper, times(1)).insert(any(ImplTask.class));
    }

    @Test
    @DisplayName("reportProgress: 创建进度记录并更新任务进度")
    void reportProgress_shouldCreateProgressAndUpdateTask() {
        ImplTask task = sampleTask(1L, STATUS_ACCEPTED, 0);
        when(implTaskMapper.selectById(1L)).thenReturn(task);
        when(implTaskMapper.updateById(any(ImplTask.class))).thenReturn(1);
        ImplProgress progress = ImplProgress.builder()
                .progressPercent(40)
                .workLog("已完成 40%")
                .build();
        when(implProgressService.create(any(ImplProgress.class))).thenReturn(progress);

        implTaskService.reportProgress(1L, progress);

        assertEquals(1L, progress.getTaskId(), "progress 的 taskId 应被设置");
        assertEquals(40, task.getProgress(), "任务进度应更新为进度记录的百分比");
        assertEquals(STATUS_IN_PROGRESS, task.getStatus(), "ACCEPTED 状态应转为 IN_PROGRESS");
        verify(implProgressService, times(1)).create(any(ImplProgress.class));
        verify(implTaskMapper, times(1)).updateById(any(ImplTask.class));
    }

    @Test
    @DisplayName("reportProgress: 进度记录无百分比时不更新任务进度")
    void reportProgress_nullPercent_keepsTaskProgress() {
        ImplTask task = sampleTask(1L, STATUS_IN_PROGRESS, 50);
        when(implTaskMapper.selectById(1L)).thenReturn(task);
        when(implTaskMapper.updateById(any(ImplTask.class))).thenReturn(1);
        ImplProgress progress = ImplProgress.builder()
                .workLog("无百分比进度")
                .build();
        when(implProgressService.create(any(ImplProgress.class))).thenReturn(progress);

        implTaskService.reportProgress(1L, progress);

        assertEquals(50, task.getProgress(), "无百分比时任务进度应保持不变");
        // IN_PROGRESS 状态不改变
        assertEquals(STATUS_IN_PROGRESS, task.getStatus());
    }

    @Test
    @DisplayName("reportProgress: 任务不存在抛出业务异常")
    void reportProgress_notFound_throws() {
        when(implTaskMapper.selectById(anyLong())).thenReturn(null);
        ImplProgress progress = ImplProgress.builder().progressPercent(10).build();
        assertThrows(BusinessException.class, () -> implTaskService.reportProgress(99L, progress));
        verify(implProgressService, never()).create(any(ImplProgress.class));
        verify(implTaskMapper, never()).updateById(any(ImplTask.class));
    }

    @Test
    @DisplayName("confirmTask: 已完成任务确认后状态变为 CONFIRMED")
    void confirmTask_shouldChangeStatusToConfirmed() {
        ImplTask task = sampleTask(1L, STATUS_COMPLETED, 100);
        when(implTaskMapper.selectById(1L)).thenReturn(task);
        when(implTaskMapper.updateById(any(ImplTask.class))).thenReturn(1);

        implTaskService.confirmTask(1L, "确认完成");

        assertEquals(STATUS_CONFIRMED, task.getStatus());
        assertEquals("确认完成", task.getAcceptOpinion());
        assertNotNull(task.getAcceptTime());
        verify(implTaskMapper, times(1)).updateById(any(ImplTask.class));
    }

    @Test
    @DisplayName("confirmTask: 非已完成状态不允许确认")
    void confirmTask_wrongStatus_throws() {
        ImplTask task = sampleTask(1L, STATUS_IN_PROGRESS, 50);
        when(implTaskMapper.selectById(1L)).thenReturn(task);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> implTaskService.confirmTask(1L, "ok"));
        assertTrue(ex.getMessage().contains("状态"));
        verify(implTaskMapper, never()).updateById(any(ImplTask.class));
    }

    @Test
    @DisplayName("confirmTask: 任务不存在抛出业务异常")
    void confirmTask_notFound_throws() {
        when(implTaskMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> implTaskService.confirmTask(99L, "ok"));
        verify(implTaskMapper, never()).updateById(any(ImplTask.class));
    }

    @Test
    @DisplayName("acceptTask: PENDING 任务接单后状态变为 ACCEPTED 并记录实际开始日期")
    void acceptTask_shouldChangeStatusToAccepted() {
        ImplTask task = sampleTask(1L, STATUS_PENDING, 0);
        when(implTaskMapper.selectById(1L)).thenReturn(task);
        when(implTaskMapper.updateById(any(ImplTask.class))).thenReturn(1);

        implTaskService.acceptTask(1L);

        assertEquals(STATUS_ACCEPTED, task.getStatus());
        assertNotNull(task.getActualStartDate());
        verify(implTaskMapper, times(1)).updateById(any(ImplTask.class));
    }

    @Test
    @DisplayName("acceptTask: 非 PENDING 状态不允许接单")
    void acceptTask_wrongStatus_throws() {
        ImplTask task = sampleTask(1L, STATUS_ACCEPTED, 0);
        when(implTaskMapper.selectById(1L)).thenReturn(task);

        assertThrows(BusinessException.class, () -> implTaskService.acceptTask(1L));
        verify(implTaskMapper, never()).updateById(any(ImplTask.class));
    }

    @Test
    @DisplayName("startTask: ACCEPTED 任务开始后状态变为 IN_PROGRESS")
    void startTask_shouldChangeStatusToInProgress() {
        ImplTask task = sampleTask(1L, STATUS_ACCEPTED, 0);
        task.setActualStartDate(LocalDate.of(2024, 2, 5));
        when(implTaskMapper.selectById(1L)).thenReturn(task);
        when(implTaskMapper.updateById(any(ImplTask.class))).thenReturn(1);

        implTaskService.startTask(1L);

        assertEquals(STATUS_IN_PROGRESS, task.getStatus());
        // 已有实际开始日期不被覆盖
        assertEquals(LocalDate.of(2024, 2, 5), task.getActualStartDate());
    }

    @Test
    @DisplayName("completeTask: 进行中任务完成后状态变为 COMPLETED 且进度为 100")
    void completeTask_shouldChangeStatusToCompleted() {
        ImplTask task = sampleTask(1L, STATUS_IN_PROGRESS, 60);
        when(implTaskMapper.selectById(1L)).thenReturn(task);
        when(implTaskMapper.updateById(any(ImplTask.class))).thenReturn(1);

        implTaskService.completeTask(1L, "全部完成");

        assertEquals(STATUS_COMPLETED, task.getStatus());
        assertEquals(100, task.getProgress(), "完成时进度应置为 100");
        assertNotNull(task.getActualEndDate());
        assertEquals("全部完成", task.getWorkDescription());
    }

    @Test
    @DisplayName("completeTask: 非进行中状态不允许完成")
    void completeTask_wrongStatus_throws() {
        ImplTask task = sampleTask(1L, STATUS_PENDING, 0);
        when(implTaskMapper.selectById(1L)).thenReturn(task);

        assertThrows(BusinessException.class, () -> implTaskService.completeTask(1L, "done"));
        verify(implTaskMapper, never()).updateById(any(ImplTask.class));
    }

    @Test
    @DisplayName("rejectTask: 任意状态任务均可驳回")
    void rejectTask_setsRejectedStatus() {
        ImplTask task = sampleTask(1L, STATUS_IN_PROGRESS, 30);
        when(implTaskMapper.selectById(1L)).thenReturn(task);
        when(implTaskMapper.updateById(any(ImplTask.class))).thenReturn(1);

        implTaskService.rejectTask(1L, "质量不达标");

        assertEquals("REJECTED", task.getStatus());
        assertEquals("质量不达标", task.getAcceptOpinion());
        assertNotNull(task.getAcceptTime());
    }

    @Test
    @DisplayName("rejectTask: 任务不存在抛出业务异常")
    void rejectTask_notFound_throws() {
        when(implTaskMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> implTaskService.rejectTask(99L, "no"));
    }
}
