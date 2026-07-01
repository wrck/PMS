package com.dp.plat.common.utils;

import java.util.*;

/**
 * 问卷工具类 - 迁移自老系统 QuestionnarieUtil (254行, 5个方法)
 */
public class QuestionnarieUtil {

    /** 计算问卷总分 */
    public static int calculateTotalScore(List<Map<String, Object>> lineAnswers) {
        if (lineAnswers == null) return 0;
        int total = 0;
        for (Map<String, Object> answer : lineAnswers) {
            Object score = answer.get("score");
            if (score instanceof Number) {
                total += ((Number) score).intValue();
            }
        }
        return total;
    }

    /** 验证问卷是否完整填写 */
    public static boolean isQuestionnaireComplete(List<Map<String, Object>> lines, List<Map<String, Object>> answers) {
        if (lines == null || answers == null) return false;
        Set<Long> requiredLineIds = new HashSet<>();
        for (Map<String, Object> line : lines) {
            Object isRequired = line.get("isRequired");
            if (isRequired != null && ((Number) isRequired).intValue() == 1) {
                Object lineId = line.get("id");
                if (lineId instanceof Number) {
                    requiredLineIds.add(((Number) lineId).longValue());
                }
            }
        }
        Set<Long> answeredLineIds = new HashSet<>();
        for (Map<String, Object> answer : answers) {
            Object lineId = answer.get("lineId");
            if (lineId instanceof Number) {
                answeredLineIds.add(((Number) lineId).longValue());
            }
        }
        return answeredLineIds.containsAll(requiredLineIds);
    }

    /** 格式化问卷结果 */
    public static Map<String, Object> formatQuestionnaireResult(Map<String, Object> header, List<Map<String, Object>> lines) {
        Map<String, Object> result = new HashMap<>();
        result.put("header", header);
        result.put("lines", lines);
        result.put("totalScore", calculateTotalScore(lines));
        result.put("isComplete", lines != null && !lines.isEmpty());
        return result;
    }
}
