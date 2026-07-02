package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ProjectLog entity - migrated from Struts
 */
@Data
@TableName("pm_project_log")
public class ProjectLog extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("projectId")
    private Long projectId;

    @TableField("operateType")
    private String operateType;

    @TableField("operateContent")
    private String operateContent;

    @TableField("operatePerson")
    private String operatePerson;

    @TableField("operateTime")
    private LocalDateTime operateTime;

    @TableField("oldValue")
    private String oldValue;

    @TableField("newValue")
    private String newValue;

}