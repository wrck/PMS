package com.dp.plat.asset.warranty.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.asset.entity.Asset;
import com.dp.plat.asset.mapper.AssetMapper;
import com.dp.plat.asset.warranty.entity.Warranty;
import com.dp.plat.asset.warranty.mapper.WarrantyMapper;
import com.dp.plat.asset.warranty.service.impl.WarrantyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WarrantyServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class WarrantyServiceImplTest {

    @Mock
    private WarrantyMapper warrantyMapper;

    @Mock
    private AssetMapper assetMapper;

    private WarrantyServiceImpl warrantyService;

    @BeforeEach
    void setUp() {
        warrantyService = Mockito.spy(new WarrantyServiceImpl(assetMapper));
        ReflectionTestUtils.setField(warrantyService, "baseMapper", warrantyMapper);
    }

    private Warranty sampleWarranty(Long id, Long assetId, Long projectId, LocalDate start, LocalDate end) {
        Warranty w = Warranty.builder()
                .assetId(assetId)
                .projectId(projectId)
                .startDate(start)
                .endDate(end)
                .durationMonths(12)
                .build();
        w.setId(id);
        return w;
    }

    @Test
    @DisplayName("listByAsset: 返回设备关联的质保记录")
    void listByAsset_returnsList() {
        List<Warranty> list = Arrays.asList(
                sampleWarranty(1L, 10L, 100L, LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1)),
                sampleWarranty(2L, 10L, 100L, LocalDate.of(2025, 1, 1), LocalDate.of(2026, 1, 1)));
        when(warrantyMapper.selectList(any(Wrapper.class))).thenReturn(list);

        List<Warranty> result = warrantyService.listByAsset(10L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("listByProject: 返回项目关联的质保记录")
    void listByProject_returnsList() {
        List<Warranty> list = Collections.singletonList(
                sampleWarranty(1L, 10L, 100L, LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1)));
        when(warrantyMapper.selectList(any(Wrapper.class))).thenReturn(list);

        List<Warranty> result = warrantyService.listByProject(100L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("listExpiringSoon: 返回即将到期的质保记录")
    void listExpiringSoon_returnsList() {
        LocalDate today = LocalDate.now();
        List<Warranty> list = Collections.singletonList(
                sampleWarranty(1L, 10L, 100L, today.minusMonths(11), today.plusDays(10)));
        when(warrantyMapper.selectList(any(Wrapper.class))).thenReturn(list);

        List<Warranty> result = warrantyService.listExpiringSoon(30);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("isInWarranty: 日期在质保期内返回 true")
    void isInWarranty_withinRange_true() {
        Warranty warranty = sampleWarranty(1L, 10L, 100L,
                LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1));
        when(warrantyMapper.selectOne(any(Wrapper.class), any(Boolean.class))).thenReturn(warranty);

        boolean result = warrantyService.isInWarranty(10L, LocalDate.of(2024, 6, 1));

        assertTrue(result);
    }

    @Test
    @DisplayName("isInWarranty: 日期在质保期外返回 false")
    void isInWarranty_outsideRange_false() {
        Warranty warranty = sampleWarranty(1L, 10L, 100L,
                LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1));
        when(warrantyMapper.selectOne(any(Wrapper.class), any(Boolean.class))).thenReturn(warranty);

        boolean result = warrantyService.isInWarranty(10L, LocalDate.of(2025, 6, 1));

        assertFalse(result);
    }

    @Test
    @DisplayName("isInWarranty: 无质保记录返回 false")
    void isInWarranty_noWarranty_false() {
        when(warrantyMapper.selectOne(any(Wrapper.class), any(Boolean.class))).thenReturn(null);

        boolean result = warrantyService.isInWarranty(10L, LocalDate.now());

        assertFalse(result);
    }

    @Test
    @DisplayName("isInWarranty: assetId 为 null 返回 false")
    void isInWarranty_nullAssetId_false() {
        boolean result = warrantyService.isInWarranty(null, LocalDate.now());
        assertFalse(result);
    }

    @Test
    @DisplayName("isInWarranty: date 为 null 返回 false")
    void isInWarranty_nullDate_false() {
        boolean result = warrantyService.isInWarranty(10L, null);
        assertFalse(result);
    }

    @Test
    @DisplayName("isInWarranty: 质保记录缺少日期返回 false")
    void isInWarranty_missingDates_false() {
        Warranty warranty = Warranty.builder()
                .assetId(10L)
                .startDate(null)
                .endDate(LocalDate.of(2025, 1, 1))
                .build();
        warranty.setId(1L);
        when(warrantyMapper.selectOne(any(Wrapper.class), any(Boolean.class))).thenReturn(warranty);

        boolean result = warrantyService.isInWarranty(10L, LocalDate.now());

        assertFalse(result);
    }

    @Test
    @DisplayName("initWarrantyForProject: projectId 为 null 时不初始化")
    void initWarrantyForProject_nullProjectId_noOp() {
        warrantyService.initWarrantyForProject(null, LocalDate.now(), 12);
        verify(assetMapper, never()).selectList(any(Wrapper.class));
    }

    @Test
    @DisplayName("initWarrantyForProject: finalAcceptanceDate 为 null 时不初始化")
    void initWarrantyForProject_nullDate_noOp() {
        warrantyService.initWarrantyForProject(100L, null, 12);
        verify(assetMapper, never()).selectList(any(Wrapper.class));
    }

    @Test
    @DisplayName("initWarrantyForProject: 为项目下无质保的设备创建质保记录")
    void initWarrantyForProject_createsWarranties() {
        Asset asset1 = Asset.builder().serialNo("SN-1").build();
        asset1.setId(1L);
        Asset asset2 = Asset.builder().serialNo("SN-2").build();
        asset2.setId(2L);
        when(assetMapper.selectList(any(Wrapper.class))).thenReturn(Arrays.asList(asset1, asset2));
        // count 返回 0 表示设备无已有质保
        when(warrantyMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(warrantyMapper.insert(any(Warranty.class))).thenReturn(1);

        LocalDate acceptanceDate = LocalDate.of(2024, 6, 1);
        warrantyService.initWarrantyForProject(100L, acceptanceDate, 12);

        // 验证为每个设备创建了一条质保记录
        verify(warrantyMapper, times(2)).insert(any(Warranty.class));
    }

    @Test
    @DisplayName("initWarrantyForProject: 已有质保的设备跳过")
    void initWarrantyForProject_skipExisting() {
        Asset asset1 = Asset.builder().serialNo("SN-1").build();
        asset1.setId(1L);
        when(assetMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(asset1));
        // count 返回 1 表示设备已有质保
        when(warrantyMapper.selectCount(any(Wrapper.class))).thenReturn(1L);

        warrantyService.initWarrantyForProject(100L, LocalDate.of(2024, 6, 1), 12);

        verify(warrantyMapper, never()).insert(any(Warranty.class));
    }

    @Test
    @DisplayName("initWarrantyForProject: durationMonths 为 null 时使用默认 12 个月")
    void initWarrantyForProject_defaultDuration() {
        Asset asset1 = Asset.builder().serialNo("SN-1").build();
        asset1.setId(1L);
        when(assetMapper.selectList(any(Wrapper.class))).thenReturn(Collections.singletonList(asset1));
        when(warrantyMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(warrantyMapper.insert(any(Warranty.class))).thenAnswer(invocation -> {
            Warranty w = invocation.getArgument(0);
            assertEquals(12, w.getDurationMonths(), "默认质保月数应为 12");
            // 开始日期应为终验日期 + 1 天
            assertEquals(LocalDate.of(2024, 6, 2), w.getStartDate());
            // 结束日期应为开始日期 + 12 个月
            assertEquals(LocalDate.of(2025, 6, 2), w.getEndDate());
            return 1;
        });

        warrantyService.initWarrantyForProject(100L, LocalDate.of(2024, 6, 1), null);

        verify(warrantyMapper, times(1)).insert(any(Warranty.class));
    }
}
