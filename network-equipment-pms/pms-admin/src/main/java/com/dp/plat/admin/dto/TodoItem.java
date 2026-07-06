package com.dp.plat.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * A single todo item shown on the dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "待办事项")
public class TodoItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务 ID */
    @Schema(description = "任务 ID")
    private Long id;

    /** 标题 */
    @Schema(description = "标题")
    private String title;

    /** 类型：TASK/APPROVAL/PUNCH_LIST/WARRANTY */
    @Schema(description = "类型")
    private String type;

    /** 优先级 HIGH/NORMAL/LOW */
    @Schema(description = "优先级")
    private String priority;

    /** 责任人姓名 */
    @Schema(description = "责任人姓名")
    private String assigneeName;

    /** 截止日期 yyyy-MM-dd */
    @Schema(description = "截止日期")
    private String deadline;

    /** 项目编号 */
    @Schema(description = "项目编号")
    private String projectCode;

    /** 项目名称 */
    @Schema(description = "项目名称")
    private String projectName;

    /** 状态 */
    @Schema(description = "状态")
    private String status;
}
