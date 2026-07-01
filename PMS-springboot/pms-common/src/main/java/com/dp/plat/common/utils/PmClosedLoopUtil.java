package com.dp.plat.common.utils;

import com.dp.plat.common.constants.MessageUtil;

/**
 * 闭环工具类 - 迁移自老系统 PmClosedLoopUtil (114行, 4个方法)
 */
public class PmClosedLoopUtil {

    /** 获取评估类型名称 */
    public static String getEvaluationTypeName(int evaluationType) {
        switch (evaluationType) {
            case MessageUtil.CL_EVALU_TYPE_PM: return "PM评估";
            case MessageUtil.CL_EVALU_TYPE_SM: return "SM评估";
            case MessageUtil.CL_EVALU_TYPE_CB: return "CB评估";
            case MessageUtil.CL_EVALU_TYPE_CL: return "CL评估";
            default: return "未知";
        }
    }

    /** 获取评估结果名称 */
    public static String getEvaluationResultName(int result) {
        switch (result) {
            case 0: return "待评估";
            case 1: return "通过";
            case MessageUtil.CL_EVALU_RESULT_REJECT: return "驳回";
            case MessageUtil.CL_EVALU_RESULT_CANTCB: return "无法闭环";
            default: return "未知";
        }
    }

    /** 获取闭环状态名称 */
    public static String getStatusName(int status) {
        switch (status) {
            case 0: return "草稿";
            case MessageUtil.CL_STATUS_SUBMIT: return "已提交";
            case 2: return "已审批";
            case 3: return "已关闭";
            default: return "未知";
        }
    }

    /** 判断是否可以发起下一步评估 */
    public static boolean canProceedToNext(int currentType, int currentResult) {
        if (currentResult == MessageUtil.CL_EVALU_RESULT_REJECT) return false;
        if (currentResult == MessageUtil.CL_EVALU_RESULT_CANTCB) return false;
        return currentType < MessageUtil.CL_EVALU_TYPE_CL;
    }
}
