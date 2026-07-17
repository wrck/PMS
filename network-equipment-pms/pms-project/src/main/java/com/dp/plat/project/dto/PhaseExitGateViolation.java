package com.dp.plat.project.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 阶段退出条件单项违规。
 *
 * <p>关联设计文档：§3.2 Story 2 验收 1 violations 数组项。
 * 字段对齐设计文档示例（gateType/message/businessId/businessName/expectedStatus/actualStatus）。
 */
@Data
@Builder
public class PhaseExitGateViolation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 违规类型：DELIVERABLE / TASK / MILESTONE / APPROVAL */
    private String gateType;

    /** 违规说明 */
    private String message;

    /** 关联业务 ID（交付件 ID / 任务 ID / 里程碑 ID 等） */
    private Long businessId;

    /** 关联业务名称 */
    private String businessName;

    /** 期望状态 */
    private String expectedStatus;

    /** 实际状态 */
    private String actualStatus;
}
