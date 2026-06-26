package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pm_project_weekly_content")
public class WeeklyContent extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("weeklyId")
    private Long weeklyId;

    @TableField("optionType")
    private Integer optionType;

    @TableField("optionDesc001")
    private String optionDesc001;

    @TableField("optionDesc002")
    private String optionDesc002;

    @TableField("effectiveFrom")
    private LocalDateTime effectiveFrom;

    @TableField("effectiveTo")
    private LocalDateTime effectiveTo;
}
