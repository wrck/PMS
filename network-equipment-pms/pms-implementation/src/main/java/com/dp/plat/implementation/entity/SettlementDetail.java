package com.dp.plat.implementation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称长度不能超过 200 个字符")
    private String itemName;

    @NotNull(message = "工作量不能为空")
    @DecimalMin(value = "0", message = "工作量不能为负数")
    private BigDecimal workQuantity;

    @NotBlank(message = "单位不能为空")
    @Size(max = 20, message = "单位长度不能超过 20 个字符")
    private String unit;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0", message = "单价不能为负数")
    private BigDecimal unitPrice;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0", message = "金额不能为负数")
    private BigDecimal amount;

    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remarks;
}
