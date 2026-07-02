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
 * Facilitator entity - migrated from Struts
 */
@Data
@TableName("pm_facilitator")
public class Facilitator extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("facilitatorCode")
    private String facilitatorCode;

    @TableField("facilitatorName")
    private String facilitatorName;

    @TableField("contactPerson")
    private String contactPerson;

    @TableField("contactPhone")
    private String contactPhone;

    @TableField("contactEmail")
    private String contactEmail;

    @TableField("status")
    private Integer status;

}