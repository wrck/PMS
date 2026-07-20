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
import com.dp.plat.common.spi.ApprovalTrigger;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.project.service.ProjectConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    /** 审批类型常量（参见 §6.9 审批类型表）。 */
    private static final String APPROVAL_TYPE_BASELINE_CHANGE = "BASELINE_CHANGE";

    private final ImplTaskMapper implTaskMapper;
    private final ProjectConfigService projectConfigService;

    /** TD-P8-008：审批触发 SPI（可选注入，pms-workflow 未加载时跳过审批触发并 log.warn）。 */
    @Autowired(required = false)
    private ApprovalTrigger approvalTrigger;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaselineDiffResult requestBaselineChange(Long baselineId, String changeReason) {
        BaselineSnapshot baseline = this.getById(baselineId);
        if (baseline == null) {
            throw new BusinessException("基线不存在：id=" + baselineId);
        }
        if (!"DRAFT".equals(baseline.getStatus())) {
            throw new BusinessException("仅 DRAFT 状态基线可申请变更，当前状态："
                    + baseline.getStatus());
        }

        // 1. 偏差分析（天数/百分比双阈值，已计算 needsApproval）
        BaselineDiffResult result = compareWithBaseline(baselineId);

        // 2. count 阈值（偏差任务数）
        int countThreshold = readIntConfig(baseline.getProjectId(),
                "baseline.variance.threshold.count", 3);
        int variancedCount = result.getTotalVarianced() == null ? 0 : result.getTotalVarianced();
        boolean countExceeded = variancedCount > countThreshold;

        // 3. 双阈值 OR：days/percent OR count
        boolean daysOrPercentExceeded = Boolean.TRUE.equals(result.getNeedsApproval());
        boolean needsApproval = daysOrPercentExceeded || countExceeded;

        // 4. 合并审批原因
        String approvalReason = null;
        if (needsApproval) {
            StringBuilder sb = new StringBuilder();
            if (daysOrPercentExceeded) {
                sb.append("偏差超过阈值");
            }
            if (countExceeded) {
                if (sb.length() > 0) {
                    sb.append("；");
                }
                sb.append("偏差任务数 ").append(variancedCount)
                        .append(" 超过阈值 ").append(countThreshold);
            }
            approvalReason = sb.toString();
        }
        result.setNeedsApproval(needsApproval);
        result.setApprovalReason(approvalReason);

        if (needsApproval) {
            // TD-P8-008：触发 BASELINE_CHANGE 审批流程，回填 approvalRecordId 到 baseline。
            // 通过 ApprovalTrigger SPI 跨模块调用 pms-workflow 的 ApprovalCenterService.createApproval，
            // 落库 ApprovalRecord（状态 PENDING，round=1），由 ApprovalDispatcher 启动 Flowable 流程实例。
            baseline.setChangeReason(changeReason);
            Long approvalRecordId = triggerBaselineChangeApproval(baseline, approvalReason);
            if (approvalRecordId != null) {
                baseline.setApprovalRecordId(approvalRecordId);
                log.info("基线 {} 偏差超阈值，已触发 BASELINE_CHANGE 审批 approvalRecordId={} reason={}",
                        baselineId, approvalRecordId, changeReason);
            } else {
                // SPI 未加载或触发失败：保留 changeReason 但不阻断流程（log.warn 已在内部记录）
                log.warn("基线 {} 偏差超阈值，但审批触发失败（SPI 未加载或异常），保留 changeReason 等待后续触发",
                        baselineId);
            }
            this.updateById(baseline);
        } else {
            // 未超阈值 → 直接 APPROVED
            baseline.setStatus("APPROVED");
            baseline.setApprovedAt(LocalDateTime.now());
            baseline.setChangeReason(changeReason);
            this.updateById(baseline);
            log.info("基线 {} 偏差未超阈值，直接 APPROVED", baselineId);
            if (result.getBaseline() != null) {
                result.getBaseline().setStatus("APPROVED");
                result.getBaseline().setApprovedAt(baseline.getApprovedAt());
            }
        }

        return result;
    }

    /**
     * 触发 BASELINE_CHANGE 审批流程（TD-P8-008）。
     *
     * <p>通过 {@link ApprovalTrigger} SPI 跨模块调用 pms-workflow 的审批中心：
     * <ul>
     *   <li>approvalType = {@value #APPROVAL_TYPE_BASELINE_CHANGE}</li>
     *   <li>businessId = baseline.id（基线ID）</li>
     *   <li>projectId = baseline.projectId</li>
     *   <li>title = 「基线变更审批：{baselineName}」</li>
     *   <li>reason = changeReason（拼接 approvalReason 偏差说明）</li>
     * </ul>
     * </p>
     *
     * @param baseline       基线快照
     * @param approvalReason 偏差原因（days/percent/count 阈值说明）
     * @return 审批记录ID；SPI 未加载或触发失败返回 null（已 log.warn）
     */
    private Long triggerBaselineChangeApproval(BaselineSnapshot baseline, String approvalReason) {
        if (approvalTrigger == null) {
            log.warn("基线 {} 审批触发跳过：ApprovalTrigger SPI 未加载（pms-workflow 模块未启用）",
                    baseline.getId());
            return null;
        }
        String title = "基线变更审批：" + baseline.getBaselineName();
        String reason = joinReason(baseline.getChangeReason(), approvalReason);
        return approvalTrigger.triggerApproval(
                APPROVAL_TYPE_BASELINE_CHANGE,
                baseline.getId(),
                baseline.getProjectId(),
                title,
                reason);
    }

    /** 拼接变更原因与偏差原因（任一为空返回另一个）。 */
    private static String joinReason(String changeReason, String approvalReason) {
        StringBuilder sb = new StringBuilder();
        if (approvalReason != null && !approvalReason.isBlank()) {
            sb.append(approvalReason);
        }
        if (changeReason != null && !changeReason.isBlank()) {
            if (sb.length() > 0) {
                sb.append("；");
            }
            sb.append("变更说明：").append(changeReason);
        }
        return sb.length() > 0 ? sb.toString() : null;
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
