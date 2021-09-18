package com.dp.plat.util;

/**
 * 记录所有hard code的信息
* @ClassName: MessageUtil 
* @Description: (这里用一句话描述这个类的作用) 
* @author dp 
* @date 2015年5月23日 下午2:02:09 
*
 */
public class MessageUtil {
	
	public static final int ERR_CODE = 2;
	public static final int SUCC_CODE = 1;
	public static final String SAVE_FAILED = "保存失败"; 
	public static final String SAVE_SUCCESS = "保存成功";
	public static final String PROJECT_GROUPCODE_PRE = "prj_gp";//项目组编码前缀
	public static String DATATYPE_CODE03_10 = "10";
	public static String DATATYPE_CODE03_20 = "20";
	public static String DATATYPE_CODE03_30 = "30";
	public static String DATATYPE_CODE07_10 = "10";
	public static String DATATYPE_CODE07_20 = "20";
	public static String DATATYPE_CODE07_30 = "30";
	public static String PROJECT_TYPE_NORMAL = "10";
	public static String PROJECT_TYPE_ENGINEE = "20";
	/**
	 * 售后项目类型
	 */
	public static final String PROJECT_TYPE_AFTERSALES = "10";
	/**
	 * 售前测试项目类型
	 */
	public static final String PROJECT_TYPE_PRESALES = "20";
	public static String MAIL_SEPARATOR = ";";//邮件分隔符
	/**
	 * 工程管理部
	 */
	public static String GCGLB = "gongcheng.mail";
	
	/**
	 * 工程管理部 待创建项目
	 */
	public static final String PROJECT_STATE_CREATING = "10";
	/**
	 * 工程管理部不予跟踪项目状态
	 */
	public static final String PROJECT_STATE_DENY = "20";
	/**
	 * ，待指定服务经理状态
	 */
	public static final String PROJECT_STATE_30 = "30";//
	
	/**
	 * 待指派项目经理
	 */
	public static final String PROJECT_STATE_31 = "31";//
	/**
	 * 待制定工程计划,项目经理跟踪状态
	 */
	public static final String PROJECT_STATE_32 = "32";//
	/**
	 * 售前项目，回访阶段
	 */
	public static final String PROJECT_STATE_33 = "33";//
	/**
	 * 尚未制定计划
	 */
	public static final String PROJECT_PLAN_STATE_40 = "40";//
	/**
	 * 工程启动会
	 */
	public static final String PROJECT_PLAN_STATE_41 = "41";//
	/**
	 * 工程准备
	 */
	public static final String PROJECT_PLAN_STATE_42 = "42";//
	/**
	 * 到货验收
	 */
	public static final String PROJECT_PLAN_STATE_43 = "43";//
	/**
	 * 安装调试
	 */
	public static final String PROJECT_PLAN_STATE_44 = "44";//
	/**
	 * 初验
	 */
	public static final String PROJECT_PLAN_STATE_45 = "45";//
	/**
	 * 终验
	 */
	public static final String PROJECT_PLAN_STATE_46 = "46";//
	/**
	 * 闭环申请
	 */
	public static final String PROJECT_PLAN_STATE_47 = "47";//
	/**
	 * 项目闭环
	 */
	public static final String PROJECT_PLAN_STATE_48 = "48";//
	
	/**
	 * 工程管理部创建项目，待指定服务经理状态 isback
	 */
	public static final String PROJECT_CREATE_STATE30 = "30";//
	/**
	 * 服务经理指定项目经理状态 isback
	 */
	public static final String PROJECT_CREATE_STATE32 = "32";//
	/**
	 * 项目经理填写项目信息状态 isback
	 */
	public static final String PROJECT_CREATE_STATE34 = "34";//
	
