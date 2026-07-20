package com.dp.plat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 任务完成校验违规项（TD-P8-005 SPI 配套）。
 *
 * <p>由 {@code pms-implementation} 的 {@code TaskCompletionChecker} 实现返回，
 * 供 {@code pms-project} 的 {@code validateExitGate} TASK 分支跨模块校验任务完成率。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionViolation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务ID。 */
    private Long taskId;

    /** 任务名称。 */
    private String taskName;

    /** 期望状态（COMPLETED）。 */
    private String expectedStatus;

    /** 实际状态（PENDING/IN_PROGRESS 等）。 */
    private String actualStatus;
}
