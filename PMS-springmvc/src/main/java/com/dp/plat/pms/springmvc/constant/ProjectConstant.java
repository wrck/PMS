package com.dp.plat.pms.springmvc.constant;

import com.dp.plat.core.param.Consts;
import com.dp.plat.util.MessageUtil;

public class ProjectConstant {

	public static class URLPath extends Consts.URLPath {
		/**
		 * 项目管理模块
		 */
		public final static String PROJECT_MANAGER = "/pm/";
		
		/**
		 * 安服管理
		 */
		public final static String AF_MANAGER = "/af/";
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

	public static final class ProcessType {
		/**
		 * 质量审批跟踪流程
		 */
		public final static String QUALITY_APPROVE_TRACK = "QualityApproveTrack";
		
		public static final class TaskType {
			public static final String AF_APPROVE_TASK = "afApproveTask";
			
			public static final String YF_APPROVE_TASK = "yfApproveTask";
			
			public static final String TRACK_TASK = "trackTask";

			public static final String END = "end";
			
			public static final String REJECT = "reject";
		}
		
		public static final class DataType {
			
			public static final String PROJECT = "project";
			
			public final static String PROJECT_TASK = "projectTask";
			
			public final static String INDUSTRY_ASSET = "industryAsset";
			
			public final static String INDUSTRY_LEAK = "industryLeak";
			
			public final static String PROJECT_OPPORTUNITY = "projectOpportunity";

		}
	}

	public static class MemberRole {
		/**
		 * 项目相关人角色 销售人员
		 */
		public static final String MEMBER_SALESMAN = "10";
		/**
		 * 项目相关人角色 服务经理
		 */
		public static final String MEMBER_SM = "20";
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
		 * 项目相关人角色质量监督员
		 */
		public static final String MEMBER_QC = "80";
	}
}
