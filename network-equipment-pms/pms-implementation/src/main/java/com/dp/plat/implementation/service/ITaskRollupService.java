package com.dp.plat.implementation.service;

/**
 * 任务进度汇总服务（Story 3 验收 2）。
 *
 * <p>父任务进度 = Σ(子任务 progress × weight) / Σ(weight)，权重默认为 plannedHours
 * （设计文档 §3.3 验收 2）。子任务 progress 或 status 变更后异步触发父链路汇总，
 * 避免阻塞主流程。</p>
 */
public interface ITaskRollupService {

    /**
     * 重新计算指定任务的进度（基于其直接子任务加权汇总），并递归向上更新所有祖先任务。
     *
     * <p>异步执行（{@code @Async}），不阻塞调用方。若任务无子任务（叶子节点），
     * 则跳过自身进度更新，直接向上回溯到父任务进行汇总。</p>
     *
     * @param taskId 发生变更的任务ID（汇总将从其父任务开始逐层向上）
     */
    void recalculateProgress(Long taskId);
}
