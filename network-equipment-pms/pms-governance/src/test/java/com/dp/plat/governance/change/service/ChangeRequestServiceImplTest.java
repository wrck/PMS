package com.dp.plat.governance.change.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.change.entity.ChangeRequest;
import com.dp.plat.governance.change.mapper.ChangeRequestMapper;
import com.dp.plat.governance.change.service.impl.ChangeRequestServiceImpl;
import com.dp.plat.workflow.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ChangeRequestServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ChangeRequestServiceImplTest {

    private static final String STATUS_SUBMITTED = "SUBMITTED";
    private static final String STATUS_UNDER_REVIEW = "UNDER_REVIEW";
    private static final String STATUS_CCB_APPROVED = "CCB_APPROVED";
    private static final String STATUS_CCB_REJECTED = "CCB_REJECTED";
    private static final String STATUS_IMPLEMENTING = "IMPLEMENTING";
    private static final String STATUS_CLOSED = "CLOSED";

    @Mock
    private ChangeRequestMapper changeRequestMapper;

    @Mock
    private IBaselineHistoryService baselineHistoryService;

    @Mock
    private ObjectProvider<WorkflowService> workflowServiceProvider;

    private ChangeRequestServiceImpl changeRequestService;

    @BeforeEach
    void setUp() {
        changeRequestService = Mockito.spy(new ChangeRequestServiceImpl(
                baselineHistoryService, workflowServiceProvider));
        ReflectionTestUtils.setField(changeRequestService, "baseMapper", changeRequestMapper);
    }

    private ChangeRequest sampleCr(Long id, String crNo, String status) {
        ChangeRequest cr = ChangeRequest.builder()
                .crNo(crNo)
                .projectId(100L)
                .title("变更标题")
                .description("变更描述")
                .priority("MEDIUM")
                .status(status)
                .build();
        cr.setId(id);
        return cr;
    }

    // ==================== create ====================

    @Test
    @DisplayName("create: changeRequest 为 null 抛出业务异常")
    void create_null_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> changeRequestService.create(null));
        assertTrue(ex.getMessage().contains("不能为空"));
    }

    @Test
    @DisplayName("create: title 为空抛出业务异常")
    void create_emptyTitle_throws() {
        ChangeRequest cr = ChangeRequest.builder().description("desc").build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> changeRequestService.create(cr));
        assertTrue(ex.getMessage().contains("标题"));
    }

    @Test
    @DisplayName("create: 正常创建，生成 crNo、状态置 SUBMITTED、缺省 priority/baselineUpdated")
    void create_normal_success() {
        ChangeRequest cr = ChangeRequest.builder()
                .projectId(100L)
                .title("网络架构变更")
                .description("描述")
                .build();
        when(changeRequestMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        Mockito.doReturn(true).when(changeRequestService).save(any(ChangeRequest.class));

        Result<ChangeRequest> result = changeRequestService.create(cr);

        assertTrue(result.isSuccess());
        assertNotNull(cr.getCrNo(), "crNo 应自动生成");
        assertEquals(STATUS_SUBMITTED, cr.getStatus());
        assertEquals("MEDIUM", cr.getPriority(), "缺省 priority 应为 MEDIUM");
        assertEquals(false, cr.getBaselineUpdated(), "缺省 baselineUpdated 应为 false");
        assertNotNull(cr.getRequestDate(), "缺省 requestDate 应为今天");
        assertEquals(LocalDate.now(), cr.getRequestDate());
    }

    @Test
    @DisplayName("create: 显式 priority 和 baselineUpdated 时不被覆盖")
    void create_keepsExplicitFields() {
        ChangeRequest cr = ChangeRequest.builder()
                .projectId(100L)
                .title("变更")
                .description("描述")
                .priority("HIGH")
                .baselineUpdated(true)
                .requestDate(LocalDate.of(2024, 1, 1))
                .build();
        when(changeRequestMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        Mockito.doReturn(true).when(changeRequestService).save(any(ChangeRequest.class));

        changeRequestService.create(cr);

        assertEquals("HIGH", cr.getPriority());
        assertEquals(true, cr.getBaselineUpdated());
        assertEquals(LocalDate.of(2024, 1, 1), cr.getRequestDate());
    }

    // ==================== update / delete ====================

    @Test
    @DisplayName("update: changeRequest 或 id 为 null 抛出业务异常")
    void update_null_throws() {
        assertThrows(BusinessException.class, () -> changeRequestService.update((ChangeRequest) null));
        ChangeRequest cr = ChangeRequest.builder().build();
        assertThrows(BusinessException.class, () -> changeRequestService.update(cr));
    }

    @Test
    @DisplayName("update: 变更请求不存在抛出业务异常")
    void update_notFound_throws() {
        when(changeRequestMapper.selectById(anyLong())).thenReturn(null);
        ChangeRequest cr = sampleCr(99L, "CR-2024-0001", STATUS_SUBMITTED);
        assertThrows(BusinessException.class, () -> changeRequestService.update(cr));
    }

    @Test
    @DisplayName("update: 正常更新成功")
    void update_success() {
        ChangeRequest existing = sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED);
        when(changeRequestMapper.selectById(1L)).thenReturn(existing);
        Mockito.doReturn(true).when(changeRequestService).updateById(any(ChangeRequest.class));

        ChangeRequest input = sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED);
        Result<?> result = changeRequestService.update(input);

        assertTrue(result.isSuccess());
        verify(changeRequestService, times(1)).updateById(any(ChangeRequest.class));
    }

    @Test
    @DisplayName("delete: 变更请求不存在抛出业务异常")
    void delete_notFound_throws() {
        when(changeRequestMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> changeRequestService.delete(99L));
    }

    @Test
    @DisplayName("delete: 正常删除成功")
    void delete_success() {
        ChangeRequest existing = sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED);
        when(changeRequestMapper.selectById(1L)).thenReturn(existing);
        Mockito.doReturn(true).when(changeRequestService).removeById(1L);

        Result<?> result = changeRequestService.delete(1L);

        assertTrue(result.isSuccess());
        verify(changeRequestService, times(1)).removeById(1L);
    }

    // ==================== listAll / getById / listByProject ====================

    @Test
    @DisplayName("listAll: 返回全部变更请求列表")
    void listAll_returnsList() {
        List<ChangeRequest> list = Arrays.asList(
                sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED),
                sampleCr(2L, "CR-2024-0002", STATUS_CLOSED));
        when(changeRequestMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<ChangeRequest>> result = changeRequestService.listAll();

        assertEquals(2, result.getData().size());
    }

    @Test
    @DisplayName("getById: 变更请求不存在抛出业务异常")
    void getById_notFound_throws() {
        when(changeRequestMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> changeRequestService.getById(99L));
    }

    @Test
    @DisplayName("getById: 返回变更请求详情")
    void getById_success() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);

        Result<ChangeRequest> result = changeRequestService.getById(1L);

        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("listByProject: projectId 为 null 返回空列表")
    void listByProject_nullId_returnsEmpty() {
        Result<List<ChangeRequest>> result = changeRequestService.listByProject(null);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("listByProject: 返回项目下的变更请求列表")
    void listByProject_returnsList() {
        List<ChangeRequest> list = Collections.singletonList(
                sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED));
        when(changeRequestMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<ChangeRequest>> result = changeRequestService.listByProject(100L);

        assertEquals(1, result.getData().size());
    }

    // ==================== submit ====================

    @Test
    @DisplayName("submit: 变更请求不存在抛出业务异常")
    void submit_notFound_throws() {
        when(changeRequestMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> changeRequestService.submit(99L));
    }

    @Test
    @DisplayName("submit: 非 SUBMITTED 状态不允许提交")
    void submit_wrongStatus_throws() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_UNDER_REVIEW);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> changeRequestService.submit(1L));
        assertTrue(ex.getMessage().contains("状态"));
    }

    @Test
    @DisplayName("submit: SUBMITTED 状态提交后转为 UNDER_REVIEW")
    void submit_success() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);
        Mockito.doReturn(true).when(changeRequestService).updateById(any(ChangeRequest.class));
        // 工作流不可用时不影响提交
        when(workflowServiceProvider.getIfAvailable()).thenReturn(null);

        Result<ChangeRequest> result = changeRequestService.submit(1L);

        assertTrue(result.isSuccess());
        assertEquals(STATUS_UNDER_REVIEW, cr.getStatus());
        verify(changeRequestService, times(1)).updateById(any(ChangeRequest.class));
    }

    // ==================== approve ====================

    @Test
    @DisplayName("approve: 变更请求不存在抛出业务异常")
    void approve_notFound_throws() {
        when(changeRequestMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> changeRequestService.approve(99L, "approver"));
    }

    @Test
    @DisplayName("approve: 非 UNDER_REVIEW 状态不允许审批")
    void approve_wrongStatus_throws() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> changeRequestService.approve(1L, "approver"));
        assertTrue(ex.getMessage().contains("状态"));
    }

    @Test
    @DisplayName("approve: UNDER_REVIEW 审批通过后转为 CCB_APPROVED 并记录基线变更")
    void approve_success() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_UNDER_REVIEW);
        cr.setImpactSchedule("延期 5 天");
        cr.setImpactCost("增加 10 万");
        cr.setImpactScope("新增 2 个站点");
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);
        Mockito.doReturn(true).when(changeRequestService).updateById(any(ChangeRequest.class));
        // processInstanceId 为 null 时 completeReviewTask 提前 return，不会调用 workflowServiceProvider

        Result<ChangeRequest> result = changeRequestService.approve(1L, "ccb-approver");

        assertTrue(result.isSuccess());
        assertEquals(STATUS_CCB_APPROVED, cr.getStatus());
        assertEquals("ccb-approver", cr.getApproverName());
        assertNotNull(cr.getApprovedAt());
        assertEquals(true, cr.getBaselineUpdated(), "审批通过后 baselineUpdated 应置 true");
        // 三个维度都有值时应记录 3 条基线变更
        verify(baselineHistoryService, times(1)).recordBaselineChange(
                eq(100L), eq(1L), eq("CR-2024-0001"), eq("SCHEDULE"),
                anyString(), anyString(), eq("延期 5 天"), anyString());
        verify(baselineHistoryService, times(1)).recordBaselineChange(
                eq(100L), eq(1L), eq("CR-2024-0001"), eq("COST"),
                anyString(), anyString(), eq("增加 10 万"), anyString());
        verify(baselineHistoryService, times(1)).recordBaselineChange(
                eq(100L), eq(1L), eq("CR-2024-0001"), eq("SCOPE"),
                anyString(), anyString(), eq("新增 2 个站点"), anyString());
    }

    @Test
    @DisplayName("approve: 影响维度为空时不记录对应基线变更")
    void approve_emptyImpactDimensions_noBaselineRecorded() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_UNDER_REVIEW);
        // impactSchedule/Cost/Scope 均为 null
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);
        Mockito.doReturn(true).when(changeRequestService).updateById(any(ChangeRequest.class));
        // processInstanceId 为 null 时 completeReviewTask 提前 return，不会调用 workflowServiceProvider

        changeRequestService.approve(1L, "approver");

        verify(baselineHistoryService, never()).recordBaselineChange(
                anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    // ==================== reject ====================

    @Test
    @DisplayName("reject: 变更请求不存在抛出业务异常")
    void reject_notFound_throws() {
        when(changeRequestMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> changeRequestService.reject(99L, "reason"));
    }

    @Test
    @DisplayName("reject: 非 UNDER_REVIEW 状态不允许驳回")
    void reject_wrongStatus_throws() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_CCB_APPROVED);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);

        assertThrows(BusinessException.class, () -> changeRequestService.reject(1L, "reason"));
    }

    @Test
    @DisplayName("reject: UNDER_REVIEW 驳回后转为 CCB_REJECTED")
    void reject_success() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_UNDER_REVIEW);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);
        Mockito.doReturn(true).when(changeRequestService).updateById(any(ChangeRequest.class));
        // processInstanceId 为 null 时 completeReviewTask 提前 return，不会调用 workflowServiceProvider

        Result<ChangeRequest> result = changeRequestService.reject(1L, "不符合规范");

        assertTrue(result.isSuccess());
        assertEquals(STATUS_CCB_REJECTED, cr.getStatus());
        assertNotNull(cr.getApprovedAt());
    }

    // ==================== implement / close ====================

    @Test
    @DisplayName("implement: 变更请求不存在抛出业务异常")
    void implement_notFound_throws() {
        when(changeRequestMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> changeRequestService.implement(99L));
    }

    @Test
    @DisplayName("implement: 非 CCB_APPROVED 状态不允许实施")
    void implement_wrongStatus_throws() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_SUBMITTED);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);

        assertThrows(BusinessException.class, () -> changeRequestService.implement(1L));
    }

    @Test
    @DisplayName("implement: CCB_APPROVED 实施后转为 IMPLEMENTING")
    void implement_success() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_CCB_APPROVED);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);
        Mockito.doReturn(true).when(changeRequestService).updateById(any(ChangeRequest.class));

        Result<ChangeRequest> result = changeRequestService.implement(1L);

        assertTrue(result.isSuccess());
        assertEquals(STATUS_IMPLEMENTING, cr.getStatus());
    }

    @Test
    @DisplayName("close: 变更请求不存在抛出业务异常")
    void close_notFound_throws() {
        when(changeRequestMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> changeRequestService.close(99L));
    }

    @Test
    @DisplayName("close: 正常关闭转为 CLOSED 并填充 closedAt")
    void close_success() {
        ChangeRequest cr = sampleCr(1L, "CR-2024-0001", STATUS_IMPLEMENTING);
        when(changeRequestMapper.selectById(1L)).thenReturn(cr);
        Mockito.doReturn(true).when(changeRequestService).updateById(any(ChangeRequest.class));

        Result<ChangeRequest> result = changeRequestService.close(1L);

        assertTrue(result.isSuccess());
        assertEquals(STATUS_CLOSED, cr.getStatus());
        assertNotNull(cr.getClosedAt());
    }

    // ==================== generateCrNo ====================

    @Test
    @DisplayName("generateCrNo: 当年无已有变更请求时生成 0001 序号")
    void generateCrNo_emptySequence() {
        when(changeRequestMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        String crNo = changeRequestService.generateCrNo();

        assertTrue(crNo.startsWith("CR-" + LocalDate.now().getYear() + "-"));
        assertTrue(crNo.endsWith("0001"));
    }

    @Test
    @DisplayName("generateCrNo: 当年已有 N 条时生成 N+1 序号")
    void generateCrNo_existingSequence() {
        when(changeRequestMapper.selectCount(any(Wrapper.class))).thenReturn(7L);

        String crNo = changeRequestService.generateCrNo();

        assertTrue(crNo.endsWith("0008"));
    }
}
