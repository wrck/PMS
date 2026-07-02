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
 * PurchaseReceiptLine entity - migrated from Struts
 */
@Data
@TableName("d365_purchase_receipt_line")
public class PurchaseReceiptLine extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("receiptId")
    private Long receiptId;

    @TableField("itemCode")
    private String itemCode;

    @TableField("receivedQty")
    private Integer receivedQty;

}