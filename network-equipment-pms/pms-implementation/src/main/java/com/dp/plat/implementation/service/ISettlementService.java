package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.entity.SettlementDetail;

import java.util.List;

/**
 * Service for {@link Settlement}.
 */
public interface ISettlementService extends IService<Settlement> {

    /**
     * Create a settlement with its line items: generate settlement_no and
     * calculate tax amounts.
     */
    Settlement createSettlement(Settlement settlement, List<SettlementDetail> details);

    /**
     * Approve a settlement (status=APPROVED) and push it to the FP financial
     * system, recording the push outcome on the settlement.
     */
    void approve(Long settlementId, String opinion);

    /**
     * Reject a settlement (status=REJECTED).
     */
    void reject(Long settlementId, String opinion);

    /**
     * Submit a settlement to the FP financial platform via Saga orchestration.
     *
     * <p>The Saga validates the settlement status, updates it to PROCESSING,
     * pushes to FP, pushes an OA todo, notifies the applicant, and finally
     * updates the status to SUBMITTED. If any step fails, compensation is
     * performed in reverse order to restore a consistent state.</p>
     *
     * @param settlementId the settlement id to submit
     * @throws com.dp.plat.common.exception.BusinessException if the settlement
     *         does not exist, the status does not allow submission, or any Saga
     *         step fails (after compensation has been attempted)
     */
    void submit(Long settlementId);

    /**
     * Paginated settlement query with optional filters.
     */
    Page<Settlement> list(int page, int size, Settlement filters);
}
