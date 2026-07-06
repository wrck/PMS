package com.dp.plat.governance.issue.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.change.entity.ChangeRequest;
import com.dp.plat.governance.change.service.IChangeRequestService;
import com.dp.plat.governance.issue.entity.Issue;
import com.dp.plat.governance.issue.mapper.IssueMapper;
import com.dp.plat.governance.issue.service.impl.IssueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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
 * Unit tests for {@link IssueServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class IssueServiceImplTest {

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_RESOLVED = "RESOLVED";
    private static final String STATUS_CLOSED = "CLOSED";

    @Mock
    private IssueMapper issueMapper;

    @Mock
    private IChangeRequestService changeRequestService;

    private IssueServiceImpl issueService;

    @BeforeEach
    void setUp() {
        issueService = Mockito.spy(new IssueServiceImpl(changeRequestService));
        ReflectionTestUtils.setField(issueService, "baseMapper", issueMapper);
    }

    private Issue sampleIssue(Long id, String issueNo, String status, String priority) {
        Issue issue = Issue.builder()
                .issueNo(issueNo)
                .projectId(100L)
                .description("问题描述")
                .priority(priority)
                .status(status)
                .build();
        issue.setId(id);
        return issue;
    }

    // ==================== create ====================

    @Test
    @DisplayName("create: issue 为 null 抛出业务异常")
    void create_null_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> issueService.create(null));
        assertTrue(ex.getMessage().contains("不能为空"));
    }

    @Test
    @DisplayName("create: description 为空抛出业务异常")
    void create_emptyDescription_throws() {
        Issue issue = Issue.builder().projectId(100L).build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> issueService.create(issue));
        assertTrue(ex.getMessage().contains("描述"));
    }

    @Test
    @DisplayName("create: 正常创建，生成 issueNo、状态置 OPEN、缺省 priority 和 targetResolveDate")
    void create_normal_success() {
        Issue issue = Issue.builder()
                .projectId(100L)
                .description("网络故障")
                .build();
        when(issueMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        Mockito.doReturn(true).when(issueService).save(any(Issue.class));

        Result<Issue> result = issueService.create(issue);

        assertTrue(result.isSuccess());
        assertNotNull(issue.getIssueNo(), "issueNo 应自动生成");
        assertEquals(STATUS_OPEN, issue.getStatus());
        assertEquals("MEDIUM", issue.getPriority(), "缺省 priority 应为 MEDIUM");
        assertNotNull(issue.getTargetResolveDate(), "缺省 targetResolveDate 应为今天+7天");
        assertEquals(LocalDate.now().plusDays(7), issue.getTargetResolveDate());
    }

    @Test
    @DisplayName("create: 显式 priority 和 targetResolveDate 时不被覆盖")
    void create_keepsExplicitFields() {
        LocalDate target = LocalDate.of(2024, 12, 31);
        Issue issue = Issue.builder()
                .projectId(100L)
                .description("故障")
                .priority("HIGH")
                .targetResolveDate(target)
                .build();
        when(issueMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        Mockito.doReturn(true).when(issueService).save(any(Issue.class));

        issueService.create(issue);

        assertEquals("HIGH", issue.getPriority());
        assertEquals(target, issue.getTargetResolveDate());
    }

    // ==================== update ====================

    @Test
    @DisplayName("update: issue 或 id 为 null 抛出业务异常")
    void update_null_throws() {
        assertThrows(BusinessException.class, () -> issueService.update((Issue) null));
        Issue issue = Issue.builder().build();
        assertThrows(BusinessException.class, () -> issueService.update(issue));
    }

    @Test
    @DisplayName("update: 问题不存在抛出业务异常")
    void update_notFound_throws() {
        when(issueMapper.selectById(anyLong())).thenReturn(null);
        Issue issue = sampleIssue(99L, "ISSUE-2024-0001", STATUS_OPEN, "MEDIUM");
        assertThrows(BusinessException.class, () -> issueService.update(issue));
    }

    @Test
    @DisplayName("update: 正常更新成功")
    void update_success() {
        Issue existing = sampleIssue(1L, "ISSUE-2024-0001", STATUS_OPEN, "MEDIUM");
        when(issueMapper.selectById(1L)).thenReturn(existing);
        Mockito.doReturn(true).when(issueService).updateById(any(Issue.class));

        Issue input = sampleIssue(1L, "ISSUE-2024-0001", STATUS_OPEN, "HIGH");
        Result<?> result = issueService.update(input);

        assertTrue(result.isSuccess());
        verify(issueService, times(1)).updateById(any(Issue.class));
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete: 问题不存在抛出业务异常")
    void delete_notFound_throws() {
        when(issueMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> issueService.delete(99L));
    }

    @Test
    @DisplayName("delete: 正常删除成功")
    void delete_success() {
        Issue existing = sampleIssue(1L, "ISSUE-2024-0001", STATUS_OPEN, "MEDIUM");
        when(issueMapper.selectById(1L)).thenReturn(existing);
        Mockito.doReturn(true).when(issueService).removeById(1L);

        Result<?> result = issueService.delete(1L);

        assertTrue(result.isSuccess());
        verify(issueService, times(1)).removeById(1L);
    }

    // ==================== listAll / getById / listByProject ====================

    @Test
    @DisplayName("listAll: 返回全部问题列表")
    void listAll_returnsList() {
        List<Issue> list = Arrays.asList(
                sampleIssue(1L, "ISSUE-2024-0001", STATUS_OPEN, "MEDIUM"),
                sampleIssue(2L, "ISSUE-2024-0002", STATUS_CLOSED, "HIGH"));
        when(issueMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<Issue>> result = issueService.listAll();

        assertEquals(2, result.getData().size());
    }

    @Test
    @DisplayName("getById: 问题不存在抛出业务异常")
    void getById_notFound_throws() {
        when(issueMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> issueService.getById(99L));
    }

    @Test
    @DisplayName("getById: 返回问题详情")
    void getById_success() {
        Issue issue = sampleIssue(1L, "ISSUE-2024-0001", STATUS_OPEN, "MEDIUM");
        when(issueMapper.selectById(1L)).thenReturn(issue);

        Result<Issue> result = issueService.getById(1L);

        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("listByProject: projectId 为 null 返回空列表")
    void listByProject_nullId_returnsEmpty() {
        Result<List<Issue>> result = issueService.listByProject(null);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("listByProject: 返回项目下的问题列表")
    void listByProject_returnsList() {
        List<Issue> list = Collections.singletonList(
                sampleIssue(1L, "ISSUE-2024-0001", STATUS_OPEN, "MEDIUM"));
        when(issueMapper.selectList(any(Wrapper.class))).thenReturn(list);

        Result<List<Issue>> result = issueService.listByProject(100L);

        assertEquals(1, result.getData().size());
    }

    // ==================== assign ====================

    @Test
    @DisplayName("assign: 问题不存在抛出业务异常")
    void assign_notFound_throws() {
        when(issueMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> issueService.assign(99L, 1L, "alice"));
    }

    @Test
    @DisplayName("assign: OPEN 状态自动转为 IN_PROGRESS")
    void assign_openTransitionsToInProgress() {
        Issue issue = sampleIssue(1L, "ISSUE-2024-0001", STATUS_OPEN, "MEDIUM");
        when(issueMapper.selectById(1L)).thenReturn(issue);
        Mockito.doReturn(true).when(issueService).updateById(any(Issue.class));

        Result<Issue> result = issueService.assign(1L, 10L, "alice");

        assertEquals(10L, result.getData().getAssigneeId());
        assertEquals("alice", result.getData().getAssigneeName());
        assertEquals(STATUS_IN_PROGRESS, result.getData().getStatus(), "OPEN 分配后应转为 IN_PROGRESS");
    }

    @Test
    @DisplayName("assign: 非 OPEN 状态不改变状态")
    void assign_nonOpen_keepsStatus() {
        Issue issue = sampleIssue(1L, "ISSUE-2024-0001", STATUS_RESOLVED, "MEDIUM");
        when(issueMapper.selectById(1L)).thenReturn(issue);
        Mockito.doReturn(true).when(issueService).updateById(any(Issue.class));

        Result<Issue> result = issueService.assign(1L, 10L, "alice");

        assertEquals(STATUS_RESOLVED, result.getData().getStatus(), "非 OPEN 状态不应改变");
    }

    // ==================== resolve / close ====================

    @Test
    @DisplayName("resolve: 问题不存在抛出业务异常")
    void resolve_notFound_throws() {
        when(issueMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> issueService.resolve(99L, "已解决"));
    }

    @Test
    @DisplayName("resolve: 置为 RESOLVED 并填充 resolution 和 resolvedAt")
    void resolve_success() {
        Issue issue = sampleIssue(1L, "ISSUE-2024-0001", STATUS_IN_PROGRESS, "MEDIUM");
        when(issueMapper.selectById(1L)).thenReturn(issue);
        Mockito.doReturn(true).when(issueService).updateById(any(Issue.class));

        Result<Issue> result = issueService.resolve(1L, "已修复");

        assertEquals(STATUS_RESOLVED, result.getData().getStatus());
        assertEquals("已修复", result.getData().getResolution());
        assertNotNull(result.getData().getResolvedAt());
    }

    @Test
    @DisplayName("close: 问题不存在抛出业务异常")
    void close_notFound_throws() {
        when(issueMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> issueService.close(99L));
    }

    @Test
    @DisplayName("close: 置为 CLOSED 并填充 closedAt")
    void close_success() {
        Issue issue = sampleIssue(1L, "ISSUE-2024-0001", STATUS_RESOLVED, "MEDIUM");
        when(issueMapper.selectById(1L)).thenReturn(issue);
        Mockito.doReturn(true).when(issueService).updateById(any(Issue.class));

        Result<Issue> result = issueService.close(1L);

        assertEquals(STATUS_CLOSED, result.getData().getStatus());
        assertNotNull(result.getData().getClosedAt());
    }

    // ==================== escalate ====================

    @Test
    @DisplayName("escalate: 问题不存在抛出业务异常")
    void escalate_notFound_throws() {
        when(issueMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> issueService.escalate(99L));
    }

    @Test
    @DisplayName("escalate: 创建变更请求并返回")
    void escalate_createsChangeRequest() {
        Issue issue = sampleIssue(1L, "ISSUE-2024-0001", STATUS_OPEN, "HIGH");
        issue.setRaisedBy(100L);
        issue.setRaisedByName("tester");
        when(issueMapper.selectById(1L)).thenReturn(issue);
        ChangeRequest createdCr = ChangeRequest.builder().title("由ISSUE-2024-0001升级的变更请求").build();
        createdCr.setId(50L);
        when(changeRequestService.create(any(ChangeRequest.class))).thenReturn(Result.ok(createdCr));

        Result<?> result = issueService.escalate(1L);

        assertTrue(result.isSuccess());
        verify(changeRequestService, times(1)).create(any(ChangeRequest.class));
    }

    // ==================== generateIssueNo ====================

    @Test
    @DisplayName("generateIssueNo: 当年无已有问题时生成 0001 序号")
    void generateIssueNo_emptySequence() {
        when(issueMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        String issueNo = issueService.generateIssueNo();

        assertTrue(issueNo.startsWith("ISSUE-" + LocalDate.now().getYear() + "-"));
        assertTrue(issueNo.endsWith("0001"));
    }

    @Test
    @DisplayName("generateIssueNo: 当年已有 N 条时生成 N+1 序号")
    void generateIssueNo_existingSequence() {
        when(issueMapper.selectCount(any(Wrapper.class))).thenReturn(5L);

        String issueNo = issueService.generateIssueNo();

        assertTrue(issueNo.endsWith("0006"));
    }
}