	/**

	 * 项目已关闭状态
	 */
	public static final String PROJECT_STATE_CLOSEDLOOP="100";
	/**

	 * 需工程管理部同意回退状态
	 */
	public static final String PROJECT_CREATE_STATE36 = "36";//
	/**
	 * 需服务经理同意回退状态
	 */
	public static final String PROJECT_CREATE_STATE38 = "38";//
	/**
	 * 40表示工程管理部不予跟踪处理
	 */
	public static final String PROJECT_CREATE_STATE40 = "40";//
	/**
	 * 42 表示项目经理选择不予跟踪 
	 */
	public static final String PROJECT_CREATE_STATE42 = "42";//
	/**
	 * 50 服务经理将不与跟踪的项目返回工程管理部,说明需要跟踪
	 */
	public static final String PROJECT_CREATE_STATE50 = "50";
	/**
     * 项目实施状态-项目闭环
     */
    public static final String PROJECT_EXECUTION_STATE_80 = "80";
    /**
     * 项目实施状态-项目闭环
     */
    public static final String PROJECT_EXECUTION_STATE_80_NAME = "项目闭环";
	/**
	 * 项目闭环状态-项目跟踪
	 */
	public static final String PROJECT_CLOSE_PROCESS_STATE_10 = "10";
	/**
     * 项目闭环状态-闭环申请
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_15 = "15";
    /**
     * 项目闭环状态-服务经理审批
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_20 = "20";
    /**
     * 项目闭环状态-回访
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_30 = "30";
    /**
     * 项目闭环状态-工程人员审核
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_40 = "40";
    /**
     * 项目闭环状态-项目闭环
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_50 = "50";
    /**
     * 项目闭环状态-项目跟踪
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_10_NAME = "项目跟踪";
    /**
     * 项目闭环状态-闭环申请
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_15_NAME = "闭环申请";
    /**
     * 项目闭环状态-服务经理审批
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_20_NAME = "服务经理审批";
    /**
     * 项目闭环状态-回访
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_30_NAME = "回访";
    /**
     * 项目闭环状态-工程人员审核
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_40_NAME = "工程人员审核";
    /**
     * 项目闭环状态-项目闭环
     */
    public static final String PROJECT_CLOSE_PROCESS_STATE_50_NAME = "项目闭环";
	
	/**
	 * 角色id为1，表示管理员
	 */
	public static final int ROLE_ADMIN = 1;
	/**
	 * 角色为3，表示普通用户
	 */
	public static final int ROLE_COMMON = 3;//
	/**
	 * 角色为9，表示办事处主任
	 */
	public static final int ROLE_AREA_LEADER = 9;//
	/**
	 * 角色为10，表示工程管理部主管
	 */
	public static final int ROLE_ENGINEEMANAGER_LEADER = 10;//
	/**
	 * 角色为11，表示服务经理
	 */
	public static final int ROLE_SERVICEMANAGER = 11;//
	/**
	 * 角色为12，表示项目经理
	 */
	public static final int ROLE_PROGRAMMANAGER = 12;//
	/**
	 * 角色为13，表示工程管理部
	 */
	public static final int ROLE_ENGINEEMANAGER = 13;//
	/**
	 * 角色为14，表示回访人员
	 */
	public static final int ROLE_CALLBACKPER = 14;//
	/**
	 * 角色为15，表示销售代表
	 */
	public static final int ROLE_SALESPEOPLE = 15;
	/**
	 * 角色表示为16 ，表示财务人员
	 */
	public static final int ROLE_FINANCIAL_STAFF = 16;
	/**
	 * 角色表示为17 ，表示售前专员
	 */
	public static final int ROLE_PRESALES_STAFF = 17;
	/**
	 * 角色表示为18，表示技术公告管理员
	 */
	public static final int ROLE_PROB_ADMIN = 18;
	/**
	 * 角色表示为19，表示技术支持人员
	 */
	public static final int ROLE_PROB_SUPPORTER = 19;
	/**
	 * 角色表示为20，表示研发人员
	 */
	public static final int ROLE_PROB_RD = 20;
	/**
	 * 角色为21，表示维保回访人员，与普通项目回访人员角色区分开
	 */
	public static final int ROLE_WARRANTY_CALLBACKER = 21;
	/**
	 * 批示类型 0批示 1 回复 
	 */
	public static final int INSTRUSTION = 0;
	/**
	 * 批示类型 0批示 1 回复 
	 */
	public static final int FEEDBACK = 1;
	
	/**
	 * 周报类容类型  work
	 */
	public static final int OPTION_TYPE_WORK = 1;
	/**
	 * 周报类容类型  risk
	 */
	public static final int OPTION_TYPE_RISK = 2;
	/**
	 * 周报类容类型  help
	 */
	public static final int OPTION_TYPE_HELP = 3;
	/**
	 * 周报类容类型  progress
	 */
	public static final int OPTION_TYPE_PROPGRESS = 4;
	/**
	 * 周报类容类型  plan
	 */
	public static final int OPTION_TYPE_PLAN = 5;
	/**
	 * 周报类容类型  file
	 */
	public static final int OPTION_TYPE_FILE = 6;
	/**
	 * 周报类容类型  mail
	 */
	public static final int OPTION_TYPE_MAIL = 7;
	/**
	 * 周报状态 0草稿 1已提交 -1 全部
	 */
	public static final int WEEKLY_STATE_RAFT = 0;
	/**
	 * 周报状态 0草稿 1已提交 -1 全部
	 */
	public static final int WEEKLY_STATE_SUBMIT = 1;
	/**
	 * 周报状态 0草稿 1已提交 -1 全部
	 */
	public static final int WEEKLY_STATE_ALL = -1;
	/**
	 * 基础数据类型 03项目成员角色
	 */
	public static final String BASIC_DATA_MEMBER_ROLE = "03";
	/**
	 * 基础数据类型 10项目维护界面选项卡
	 */
	public static final String BASIC_DATA_NAV_TAB = "10";
	/**
	 * 基础数据类型 12工作台页面选项卡
	 */
	public static final String BASIC_DATA_NAV_WORK_TAB = "12";
	
