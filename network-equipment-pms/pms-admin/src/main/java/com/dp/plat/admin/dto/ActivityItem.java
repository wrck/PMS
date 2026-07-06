package com.dp.plat.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * A single activity record shown on the dashboard timeline.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "近期动态")
public class ActivityItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 日志 ID */
    @Schema(description = "ID")
    private Long id;

    /** 类型：LOGIN/OPER/SCHEDULE/INTEGRATION */
    @Schema(description = "类型")
    private String type;

    /** 描述 */
    @Schema(description = "描述")
    private String description;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /** 创建时间（yyyy-MM-dd HH:mm:ss） */
    @Schema(description = "创建时间")
    private String createdAt;

    /** 业务类型 */
    @Schema(description = "业务类型")
    private String bizType;

    /** 业务 ID */
    @Schema(description = "业务 ID")
    private Long bizId;
}
