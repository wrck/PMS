package com.dp.plat.project.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.IMilestoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Daily scheduler that scans milestones whose planned date has passed and
 * marks them as OVERDUE.
 *
 * <p>Notifications are recorded via logs only; integration with the
 * notification module is intentionally avoided to prevent cyclic
 * dependencies.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MilestoneOverdueScheduler {

    /** Milestone status values that should not be re-evaluated. */
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_OVERDUE = "OVERDUE";

    private final IMilestoneService milestoneService;
    private final ProjectMapper projectMapper;

    /**
     * Run every day at 02:00 to flag overdue milestones.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scanOverdueMilestones() {
        LocalDate today = LocalDate.now();
        List<Milestone> overdue = milestoneService.list(new LambdaQueryWrapper<Milestone>()
                .lt(Milestone::getPlanDate, today)
                .ne(Milestone::getStatus, STATUS_COMPLETED)
                .ne(Milestone::getStatus, STATUS_OVERDUE));
        if (overdue.isEmpty()) {
            return;
        }
        log.info("里程碑延期扫描：发现 {} 条已过期未完成的里程碑", overdue.size());
        for (Milestone milestone : overdue) {
            milestone.setStatus(STATUS_OVERDUE);
            milestoneService.updateById(milestone);
            Long pmUserId = lookupProjectManager(milestone.getProjectId());
            log.info("里程碑 {} 已延期，计划日期 {}，项目经理 {}",
                    milestone.getId(),
                    milestone.getPlanDate(),
                    pmUserId);
        }
    }

    /**
     * Look up the project manager user id for the given project.
     */
    private Long lookupProjectManager(Long projectId) {
        if (projectId == null) {
            return null;
        }
        Project project = projectMapper.selectById(projectId);
        return project == null ? null : project.getProjectManagerId();
    }
}