	/**
	 * 基础数据类型 16拆分页面选项卡
	 */
	public static final String BASIC_DATA_NAV_MERGE_TAB = "16";
	
	/**
	 * 基础数据类型 18数据统计界面选项卡
	 */
	public static final String BASIC_DATA_NAV_DATA_TAB = "18";
	
	/**
	 * 基础数据类型  09项目阶段划分
	 */
	public static final String BASIC_DATA_PRJ_PHASE = "09";
	/**
	 * 基础数据类型  15项目实施方式划分
	 */
	public static final String BASIC_DATA_SERVICE_TYPE = "15";
	/**
	 * 基础数据类型 06项目类别
	 */
	public static final String BASIC_DATA_PROTYPE="06";
	/**
	 * 基础数据类型 06项目类型
	 */
	public static final String BASIC_DATA_PRORANK="05";
	/**
	 * 基础数据类型 订单发货状态
	 */
	public static final String BASIC_DATA_DELIVERSTATE = "20";
	/**
	 * 基础数据类型 项目工程状态
	 */
	public static final String BASIC_DATA_ENGINEERSTATE = "22";
	/**
	 * 基础数据类型 项目查询条件--时间点集合
	 */
	public static final String BASIC_DATA_PORJECT_TIME = "24";
	/**
	 * 系统项目分类
	 */
	public static final String BASIC_DATA_PROJECT_TYPE = "29";
	/**
	 * 项目转包分类
	 */
	public static final String BASIC_DATA_SUBCONTRACT_TYPE = "subcontractType";
	/**
	 * 项目转包状态
	 */
	public static final String BASIC_DATA_SUBCONTRACT_STATE = "subcontractState";
	/**
	 * 项目转包交付件类型
	 */
	public static final String BASIC_DATA_SUBCONTRACT_DELIVER_STATE = "subcontractDeliverState";
	/**
	 * 项目转包审批状态
	 */
	public static final String BASIC_DATA_SUBCONTRACT_WORKFLOW_STATE = "subcontractWorkFlowState";
	
	public static final String FLAG_FROM_PROJECT = "1";//成员信息来源，1表示来源于项目信息
	
	public static final String FLAG_FROM_MEMBER = "2";//成员信息来源，2表示来源于成员信息
	/**
	 * 邮件或消息格式模板 周报提交邮件模板 01
	 */
	public static final String NOTIFICATION_CODE_WEEKLY_SUBMIT = "01";
	/**
	 * 邮件或消息格式模板 周报批复邮件模板 02
	 */
	public static final String NOTIFICATION_CODE_WEEKLY_PISHI = "02";
	/**
	 * 邮件或消息格式模板 项目留言邮件模板 03
	 */
	public static final String NOTIFICATION_CODE_INSTRUCTION = "03";
	/**
	 * 邮件或消息格式模板 项目不予跟踪邮件模板 04
	 */
	public static final String NOTIFICATION_CODE_DENY_PRJ = "04";
	
	/**
	 * 邮件或消息格式模板 项目继续跟踪邮件模板 05
	 */
	public static final String NOTIFICATION_CODE_CONTINUE_PRJ = "05";
	/**
	 * 邮件或消息格式模板 项目确认继续跟踪
	 */
	public static final String NOTIFICATION_CODE_SURE_PRJ = "06";
	/**
	 * 邮件或消息格式模板 项目经理选择不予跟踪邮件模板 
	 */
	public static final String NOTIFICATION_CODE_DENY_PRJ_42 = "07";
	
	/**
	 * 邮件或消息格式模板 工程管理确认不予跟踪邮件模板 
	 */
	public static final String NOTIFICATION_CODE_DENY_PRJ_SURE = "08";
	/**
	 * 邮件或消息格式模板 项目立项通知-普通类09
	 */
	public static final String NOTIFICATION_CODE_CREATEPRJ_NORMAL = "09";
	
