package com.dp.plat.project.deliverable.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.common.result.Result;
import com.dp.plat.project.deliverable.entity.DeliverableChecklist;

import java.util.List;

/**
 * Service for {@link DeliverableChecklist}.
 *
 * @deprecated 终验校验已改为直接查 pms_deliverable 表，本服务保留用于历史兼容，将在下版本删除。
 */
@Deprecated
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

    /**
     * 标记指定清单项已上传附件。
     *
     * <p>专用端点：仅更新 attachmentId / uploaded / checkedAt 字段，绕开
     * {@link #update(DeliverableChecklist)} 的 {@code @Valid} 全字段校验，
     * 避免前端只传部分字段时触发 projectId/deliverableType 非空校验失败。</p>
     *
     * @param id           清单项 id
     * @param attachmentId 附件 id
     * @return 操作结果
     */
    Result<?> markUploaded(Long id, Long attachmentId);

    /**
     * 取消指定清单项的上传标记。
     *
     * @param id 清单项 id
     * @return 操作结果
     */
    Result<?> cancelUploaded(Long id);
}
