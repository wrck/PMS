package com.dp.plat.project.punchlist.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.mapper.MilestoneMapper;
import com.dp.plat.project.punchlist.entity.PunchList;
import com.dp.plat.project.punchlist.mapper.PunchListMapper;
import com.dp.plat.project.punchlist.service.impl.PunchListServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PunchListServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class PunchListServiceImplTest {

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_RESOLVED = "RESOLVED";
    private static final String STATUS_VERIFIED = "VERIFIED";
    private static final String SEVERITY_SAFETY = "SAFETY";
    private static final String SEVERITY_FUNCTIONAL = "FUNCTIONAL";
    private static final String MILESTONE_BLOCKED = "BLOCKED";
    private static final String MILESTONE_COMPLETED = "COMPLETED";

    @Mock
    private PunchListMapper punchListMapper;

    @Mock
    private MilestoneMapper milestoneMapper;

    @Mock
    private INotificationService notificationService;

    @Spy
    @InjectMocks
    private PunchListServiceImpl punchListService;

    @BeforeEach
    void setUp() {
        // ServiceImpl.baseMapper (PunchListMapper) is wired via field injection at runtime;
        // @InjectMocks stops at constructor injection so set it manually.
        ReflectionTestUtils.setField(punchListService, "baseMapper", punchListMapper);
    }

    private PunchList samplePunchList(Long id, Long projectId, String status, String severity) {
        PunchList pl = PunchList.builder()
                .projectId(projectId)
                .milestoneId(1L)
                .severity(severity)
                .title("缺陷-" + id)
                .description("描述")
                .status(status)
                .assigneeId(10L)
                .build();
        pl.setId(id);
        return pl;
    }

    @Test
    @DisplayName("create: 缺省状态填充为 OPEN 并保存")
    void create_shouldSaveWithOpenStatus() {
        PunchList input = PunchList.builder()
                .projectId(10L)
                .title("走查缺陷")
                .severity(SEVERITY_FUNCTIONAL)
                .build();
        when(punchListMapper.insert(any(PunchList.class))).thenAnswer(invocation -> {
            PunchList p = invocation.getArgument(0);
            p.setId(1L);
            return 1;
        });

        Result<PunchList> result = punchListService.create(input);

        assertTrue(result.isSuccess());
        assertEquals(STATUS_OPEN, result.getData().getStatus(), "缺省状态应为 OPEN");
        verify(punchListMapper, times(1)).insert(any(PunchList.class));
    }

    @Test
    @DisplayName("create: 入参为 null 抛出业务异常")
    void create_null_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> punchListService.create(null));
        assertTrue(ex.getMessage().contains("Punch List"));
        verify(punchListMapper, never()).insert(any(PunchList.class));
    }

    @Test
    @DisplayName("create: 缺少项目ID抛出业务异常")
    void create_missingProjectId_throws() {
        PunchList input = PunchList.builder()
                .title("缺陷")
                .severity(SEVERITY_FUNCTIONAL)
                .build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> punchListService.create(input));
        assertTrue(ex.getMessage().contains("项目ID"));
        verify(punchListMapper, never()).insert(any(PunchList.class));
    }

    @Test
    @DisplayName("create: 缺少标题抛出业务异常")
    void create_missingTitle_throws() {
        PunchList input = PunchList.builder()
                .projectId(10L)
                .severity(SEVERITY_FUNCTIONAL)
                .build();
        assertThrows(BusinessException.class, () -> punchListService.create(input));
        verify(punchListMapper, never()).insert(any(PunchList.class));
    }

    @Test
    @DisplayName("create: 缺少严重等级抛出业务异常")
    void create_missingSeverity_throws() {
        PunchList input = PunchList.builder()
                .projectId(10L)
                .title("缺陷")
                .build();
        assertThrows(BusinessException.class, () -> punchListService.create(input));
        verify(punchListMapper, never()).insert(any(PunchList.class));
    }

    @Test
    @DisplayName("create: SAFETY 级缺陷且里程碑非完成状态时阻塞里程碑")
    void create_safetySeverity_blocksMilestone() {
        PunchList input = PunchList.builder()
                .projectId(10L)
                .milestoneId(7L)
                .title("安全隐患")
                .severity(SEVERITY_SAFETY)
                .build();
        when(punchListMapper.insert(any(PunchList.class))).thenReturn(1);
        Milestone milestone = Milestone.builder()
                .projectId(10L)
                .milestoneName("M1")
                .status("PENDING")
                .build();
        milestone.setId(7L);
        when(milestoneMapper.selectById(7L)).thenReturn(milestone);
        when(milestoneMapper.updateById(any(Milestone.class))).thenReturn(1);

        punchListService.create(input);

        assertEquals(MILESTONE_BLOCKED, milestone.getStatus(), "里程碑应被置为 BLOCKED");
        verify(milestoneMapper, times(1)).updateById(any(Milestone.class));
    }

    @Test
    @DisplayName("create: SAFETY 级缺陷但里程碑已完成时不阻塞")
    void create_safetySeverity_completedMilestoneNotBlocked() {
        PunchList input = PunchList.builder()
                .projectId(10L)
                .milestoneId(7L)
                .title("安全隐患")
                .severity(SEVERITY_SAFETY)
                .build();
        when(punchListMapper.insert(any(PunchList.class))).thenReturn(1);
        Milestone milestone = Milestone.builder()
                .projectId(10L)
                .milestoneName("M1")
                .status(MILESTONE_COMPLETED)
                .build();
        milestone.setId(7L);
        when(milestoneMapper.selectById(7L)).thenReturn(milestone);

        punchListService.create(input);

        assertEquals(MILESTONE_COMPLETED, milestone.getStatus(), "已完成里程碑不应被阻塞");
        verify(milestoneMapper, never()).updateById(any(Milestone.class));
    }

    @Test
    @DisplayName("create: SAFETY 级缺陷但里程碑不存在时仅记日志")
    void create_safetySeverity_milestoneNotFound() {
        PunchList input = PunchList.builder()
                .projectId(10L)
                .milestoneId(99L)
                .title("安全隐患")
                .severity(SEVERITY_SAFETY)
                .build();
        when(punchListMapper.insert(any(PunchList.class))).thenReturn(1);
        when(milestoneMapper.selectById(99L)).thenReturn(null);

        punchListService.create(input);
        // 不抛异常即可
        verify(milestoneMapper, never()).updateById(any(Milestone.class));
    }

    @Test
    @DisplayName("create: 非 SAFETY 级缺陷不阻塞里程碑")
    void create_functionalSeverity_doesNotBlockMilestone() {
        PunchList input = PunchList.builder()
                .projectId(10L)
                .milestoneId(7L)
                .title("功能缺陷")
                .severity(SEVERITY_FUNCTIONAL)
                .build();
        when(punchListMapper.insert(any(PunchList.class))).thenReturn(1);

        punchListService.create(input);

        verify(milestoneMapper, never()).selectById(anyLong());
        verify(milestoneMapper, never()).updateById(any(Milestone.class));
    }

    @Test
    @DisplayName("update: 更新已存在 Punch List 项")
    void update_existing_succeeds() {
        PunchList existing = samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL);
        when(punchListMapper.selectById(1L)).thenReturn(existing);
        when(punchListMapper.updateById(any(PunchList.class))).thenReturn(1);

        PunchList toUpdate = PunchList.builder().build();
        toUpdate.setId(1L);
        toUpdate.setTitle("更新标题");
        Result<?> result = punchListService.update(toUpdate);

        assertTrue(result.isSuccess());
        verify(punchListMapper, times(1)).updateById(any(PunchList.class));
    }

    @Test
    @DisplayName("update: 缺少 ID 抛出业务异常")
    void update_missingId_throws() {
        assertThrows(BusinessException.class, () -> punchListService.update(new PunchList()));
        verify(punchListMapper, never()).updateById(any(PunchList.class));
    }

    @Test
    @DisplayName("update: Punch List 不存在抛出业务异常")
    void update_notFound_throws() {
        when(punchListMapper.selectById(anyLong())).thenReturn(null);
        PunchList toUpdate = PunchList.builder().build();
        toUpdate.setId(99L);
        assertThrows(BusinessException.class, () -> punchListService.update(toUpdate));
        verify(punchListMapper, never()).updateById(any(PunchList.class));
    }

    @Test
    @DisplayName("delete: 删除已存在 Punch List 项")
    void delete_existing_succeeds() {
        when(punchListMapper.selectById(1L)).thenReturn(samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL));
        // ServiceImpl.removeById 依赖 TableInfo（单元测试中为 null），stub 为 true
        doReturn(true).when(punchListService).removeById(1L);

        Result<?> result = punchListService.delete(1L);

        assertTrue(result.isSuccess());
        verify(punchListService, times(1)).removeById(1L);
    }

    @Test
    @DisplayName("delete: Punch List 不存在抛出业务异常")
    void delete_notFound_throws() {
        when(punchListMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> punchListService.delete(99L));
        verify(punchListMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("getById: 返回 Punch List 项")
    void getById_found() {
        PunchList pl = samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL);
        when(punchListMapper.selectById(1L)).thenReturn(pl);

        Result<PunchList> result = punchListService.getById(1L);

        assertTrue(result.isSuccess());
        assertEquals("缺陷-1", result.getData().getTitle());
    }

    @Test
    @DisplayName("getById: id 为 null 抛出业务异常")
    void getById_nullId_throws() {
        assertThrows(BusinessException.class, () -> punchListService.getById(null));
    }

    @Test
    @DisplayName("getById: 不存在抛出业务异常")
    void getById_notFound_throws() {
        when(punchListMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> punchListService.getById(99L));
    }

    @Test
    @DisplayName("listByProject: 返回项目下 Punch List 项")
    void listByProject_returnsList() {
        List<PunchList> list = Arrays.asList(
                samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL),
                samplePunchList(2L, 10L, STATUS_VERIFIED, SEVERITY_FUNCTIONAL));
        when(punchListMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<PunchList>> result = punchListService.listByProject(10L);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    @DisplayName("listByProject: projectId 为 null 抛出业务异常")
    void listByProject_nullId_throws() {
        assertThrows(BusinessException.class, () -> punchListService.listByProject(null));
    }

    @Test
    @DisplayName("listByMilestone: 返回里程碑下 Punch List 项")
    void listByMilestone_returnsList() {
        List<PunchList> list = Collections.singletonList(
                samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL));
        when(punchListMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<PunchList>> result = punchListService.listByMilestone(7L);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("listByMilestone: milestoneId 为 null 抛出业务异常")
    void listByMilestone_nullId_throws() {
        assertThrows(BusinessException.class, () -> punchListService.listByMilestone(null));
    }

    @Test
    @DisplayName("resolve: OPEN 状态 Punch List 项标记为 RESOLVED 并记录时间")
    void resolve_shouldChangeStatusToResolved() {
        PunchList pl = samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL);
        when(punchListMapper.selectById(1L)).thenReturn(pl);
        when(punchListMapper.updateById(any(PunchList.class))).thenReturn(1);

        Result<PunchList> result = punchListService.resolve(1L);

        assertTrue(result.isSuccess());
        assertEquals(STATUS_RESOLVED, pl.getStatus());
        assertNotNull(pl.getResolvedAt());
        verify(punchListMapper, times(1)).updateById(any(PunchList.class));
    }

    @Test
    @DisplayName("resolve: 非 OPEN 状态不允许标记已解决")
    void resolve_wrongStatus_throws() {
        PunchList pl = samplePunchList(1L, 10L, STATUS_RESOLVED, SEVERITY_FUNCTIONAL);
        when(punchListMapper.selectById(1L)).thenReturn(pl);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> punchListService.resolve(1L));
        assertTrue(ex.getMessage().contains("已解决"));
        verify(punchListMapper, never()).updateById(any(PunchList.class));
    }

    @Test
    @DisplayName("resolve: Punch List 不存在抛出业务异常")
    void resolve_notFound_throws() {
        when(punchListMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> punchListService.resolve(99L));
        verify(punchListMapper, never()).updateById(any(PunchList.class));
    }

    @Test
    @DisplayName("verify: RESOLVED 状态 Punch List 项验证后置为 VERIFIED")
    void verify_shouldChangeStatusToVerified() {
        PunchList pl = samplePunchList(1L, 10L, STATUS_RESOLVED, SEVERITY_FUNCTIONAL);
        when(punchListMapper.selectById(1L)).thenReturn(pl);
        when(punchListMapper.updateById(any(PunchList.class))).thenReturn(1);

        Result<PunchList> result = punchListService.verify(1L);

        assertTrue(result.isSuccess());
        assertEquals(STATUS_VERIFIED, pl.getStatus());
        assertNotNull(pl.getVerifiedAt());
        verify(punchListMapper, times(1)).updateById(any(PunchList.class));
    }

    @Test
    @DisplayName("verify: 非 RESOLVED 状态不允许验证")
    void verify_wrongStatus_throws() {
        PunchList pl = samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL);
        when(punchListMapper.selectById(1L)).thenReturn(pl);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> punchListService.verify(1L));
        assertTrue(ex.getMessage().contains("已解决"));
        verify(punchListMapper, never()).updateById(any(PunchList.class));
    }

    @Test
    @DisplayName("verify: Punch List 不存在抛出业务异常")
    void verify_notFound_throws() {
        when(punchListMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> punchListService.verify(99L));
    }

    @Test
    @DisplayName("isAllVerified: 全部 VERIFIED 时返回 true")
    void isAllVerified_allVerified_true() {
        List<PunchList> list = Arrays.asList(
                samplePunchList(1L, 10L, STATUS_VERIFIED, SEVERITY_FUNCTIONAL),
                samplePunchList(2L, 10L, STATUS_VERIFIED, SEVERITY_FUNCTIONAL));
        when(punchListMapper.selectList(any(Wrapper.class))).thenReturn(list);

        assertTrue(punchListService.isAllVerified(10L));
    }

    @Test
    @DisplayName("isAllVerified: 存在未验证项时返回 false")
    void isAllVerified_someOpen_false() {
        List<PunchList> list = Arrays.asList(
                samplePunchList(1L, 10L, STATUS_VERIFIED, SEVERITY_FUNCTIONAL),
                samplePunchList(2L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL));
        when(punchListMapper.selectList(any(Wrapper.class))).thenReturn(list);

        assertEquals(false, punchListService.isAllVerified(10L));
    }

    @Test
    @DisplayName("isAllVerified: 项目无 Punch List 项时返回 true（清零校验通过）")
    void isAllVerified_empty_true() {
        when(punchListMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        assertTrue(punchListService.isAllVerified(10L));
    }

    @Test
    @DisplayName("isAllVerified: projectId 为 null 时返回 false")
    void isAllVerified_nullProject_false() {
        assertEquals(false, punchListService.isAllVerified(null));
    }

    @Test
    @DisplayName("scanDeadlineApproaching: 临近到期项发送通知，无 assigneeId 跳过")
    void scanDeadlineApproaching_sendsNotifications() {
        PunchList withAssignee = samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL);
        withAssignee.setDeadline(LocalDate.now().plusDays(2));
        PunchList noAssignee = samplePunchList(2L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL);
        noAssignee.setAssigneeId(null);
        noAssignee.setDeadline(LocalDate.now().plusDays(1));
        when(punchListMapper.selectList(any(Wrapper.class)))
                .thenReturn(Arrays.asList(withAssignee, noAssignee));

        punchListService.scanDeadlineApproaching();

        // 仅 withAssignee 触发 multiChannelSend
        verify(notificationService, times(1)).multiChannelSend(any(Notification.class), anySet());
    }

    @Test
    @DisplayName("scanDeadlineApproaching: 无临近到期项时不发送通知")
    void scanDeadlineApproaching_noItems_noNotifications() {
        when(punchListMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        punchListService.scanDeadlineApproaching();

        verify(notificationService, never()).multiChannelSend(any(Notification.class), anySet());
    }

    @Test
    @DisplayName("scanDeadlineApproaching: 通知发送异常被吞掉不影响扫描")
    void scanDeadlineApproaching_notificationExceptionSwallowed() {
        PunchList item = samplePunchList(1L, 10L, STATUS_OPEN, SEVERITY_FUNCTIONAL);
        item.setDeadline(LocalDate.now().plusDays(2));
        when(punchListMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(item));
        doThrow(new RuntimeException("redis down"))
                .when(notificationService).multiChannelSend(any(Notification.class), anySet());

        // 不应抛异常
        punchListService.scanDeadlineApproaching();
        verify(notificationService, times(1)).multiChannelSend(any(Notification.class), anySet());
    }
}
