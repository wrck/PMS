package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
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
}
