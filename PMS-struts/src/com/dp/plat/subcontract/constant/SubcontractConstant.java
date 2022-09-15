/**
 * 
 */
package com.dp.plat.subcontract.constant;

/**
 * 项目转包常数类
 * 
 * @author w02611
 *
 */
public class SubcontractConstant {

	/**
	 * 项目转包审批测评类别
	 */
	public static final int EVALU_TYPE = 10;

	/**
	 * 工程管理部主管审批
	 */
	public static final int EVALU_TYPE_ENG_LEADER = 11;

	/**
	 * 工程管理部主管审批
	 */
	public static final String TEXT_ENG_LEADER = "工程管理部主管审批";

	/**
	 * 项目转包申请提交状态
	 */
	public static final int STATUS_SUBMIT = 1;

	/**
	 * 项目转包申请审批状态
	 */
	public static final int STATUS_AUDIT = 2;

	/**
	 * 测评结果同意
	 */
	public static final int EVALU_RESULT_AGREE = 1;

	/**
	 * 测评结果不同意
	 */
	public static final int EVALU_RESULT_REJECT = -1;

	/**
	 * 流程服务经理变量名
	 */
	public static final String TASK_USER_SERVICE = "serviceManager";

	/**
	 * 受益部门服务经理变量名
	 */
	public static final String TASK_PROFIT_SERVICEMANAGER = "profitServiceManager";

	/**
	 * 工程管理部主管流程变名
	 */
	public static final String TASK_USER_ENGINEEMANAGER_LEADER = "engManagerLeader";

	/**
	 * 工程管理部人员流程变名
	 */
	public static final String TASK_USER_ENGINEEMANAGER_EMP = "engManagerEmp";

	/**
	 * 办事处主任流程变名
	 */
	public static final String TASK_USER_AREA_LEADER = "areaLeader";

	/**
	 * 项目转包流程key
	 */
	public static final String PROCESS_SUBCONTRACT_KEY = "Subcontract";

	/**
	 * 回访流程KEY
	 */
	public static final String PROCESS_CALLBACK_KEY = "SubcontractCallBack";
	
	/**
     * 转包验收流程KEY
     */
    public static final String PROCESS_INSPECTION_KEY = "SubcontractInspection";

	/**
	 * 审批任务KEY
	 */
	public static final String TASK_KEY_APPROVE = "approveTask";

	/**
	 * 主任审批任务KEY
	 */
	public static final String TASK_KEY_ZR_APPROVE = "approveZRTask";

	/**
	 * 闭环任务KEY
	 */
	public static final String TASK_KEY_CLOSE = "closeTask";

	/**
	 * 生成合同任务KEY
	 */
	public static final String TASK_KEY_GENERATE_CONTRACT = "generateContractTask";

	/**
	 * 服务经理提交付款信息任务KEY
	 */
	public static final String TASK_KEY_APPLY_PAYMENT = "applyPaymentTask";

	/**
	 * 回访任务KEY
	 */
	public static final String TASK_KEY_CALLBACK = "callbackTask";

	/**
	 * 付款任务KEY
	 */
	public static final String TASK_KEY_APPROVE_PAYMENT = "approvePaymentTask";
	/**
	 * 转包项目类型KEY
	 */
	public static final String SUBCONTRACT_TYPE_KEY = "subcontractType";
	/**
	 * 转包状态KEY
	 */
	public static final String SUBCONTRACT_STATE_KEY = "subcontractState";
	/**
	 * 转包回访状态KEY
	 */
	public static final String SUBCONTRACT_CALLBACK_STATE_KEY = "subcontractCbState";
	/**
	 * 转包交付件类型KEY
	 */
	public static final String SUBCONTRACT_DELIVER_TYPE_KEY = "subcontractDeliverState";
	/**
     * 转包税率KEY
     */
    public static final String SUBCONTRACT_TAX_KEY = "subcontractTax";

	/**
	 * 流程任务TaskKey
	 * 
	 * @author w02611
	 *
	 */
	public final class TaskKey {
	    /**
         * 服务经理发起转包申请任务KEY
         */
        public static final String START_SUBCONTRACT = "serviceTask";
		/**
		 * 受益部门服务经理审批任务KEY
		 */
		public static final String PROFIT_SERVICE_APPROVE = "profitServiceTask";
		/**
		 * 审批任务KEY
		 */
		public static final String APPROVE = "approveTask";

		/**
		 * 主任审批任务KEY
		 */
		public static final String ZR_APPROVE = "approveZRTask";

		/**
		 * 闭环任务KEY
		 */
		public static final String CLOSE = "closeTask";

		/**
		 * 生成合同任务KEY
		 */
		public static final String GENERATE_CONTRACT = "generateContractTask";

		/**
		 * 服务经理提交付款信息任务KEY
		 */
		public static final String APPLY_PAYMENT = "applyPaymentTask";

		/**
		 * 回访任务KEY
		 */
		public static final String CALLBACK = "callbackTask";

		/**
		 * 付款任务KEY
		 */
		public static final String APPROVE_PAYMENT = "approvePaymentTask";
		
