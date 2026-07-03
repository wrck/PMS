package com.dp.plat.asset.rma.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * KPI summary for RMA tickets within a date range.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RmaKpiDto {

    /** Total number of RMA tickets registered in the range. */
    private long totalCount;

    /** Number of RMA tickets closed in the range. */
    private long closedCount;

    /** Mean Time To Repair: average hours from registeredAt to closedAt for closed RMAs. */
    private BigDecimal mttrHours;

    /** First-pass yield: percentage of inspected RMAs closed without re-open (0-100). */
    private BigDecimal firstPassRate;
}
