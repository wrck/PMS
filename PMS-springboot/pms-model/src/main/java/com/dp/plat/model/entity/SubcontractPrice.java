package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/** 分包价格 - 对应老系统 SubcontractPrice (13字段) */
@Data
@TableName("pm_subcontract_price")
public class SubcontractPrice extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO) private Long id;
    @TableField("subcontract_id") private Long subcontractId;
    @TableField("product_code") private String productCode;
    @TableField("product_name") private String productName;
    @TableField("product_model") private String productModel;
    @TableField("unit_price") private Double unitPrice;
    @TableField("quantity") private Integer quantity;
    @TableField("amount") private Double amount;
    @TableField("tax_rate") private Double taxRate;
}
