package com.dp.plat.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * PMS 业务指标采集组件。
 *
 * <p>封装三类核心业务指标，供各业务 Service 注入调用：
 * <ul>
 *   <li>{@code pms_project_created_total} —— 项目创建计数（Counter，按项目类型 tag）</li>
 *   <li>{@code pms_asset_status} —— 资产状态分布（Gauge，按状态 tag）</li>
 *   <li>{@code pms_settlement_amount} —— 结算金额分布（DistributionSummary，按币种 tag）</li>
 * </ul>
 *
 * <p>放置于 pms-common 模块（所有业务模块的公共依赖），使 ProjectServiceImpl、
 * AssetServiceImpl、SettlementServiceImpl 等均可直接注入。{@link MeterRegistry} 的
 * 运行时实现（PrometheusMeterRegistry）由 pms-admin 模块的
 * spring-boot-starter-actuator + micrometer-registry-prometheus 提供，
 * 组件扫描由 {@code PmsApplication} 的 {@code scanBasePackages = "com.dp.plat"} 覆盖。</p>
 */
@Component
public class BusinessMetrics {

    private final MeterRegistry registry;

    /** 项目创建 Counter 缓存（按项目类型），避免重复注册同名 meter。 */
    private final ConcurrentHashMap<String, Counter> projectCounters = new ConcurrentHashMap<>();

    /** 结算金额 DistributionSummary 缓存（按币种）。 */
    private final ConcurrentHashMap<String, DistributionSummary> settlementSummaries = new ConcurrentHashMap<>();

    /** 资产状态 Gauge 引用缓存（按状态），AtomicReference 持有可变最新值。 */
    private final ConcurrentHashMap<String, AtomicReference<Double>> assetStatusGauges = new ConcurrentHashMap<>();

    public BusinessMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * 记录项目创建事件，按项目类型累加计数。
     *
     * @param projectType 项目类型（NETWORK_DEVICE / SECURITY / DATACENTER 等），为 null 时记为 UNKNOWN
     */
    public void recordProjectCreated(String projectType) {
        String type = projectType == null ? "UNKNOWN" : projectType;
        projectCounters.computeIfAbsent(type, t -> Counter.builder("pms_project_created_total")
                        .description("项目创建总数")
                        .tag("type", t)
                        .register(registry))
                .increment();
    }

    /**
     * 注册或更新资产状态分布 Gauge。
     *
     * <p>同一状态首次调用时注册 Gauge（绑定 {@link AtomicReference}），
     * 后续调用仅更新引用值，Prometheus 抓取时读取最新值。Gauge 要求被引用对象
     * 保持稳定，故使用 AtomicReference 而非直接传值。</p>
     *
     * @param status 资产状态（RECEIVED / INSTALLED / STAGED / SCRAPPED 等）
     * @param value  当前该状态的资产数量
     */
    public void registerAssetStatusGauge(String status, Number value) {
        String safeStatus = status == null ? "UNKNOWN" : status;
        AtomicReference<Double> ref = assetStatusGauges.computeIfAbsent(safeStatus, s -> {
            AtomicReference<Double> holder = new AtomicReference<>(0.0);
            Gauge.builder("pms_asset_status", holder, aref -> aref.get() == null ? 0.0 : aref.get())
                    .description("资产状态分布数量")
                    .baseUnit("count")
                    .tag("status", s)
                    .register(registry);
            return holder;
        });
        ref.set(value == null ? 0.0 : value.doubleValue());
    }

    /**
     * 记录结算金额，用于统计金额分布（count / totalAmount / max / percentiles）。
     *
     * @param amount   结算总金额
     * @param currency 币种（CNY / USD 等），为 null 时记为 CNY
     */
    public void recordSettlementAmount(double amount, String currency) {
        String cur = currency == null ? "CNY" : currency;
        settlementSummaries.computeIfAbsent(cur, c -> DistributionSummary.builder("pms_settlement_amount")
                        .description("结算金额分布")
                        .baseUnit("yuan")
                        .tag("currency", c)
                        .register(registry))
                .record(amount);
    }
}
