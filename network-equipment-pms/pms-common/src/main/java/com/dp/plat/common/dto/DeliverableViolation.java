package com.dp.plat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 必需交付件违规项（TD-P8-012 SPI 配套）。
 *
 * <p>由 {@code pms-deliverable} 的 {@code MandatoryDeliverableValidator} 实现返回，
 * 供 {@code pms-project} 的 {@code validateExitGate} DELIVERABLE 分支跨模块复用校验逻辑。
 * 字段与 {@code PhaseExitGateViolation}（DELIVERABLE 类型）对齐，便于转换。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliverableViolation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 交付件ID。 */
    private Long deliverableId;

    /** 交付件名称。 */
    private String deliverableName;

    /** 期望状态（如「已批准（PUBLISHED/REFERENCED/ARCHIVED）」）。 */
    private String expectedStatus;

    /** 实际状态。 */
    private String actualStatus;

    /** 是否已批准（PUBLISHED/REFERENCED/ARCHIVED 之一）。 */
    private Boolean approved;
}
