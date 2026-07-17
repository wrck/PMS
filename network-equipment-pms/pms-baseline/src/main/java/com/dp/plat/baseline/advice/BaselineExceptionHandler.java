package com.dp.plat.baseline.advice;

import com.dp.plat.baseline.dto.DependencyCycleResult;
import com.dp.plat.baseline.exception.CycleDetectedException;
import com.dp.plat.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 依赖与基线模块异常处理器。
 *
 * <p>以最高优先级处理 {@link CycleDetectedException}，将其转换为
 * HTTP 200 + 结构化失败数据（success=false、errorCode=CYCLE_DETECTED、cyclePath），
 * 符合设计文档 §5.5 Story 4 验收 1 的响应结构。</p>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class BaselineExceptionHandler {

    @ExceptionHandler(CycleDetectedException.class)
    public Result<DependencyCycleResult> handleCycleDetected(CycleDetectedException e) {
        log.warn("检测到循环依赖，拦截保存依赖：{}", e.getMessage());
        DependencyCycleResult result = DependencyCycleResult.builder()
                .success(false)
                .errorCode(CycleDetectedException.ERROR_CODE)
                .errorMessage(e.getMessage())
                .cyclePath(e.getCyclePath())
                .build();
        // 设计要求 code=200 + data.success=false，便于前端按业务结果分支处理
        return Result.ok(result);
    }
}
