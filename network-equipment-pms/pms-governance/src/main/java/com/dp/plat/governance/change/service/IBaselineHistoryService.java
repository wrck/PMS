package com.dp.plat.governance.change.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.governance.change.entity.BaselineHistory;

import java.util.List;

/**
 * Service for {@link BaselineHistory}.
 */
public interface IBaselineHistoryService extends IService<BaselineHistory> {

    /**
     * Record a single baseline change entry.
     *
     * @param projectId      project id
     * @param changeRequestId change request id
     * @param crNo           change request number
     * @param changeType     change type (SCHEDULE, COST, SCOPE)
     * @param fieldName      field name that changed
     * @param oldValue       old value
     * @param newValue       new value
     * @param changedBy      user who performed the change
     * @return the created baseline history record
     */
    BaselineHistory recordBaselineChange(Long projectId, Long changeRequestId, String crNo,
                                         String changeType, String fieldName,
                                         String oldValue, String newValue, String changedBy);

    /**
     * List baseline history records for a project.
     *
     * @param projectId project id
     * @return list of baseline history records
     */
    List<BaselineHistory> listByProject(Long projectId);
}
