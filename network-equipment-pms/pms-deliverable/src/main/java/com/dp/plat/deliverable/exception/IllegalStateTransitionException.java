package com.dp.plat.deliverable.exception;

import com.dp.plat.common.exception.BusinessException;
import lombok.Getter;

import java.io.Serial;

/**
 * 交付件状态机非法流转异常。
 *
 * <p>关联设计文档：§3.4 交付件状态机 7 态（行 393-428）。
 * 当 {@code DeliverableService.transition} 检测到当前状态不允许流转到目标状态时抛出。
 * 继承 {@link BusinessException} 以复用全局异常处理体系（统一转换为失败响应）。</p>
 */
@Getter
public class IllegalStateTransitionException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 交付件ID。 */
    private final Long deliverableId;

    /** 当前状态码。 */
    private final String currentStatus;

    /** 目标状态码。 */
    private final String targetStatus;

    public IllegalStateTransitionException(Long deliverableId, String currentStatus, String targetStatus) {
        super(buildMessage(deliverableId, currentStatus, targetStatus));
        this.deliverableId = deliverableId;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    public IllegalStateTransitionException(Long deliverableId, String currentStatus, String targetStatus, String hint) {
        super(buildMessage(deliverableId, currentStatus, targetStatus, hint));
        this.deliverableId = deliverableId;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    private static String buildMessage(Long id, String current, String target) {
        return String.format("交付件[%d]状态非法流转：%s → %s（参考状态机：DRAFT→SUBMITTED→REVIEWED→SIGNED→PUBLISHED→REFERENCED→ARCHIVED）",
                id, current, target);
    }

    private static String buildMessage(Long id, String current, String target, String hint) {
        return String.format("交付件[%d]状态非法流转：%s → %s。%s",
                id, current, target, hint);
    }
}
