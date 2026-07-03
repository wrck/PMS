package com.dp.plat.project.deliverable.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.deliverable.entity.DeliverableChecklist;

import java.util.List;

/**
 * Service for {@link DeliverableChecklist}.
 */
public interface IDeliverableChecklistService extends IService<DeliverableChecklist> {

    /**
     * Create a checklist record.
     *
     * @param checklist checklist record to create
     * @return operation result containing the created record
     */
    Result<DeliverableChecklist> create(DeliverableChecklist checklist);

    /**
     * Update a checklist record.
     *
     * @param checklist checklist record to update
     * @return operation result
     */
    Result<?> update(DeliverableChecklist checklist);

    /**
     * Delete a checklist record by id.
     *
     * @param id checklist record id
     * @return operation result
     */
    Result<?> delete(Long id);

    /**
     * Get a checklist record by id.
     *
     * @param id checklist record id
     * @return operation result containing the record
     */
    Result<DeliverableChecklist> getById(Long id);

    /**
     * List checklist records by project id.
     *
     * @param projectId project id
     * @return operation result containing the record list
     */
    Result<List<DeliverableChecklist>> listByProject(Long projectId);

    /**
     * Initialise the 8 standard deliverable checklist records for a project.
     * If records already exist for the project, the existing records are returned
     * without creating duplicates.
     *
     * @param projectId project id
     * @return operation result containing the initialised checklist records
     */
    Result<List<DeliverableChecklist>> initChecklist(Long projectId);
}
