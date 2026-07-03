package com.dp.plat.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.FinalAcceptance;

/**
 * Service for {@link FinalAcceptance}.
 */
public interface IFinalAcceptanceService extends IService<FinalAcceptance> {

    /**
     * Apply for final acceptance. All project milestones must be completed before applying.
     *
     * @param projectId project id
     * @param report    acceptance report content
     * @return operation result
     */
    Result apply(Long projectId, String report);

    /**
     * Approve a final acceptance application. The project status is set to COMPLETED
     * and bound equipment is released.
     *
     * @param acceptanceId acceptance record id
     * @param opinion      approval opinion
     * @return operation result
     */
    Result approve(Long acceptanceId, String opinion);

    /**
     * Reject a final acceptance application.
     *
     * @param acceptanceId acceptance record id
     * @param opinion      rejection opinion
     * @return operation result
     */
    Result reject(Long acceptanceId, String opinion);

    /**
     * Get the final acceptance record by project id.
     *
     * @param projectId project id
     * @return operation result containing the acceptance record
     */
    Result<FinalAcceptance> getByProjectId(Long projectId);
}
