package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pm_project_weekly")
public class PmsProjectWeekly extends BaseEntity {
    @TableId(value = "weeklyId", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("currentTask")
    private String currentTask;

    @TableField("taskStartTime")
    private LocalDateTime taskStartTime;

    @TableField("taskEndTime")
    private LocalDateTime taskEndTime;

    @TableField("taskDeviation")
    private String taskDeviation;

    @TableField("remark")
    private String remark;

    @TableField("weeklyStartTime")
    private LocalDateTime weeklyStartTime;

    @TableField("weeklyEndTime")
    private LocalDateTime weeklyEndTime;

    @TableField("weeklyState")
    private Integer weeklyState;

    @TableField("createBy")
    private String createBy;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateBy")
    private String updateBy;

    @TableField("updateTime")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String weeklyStateName;
}
