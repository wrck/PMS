package com.dp.plat.baseline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 单个任务的基线偏差。
 *
 * <p>关联设计文档：§5.5 Story 4 验收 2 — diffs 元素结构。
 * 偏差天数为 {@code 当前 - 基线}（正值表示延后，负值表示提前）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDiff implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务ID。 */
    private Long taskId;

    /** 任务名称。 */
    private String taskName;

    /** 基线开始日期（ISO yyyy-MM-dd）。 */
    private String baselineStart;

    /** 当前开始日期（ISO yyyy-MM-dd）。 */
    private String currentStart;

    /** 开始偏差天数（当前 - 基线）。 */
    private Integer startVariance;

    /** 基线结束日期（ISO yyyy-MM-dd）。 */
    private String baselineEnd;

    /** 当前结束日期（ISO yyyy-MM-dd）。 */
    private String currentEnd;

    /** 结束偏差天数（当前 - 基线）。 */
    private Integer endVariance;

    /** 偏差百分比（|结束偏差| / 基线工期 * 100）。 */
    private Double percentVariance;
}
