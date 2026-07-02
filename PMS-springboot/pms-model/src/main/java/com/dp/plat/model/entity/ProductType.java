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
 * ProductType entity - migrated from Struts
 */
@Data
@TableName("pm_product_type")
public class ProductType extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("typeCode")
    private String typeCode;

    @TableField("typeName")
    private String typeName;

    @TableField("typeDesc")
    private String typeDesc;

    @TableField("status")
    private Integer status;

}