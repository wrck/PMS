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
 * Purchase entity - migrated from Struts
 */
@Data
@TableName("d365_purchase")
public class Purchase extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("purchaseCode")
    private String purchaseCode;

    @TableField("purchaseName")
    private String purchaseName;

    @TableField("supplierCode")
    private String supplierCode;

    @TableField("status")
    private String status;

    @TableField("createTime")
    private LocalDateTime createTime;

}