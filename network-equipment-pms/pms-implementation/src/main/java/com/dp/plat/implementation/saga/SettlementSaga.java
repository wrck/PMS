package com.dp.plat.implementation.saga;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.saga.SagaCoordinator;
import com.dp.plat.common.saga.SagaCoordinator.SagaResult;
import com.dp.plat.common.saga.SagaCoordinator.SagaStep;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.entity.SettlementDetail;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.SettlementDetailMapper;
import com.dp.plat.implementation.mapper.SettlementMapper;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.SettlementPushDetail;
import com.dp.plat.integration.model.fp.SettlementPushRequest;
import com.dp.plat.integration.model.oa.OaTodoRequest;
import com.dp.plat.integration.service.FpIntegrationService;
import com.dp.plat.integration.service.OaIntegrationService;
import com.dp.plat.notification.entity.Notification;
import com.dp.plat.notification.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 结算单提交 Saga 协调器：编排结算单提交流程的多个步骤与补偿动作。
 *
 * <p><b>流程步骤</b>（任一步骤失败时按反向顺序补偿已成功步骤）：</p>
 * <ol>
 *   <li><b>validateStatus</b> — 校验结算单状态为 DRAFT/PENDING（无补偿）</li>
 *   <li><b>markProcessing</b> — 更新状态为 PROCESSING（补偿：回退为原始状态）</li>
 *   <li><b>pushToFp</b> — 推送至 FP 财务平台（补偿：标记推送失败）</li>
 *   <li><b>pushOaTodo</b> — 推送 OA 待办（补偿：删除 OA 待办）</li>
 *   <li><b>notifyProjectManager</b> — 通知项目经理（无补偿，best-effort）</li>
 *   <li><b>markSubmitted</b> — 更新状态为 SUBMITTED（补偿：回退为 PROCESSING）</li>
 * </ol>
 *
 * <p><b>事务边界</b>：每个步骤的 DB 写操作独立提交（MyBatis-Plus mapper 无外层事务时
 * 自动提交单条语句），确保补偿动作能看到已持久化的中间状态。submit 方法本身不加
 * {@code @Transactional}，避免跨步骤大事务导致补偿无法生效。</p>
 *
 * <p><b>幂等性</b>：所有补偿动作设计为幂等 —— 状态回退重复设置相同值无副作用；
 * OA completeTodo 对已完成待办为 no-op。补偿可能被多次调用（人工重试场景）。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementSaga {

    /** 结算单状态：草稿。 */
    public static final String STATUS_DRAFT = "DRAFT";
    /** 结算单状态：待提交（createSettlement 默认状态）。 */
    public static final String STATUS_PENDING = "PENDING";
    /** 结算单状态：提交处理中（Saga 中间状态）。 */
    public static final String STATUS_PROCESSING = "PROCESSING";
    /** 结算单状态：已提交。 */
    public static final String STATUS_SUBMITTED = "SUBMITTED";

    /** FP 推送状态：成功。 */
    public static final String PUSH_SUCCESS = "SUCCESS";
    /** FP 推送状态：失败。 */
    public static final String PUSH_FAILED = "FAILED";

    /** Saga 名称，用于日志标识。 */
    public static final String SAGA_NAME = "SettlementSubmit";

    private static final String CATEGORY_SETTLEMENT = "SETTLEMENT";
    private static final String BIZ_TYPE_SETTLEMENT_SUBMITTED = "SETTLEMENT_SUBMITTED";
    /** 通知投递通道：站内信 + WebSocket（OA 待办已在独立步骤推送，此处不重复）。 */
    private static final Set<String> NOTIFY_CHANNELS = Set.of("IN_APP", "WS");

    private final SagaCoordinator sagaCoordinator;
    private final SettlementMapper settlementMapper;
    private final SettlementDetailMapper settlementDetailMapper;
    private final AgentMapper agentMapper;
    private final FpIntegrationService fpIntegrationService;
    private final OaIntegrationService oaIntegrationService;
    private final INotificationService notificationService;

    /**
     * 执行结算单提交 Saga 流程。
     *
     * @param settlement 待提交的结算单（需已持久化，含 id）
     * @return Saga 执行结果，包含成功标志与已执行/已补偿步骤列表
     */
    public SagaResult<SettlementSagaContext> submit(Settlement settlement) {
        SettlementSagaContext context = new SettlementSagaContext(settlement);

        List<SagaStep<SettlementSagaContext>> steps = List.of(
                // 1. 校验状态（无补偿）
                SagaStep.of("validateStatus", this::validateStatus),
                // 2. 标记 PROCESSING（补偿：回退为原始状态）
                SagaStep.of("markProcessing", this::markProcessing, this::compensateMarkProcessing),
                // 3. 推送 FP（补偿：标记推送失败）
                SagaStep.of("pushToFp", this::pushToFp, this::compensatePushToFp),
                // 4. 推送 OA 待办（补偿：删除 OA 待办）
                SagaStep.of("pushOaTodo", this::pushOaTodo, this::compensatePushOaTodo),
                // 5. 通知项目经理（无补偿，best-effort）
                SagaStep.of("notifyProjectManager", this::notifyProjectManager),
                // 6. 标记 SUBMITTED（补偿：回退为 PROCESSING）
                SagaStep.of("markSubmitted", this::markSubmitted, this::compensateMarkSubmitted)
        );

        return sagaCoordinator.execute(SAGA_NAME, steps, context);
    }

    // ==================== 步骤 1：校验状态 ====================

    /**
     * 校验结算单状态允许提交（仅 DRAFT / PENDING 可提交）。
     */
    private Boolean validateStatus(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        if (s == null || s.getId() == null) {
            throw new BusinessException("结算单不存在，无法提交");
        }
        String status = s.getStatus();
        if (!STATUS_DRAFT.equals(status) && !STATUS_PENDING.equals(status)) {
            throw new BusinessException("当前结算单状态不允许提交，当前状态: " + status);
        }
        return true;
    }

    // ==================== 步骤 2：标记 PROCESSING ====================

    /**
     * 将结算单状态更新为 PROCESSING，标识进入提交处理中。
     */
    private Boolean markProcessing(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        s.setStatus(STATUS_PROCESSING);
        settlementMapper.updateById(s);
        log.info("结算单 id={} 状态更新为 PROCESSING", s.getId());
        return true;
    }

    /**
     * 补偿：将状态回退为 Saga 开始前的原始状态（幂等）。
     */
    private void compensateMarkProcessing(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        s.setStatus(ctx.getOriginalStatus());
        settlementMapper.updateById(s);
        log.info("结算单 id={} 状态补偿回退为 {}", s.getId(), ctx.getOriginalStatus());
    }

    // ==================== 步骤 3：推送 FP ====================

    /**
     * 构建推送请求并调用 FP 财务平台。推送成功记录 PUSH_SUCCESS，失败记录
     * PUSH_FAILED 并抛出异常触发补偿。
     *
     * <p>注意：{@link FpIntegrationService#pushSettlement} 内置 Resilience4j
     * 熔断/隔离/重试保护，并在同步失败后调度后台指数退避重试。Saga 在同步
     * 失败时即补偿本地状态；若后台重试后续成功，需由对账任务修正 pushStatus。</p>
     */
    private Boolean pushToFp(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        SettlementPushRequest pushReq = buildFpPushRequest(s);
        try {
            FpResponse<String> resp = fpIntegrationService.pushSettlement(pushReq);
            if (resp == null || !resp.isSuccess()) {
                String err = resp == null ? "FP 推送返回空响应" : resp.getMessage();
                ctx.setFpPushError(err);
                recordFpPushFailure(s, err);
                throw new BusinessException("FP 财务平台推送失败: " + err);
            }
            s.setPushStatus(PUSH_SUCCESS);
            s.setPushResponse(resp.getMessage());
            s.setPushTime(LocalDateTime.now());
            settlementMapper.updateById(s);
            log.info("结算单 id={} FP 推送成功", s.getId());
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            ctx.setFpPushError(e.getMessage());
            recordFpPushFailure(s, e.getMessage());
            throw new BusinessException("FP 财务平台推送异常: " + e.getMessage());
        }
    }

    /**
     * 补偿：标记 FP 推送为失败（幂等，重复设置 FAILED 无副作用）。
     * 仅当后续步骤失败时触发，记录补偿原因。
     */
    private void compensatePushToFp(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        String reason = ctx.getFpPushError() != null
                ? ctx.getFpPushError() : "Saga 补偿回滚";
        s.setPushStatus(PUSH_FAILED);
        s.setPushResponse("Saga 补偿: " + reason);
        s.setPushTime(LocalDateTime.now());
        settlementMapper.updateById(s);
        log.info("结算单 id={} FP 推送补偿标记为 FAILED", s.getId());
    }

    /**
     * 记录 FP 推送失败信息到结算单。
     */
    private void recordFpPushFailure(Settlement s, String error) {
        s.setPushStatus(PUSH_FAILED);
        s.setPushResponse(error);
        s.setPushTime(LocalDateTime.now());
        settlementMapper.updateById(s);
    }

    /**
     * 从结算单及明细构建 FP 推送请求。
     */
    private SettlementPushRequest buildFpPushRequest(Settlement s) {
        List<SettlementDetail> details = settlementDetailMapper.selectList(
                new LambdaQueryWrapper<SettlementDetail>()
                        .eq(SettlementDetail::getSettlementId, s.getId()));
        Agent agent = s.getAgentId() == null
                ? null : agentMapper.selectById(s.getAgentId());
        List<SettlementPushDetail> pushDetails = details == null
                ? Collections.emptyList()
                : details.stream().map(d -> SettlementPushDetail.builder()
                        .itemName(d.getItemName())
                        .workQuantity(d.getWorkQuantity())
                        .unit(d.getUnit())
                        .unitPrice(d.getUnitPrice())
                        .amount(d.getAmount())
                        .build()).toList();
        return SettlementPushRequest.builder()
                .settlementNo(s.getSettlementNo())
                .agentName(agent == null ? null : agent.getAgentName())
                .totalAmount(s.getTotalAmount())
                .taxAmount(s.getTaxAmount())
                .totalWithTax(s.getTotalWithTax())
                .details(pushDetails)
                .build();
    }

    // ==================== 步骤 4：推送 OA 待办 ====================

    /**
     * 向 OA 推送结算审批待办。失败时抛出异常触发补偿。
     * businessKey 使用结算单号，补偿时据此删除待办。
     */
    private Boolean pushOaTodo(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        String businessKey = s.getSettlementNo();
        ctx.setOaBusinessKey(businessKey);

        OaTodoRequest req = OaTodoRequest.builder()
                .title("结算单提交审批: " + s.getSettlementNo())
                .content(String.format("结算单 %s 已提交至财务平台，含税总额 %s，请审批",
                        s.getSettlementNo(), s.getTotalWithTax()))
                .businessKey(businessKey)
                .businessType(BIZ_TYPE_SETTLEMENT_SUBMITTED)
                .processInstanceId(s.getProcessInstanceId())
                .build();
        boolean ok = oaIntegrationService.pushTodo(req);
        if (!ok) {
            throw new BusinessException("OA 待办推送失败，结算单号: " + s.getSettlementNo());
        }
        log.info("结算单 id={} OA 待办推送成功 businessKey={}", s.getId(), businessKey);
        return true;
    }

    /**
     * 补偿：删除 OA 待办（幂等，对已完成待办为 no-op）。
     */
    private void compensatePushOaTodo(SettlementSagaContext ctx) {
        String businessKey = ctx.getOaBusinessKey();
        if (businessKey == null) {
            log.info("OA 待办未推送，无需补偿");
            return;
        }
        // completeTodo 对已完成的待办为 no-op，保证幂等
        oaIntegrationService.completeTodo(businessKey);
        log.info("结算单 OA 待办补偿删除 businessKey={}", businessKey);
    }

    // ==================== 步骤 5：通知项目经理 ====================

    /**
     * 通知结算申请人提交结果（best-effort，失败不阻断流程）。
     */
    private Boolean notifyProjectManager(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        Long applicantId = s.getApplyUserId();
        if (applicantId == null) {
            log.warn("结算单 id={} 无申请人 userId，跳过提交通知", s.getId());
            return true;
        }
        try {
            Notification notification = Notification.builder()
                    .userId(applicantId)
                    .title("结算单提交通知")
                    .content(String.format("结算单 %s 已提交至财务平台，含税总额 %s",
                            s.getSettlementNo(), s.getTotalWithTax()))
                    .category(CATEGORY_SETTLEMENT)
                    .bizType(BIZ_TYPE_SETTLEMENT_SUBMITTED)
                    .bizId(s.getId())
                    .build();
            notificationService.multiChannelSend(notification, NOTIFY_CHANNELS);
            log.info("结算单 id={} 提交通知已发送 userId={}", s.getId(), applicantId);
        } catch (Exception e) {
            // 通知为 best-effort，失败仅记录日志，不阻断 Saga 流程
            log.error("结算单提交通知发送失败 settlementId={} applicantId={}",
                    s.getId(), applicantId, e);
        }
        return true;
    }

    // ==================== 步骤 6：标记 SUBMITTED ====================

    /**
     * 将结算单状态更新为 SUBMITTED，标识提交完成。
     */
    private Boolean markSubmitted(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        s.setStatus(STATUS_SUBMITTED);
        settlementMapper.updateById(s);
        log.info("结算单 id={} 状态更新为 SUBMITTED", s.getId());
        return true;
    }

    /**
     * 补偿：将状态回退为 PROCESSING（幂等）。
     */
    private void compensateMarkSubmitted(SettlementSagaContext ctx) {
        Settlement s = ctx.getSettlement();
        s.setStatus(STATUS_PROCESSING);
        settlementMapper.updateById(s);
        log.info("结算单 id={} 状态补偿回退为 PROCESSING", s.getId());
    }
}
