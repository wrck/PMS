package com.dp.plat.pms.springmvc.constant;

import com.dp.plat.core.param.Consts;

public class ProjectConstant {

	public static class URLPath extends Consts.URLPath {
		/**
		 * 项目管理模块
		 */
		public final static String PROJECT_MANAGER = "/pm/";
	}

	public static class ProjectType {
		/**
		 * 安服订单项目
		 */
		public final static String AF_SALES_PROJECT	 = "afss";
		
		/**
		 * 安服先行项目
		 */
		public final static String AF_XX_PROJECT	 = "afxx";
		
		/**
		 * 用服售后项目
		 */
		public final static String JF_SALES_PROJECT	 = "10";
		
		/**
		 * 用服售前测试
		 */
		public final static String JF_TEST_PROJECT	 = "20";
		
	}

	public static class ProjectState {
		/**
		 * 待创建
		 */
		public final static String UNCREATED	 = "10";
		
	}

	public static class DispatchNOPrefix {
		/**
		 * 安服项目外派合同
		 */
		public final static String AF = "SS";
	}
	
	public static class DispatchType {
		public final static String FRAMEWORK_AGREEMENT = "frameworkAgreement";
		public final static String THIRD_PARTY_SERVICES = "thirdPartyServices";
	}
}
