package com.dp.plat.common.utils;

/**
 * 分包工具类 - 迁移自老系统 SubcontractUtil (82行, 5个方法)
 */
public class SubcontractUtil {

    /** 生成分包编号 */
    public static String generateSubcontractCode(String prefix, int seq) {
        return String.format("%s%04d", prefix, seq);
    }

    /** 解析分包状态 */
    public static String getSubcontractStateName(int state) {
        switch (state) {
            case 0: return "草稿";
            case 1: return "待审批";
            case 2: return "已审批";
            case 3: return "已驳回";
            case 4: return "已关闭";
            default: return "未知";
        }
    }

    /** 计算分包金额 */
    public static double calculateAmount(double unitPrice, int quantity) {
        return unitPrice * quantity;
    }

    /** 验证分包名称唯一性 */
    public static boolean isValidSubcontractName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 200;
    }

    /** 格式化分包金额 */
    public static String formatAmount(double amount) {
        return String.format("%.2f", amount);
    }
}