	/**
	 * 邮件或消息格式模板 项目立项通知-工程类10
	 */
	public static final String NOTIFICATION_CODE_CREATEPRJ_ENGINEE = "10";
	/**
	 * 邮件或消息格式模板 项目经理任命通知-普通类11
	 */
	public static final String NOTIFICATION_CODE_PMNOMINATE_NORMAL = "11";
	/**
	 * 邮件或消息格式模板 项目经理任命通知-工程类12
	 */
	public static final String NOTIFICATION_CODE_PMNOMINATE_ENGINEE = "12";
	/**
	 * 邮件或消息格式模板 项目组成立13
	 */
	public static final String NOTIFICATION_CODE_PROJECT_VALIDATE = "13";
	/**
	 * 回退邮件模版14
	 */
	public static final String NOTIFICATION_CODE_PROJECT_BACK = "14";
	/**
	 * 邮件通知模板，项目计划到期提醒
	 */
	public static final String NOTIFICATION_CODE_PROJECT_EXPIRATION_TIP = "29";	
	/**
	 * 邮件通知模板，项目计划上传交付件提醒
	 */
	public static final String NOTIFICATION_CODE_PROJECT_UPLOAD_DELIVER = "30";	
	/**
	 * 技术公告邮件模板
	 */	
	public static final String NOTIFICATION_CODE_PROB = "50";
	/**
	 * 项目相关人角色 服务经理
	 */
	public static final String MEMBER_SM = "20";
	/**
	 * 项目相关人角色 销售人员
	 */
	public static final String MEMBER_SALESMAN = "10";
	/**
	 * 项目相关人角色 项目经理
	 */
	public static final String MEMBER_PM = "30";
	/**
	 * 项目相关人角色 团队成员
	 */
	public static final String MEMBER_PARTY = "40";
	/**
	 * 项目相关人角色 出货代理商/服务渠道工程师
	 */
	public static final String MEMBER_SERVICE_CHANNEL = "50";
	/**
	 * 项目相关人角色 最终客户
	 */
	public static final String MEMBER_CUSTOMER = "60";
	
	/**
	 * 项目相关人角色 技术经理
	 */
	public static final String MEMBER_TECH_MANMER = "70";
	/**
	 * 项目通知模板 项目创建
	 */
	public static final String NOTIFICATION_CODE_101 = "101";
	/**
	 * 项目通知模板 不予跟踪
	 */
	public static final String NOTIFICATION_CODE_102 = "102";
	/**
	 * 项目通知模板 项目经理选择不予跟踪
	 */
	public static final String NOTIFICATION_CODE_109 = "109";
	
	/**
	 * 项目通知模板 项目管理部确认不予跟踪
	 */
	public static final String NOTIFICATION_CODE_110 = "110";
	/**
	 * 项目通知模板 指定服务经理
	 */
	public static final String NOTIFICATION_CODE_103 = "103";
	/**
	 * 项目通知模板 指定项目经理
	 */
	public static final String NOTIFICATION_CODE_104 = "104";
	/**
	 * 项目通知模板 需要跟踪
	 */
	public static final String NOTIFICATION_CODE_105 = "105";
	/**
	 * 项目通知模板 确认跟踪
	 */
	public static final String NOTIFICATION_CODE_106 = "106";
	/**
	 * 项目回退 
	 */
	public static final String NOTIFICATION_CODE_107 = "107";
	/**
	 * 同意回退 
	 */
	public static final String NOTIFICATION_CODE_108 = "108";
	/**
	 * 上传交付件
	 */
	public static final String NOTIFICATION_CODE_111 = "111";
	/**
	 * 制定工程计划
	 */
	public static final String NOTIFICATION_CODE_112 = "112";
	/**
	 * 增加项目干系人
	 */
	public static final String NOTIFICATION_CODE_113 = "113";
	/**
	 * 增加设备安装地址
	 */
	public static final String NOTIFICATION_CODE_114 = "114";
	/**
	 * 修改工程计划
	 */
	public static final String NOTIFICATION_CODE_115 = "115";
	/**
	 * 修改项目干系人
	 */
	public static final String NOTIFICATION_CODE_116 = "116";
	/**
	 * 删除工程交付件
	 */
	public static final String NOTIFICATION_CODE_117 = "117";
	/**
	 * 提交工程周报
	 */
	public static final String NOTIFICATION_CODE_118 = "118";
	/**
	 * 项目闭环
	 */
	public static final String NOTIFICATION_CODE_119 = "119";
	/**
	 * 项目设备转移
	 */
	public static final String NOTIFICATION_CODE_120 = "120";
	/**
	 * 计划可见标识
	 */
	public static final String TASK_SHOW = "1";
	/**
	 * 计划不可见标识
	 */
	public static final String TASK_HIDE = "2";
	/**
	 * 项目实施方式所有
	 */
	public static final int IMPL_WAY_ALL = -1;
	/**
	 * 项目实施方式---原厂直服
	 */
	public static final int IMPL_WAY_0 = 0;
	/**
	 * 项目实施方式---原厂督导
	 */
	public static final int IMPL_WAY_1 = 1;
	/**
	 * 项目实施方式---代理商自服
	 */
	public static final int IMPL_WAY_3 = 3;
	
}
