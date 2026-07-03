package com.dp.plat.governance.issue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.change.entity.ChangeRequest;
import com.dp.plat.governance.change.service.IChangeRequestService;
import com.dp.plat.governance.issue.entity.Issue;
import com.dp.plat.governance.issue.mapper.IssueMapper;
import com.dp.plat.governance.issue.service.IIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link IIssueService}.
 *
 * <p>Supports three-book linkage: an issue can be escalated to a change request
 * via {@link #escalate}. The {@link IChangeRequestService} is injected via
 * constructor to avoid circular dependencies.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IssueServiceImpl
        extends ServiceImpl<IssueMapper, Issue>
        implements IIssueService {

    /** Default status for a newly created issue. */
    private static final String STATUS_OPEN = "OPEN";
    /** Status while the issue is being worked on. */
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    /** Status after the issue is resolved. */
    private static final String STATUS_RESOLVED = "RESOLVED";
    /** Final closed status. */
    private static final String STATUS_CLOSED = "CLOSED";

    /** Default priority for a new issue. */
    private static final String PRIORITY_MEDIUM = "MEDIUM";

    private final IChangeRequestService changeRequestService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Issue> create(Issue issue) {
        if (issue == null) {
            throw new BusinessException("问题信息不能为空");
        }
        if (!StringUtils.hasText(issue.getDescription())) {
            throw new BusinessException("问题描述不能为空");
        }
        issue.setId(null);
        issue.setIssueNo(generateIssueNo());
        issue.setStatus(STATUS_OPEN);
        if (!StringUtils.hasText(issue.getPriority())) {
            issue.setPriority(PRIORITY_MEDIUM);
        }
        if (issue.getTargetResolveDate() == null) {
            issue.setTargetResolveDate(LocalDate.now().plusDays(7));
        }
        this.save(issue);
        return Result.ok(issue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(Issue issue) {
        if (issue == null || issue.getId() == null) {
            throw new BusinessException("问题信息或ID不能为空");
        }
        Issue existing = baseMapper.selectById(issue.getId());
        if (existing == null) {
            throw new BusinessException("问题不存在");
        }
        this.updateById(issue);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(Long id) {
        Issue existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("问题不存在");
        }
        this.removeById(id);
        return Result.ok();
    }

    @Override
    public Result<List<Issue>> listAll() {
        List<Issue> list = this.list(new LambdaQueryWrapper<Issue>()
                .orderByDesc(Issue::getCreateTime));
        return Result.ok(list);
    }

    @Override
    public Result<Issue> getById(Long id) {
        Issue issue = baseMapper.selectById(id);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }
        return Result.ok(issue);
    }

    @Override
    public Result<List<Issue>> listByProject(Long projectId) {
        if (projectId == null) {
            return Result.ok(List.of());
        }
        List<Issue> list = this.list(new LambdaQueryWrapper<Issue>()
                .eq(Issue::getProjectId, projectId)
                .orderByDesc(Issue::getCreateTime));
        return Result.ok(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Issue> assign(Long id, Long assigneeId, String assigneeName) {
        Issue issue = baseMapper.selectById(id);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }
        issue.setAssigneeId(assigneeId);
        issue.setAssigneeName(assigneeName);
        // Auto-transition OPEN → IN_PROGRESS on assignment.
        if (STATUS_OPEN.equals(issue.getStatus())) {
            issue.setStatus(STATUS_IN_PROGRESS);
        }
        this.updateById(issue);
        return Result.ok(issue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Issue> resolve(Long id, String resolution) {
        Issue issue = baseMapper.selectById(id);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }
        issue.setStatus(STATUS_RESOLVED);
        issue.setResolution(resolution);
        issue.setResolvedAt(LocalDateTime.now());
        this.updateById(issue);
        return Result.ok(issue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Issue> close(Long id) {
        Issue issue = baseMapper.selectById(id);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }
        issue.setStatus(STATUS_CLOSED);
        issue.setClosedAt(LocalDateTime.now());
        this.updateById(issue);
        return Result.ok(issue);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> escalate(Long id) {
        Issue issue = baseMapper.selectById(id);
        if (issue == null) {
            throw new BusinessException("问题不存在");
        }
        // Create a change request escalated from this issue.
        ChangeRequest cr = ChangeRequest.builder()
                .projectId(issue.getProjectId())
                .title("由" + issue.getIssueNo() + "升级的变更请求")
                .description("由问题 " + issue.getIssueNo() + " 升级: " + issue.getDescription())
                .requesterId(issue.getRaisedBy())
                .requesterName(issue.getRaisedByName())
                .requestDate(LocalDate.now())
                .priority(StringUtils.hasText(issue.getPriority()) ? issue.getPriority() : PRIORITY_MEDIUM)
                .build();
        Result<ChangeRequest> crResult = changeRequestService.create(cr);
        return Result.ok(crResult.getData());
    }

    @Override
    public String generateIssueNo() {
        int year = LocalDate.now().getYear();
        String prefix = "ISSUE-" + year + "-";
        long count = this.count(new LambdaQueryWrapper<Issue>()
                .likeRight(Issue::getIssueNo, prefix));
        long sequence = count + 1;
        return prefix + String.format("%04d", sequence);
    }
}
