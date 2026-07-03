package com.dp.plat.implementation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Settlement line item entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_settlement_detail")
public class SettlementDetail extends BaseEntity {

    private Long settlementId;

    private String itemName;

    private BigDecimal workQuantity;

    private String unit;

    private BigDecimal unitPrice;

    private BigDecimal amount;

    private String remarks;
}
