package com.dp.plat.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.admin.dto.ActivityItem;
import com.dp.plat.admin.dto.DashboardStats;
import com.dp.plat.admin.dto.ProjectTrendItem;
import com.dp.plat.admin.dto.TodoItem;
import com.dp.plat.admin.service.ReportService;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.warranty.entity.Warranty;
import com.dp.plat.asset.warranty.mapper.WarrantyMapper;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.system.entity.LoginLog;
import com.dp.plat.system.entity.SysOperLog;
import com.dp.plat.system.mapper.LoginLogMapper;
import com.dp.plat.system.mapper.SysOperLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ReportService} that aggregates data from the project,
 * asset, implementation and system-log modules.
 *
 * <p>Aggregate queries are kept efficient by delegating to MyBatis-Plus
 * {@code selectCount} where possible and by limiting the result set of list
 * queries used for grouping (only the last 6 months of projects are loaded for
 * the trend chart).</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final String PROJECT_STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String PROJECT_STATUS_COMPLETED = "COMPLETED";

    /** ImplTask statuses considered as "pending todo". */
    private static final List<String> TASK_OPEN_STATUSES =
            List.of("PENDING", "ACCEPTED", "IN_PROGRESS");

    private static final String ASSET_STATUS_IN_STOCK = "IN_STOCK";

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATETIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Trend window in months (inclusive of the current month). */
    private static final int TREND_MONTHS = 6;

    /** Look-ahead window for warranty expiry alerts. */
    private static final int WARRANTY_ALERT_DAYS = 30;

    private final ProjectMapper projectMapper;
    private final AssetMapper assetMapper;
    private final ImplTaskMapper implTaskMapper;
    private final WarrantyMapper warrantyMapper;
    private final SysOperLogMapper sysOperLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    public DashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();

        long projectTotal = projectMapper.selectCount(null);
        long projectInProgress = projectMapper.selectCount(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getStatus, PROJECT_STATUS_IN_PROGRESS));
        long assetInStock = assetMapper.selectCount(
                new LambdaQueryWrapper<Asset>()
                        .eq(Asset::getStatus, ASSET_STATUS_IN_STOCK));

        // Open todo tasks: scoped to current user when authenticated, else global
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<ImplTask> todoWrapper = new LambdaQueryWrapper<ImplTask>()
                .in(ImplTask::getStatus, TASK_OPEN_STATUSES);
        if (currentUserId != null) {
            todoWrapper.eq(ImplTask::getEngineerId, currentUserId);
        }
        long todoCount = implTaskMapper.selectCount(todoWrapper);

        // Month-bounded metrics
        long monthNewProject = projectMapper.selectCount(
                new LambdaQueryWrapper<Project>()
                        .ge(Project::getCreateTime, monthStart));

        long monthDelivery = projectMapper.selectCount(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getStatus, PROJECT_STATUS_COMPLETED)
                        .ge(Project::getActualEndDate, today.withDayOfMonth(1)));

        // Assets newly inbound this month (inboundTime falls back to createTime)
        long monthNewAsset = assetMapper.selectCount(
                new LambdaQueryWrapper<Asset>()
                        .ge(Asset::getInboundTime, monthStart));

        // Alerts: overdue open tasks + warranties expiring within 30 days
        long overdueTaskCount = implTaskMapper.selectCount(
                new LambdaQueryWrapper<ImplTask>()
                        .in(ImplTask::getStatus, TASK_OPEN_STATUSES)
                        .lt(ImplTask::getPlanEndDate, today));
        long warrantyAlertCount = warrantyMapper.selectCount(
                new LambdaQueryWrapper<Warranty>()
                        .ge(Warranty::getEndDate, today)
                        .le(Warranty::getEndDate, today.plusDays(WARRANTY_ALERT_DAYS)));
        long alertCount = overdueTaskCount + warrantyAlertCount;

        return DashboardStats.builder()
                .projectTotal(projectTotal)
                .projectInProgress(projectInProgress)
                .assetInStock(assetInStock)
                .todoCount(todoCount)
                .monthNewProject(monthNewProject)
                .monthDelivery(monthDelivery)
                .monthNewAsset(monthNewAsset)
                .alertCount(alertCount)
                .build();
    }

    @Override
    public List<ProjectTrendItem> getProjectTrend() {
        // Load only projects created in the last 6 months to keep the
        // in-memory grouping cheap; grouping is done in Java to remain
        // portable across MySQL / PostgreSQL (no DATE_FORMAT dependency).
        LocalDateTime trendStart = LocalDate.now()
                .withDayOfMonth(1)
                .minusMonths(TREND_MONTHS - 1L)
                .atStartOfDay();
        List<Project> recent = projectMapper.selectList(
                new LambdaQueryWrapper<Project>()
                        .ge(Project::getCreateTime, trendStart));

        // Group by month + status, preserving month asc ordering
        Map<String, Map<String, Long>> grouped = new LinkedHashMap<>();
        // Pre-seed the last 6 months so the chart always has full x-axis
        LocalDate cursor = LocalDate.now().withDayOfMonth(1).minusMonths(TREND_MONTHS - 1L);
        for (int i = 0; i < TREND_MONTHS; i++) {
            grouped.put(cursor.format(MONTH_FMT), new LinkedHashMap<>());
            cursor = cursor.plusMonths(1);
        }
        for (Project p : recent) {
            if (p.getCreateTime() == null || !StringUtils.hasText(p.getStatus())) {
                continue;
            }
            String month = p.getCreateTime().format(MONTH_FMT);
            grouped.computeIfAbsent(month, k -> new LinkedHashMap<>())
                    .merge(p.getStatus(), 1L, Long::sum);
        }

        List<ProjectTrendItem> items = new ArrayList<>();
        grouped.forEach((month, statusMap) ->
                statusMap.forEach((status, count) ->
                        items.add(ProjectTrendItem.builder()
                                .month(month)
                                .status(status)
                                .count(count)
                                .build())));
        return items;
    }

    @Override
    public List<TodoItem> getTodoList(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<ImplTask> wrapper = new LambdaQueryWrapper<ImplTask>()
                .in(ImplTask::getStatus, TASK_OPEN_STATUSES);
        if (currentUserId != null) {
            wrapper.eq(ImplTask::getEngineerId, currentUserId);
        }
        wrapper.orderByAsc(ImplTask::getPlanEndDate).last("LIMIT " + limit);
        List<ImplTask> tasks = implTaskMapper.selectList(wrapper);
        if (tasks.isEmpty()) {
            return List.of();
        }

        // Bulk-load the related projects to avoid N+1
        List<Long> projectIds = tasks.stream()
                .map(ImplTask::getProjectId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Project> projectById = projectIds.isEmpty()
                ? Map.of()
                : projectMapper.selectList(
                        new LambdaQueryWrapper<Project>().in(Project::getId, projectIds))
                        .stream()
                        .collect(Collectors.toMap(Project::getId, p -> p, (a, b) -> a));

        return tasks.stream()
                .map(task -> toTodoItem(task, projectById.get(task.getProjectId())))
                .toList();
    }

    @Override
    public List<ActivityItem> getRecentActivities(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        // Pull the latest N oper logs and login logs in parallel, then merge
        List<SysOperLog> operLogs = sysOperLogMapper.selectList(
                new LambdaQueryWrapper<SysOperLog>()
                        .orderByDesc(SysOperLog::getOperTime)
                        .last("LIMIT " + limit));
        List<LoginLog> loginLogs = loginLogMapper.selectList(
                new LambdaQueryWrapper<LoginLog>()
                        .orderByDesc(LoginLog::getLoginTime)
                        .last("LIMIT " + limit));

        List<ActivityItem> items = new ArrayList<>(operLogs.size() + loginLogs.size());
        for (SysOperLog oper : operLogs) {
            items.add(ActivityItem.builder()
                    .id(oper.getId())
                    .type("OPER")
                    .description(StringUtils.hasText(oper.getTitle())
                            ? oper.getTitle()
                            : "操作日志")
                    .operatorName(oper.getOperName())
                    .createdAt(oper.getOperTime() == null ? null : oper.getOperTime().format(DATETIME_FMT))
                    .bizType(oper.getBusinessType() == null ? null : String.valueOf(oper.getBusinessType()))
                    .build());
        }
        for (LoginLog login : loginLogs) {
            items.add(ActivityItem.builder()
                    .id(login.getId())
                    .type("LOGIN")
                    .description(login.getStatus() != null && "SUCCESS".equals(login.getStatus())
                            ? "用户登录成功"
                            : "用户登录失败")
                    .operatorName(login.getUsername())
                    .createdAt(login.getLoginTime() == null ? null : login.getLoginTime().format(DATETIME_FMT))
                    .build());
        }
        items.sort(Comparator.nullsLast(
                Comparator.comparing(ActivityItem::getCreatedAt, Comparator.reverseOrder())));
        if (items.size() > limit) {
            return new ArrayList<>(items.subList(0, limit));
        }
        return items;
    }

    private TodoItem toTodoItem(ImplTask task, Project project) {
        return TodoItem.builder()
                .id(task.getId())
                .title(task.getTaskName())
                .type("TASK")
                .priority(derivePriority(task))
                .assigneeName(task.getEngineerName())
                .deadline(task.getPlanEndDate() == null ? null : task.getPlanEndDate().format(DATE_FMT))
                .projectCode(project == null ? null : project.getProjectCode())
                .projectName(project == null ? null : project.getProjectName())
                .status(task.getStatus())
                .build();
    }

    /**
     * Derive a coarse priority label from the task's planned end date:
     * overdue tasks are HIGH, due within 3 days are HIGH, due within 7 days
     * are NORMAL, otherwise LOW.
     */
    private String derivePriority(ImplTask task) {
        if (task.getPlanEndDate() == null) {
            return "NORMAL";
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), task.getPlanEndDate());
        if (days < 0) {
            return "HIGH";
        }
        if (days <= 3) {
            return "HIGH";
        }
        if (days <= 7) {
            return "NORMAL";
        }
        return "LOW";
    }
}
