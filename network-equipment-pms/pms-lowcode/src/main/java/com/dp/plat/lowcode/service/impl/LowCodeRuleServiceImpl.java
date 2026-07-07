package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.engine.rule.RuleEngineService;
import com.dp.plat.lowcode.entity.LowCodeRule;
import com.dp.plat.lowcode.mapper.LowCodeRuleMapper;
import com.dp.plat.lowcode.service.LowCodeRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 低代码规则服务实现。
 *
 * <p>根据规则类型分发到对应执行方法：决策表 / 表达式 / LiteFlow。</p>
 */
@Service
@RequiredArgsConstructor
public class LowCodeRuleServiceImpl extends ServiceImpl<LowCodeRuleMapper, LowCodeRule>
        implements LowCodeRuleService {

    private final RuleEngineService ruleEngineService;

    @Override
    public Map<String, Object> execute(String code, Map<String, Object> facts) {
        LowCodeRule rule = getOne(new LambdaQueryWrapper<LowCodeRule>()
                .eq(LowCodeRule::getCode, code));
        if (rule == null) {
            throw new RuntimeException("规则不存在: " + code);
        }
        Map<String, Object> result = new HashMap<>();
        switch (rule.getType()) {
            case "DECISION_TABLE" -> result.put("actions", ruleEngineService.executeDecisionTable(rule.getDefinition(), facts));
            case "EXPRESSION" -> result.put("result", ruleEngineService.executeExpression(rule.getDefinition(), facts));
            case "LITEFLOW" -> result.put("result", ruleEngineService.executeLiteFlow(rule.getDefinition(), facts));
            default -> throw new IllegalArgumentException("未知规则类型: " + rule.getType());
        }
        return result;
    }
}
