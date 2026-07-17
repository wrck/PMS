package com.dp.plat.implementation.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 任务进度视图对象（含子任务汇总）。
 *
 * <p>关联设计文档 §5.4 Story 3 验收 2：进度汇总响应结构。</p>
 */
@Data
@Builder
public class TaskProgressVO {

    /** 任务ID。 */
    private Long taskId;

    /** 任务名称。 */
    private String taskName;

    /** 任务自身进度（叶子节点为实际填报值，父任务为汇总值）。 */
    private Integer selfProgress;

    /** 汇总进度（基于子任务加权计算；无子任务时等于 selfProgress）。 */
    private Integer rolledUpProgress;

    /** 直接子任务总数。 */
    private Integer totalSubtasks;

    /** 已完成子任务数（状态为 COMPLETED/CONFIRMED）。 */
    private Integer completedSubtasks;

    /** 任务状态。 */
    private String status;

    /** 直接子任务的进度视图（递归）。 */
    private List<TaskProgressVO> children;
}
