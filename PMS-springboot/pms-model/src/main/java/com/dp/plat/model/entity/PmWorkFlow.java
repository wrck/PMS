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
 * PmWorkFlow entity - migrated from Struts
 */
@Data
@TableName("pm_workflow")
public class PmWorkFlow extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("processInstanceId")
    private String processInstanceId;

    @TableField("processDefinitionKey")
    private String processDefinitionKey;

    @TableField("businessId")
    private Long businessId;

    @TableField("businessType")
    private String businessType;

    @TableField("startPerson")
    private String startPerson;

    @TableField("startTime")
    private LocalDateTime startTime;

    @TableField("status")
    private String status;

}