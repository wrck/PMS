package com.dp.plat.project.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
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
import java.util.Set;

/**
 * Daily scheduler that scans milestones whose planned date has passed and
 * marks them as OVERDUE.
 *
 * <p>For each overdue milestone a {@code MILESTONE} category notification is
 * sent to the project manager via the in-app + WebSocket channels. Notification
 * failures are isolated in try-catch so they never affect the milestone state
 * update or the surrounding transaction.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MilestoneOverdueScheduler {

    /** Milestone status values that should not be re-evaluated. */
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_OVERDUE = "OVERDUE";

    /** Notification metadata. */
    private static final String CATEGORY_MILESTONE = "MILESTONE";
    private static final String BIZ_TYPE_MILESTONE_OVERDUE = "MILESTONE_OVERDUE";
    private static final Set<String> CHANNELS = Set.of("IN_APP", "WS");

    private final IMilestoneService milestoneService;
    private final ProjectMapper projectMapper;
    private final INotificationService notificationService;

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
            sendOverdueNotification(milestone, pmUserId);
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

    /**
     * Look up the project name for the given project (best-effort).
     */
    private String lookupProjectName(Long projectId) {
        if (projectId == null) {
            return "未知项目";
        }
        Project project = projectMapper.selectById(projectId);
        return project == null || project.getProjectName() == null
                ? "未知项目" : project.getProjectName();
    }

    /**
     * Send a milestone-overdue notification to the project manager. Failures
     * are swallowed: notifications are best-effort and must not affect the
     * milestone scan.
     */
    private void sendOverdueNotification(Milestone milestone, Long pmUserId) {
        if (pmUserId == null) {
            log.warn("里程碑 {} 未找到项目经理，跳过通知发送", milestone.getId());
            return;
        }
        String projectName = lookupProjectName(milestone.getProjectId());
        String title = "里程碑延期预警";
        String content = String.format("%s 的里程碑 %s 已延期，计划完成日 %s",
                projectName,
                milestone.getMilestoneType() == null ? milestone.getMilestoneName() : milestone.getMilestoneType(),
                milestone.getPlanDate());
        Notification notification = Notification.builder()
                .userId(pmUserId)
                .title(title)
                .content(content)
                .category(CATEGORY_MILESTONE)
                .bizType(BIZ_TYPE_MILESTONE_OVERDUE)
                .bizId(milestone.getId())
                .build();
        try {
            notificationService.multiChannelSend(notification, CHANNELS);
        } catch (Exception e) {
            log.error("里程碑延期通知发送失败 milestoneId={} pmUserId={}",
                    milestone.getId(), pmUserId, e);
        }
    }
}
