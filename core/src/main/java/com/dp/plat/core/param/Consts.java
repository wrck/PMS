package com.dp.plat.core.param;

public class Consts {
	public final static String VIEW_UNAUTHORIZED = "unauthorized";
	
	public final static class NodeType {
		/**
		 * 1 已完成
		 */
		public final static int FINISHED = 1;
		/**
		 * -1 未完成
		 */
		public final static int UNFINISHED = -1;
		/**
		 * 0 部分完成
		 */
		public final static int HALFDONE = 0;
	}

	public final static class CollectionType {
		/**
		 * 1 已全部收款
		 */
		public final static int FINISHED = 1;
		/**
		 * 2 已部分收款
		 */
		public final static int UNFINISHED = 2;
		/**
		 * 3 未收到款
		 */
		public final static int ZERO = 3;
		/**
		 * 4 非收款节点
		 */
		public final static int UNNECESSARY = 4;
	}

	public final static class CompleteFlag {
		/**
		 * 1 已完成
		 */
		public final static int COMPLETE = 1;
		/**
		 * 2 部分完成
		 */
		public final static int UNCOMPLETE = 2;
	}

	public final static class NodeTypeCode {

		/**
		 * 10 订单申报
		 */
		public final static int ORDER_APPLY = 10;
		/**
		 * 20 合同签订
		 */
		public final static int CONTRACT_SIGNING = 20;
		/**
		 * 30 备货
		 */
		public final static int PREPARE_GOODS = 30;
		/**
		 * 40 发货
		 */
		public final static int SHIPMENT = 40;
		/**
		 * 50 到货验收
		 */
		public final static int AOGs_ACCEPTANCE = 50;
		/**
		 * 60 安装调试
		 */
		public final static int INSTALL_AND_DEBUG = 60;
		/**
		 * 70 初验
		 */
		public final static int FIRST_EXAMINE = 70;
		/**
		 * 80 终验
		 */
		public final static int FINAL_EXAMINE = 80;

	}

	public static class URLPath {
		/**
		 * 管理员角色URL
		 */
		public final static String SYSTEM_MANAGER = "/sys/";

		/**
		 * 工作流URL
		 */
		public final static String WORKFLOW_MANAGER = "/workflow/";
		
		/**
		 * 业务模块
		 */
		public final static String MODULE_MANAGER = "/module";
	}
}
