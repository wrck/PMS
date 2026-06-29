package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pm_project_weekly_feedback")
public class WeeklyFeedback extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("weeklyId")
    private Long weeklyId;

    @TableField("feedback")
    private String feedback;

    @TableField("feedbacker")
    private String feedbacker;

    @TableField("feedbackTime")
    private LocalDateTime feedbackTime;

    @TableField(exist = false)
    private String feedbackerName;
}
