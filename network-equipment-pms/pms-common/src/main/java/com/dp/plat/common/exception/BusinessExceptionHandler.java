package com.dp.plat.common.exception;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 业务异常处理器。
 *
 * <p>处理 PMS 自定义的 {@link BusinessException}，将其转换为 yudao {@link CommonResult} 格式，
 * 避免被 yudao {@code GlobalExceptionHandler} 的兜底逻辑当作系统异常返回 500 "系统异常"。</p>
 *
 * <p>优先级低于 {@code ProjectExceptionHandler}（HIGHEST_PRECEDENCE，处理
 * {@code PhaseExitGateFailedException} / {@code SubprojectNotClosedException}
 * 等 BusinessException 子类），确保子类异常优先由域级处理器处理。</p>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public CommonResult<?> handleBusinessException(BusinessException ex) {
        log.warn("[handleBusinessException] code={}, message={}", ex.getCode(), ex.getMessage());
        return CommonResult.error(ex.getCode(), ex.getMessage());
    }
}
