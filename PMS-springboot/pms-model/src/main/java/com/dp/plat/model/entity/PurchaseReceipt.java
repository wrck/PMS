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
 * PurchaseReceipt entity - migrated from Struts
 */
@Data
@TableName("d365_purchase_receipt")
public class PurchaseReceipt extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("receiptCode")
    private String receiptCode;

    @TableField("purchaseId")
    private Long purchaseId;

    @TableField("receiptDate")
    private LocalDateTime receiptDate;

    @TableField("status")
    private String status;

}