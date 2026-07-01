package com.dp.plat.common.constants;

/**
 * 消息/角色/基础数据常量 - 迁移自老系统 MessageUtil (600行, 155个常量)
 *
 * 包含所有角色编码、基础数据类型编码、项目状态编码等
 */
public class MessageUtil {

    // ===== 角色编码 =====
    public static final int ROLE_ADMIN = 1;                    // 管理员
    public static final int ROLE_SERVICEMANAGER = 2;           // 服务经理
    public static final int ROLE_PROGRAMMANAGER = 3;           // 项目经理
    public static final int ROLE_ENGINEEMANAGER = 4;           // 工程管理部
    public static final int ROLE_ENGINEEMANAGER_LEADER = 5;    // 工程管理部领导
    public static final int ROLE_CALLBACKPER = 6;              // 回访人员
    public static final int ROLE_AREA_LEADER = 7;              // 区域主管
    public static final int ROLE_PROB_ADMIN = 18;              // 技术公告管理员
    public static final int ROLE_PROB_SUPPORTER = 19;          // 技术公告技术支持
    public static final int ROLE_PROB_RD = 20;                 // 技术公告研发
    public static final int ROLE_PRESALES_STAFF = 21;          // 售前人员
    public static final int ROLE_FINANCIAL_STAFF = 22;         // 财务人员
    public static final int ROLE_COMPONENT_ADMIN = 23;         // 组件管理员

    // ===== 基础数据类型编码 =====
    public static final String BASIC_DATA_NAV_WORK_TAB = "34";  // 工作台选项卡
    public static final String BASIC_DATA_PROJECT_STATE = "02"; // 项目状态
    public static final String BASIC_DATA_PROJECT_TYPE = "05";  // 项目类型
    public static final String BASIC_DATA_TASK_TYPE = "09";     // 任务类型
    public static final String BASIC_DATA_PLAN_STATE = "22";    // 计划状态
    public static final String BASIC_DATA_TASK_STATE = "23";    // 任务状态
    public static final String BASIC_DATA_WATCH = "30";         // 公告关注级别
    public static final String BASIC_DATA_PROB_STATUS = "31";   // 公告状态
    public static final String BASIC_DATA_PRIORITY = "32";      // 优先级
    public static final String BASIC_DATA_RESTORE_STATUS = "33"; // 恢复任务状态
    public static final String BASIC_DATA_IMPL_TYPE = "15";     // 实施方式

    // ===== 项目状态编码 =====
    public static final String PROJECT_CREATE_STATE30 = "30";   // 创建项目
    public static final String PROJECT_CREATE_STATE32 = "32";   // 指定PM
    public static final String PROJECT_CREATE_STATE34 = "34";   // 填写渠道
    public static final String PROJECT_CREATE_STATE36 = "36";   // 工程管理部待确认
    public static final String PROJECT_CREATE_STATE38 = "38";   // 服务经理待确认
    public static final String PROJECT_CREATE_STATE40 = "40";   // 不予跟踪
    public static final String PROJECT_CREATE_STATE42 = "42";   // 不予跟踪确认
    public static final String PROJECT_CREATE_STATE47 = "47";   // 计划中
    public static final String PROJECT_CREATE_STATE48 = "48";   // 已关闭
    public static final String PROJECT_CREATE_STATE50 = "50";   // 不予跟踪确认

    // ===== 闭环相关常量 =====
    public static final String CL_PROCESS_KEY = "CLProcess";
    public static final int CL_EVALU_TYPE_PM = 1;              // PM评估
    public static final int CL_EVALU_TYPE_SM = 2;              // SM评估
    public static final int CL_EVALU_TYPE_CB = 3;              // CB评估
    public static final int CL_EVALU_TYPE_CL = 4;              // CL评估
    public static final int CL_EVALU_TYPE_END = 5;             // 闭环结束
    public static final int CL_EVALU_RESULT_AGREE = 1;         // 同意
    public static final int CL_EVALU_RESULT_REJECT = 2;        // 驳回
    public static final int CL_EVALU_RESULT_CANTCB = 3;        // 无法闭环
    public static final int CL_STATUS_DRAFT = -1;              // 草稿
    public static final int CL_STATUS_SUBMIT = 1;              // 已提交/生效
    public static final int CL_STATUS_SUBMITQUES = 2;          // 问卷已提交
    public static final int CL_STATUS_ENDEFFEC = -2;           // 失效

    // ===== 问卷相关常量 =====
    public static final String CL_QUESNAIRE_HEADERID = "13";   // 问卷类型基础数据类型编码
    public static final String CL_QUESNAIRE_LINEID = "14";     // 问卷题目回访类型基础数据类型编码
    public static final String CL_QUESNAIRE_HEADER_TYPE = "30"; // 问卷类型:闭环建议类
    public static final String CL_QUESNAIRE_LINE_TYPE1 = "10";  // 工程项目类
    public static final String CL_QUESNAIRE_LINE_TYPE2 = "20";  // 设备类
    public static final String CL_QUESNAIRE_LINE_TYPE3 = "30";  // 工程师类
    public static final String CL_QUESNAIRE_LINE_TYPE4 = "40";  // 其他
    public static final String CL_QUESNAIRE_PROCESSID = "35";  // 问卷流程ID

    // ===== 成员角色 =====
    public static final String MEMBER_ROLE_SM = "20";          // 服务经理
    public static final String MEMBER_ROLE_PM = "30";          // 项目经理
    public static final String MEMBER_ROLE_SALES = "40";       // 销售
    public static final String MEMBER_ROLE_PARTNER = "50";     // 合作伙伴
    public static final String MEMBER_ROLE_CUSTOMER = "60";    // 最终客户

    // ===== 工作流 =====
    public static final String WORKFLOW_PROCESS_KEY = "PMSProcess";

    // ===== 通知模板 =====
    public static final String NOTIFY_PROJECT_CREATE = "PROJECT_CREATE";
    public static final String NOTIFY_PROJECT_BACK = "PROJECT_BACK";
    public static final String NOTIFY_PRESALES_APPLY = "PRESALES_APPLY";
    public static final String NOTIFY_CALLBACK_APPLY = "CALLBACK_APPLY";
    public static final String NOTIFY_CLOSEDLOOP_APPLY = "CLOSEDLOOP_APPLY";
    public static final String NOTIFY_SUBCONTRACT_APPLY = "SUBCONTRACT_APPLY";
}
