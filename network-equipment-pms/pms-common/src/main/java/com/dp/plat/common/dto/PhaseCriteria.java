package com.dp.plat.common.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 阶段进入条件（结构化 JSON）
 */
@Data
public class PhaseCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 是否需要前置阶段完成 */
    private Boolean requirePreviousPhaseComplete;

    /** 是否需要审批通过 */
    private Boolean requireApproval;
}
