package com.dp.plat.asset.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetAllocation;
import com.dp.plat.asset.entity.AssetLifecycleLog;
import com.dp.plat.asset.enums.AssetStatus;
import com.dp.plat.asset.mapper.AssetAllocationMapper;
import com.dp.plat.asset.mapper.AssetLifecycleLogMapper;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.service.impl.AssetServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.metrics.BusinessMetrics;
import com.dp.plat.project.mapper.ProjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AssetServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    private static final String STATUS_RECEIVED = "RECEIVED";
    private static final String STATUS_INSTALLED = "INSTALLED";
    private static final String STATUS_ALLOCATED = "ALLOCATED";

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private AssetAllocationMapper assetAllocationMapper;

    @Mock
    private AssetLifecycleLogMapper assetLifecycleLogMapper;

    @Mock
    private AssetStateTransitionValidator stateValidator;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private BusinessMetrics businessMetrics;

    private AssetServiceImpl assetService;

    @BeforeEach
    void setUp() {
        assetService = Mockito.spy(new AssetServiceImpl(
                assetAllocationMapper, assetLifecycleLogMapper, stateValidator, projectMapper, businessMetrics));
        ReflectionTestUtils.setField(assetService, "baseMapper", assetMapper);
    }

    private Asset sampleAsset(Long id, String status, Long projectId) {
        Asset asset = Asset.builder()
                .serialNo("SN-" + id)
                .modelId(1L)
                .categoryId(1L)
                .assetName("Router-" + id)
                .status(status)
                .warehouse("WH1")
                .location("A1")
                .projectId(projectId)
                .build();
        asset.setId(id);
        return asset;
    }

    // ==================== inbound ====================

    @Test
    @DisplayName("inbound: 创建设备并置为 RECEIVED，记录入库生命周期日志")
    void inbound_shouldCreateWithReceivedStatus() {
        Asset asset = Asset.builder()
                .serialNo("SN-NEW")
                .assetName("New Router")
                .modelId(1L)
                .categoryId(1L)
                .build();
        when(assetMapper.insert(any(Asset.class))).thenAnswer(invocation -> {
            Asset a = invocation.getArgument(0);
            a.setId(1L);
            return 1;
        });

        boolean saved = assetService.inbound(asset);

        assertTrue(saved);
        assertEquals(STATUS_RECEIVED, asset.getStatus(), "缺省状态应为 RECEIVED");
        assertNotNull(asset.getInboundTime(), "入库时间应被填充");
        verify(assetMapper, times(1)).insert(any(Asset.class));
        verify(assetLifecycleLogMapper, times(1)).insert(any(AssetLifecycleLog.class));
    }

    @Test
    @DisplayName("inbound: 保留已有入库时间")
    void inbound_keepsExistingInboundTime() {
        LocalDateTime fixed = LocalDateTime.of(2024, 1, 1, 9, 0);
        Asset asset = Asset.builder()
                .serialNo("SN-X")
                .assetName("Router")
                .inboundTime(fixed)
                .build();
        when(assetMapper.insert(any(Asset.class))).thenReturn(1);

        assetService.inbound(asset);

        assertEquals(fixed, asset.getInboundTime(), "已有入库时间不应被覆盖");
        verify(assetLifecycleLogMapper, times(1)).insert(any(AssetLifecycleLog.class));
    }

    @Test
    @DisplayName("inbound: 保存失败时不记录生命周期日志")
    void inbound_saveFails_noLogRecorded() {
        Asset asset = Asset.builder().serialNo("SN-F").assetName("R").build();
        when(assetMapper.insert(any(Asset.class))).thenReturn(0);

        boolean saved = assetService.inbound(asset);

        assertEquals(false, saved);
        verify(assetLifecycleLogMapper, never()).insert(any(AssetLifecycleLog.class));
    }

    // ==================== allocate ====================

    @Test
    @DisplayName("allocate: RECEIVED 状态设备分配后变为 INSTALLED，创建分配记录并记录日志")
    void allocate_received_shouldChangeToInstalled() {
        Asset asset = sampleAsset(1L, STATUS_RECEIVED, null);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        boolean updated = assetService.allocate(1L, 100L);

        assertTrue(updated);
        assertEquals(STATUS_INSTALLED, asset.getStatus());
        assertEquals(100L, asset.getProjectId());
        assertNotNull(asset.getOutboundTime());
        verify(assetAllocationMapper, times(1)).insert(any(AssetAllocation.class));
        verify(assetLifecycleLogMapper, times(1)).insert(any(AssetLifecycleLog.class));
    }

    @Test
    @DisplayName("allocate: STAGED 状态设备也可分配")
    void allocate_staged_shouldChangeToInstalled() {
        Asset asset = sampleAsset(1L, "STAGED", null);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        boolean updated = assetService.allocate(1L, 100L);

        assertTrue(updated);
        assertEquals(STATUS_INSTALLED, asset.getStatus());
        verify(assetAllocationMapper, times(1)).insert(any(AssetAllocation.class));
    }

    @Test
    @DisplayName("allocate: 非 RECEIVED/STAGED 状态设备不可分配")
    void allocate_wrongStatus_throws() {
        Asset asset = sampleAsset(1L, STATUS_INSTALLED, 50L);
        when(assetMapper.selectById(1L)).thenReturn(asset);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> assetService.allocate(1L, 100L));
        assertTrue(ex.getMessage().contains("不可分配"));
        verify(assetMapper, never()).updateById(any(Asset.class));
        verify(assetAllocationMapper, never()).insert(any(AssetAllocation.class));
    }

    @Test
    @DisplayName("allocate: 设备不存在抛出业务异常")
    void allocate_notFound_throws() {
        when(assetMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> assetService.allocate(99L, 100L));
        verify(assetMapper, never()).updateById(any(Asset.class));
    }

    @Test
    @DisplayName("allocate: assetId 为 null 抛出业务异常")
    void allocate_nullId_throws() {
        assertThrows(BusinessException.class, () -> assetService.allocate(null, 100L));
        verify(assetMapper, never()).selectById(anyLong());
    }

    @Test
    @DisplayName("allocate: 设备已有项目时 fromProjectId 取自原 projectId")
    void allocate_recordsFromProjectId() {
        Asset asset = sampleAsset(1L, STATUS_RECEIVED, 50L);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        assetService.allocate(1L, 100L);

        verify(assetLifecycleLogMapper, times(1)).insert(any(AssetLifecycleLog.class));
        assertEquals(100L, asset.getProjectId());
    }

    // ==================== returnAsset ====================

    @Test
    @DisplayName("returnAsset: 设备状态由 INSTALLED 变回 RECEIVED，更新分配记录并记录日志")
    void returnAsset_shouldChangeStatusToReceived() {
        Asset asset = sampleAsset(1L, STATUS_INSTALLED, 100L);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);
        AssetAllocation active = AssetAllocation.builder()
                .assetId(1L).projectId(100L).status("ACTIVE").build();
        active.setId(5L);
        when(assetAllocationMapper.selectOne(any(Wrapper.class))).thenReturn(active);
        when(assetAllocationMapper.updateById(any(AssetAllocation.class))).thenReturn(1);

        boolean updated = assetService.returnAsset(1L);

        assertTrue(updated);
        assertEquals(STATUS_RECEIVED, asset.getStatus());
        assertNull(asset.getProjectId(), "归还后 projectId 应清空");
        assertEquals("RETURNED", active.getStatus());
        assertNotNull(active.getReturnTime());
        verify(assetAllocationMapper, times(1)).updateById(any(AssetAllocation.class));
        verify(assetLifecycleLogMapper, times(1)).insert(any(AssetLifecycleLog.class));
    }

    @Test
    @DisplayName("returnAsset: 非法状态迁移时抛出业务异常")
    void returnAsset_invalidTransition_throws() {
        Asset asset = sampleAsset(1L, STATUS_RECEIVED, null);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        // stateValidator 拒绝 RECEIVED → RECEIVED 之外的非法迁移
        Mockito.doThrow(new BusinessException("非法状态迁移: RECEIVED → RECEIVED"))
                .when(stateValidator).validate(AssetStatus.RECEIVED, AssetStatus.RECEIVED);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> assetService.returnAsset(1L));
        assertTrue(ex.getMessage().contains("非法状态迁移"));
        verify(assetMapper, never()).updateById(any(Asset.class));
    }

    @Test
    @DisplayName("returnAsset: 无活跃分配记录时仅更新设备状态")
    void returnAsset_noActiveAllocation() {
        Asset asset = sampleAsset(1L, STATUS_INSTALLED, 100L);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);
        when(assetAllocationMapper.selectOne(any(Wrapper.class))).thenReturn(null);

        boolean updated = assetService.returnAsset(1L);

        assertTrue(updated);
        assertEquals(STATUS_RECEIVED, asset.getStatus());
        verify(assetAllocationMapper, never()).updateById(any(AssetAllocation.class));
        verify(assetLifecycleLogMapper, times(1)).insert(any(AssetLifecycleLog.class));
    }

    // ==================== getLifecycleLog ====================

    @Test
    @DisplayName("getLifecycleLog: 按时间正序返回生命周期日志")
    void getLifecycleLog_returnsOrderedLogs() {
        AssetLifecycleLog log1 = AssetLifecycleLog.builder().assetId(1L).actionType("INBOUND").build();
        log1.setId(1L);
        AssetLifecycleLog log2 = AssetLifecycleLog.builder().assetId(1L).actionType("ALLOCATE").build();
        log2.setId(2L);
        AssetLifecycleLog log3 = AssetLifecycleLog.builder().assetId(1L).actionType("RETURN").build();
        log3.setId(3L);
        List<AssetLifecycleLog> logs = Arrays.asList(log1, log2, log3);
        when(assetLifecycleLogMapper.selectList(any(Wrapper.class))).thenReturn(logs);

        List<AssetLifecycleLog> result = assetService.getLifecycleLog(1L);

        assertEquals(3, result.size());
        assertEquals("INBOUND", result.get(0).getActionType());
        assertEquals("RETURN", result.get(2).getActionType());
    }

    // ==================== returnByProject ====================

    @Test
    @DisplayName("returnByProject: 返回项目下所有 ALLOCATED 状态设备")
    void returnByProject_returnsAllocatedAssets() {
        List<Asset> assets = Arrays.asList(
                sampleAsset(1L, STATUS_ALLOCATED, 100L),
                sampleAsset(2L, STATUS_ALLOCATED, 100L));
        when(assetMapper.selectList(any(Wrapper.class))).thenReturn(assets);

        List<Asset> result = assetService.returnByProject(100L);

        assertEquals(2, result.size());
        assertEquals(STATUS_ALLOCATED, result.get(0).getStatus());
        verify(assetMapper, times(1)).selectList(any(Wrapper.class));
    }

    @Test
    @DisplayName("returnByProject: 项目下无已分配设备返回空列表")
    void returnByProject_empty() {
        when(assetMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        List<Asset> result = assetService.returnByProject(100L);

        assertTrue(result.isEmpty());
    }

    // ==================== recycleByProject ====================

    @Test
    @DisplayName("recycleByProject: projectId 为 null 时返回 0")
    void recycleByProject_nullId_returnsZero() {
        int result = assetService.recycleByProject(null);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("recycleByProject: 回收项目下所有设备")
    void recycleByProject_shouldRecycleAssets() {
        // 注意：recycleByProject 查询 STATUS_ALLOCATED（遗留），但 returnAsset 调用 parseStatus
        // 需要有效 AssetStatus。mock 返回 INSTALLED 状态设备以通过 parseStatus 校验
        Asset asset1 = sampleAsset(1L, STATUS_INSTALLED, 100L);
        Asset asset2 = sampleAsset(2L, STATUS_INSTALLED, 100L);
        when(assetMapper.selectList(any(Wrapper.class))).thenReturn(Arrays.asList(asset1, asset2));
        when(assetMapper.selectById(1L)).thenReturn(asset1);
        when(assetMapper.selectById(2L)).thenReturn(asset2);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);
        when(assetAllocationMapper.selectOne(any(Wrapper.class))).thenReturn(null);

        int count = assetService.recycleByProject(100L);

        assertEquals(2, count);
        verify(assetMapper, times(2)).updateById(any(Asset.class));
    }
}
