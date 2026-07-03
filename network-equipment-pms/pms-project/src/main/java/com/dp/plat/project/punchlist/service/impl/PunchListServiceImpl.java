package com.dp.plat.project.punchlist.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.mapper.MilestoneMapper;
import com.dp.plat.project.punchlist.entity.PunchList;
import com.dp.plat.project.punchlist.mapper.PunchListMapper;
import com.dp.plat.project.punchlist.service.IPunchListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

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

    private final MilestoneMapper milestoneMapper;

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
}
