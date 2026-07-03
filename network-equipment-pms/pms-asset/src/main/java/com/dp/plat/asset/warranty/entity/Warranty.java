package com.dp.plat.asset.warranty.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
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

    private Long assetId;

    private LocalDate startDate;

    private LocalDate endDate;

    /** Warranty duration in months. */
    private Integer durationMonths;

    /** BASIC/PREMIUM/PLATINUM. */
    private String slaLevel;

    private String contractNo;

    private Long projectId;

    private String notes;
}
