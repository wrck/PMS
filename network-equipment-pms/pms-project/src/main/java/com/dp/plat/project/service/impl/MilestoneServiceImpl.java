package com.dp.plat.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.MilestoneGroupDto;
import com.dp.plat.project.entity.Milestone;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.enums.MilestoneType;
import com.dp.plat.project.enums.PpdiooPhase;
import com.dp.plat.project.mapper.MilestoneMapper;
import com.dp.plat.project.mapper.ProjectMapper;
import com.dp.plat.project.service.IMilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        // Derive ppdioo phase and sort order from the milestone type when not provided.
        MilestoneType type = MilestoneType.fromName(milestone.getMilestoneType());
        if (type != null) {
            if (!StringUtils.hasText(milestone.getPpdiooPhase())) {
                milestone.setPpdiooPhase(type.getPpdiooPhase().name());
            }
            if (milestone.getSortOrder() == null || milestone.getSortOrder() == 0) {
                milestone.setSortOrder(type.getSortOrder());
            }
        } else if (milestone.getSortOrder() == null) {
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
        // Stage-gate check before marking the milestone as completed.
        if (STATUS_COMPLETED.equals(milestone.getStatus())) {
            validateStageGate(mergeForValidation(existing, milestone));
        }
        this.updateById(milestone);
        // If the status or actual date changed, recalculate the parent project progress.
        if (milestone.getProjectId() != null) {
            recalculateProjectProgress(milestone.getProjectId());
        } else if (existing.getProjectId() != null) {
            recalculateProjectProgress(existing.getProjectId());
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
        // Stage-gate check before marking the milestone as completed.
        validateStageGate(milestone);
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

    @Override
    public Result<List<MilestoneGroupDto>> dashboardByPpdiooPhase(Long projectId) {
        if (projectId == null) {
            throw new BusinessException("项目ID不能为空");
        }
        List<Milestone> milestones = this.list(new LambdaQueryWrapper<Milestone>()
                .eq(Milestone::getProjectId, projectId)
                .orderByAsc(Milestone::getSortOrder));
        List<MilestoneGroupDto> groups = new ArrayList<>();
        for (PpdiooPhase phase : PpdiooPhase.values()) {
            List<Milestone> phaseMilestones = milestones.stream()
                    .filter(m -> phase.name().equals(m.getPpdiooPhase()))
                    .collect(Collectors.toList());
            if (!phaseMilestones.isEmpty()) {
                groups.add(MilestoneGroupDto.builder()
                        .ppdiooPhase(phase.name())
                        .ppdiooPhaseName(phase.getDisplayName())
                        .milestones(phaseMilestones)
                        .build());
            }
        }
        // Preserve any milestones whose ppdioo phase is unknown/unset in a fallback group.
        List<Milestone> unmatched = milestones.stream()
                .filter(m -> {
                    String phase = m.getPpdiooPhase();
                    if (phase == null || phase.isBlank()) {
                        return true;
                    }
                    try {
                        PpdiooPhase.valueOf(phase);
                        return false;
                    } catch (IllegalArgumentException e) {
                        return true;
                    }
                })
                .collect(Collectors.toList());
        if (!unmatched.isEmpty()) {
            groups.add(MilestoneGroupDto.builder()
                    .ppdiooPhase("UNKNOWN")
                    .ppdiooPhaseName("未分类")
                    .milestones(unmatched)
                    .build());
        }
        return Result.ok(groups);
    }

    /**
     * Validate the stage-gate rule before a milestone can be marked as completed.
     *
     * <p>All predecessor milestones (those with a lower sort order in the same project)
     * must be completed. As a special case, the FINAL_ACCEPTANCE milestone additionally
     * requires both the SAT and UAT milestones to be completed.</p>
     *
     * @param milestone the milestone being transitioned to completed
     */
    private void validateStageGate(Milestone milestone) {
        if (milestone == null || milestone.getProjectId() == null) {
            return;
        }
        MilestoneType type = MilestoneType.fromName(milestone.getMilestoneType());

        // Special rule: FINAL_ACCEPTANCE requires both SAT and UAT to be completed.
        if (type == MilestoneType.FINAL_ACCEPTANCE) {
            List<Milestone> siblings = this.list(new LambdaQueryWrapper<Milestone>()
                    .eq(Milestone::getProjectId, milestone.getProjectId())
                    .ne(Milestone::getId, milestone.getId()));
            for (MilestoneType required : new MilestoneType[]{MilestoneType.SAT, MilestoneType.UAT}) {
                boolean satisfied = siblings.stream()
                        .filter(m -> required.name().equals(m.getMilestoneType()))
                        .anyMatch(m -> STATUS_COMPLETED.equals(m.getStatus()));
                if (!satisfied) {
                    throw new BusinessException("前置里程碑 " + required.getDescription() + " 未完成，无法跳过");
                }
            }
            return;
        }

        // General rule: all predecessors (lower sort order) in the same project must be completed.
        Integer currentSortOrder = milestone.getSortOrder();
        if (currentSortOrder == null && type != null) {
            currentSortOrder = type.getSortOrder();
        }
        if (currentSortOrder == null) {
            return;
        }
        List<Milestone> predecessors = this.list(new LambdaQueryWrapper<Milestone>()
                .eq(Milestone::getProjectId, milestone.getProjectId())
                .ne(Milestone::getId, milestone.getId())
                .lt(Milestone::getSortOrder, currentSortOrder));
        for (Milestone predecessor : predecessors) {
            if (!STATUS_COMPLETED.equals(predecessor.getStatus())) {
                throw new BusinessException("前置里程碑 " + predecessor.getMilestoneName() + " 未完成，无法跳过");
            }
        }
    }

    /**
     * Build a merged view of the milestone for stage-gate validation by overlaying
     * the non-null fields from the incoming update onto the persisted entity.
     *
     * @param existing   the persisted milestone
     * @param incoming   the update request milestone
     * @return a merged milestone carrying the effective validation-relevant fields
     */
    private Milestone mergeForValidation(Milestone existing, Milestone incoming) {
        Milestone merged = new Milestone();
        merged.setId(existing.getId());
        merged.setProjectId(existing.getProjectId());
        merged.setMilestoneName(existing.getMilestoneName());
        merged.setMilestoneType(incoming.getMilestoneType() != null
                ? incoming.getMilestoneType() : existing.getMilestoneType());
        merged.setSortOrder(incoming.getSortOrder() != null
                ? incoming.getSortOrder() : existing.getSortOrder());
        merged.setPpdiooPhase(incoming.getPpdiooPhase() != null
                ? incoming.getPpdiooPhase() : existing.getPpdiooPhase());
        return merged;
    }
}
