package com.dp.plat.lowcode.engine.apm;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 低代码 APM 指标服务。
 *
 * <p>借鉴 Joget APM，为微流/规则/连接器/触发器执行提供统一的 counter + timer 指标记录。
 * MeterRegistry 通过 {@code @Autowired(required=false)} 注入，无 actuator 环境下安全跳过。</p>
 *
 * <p>指标命名规范：
 * <ul>
 *   <li>{@code lowcode_microflow_execution_total} — 微流执行次数（tag: code, status）</li>
 *   <li>{@code lowcode_microflow_execution_duration} — 微流执行耗时（tag: code）</li>
 *   <li>{@code lowcode_rule_execution_total} — 规则执行次数（tag: type, status）</li>
 *   <li>{@code lowcode_rule_execution_duration} — 规则执行耗时（tag: type）</li>
 *   <li>{@code lowcode_connector_call_total} — 连接器调用次数（tag: type, status）</li>
 *   <li>{@code lowcode_connector_call_duration} — 连接器调用耗时（tag: type）</li>
 *   <li>{@code lowcode_trigger_execution_total} — 触发器执行次数（tag: type, status）</li>
 *   <li>{@code lowcode_trigger_execution_duration} — 触发器执行耗时（tag: type）</li>
 * </ul></p>
 */
@Slf4j
@Service
public class LowCodeApmService {

    private final MeterRegistry meterRegistry;

    @Autowired
    public LowCodeApmService(@org.springframework.beans.factory.annotation.Autowired(required = false) MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        if (meterRegistry == null) {
            log.info("MeterRegistry 未注入，低代码 APM 指标记录将安全跳过");
        }
    }

    /** 记录微流执行指标 */
    public void recordMicroflowExecution(String code, String status, long durationMs) {
        if (meterRegistry == null) return;
        try {
            Counter.builder("lowcode_microflow_execution_total")
                    .tag("code", code == null ? "unknown" : code)
                    .tag("status", status == null ? "UNKNOWN" : status)
                    .register(meterRegistry).increment();
            Timer.builder("lowcode_microflow_execution_duration")
                    .tag("code", code == null ? "unknown" : code)
                    .register(meterRegistry).record(java.time.Duration.ofMillis(durationMs));
        } catch (Exception e) {
            log.debug("记录微流 APM 指标失败（不影响主流程）: code={}, status={}", code, status, e);
        }
    }

    /** 记录规则执行指标 */
    public void recordRuleExecution(String type, String status, long durationMs) {
        if (meterRegistry == null) return;
        try {
            Counter.builder("lowcode_rule_execution_total")
                    .tag("type", type == null ? "unknown" : type)
                    .tag("status", status == null ? "UNKNOWN" : status)
                    .register(meterRegistry).increment();
            Timer.builder("lowcode_rule_execution_duration")
                    .tag("type", type == null ? "unknown" : type)
                    .register(meterRegistry).record(java.time.Duration.ofMillis(durationMs));
        } catch (Exception e) {
            log.debug("记录规则 APM 指标失败（不影响主流程）: type={}, status={}", type, status, e);
        }
    }

    /** 记录连接器调用指标 */
    public void recordConnectorCall(String type, String status, long durationMs) {
        if (meterRegistry == null) return;
        try {
            Counter.builder("lowcode_connector_call_total")
                    .tag("type", type == null ? "unknown" : type)
                    .tag("status", status == null ? "UNKNOWN" : status)
                    .register(meterRegistry).increment();
            Timer.builder("lowcode_connector_call_duration")
                    .tag("type", type == null ? "unknown" : type)
                    .register(meterRegistry).record(java.time.Duration.ofMillis(durationMs));
        } catch (Exception e) {
            log.debug("记录连接器 APM 指标失败（不影响主流程）: type={}, status={}", type, status, e);
        }
    }

    /** 记录触发器执行指标 */
    public void recordTriggerExecution(String type, String status, long durationMs) {
        if (meterRegistry == null) return;
        try {
            Counter.builder("lowcode_trigger_execution_total")
                    .tag("type", type == null ? "unknown" : type)
                    .tag("status", status == null ? "UNKNOWN" : status)
                    .register(meterRegistry).increment();
            Timer.builder("lowcode_trigger_execution_duration")
                    .tag("type", type == null ? "unknown" : type)
                    .register(meterRegistry).record(java.time.Duration.ofMillis(durationMs));
        } catch (Exception e) {
            log.debug("记录触发器 APM 指标失败（不影响主流程）: type={}, status={}", type, status, e);
        }
    }
}
