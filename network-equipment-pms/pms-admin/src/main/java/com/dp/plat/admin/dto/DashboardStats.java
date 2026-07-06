package com.dp.plat.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Dashboard overview statistics aggregated across modules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表盘统计")
public class DashboardStats implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 项目总数 */
    @Schema(description = "项目总数")
    private long projectTotal;

    /** 在库设备数 */
    @Schema(description = "在库设备数")
    private long assetInStock;

    /** 待办任务数 */
    @Schema(description = "待办任务数")
    private long todoCount;

    /** 本月交付项目数 */
    @Schema(description = "本月交付项目数")
    private long monthDelivery;

    /** 进行中项目数 */
    @Schema(description = "进行中项目数")
    private long projectInProgress;

    /** 本月新增项目 */
    @Schema(description = "本月新增项目")
    private long monthNewProject;

    /** 本月新增资产 */
    @Schema(description = "本月新增资产")
    private long monthNewAsset;

    /** 告警数（逾期任务 + 30 天内到期质保） */
    @Schema(description = "告警数")
    private long alertCount;
}
