package com.dp.plat.lowcode.engine.trigger;

import com.dp.plat.lowcode.service.LowCodeMicroflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * CRUD 触发器执行器：监听动态实体 CRUD 事件。
 *
 * <p>本实现为简化版，直接调用目标微流。完整实现应在 DynamicEntityDataService CRUD 钩子中触发。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrudTriggerExecutor implements TriggerExecutor {

    private final LowCodeMicroflowService microflowService;

    @Override
    public String supportedType() {
        return "CRUD";
    }

    @Override
    public Map<String, Object> execute(LowCodeTrigger trigger, Map<String, Object> data) {
        log.info("CRUD 触发器执行: trigger={}, target={}", trigger.getCode(), trigger.getTargetCode());
        if ("MICROFLOW".equals(trigger.getTargetType())) {
            return microflowService.execute(trigger.getTargetCode(), data);
        }
        return Map.of("message", "CRUD trigger fired, target=" + trigger.getTargetCode());
    }
}
