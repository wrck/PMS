package com.dp.plat.implementation.advice;

import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.dto.TaskReviewResult;
import com.dp.plat.implementation.exception.TaskChecklistRequiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 任务模块异常处理器。
 *
 * <p>以最高优先级处理 {@link TaskChecklistRequiredException}，将其转换为
 * HTTP 200 + 结构化失败数据（success=false），符合设计文档 §5.4 验收 1 的响应结构。</p>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class TaskExceptionHandler {

    @ExceptionHandler(TaskChecklistRequiredException.class)
    public Result<TaskReviewResult> handleTaskChecklistRequired(TaskChecklistRequiredException e) {
        log.warn("强制检查项未完成，拦截提交评审：taskStatus={}, 未勾选数={}",
                e.getTaskStatus(), e.getUncheckedMandatoryItems().size());
        TaskReviewResult result = TaskReviewResult.builder()
                .success(false)
                .errorCode(e.ERROR_CODE)
                .errorMessage(e.ERROR_MESSAGE)
                .uncheckedMandatoryItems(e.getUncheckedMandatoryItems())
                .taskStatus(e.getTaskStatus())
                .build();
        // 设计要求 code=200 + data.success=false，便于前端按业务结果分支处理
        return Result.ok(result);
    }
}
