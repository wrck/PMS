package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import com.dp.plat.implementation.service.ITaskRollupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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
     *
     * <p>失败重试策略（TD-P8-007 修复）：同时标注 {@code @Async} 与 {@code @Retryable}，
     * 最多重试 3 次，间隔 1 秒、倍数 2（指数退避）。所有重试均失败后由
     * {@link #recover} 方法执行最终失败回调，记录详细错误日志。
     * Spring 代理顺序确保先异步调度、后在异步线程内执行重试逻辑。</p>
     */
    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2), retryFor = Exception.class)
    @Override
    public void recalculateProgress(Long taskId) {
        if (taskId == null) {
            return;
        }
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
    }

    /**
     * 重试耗尽后的最终失败回调（TD-P8-007 修复）。
     *
     * <p>当 {@link #recalculateProgress} 经 3 次重试后仍抛出异常时调用。
     * 此处仅记录详细错误日志（含任务ID与异常堆栈），不进行额外通知。
     * 后续如需增强，可在此处接入消息队列或告警系统。</p>
     *
     * @param e     最后一次抛出的异常
     * @param taskId 触发汇总的任务ID（与 {@link #recalculateProgress} 参数对应）
     */
    @Recover
    public void recover(Exception e, Long taskId) {
        log.error("任务进度汇总最终失败（已重试 3 次），taskId={}, 异常类型={}, 错误消息={}",
                taskId, e.getClass().getName(), e.getMessage(), e);
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
