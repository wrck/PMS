package com.dp.plat.asset.rma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.asset.rma.dto.RmaKpiDto;
import com.dp.plat.asset.rma.entity.Rma;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for {@link Rma} return merchandise authorization tickets.
 */
public interface IRmaService extends IService<Rma> {

    /**
     * Register a new RMA ticket. Generates the RMA number, sets ticket status
     * REGISTERED and records the registering user.
     */
    boolean create(Rma rma);

    /**
     * Check warranty status for the RMA's asset and transition the ticket from
     * REGISTERED to WARRANTY_CHECKED.
     */
    boolean checkWarranty(Long id);

    /**
     * Issue the RMA, transitioning the ticket from REGISTERED/WARRANTY_CHECKED
     * to RMA_ISSUED.
     */
    boolean issueRma(Long id);

    /**
     * Mark the RMA as returning (RMA_ISSUED → RETURNING).
     */
    boolean markReturning(Long id);

    /**
     * Inspect the returned asset (RETURNING → INSPECTED) and update the asset
     * status according to the repair outcome (repaired → IN_PRODUCTION/COMMISSIONED,
     * scrapped → DECOMMISSIONED).
     */
    boolean inspect(Long id, String notes);

    /**
     * Close the RMA ticket (INSPECTED → CLOSED).
     */
    boolean close(Long id);

    /**
     * List RMA tickets for a project.
     */
    List<Rma> listByProject(Long projectId);

    /**
     * List RMA tickets for an asset.
     */
    List<Rma> listByAsset(Long assetId);

    /**
     * Compute RMA KPIs (total, closed, MTTR, first-pass rate) for the given date range.
     */
    RmaKpiDto kpi(LocalDate startDate, LocalDate endDate);

    /**
     * Generate the next RMA number in the format RMA-YYYY-XXXX.
     */
    String generateRmaNo();
}
