package com.dp.plat.governance.change.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.governance.change.entity.ChangeRequest;

import java.util.List;

/**
 * Service for {@link ChangeRequest}.
 *
 * <p>Implements the change request lifecycle: create → submit → CCB review
 * (approve/reject) → implement → close. Submitting triggers the CCB approval
 * workflow; approving records baseline changes.</p>
 */
public interface IChangeRequestService extends IService<ChangeRequest> {

    /**
     * Create a change request in SUBMITTED status.
     *
     * @param changeRequest change request to create
     * @return operation result containing the created change request
     */
    Result<ChangeRequest> create(ChangeRequest changeRequest);

    /**
     * Update a change request.
     *
     * @param changeRequest change request to update
     * @return operation result
     */
    Result<?> update(ChangeRequest changeRequest);

    /**
     * Delete a change request by id.
     *
     * @param id change request id
     * @return operation result
     */
    Result<?> delete(Long id);

    /**
     * List all change requests.
     *
     * @return operation result containing the list
     */
    Result<List<ChangeRequest>> listAll();

    /**
     * Get a change request by id.
     *
     * @param id change request id
     * @return operation result containing the change request
     */
    Result<ChangeRequest> getById(Long id);

    /**
     * List change requests by project id.
     *
     * @param projectId project id
     * @return operation result containing the list
     */
    Result<List<ChangeRequest>> listByProject(Long projectId);

    /**
     * Submit a change request: transition SUBMITTED → UNDER_REVIEW and start the
     * CCB approval workflow.
     *
     * @param id change request id
     * @return operation result containing the updated change request
     */
    Result<ChangeRequest> submit(Long id);

    /**
     * Approve a change request: transition UNDER_REVIEW → CCB_APPROVED, record
     * baseline changes and mark baseline as updated.
     *
     * @param id           change request id
     * @param approverName approver name
     * @return operation result containing the updated change request
     */
    Result<ChangeRequest> approve(Long id, String approverName);

    /**
     * Reject a change request: transition UNDER_REVIEW → CCB_REJECTED.
     *
     * @param id     change request id
     * @param reason rejection reason
     * @return operation result containing the updated change request
     */
    Result<ChangeRequest> reject(Long id, String reason);

    /**
     * Start implementation: transition CCB_APPROVED → IMPLEMENTING.
     *
     * @param id change request id
     * @return operation result containing the updated change request
     */
    Result<ChangeRequest> implement(Long id);

    /**
     * Close a change request: transition → CLOSED.
     *
     * @param id change request id
     * @return operation result containing the updated change request
     */
    Result<ChangeRequest> close(Long id);

    /**
     * Generate the change request number in format CR-YYYY-XXXX.
     *
     * @return generated change request number
     */
    String generateCrNo();
}
