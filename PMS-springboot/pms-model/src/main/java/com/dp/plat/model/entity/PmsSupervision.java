package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pm_project_supervision")
public class PmsSupervision extends BaseEntity {
    @TableId(value = "supervisionId", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("projectCode")
    private String projectCode;

    @TableField("projectName")
    private String projectName;

    @TableField("officeCode")
    private String officeCode;

    @TableField("supervisionType")
    private String supervisionType;

    @TableField("supervisionContent")
    private String supervisionContent;

    @TableField("supervisionResult")
    private String supervisionResult;

    @TableField("supervisionDate")
    private LocalDateTime supervisionDate;

    @TableField("supervisor")
    private String supervisor;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("createBy")
    private String createBy;

    @TableField(exist = false)
    private String officeName;
}
