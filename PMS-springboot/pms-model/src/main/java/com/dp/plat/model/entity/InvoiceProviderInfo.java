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
 * InvoiceProviderInfo entity - migrated from Struts
 */
@Data
@TableName("fp_invoice_provider_info")
public class InvoiceProviderInfo extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("providerCode")
    private String providerCode;

    @TableField("providerName")
    private String providerName;

    @TableField("taxNo")
    private String taxNo;

    @TableField("bankName")
    private String bankName;

    @TableField("bankAccount")
    private String bankAccount;

    @TableField("address")
    private String address;

    @TableField("phone")
    private String phone;

}