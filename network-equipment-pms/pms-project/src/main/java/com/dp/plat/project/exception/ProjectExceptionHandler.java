package com.dp.plat.project.exception;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.dto.PhaseExitGateResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 项目域异常处理器。
 *
 * <p>针对项目域自定义异常（PhaseExitGateFailedException 等），返回与设计文档一致的
 * {@code code=200} 结构化响应体（{@code data.success=false} + 违规明细），而非通用
 * {@code Result.fail}（code=500）。{@link Order} 设为最高优先级，确保优先于
 * pms-common 的 {@code GlobalExceptionHandler} 命中。
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ProjectExceptionHandler {

    @ExceptionHandler(PhaseExitGateFailedException.class)
    public Result<PhaseExitGateResult> handlePhaseExitGateFailed(PhaseExitGateFailedException e) {
        PhaseExitGateResult result = new PhaseExitGateResult();
        result.setSuccess(false);
        result.setErrorCode("PHASE_EXIT_GATE_FAILED");
        result.setErrorMessage(e.getMessage());
        result.setViolations(e.getViolations());
        return Result.ok(result);
    }
}
