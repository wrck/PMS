package com.dp.plat.project.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 阶段退出条件校验结果（作为 Result.data 返回，code=200）。
 *
 * <p>关联设计文档：§3.2 Story 2 验收 1。
 * 推进失败时 {@code success=false}，{@code violations} 列出未满足条件；
 * 推进成功时由 service 直接返回更新后的 ProjectPhase，不使用本类。
 */
@Data
public class PhaseExitGateResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String errorCode;
    private String errorMessage;
    private List<PhaseExitGateViolation> violations;
}
