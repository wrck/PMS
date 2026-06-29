package com.dp.plat.data.bean;

public class WorkflowCommonParam extends CustomInfoEntity {
	private String deploymentId;// 部署id
	private String imageName;//
	private String taskId;// 任务id
	private String formUrl;// 对业务表单的请求url
	private int objId;// 对象id
	private String outcome;// 结果
	private String comment;// 意见
	private String assignee;// 代理人
	private boolean existNextNode;// 是否存在
	private String instId;// 流程实例id
	private int flag;// 是查看还是审批 ，也可以用于其他标识
	private int issamecustomer;// 借货转项目使用，是否为同一个客户
	private int issave;// 是否保存草稿（0：保存草稿；1：提交申请）
	private int needleader;
	private int type;// 类型 传递数据
	private int isBusinessBeforeFit;// 项目总结时 专员判断是否与商务报备一致 ，1 一致 0 不一致
	private int workflowflag;// 流程审批标记，判断是否订单审批的销管提交（1：是）
	private int majorProjectDeploymentId;// 设置重大项目申请流程定义最新部署Id
	private int businessBeforeApplyDeploymentId;// 设置商务报备流程定义最新部署id
	private int businessApplyOfficeDeploymentId;// 设置办事处项目总结流程定义最新部署id
	private int businessApplySystemDeploymentId;// 设置系统项目总结流程定义最新部署id
	private int businessOrderDeploymentId;// 设置订单申请（一般项目）流程定义最新部署Id
	private int businessOrderDirectDeploymentId;// 设置订单申请（直签/集采）流程定义最新部署Id
	private int businessApplyLostId;// 设置丢单总结定义最新部署Id
	private int projectInvalidId;// 设置项目失效最新部署Id
	private int canSee;// 只能查看任务 = 1
	private int approveStatus;// 审批操作的状态（成功或发生错误）
	private int marketPower;//借货审批是否超出市场部审批
	
	public int getCanSee() {
		return canSee;
	}

	public void setCanSee(int canSee) {
		this.canSee = canSee;
	}

	public int getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(int approveStatus) {
		this.approveStatus = approveStatus;
	}

	public int getMajorProjectDeploymentId() {
		return majorProjectDeploymentId;
	}

	public void setMajorProjectDeploymentId(int majorProjectDeploymentId) {
		this.majorProjectDeploymentId = majorProjectDeploymentId;
	}

	public int getBusinessOrderDirectDeploymentId() {
		return businessOrderDirectDeploymentId;
	}

	public void setBusinessOrderDirectDeploymentId(
			int businessOrderDirectDeploymentId) {
		this.businessOrderDirectDeploymentId = businessOrderDirectDeploymentId;
	}

	public int getWorkflowflag() {
		return workflowflag;
	}

	public void setWorkflowflag(int workflowflag) {
		this.workflowflag = workflowflag;
	}

	public int getBusinessBeforeApplyDeploymentId() {
		return businessBeforeApplyDeploymentId;
	}

	public void setBusinessBeforeApplyDeploymentId(
			int businessBeforeApplyDeploymentId) {
		this.businessBeforeApplyDeploymentId = businessBeforeApplyDeploymentId;
	}

	public int getBusinessApplyOfficeDeploymentId() {
		return businessApplyOfficeDeploymentId;
	}

	public void setBusinessApplyOfficeDeploymentId(
			int businessApplyOfficeDeploymentId) {
		this.businessApplyOfficeDeploymentId = businessApplyOfficeDeploymentId;
	}

	public int getBusinessApplySystemDeploymentId() {
		return businessApplySystemDeploymentId;
	}

	public void setBusinessApplySystemDeploymentId(
			int businessApplySystemDeploymentId) {
		this.businessApplySystemDeploymentId = businessApplySystemDeploymentId;
	}

	public int getIsBusinessBeforeFit() {
		return isBusinessBeforeFit;
	}

	public void setIsBusinessBeforeFit(int isBusinessBeforeFit) {
		this.isBusinessBeforeFit = isBusinessBeforeFit;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getNeedleader() {
		return needleader;
	}

	public void setNeedleader(int needleader) {
		this.needleader = needleader;
	}

	public int getIssave() {
		return issave;
	}

	public void setIssave(int issave) {
		this.issave = issave;
	}

	public int getIssamecustomer() {
		return issamecustomer;
	}

	public void setIssamecustomer(int issamecustomer) {
		this.issamecustomer = issamecustomer;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public boolean isExistNextNode() {
		return existNextNode;
	}

	public void setExistNextNode(boolean existNextNode) {
		this.existNextNode = existNextNode;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}

	public int getBusinessOrderDeploymentId() {
		return businessOrderDeploymentId;
	}

	public void setBusinessOrderDeploymentId(int businessOrderDeploymentId) {
		this.businessOrderDeploymentId = businessOrderDeploymentId;
	}

	public int getBusinessApplyLostId() {
		return businessApplyLostId;
	}

	public void setBusinessApplyLostId(int businessApplyLostId) {
		this.businessApplyLostId = businessApplyLostId;
	}

	public int getProjectInvalidId() {
		return projectInvalidId;
	}

	public void setProjectInvalidId(int projectInvalidId) {
		this.projectInvalidId = projectInvalidId;
	}

	public int getMarketPower() {
		return marketPower;
	}

	public void setMarketPower(int marketPower) {
		this.marketPower = marketPower;
	}

}
