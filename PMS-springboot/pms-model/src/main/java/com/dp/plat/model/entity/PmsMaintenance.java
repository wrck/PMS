package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pm_project_maintenance")
public class PmsMaintenance extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("projectCode")
    private String projectCode;

    @TableField("projectName")
    private String projectName;

    @TableField("maintenanceType")
    private String maintenanceType;

    @TableField("maintenanceContent")
    private String maintenanceContent;

    @TableField("maintenanceDate")
    private LocalDateTime maintenanceDate;

    @TableField("operator")
    private String operator;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("createBy")
    private String createBy;
}
