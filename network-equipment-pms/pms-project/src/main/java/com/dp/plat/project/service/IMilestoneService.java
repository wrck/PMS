package com.dp.plat.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.MilestoneGroupDto;
import com.dp.plat.project.entity.Milestone;

import java.util.List;

/**
 * Service for {@link Milestone}.
 */
public interface IMilestoneService extends IService<Milestone> {

    /**
     * Create a milestone.
     *
     * @param milestone milestone to create
     * @return operation result
     */
    Result createMilestone(Milestone milestone);

    /**
     * Update a milestone.
     *
     * @param milestone milestone to update
     * @return operation result
     */
    Result updateMilestone(Milestone milestone);

    /**
     * Delete a milestone by id.
     *
     * @param id milestone id
     * @return operation result
     */
    Result deleteMilestone(Long id);

    /**
     * List milestones by project id.
     *
     * @param projectId project id
     * @return operation result containing the milestone list
     */
    Result<List<Milestone>> listByProjectId(Long projectId);

    /**
     * Update milestone progress: set the actual completion date and description,
     * then recalculate the parent project's progress percentage.
     *
     * @param milestoneId milestone id
     * @param actualDate  actual completion date (ISO yyyy-MM-dd)
     * @param description progress description
     * @return operation result
     */
    Result updateProgress(Long milestoneId, String actualDate, String description);

    /**
     * Recalculate the project progress as completed milestones / total milestones * 100.
     *
     * @param projectId project id
     * @return the recalculated progress percentage
     */
    int recalculateProjectProgress(Long projectId);

    /**
     * Build the milestone dashboard view for a project, grouping milestones by PPDIOO phase.
     *
     * @param projectId project id
     * @return operation result containing the grouped milestone list
     */
    Result<List<MilestoneGroupDto>> dashboardByPpdiooPhase(Long projectId);
}
