package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.MilestoneMapper;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.IMilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of {@link IMilestoneService}.
 */
@Service
@RequiredArgsConstructor
public class MilestoneServiceImpl extends ServiceImpl<MilestoneMapper, Milestone> implements IMilestoneService {

    /** Milestone status indicating completion. */
    private static final String STATUS_COMPLETED = "COMPLETED";
    /** Default milestone status. */
    private static final String STATUS_PENDING = "PENDING";

    private final ProjectMapper projectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createMilestone(Milestone milestone) {
        if (milestone == null) {
            throw new BusinessException("里程碑信息不能为空");
        }
        if (milestone.getProjectId() == null) {
            throw new BusinessException("项目ID不能为空");
        }
        if (!StringUtils.hasText(milestone.getMilestoneName())) {
            throw new BusinessException("里程碑名称不能为空");
        }
        if (milestone.getPlanDate() == null) {
            throw new BusinessException("计划日期不能为空");
        }
        if (!StringUtils.hasText(milestone.getStatus())) {
            milestone.setStatus(STATUS_PENDING);
        }
        if (milestone.getSortOrder() == null) {
            milestone.setSortOrder(0);
        }
        milestone.setId(null);
        this.save(milestone);
        return Result.ok(milestone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMilestone(Milestone milestone) {
        if (milestone == null || milestone.getId() == null) {
            throw new BusinessException("里程碑信息或ID不能为空");
        }
        Milestone existing = this.getById(milestone.getId());
        if (existing == null) {
            throw new BusinessException("里程碑不存在");
        }
        this.updateById(milestone);
        // If the status or actual date changed, recalculate the parent project progress.
        if (milestone.getProjectId() != null) {
            recalculateProjectProgress(milestone.getProjectId());
        }
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteMilestone(Long id) {
        Milestone existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("里程碑不存在");
        }
        this.removeById(id);
        // Recalculate the parent project progress after deletion.
        if (existing.getProjectId() != null) {
            recalculateProjectProgress(existing.getProjectId());
        }
        return Result.ok();
    }

    @Override
    public Result<List<Milestone>> listByProjectId(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        List<Milestone> list = this.list(new LambdaQueryWrapper<Milestone>()
                .eq(Milestone::getProjectId, projectId)
                .orderByAsc(Milestone::getSortOrder));
        return Result.ok(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateProgress(Long milestoneId, String actualDate, String description) {
        Milestone milestone = this.getById(milestoneId);
        if (milestone == null) {
            throw new BusinessException("里程碑不存在");
        }
        if (StringUtils.hasText(actualDate)) {
            milestone.setActualDate(LocalDate.parse(actualDate));
        }
        if (description != null) {
            milestone.setDescription(description);
        }
        // Recording the actual completion date marks the milestone as completed.
        milestone.setStatus(STATUS_COMPLETED);
        this.updateById(milestone);
        // Recalculate the parent project progress.
        if (milestone.getProjectId() != null) {
            recalculateProjectProgress(milestone.getProjectId());
        }
        return Result.ok(milestone);
    }

    @Override
    public int recalculateProjectProgress(Long projectId) {
        if (projectId == null) {
            return 0;
        }
        List<Milestone> milestones = this.list(new LambdaQueryWrapper<Milestone>()
                .eq(Milestone::getProjectId, projectId));
        if (milestones.isEmpty()) {
            return 0;
        }
        long completed = milestones.stream()
                .filter(m -> STATUS_COMPLETED.equals(m.getStatus()))
                .count();
        int progress = (int) (completed * 100 / milestones.size());
        // Update the project progress field.
        Project project = projectMapper.selectById(projectId);
        if (project != null) {
            project.setProgress(progress);
            projectMapper.updateById(project);
        }
        return progress;
    }
}
