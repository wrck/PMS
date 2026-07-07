package com.dp.plat.lowcode.engine.trigger;

import com.dp.plat.lowcode.service.LowCodeMicroflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Quartz 定时触发器执行器。
 *
 * <p>解析 config.cron 表达式，注册 Quartz Job。
 * 简化实现：仅记录日志并执行一次目标微流。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzTriggerExecutor implements TriggerExecutor {

    private final LowCodeMicroflowService microflowService;

    @Override
    public String supportedType() {
        return "QUARTZ";
    }

    @Override
    public Map<String, Object> execute(LowCodeTrigger trigger, Map<String, Object> data) {
        log.info("Quartz 触发器手动执行: trigger={}, cron config={}", trigger.getCode(), trigger.getConfig());
        if ("MICROFLOW".equals(trigger.getTargetType())) {
            return microflowService.execute(trigger.getTargetCode(), data);
        }
        return Map.of("message", "Quartz trigger fired, target=" + trigger.getTargetCode());
    }
}
