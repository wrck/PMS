package com.dp.plat.implementation.saga;

import com.dp.plat.common.saga.SagaCoordinator;
import com.dp.plat.common.saga.SagaCoordinator.SagaResult;
import com.dp.plat.implementation.entity.Agent;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.SettlementDetailMapper;
import com.dp.plat.implementation.mapper.SettlementMapper;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.SettlementPushRequest;
import com.dp.plat.integration.service.FpIntegrationService;
import com.dp.plat.integration.service.OaIntegrationService;
import com.dp.plat.notification.service.INotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link SettlementSaga} 单元测试。
 *
 * <p>使用<b>真实</b> {@link SagaCoordinator} + Mock 依赖（Mapper / 集成服务 / 通知服务），
 * 端到端验证 Saga 步骤执行与补偿逻辑。</p>
 *
 * <p>覆盖 4 个核心场景：</p>
 * <ol>
 *   <li>全部步骤成功 → 状态 SUBMITTED</li>
 *   <li>FP 推送失败（步骤 3）→ markProcessing 补偿执行，状态回退</li>
 *   <li>OA 推送失败（步骤 4）→ 前 3 步补偿执行</li>
 *   <li>补偿失败不影响其他补偿执行</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SettlementSagaTest {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_SUBMITTED = "SUBMITTED";
    private static final String PUSH_SUCCESS = "SUCCESS";
    private static final String PUSH_FAILED = "FAILED";

    @Mock
    private SettlementMapper settlementMapper;
    @Mock
    private SettlementDetailMapper settlementDetailMapper;
    @Mock
    private AgentMapper agentMapper;
    @Mock
    private FpIntegrationService fpIntegrationService;
    @Mock
    private OaIntegrationService oaIntegrationService;
    @Mock
    private INotificationService notificationService;

    /** 被测对象：使用真实 SagaCoordinator + Mock 依赖。 */
    private SettlementSaga settlementSaga;

    @BeforeEach
    void setUp() {
        SagaCoordinator realCoordinator = new SagaCoordinator();
        settlementSaga = new SettlementSaga(realCoordinator, settlementMapper,
                settlementDetailMapper, agentMapper, fpIntegrationService,
                oaIntegrationService, notificationService);

        // 公共 lenient 桩：明细查询返回空、代理查询返回 null、通知不抛异常
        lenient().when(settlementDetailMapper.selectList(any())).thenReturn(Collections.emptyList());
        lenient().when(agentMapper.selectById(any())).thenReturn(null);
        lenient().doNothing().when(notificationService).multiChannelSend(any(), anySet());
        // updateById 默认返回 1（成功）
        lenient().when(settlementMapper.updateById(any(Settlement.class))).thenReturn(1);
    }

    /** 构造一个可提交的结算单（PENDING 状态）。 */
    private Settlement sampleSettlement() {
        Settlement s = Settlement.builder()
                .taskId(10L)
                .agentId(5L)
                .projectId(1L)
                .settlementNo("ST20260706000001")
                .totalAmount(new BigDecimal("1000.00"))
                .taxAmount(new BigDecimal("130.00"))
                .totalWithTax(new BigDecimal("1130.00"))
                .status(STATUS_PENDING)
                .applyUserId(200L)
                .applyUserName("张三")
                .build();
        // id 在 BaseEntity 中，@Builder 不含继承字段，需单独设置
        s.setId(100L);
        return s;
    }

    /** FP 成功响应。 */
    private FpResponse<String> successFpResponse() {
        FpResponse<String> r = new FpResponse<>();
        r.setCode("200");
        r.setMessage("ok");
        return r;
    }

    /** FP 失败响应。 */
    private FpResponse<String> failedFpResponse() {
        FpResponse<String> r = new FpResponse<>();
        r.setCode("500");
        r.setMessage("FP 内部错误");
        return r;
    }

    // ==================== 测试 1：全部步骤成功 ====================

    @Test
    @DisplayName("测试1：所有步骤成功，结算单状态为 SUBMITTED，pushStatus 为 SUCCESS")
    void submit_allStepsSucceed_settlementSubmitted() {
        Settlement settlement = sampleSettlement();
        when(fpIntegrationService.pushSettlement(any(SettlementPushRequest.class)))
                .thenReturn(successFpResponse());
        when(oaIntegrationService.pushTodo(any())).thenReturn(true);

        SagaResult<SettlementSagaContext> result = settlementSaga.submit(settlement);

        assertTrue(result.isSuccess());
        // 最终状态为 SUBMITTED
        assertEquals(STATUS_SUBMITTED, settlement.getStatus());
        // FP 推送成功
        assertEquals(PUSH_SUCCESS, settlement.getPushStatus());
        // 全部 6 步执行
        assertEquals(6, result.getExecutedSteps().size());
        assertEquals(java.util.List.of("validateStatus", "markProcessing", "pushToFp",
                "pushOaTodo", "notifyProjectManager", "markSubmitted"), result.getExecutedSteps());
        // 无补偿
        assertTrue(result.getCompensatedSteps().isEmpty());
        // OA 待办推送被调用
        verify(oaIntegrationService).pushTodo(any());
        // OA 补偿（completeTodo）未被调用
        verify(oaIntegrationService, never()).completeTodo(anyString());
    }

    // ==================== 测试 2：FP 推送失败（步骤 3）====================

    @Test
    @DisplayName("测试2：FP 推送失败（步骤3），markProcessing 补偿执行，状态回退为 PENDING")
    void submit_fpPushFails_compensatesMarkProcessing() {
        Settlement settlement = sampleSettlement();
        when(fpIntegrationService.pushSettlement(any(SettlementPushRequest.class)))
                .thenReturn(failedFpResponse());

        SagaResult<SettlementSagaContext> result = settlementSaga.submit(settlement);

        assertFalse(result.isSuccess());
        // markProcessing 补偿：状态从 PROCESSING 回退为 PENDING
        assertEquals(STATUS_PENDING, settlement.getStatus());
        // FP 推送失败被记录
        assertEquals(PUSH_FAILED, settlement.getPushStatus());
        // 仅 validateStatus + markProcessing 执行成功（pushToFp 失败未计入）
        assertEquals(java.util.List.of("validateStatus", "markProcessing"),
                result.getExecutedSteps());
        // 仅 markProcessing 被补偿（validateStatus 无补偿）
        assertEquals(java.util.List.of("markProcessing"), result.getCompensatedSteps());
        // OA 待办未推送
        verify(oaIntegrationService, never()).pushTodo(any());
    }

    // ==================== 测试 3：OA 推送失败（步骤 4）====================

    @Test
    @DisplayName("测试3：OA 推送失败（步骤4），前3步补偿执行（pushToFp + markProcessing）")
    void submit_oaPushFails_compensatesPreviousSteps() {
        Settlement settlement = sampleSettlement();
        when(fpIntegrationService.pushSettlement(any(SettlementPushRequest.class)))
                .thenReturn(successFpResponse());
        // OA 推送返回 false → 步骤失败
        when(oaIntegrationService.pushTodo(any())).thenReturn(false);

        SagaResult<SettlementSagaContext> result = settlementSaga.submit(settlement);

        assertFalse(result.isSuccess());
        // 前 3 步执行成功
        assertEquals(java.util.List.of("validateStatus", "markProcessing", "pushToFp"),
                result.getExecutedSteps());
        // 补偿按反向顺序：pushToFp → markProcessing（validateStatus 无补偿）
        assertEquals(java.util.List.of("pushToFp", "markProcessing"),
                result.getCompensatedSteps());
        // markProcessing 补偿：状态回退为 PENDING
        assertEquals(STATUS_PENDING, settlement.getStatus());
        // pushToFp 补偿：pushStatus 从 SUCCESS 标记为 FAILED
        assertEquals(PUSH_FAILED, settlement.getPushStatus());
        // OA completeTodo 未被调用（pushOaTodo 失败未计入成功步骤，其补偿不执行）
        verify(oaIntegrationService, never()).completeTodo(anyString());
    }

    // ==================== 测试 4：补偿失败不影响其他补偿 ====================

    @Test
    @DisplayName("测试4：markSubmitted 失败 + OA 补偿失败，其他补偿（pushToFp/markProcessing）仍执行")
    void submit_compensationFailure_doesNotBlockOtherCompensations() {
        Settlement settlement = sampleSettlement();
        when(fpIntegrationService.pushSettlement(any(SettlementPushRequest.class)))
                .thenReturn(successFpResponse());
        when(oaIntegrationService.pushTodo(any())).thenReturn(true);
        // 用计数器精确控制：第 3 次 updateById 是 markSubmitted，模拟 DB 故障
        // 第 1 次=markProcessing, 第 2 次=pushToFp(成功), 第 3 次=markSubmitted(抛异常)
        // 后续补偿调用正常返回
        AtomicInteger updateCallCount = new AtomicInteger(0);
        when(settlementMapper.updateById(any(Settlement.class))).thenAnswer(invocation -> {
            if (updateCallCount.incrementAndGet() == 3) {
                throw new RuntimeException("DB 锁超时");
            }
            return 1;
        });
        // OA 补偿（completeTodo）抛异常
        when(oaIntegrationService.completeTodo(anyString()))
                .thenThrow(new RuntimeException("OA 删除待办失败"));

        SagaResult<SettlementSagaContext> result = settlementSaga.submit(settlement);

        assertFalse(result.isSuccess());
        // 前 5 步执行成功（markSubmitted 失败）
        assertEquals(java.util.List.of("validateStatus", "markProcessing", "pushToFp",
                "pushOaTodo", "notifyProjectManager"), result.getExecutedSteps());
        // OA 补偿失败（未计入 compensatedSteps），但 pushToFp + markProcessing 补偿仍执行
        assertEquals(java.util.List.of("pushToFp", "markProcessing"),
                result.getCompensatedSteps());
        // 关键断言：尽管 OA 补偿失败，其他补偿仍生效
        assertEquals(STATUS_PENDING, settlement.getStatus());         // markProcessing 补偿生效
        assertEquals(PUSH_FAILED, settlement.getPushStatus());        // pushToFp 补偿生效
        // OA completeTodo 被调用（尽管失败）
        verify(oaIntegrationService).completeTodo(anyString());
    }

    // ==================== 补充测试：状态校验失败 ====================

    @Test
    @DisplayName("补充：非 DRAFT/PENDING 状态提交直接失败，无补偿执行")
    void submit_invalidStatus_failsWithoutCompensation() {
        Settlement settlement = sampleSettlement();
        settlement.setStatus(STATUS_SUBMITTED);  // 已提交，不允许再次提交

        SagaResult<SettlementSagaContext> result = settlementSaga.submit(settlement);

        assertFalse(result.isSuccess());
        // validateStatus 失败，无任何步骤执行成功
        assertTrue(result.getExecutedSteps().isEmpty());
        assertTrue(result.getCompensatedSteps().isEmpty());
        // 后续步骤未调用
        verify(fpIntegrationService, never()).pushSettlement(any());
        verify(oaIntegrationService, never()).pushTodo(any());
    }
}
