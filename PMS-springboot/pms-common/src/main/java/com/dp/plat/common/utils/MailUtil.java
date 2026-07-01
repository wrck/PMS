package com.dp.plat.common.utils;

import java.util.*;

/**
 * 邮件工具类 - 迁移自老系统 MailUtil (1646行, 19个方法)
 *
 * 使用Spring Boot的spring-boot-starter-mail替代老系统自研邮件服务
 * 核心邮件发送功能通过Spring Mail实现,此类保留辅助方法
 */
public class MailUtil {

    /** 构建项目到货回执邮件内容 */
    public static String buildArrivalReceiptContent(String projectCode, String projectName, String pmName) {
        return String.format("<h3>项目到货回执</h3><p>项目编号: %s</p><p>项目名称: %s</p><p>项目经理: %s</p><p>请确认到货信息。</p>",
                projectCode, projectName, pmName);
    }

    /** 构建项目初终验邮件内容 */
    public static String buildInspectionContent(String projectCode, String projectName, String inspectionType) {
        return String.format("<h3>项目%s</h3><p>项目编号: %s</p><p>项目名称: %s</p><p>请关注。</p>",
                inspectionType, projectCode, projectName);
    }

    /** 构建项目到货延迟邮件内容 */
    public static String buildArrivalDelayContent(String projectCode, String projectName, int delayDays) {
        return String.format("<h3>项目到货延迟提醒</h3><p>项目编号: %s</p><p>项目名称: %s</p><p>延迟天数: %d天</p>",
                projectCode, projectName, delayDays);
    }

    /** 构建项目验收邮件内容 */
    public static String buildProjectInspectionContent(String projectCode, String projectName) {
        return String.format("<h3>项目验收提醒</h3><p>项目编号: %s</p><p>项目名称: %s</p><p>请及时处理验收事宜。</p>",
                projectCode, projectName);
    }

    /** 构建运维日报邮件内容 */
    public static String buildMaintenanceDailyReport(String date, String content) {
        return String.format("<h3>运维日报 - %s</h3><div>%s</div>", date, content);
    }

    /** 构建分包下期付款邮件内容 */
    public static String buildSubcontractPaymentContent(String subcontractName, String paymentInfo) {
        return String.format("<h3>分包付款提醒</h3><p>分包名称: %s</p><p>付款信息: %s</p>",
                subcontractName, paymentInfo);
    }

    /** 清理HTML标签(用于纯文本邮件) */
    public static String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]+>", "").replaceAll("&nbsp;", " ").replaceAll("\\s+", " ").trim();
    }

    /** 验证邮箱格式 */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /** 解析多个邮箱地址 */
    public static List<String> parseEmailAddresses(String emails) {
        List<String> result = new ArrayList<>();
        if (emails == null || emails.isEmpty()) return result;
        for (String email : emails.split("[,;]")) {
            String trimmed = email.trim();
            if (isValidEmail(trimmed)) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
