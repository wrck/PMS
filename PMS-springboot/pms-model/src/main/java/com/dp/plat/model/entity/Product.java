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
 * Product entity - migrated from Struts
 */
@Data
@TableName("pm_product")
public class Product extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("productCode")
    private String productCode;

    @TableField("productName")
    private String productName;

    @TableField("productType")
    private String productType;

    @TableField("productDesc")
    private String productDesc;

    @TableField("status")
    private Integer status;

}