package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pm_project_deliver")
public class PmsProjectDeliver extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("deliverType")
    private String deliverType;

    @TableField("deliverName")
    private String deliverName;

    @TableField("deliverDesc")
    private String deliverDesc;

    @TableField("fileIds")
    private String fileIds;

    @TableField("status")
    private Integer status;

    @TableField("createBy")
    private String createBy;

    @TableField("createTime")
    private LocalDateTime createTime;
}
