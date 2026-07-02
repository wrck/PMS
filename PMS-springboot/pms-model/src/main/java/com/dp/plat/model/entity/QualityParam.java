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
 * QualityParam entity - migrated from Struts
 */
@Data
@TableName("pm_quality_param")
public class QualityParam extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("paramCode")
    private String paramCode;

    @TableField("paramName")
    private String paramName;

    @TableField("paramValue")
    private String paramValue;

    @TableField("paramDesc")
    private String paramDesc;

}