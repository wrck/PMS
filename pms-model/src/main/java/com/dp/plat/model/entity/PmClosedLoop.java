package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pm_closed_loop_header")
public class PmClosedLoop extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("projectCode")
    private String projectCode;

    @TableField("projectName")
    private String projectName;

    @TableField("applyState")
    private Integer applyState;

    @TableField("applyBy")
    private String applyBy;

    @TableField("applyTime")
    private LocalDateTime applyTime;

    @TableField("instId")
    private String instId;

    @TableField("evaluationScore")
    private Double evaluationScore;

    @TableField("evaluationComment")
    private String evaluationComment;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String serviceManagerName;

    @TableField(exist = false)
    private String projectManagerName;
}
