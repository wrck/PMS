package com.dp.plat.common.utils;

import java.util.*;
import java.util.regex.*;

/**
 * 通知模板工具类 - 迁移自老系统 NotificationTemplateUtil (328行, 9个方法)
 *
 * 用于生成系统通知内容
 */
public class NotificationTemplateUtil {

    /** 生成项目创建通知 */
    public static String buildProjectCreateMsg(String projectCode, String projectName, String pmName) {
        return String.format("新项目已创建: [%s] %s, 项目经理: %s", projectCode, projectName, pmName);
    }

    /** 生成项目回退通知 */
    public static String buildProjectBackMsg(String projectCode, String projectName, String reason) {
        return String.format("项目已回退: [%s] %s, 原因: %s", projectCode, projectName, reason);
    }

    /** 生成售前申请通知 */
    public static String buildPresalesApplyMsg(String presalesCode, String projectName) {
        return String.format("售前项目申请: [%s] %s, 请审批", presalesCode, projectName);
    }

    /** 生成回访申请通知 */
    public static String buildCallbackApplyMsg(String projectCode, String projectName) {
        return String.format("回访申请: [%s] %s, 请审批", projectCode, projectName);
    }

    /** 生成闭环申请通知 */
    public static String buildClosedLoopApplyMsg(String projectCode, String projectName, String applyType) {
        return String.format("闭环申请(%s): [%s] %s, 请审批", applyType, projectCode, projectName);
    }

    /** 生成分包申请通知 */
    public static String buildSubcontractApplyMsg(String subcontractName, String projectName) {
        return String.format("分包申请: %s (项目: %s), 请审批", subcontractName, projectName);
    }

    /** 替换模板中的变量 */
    public static String replaceVariables(String template, Map<String, String> variables) {
        if (template == null || variables == null) return template;
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    /** 解析模板中的变量名 */
    public static List<String> extractVariables(String template) {
        List<String> vars = new ArrayList<>();
        if (template == null) return vars;
        Matcher matcher = Pattern.compile("\\$\\{([^}]+)}").matcher(template);
        while (matcher.find()) {
            vars.add(matcher.group(1));
        }
        return vars;
    }

    /** 生成通用通知 */
    public static String buildGeneralMsg(String title, String content) {
        return String.format("%s: %s", title, content);
    }
}
