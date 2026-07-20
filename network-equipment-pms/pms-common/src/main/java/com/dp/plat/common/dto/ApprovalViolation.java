package com.dp.plat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 审批状态校验违规项（TD-P8-005 SPI 配套）。
 *
 * <p>由 {@code pms-workflow} 的 {@code ApprovalStatusChecker} 实现返回，
 * 供 {@code pms-project} 的 {@code validateExitGate} APPROVAL 分支跨模块校验关联审批是否通过。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalViolation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 审批记录ID（若存在）。 */
    private Long approvalRecordId;

    /** 审批类型（如 PHASE_EXIT）。 */
    private String approvalType;

    /** 期望状态（APPROVED）。 */
    private String expectedStatus;

    /** 实际状态（PENDING/REJECTED/WITHDRAWN/TIMEOUT 或 null=不存在）。 */
    private String actualStatus;
}
