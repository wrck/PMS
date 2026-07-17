package com.dp.plat.baseline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.baseline.entity.BaselineSnapshot;
import com.dp.plat.baseline.mapper.BaselineSnapshotMapper;
import com.dp.plat.baseline.service.BaselineService;
import com.dp.plat.common.dto.TaskPlanSnapshot;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
}
