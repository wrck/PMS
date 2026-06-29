package com.dp.plat.data.bean;

import java.util.Date;

/**
 * 需要用户维护计划的订单的实例
 * @author admin
 *
 */
public class OrderTodoBean {
	
	private String orderNumber;//SAP订单号
	private String contractNo;//合同号
	private Date orderDate;//过账日期即下单日期
	private Date custDeliverDate;//客户要求日期
	private String custDeliverRemark;//客户要求日期变更说明
	private String customerName;//客户名称
	private String projectName;//项目名称
	private String orderType;//订单类型
	private Date orderCreateDate;//订单创建日期
	private String plantype;//计划状态类型
	private int planType;
	private String remark;//SAP  订单注释
	private int orderPlanState;//订单是否关闭状态
	private String orderPlanRemark;//订单计划制定或变更说明
	
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public Date getCustDeliverDate() {
		return custDeliverDate;
	}
	public void setCustDeliverDate(Date custDeliverDate) {
		this.custDeliverDate = custDeliverDate;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public Date getOrderCreateDate() {
		return orderCreateDate;
	}
	public void setOrderCreateDate(Date orderCreateDate) {
		this.orderCreateDate = orderCreateDate;
	}
	public String getPlantype() {
		return plantype;
	}
	public void setPlantype(String plantype) {
		this.plantype = plantype;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getPlanType() {
		return planType;
	}
	public void setPlanType(int planType) {
		this.planType = planType;
	}
	public int getOrderPlanState() {
		return orderPlanState;
	}
	public void setOrderPlanState(int orderPlanState) {
		this.orderPlanState = orderPlanState;
	}
	public String getCustDeliverRemark() {
		return custDeliverRemark;
	}
	public void setCustDeliverRemark(String custDeliverRemark) {
		this.custDeliverRemark = custDeliverRemark;
	}
	public String getOrderPlanRemark() {
		return orderPlanRemark;
	}
	public void setOrderPlanRemark(String orderPlanRemark) {
		this.orderPlanRemark = orderPlanRemark;
	}
	
}
