package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pm_prob_header")
public class PmsProb extends BaseEntity {
    @TableId(value = "probId", type = IdType.AUTO)
    private Long id;
    @TableField("probCode")
    private String probCode;
    @TableField("probTitle")
    private String probTitle;
    @TableField("probContent")
    private String probContent;
    @TableField("probType")
    private Integer probType;
    @TableField("probLevel")
    private Integer probLevel;
    @TableField("probState")
    private Integer probState;
    @TableField("trackType")
    private String trackType;
    @TableField("affectRange")
    private String affectRange;
    @TableField("solutionType")
    private String solutionType;
    @TableField("createBy")
    private String createBy;
    @TableField("createTime")
    private LocalDateTime createTime;
    @TableField("updateTime")
    private LocalDateTime updateTime;
}
