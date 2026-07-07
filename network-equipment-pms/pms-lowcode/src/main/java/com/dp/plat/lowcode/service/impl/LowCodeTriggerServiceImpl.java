package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.engine.trigger.LowCodeTrigger;
import com.dp.plat.lowcode.engine.trigger.QuartzTriggerExecutor;
import com.dp.plat.lowcode.engine.trigger.TriggerExecutor;
import com.dp.plat.lowcode.mapper.LowCodeTriggerMapper;
import com.dp.plat.lowcode.service.LowCodeTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 低代码触发器服务实现。
 *
 * <p>根据触发器 type 分发到对应 TriggerExecutor 执行。
 * QUARTZ 类型触发器在保存/删除时同步注册/卸载 Quartz 调度任务（借鉴 ServiceNow Flow Designer）。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowCodeTriggerServiceImpl extends ServiceImpl<LowCodeTriggerMapper, LowCodeTrigger>
        implements LowCodeTriggerService {

    private final List<TriggerExecutor> executors;
    private final QuartzTriggerExecutor quartzTriggerExecutor;

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

    @Override
    public boolean saveOrUpdate(LowCodeTrigger trigger) {
        boolean saved = super.saveOrUpdate(trigger);
        if (saved && "QUARTZ".equals(trigger.getType())) {
            try {
                if ("ACTIVE".equals(trigger.getStatus())) {
                    quartzTriggerExecutor.scheduleJob(trigger);
                } else {
                    // 非激活状态：卸载已有调度任务
                    quartzTriggerExecutor.unscheduleJob(trigger.getCode());
                }
            } catch (Exception e) {
                // 调度注册失败不阻断保存，仅记录日志
                log.error("QUARTZ 触发器调度注册失败（不影响保存）: triggerCode={}", trigger.getCode(), e);
            }
        }
        return saved;
    }

    @Override
    public boolean removeById(java.io.Serializable id) {
        LowCodeTrigger trigger = id == null ? null : getById(id);
        if (trigger != null && "QUARTZ".equals(trigger.getType())) {
            try {
                quartzTriggerExecutor.unscheduleJob(trigger.getCode());
            } catch (Exception e) {
                log.error("QUARTZ 触发器调度卸载失败（不影响删除）: triggerCode={}",
                        trigger.getCode(), e);
            }
        }
        return super.removeById(id);
    }
}
