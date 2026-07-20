package com.dp.plat.common.spi;

import com.dp.plat.common.dto.TaskCompletionViolation;

import java.util.List;

/**
 * 任务完成率校验 SPI（TD-P8-005）。
 *
 * <p>{@code pms-project} 的 {@code validateExitGate} TASK 分支通过本 SPI 跨模块校验
 * {@code pms-implementation} 中阶段的任务完成情况。设计文档 §3.4 定义 TASK 类退出条件为
 * 「阶段内任务完成率达阈值」，本 SPI 提供按阶段查询未完成任务的能力。</p>
 *
 * <p>由 {@code pms-implementation} 模块实现并注册为 Spring Bean，
 * {@code pms-project} 通过 {@code @Autowired(required=false)} 注入。
 * 若模块未加载（bean 不存在），TASK 分支跳过校验（仅 log.warn），
 * 避免在无任务数据时锁死阶段推进。</p>
 */
public interface TaskCompletionChecker {

    /**
     * 查询指定阶段下未完成的任务（status != COMPLETED）。
     *
     * @param phaseId 阶段ID
     * @return 未完成任务违规列表（空列表表示全部完成或无任务）
     */
    List<TaskCompletionViolation> findUncompletedTasks(Long phaseId);
}
