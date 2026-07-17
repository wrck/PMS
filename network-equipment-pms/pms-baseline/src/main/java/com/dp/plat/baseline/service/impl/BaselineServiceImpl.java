package com.dp.plat.baseline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.baseline.dto.BaselineDiffResult;
import com.dp.plat.baseline.dto.TaskDiff;
import com.dp.plat.baseline.entity.BaselineSnapshot;
import com.dp.plat.baseline.mapper.BaselineSnapshotMapper;
import com.dp.plat.baseline.service.BaselineService;
import com.dp.plat.common.dto.TaskPlanSnapshot;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.project.service.ProjectConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计划基线服务实现 — 保存基线（快照全部任务）。
 *
 * <p>关联设计文档：§2.2 BaselineSnapshot、§3.6 单一活跃基线规则。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaselineServiceImpl extends ServiceImpl<BaselineSnapshotMapper, BaselineSnapshot>
        implements BaselineService {

    private static final DateTimeFormatter NAME_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");

    private final ImplTaskMapper implTaskMapper;
    private final ProjectConfigService projectConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaselineSnapshot saveBaseline(Long projectId, String baselineName) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }

        // 1. 查询项目下全部任务（@TableLogic 自动过滤已删除）
        List<ImplTask> tasks = implTaskMapper.selectList(
                new LambdaQueryWrapper<ImplTask>().eq(ImplTask::getProjectId, projectId));
        if (tasks.isEmpty()) {
            throw new BusinessException("项目下无任务，无法保存基线：projectId=" + projectId);
        }

        // 2. 组装任务计划快照列表
        List<TaskPlanSnapshot> snapshots = new ArrayList<>(tasks.size());
        for (ImplTask task : tasks) {
            snapshots.add(TaskPlanSnapshot.builder()
                    .taskId(task.getId())
                    .taskName(task.getTaskName())
                    .plannedStart(toStr(task.getPlanStartDate()))
                    .plannedEnd(toStr(task.getPlanEndDate()))
                    .duration(durationBetween(task.getPlanStartDate(), task.getPlanEndDate()))
                    .plannedHours(task.getPlannedHours())
                    .taskType(task.getTaskType())
                    .build());
        }

        // 3. 若项目已有 APPROVED 基线 → 置为 SUPERSEDED（单一活跃基线）
        List<BaselineSnapshot> approved = this.list(new LambdaQueryWrapper<BaselineSnapshot>()
                .eq(BaselineSnapshot::getProjectId, projectId)
                .eq(BaselineSnapshot::getStatus, "APPROVED"));
        for (BaselineSnapshot old : approved) {
            old.setStatus("SUPERSEDED");
            this.updateById(old);
            log.info("基线 {} 被新基线取代（SUPERSEDED）", old.getId());
        }

        // 4. 创建新基线（status=DRAFT）
        BaselineSnapshot baseline = BaselineSnapshot.builder()
                .projectId(projectId)
                .baselineName((baselineName == null || baselineName.isBlank())
                        ? "基线_" + LocalDateTime.now().format(NAME_FMT)
                        : baselineName)
                .status("DRAFT")
                .snapshotJson(snapshots)
                .build();
        this.save(baseline);
        log.info("保存基线成功：projectId={}, baselineId={}, taskCount={}",
                projectId, baseline.getId(), snapshots.size());
        return baseline;
    }

    @Override
    public List<BaselineSnapshot> listByProject(Long projectId) {
        return this.list(new LambdaQueryWrapper<BaselineSnapshot>()
                .eq(BaselineSnapshot::getProjectId, projectId)
                .orderByDesc(BaselineSnapshot::getCreateTime));
    }

    @Override
    public BaselineDiffResult compareWithBaseline(Long baselineId) {
        BaselineSnapshot baseline = this.getById(baselineId);
        if (baseline == null) {
            throw new BusinessException("基线不存在：id=" + baselineId);
        }

        // 1. 加载基线快照 + 当前任务（按 taskId 索引）
        List<TaskPlanSnapshot> snapshots = baseline.getSnapshotJson();
        List<ImplTask> currentTasks = implTaskMapper.selectList(
                new LambdaQueryWrapper<ImplTask>().eq(ImplTask::getProjectId, baseline.getProjectId()));
        Map<Long, ImplTask> currentMap = new HashMap<>();
        for (ImplTask t : currentTasks) {
            currentMap.put(t.getId(), t);
        }

        // 2. 读取双阈值（项目级 > 系统默认，缺省 5 天 / 10%）
        int daysThreshold = readIntConfig(baseline.getProjectId(),
                "baseline.variance.days.threshold", 5);
        int percentThreshold = readIntConfig(baseline.getProjectId(),
                "baseline.variance.percent.threshold", 10);

        // 3. 逐任务计算偏差
        List<TaskDiff> diffs = new ArrayList<>();
        int totalVarianced = 0;
        boolean needsApproval = false;
        if (snapshots != null) {
            for (TaskPlanSnapshot snap : snapshots) {
                ImplTask current = currentMap.get(snap.getTaskId());
                String currentStart = current != null ? toStr(current.getPlanStartDate()) : null;
                String currentEnd = current != null ? toStr(current.getPlanEndDate()) : null;

                Integer startVariance = daysBetween(snap.getPlannedStart(), currentStart);
                Integer endVariance = daysBetween(snap.getPlannedEnd(), currentEnd);
                long baselineDuration = daysBetweenLong(snap.getPlannedStart(), snap.getPlannedEnd());
                Double percentVariance = null;
                if (endVariance != null && baselineDuration > 0) {
                    percentVariance = Math.round(
                            Math.abs(endVariance) * 10000.0 / baselineDuration) / 100.0;
                }

                TaskDiff diff = TaskDiff.builder()
                        .taskId(snap.getTaskId())
                        .taskName(snap.getTaskName())
                        .baselineStart(snap.getPlannedStart())
                        .currentStart(currentStart)
                        .startVariance(startVariance)
                        .baselineEnd(snap.getPlannedEnd())
                        .currentEnd(currentEnd)
                        .endVariance(endVariance)
                        .percentVariance(percentVariance)
                        .build();
                diffs.add(diff);

                if ((startVariance != null && startVariance != 0)
                        || (endVariance != null && endVariance != 0)) {
                    totalVarianced++;
                }
                // 双阈值 OR：|结束偏差| > 天数阈值 OR 偏差百分比 > 百分比阈值
                if (endVariance != null) {
                    long daysVar = Math.abs(endVariance);
                    double percentVar = (baselineDuration > 0)
                            ? (double) daysVar / baselineDuration * 100 : 0;
                    if (daysVar > daysThreshold || percentVar > percentThreshold) {
                        needsApproval = true;
                    }
                }
            }
        }

        BaselineDiffResult.BaselineInfo info = BaselineDiffResult.BaselineInfo.builder()
                .id(baseline.getId())
                .baselineName(baseline.getBaselineName())
                .status(baseline.getStatus())
                .approvedAt(baseline.getApprovedAt())
                .build();

        return BaselineDiffResult.builder()
                .baseline(info)
                .diffs(diffs)
                .totalVarianced(totalVarianced)
                .needsApproval(needsApproval)
                .approvalReason(needsApproval
                        ? "偏差超过阈值（" + daysThreshold + " 天 / " + percentThreshold + "%）"
                        : null)
                .build();
    }

    /** LocalDate → ISO 字符串（null 返回 null）。 */
    private static String toStr(LocalDate date) {
        return date == null ? null : date.toString();
    }

    /** 计算工期天数（任一端为 null 返回 null）。 */
    private static Integer durationBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个 ISO 日期字符串间的天数差（current - baseline）。
     * 任一为 null 返回 null。
     */
    private static Integer daysBetween(String baselineDate, String currentDate) {
        if (baselineDate == null || currentDate == null) {
            return null;
        }
        try {
            LocalDate b = LocalDate.parse(baselineDate);
            LocalDate c = LocalDate.parse(currentDate);
            return (int) ChronoUnit.DAYS.between(b, c);
        } catch (Exception e) {
            return null;
        }
    }

    /** 同 {@link #daysBetween} 但返回 long，用于工期计算（端点为 null 返回 0）。 */
    private static long daysBetweenLong(String start, String end) {
        if (start == null || end == null) {
            return 0L;
        }
        try {
            return ChronoUnit.DAYS.between(LocalDate.parse(start), LocalDate.parse(end));
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 读取项目配置 int 值，缺省返回 defaultValue。
     * templateId 传 null（读取项目级或系统默认）。
     */
    private int readIntConfig(Long projectId, String key, int defaultValue) {
        try {
            String value = projectConfigService.get(projectId, null, key);
            return value == null ? defaultValue : Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
