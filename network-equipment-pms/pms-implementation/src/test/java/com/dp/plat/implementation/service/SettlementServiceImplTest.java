package com.dp.plat.implementation.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.metrics.BusinessMetrics;
import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.Settlement;
import com.dp.plat.implementation.entity.SettlementDetail;
import com.dp.plat.implementation.mapper.AgentMapper;
import com.dp.plat.implementation.mapper.SettlementDetailMapper;
import com.dp.plat.implementation.mapper.SettlementMapper;
import com.dp.plat.implementation.saga.SettlementSaga;
import com.dp.plat.implementation.service.impl.SettlementServiceImpl;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.SettlementPushRequest;
import com.dp.plat.integration.service.FpIntegrationService;
import com.dp.plat.workflow.dto.ProcessInstanceDTO;
import com.dp.plat.workflow.dto.StartProcessRequest;
import com.dp.plat.workflow.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SettlementServiceImpl}.
 *
 * <p>Mocks {@link SettlementMapper} (the {@code baseMapper} of {@link com.baomidou.mybatisplus.extension.service.impl.ServiceImpl})
 * and {@link SettlementDetailMapper} so the service can be exercised without a database.</p>
 */
@ExtendWith(MockitoExtension.class)
class SettlementServiceImplTest {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";

    @Mock
    private SettlementMapper settlementMapper;

    @Mock
    private SettlementDetailMapper settlementDetailMapper;

    @Mock
    private AgentMapper agentMapper;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private FpIntegrationService fpIntegrationService;

    @Mock
    private BusinessMetrics businessMetrics;

    @Mock
    private SettlementSaga settlementSaga;

    @InjectMocks
    private SettlementServiceImpl settlementService;

    @BeforeEach
    void setUp() {
        // ServiceImpl.baseMapper (SettlementMapper) is wired via field injection at runtime;
        // @InjectMocks stops at constructor injection so set it manually.
        ReflectionTestUtils.setField(settlementService, "baseMapper", settlementMapper);

        // createSettlement starts the approval workflow; approve() pushes to FP.
        // Stubbed leniently so tests that do not reach these paths are unaffected.
        lenient().when(workflowService.startProcess(any(StartProcessRequest.class)))
                .thenReturn(Result.ok(new ProcessInstanceDTO()));
        lenient().when(settlementDetailMapper.selectList(any()))
                .thenReturn(Collections.emptyList());
        lenient().when(fpIntegrationService.pushSettlement(any(SettlementPushRequest.class)))
                .thenReturn(successFpResponse());
    }

    private FpResponse<String> successFpResponse() {
        FpResponse<String> response = new FpResponse<>();
        response.setCode("200");
        response.setMessage("ok");
        return response;
    }

    private Settlement sampleSettlement() {
        return Settlement.builder()
                .taskId(10L)
                .agentId(5L)
                .projectId(1L)
                .build();
    }

    private SettlementDetail detail(BigDecimal workQuantity, BigDecimal unitPrice, BigDecimal amount) {
        return SettlementDetail.builder()
                .itemName("实施工作")
                .workQuantity(workQuantity)
                .unitPrice(unitPrice)
                .amount(amount)
                .build();
    }

