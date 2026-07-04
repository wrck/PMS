package com.dp.plat.project.punchlist.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.mapper.MilestoneMapper;
import com.dp.plat.project.punchlist.entity.PunchList;
import com.dp.plat.project.punchlist.mapper.PunchListMapper;
import com.dp.plat.project.punchlist.service.IPunchListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link IPunchListService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PunchListServiceImpl extends ServiceImpl<PunchListMapper, PunchList>
        implements IPunchListService {

    /** Severity for safety-critical defects that block the related milestone. */
    private static final String SEVERITY_SAFETY = "SAFETY";
    /** Punch list status values. */
    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_RESOLVED = "RESOLVED";
    private static final String STATUS_VERIFIED = "VERIFIED";
    /** Milestone blocked status. */
    private static final String MILESTONE_BLOCKED = "BLOCKED";

    /** Notification metadata. */
    private static final String CATEGORY_PUNCH_LIST = "PUNCH_LIST";
    private static final String BIZ_TYPE_PUNCH_LIST_DEADLINE = "PUNCH_LIST_DEADLINE";
    private static final Set<String> CHANNELS = Set.of("IN_APP", "WS");
    /** Scan window: items whose deadline is within this many days (inclusive). */
    private static final int DEADLINE_WINDOW_DAYS = 3;

    private final MilestoneMapper milestoneMapper;
    private final INotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PunchList> create(PunchList punchList) {
        if (punchList == null) {
            throw new BusinessException("Punch List 信息不能为空");
        }
        if (punchList.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        if (!StringUtils.hasText(punchList.getTitle())) {
            throw new BusinessException("缺陷标题不能为空");
        }
        if (!StringUtils.hasText(punchList.getSeverity())) {
            throw new BusinessException("严重等级不能为空");
        }
        if (!StringUtils.hasText(punchList.getStatus())) {
            punchList.setStatus(STATUS_OPEN);
        }
        punchList.setId(null);
        this.save(punchList);

        // Safety-severity defects block the related milestone until resolved and verified.
        if (SEVERITY_SAFETY.equals(punchList.getSeverity()) && punchList.getMilestoneId() != null) {
            Milestone milestone = milestoneMapper.selectById(punchList.getMilestoneId());
            if (milestone != null && !MILESTONE_BLOCKED.equals(milestone.getStatus())
                    && !"COMPLETED".equals(milestone.getStatus())) {
                milestone.setStatus(MILESTONE_BLOCKED);
                milestoneMapper.updateById(milestone);
                log.warn("Punch List 项(id={})为安全级缺陷，已阻塞里程碑(id={}, name={})",
                        punchList.getId(), milestone.getId(), milestone.getMilestoneName());
            } else if (milestone == null) {
                log.warn("Punch List 项(id={})关联的里程碑(id={})不存在，未执行阻塞",
                        punchList.getId(), punchList.getMilestoneId());
            }
        }
        return Result.ok(punchList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(PunchList punchList) {
        if (punchList == null || punchList.getId() == null) {
            throw new BusinessException("Punch List 信息或ID不能为空");
        }
        PunchList existing = super.getById(punchList.getId());
        if (existing == null) {
            throw new BusinessException("Punch List 项不存在");
        }
        this.updateById(punchList);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(Long id) {
        PunchList existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException("Punch List 项不存在");
        }
        this.removeById(id);
        return Result.ok();
    }

    @Override
    public Result<PunchList> getById(Long id) {
        if (id == null) {
            throw new BusinessException("ID不能为空");
        }
        PunchList punchList = super.getById(id);
        if (punchList == null) {
            throw new BusinessException("Punch List 项不存在");
        }
        return Result.ok(punchList);
    }

    @Override
    public Result<List<PunchList>> listByProject(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        List<PunchList> list = this.list(new LambdaQueryWrapper<PunchList>()
                .eq(PunchList::getProjectId, projectId)
                .orderByAsc(PunchList::getSeverity)
                .orderByAsc(PunchList::getCreateTime));
        return Result.ok(list);
    }

    @Override
    public Result<List<PunchList>> listByMilestone(Long milestoneId) {
        if (milestoneId == null) {
            throw new BusinessException("里程碑ID不能为空");
        }
        List<PunchList> list = this.list(new LambdaQueryWrapper<PunchList>()
                .eq(PunchList::getMilestoneId, milestoneId)
                .orderByAsc(PunchList::getSeverity)
                .orderByAsc(PunchList::getCreateTime));
        return Result.ok(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PunchList> resolve(Long id) {
        PunchList punchList = super.getById(id);
        if (punchList == null) {
            throw new BusinessException("Punch List 项不存在");
        }
        if (!STATUS_OPEN.equals(punchList.getStatus())) {
            throw new BusinessException("当前Punch List项状态不允许标记为已解决");
        }
        punchList.setStatus(STATUS_RESOLVED);
        punchList.setResolvedAt(LocalDateTime.now());
        this.updateById(punchList);
        return Result.ok(punchList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PunchList> verify(Long id) {
        PunchList punchList = super.getById(id);
        if (punchList == null) {
            throw new BusinessException("Punch List 项不存在");
        }
        if (!STATUS_RESOLVED.equals(punchList.getStatus())) {
            throw new BusinessException("当前Punch List项状态不允许验证，需先标记为已解决");
        }
        punchList.setStatus(STATUS_VERIFIED);
        punchList.setVerifiedAt(LocalDateTime.now());
        punchList.setVerifiedBy(SecurityUtils.getCurrentUserId());
        punchList.setVerifiedByName(SecurityUtils.getCurrentUsername());
        this.updateById(punchList);
        return Result.ok(punchList);
    }

    @Override
    public boolean isAllVerified(Long projectId) {
        if (projectId == null) {
            return false;
        }
        List<PunchList> list = this.list(new LambdaQueryWrapper<PunchList>()
                .eq(PunchList::getProjectId, projectId));
        if (list.isEmpty()) {
            return true;
        }
        return list.stream().allMatch(p -> STATUS_VERIFIED.equals(p.getStatus()));
    }

    /**
     * Every day at 09:00, scan punch list items whose deadline is approaching
     * (within {@value #DEADLINE_WINDOW_DAYS} days, inclusive) and notify the
     * assignee. Items already verified or closed are skipped. Notification
     * failures are isolated in try-catch so they never affect the scan.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void scanDeadlineApproaching() {
        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(DEADLINE_WINDOW_DAYS);
        List<PunchList> approaching = this.list(new LambdaQueryWrapper<PunchList>()
                .isNotNull(PunchList::getDeadline)
                .ge(PunchList::getDeadline, today)
                .le(PunchList::getDeadline, windowEnd)
                .ne(PunchList::getStatus, STATUS_VERIFIED));
        if (approaching.isEmpty()) {
            return;
        }
        log.info("Punch List 整改到期扫描：发现 {} 条临近到期项", approaching.size());
        for (PunchList item : approaching) {
            sendDeadlineNotification(item);
        }
    }

    /**
     * Send a deadline-approaching notification to the assignee. Failures are
     * swallowed: notifications are best-effort.
     */
    private void sendDeadlineNotification(PunchList item) {
        Long assigneeId = item.getAssigneeId();
        if (assigneeId == null) {
            log.warn("Punch List 项 id={} 未指派负责人，跳过通知发送", item.getId());
            return;
        }
        String title = "Punch List 整改到期提醒";
        String content = String.format("缺陷 %s(%s) 将于 %s 到期，请尽快整改",
                item.getId(),
                item.getSeverity() == null ? "未知等级" : item.getSeverity(),
                item.getDeadline());
        Notification notification = Notification.builder()
                .userId(assigneeId)
                .title(title)
                .content(content)
                .category(CATEGORY_PUNCH_LIST)
                .bizType(BIZ_TYPE_PUNCH_LIST_DEADLINE)
                .bizId(item.getId())
                .build();
        try {
            notificationService.multiChannelSend(notification, CHANNELS);
        } catch (Exception e) {
            log.error("Punch List 整改到期通知发送失败 punchListId={} assigneeId={}",
                    item.getId(), assigneeId, e);
        }
    }
}
