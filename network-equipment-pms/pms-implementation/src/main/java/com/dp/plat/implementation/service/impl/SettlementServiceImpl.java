package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.entity.SettlementDetail;
import com.dp.plat.implementation.mapper.SettlementDetailMapper;
import com.dp.plat.implementation.mapper.SettlementMapper;
import com.dp.plat.implementation.service.ISettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of {@link ISettlementService}.
 */
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl extends ServiceImpl<SettlementMapper, Settlement> implements ISettlementService {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_PUSHED = "PUSHED";

    public static final String PUSH_SUCCESS = "SUCCESS";
    public static final String PUSH_FAILED = "FAILED";

    private static final DateTimeFormatter NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("13.00");

    private final SettlementDetailMapper settlementDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Settlement createSettlement(Settlement settlement, List<SettlementDetail> details) {
        // Calculate line item amounts and total amount from details when provided.
        if (details != null && !details.isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            for (SettlementDetail detail : details) {
                if (detail.getAmount() == null && detail.getWorkQuantity() != null && detail.getUnitPrice() != null) {
                    detail.setAmount(detail.getWorkQuantity().multiply(detail.getUnitPrice())
                            .setScale(2, RoundingMode.HALF_UP));
                }
                if (detail.getAmount() != null) {
                    total = total.add(detail.getAmount());
                }
            }
            settlement.setTotalAmount(total);
        }

        BigDecimal totalAmount = settlement.getTotalAmount() == null ? BigDecimal.ZERO : settlement.getTotalAmount();
        BigDecimal taxRate = settlement.getTaxRate() == null ? DEFAULT_TAX_RATE : settlement.getTaxRate();
        BigDecimal taxAmount = totalAmount.multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalWithTax = totalAmount.add(taxAmount);

        settlement.setTaxRate(taxRate);
        settlement.setTaxAmount(taxAmount);
        settlement.setTotalWithTax(totalWithTax);
        settlement.setSettlementNo(generateSettlementNo());
        settlement.setStatus(STATUS_PENDING);
        settlement.setApplyUserId(SecurityUtils.getCurrentUserId());
        settlement.setApplyUserName(SecurityUtils.getCurrentUsername());
        settlement.setApplyTime(LocalDateTime.now());

        this.save(settlement);

        if (details != null && !details.isEmpty()) {
            for (SettlementDetail detail : details) {
                detail.setSettlementId(settlement.getId());
                settlementDetailMapper.insert(detail);
            }
        }
        return settlement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long settlementId, String opinion) {
        Settlement settlement = loadOrThrow(settlementId);
        if (!STATUS_PENDING.equals(settlement.getStatus())) {
            throw new BusinessException("当前结算单状态不允许审批");
        }
        settlement.setStatus(STATUS_APPROVED);
        settlement.setApproveOpinion(opinion);
        settlement.setApproveUserId(SecurityUtils.getCurrentUserId());
        settlement.setApproveUserName(SecurityUtils.getCurrentUsername());
        settlement.setApproveTime(LocalDateTime.now());
        this.updateById(settlement);

        // TODO: push the approved settlement to the FP financial system.
        // pushToFp(settlementId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long settlementId, String opinion) {
        Settlement settlement = loadOrThrow(settlementId);
        if (!STATUS_PENDING.equals(settlement.getStatus())) {
            throw new BusinessException("当前结算单状态不允许驳回");
        }
        settlement.setStatus(STATUS_REJECTED);
        settlement.setApproveOpinion(opinion);
        settlement.setApproveUserId(SecurityUtils.getCurrentUserId());
        settlement.setApproveUserName(SecurityUtils.getCurrentUsername());
        settlement.setApproveTime(LocalDateTime.now());
        this.updateById(settlement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushToFp(Long settlementId) {
        Settlement settlement = loadOrThrow(settlementId);
        // TODO: call the FP integration adapter to push the settlement data.
        // FpAdapter.push(settlement, settlementDetailMapper.selectList(...));
        try {
            // Placeholder: FP push not yet implemented.
            settlement.setPushStatus(PUSH_SUCCESS);
            settlement.setPushTime(LocalDateTime.now());
            settlement.setPushResponse("FP push not implemented yet");
            settlement.setStatus(STATUS_PUSHED);
        } catch (Exception e) {
            settlement.setPushStatus(PUSH_FAILED);
            settlement.setPushTime(LocalDateTime.now());
            settlement.setPushResponse(e.getMessage());
        }
        this.updateById(settlement);
    }

    @Override
    public Page<Settlement> list(int page, int size, Settlement filters) {
        LambdaQueryWrapper<Settlement> wrapper = new LambdaQueryWrapper<>();
        if (filters != null) {
            wrapper.eq(filters.getTaskId() != null, Settlement::getTaskId, filters.getTaskId())
                    .eq(filters.getAgentId() != null, Settlement::getAgentId, filters.getAgentId())
                    .eq(filters.getProjectId() != null, Settlement::getProjectId, filters.getProjectId())
                    .eq(filters.getStatus() != null, Settlement::getStatus, filters.getStatus())
                    .like(filters.getSettlementNo() != null, Settlement::getSettlementNo, filters.getSettlementNo());
        }
        wrapper.orderByDesc(Settlement::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    private String generateSettlementNo() {
        return "ST" + LocalDateTime.now().format(NO_FORMATTER)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    private Settlement loadOrThrow(Long settlementId) {
        Settlement settlement = this.getById(settlementId);
        if (settlement == null) {
            throw new BusinessException("结算单不存在");
        }
        return settlement;
    }
}
