package com.dp.plat.implementation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.result.Result;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.entity.SettlementDetail;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.SettlementDetailMapper;
import com.dp.plat.implementation.mapper.SettlementMapper;
import com.dp.plat.implementation.service.ISettlementService;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.SettlementPushDetail;
import com.dp.plat.integration.model.fp.SettlementPushRequest;
import com.dp.plat.integration.service.FpIntegrationService;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import com.dp.plat.workflow.dto.ProcessInstanceDTO;
import com.dp.plat.workflow.dto.StartProcessRequest;
import com.dp.plat.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of {@link ISettlementService}.
 */
@Slf4j
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

    private static final String PROCESS_KEY_SETTLEMENT_APPROVAL = "settlementApproval";

    /** Notification metadata. */
    private static final String CATEGORY_SETTLEMENT = "SETTLEMENT";
    private static final String BIZ_TYPE_SETTLEMENT_APPROVED = "SETTLEMENT_APPROVED";
    private static final String BIZ_TYPE_SETTLEMENT_REJECTED = "SETTLEMENT_REJECTED";
    /** OA 通道走占位实现（见 NotificationServiceImpl）。 */
    private static final Set<String> CHANNELS = Set.of("IN_APP", "WS", "OA");

    private final SettlementDetailMapper settlementDetailMapper;
    private final AgentMapper agentMapper;
    private final WorkflowService workflowService;
    private final FpIntegrationService fpIntegrationService;
    private final INotificationService notificationService;

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
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        settlement.setTotalAmount(totalAmount);
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

        // Start the settlement approval workflow and link the process instance.
        StartProcessRequest req = new StartProcessRequest();
        req.setProcessDefinitionKey(PROCESS_KEY_SETTLEMENT_APPROVAL);
        req.setBusinessKey(settlement.getSettlementNo());
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantId", SecurityUtils.getCurrentUserId());
        variables.put("settlementAmount", settlement.getTotalAmount());
        req.setVariables(variables);
        Result<ProcessInstanceDTO> resp = workflowService.startProcess(req);
        if (resp == null || !resp.isSuccess() || resp.getData() == null) {
            throw new BusinessException("结算审批流程启动失败");
        }
        settlement.setProcessInstanceId(resp.getData().getId());
        this.updateById(settlement);
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

        pushSettlementToFp(settlement);

        // Notify the applicant the settlement has been approved.
        sendSettlementNotification(settlement, "已审批通过", BIZ_TYPE_SETTLEMENT_APPROVED);
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

        // Notify the applicant the settlement has been rejected.
        sendSettlementNotification(settlement, "已被驳回", BIZ_TYPE_SETTLEMENT_REJECTED);
    }

    /**
     * Send a settlement approval notification to the applicant. Failures are
     * swallowed: notifications are best-effort and must not roll back the
     * approval decision.
     */
    private void sendSettlementNotification(Settlement settlement, String outcomeSuffix, String bizType) {
        Long applicantId = settlement.getApplyUserId();
        if (applicantId == null) {
            log.warn("结算单 id={} 无申请人 userId，跳过审批通知发送", settlement.getId());
            return;
        }
        String title = "结算审批通知";
        String content = String.format("结算单 %s %s", settlement.getSettlementNo(), outcomeSuffix);
        Notification notification = Notification.builder()
                .userId(applicantId)
                .title(title)
                .content(content)
                .category(CATEGORY_SETTLEMENT)
                .bizType(bizType)
                .bizId(settlement.getId())
                .build();
        try {
            notificationService.multiChannelSend(notification, CHANNELS);
        } catch (Exception e) {
            log.error("结算审批通知发送失败 settlementId={} applicantId={}",
                    settlement.getId(), applicantId, e);
        }
    }

    /**
     * Build the FP push request from the settlement and its line items and
     * invoke {@link FpIntegrationService#pushSettlement}. Records the push
     * outcome (success/failed) on the settlement. A failed push does not roll
     * back the approval itself so the integration log can be retried later.
     */
    private void pushSettlementToFp(Settlement settlement) {
        List<SettlementDetail> details = settlementDetailMapper.selectList(
                new LambdaQueryWrapper<SettlementDetail>()
                        .eq(SettlementDetail::getSettlementId, settlement.getId()));
        Agent agent = settlement.getAgentId() == null
                ? null : agentMapper.selectById(settlement.getAgentId());
        List<SettlementPushDetail> pushDetails = details == null
                ? Collections.emptyList()
                : details.stream().map(d -> SettlementPushDetail.builder()
                        .itemName(d.getItemName())
                        .workQuantity(d.getWorkQuantity())
                        .unit(d.getUnit())
                        .unitPrice(d.getUnitPrice())
                        .amount(d.getAmount())
                        .build()).toList();
        SettlementPushRequest pushReq = SettlementPushRequest.builder()
                .settlementNo(settlement.getSettlementNo())
                .agentName(agent == null ? null : agent.getAgentName())
                .totalAmount(settlement.getTotalAmount())
                .taxAmount(settlement.getTaxAmount())
                .totalWithTax(settlement.getTotalWithTax())
                .details(pushDetails)
                .build();
        try {
            FpResponse<String> pushResp = fpIntegrationService.pushSettlement(pushReq);
            boolean success = pushResp != null && pushResp.isSuccess();
            settlement.setPushStatus(success ? PUSH_SUCCESS : PUSH_FAILED);
            settlement.setPushResponse(pushResp == null
                    ? "FP push returned null response"
                    : pushResp.getMessage());
        } catch (BusinessException e) {
            settlement.setPushStatus(PUSH_FAILED);
            settlement.setPushResponse(e.getMessage());
        }
        settlement.setPushTime(LocalDateTime.now());
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
