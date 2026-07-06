package com.dp.plat.asset.rma.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.enums.AssetStatus;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.rma.dto.RmaKpiDto;
import com.dp.plat.asset.rma.entity.Rma;
import com.dp.plat.asset.rma.mapper.RmaMapper;
import com.dp.plat.asset.rma.service.impl.RmaServiceImpl;
import com.dp.plat.asset.service.AssetStateTransitionValidator;
import com.dp.plat.asset.warranty.service.IWarrantyService;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.notification.service.INotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RmaServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class RmaServiceImplTest {

    private static final String TICKET_REGISTERED = "REGISTERED";
    private static final String TICKET_WARRANTY_CHECKED = "WARRANTY_CHECKED";
    private static final String TICKET_RMA_ISSUED = "RMA_ISSUED";
    private static final String TICKET_RETURNING = "RETURNING";
    private static final String TICKET_INSPECTED = "INSPECTED";
    private static final String TICKET_CLOSED = "CLOSED";

    private static final String WARRANTY_IN = "IN_WARRANTY";
    private static final String WARRANTY_OUT = "OUT_OF_WARRANTY";

    @Mock
    private RmaMapper rmaMapper;

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private AssetStateTransitionValidator stateValidator;

    @Mock
    private ObjectProvider<IWarrantyService> warrantyServiceProvider;

    @Mock
    private IWarrantyService warrantyService;

    @Mock
    private INotificationService notificationService;

    private RmaServiceImpl rmaService;

    @BeforeEach
    void setUp() {
        rmaService = Mockito.spy(new RmaServiceImpl(
                assetMapper, stateValidator, warrantyServiceProvider, notificationService));
        ReflectionTestUtils.setField(rmaService, "baseMapper", rmaMapper);
    }

    private Rma sampleRma(Long id, Long assetId, String ticketStatus) {
        Rma rma = Rma.builder()
                .rmaNo("RMA-2024-0001")
                .assetId(assetId)
                .faultDescription("设备故障")
                .ticketStatus(ticketStatus)
                .registerUserId(100L)
                .registerUserName("tester")
                .build();
        rma.setId(id);
        return rma;
    }

    private Asset sampleAsset(Long id, String status, String sn, Long projectId) {
        Asset asset = Asset.builder()
                .serialNo(sn)
                .status(status)
                .projectId(projectId)
                .build();
        asset.setId(id);
        return asset;
    }

    // ==================== create ====================

    @Test
    @DisplayName("create: assetId 为 null 抛出业务异常")
    void create_nullAssetId_throws() {
        Rma rma = Rma.builder().faultDescription("故障").build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> rmaService.create(rma));
        assertTrue(ex.getMessage().contains("设备"));
    }

    @Test
    @DisplayName("create: 正常创建 RMA，状态置为 REGISTERED，回填 rmaNo/registeredAt/registerUser")
    void create_normal_success() {
        Rma rma = Rma.builder()
                .assetId(10L)
                .faultDescription("故障")
                .sn("SN-1")
                .projectId(100L)
                .build();
        Mockito.doReturn(true).when(rmaService).save(any(Rma.class));

        boolean saved = rmaService.create(rma);

        assertTrue(saved);
        assertEquals(TICKET_REGISTERED, rma.getTicketStatus());
        assertNotNull(rma.getRmaNo(), "rmaNo 应自动生成");
        assertNotNull(rma.getRegisteredAt(), "registeredAt 应被填充");
        verify(assetMapper, never()).selectById(anyLong());
    }

    @Test
    @DisplayName("create: sn/projectId 缺省时从设备快照补全")
    void create_snapshotFromAsset() {
        Asset asset = sampleAsset(10L, "INSTALLED", "SN-ASSET", 100L);
        when(assetMapper.selectById(10L)).thenReturn(asset);
        Mockito.doReturn(true).when(rmaService).save(any(Rma.class));

        Rma rma = Rma.builder()
                .assetId(10L)
                .faultDescription("故障")
                .build();
        rmaService.create(rma);

        assertEquals("SN-ASSET", rma.getSn(), "sn 应从设备快照");
        assertEquals(100L, rma.getProjectId(), "projectId 应从设备快照");
    }

    @Test
    @DisplayName("create: 设备不存在时仅不补全快照但不抛异常")
    void create_assetNotFound_noSnapshot() {
        when(assetMapper.selectById(10L)).thenReturn(null);
        Mockito.doReturn(true).when(rmaService).save(any(Rma.class));

        Rma rma = Rma.builder()
                .assetId(10L)
                .faultDescription("故障")
                .build();
        rmaService.create(rma);

        assertNull(rma.getSn());
        assertNull(rma.getProjectId());
    }

    // ==================== checkWarranty ====================

    @Test
    @DisplayName("checkWarranty: 非 REGISTERED 状态不可核验")
    void checkWarranty_wrongStatus_throws() {
        Rma rma = sampleRma(1L, 10L, TICKET_WARRANTY_CHECKED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> rmaService.checkWarranty(1L));
        assertTrue(ex.getMessage().contains("状态"));
    }

    @Test
    @DisplayName("checkWarranty: 质保服务返回 true 时置 IN_WARRANTY")
    void checkWarranty_inWarranty_success() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        when(warrantyServiceProvider.getIfAvailable()).thenReturn(warrantyService);
        when(warrantyService.isInWarranty(eq(10L), any(LocalDate.class))).thenReturn(true);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));

        boolean updated = rmaService.checkWarranty(1L);

        assertTrue(updated);
        assertEquals(WARRANTY_IN, rma.getWarrantyStatus());
        assertEquals(TICKET_WARRANTY_CHECKED, rma.getTicketStatus());
        assertNotNull(rma.getWarrantyCheckedAt());
        verify(notificationService, times(1)).multiChannelSend(any(), anySet());
    }

    @Test
    @DisplayName("checkWarranty: 质保服务返回 false 时置 OUT_OF_WARRANTY")
    void checkWarranty_outOfWarranty_success() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        when(warrantyServiceProvider.getIfAvailable()).thenReturn(warrantyService);
        when(warrantyService.isInWarranty(eq(10L), any(LocalDate.class))).thenReturn(false);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));

        boolean updated = rmaService.checkWarranty(1L);

        assertTrue(updated);
        assertEquals(WARRANTY_OUT, rma.getWarrantyStatus());
        assertEquals(TICKET_WARRANTY_CHECKED, rma.getTicketStatus());
    }

    @Test
    @DisplayName("checkWarranty: 质保服务不可用时默认 IN_WARRANTY")
    void checkWarranty_noWarrantyService_defaultsInWarranty() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        when(warrantyServiceProvider.getIfAvailable()).thenReturn(null);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));

        boolean updated = rmaService.checkWarranty(1L);

        assertTrue(updated);
        assertEquals(WARRANTY_IN, rma.getWarrantyStatus(), "质保服务不可用应默认 IN_WARRANTY");
    }

    @Test
    @DisplayName("checkWarranty: id 为 null 抛出业务异常")
    void checkWarranty_nullId_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> rmaService.checkWarranty(null));
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    @DisplayName("checkWarranty: RMA 不存在抛出业务异常")
    void checkWarranty_notFound_throws() {
        when(rmaMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> rmaService.checkWarranty(99L));
    }

    // ==================== issueRma ====================

    @Test
    @DisplayName("issueRma: 从 REGISTERED 状态签发成功")
    void issueRma_fromRegistered_success() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));

        boolean updated = rmaService.issueRma(1L);

        assertTrue(updated);
        assertEquals(TICKET_RMA_ISSUED, rma.getTicketStatus());
        assertNotNull(rma.getRmaIssuedAt());
        verify(notificationService, times(1)).multiChannelSend(any(), anySet());
    }

    @Test
    @DisplayName("issueRma: 从 WARRANTY_CHECKED 状态签发成功")
    void issueRma_fromWarrantyChecked_success() {
        Rma rma = sampleRma(1L, 10L, TICKET_WARRANTY_CHECKED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));

        boolean updated = rmaService.issueRma(1L);

        assertTrue(updated);
        assertEquals(TICKET_RMA_ISSUED, rma.getTicketStatus());
    }

    @Test
    @DisplayName("issueRma: 非 REGISTERED/WARRANTY_CHECKED 状态不可签发")
    void issueRma_wrongStatus_throws() {
        Rma rma = sampleRma(1L, 10L, TICKET_RETURNING);
        when(rmaMapper.selectById(1L)).thenReturn(rma);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> rmaService.issueRma(1L));
        assertTrue(ex.getMessage().contains("状态"));
    }

    // ==================== markReturning ====================

    @Test
    @DisplayName("markReturning: 从 RMA_ISSUED 状态标记返修运输中成功")
    void markReturning_success() {
        Rma rma = sampleRma(1L, 10L, TICKET_RMA_ISSUED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));

        boolean updated = rmaService.markReturning(1L);

        assertTrue(updated);
        assertEquals(TICKET_RETURNING, rma.getTicketStatus());
        assertNotNull(rma.getReturningAt());
        verify(notificationService, times(1)).multiChannelSend(any(), anySet());
    }

    @Test
    @DisplayName("markReturning: 非 RMA_ISSUED 状态不可标记")
    void markReturning_wrongStatus_throws() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> rmaService.markReturning(1L));
        assertTrue(ex.getMessage().contains("状态"));
    }

    // ==================== inspect ====================

    @Test
    @DisplayName("inspect: 非 RETURNING 状态不可检验")
    void inspect_wrongStatus_throws() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> rmaService.inspect(1L, "notes"));
        assertTrue(ex.getMessage().contains("状态"));
    }

    @Test
    @DisplayName("inspect: 设备 INSTALLED 时修复后置 COMMISSIONED")
    void inspect_repairedInstalled_toCommissioned() {
        Rma rma = sampleRma(1L, 10L, TICKET_RETURNING);
        rma.setResolution("已修复");
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));
        Asset asset = sampleAsset(10L, "INSTALLED", "SN-1", 100L);
        when(assetMapper.selectById(10L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        boolean updated = rmaService.inspect(1L, "检验通过");

        assertTrue(updated);
        assertEquals(TICKET_INSPECTED, rma.getTicketStatus());
        assertNotNull(rma.getInspectedAt());
        assertEquals("检验通过", rma.getInspectorNotes());
        assertEquals("COMMISSIONED", asset.getStatus());
        verify(stateValidator, times(1)).validate(eq(AssetStatus.INSTALLED), eq(AssetStatus.COMMISSIONED));
        verify(assetMapper, times(1)).updateById(any(Asset.class));
        verify(notificationService, times(1)).multiChannelSend(any(), anySet());
    }

    @Test
    @DisplayName("inspect: 报废时设备置 DECOMMISSIONED")
    void inspect_scrapped_toDecommissioned() {
        Rma rma = sampleRma(1L, 10L, TICKET_RETURNING);
        rma.setResolution("设备报废 SCRAP");
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));
        Asset asset = sampleAsset(10L, "IN_PRODUCTION", "SN-1", 100L);
        when(assetMapper.selectById(10L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        rmaService.inspect(1L, "报废");

        assertEquals("DECOMMISSIONED", asset.getStatus());
        verify(stateValidator, times(1)).validate(eq(AssetStatus.IN_PRODUCTION), eq(AssetStatus.DECOMMISSIONED));
    }

    @Test
    @DisplayName("inspect: 中文'报废'关键字也能触发报废")
    void inspect_chineseScrapKeyword() {
        Rma rma = sampleRma(1L, 10L, TICKET_RETURNING);
        rma.setResolution("设备已报废");
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));
        Asset asset = sampleAsset(10L, "RECEIVED", "SN-1", 100L);
        when(assetMapper.selectById(10L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        rmaService.inspect(1L, "报废处理");

        assertEquals("DECOMMISSIONED", asset.getStatus());
    }

    @Test
    @DisplayName("inspect: 非 INSTALLED 状态修复后置 IN_PRODUCTION")
    void inspect_repairedOther_toInProduction() {
        Rma rma = sampleRma(1L, 10L, TICKET_RETURNING);
        rma.setResolution("已修复");
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));
        Asset asset = sampleAsset(10L, "RECEIVED", "SN-1", 100L);
        when(assetMapper.selectById(10L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        rmaService.inspect(1L, "通过");

        assertEquals("IN_PRODUCTION", asset.getStatus());
        verify(stateValidator, times(1)).validate(eq(AssetStatus.RECEIVED), eq(AssetStatus.IN_PRODUCTION));
    }

    @Test
    @DisplayName("inspect: 非法状态迁移抛出业务异常")
    void inspect_invalidTransition_throws() {
        Rma rma = sampleRma(1L, 10L, TICKET_RETURNING);
        rma.setResolution("已修复");
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));
        Asset asset = sampleAsset(10L, "DECOMMISSIONED", "SN-1", 100L);
        when(assetMapper.selectById(10L)).thenReturn(asset);
        doThrow(new BusinessException("非法状态迁移: DECOMMISSIONED → IN_PRODUCTION"))
                .when(stateValidator).validate(eq(AssetStatus.DECOMMISSIONED), eq(AssetStatus.IN_PRODUCTION));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> rmaService.inspect(1L, "notes"));
        assertTrue(ex.getMessage().contains("非法状态迁移"));
        verify(assetMapper, never()).updateById(any(Asset.class));
    }

    @Test
    @DisplayName("inspect: 设备不存在时仅更新 RMA 不更新设备")
    void inspect_assetNotFound() {
        Rma rma = sampleRma(1L, 10L, TICKET_RETURNING);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));
        when(assetMapper.selectById(10L)).thenReturn(null);

        boolean updated = rmaService.inspect(1L, "notes");

        assertTrue(updated);
        assertEquals(TICKET_INSPECTED, rma.getTicketStatus());
        verify(assetMapper, never()).updateById(any(Asset.class));
    }

    // ==================== close ====================

    @Test
    @DisplayName("close: 从 INSPECTED 状态关闭成功")
    void close_success() {
        Rma rma = sampleRma(1L, 10L, TICKET_INSPECTED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));

        boolean updated = rmaService.close(1L);

        assertTrue(updated);
        assertEquals(TICKET_CLOSED, rma.getTicketStatus());
        assertNotNull(rma.getClosedAt());
        verify(notificationService, times(1)).multiChannelSend(any(), anySet());
    }

    @Test
    @DisplayName("close: 非 INSPECTED 状态不可关闭")
    void close_wrongStatus_throws() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> rmaService.close(1L));
        assertTrue(ex.getMessage().contains("状态"));
    }

    // ==================== listByProject / listByAsset ====================

    @Test
    @DisplayName("listByProject: 返回项目关联的 RMA 列表")
    void listByProject_returnsList() {
        List<Rma> list = Arrays.asList(sampleRma(1L, 10L, TICKET_REGISTERED));
        when(rmaMapper.selectList(any(Wrapper.class))).thenReturn(list);

        List<Rma> result = rmaService.listByProject(100L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("listByAsset: 返回设备关联的 RMA 列表")
    void listByAsset_returnsList() {
        List<Rma> list = Arrays.asList(sampleRma(1L, 10L, TICKET_REGISTERED));
        when(rmaMapper.selectList(any(Wrapper.class))).thenReturn(list);

        List<Rma> result = rmaService.listByAsset(10L);

        assertEquals(1, result.size());
    }

    // ==================== kpi ====================

    @Test
    @DisplayName("kpi: 范围内无数据时返回全 0")
    void kpi_noData_returnsZeros() {
        when(rmaMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(rmaMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        RmaKpiDto result = rmaService.kpi(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

        assertEquals(0L, result.getTotalCount());
        assertEquals(0L, result.getClosedCount());
        assertEquals(0, result.getMttrHours().doubleValue(), 0.001);
        assertEquals(0, result.getFirstPassRate().doubleValue(), 0.001);
    }

    @Test
    @DisplayName("kpi: 范围内有已关闭 RMA 时计算 MTTR 和一次通过率")
    void kpi_withClosedRmas() {
        // total count
        when(rmaMapper.selectCount(any(Wrapper.class))).thenReturn(2L);
        // closed list (kpi 内部 list 调用)
        Rma closed1 = sampleRma(1L, 10L, TICKET_CLOSED);
        closed1.setRegisteredAt(LocalDateTime.of(2024, 6, 1, 0, 0));
        closed1.setClosedAt(LocalDateTime.of(2024, 6, 2, 0, 0)); // 24h
        Rma closed2 = sampleRma(2L, 11L, TICKET_CLOSED);
        closed2.setRegisteredAt(LocalDateTime.of(2024, 6, 3, 0, 0));
        closed2.setClosedAt(LocalDateTime.of(2024, 6, 4, 0, 0)); // 24h
        when(rmaMapper.selectList(any(Wrapper.class))).thenReturn(Arrays.asList(closed1, closed2));

        RmaKpiDto result = rmaService.kpi(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

        assertEquals(2L, result.getTotalCount());
        assertEquals(2L, result.getClosedCount());
        assertEquals(24.0, result.getMttrHours().doubleValue(), 0.001, "MTTR 应为 24h");
        assertTrue(result.getFirstPassRate().doubleValue() > 0, "一次通过率应大于 0");
    }

    // ==================== generateRmaNo ====================

    @Test
    @DisplayName("generateRmaNo: 当年无已有 RMA 时生成 0001 序号")
    void generateRmaNo_emptySequence() {
        when(rmaMapper.selectCount(any(Wrapper.class))).thenReturn(0L);

        String rmaNo = rmaService.generateRmaNo();

        assertTrue(rmaNo.startsWith("RMA-" + LocalDate.now().getYear() + "-"));
        assertTrue(rmaNo.endsWith("0001"));
    }

    @Test
    @DisplayName("generateRmaNo: 当年已有 N 条时生成 N+1 序号")
    void generateRmaNo_existingSequence() {
        when(rmaMapper.selectCount(any(Wrapper.class))).thenReturn(5L);

        String rmaNo = rmaService.generateRmaNo();

        assertTrue(rmaNo.endsWith("0006"));
    }

    // ==================== notification best-effort ====================

    @Test
    @DisplayName("checkWarranty: 通知发送失败不影响状态变更（best-effort）")
    void checkWarranty_notificationFailure_doesNotRollback() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        when(warrantyServiceProvider.getIfAvailable()).thenReturn(null);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));
        doThrow(new RuntimeException("通知服务不可用"))
                .when(notificationService).multiChannelSend(any(), anySet());

        boolean updated = rmaService.checkWarranty(1L);

        assertTrue(updated, "通知失败不应影响状态变更");
        assertEquals(TICKET_WARRANTY_CHECKED, rma.getTicketStatus());
    }

    @Test
    @DisplayName("checkWarranty: registerUserId 为 null 时跳过通知")
    void checkWarranty_nullRegisterUser_skipsNotification() {
        Rma rma = sampleRma(1L, 10L, TICKET_REGISTERED);
        rma.setRegisterUserId(null);
        when(rmaMapper.selectById(1L)).thenReturn(rma);
        when(warrantyServiceProvider.getIfAvailable()).thenReturn(null);
        Mockito.doReturn(true).when(rmaService).updateById(any(Rma.class));

        boolean updated = rmaService.checkWarranty(1L);

        assertTrue(updated);
        verify(notificationService, never()).multiChannelSend(any(), anySet());
    }
}