    @Test
    @DisplayName("createSettlement: 根据明细计算总金额并生成结算单号与 PENDING 状态")
    void createSettlement_shouldCalculateAmountsAndGenerateNo() {
        Settlement settlement = sampleSettlement();
        List<SettlementDetail> details = Arrays.asList(
                detail(new BigDecimal("10"), new BigDecimal("100"), null),
                detail(null, null, new BigDecimal("500"))
        );
        when(settlementMapper.insert(any(Settlement.class))).thenAnswer(invocation -> {
            Settlement s = invocation.getArgument(0);
            s.setId(1L);
            return 1;
        });
        when(settlementDetailMapper.insert(any(SettlementDetail.class))).thenReturn(1);

        Settlement result = settlementService.createSettlement(settlement, details);

        assertNotNull(result);
        // 第一条明细金额 = 10 * 100 = 1000；第二条金额 = 500；合计 = 1500
        assertEquals(new BigDecimal("1000.00"), details.get(0).getAmount());
        assertEquals(new BigDecimal("1500.00"), result.getTotalAmount());
        // 税额 = 1500 * 13 / 100 = 195.00
        assertEquals(new BigDecimal("195.00"), result.getTaxAmount());
        // 含税总额 = 1500 + 195 = 1695.00
        assertEquals(new BigDecimal("1695.00"), result.getTotalWithTax());
        assertEquals(new BigDecimal("13.00"), result.getTaxRate());
        assertEquals(STATUS_PENDING, result.getStatus());
        assertNotNull(result.getSettlementNo());
        assertTrue(result.getSettlementNo().startsWith("ST"));
        assertNotNull(result.getApplyTime());
        // 明细应关联结算单 ID
        assertEquals(1L, details.get(0).getSettlementId());
        assertEquals(1L, details.get(1).getSettlementId());
        verify(settlementMapper, times(1)).insert(any(Settlement.class));
        verify(settlementDetailMapper, times(2)).insert(any(SettlementDetail.class));
    }

    @Test
    @DisplayName("createSettlement: 无明细时总金额为 0，税额为 0")
    void createSettlement_noDetails_totalIsZero() {
        Settlement settlement = sampleSettlement();
        when(settlementMapper.insert(any(Settlement.class))).thenAnswer(invocation -> {
            Settlement s = invocation.getArgument(0);
            s.setId(2L);
            return 1;
        });

        Settlement result = settlementService.createSettlement(settlement, Collections.emptyList());

        assertEquals(new BigDecimal("0.00"), result.getTotalAmount());
        assertEquals(new BigDecimal("0.00"), result.getTaxAmount());
        assertEquals(new BigDecimal("0.00"), result.getTotalWithTax());
        assertEquals(STATUS_PENDING, result.getStatus());
        verify(settlementDetailMapper, never()).insert(any(SettlementDetail.class));
    }

    @Test
    @DisplayName("createSettlement: 自定义税率时税额按自定义税率计算")
    void createSettlement_customTaxRate() {
        Settlement settlement = sampleSettlement();
        settlement.setTaxRate(new BigDecimal("6.00"));
        settlement.setTotalAmount(new BigDecimal("1000.00"));
        when(settlementMapper.insert(any(Settlement.class))).thenReturn(1);

        Settlement result = settlementService.createSettlement(settlement, null);

        // 税额 = 1000 * 6 / 100 = 60.00
        assertEquals(new BigDecimal("60.00"), result.getTaxAmount());
        // 含税总额 = 1000 + 60 = 1060.00
        assertEquals(new BigDecimal("1060.00"), result.getTotalWithTax());
        assertEquals(new BigDecimal("6.00"), result.getTaxRate());
    }

    @Test
    @DisplayName("createSettlement: 明细 amount 缺失且无工作量/单价时不计入总额")
    void createSettlement_detailWithoutAmountNotCounted() {
        Settlement settlement = sampleSettlement();
        List<SettlementDetail> details = Arrays.asList(
                detail(new BigDecimal("5"), new BigDecimal("200"), null),
                detail(null, null, null)
        );
        when(settlementMapper.insert(any(Settlement.class))).thenReturn(1);
        when(settlementDetailMapper.insert(any(SettlementDetail.class))).thenReturn(1);

        Settlement result = settlementService.createSettlement(settlement, details);

        // 仅第一条明细金额 = 5 * 200 = 1000.00
        assertEquals(new BigDecimal("1000.00"), result.getTotalAmount());
        assertEquals(new BigDecimal("130.00"), result.getTaxAmount());
        assertEquals(new BigDecimal("1130.00"), result.getTotalWithTax());
    }

