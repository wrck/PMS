package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.engine.trigger.LowCodeTrigger;
import com.dp.plat.lowcode.engine.trigger.TriggerExecutor;
import com.dp.plat.lowcode.mapper.LowCodeTriggerMapper;
import com.dp.plat.lowcode.service.LowCodeTriggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 低代码触发器服务实现。
 *
 * <p>根据触发器 type 分发到对应 TriggerExecutor 执行。</p>
 */
@Service
@RequiredArgsConstructor
public class LowCodeTriggerServiceImpl extends ServiceImpl<LowCodeTriggerMapper, LowCodeTrigger>
        implements LowCodeTriggerService {

    private final List<TriggerExecutor> executors;

    @Override
    public Map<String, Object> executeTrigger(String code, Map<String, Object> data) {
        LowCodeTrigger trigger = getOne(new LambdaQueryWrapper<LowCodeTrigger>()
                .eq(LowCodeTrigger::getCode, code));
        if (trigger == null) {
            throw new RuntimeException("触发器不存在: " + code);
        }
        TriggerExecutor executor = executors.stream()
                .filter(e -> e.supportsType(trigger.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无可用执行器: " + trigger.getType()));
        return executor.execute(trigger, data);
    }
}
