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
 * PurchaseLine entity - migrated from Struts
 */
@Data
@TableName("d365_purchase_line")
public class PurchaseLine extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("purchaseId")
    private Long purchaseId;

    @TableField("itemCode")
    private String itemCode;

    @TableField("itemName")
    private String itemName;

    @TableField("quantity")
    private Integer quantity;

    @TableField("unitPrice")
    private java.math.BigDecimal unitPrice;

}