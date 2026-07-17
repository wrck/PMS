package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.implementation.dto.TaskProgressVO;
import com.dp.plat.implementation.dto.TaskReviewResult;
import com.dp.plat.implementation.entity.ImplTask;

import java.util.List;

/**
 * Service for {@link ImplTask}.
 */
public interface IImplTaskService extends IService<ImplTask> {

    /**
     * Assign an OEM implementation task (type=OEM, status=PENDING).
     */
    ImplTask assignOemTask(ImplTask task);

    /**
     * Assign an agent implementation task (type=AGENT, status=PENDING).
     */
    ImplTask assignAgentTask(ImplTask task);

    /**
     * Accept a task (status=ACCEPTED).
     */
    void acceptTask(Long taskId);

    /**
     * Start a task (status=IN_PROGRESS).
     */
    void startTask(Long taskId);

    /**
     * Report progress for a task and update the task progress percent.
     */
    void reportProgress(Long taskId, com.dp.plat.implementation.entity.ImplProgress progress);

    /**
     * Complete a task (status=COMPLETED).
     */
    void completeTask(Long taskId, String description);

    /**
     * Confirm a completed task (status=CONFIRMED, record accept info).
     */
    void confirmTask(Long taskId, String opinion);

    /**
     * Reject a task (status=REJECTED).
     */
    void rejectTask(Long taskId, String opinion);

    /**
     * List tasks by project id.
     */
    List<ImplTask> getByProjectId(Long projectId);

    /**
     * Paginated task query with optional filters.
     */
    Page<ImplTask> list(int page, int size, ImplTask filters);

    /**
     * 提交评审 — 含强制检查项校验（Story 3 验收 1）。
     *
     * <p>若任务存在 mandatory=true 且 checked=false 的检查项，抛出
     * {@link com.dp.plat.implementation.exception.TaskChecklistRequiredException}，
     * 拦截状态流转；全部强制检查项已勾选则流转至 REVIEW 状态。</p>
     *
     * @param taskId     任务ID
     * @param operatorId 操作人ID
     * @return 评审提交结果（success=true 表示已进入 REVIEW）
     */
    TaskReviewResult submitForReview(Long taskId, Long operatorId);

    /**
     * 验收任务 — 评审通过，流转至 COMPLETED。
     *
     * @param taskId     任务ID
     * @param operatorId 操作人ID
     * @return 验收结果
     */
    TaskReviewResult approveTask(Long taskId, Long operatorId);

    /**
     * 移动任务（变更父任务），同步更新 taskPath 与 depth（含所有后代）。
     *
     * <p>校验 newParentId 不能是 taskId 的后代（避免环路），并批量更新子树路径。</p>
     *
     * @param taskId      任务ID
     * @param newParentId 新父任务ID（NULL=提升为顶层）
     */
    void moveTask(Long taskId, Long newParentId);

    /**
     * 查询任务子树（基于 task_path 物化路径前缀匹配，含自身）。
     *
     * @param taskId 任务ID
     * @return 子树任务列表（含自身）
     */
    List<ImplTask> getTaskSubtree(Long taskId);

    /**
     * 查询任务进度（含子任务加权汇总）。
     *
     * @param taskId 任务ID
     * @return 进度视图（含递归子任务进度）
     */
    TaskProgressVO getTaskProgress(Long taskId);
}
