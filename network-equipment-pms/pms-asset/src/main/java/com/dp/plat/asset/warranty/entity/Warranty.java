package com.dp.plat.asset.warranty.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Warranty record for an equipment asset, typically initialized after a project's
 * final acceptance is approved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_warranty")
public class Warranty extends BaseEntity {

    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    @NotNull(message = "质保开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "质保结束日期不能为空")
    private LocalDate endDate;

    /** Warranty duration in months. */
    @NotNull(message = "质保月数不能为空")
    @Min(value = 1, message = "质保月数最小为 1")
    private Integer durationMonths;

    /** BASIC/PREMIUM/PLATINUM. */
    @Size(max = 20, message = "SLA等级长度不能超过 20 个字符")
    private String slaLevel;

    @Size(max = 100, message = "合同编号长度不能超过 100 个字符")
    private String contractNo;

    private Long projectId;

    @Size(max = 2000, message = "备注长度不能超过 2000 个字符")
    private String notes;
}
