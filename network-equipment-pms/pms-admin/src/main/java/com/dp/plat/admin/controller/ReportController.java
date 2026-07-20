package com.dp.plat.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.admin.dto.ActivityItem;
import com.dp.plat.admin.dto.DashboardStats;
import com.dp.plat.admin.dto.ProjectTrendItem;
import com.dp.plat.admin.dto.TodoItem;
import com.dp.plat.admin.service.ReportService;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetCategory;
import com.dp.plat.asset.entity.AssetModel;
import com.dp.plat.asset.mapper.AssetCategoryMapper;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.mapper.AssetModelMapper;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.entity.AgentScore;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.AgentScoreMapper;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Report statistics controller.
 *
 * <p>Aggregates data across the project / asset / implementation modules for
 * the frontend report dashboard. Each endpoint returns a flat {@link Map} so
 * the frontend can consume the data directly without extra DTOs.</p>
 */
@Tag(name = "报表统计", description = "Report statistics APIs")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private static final String PROJECT_STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String PROJECT_STATUS_COMPLETED = "COMPLETED";
    private static final String TASK_STATUS_COMPLETED = "COMPLETED";
    private static final String TASK_TYPE_OEM = "OEM";
    private static final String TASK_TYPE_AGENT = "AGENT";
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ProjectMapper projectMapper;
    private final AssetMapper assetMapper;
    private final AssetCategoryMapper assetCategoryMapper;
    private final AssetModelMapper assetModelMapper;
    private final ImplTaskMapper implTaskMapper;
    private final AgentMapper agentMapper;
    private final AgentScoreMapper agentScoreMapper;
    private final ReportService reportService;

    @Operation(summary = "项目交付统计")
    @GetMapping("/delivery")
    public Result<Map<String, Object>> deliveryStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        LocalDate start = StringUtils.hasText(startDate) ? LocalDate.parse(startDate) : null;
        LocalDate end = StringUtils.hasText(endDate) ? LocalDate.parse(endDate) : null;
        if (start != null) {
            wrapper.ge(Project::getCreateTime, start.atStartOfDay());
        }
        if (end != null) {
            wrapper.le(Project::getCreateTime, end.atTime(23, 59, 59));
        }
        List<Project> projects = projectMapper.selectList(wrapper);

        // Group initiated by month of createTime
        Map<String, Long> initiatedByMonth = projects.stream()
                .filter(p -> p.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getCreateTime().format(MONTH_FMT),
                        TreeMap::new,
                        Collectors.counting()));

        // Group completed by month of actualEndDate (only for COMPLETED projects)
        Map<String, Long> completedByMonth = projects.stream()
                .filter(p -> PROJECT_STATUS_COMPLETED.equals(p.getStatus()) && p.getActualEndDate() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getActualEndDate().format(MONTH_FMT),
                        TreeMap::new,
                        Collectors.counting()));

        // Build a unified sorted month list
        Set<String> monthSet = new TreeSet<>();
        monthSet.addAll(initiatedByMonth.keySet());
        monthSet.addAll(completedByMonth.keySet());

        List<Map<String, Object>> monthlyStats = new ArrayList<>();
        long totalInitiated = 0L;
        long totalCompletedInRange = 0L;
        for (String month : monthSet) {
            long initiated = initiatedByMonth.getOrDefault(month, 0L);
            long completed = completedByMonth.getOrDefault(month, 0L);
            totalInitiated += initiated;
            totalCompletedInRange += completed;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("month", month);
            row.put("initiated", initiated);
            row.put("completed", completed);
            monthlyStats.add(row);
        }

        long totalInProgress = projects.stream()
                .filter(p -> PROJECT_STATUS_IN_PROGRESS.equals(p.getStatus()))
                .count();
        long totalCompleted = projects.stream()
                .filter(p -> PROJECT_STATUS_COMPLETED.equals(p.getStatus()))
                .count();

        // Average cycle days = avg(actualEndDate - createTime) for completed projects with actualEndDate
        double avgCycleDays = projects.stream()
                .filter(p -> PROJECT_STATUS_COMPLETED.equals(p.getStatus())
                        && p.getActualEndDate() != null
                        && p.getCreateTime() != null)
                .mapToLong(p -> ChronoUnit.DAYS.between(p.getCreateTime().toLocalDate(), p.getActualEndDate()))
                .average()
                .orElse(0.0);

        // Delay rate: ratio of (initiated - completed) over initiated, capped at 0
        long pendingCount = Math.max(totalInitiated - totalCompletedInRange, 0);
        double delayRate = totalInitiated > 0
                ? (pendingCount * 100.0 / totalInitiated)
                : 0.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("monthlyStats", monthlyStats);
        result.put("totalInitiated", totalInitiated);
        result.put("totalInProgress", totalInProgress);
        result.put("totalCompleted", totalCompleted);
        result.put("avgCycleDays", BigDecimal.valueOf(avgCycleDays).setScale(1, RoundingMode.HALF_UP));
        result.put("delayRate", BigDecimal.valueOf(delayRate).setScale(1, RoundingMode.HALF_UP));
        return Result.ok(result);
    }

    @Operation(summary = "设备资产统计")
    @GetMapping("/asset")
    public Result<Map<String, Object>> assetStats() {
        List<Asset> assets = assetMapper.selectList(null);

        // byStatus: count group by status
        Map<String, Long> byStatusRaw = assets.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus() == null ? "UNKNOWN" : a.getStatus(),
                        TreeMap::new,
                        Collectors.counting()));
        // Materialize the well-known statuses so the frontend always has them
        Map<String, Long> byStatus = new LinkedHashMap<>();
        byStatus.put("IN_STOCK", byStatusRaw.getOrDefault("IN_STOCK", 0L));
        byStatus.put("ALLOCATED", byStatusRaw.getOrDefault("ALLOCATED", 0L));
        byStatus.put("IN_TRANSIT", byStatusRaw.getOrDefault("IN_TRANSIT", 0L));
        byStatus.put("SCRAPPED", byStatusRaw.getOrDefault("SCRAPPED", 0L));
        byStatusRaw.forEach((k, v) -> {
            if (!byStatus.containsKey(k)) {
                byStatus.put(k, v);
            }
        });

        // byCategory: count group by category name
        List<AssetCategory> categories = assetCategoryMapper.selectList(null);
        Map<Long, String> categoryNameById = categories.stream()
                .collect(Collectors.toMap(AssetCategory::getId,
                        c -> c.getCategoryName() == null ? ("category-" + c.getId()) : c.getCategoryName(),
                        (a, b) -> a));
        Map<String, Long> byCategory = assets.stream()
                .filter(a -> a.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        a -> categoryNameById.getOrDefault(a.getCategoryId(), "未分类"),
                        TreeMap::new,
                        Collectors.counting()));

        // totalValue: sum of standardPrice from pms_asset_model joined by modelId
        List<AssetModel> models = assetModelMapper.selectList(null);
        Map<Long, BigDecimal> priceByModelId = models.stream()
                .filter(m -> m.getId() != null && m.getStandardPrice() != null)
                .collect(Collectors.toMap(AssetModel::getId, AssetModel::getStandardPrice, (a, b) -> a));
        BigDecimal totalValue = assets.stream()
                .filter(a -> a.getModelId() != null && priceByModelId.containsKey(a.getModelId()))
                .map(a -> priceByModelId.get(a.getModelId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long total = assets.size();
        long inStock = byStatus.getOrDefault("IN_STOCK", 0L);
        long allocated = byStatus.getOrDefault("ALLOCATED", 0L);
        long inTransfer = byStatus.getOrDefault("IN_TRANSIT", 0L);
        long scrapped = byStatus.getOrDefault("SCRAPPED", 0L);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("byStatus", byStatus);
        result.put("byCategory", byCategory);
        result.put("totalValue", totalValue);
        result.put("total", total);
        result.put("inStock", inStock);
        result.put("allocated", allocated);
        result.put("inTransfer", inTransfer);
        result.put("scrapped", scrapped);
        return Result.ok(result);
    }

    @Operation(summary = "实施效能统计")
    @GetMapping("/implementation")
    public Result<Map<String, Object>> implementationStats() {
        List<ImplTask> tasks = implTaskMapper.selectList(null);

        // Monthly efficiency for completed tasks
        Map<String, List<ImplTask>> tasksByMonth = tasks.stream()
                .filter(t -> TASK_STATUS_COMPLETED.equals(t.getStatus()) && t.getActualEndDate() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getActualEndDate().format(MONTH_FMT),
                        TreeMap::new,
                        Collectors.toList()));
        List<Map<String, Object>> efficiency = new ArrayList<>();
        for (Map.Entry<String, List<ImplTask>> entry : tasksByMonth.entrySet()) {
            List<ImplTask> monthTasks = entry.getValue();
            double avgDuration = monthTasks.stream()
                    .filter(t -> t.getActualStartDate() != null && t.getActualEndDate() != null)
                    .mapToLong(t -> ChronoUnit.DAYS.between(t.getActualStartDate(), t.getActualEndDate()))
                    .average()
                    .orElse(0.0);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("month", entry.getKey());
            row.put("completedCount", monthTasks.size());
            row.put("avgDurationDays", BigDecimal.valueOf(avgDuration).setScale(1, RoundingMode.HALF_UP));
            efficiency.add(row);
        }

        // Efficiency by task type (OEM vs AGENT): completion rate + avg duration
        Map<String, Object> efficiencyByType = new LinkedHashMap<>();
        efficiencyByType.put("OEM", buildTypeStats(tasks, TASK_TYPE_OEM));
        efficiencyByType.put("AGENT", buildTypeStats(tasks, TASK_TYPE_AGENT));

        // Agent ranking — top 10 by overallScore
        List<Agent> agents = agentMapper.selectList(null);
        List<AgentScore> allScores = agentScoreMapper.selectList(null);
        Map<Long, List<AgentScore>> scoresByAgent = allScores.stream()
                .filter(s -> s.getAgentId() != null)
                .collect(Collectors.groupingBy(AgentScore::getAgentId));

        List<Map<String, Object>> agentRanking = new ArrayList<>();
        for (Agent agent : agents) {
            List<AgentScore> agentScores = scoresByAgent.getOrDefault(agent.getId(), List.of());
            long taskCount = tasks.stream()
                    .filter(t -> agent.getId() != null && agent.getId().equals(t.getAgentId()))
                    .count();
            double avgResp = agentScores.stream()
                    .filter(s -> s.getResponseSpeedScore() != null)
                    .mapToInt(AgentScore::getResponseSpeedScore)
                    .average().orElse(0.0);
            double avgQuality = agentScores.stream()
                    .filter(s -> s.getConstructionQualityScore() != null)
                    .mapToInt(AgentScore::getConstructionQualityScore)
                    .average().orElse(0.0);
            double avgDoc = agentScores.stream()
                    .filter(s -> s.getDocumentCompletenessScore() != null)
                    .mapToInt(AgentScore::getDocumentCompletenessScore)
                    .average().orElse(0.0);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("agentId", agent.getId());
            row.put("agentName", agent.getAgentName());
            row.put("overallScore", agent.getOverallScore() == null
                    ? BigDecimal.ZERO
                    : agent.getOverallScore());
            row.put("taskCount", taskCount);
            row.put("responseSpeedScore", BigDecimal.valueOf(avgResp).setScale(1, RoundingMode.HALF_UP));
            row.put("constructionQualityScore", BigDecimal.valueOf(avgQuality).setScale(1, RoundingMode.HALF_UP));
            row.put("documentCompletenessScore", BigDecimal.valueOf(avgDoc).setScale(1, RoundingMode.HALF_UP));
            agentRanking.add(row);
        }
        // Sort by overallScore desc and assign rank, keep only top 10
        agentRanking.sort(Comparator.<Map<String, Object>, BigDecimal>comparing(
                m -> (BigDecimal) m.get("overallScore"), Comparator.reverseOrder()));
        if (agentRanking.size() > 10) {
            agentRanking = new ArrayList<>(agentRanking.subList(0, 10));
        }
        for (int i = 0; i < agentRanking.size(); i++) {
            agentRanking.get(i).put("rank", i + 1);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("efficiency", efficiency);
        result.put("efficiencyByType", efficiencyByType);
        result.put("agentRanking", agentRanking);
        return Result.ok(result);
    }

    private Map<String, Object> buildTypeStats(List<ImplTask> tasks, String type) {
        List<ImplTask> typed = tasks.stream()
                .filter(t -> type.equals(t.getTaskType()))
                .toList();
        long total = typed.size();
        long completed = typed.stream()
                .filter(t -> TASK_STATUS_COMPLETED.equals(t.getStatus()))
                .count();
        double completionRate = total > 0 ? (completed * 100.0 / total) : 0.0;
        double avgDuration = typed.stream()
                .filter(t -> TASK_STATUS_COMPLETED.equals(t.getStatus())
                        && t.getActualStartDate() != null
                        && t.getActualEndDate() != null)
                .mapToLong(t -> ChronoUnit.DAYS.between(t.getActualStartDate(), t.getActualEndDate()))
                .average().orElse(0.0);
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", total);
        stats.put("completed", completed);
        stats.put("completionRate", BigDecimal.valueOf(completionRate).setScale(1, RoundingMode.HALF_UP));
        stats.put("avgDurationDays", BigDecimal.valueOf(avgDuration).setScale(1, RoundingMode.HALF_UP));
        return stats;
    }

    // ========================================================================
    // Dashboard endpoints (Task 31)
    // ========================================================================

    @Operation(summary = "仪表盘统计")
    @GetMapping("/dashboard/stats")
    public Result<DashboardStats> dashboardStats() {
        return Result.ok(reportService.getDashboardStats());
    }

    @Operation(summary = "项目趋势（最近 6 月状态分布）")
    @GetMapping("/project/trend")
    public Result<List<ProjectTrendItem>> projectTrend() {
        return Result.ok(reportService.getProjectTrend());
    }

    @Operation(summary = "待办列表（Top N）")
    @GetMapping("/todo/list")
    public Result<List<TodoItem>> todoList(
            @RequestParam(defaultValue = "5") int limit) {
        return Result.ok(reportService.getTodoList(limit));
    }

    @Operation(summary = "近期动态（最近 N 条日志）")
    @GetMapping("/recent-activities")
    public Result<List<ActivityItem>> recentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.ok(reportService.getRecentActivities(limit));
    }
}
