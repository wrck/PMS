package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pm_project_task")
public class PmsProjectTask extends BaseEntity {

    @TableId(value = "taskId", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("contractNo")
    private String contractNo;

    @TableField("taskTypeCode")
    private String taskTypeCode;

    @TableField("taskTypeId")
    private Long taskTypeId;

    @TableField("eventPlanHappenDate")
    private LocalDateTime eventPlanHappenDate;

    @TableField("eventPlanHappenDateENG")
    private LocalDateTime eventPlanHappenDateENG;

    @TableField("eventActualFinishDate")
    private LocalDateTime eventActualFinishDate;

    @TableField("visibleFlag")
    private Integer visibleFlag;

    @TableField("effectiveFrom")
    private LocalDateTime effectiveFrom;

    @TableField("effectiveTo")
    private LocalDateTime effectiveTo;

    @TableField(exist = false)
    private String eventValue;

    @TableField(exist = false)
    private String eventKeyStr;
}
