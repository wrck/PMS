package com.dp.plat.asset.service;

import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.entity.AssetLifecycleLog;
import com.dp.plat.asset.entity.AssetTransfer;
import com.dp.plat.asset.mapper.AssetLifecycleLogMapper;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.mapper.AssetTransferMapper;
import com.dp.plat.asset.service.impl.AssetTransferServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.workflow.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AssetTransferServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AssetTransferServiceImplTest {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String ASSET_IN_TRANSIT = "IN_TRANSIT";
    private static final String ASSET_RECEIVED = "RECEIVED";

    @Mock
    private AssetTransferMapper assetTransferMapper;

    @Mock
    private AssetMapper assetMapper;

    @Mock
    private AssetLifecycleLogMapper assetLifecycleLogMapper;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AssetStateTransitionValidator stateValidator;

    private AssetTransferServiceImpl assetTransferService;

    @BeforeEach
    void setUp() {
        assetTransferService = Mockito.spy(new AssetTransferServiceImpl(
                assetMapper, assetLifecycleLogMapper, workflowService, stateValidator));
        // ServiceImpl.baseMapper (AssetTransferMapper) is set via field injection in real
        // runtime; constructor injection stops there, so set it manually.
        ReflectionTestUtils.setField(assetTransferService, "baseMapper", assetTransferMapper);
    }

    private Asset sampleAsset(Long id, String status, Long projectId) {
        Asset asset = Asset.builder()
                .serialNo("SN-" + id)
                .modelId(1L)
                .assetName("Router-" + id)
                .status(status)
                .projectId(projectId)
                .build();
        asset.setId(id);
        return asset;
    }

    private AssetTransfer sampleTransfer(Long id, Long assetId, Long fromId, Long toId, String status) {
        AssetTransfer transfer = AssetTransfer.builder()
                .assetId(assetId)
                .fromProjectId(fromId)
                .toProjectId(toId)
                .transferReason("测试调拨")
                .status(status)
                .build();
        transfer.setId(id);
        return transfer;
    }

    @Test
    @DisplayName("apply: 创建调拨申请并置为 PENDING，设备置为 IN_TRANSIT")
    void apply_shouldCreatePendingTransferAndMarkAssetInTransit() {
        Asset asset = sampleAsset(1L, ASSET_RECEIVED, 100L);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);
        // spy: stub save() since ServiceImpl.save() depends on SqlSessionFactory in pure unit tests
        Mockito.doReturn(true).when(assetTransferService).save(any(AssetTransfer.class));

        AssetTransfer transfer = AssetTransfer.builder()
                .assetId(1L)
                .toProjectId(200L)
                .transferReason("项目需要")
                .build();

        boolean saved = assetTransferService.apply(transfer);

        assertTrue(saved);
        assertEquals(STATUS_PENDING, transfer.getStatus());
        assertEquals(100L, transfer.getFromProjectId(), "fromProjectId 缺省时取自设备当前项目");
        assertEquals(200L, transfer.getToProjectId());
        assertNotNull(transfer.getApplyTime());
        assertEquals(ASSET_IN_TRANSIT, asset.getStatus(), "设备状态应置为 IN_TRANSIT");
        verify(stateValidator, times(1)).validate(any(), any());
        verify(assetMapper, times(1)).updateById(any(Asset.class));
    }

    @Test
    @DisplayName("apply: 显式传入 fromProjectId 时不被覆盖")
    void apply_keepsExplicitFromProjectId() {
        Asset asset = sampleAsset(1L, ASSET_RECEIVED, 100L);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);
        Mockito.doReturn(true).when(assetTransferService).save(any(AssetTransfer.class));

        AssetTransfer transfer = AssetTransfer.builder()
                .assetId(1L)
                .fromProjectId(999L)
                .toProjectId(200L)
                .build();
        assetTransferService.apply(transfer);

        assertEquals(999L, transfer.getFromProjectId(), "显式 fromProjectId 应被保留");
    }

    @Test
    @DisplayName("apply: assetId 为 null 抛出业务异常")
    void apply_nullAssetId_throws() {
        AssetTransfer transfer = AssetTransfer.builder().toProjectId(200L).build();
        BusinessException ex = assertThrows(BusinessException.class,
                () -> assetTransferService.apply(transfer));
        assertTrue(ex.getMessage().contains("设备"));
        verify(assetMapper, never()).updateById(any(Asset.class));
    }

    @Test
    @DisplayName("apply: toProjectId 为 null 抛出业务异常")
    void apply_nullToProjectId_throws() {
        AssetTransfer transfer = AssetTransfer.builder().assetId(1L).build();
        assertThrows(BusinessException.class, () -> assetTransferService.apply(transfer));
        verify(assetMapper, never()).updateById(any(Asset.class));
    }

    @Test
    @DisplayName("apply: 设备不存在抛出业务异常")
    void apply_assetNotFound_throws() {
        when(assetMapper.selectById(anyLong())).thenReturn(null);
        AssetTransfer transfer = AssetTransfer.builder()
                .assetId(99L).toProjectId(200L).build();
        assertThrows(BusinessException.class, () -> assetTransferService.apply(transfer));
        verify(assetMapper, never()).updateById(any(Asset.class));
    }

    @Test
    @DisplayName("approve: 审批通过后设备 project_id 更新为目标项目，记录生命周期日志")
    void approve_shouldUpdateAssetProjectAndLog() {
        AssetTransfer transfer = sampleTransfer(5L, 1L, 100L, 200L, STATUS_PENDING);
        when(assetTransferMapper.selectById(5L)).thenReturn(transfer);
        when(assetTransferMapper.updateById(any(AssetTransfer.class))).thenReturn(1);
        Asset asset = sampleAsset(1L, ASSET_IN_TRANSIT, 100L);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        boolean updated = assetTransferService.approve(5L, "同意调拨");

        assertTrue(updated);
        assertEquals(STATUS_APPROVED, transfer.getStatus());
        assertNotNull(transfer.getApproveTime());
        assertEquals("同意调拨", transfer.getApproveOpinion());
        assertEquals(200L, asset.getProjectId(), "设备 project_id 应更新为目标项目");
        assertEquals(ASSET_RECEIVED, asset.getStatus(), "设备状态应恢复为 RECEIVED");
        verify(stateValidator, times(1)).validate(any(), any());
        verify(assetTransferMapper, times(1)).updateById(any(AssetTransfer.class));
        verify(assetMapper, times(1)).updateById(any(Asset.class));
        verify(assetLifecycleLogMapper, times(1)).insert(any(AssetLifecycleLog.class));
    }

    @Test
    @DisplayName("approve: 调拨申请不存在抛出业务异常")
    void approve_notFound_throws() {
        when(assetTransferMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> assetTransferService.approve(99L, "ok"));
        verify(assetTransferMapper, never()).updateById(any(AssetTransfer.class));
    }

    @Test
    @DisplayName("approve: 非待审批状态不允许审批")
    void approve_wrongStatus_throws() {
        AssetTransfer transfer = sampleTransfer(5L, 1L, 100L, 200L, STATUS_APPROVED);
        when(assetTransferMapper.selectById(5L)).thenReturn(transfer);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> assetTransferService.approve(5L, "ok"));
        assertTrue(ex.getMessage().contains("状态"));
        verify(assetTransferMapper, never()).updateById(any(AssetTransfer.class));
    }

    @Test
    @DisplayName("reject: 驳回后设备恢复为原项目 RECEIVED 状态")
    void reject_shouldRestoreAssetToPreviousStatus() {
        AssetTransfer transfer = sampleTransfer(5L, 1L, 100L, 200L, STATUS_PENDING);
        when(assetTransferMapper.selectById(5L)).thenReturn(transfer);
        when(assetTransferMapper.updateById(any(AssetTransfer.class))).thenReturn(1);
        Asset asset = sampleAsset(1L, ASSET_IN_TRANSIT, 200L);
        when(assetMapper.selectById(1L)).thenReturn(asset);
        when(assetMapper.updateById(any(Asset.class))).thenReturn(1);

        boolean updated = assetTransferService.reject(5L, "不同意");

        assertTrue(updated);
        assertEquals(STATUS_REJECTED, transfer.getStatus());
        assertEquals("不同意", transfer.getApproveOpinion());
        assertEquals(ASSET_RECEIVED, asset.getStatus(), "设备状态应恢复为 RECEIVED");
        assertEquals(100L, asset.getProjectId(), "设备应恢复至原项目 fromProjectId");
        verify(stateValidator, times(1)).validate(any(), any());
        verify(assetTransferMapper, times(1)).updateById(any(AssetTransfer.class));
        verify(assetMapper, times(1)).updateById(any(Asset.class));
        verify(assetLifecycleLogMapper, times(1)).insert(any(AssetLifecycleLog.class));
    }

    @Test
    @DisplayName("reject: 调拨申请不存在抛出业务异常")
    void reject_notFound_throws() {
        when(assetTransferMapper.selectById(anyLong())).thenReturn(null);
        assertThrows(BusinessException.class, () -> assetTransferService.reject(99L, "no"));
        verify(assetTransferMapper, never()).updateById(any(AssetTransfer.class));
    }

    @Test
    @DisplayName("reject: 非待审批状态不允许驳回")
    void reject_wrongStatus_throws() {
        AssetTransfer transfer = sampleTransfer(5L, 1L, 100L, 200L, STATUS_REJECTED);
        when(assetTransferMapper.selectById(5L)).thenReturn(transfer);

        assertThrows(BusinessException.class, () -> assetTransferService.reject(5L, "no"));
        verify(assetTransferMapper, never()).updateById(any(AssetTransfer.class));
    }

    @Test
    @DisplayName("approve: transferId 为 null 抛出业务异常")
    void approve_nullId_throws() {
        assertThrows(BusinessException.class, () -> assetTransferService.approve(null, "ok"));
        verify(assetTransferMapper, never()).selectById(anyLong());
    }
}
