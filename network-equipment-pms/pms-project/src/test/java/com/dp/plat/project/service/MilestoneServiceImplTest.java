package com.dp.plat.project.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.MilestoneMapper;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.impl.MilestoneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 * Unit tests for {@link MilestoneServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class MilestoneServiceImplTest {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_COMPLETED = "COMPLETED";

    @Mock
    private MilestoneMapper milestoneMapper;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private MilestoneServiceImpl milestoneService;

    @BeforeEach
    void setUp() {
        // ServiceImpl.baseMapper is populated via field injection; @InjectMocks stops at
        // constructor injection, so wire the baseMapper (MilestoneMapper) manually.
        ReflectionTestUtils.setField(milestoneService, "baseMapper", milestoneMapper);
    }

    private Milestone sampleMilestone(Long id, Long projectId, String name, String status) {
        Milestone milestone = Milestone.builder()
                .projectId(projectId)
                .milestoneName(name)
                .milestoneType("INSTALL")
                .planDate(LocalDate.of(2024, 3, 1))
                .status(status)
                .sortOrder(1)
                .build();
        milestone.setId(id);
        return milestone;
    }

    @Test
    @DisplayName("createMilestone: 默认填充 PENDING 状态并保存")
    void createMilestone_shouldSaveWithDefaults() {
        Milestone input = Milestone.builder()
                .projectId(10L)
                .milestoneName("现场安装")
                .planDate(LocalDate.of(2024, 3, 1))
                .build();
        // status 与 sortOrder 均未设置，应被默认填充
        when(milestoneMapper.insert(any(Milestone.class))).thenAnswer(invocation -> {
            Milestone m = invocation.getArgument(0);
            m.setId(1L);
            return 1;
        });

        Result result = milestoneService.createMilestone(input);

        assertTrue(result.isSuccess());
        Milestone saved = (Milestone) result.getData();
        assertNotNull(saved);
        assertEquals(STATUS_PENDING, saved.getStatus(), "缺省状态应为 PENDING");
        assertEquals(0, saved.getSortOrder(), "缺省排序应为 0");
        assertEquals("现场安装", saved.getMilestoneName());
        verify(milestoneMapper, times(1)).insert(any(Milestone.class));
    }

    @Test
    @DisplayName("createMilestone: 缺少项目ID抛出业务异常")
    void createMilestone_missingProjectId_throws() {
        Milestone input = Milestone.builder()
                .milestoneName("现场安装")
                .planDate(LocalDate.of(2024, 3, 1))
                .build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> milestoneService.createMilestone(input));
        assertTrue(ex.getMessage().contains("项目ID"));
        verify(milestoneMapper, never()).insert(any(Milestone.class));
    }

    @Test
    @DisplayName("createMilestone: 缺少计划日期抛出业务异常")
    void createMilestone_missingPlanDate_throws() {
        Milestone input = Milestone.builder()
                .projectId(10L)
                .milestoneName("现场安装")
                .build();
        assertThrows(BusinessException.class, () -> milestoneService.createMilestone(input));
        verify(milestoneMapper, never()).insert(any(Milestone.class));
    }

    @Test
    @DisplayName("updateProgress: 记录实际完成日期并将里程碑置为 COMPLETED")
    void updateProgress_shouldMarkCompleted() {
        Milestone existing = sampleMilestone(7L, 10L, "现场调试", STATUS_PENDING);
        when(milestoneMapper.selectById(7L)).thenReturn(existing);
        when(milestoneMapper.updateById(any(Milestone.class))).thenReturn(1);

        // recalculateProjectProgress 调用 list 与 projectMapper
        when(milestoneMapper.selectList(any(Wrapper.class)))
                .thenReturn(Collections.singletonList(
                        sampleMilestone(7L, 10L, "现场调试", STATUS_COMPLETED)));
        Project project = Project.builder().progress(0).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);

        Result result = milestoneService.updateProgress(7L, "2024-04-01", "调试完成");

        assertTrue(result.isSuccess());
        Milestone updated = (Milestone) result.getData();
        assertEquals(STATUS_COMPLETED, updated.getStatus());
        assertEquals(LocalDate.of(2024, 4, 1), updated.getActualDate());
        assertEquals("调试完成", updated.getDescription());
        verify(milestoneMapper, times(1)).updateById(any(Milestone.class));
    }

    @Test
    @DisplayName("updateProgress: 里程碑不存在抛出业务异常")
    void updateProgress_notFound_throws() {
        when(milestoneMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class,
                () -> milestoneService.updateProgress(999L, "2024-04-01", "x"));
        verify(milestoneMapper, never()).updateById(any(Milestone.class));
    }

    @Test
    @DisplayName("recalculateProjectProgress: completed/total*100 计算进度（2/4=50）")
    void recalculateProjectProgress_halfCompleted() {
        List<Milestone> milestones = new ArrayList<>();
        milestones.add(sampleMilestone(1L, 10L, "M1", STATUS_COMPLETED));
        milestones.add(sampleMilestone(2L, 10L, "M2", STATUS_COMPLETED));
        milestones.add(sampleMilestone(3L, 10L, "M3", STATUS_PENDING));
        milestones.add(sampleMilestone(4L, 10L, "M4", STATUS_PENDING));
        when(milestoneMapper.selectList(any(Wrapper.class))).thenReturn(milestones);
        Project project = Project.builder().progress(0).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);

        int progress = milestoneService.recalculateProjectProgress(10L);

        assertEquals(50, progress, "2/4*100 应为 50");
        assertEquals(50, project.getProgress(), "项目进度字段应被更新为 50");
        verify(projectMapper, times(1)).updateById(any(Project.class));
    }

    @Test
    @DisplayName("recalculateProjectProgress: 全部完成时进度为 100")
    void recalculateProjectProgress_allCompleted() {
        List<Milestone> milestones = Arrays.asList(
                sampleMilestone(1L, 10L, "M1", STATUS_COMPLETED),
                sampleMilestone(2L, 10L, "M2", STATUS_COMPLETED),
                sampleMilestone(3L, 10L, "M3", STATUS_COMPLETED));
        when(milestoneMapper.selectList(any(Wrapper.class))).thenReturn(milestones);
        Project project = Project.builder().progress(40).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);

        int progress = milestoneService.recalculateProjectProgress(10L);

        assertEquals(100, progress);
        assertEquals(100, project.getProgress());
    }

    @Test
    @DisplayName("recalculateProjectProgress: 无里程碑返回 0 且不更新项目")
    void recalculateProjectProgress_noMilestones_returnsZero() {
        when(milestoneMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        int progress = milestoneService.recalculateProjectProgress(10L);

        assertEquals(0, progress);
        verify(projectMapper, never()).updateById(any(Project.class));
    }

    @Test
    @DisplayName("recalculateProjectProgress: projectId 为 null 直接返回 0")
    void recalculateProjectProgress_nullProjectId() {
        int progress = milestoneService.recalculateProjectProgress(null);
        assertEquals(0, progress);
        verify(milestoneMapper, never()).selectList(any(Wrapper.class));
    }

    @Test
    @DisplayName("recalculateProjectProgress: 3/4 完成按整数除法得 75")
    void recalculateProjectProgress_threeOfFour() {
        List<Milestone> milestones = Arrays.asList(
                sampleMilestone(1L, 10L, "M1", STATUS_COMPLETED),
                sampleMilestone(2L, 10L, "M2", STATUS_COMPLETED),
                sampleMilestone(3L, 10L, "M3", STATUS_COMPLETED),
                sampleMilestone(4L, 10L, "M4", STATUS_PENDING));
        when(milestoneMapper.selectList(any(Wrapper.class))).thenReturn(milestones);
        Project project = Project.builder().progress(0).build();
        project.setId(10L);
        when(projectMapper.selectById(10L)).thenReturn(project);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);

        int progress = milestoneService.recalculateProjectProgress(10L);

        // (3 * 100 / 4) = 75
        assertEquals(75, progress);
    }

    @Test
    @DisplayName("listByProjectId: 按排序字段返回里程碑列表")
    void listByProjectId_returnsOrderedList() {
        List<Milestone> milestones = Arrays.asList(
                sampleMilestone(1L, 10L, "M1", STATUS_PENDING),
                sampleMilestone(2L, 10L, "M2", STATUS_PENDING));
        when(milestoneMapper.selectList(any(Wrapper.class))).thenReturn(milestones);

        Result<List<Milestone>> result = milestoneService.listByProjectId(10L);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    @DisplayName("listByProjectId: projectId 为 null 抛出业务异常")
    void listByProjectId_nullId_throws() {
        assertThrows(BusinessException.class, () -> milestoneService.listByProjectId(null));
    }

    @Test
    @DisplayName("deleteMilestone: 删除已存在里程碑并重算项目进度")
    void deleteMilestone_existing_recalculates() {
        Milestone existing = sampleMilestone(7L, 10L, "M7", STATUS_PENDING);
        when(milestoneMapper.selectById(7L)).thenReturn(existing);
        when(milestoneMapper.deleteById(7L)).thenReturn(1);
        when(milestoneMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        Result result = milestoneService.deleteMilestone(7L);

        assertTrue(result.isSuccess());
        verify(milestoneMapper, times(1)).deleteById(7L);
        verify(milestoneMapper, times(1)).selectList(any(Wrapper.class));
    }

    @Test
    @DisplayName("deleteMilestone: 里程碑不存在抛出业务异常")
    void deleteMilestone_notFound_throws() {
        when(milestoneMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> milestoneService.deleteMilestone(99L));
        verify(milestoneMapper, never()).deleteById(anyLong());
    }
}
