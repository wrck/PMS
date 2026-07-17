package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.implementation.service.ITaskRollupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务进度汇总服务实现 — 异步按计划工时加权汇总，递归向上更新所有祖先。
 *
 * <p>关联设计文档 §3.3 Story 3 验收 2：进度汇总。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskRollupServiceImpl implements ITaskRollupService {

    /** 权重缺省值（当子任务 plannedHours 为空或非正时使用，确保每个子任务都参与汇总）。 */
    private static final int DEFAULT_WEIGHT = 1;

    private final ImplTaskMapper implTaskMapper;

    /**
     * 异步重新计算任务进度并向上回溯所有祖先。
     *
     * <p>汇总逻辑：从 {@code taskId} 开始，逐层向上对其父任务执行
     * "父任务进度 = Σ(直接子任务 progress × weight) / Σ(weight)"，其中 weight = plannedHours
     * （缺省 1）。叶子节点（无子任务）跳过自身更新，仅作为权重来源参与父任务汇总。</p>
     */
    @Async
    @Override
    public void recalculateProgress(Long taskId) {
        if (taskId == null) {
            return;
        }
        try {
            Long currentId = taskId;
            // 向上回溯祖先链路，逐层重算
            while (currentId != null) {
                ImplTask task = implTaskMapper.selectById(currentId);
                if (task == null) {
                    break;
                }
                // 查询直接子任务（depth+1 层），子任务的 progress 已是其自身子树的汇总结果
                List<ImplTask> children = implTaskMapper.selectList(
                        new LambdaQueryWrapper<ImplTask>()
                                .eq(ImplTask::getParentTaskId, currentId));
                if (children != null && !children.isEmpty()) {
                    int rolledUp = weightedAverage(children);
                    // 仅当汇总值变化时更新，减少无意义写操作
                    if (task.getProgress() == null || task.getProgress() != rolledUp) {
                        task.setProgress(rolledUp);
                        implTaskMapper.updateById(task);
                        log.debug("任务 #{} 进度汇总更新为 {}（子任务数={}）", currentId, rolledUp, children.size());
                    }
                }
                // 继续向上回溯到父任务
                currentId = task.getParentTaskId();
            }
        } catch (Exception e) {
            // 异步任务不阻塞主流程，异常仅记录日志
            log.error("任务进度汇总失败，taskId={}: {}", taskId, e.getMessage(), e);
        }
    }

    /**
     * 按计划工时加权计算子任务汇总进度。
     *
     * <p>公式：rolledUp = Σ(child.progress × weight) / Σ(weight)，
     * weight = plannedHours（缺省 1）。结果四舍五入为整数百分比。</p>
     */
    private int weightedAverage(List<ImplTask> children) {
        double totalWeight = 0;
        double weightedSum = 0;
        for (ImplTask child : children) {
            int weight = resolveWeight(child);
            int progress = child.getProgress() != null ? child.getProgress() : 0;
            weightedSum += (double) progress * weight;
            totalWeight += weight;
        }
        if (totalWeight <= 0) {
            return 0;
        }
        return (int) Math.round(weightedSum / totalWeight);
    }

    /**
     * 解析子任务权重：优先使用 plannedHours，为空或非正时缺省 1。
     */
    private int resolveWeight(ImplTask task) {
        Integer plannedHours = task.getPlannedHours();
        if (plannedHours != null && plannedHours > 0) {
            return plannedHours;
        }
        return DEFAULT_WEIGHT;
    }
}
