package com.dp.plat.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Single data point of the project trend chart: the count of projects with the
 * given status created in the given month (yyyy-MM).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "项目趋势数据项")
public class ProjectTrendItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 月份，格式 yyyy-MM */
    @Schema(description = "月份，格式 yyyy-MM")
    private String month;

    /** 项目状态（PENDING/IN_PROGRESS/COMPLETED/...） */
    @Schema(description = "项目状态")
    private String status;

    /** 数量 */
    @Schema(description = "数量")
    private long count;
}