		/**
		 * 验收确认Key
		 */
		public static final String ACCEPTANCE_TASK = "acceptanceTask";
	}

	/**
	 * 自定义审批记录状态值
	 * 
	 * @author w02611
	 *
	 */
	public final class CommentStatus {
		/**
		 * 申请
		 */
		public static final int APPLY = 0;
		/**
		 * 审批通过
		 */
		public static final int AGREE = 1;
		/**
		 * 驳回
		 */
		public static final int REJECT = -1;
		/**
		 * 可以闭环
		 */
		public static final int CLOSE_ABLE = 2;
		/**
		 * 无法闭环
		 */
		public static final int CLOSE_DISABLE = -2;
		/**
		 * 回访通过
		 */
		public static final int CALLBACK_PASS = 3;
		/**
		 * 无法回访
		 */
		public static final int CALLBACK_DISABLE = 4;
		/**
		 * 回访不通过
		 */
		public static final int CALLBACK_REJECT = -3;
		/**
		 * 付款完成，余款未清
		 */
		public static final int PAYMENT = 5;
	}

	/**
	 * 转包状态值
	 * 
	 * @author w02611
	 *
	 */
	public final class SubcontractStatus {
		/**
		 * 草稿
		 */
		public static final int DRAFT = 0;
		/**
		 * 待审批
		 */
		public static final int APPLY = 10;
		/**
		 * 受益部门服务经理审批
		 */
		public static final int PROFIT_SM_AGREE = 15;
		/**
		 * 受益部门服务经理驳回
		 */
		public static final int PROFIT_SM_REJECT = -15;
		/**
		 * 工程管理部审批通过
		 */
		public static final int ENG_AGREE = 20;
		/**
		 * 工程管理部驳回
		 */
		public static final int ENG_REJECT = -20;
		/**
		 * 办事处主任审批通过
		 */
		public static final int AREA_AGREE = 30;
		/**
		 * 办事处主任驳回
		 */
		public static final int AREA_REJECT = -30;
		/**
		 * 合同执行中
		 */
		public static final int EXECUTING = 40;
		/**
		 * 已闭环
		 */
		public static final int CLOSED = 100;
		/**
		 * 闭环驳回
		 */
		public static final int CLOSE_REJECT = -100;
	}

	public final class SubcontractType {
		/**
		 * 工程实施类
		 */
		public static final int ENG_ACTUALIZE = 10;

		/**
		 * 驻场类
		 */
		public static final int ON_SITE = 20;

		/**
		 * 维护类
		 */
		public static final int MAINTENANCE = 30;
	}

	/**
	 * 转包回访状态值
	 * 
	 * @author w02611
	 *
	 */
	public final class SubcontractCallbackStatus {
		/**
		 * 回访通过
		 */
		public static final int CALLBACK_PASS = 10;
		/**
		 * 无法回访
		 */
		public static final int CALLBACK_DISABLE = 20;
		/**
		 * 回访不通过
		 */
		public static final int CALLBACK_REJECT = -10;

	}

	public final class SubcontractTemplate {
		/**
		 * 项目转包邮件抄送人
		 */
		public static final String SUBCONTRACT_CCS_MAIL = "subcontract.css.mail";
		/**
		 * 付款人员,姓名/邮箱
		 */
		public static final String PAYMENT_USER = "subcontract.payment.user";
		/**
		 * 主任审批工程服务费的部门
		 */
		public static final String AREA_LEADER_AUDIT_ENGINEE_FEE_OFFICES = "subcontract.areaLeader.auditEngineeFee.offices";
		/**
		 * 转包服务商下单通知邮件
		 */
		public static final String FACILITATOR_NOTIFY_CODE = "notifyFacilitator";

		/**
		 * 项目转包审批邮件
		 */
		public static final String APPROVE_NOTIFY_CODE = "subcontractApprove";

		/**
		 * 审批通过邮件
		 */
		public static final String APPROVE_PASS_NOTIFY_CODE = "subcontractApprovePass";
		/**
		 * 审批驳回邮件
		 */
		public static final String APPROVE_REJECT_NOTIFY_CODE = "subcontractApproveReject";
		/**
		 * 生成合同号邮件
		 */
		public static final String GEN_CONTRACT_NOTIFY_CODE = "subcontractGenContract";
		/**
		 * 下一步付款邮件提醒
		 */
		public static final String PAYMENT_NEXT_NOTIFY_CODE = "subcontractNextPayment";
		/**
		 * 付款申请邮件
		 */
		public static final String PAYMENT_APPLY_NOTIFY_CODE = "subcontractPaymentApply";
		/**
		 * 部分付款结束邮件
		 */
		public static final String PAYMENT_FINISH_NOTIFY_CODE = "subcontractPaymentFinish";
		/**
		 * 项目闭环邮件
		 */
		public static final String CLOSE_NOTIFY_CODE = "subcontractClose";
		/**
		 * 回访不通过邮件
		 */
		public static final String CALLBACK_REJECT_NOTIFY_CODE = "subcontractCallbackReject";
	}
}
