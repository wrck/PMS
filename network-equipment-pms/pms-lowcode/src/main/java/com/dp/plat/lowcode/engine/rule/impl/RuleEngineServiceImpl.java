package com.dp.plat.lowcode.engine.rule.impl;

import com.dp.plat.lowcode.engine.rule.RuleEngineService;
import com.googlecode.aviator.AviatorEvaluator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 规则引擎服务实现。
 *
 * <p>决策表：解析 JSON，按行匹配条件后返回行动作。
 * 表达式：通过 Aviator 求值。
 * LiteFlow：占位实现，后续接入 pms-rules LiteFlowEngine。</p>
 */
@Slf4j
@Service
public class RuleEngineServiceImpl implements RuleEngineService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> executeDecisionTable(String definition, Map<String, Object> facts) {
        // 决策表 JSON 结构：{rows: [{conditions: [{field, operator, value}], actions: {field, value}}]}
        List<Map<String, Object>> hitActions = new ArrayList<>();
        try {
            Map<String, Object> table = objectMapper.readValue(definition, Map.class);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) table.get("rows");
            if (rows == null) return hitActions;
            for (Map<String, Object> row : rows) {
                if (matchRow((List<Map<String, Object>>) row.get("conditions"), facts)) {
                    hitActions.add((Map<String, Object>) row.get("actions"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("决策表执行失败", e);
        }
        return hitActions;
    }

    @SuppressWarnings("unchecked")
    private boolean matchRow(List<Map<String, Object>> conditions, Map<String, Object> facts) {
        if (conditions == null) return true;
        for (Map<String, Object> cond : conditions) {
            String field = (String) cond.get("field");
            String op = (String) cond.get("operator");
            Object expected = cond.get("value");
            Object actual = facts.get(field);
            if (!matchOperator(actual, op, expected)) return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean matchOperator(Object actual, String op, Object expected) {
        if (actual == null) return false;
        return switch (op) {
            case "EQ" -> actual.equals(expected);
            case "NE" -> !actual.equals(expected);
            case "GT" -> compareTo(actual, expected) > 0;
            case "GE" -> compareTo(actual, expected) >= 0;
            case "LT" -> compareTo(actual, expected) < 0;
            case "LE" -> compareTo(actual, expected) <= 0;
            case "IN" -> ((List<Object>) expected).contains(actual);
            default -> false;
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compareTo(Object a, Object b) {
        return ((Comparable) a).compareTo(b);
    }

    @Override
    public Object executeExpression(String expression, Map<String, Object> context) {
        return AviatorEvaluator.execute(expression, context);
    }

    @Override
    public Object executeLiteFlow(String el, Map<String, Object> context) {
        // LiteFlow 集成占位，后续接入 pms-rules LiteFlowEngine
        log.warn("LiteFlow 执行暂未接入，EL: {}", el);
        return null;
    }
}
