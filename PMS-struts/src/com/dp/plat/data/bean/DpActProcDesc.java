package com.dp.plat.data.bean;

import java.util.Date;

/**
 * 
 * @title: 流程描述
 * @desc: 功能描述
 * @connection: 对应 dp_act_proc_desc 表的 ID
 * @author: xumaocai
 * @date: 2014年7月12日下午5:05:46
 */
public class DpActProcDesc {
	private int id;
	private String procInstId; //流程实例ID 
	private int procType;      //流程类型 @dp_act_proc_type.id
	private String procTypeName;//流程类型名称 @dp_act_proc_type.desc
	private String procTypeDesc;//流程类型描述
	private String projectCode;//项目编码 @project.projectCode
	private String projectName;//项目名称
	private String projectPlanStateName;//项目阶段
	private String projectCustomer;//客户名称
	private String projectImpl;//项目实施方式
	private String officeName;//办事处
	private int planDiffTime;//计划偏离时间
	private String username;   // 申请人 @user.username
	private String realName;  //申请人姓名
	private int applyNum;   //来自业务表单ID  @*.id
	private String name;     //任务名称
	private Date createTime;//创建时间
	private String assignee;//办理人
	private String taskId;//任务ID
	private String assigneeName;
	private String showflag;//显示产品配置商务信息等按钮
	private int evaluaResult;	//审核结果
	
	private Date startTime;//开始时间 @act_hi_taskinst
	private Date endTime;//结束时间 @act_hi_taskinst
	private String procdefKey;//流程定义key
	private String cause;//委派任务 @dp_act_procdef_delegate.cause
	
	private int isCandidateUser;//候选用户 @ 1 = 是

	private int cansee;
	
	public int getIsCandidateUser() {
		return isCandidateUser;
	}
	public void setIsCandidateUser(int isCandidateUser) {
		this.isCandidateUser = isCandidateUser;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public String getProcdefKey() {
		return procdefKey;
	}
	public void setProcdefKey(String procdefKey) {
		this.procdefKey = procdefKey;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getProcTypeName() {
		return procTypeName;
	}
	public void setProcTypeName(String procTypeName) {
		this.procTypeName = procTypeName;
	}
	public int getId() {
		return id;
	}
	public int getCansee() {
		return cansee;
	}
	public void setCansee(int cansee) {
		this.cansee = cansee;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	public int getProcType() {
		return procType;
	}
	public void setProcType(int procType) {
		this.procType = procType;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getApplyNum() {
		return applyNum;
	}
	public void setApplyNum(int applyNum) {
		this.applyNum = applyNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getAssigneeName() {
		return assigneeName;
	}
	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}
	public String getShowflag() {
		return showflag;
	}
	public void setShowflag(String showflag) {
		this.showflag = showflag;
	}
	public int getEvaluaResult() {
		return evaluaResult;
	}
	public void setEvaluaResult(int evaluaResult) {
		this.evaluaResult = evaluaResult;
	}
	public String getProjectPlanStateName() {
		return projectPlanStateName;
	}
	public void setProjectPlanStateName(String projectPlanStateName) {
		this.projectPlanStateName = projectPlanStateName;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public int getPlanDiffTime() {
		return planDiffTime;
	}
	public void setPlanDiffTime(int planDiffTime) {
		this.planDiffTime = planDiffTime;
	}
	public String getProcTypeDesc() {
		return procTypeDesc;
	}
	public void setProcTypeDesc(String procTypeDesc) {
		this.procTypeDesc = procTypeDesc;
	}
	public String getProjectCustomer() {
		return projectCustomer;
	}
	public void setProjectCustomer(String projectCustomer) {
		this.projectCustomer = projectCustomer;
	}
	public String getProjectImpl() {
		return projectImpl;
	}
	public void setProjectImpl(String projectImpl) {
		this.projectImpl = projectImpl;
	}
	
}
