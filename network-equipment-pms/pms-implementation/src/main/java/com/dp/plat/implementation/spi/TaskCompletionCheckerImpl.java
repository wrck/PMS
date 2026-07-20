package com.dp.plat.implementation.spi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.dto.TaskCompletionViolation;
import com.dp.plat.common.spi.TaskCompletionChecker;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务完成率校验 SPI 实现（TD-P8-005）。
 *
 * <p>实现 {@link TaskCompletionChecker}，供 {@code pms-project} 的 {@code validateExitGate}
 * TASK 分支跨模块查询阶段下未完成任务。</p>
 *
 * <p>判定逻辑：查询 {@code pms_impl_task} 中 {@code phaseId = ?} 且 {@code status != COMPLETED}
 * 的任务，返回未完成违规列表。空列表表示该阶段无任务或全部已完成。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCompletionCheckerImpl implements TaskCompletionChecker {

    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String EXPECTED_STATUS = "COMPLETED";

    private final ImplTaskMapper implTaskMapper;

    @Override
    public List<TaskCompletionViolation> findUncompletedTasks(Long phaseId) {
        if (phaseId == null) {
            return List.of();
        }
        List<ImplTask> tasks = implTaskMapper.selectList(new LambdaQueryWrapper<ImplTask>()
                .eq(ImplTask::getPhaseId, phaseId)
                .ne(ImplTask::getStatus, STATUS_COMPLETED));
        if (tasks == null || tasks.isEmpty()) {
            return List.of();
        }
        log.info("阶段任务完成校验：phaseId={} 未完成任务数={}", phaseId, tasks.size());
        return tasks.stream()
                .map(t -> TaskCompletionViolation.builder()
                        .taskId(t.getId())
                        .taskName(t.getTaskName())
                        .expectedStatus(EXPECTED_STATUS)
                        .actualStatus(t.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
