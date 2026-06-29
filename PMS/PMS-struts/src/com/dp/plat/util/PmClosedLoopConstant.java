package com.dp.plat.util;

public class PmClosedLoopConstant {
	/**
	 * 问卷模板编码开头字符串
	 */
	public static final String QUESNAIRE_TEMPLATE_NUM_HEADER="CH";
	
	
	/**
	 * 闭环流程定义key
	 */
	public static final String CL_PROCESS_KEY="PmClosedLoop";
	
	/**
	 * 闭环流程节点1的任务办理人的流程变量名称
	 */
	public static final String CL_TASK_USER_1="projectManager";
	
	/**
	 * 闭环流程节点2的任务办理人的流程变量名称
	 */
	public static final String CL_TASK_USER_2="serviceManager";
	
	/**
	 * 闭环流程节点3的任务办理人的流程变量名称  
	 */
	public static final String CL_TASK_USER_3="callBackPerson";
	
	/**
	 * 闭环流程节点4的任务办理人的流程变量名称
	 */
	public static final String CL_TASK_USER_4="projectManageEmp";
	
	/**
	 * 流程判断的流程变量名称
	 */
	public static final String CL_TASK_EVALU_1="evaluationResult";
	
	/**
	 * 项目闭环状态的流程变量名称
	 */
	public static final String CL_PROJECT_PROCESS_STATUS="projectProcessStatus";
	
	/**
	 * 生效状态
	 */
	public static final int CL_STATUS_SUBMIT=1;
	
	/**
	 * 失效状态
	 */
	public static final int CL_STATUS_ENDEFFEC=-2;
	
	/**
	 * 草稿状态
	 */
	public static final int CL_STATUS_DRAFT=-1;
	
	/**
	 * 提交问卷状态
	 */
	public static final int CL_STATUS_SUBMITQUES=2;
	
	
	/**
	 * 项目经理闭环申请
	 */
	public static final int CL_EVALU_TYPE_PM=1;
	
	/**
	 * 服务经理回访申请
	 */
	public static final int CL_EVALU_TYPE_SM=2;
	
	/**
	 * 回访人员回访
	 */
	public static final int CL_EVALU_TYPE_CB=3;
	
	/**
	 * 工程人员闭环
	 */
	public static final int CL_EVALU_TYPE_CL=4;
	
	/**
	 * 闭环结束
	 */
	public static final int CL_EVALU_TYPE_END=5;
	
	
	/**
	 * 测评结果同意
	 */
	public static final int CL_EVALU_RESULT_AGREE=1;
	
	/**
	 * 测评结果不同意
	 */
	public static final int CL_EVALU_RESULT_REJECT=-1;
	
	/**
	 * 测评结果无法回访
	 */
	public static final int CL_EVALU_RESULT_CANTCB=-3;
	
	/**
	 * 测评试卷类型：单选题
	 */
	public static final int CL_QUESNAIRE_LINE_TYPE_ONE=1;
	
	/**
	 * 测评试卷类型：多选
	 */
	public static final int CL_QUESNAIRE_LINE_TYPE_MORE=2;
	
	/**
	 * 测评试卷类型：问答
	 */
	public static final int CL_QUESNAIRE_LINE_TYPE_AQ=3;
	
	/**
	 * 测评试卷类型：评分
	 */
	public static final int CL_QUESNAIRE_LINE_TYPE_MARK=4;
	
	/**
	 * 问卷类型的Id
	 */
	public static final String CL_QUESNAIRE_HEADERID="13";
	
	/**
	 * 问卷题目回访类型的Id
	 */
	public static final String CL_QUESNAIRE_LINEID="14";
	
	/**
	 * 问卷类型:闭环建议类
	 */
	public static final String CL_QUESNAIRE_HEADER_TYPE="30";
	
	/**
	 * 测评问卷题目类型: 工程项目类
	 */
	public static final String CL_QUESNAIRE_LINE_TYPE1="10";
	
	/**
	 * 测评问卷题目类型: 设备类
	 */
	public static final String CL_QUESNAIRE_LINE_TYPE2="20";
	
	/**
	 * 测评问卷题目类型: 工程师类
	 */
	public static final String CL_QUESNAIRE_LINE_TYPE3="30";
	
	/**
	 * 测评问卷题目类型: 其他
	 */
	public static final String CL_QUESNAIRE_LINE_TYPE4="40";
	
	/**
	 * 项目闭环流程节点名称Id
	 */
	public static final String CL_QUESNAIRE_PROCESSID="17";

	/**
	 * 邮件模板-服务经理驳回
	 */
	public static final String CL_MAIL_TEMPLATE_01="20";
	
	/**
	 * 邮件模板-回访通过
	 */
	public static final String CL_MAIL_TEMPLATE_02="21";
	
	/**
	 * 邮件模板-回访不通过
	 */
	public static final String CL_MAIL_TEMPLATE_03="22";
	
	/**
	 * 邮件模板-同意闭环
	 */
	public static final String CL_MAIL_TEMPLATE_04="23";
	
	/**
	 * 邮件模板-不同意闭环
	 */
	public static final String CL_MAIL_TEMPLATE_05="24";
	
	/**
	 * 邮件模板-无法回访
	 */
	public static final String CL_MAIL_TEMPLATE_06="25";
	
	/**
	 * 邮件模板-服务经理同意闭环
	 */
	public static final String CL_MAIL_TEMPLATE_07="26";
	
	/**
	 * 邮件模板-项目经理发起闭环申请
	 */
	public static final String CL_MAIL_TEMPLATE_08="28";
	
	public static final String DP_MAIL="dptech.com";
	
	
}