    @Test
    @DisplayName("approve: PENDING 状态结算单审批后变为 APPROVED")
    void approve_shouldChangeStatusToApproved() {
        Settlement settlement = sampleSettlement();
        settlement.setId(1L);
        settlement.setStatus(STATUS_PENDING);
        when(settlementMapper.selectById(1L)).thenReturn(settlement);
        when(settlementMapper.updateById(any(Settlement.class))).thenReturn(1);

        settlementService.approve(1L, "同意");

        assertEquals(STATUS_APPROVED, settlement.getStatus());
        assertEquals("同意", settlement.getApproveOpinion());
        assertNotNull(settlement.getApproveTime());
        // updateById is called twice: once for the approval, once after the FP push.
        verify(settlementMapper, times(2)).updateById(any(Settlement.class));
    }

    @Test
    @DisplayName("approve: 非 PENDING 状态不允许审批")
    void approve_wrongStatus_throws() {
        Settlement settlement = sampleSettlement();
        settlement.setId(2L);
        settlement.setStatus(STATUS_APPROVED);
        when(settlementMapper.selectById(2L)).thenReturn(settlement);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> settlementService.approve(2L, "ok"));
        assertTrue(ex.getMessage().contains("状态"));
        verify(settlementMapper, never()).updateById(any(Settlement.class));
    }

    @Test
    @DisplayName("approve: 结算单不存在抛出业务异常")
    void approve_notFound_throws() {
        when(settlementMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> settlementService.approve(99L, "ok"));
        verify(settlementMapper, never()).updateById(any(Settlement.class));
    }

    @Test
    @DisplayName("reject: PENDING 状态结算单驳回后变为 REJECTED")
    void reject_shouldChangeStatusToRejected() {
        Settlement settlement = sampleSettlement();
        settlement.setId(3L);
        settlement.setStatus(STATUS_PENDING);
        when(settlementMapper.selectById(3L)).thenReturn(settlement);
        when(settlementMapper.updateById(any(Settlement.class))).thenReturn(1);

        settlementService.reject(3L, "金额有误");

        assertEquals(STATUS_REJECTED, settlement.getStatus());
        assertEquals("金额有误", settlement.getApproveOpinion());
        assertNotNull(settlement.getApproveTime());
        verify(settlementMapper, times(1)).updateById(any(Settlement.class));
    }

    @Test
    @DisplayName("reject: 非 PENDING 状态不允许驳回")
    void reject_wrongStatus_throws() {
        Settlement settlement = sampleSettlement();
        settlement.setId(4L);
        settlement.setStatus(STATUS_REJECTED);
        when(settlementMapper.selectById(4L)).thenReturn(settlement);

        assertThrows(BusinessException.class, () -> settlementService.reject(4L, "no"));
        verify(settlementMapper, never()).updateById(any(Settlement.class));
    }

    @Test
    @DisplayName("list: 按条件分页查询结算单")
    void list_shouldReturnPage() {
        List<Settlement> records = new ArrayList<>();
        records.add(sampleSettlement());
        when(settlementMapper.selectPage(any(IPage.class), any(Wrapper.class)))
                .thenAnswer(invocation -> {
                    Page<Settlement> page = invocation.getArgument(0);
                    page.setRecords(records);
                    page.setTotal(records.size());
                    return page;
                });

        Settlement filters = new Settlement();
        filters.setStatus(STATUS_PENDING);

        Page<Settlement> result = settlementService.list(1, 10, filters);

        assertEquals(1, result.getRecords().size());
        assertEquals(1L, result.getTotal());
        verify(settlementMapper, times(1)).selectPage(any(IPage.class), any(Wrapper.class));
    }

    @Test
    @DisplayName("list: 过滤条件为 null 时查询全部")
    void list_nullFilters() {
        when(settlementMapper.selectPage(any(IPage.class), any(Wrapper.class)))
                .thenAnswer(invocation -> {
                    Page<Settlement> page = invocation.getArgument(0);
                    page.setRecords(new ArrayList<>());
                    page.setTotal(0);
                    return page;
                });

        Page<Settlement> result = settlementService.list(1, 10, null);

        assertEquals(0, result.getRecords().size());
        assertEquals(0L, result.getTotal());
    }
}
