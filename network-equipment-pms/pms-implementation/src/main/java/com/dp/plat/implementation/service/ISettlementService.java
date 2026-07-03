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
     * Approve a settlement (status=APPROVED).
     *
     * TODO: push settlement to FP financial system after approval.
     */
    void approve(Long settlementId, String opinion);

    /**
     * Reject a settlement (status=REJECTED).
     */
    void reject(Long settlementId, String opinion);

    /**
     * Push an approved settlement to the FP integration adapter and update
     * push_status.
     *
     * TODO: call FP integration adapter.
     */
    void pushToFp(Long settlementId);

    /**
     * Paginated settlement query with optional filters.
     */
    Page<Settlement> list(int page, int size, Settlement filters);
}
