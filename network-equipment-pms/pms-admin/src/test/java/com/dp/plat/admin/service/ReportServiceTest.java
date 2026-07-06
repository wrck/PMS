package com.dp.plat.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.admin.dto.ActivityItem;
import com.dp.plat.admin.dto.DashboardStats;
import com.dp.plat.admin.dto.ProjectTrendItem;
import com.dp.plat.admin.dto.TodoItem;
import com.dp.plat.admin.service.impl.ReportServiceImpl;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.warranty.mapper.WarrantyMapper;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.system.entity.LoginLog;
import com.dp.plat.system.entity.SysOperLog;
import com.dp.plat.system.mapper.LoginLogMapper;
import com.dp.plat.system.mapper.SysOperLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pure-unit Mockito tests for {@link ReportServiceImpl}.
 *
 * <p>Each mapper is mocked so the aggregation logic in the service can be
 * verified in isolation without a database. The tests cover the four
 * dashboard endpoints added in Task 31.</p>
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private AssetMapper assetMapper;
    @Mock
    private ImplTaskMapper implTaskMapper;
    @Mock
    private WarrantyMapper warrantyMapper;
    @Mock
    private SysOperLogMapper sysOperLogMapper;
    @Mock
    private LoginLogMapper loginLogMapper;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(
                projectMapper, assetMapper, implTaskMapper,
                warrantyMapper, sysOperLogMapper, loginLogMapper);
    }

    @Test
    @DisplayName("getDashboardStats 聚合项目/资产/任务/质保告警数量")
    void getDashboardStats_aggregatesAllCounts() {
        // Arrange — selectCount returns Long per MyBatis-Plus signature.
        // All selectCount calls on the same mapper are stubbed to the same
        // value because the aggregate semantics (total / in-progress / month)
        // are exercised end-to-end by integration tests; here we focus on
        // the aggregation math in the service.
        when(projectMapper.selectCount(any())).thenReturn(15L);
        when(assetMapper.selectCount(any())).thenReturn(8L);
        when(implTaskMapper.selectCount(any())).thenReturn(3L);
        when(warrantyMapper.selectCount(any())).thenReturn(2L);

        // Act
        DashboardStats stats = reportService.getDashboardStats();

        // Assert
        assertNotNull(stats);
        assertEquals(15L, stats.getProjectTotal());
        assertEquals(15L, stats.getProjectInProgress());
        assertEquals(8L, stats.getAssetInStock());
        assertEquals(3L, stats.getTodoCount());
        assertEquals(15L, stats.getMonthNewProject());
        assertEquals(15L, stats.getMonthDelivery());
        assertEquals(8L, stats.getMonthNewAsset());
        // alertCount = overdueTaskCount (3) + warrantyAlertCount (2)
        assertEquals(5L, stats.getAlertCount());
    }

    @Test
    @DisplayName("getProjectTrend 按月份+状态分组并预填充 6 个月")
    void getProjectTrend_groupsByMonthAndStatus() {
        // Build projects spanning the last two months
        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime recent = thisMonth.atStartOfDay().plusDays(2);
        LocalDateTime lastMonth = thisMonth.minusMonths(1).atStartOfDay().plusDays(3);

        Project p1 = Project.builder()
                .projectCode("PMS-A").projectName("A").projectType("NETWORK_DEVICE")
                .customerName("c").status("IN_PROGRESS").build();
        p1.setCreateTime(recent);
        Project p2 = Project.builder()
                .projectCode("PMS-B").projectName("B").projectType("NETWORK_DEVICE")
                .customerName("c").status("COMPLETED").build();
        p2.setCreateTime(recent);
        Project p3 = Project.builder()
                .projectCode("PMS-C").projectName("C").projectType("NETWORK_DEVICE")
                .customerName("c").status("IN_PROGRESS").build();
        p3.setCreateTime(lastMonth);

        when(projectMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(p1, p2, p3));

        List<ProjectTrendItem> items = reportService.getProjectTrend();

        assertNotNull(items);
        assertFalse(items.isEmpty());
        // Verify the recent-month groupings exist
        String recentMonth = recent.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        String lastMonthStr = lastMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        long recentInProgress = items.stream()
                .filter(i -> recentMonth.equals(i.getMonth()) && "IN_PROGRESS".equals(i.getStatus()))
                .mapToLong(ProjectTrendItem::getCount).sum();
        long recentCompleted = items.stream()
                .filter(i -> recentMonth.equals(i.getMonth()) && "COMPLETED".equals(i.getStatus()))
                .mapToLong(ProjectTrendItem::getCount).sum();
        long lastInProgress = items.stream()
                .filter(i -> lastMonthStr.equals(i.getMonth()) && "IN_PROGRESS".equals(i.getStatus()))
                .mapToLong(ProjectTrendItem::getCount).sum();
        assertEquals(1L, recentInProgress);
        assertEquals(1L, recentCompleted);
        assertEquals(1L, lastInProgress);
    }

    @Test
    @DisplayName("getTodoList 返回任务并填充项目信息")
    void getTodoList_returnsItemsWithProjectInfo() {
        Project project = Project.builder()
                .projectCode("PMS-X").projectName("Project X").projectType("NETWORK_DEVICE")
                .customerName("c").status("IN_PROGRESS").build();
        project.setId(100L);

        ImplTask task = ImplTask.builder()
                .projectId(100L).taskName("实施任务 A").taskType("OEM")
                .status("IN_PROGRESS").engineerName("Alice")
                .planEndDate(LocalDate.now().plusDays(2)).build();
        task.setId(1L);

        when(implTaskMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(task));
        when(projectMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(project));

        List<TodoItem> todos = reportService.getTodoList(5);

        assertEquals(1, todos.size());
        TodoItem item = todos.get(0);
        assertEquals(1L, item.getId());
        assertEquals("实施任务 A", item.getTitle());
        assertEquals("Project X", item.getProjectName());
        assertEquals("PMS-X", item.getProjectCode());
        assertEquals("Alice", item.getAssigneeName());
        assertEquals("TASK", item.getType());
        assertEquals("IN_PROGRESS", item.getStatus());
        assertNotNull(item.getDeadline());
        // Plan end date is 2 days out → priority HIGH (within 3 days)
        assertEquals("HIGH", item.getPriority());
    }

    @Test
    @DisplayName("getRecentActivities 合并操作日志与登录日志并按时间倒序")
    void getRecentActivities_mergesAndSortsByTimeDesc() {
        SysOperLog operLog = new SysOperLog();
        operLog.setId(1L);
        operLog.setTitle("创建项目");
        operLog.setOperName("admin");
        operLog.setBusinessType(1);
        operLog.setOperTime(LocalDateTime.of(2026, 7, 6, 10, 0, 0));

        LoginLog loginLog = LoginLog.builder()
                .id(2L)
                .username("alice")
                .loginTime(LocalDateTime.of(2026, 7, 6, 9, 0, 0))
                .status("SUCCESS")
                .build();

        when(sysOperLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(operLog));
        when(loginLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(loginLog));

        List<ActivityItem> items = reportService.getRecentActivities(10);

        assertEquals(2, items.size());
        // The oper log (10:00) is more recent than the login log (09:00)
        ActivityItem first = items.get(0);
        assertEquals("OPER", first.getType());
        assertEquals("创建项目", first.getDescription());
        assertEquals("admin", first.getOperatorName());
        assertTrue(first.getCreatedAt().contains("2026-07-06 10:00:00"));

        ActivityItem second = items.get(1);
        assertEquals("LOGIN", second.getType());
        assertEquals("alice", second.getOperatorName());
    }

    @Test
    @DisplayName("getTodoList limit<=0 返回空列表")
    void getTodoList_nonPositiveLimitReturnsEmpty() {
        assertTrue(reportService.getTodoList(0).isEmpty());
        assertTrue(reportService.getTodoList(-1).isEmpty());
    }

    @Test
    @DisplayName("getRecentActivities limit<=0 返回空列表")
    void getRecentActivities_nonPositiveLimitReturnsEmpty() {
        assertTrue(reportService.getRecentActivities(0).isEmpty());
        assertTrue(reportService.getRecentActivities(-1).isEmpty());
    }

    @Test
    @DisplayName("getRecentActivities 在超过 limit 时截断")
    void getRecentActivities_truncatesToLimit() {
        SysOperLog oper1 = new SysOperLog();
        oper1.setId(1L);
        oper1.setTitle("op1");
        oper1.setOperTime(LocalDateTime.of(2026, 7, 6, 10, 0, 0));

        SysOperLog oper2 = new SysOperLog();
        oper2.setId(2L);
        oper2.setTitle("op2");
        oper2.setOperTime(LocalDateTime.of(2026, 7, 6, 11, 0, 0));

        LoginLog login1 = LoginLog.builder()
                .id(3L).username("u1")
                .loginTime(LocalDateTime.of(2026, 7, 6, 9, 0, 0))
                .status("SUCCESS").build();

        when(sysOperLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(oper1, oper2));
        when(loginLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(login1));

        List<ActivityItem> items = reportService.getRecentActivities(2);
        assertEquals(2, items.size());
    }
}
