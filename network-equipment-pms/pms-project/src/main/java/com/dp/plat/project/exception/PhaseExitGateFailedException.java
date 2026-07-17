package com.dp.plat.project.exception;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.project.dto.PhaseExitGateViolation;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

/**
 * 阶段退出条件未满足异常。
 *
 * <p>关联设计文档：§3.2 Story 2 验收 1。
 * 由 {@code advancePhase} 在 4 类退出条件（requiredDeliverables / requiredTasks /
 * requiredMilestones / requiredApprovals）任一未满足时抛出。
 *
 * <p>注：继承 {@link BusinessException} 以便被全局异常体系识别；但响应格式由
 * {@link ProjectExceptionHandler} 专门处理为 {@code Result.ok(PhaseExitGateResult)}
 * （code=200、data.success=false、data.violations=...），与设计文档响应体一致。
 */
@Getter
public class PhaseExitGateFailedException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<PhaseExitGateViolation> violations;

    public PhaseExitGateFailedException(String message, List<PhaseExitGateViolation> violations) {
        super(message);
        this.violations = violations;
    }
}
