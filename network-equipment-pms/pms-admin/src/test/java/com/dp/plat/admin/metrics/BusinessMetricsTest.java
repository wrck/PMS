package com.dp.plat.admin.metrics;

import com.dp.plat.common.metrics.BusinessMetrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link BusinessMetrics} 单元测试。
 *
 * <p>使用 {@link SimpleMeterRegistry}（内存级、无外部依赖）验证三类业务指标的
 * 计数与量值正确性，不依赖 Spring 容器。</p>
 */
class BusinessMetricsTest {

    private SimpleMeterRegistry registry;
    private BusinessMetrics businessMetrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        businessMetrics = new BusinessMetrics(registry);
    }

    @Test
    @DisplayName("recordProjectCreated: 同类型累加计数，不同类型独立计数")
    void recordProjectCreated_accumulatesByType() {
        businessMetrics.recordProjectCreated("NETWORK_DEVICE");
        businessMetrics.recordProjectCreated("NETWORK_DEVICE");
        businessMetrics.recordProjectCreated("SECURITY");

        Counter networkCounter = registry.find("pms_project_created_total")
                .tag("type", "NETWORK_DEVICE").counter().orElse(null);
        Counter securityCounter = registry.find("pms_project_created_total")
                .tag("type", "SECURITY").counter().orElse(null);

        assertNotNull(networkCounter, "NETWORK_DEVICE 计数器应已注册");
        assertNotNull(securityCounter, "SECURITY 计数器应已注册");
        assertEquals(2.0, networkCounter.count(), 0.001, "NETWORK_DEVICE 应计数 2 次");
        assertEquals(1.0, securityCounter.count(), 0.001, "SECURITY 应计数 1 次");
    }

    @Test
    @DisplayName("recordProjectCreated: null 类型记为 UNKNOWN")
    void recordProjectCreated_nullType_recordedAsUnknown() {
        businessMetrics.recordProjectCreated(null);

        Counter counter = registry.find("pms_project_created_total")
                .tag("type", "UNKNOWN").counter().orElse(null);
        assertNotNull(counter, "UNKNOWN 计数器应已注册");
        assertEquals(1.0, counter.count(), 0.001);
    }

    @Test
    @DisplayName("registerAssetStatusGauge: 首次注册后更新值，Gauge 反映最新值")
    void registerAssetStatusGauge_updatesValue() {
        businessMetrics.registerAssetStatusGauge("RECEIVED", 10);
        // 更新为最新值，Gauge 应反映更新后的值而非初始 0
        businessMetrics.registerAssetStatusGauge("RECEIVED", 15);
        businessMetrics.registerAssetStatusGauge("INSTALLED", 3);

        Gauge receivedGauge = registry.find("pms_asset_status")
                .tag("status", "RECEIVED").gauge().orElse(null);
        Gauge installedGauge = registry.find("pms_asset_status")
                .tag("status", "INSTALLED").gauge().orElse(null);

        assertNotNull(receivedGauge, "RECEIVED gauge 应已注册");
        assertNotNull(installedGauge, "INSTALLED gauge 应已注册");
        assertEquals(15.0, receivedGauge.value(), 0.001, "RECEIVED gauge 应反映最新值 15");
        assertEquals(3.0, installedGauge.value(), 0.001, "INSTALLED gauge 应为 3");
    }

    @Test
    @DisplayName("recordSettlementAmount: 按币种记录金额并统计 count/totalAmount")
    void recordSettlementAmount_recordsAmountByCurrency() {
        businessMetrics.recordSettlementAmount(1500.00, "CNY");
        businessMetrics.recordSettlementAmount(1000.00, "CNY");
        businessMetrics.recordSettlementAmount(500.00, "USD");

        DistributionSummary cnySummary = registry.find("pms_settlement_amount")
                .tag("currency", "CNY").summary().orElse(null);
        DistributionSummary usdSummary = registry.find("pms_settlement_amount")
                .tag("currency", "USD").summary().orElse(null);

        assertNotNull(cnySummary, "CNY summary 应已注册");
        assertNotNull(usdSummary, "USD summary 应已注册");
        assertEquals(2L, cnySummary.count(), "CNY 应记录 2 笔");
        assertEquals(2500.00, cnySummary.totalAmount(), 0.001, "CNY 总额应为 2500");
        assertEquals(1L, usdSummary.count(), "USD 应记录 1 笔");
        assertEquals(500.00, usdSummary.totalAmount(), 0.001, "USD 总额应为 500");
    }

    @Test
    @DisplayName("recordSettlementAmount: null 币种记为 CNY")
    void recordSettlementAmount_nullCurrency_recordedAsCny() {
        businessMetrics.recordSettlementAmount(100.00, null);

        DistributionSummary summary = registry.find("pms_settlement_amount")
                .tag("currency", "CNY").summary().orElse(null);
        assertNotNull(summary, "CNY summary 应已注册");
        assertEquals(1L, summary.count());
        assertEquals(100.00, summary.totalAmount(), 0.001);
    }
}
