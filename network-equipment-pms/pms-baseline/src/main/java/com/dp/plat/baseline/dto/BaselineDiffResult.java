package com.dp.plat.baseline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 基线偏差分析结果。
 *
 * <p>关联设计文档：§5.5 Story 4 验收 2 — 响应 data 结构。
 * 包含基线摘要、逐任务偏差列表、偏差任务总数及是否需要审批。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaselineDiffResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 基线摘要。 */
    private BaselineInfo baseline;

    /** 逐任务偏差列表。 */
    private List<TaskDiff> diffs;

    /** 偏差任务总数（开始或结束偏差非 0 的任务数）。 */
    private Integer totalVarianced;

    /** 是否需要审批（任一任务偏差超阈值）。 */
    private Boolean needsApproval;

    /** 审批原因（偏差超阈值时填充）。 */
    private String approvalReason;

    /**
     * 基线摘要信息。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaselineInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Long id;
        private String baselineName;
        private String status;
        private LocalDateTime approvedAt;
    }
}
