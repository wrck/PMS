package com.dp.plat.data.bean;

import java.util.Calendar;
import java.util.Date;

/**
 * 订单主体信息bean
 * @author admin
 *
 */
public class OrderMainBean {
	private int id ;//表主键
	private String orderNumber;//SAP订单号
	private int orderLineId;//SAP订单行号
	private String contractNo;//合同号
	private String orderExcCode;//SMS执行单号
	private Date orderDate;//过账日期（即下单日期）
	private Date custDeliverDate;//要货日期（交付日期）
	private int custDeliverQuantity;//客户要求交付数量
	private String customerName;//客户名称
	private String itemCode;//产品编码
	private String itemName;//产品描述
	private int inventories;//存货量
	private int havePromiseQuantity;//已承诺
	private int haveOrderQuantity;//已订购
	private int orderQuantity;//此订单下单数量
	private int deliveredQuantity;//已交货数量
	private int unDeliverQuantity;//未清数量
	private String bundleCode;//bundle父编码
	private String warrantyMonths;//维保期-月
	private String projectName;//项目名称
	private String remark;//SAP注释
	private String orderType;//订单类型
	private Date orderCreateDate;//订单创建日期
	private Date plannedDate;//计划承诺日期
	private int  plannedQuantity;//计划交付数量
	private Date deliverDate;//实际交付日期
	private int deliverQuantity;//实际交付数量
	private String planRemark;//计划备注说明
	//数据导出筛选条件 
	private Date orderDateStart;
	private Date orderDateEnd;
	
	private Date plannedDateStart;
	private Date plannedDateEnd;
	//划分变更记录
	private long updateVersion;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public int getOrderLineId() {
		return orderLineId;
	}
	public void setOrderLineId(int orderLineId) {
		this.orderLineId = orderLineId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getOrderExcCode() {
		return orderExcCode;
	}
	public void setOrderExcCode(String orderExcCode) {
		this.orderExcCode = orderExcCode;
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
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getInventories() {
		return inventories;
	}
	public void setInventories(int inventories) {
		this.inventories = inventories;
	}
	public int getHavePromiseQuantity() {
		return havePromiseQuantity;
	}
	public void setHavePromiseQuantity(int havePromiseQuantity) {
		this.havePromiseQuantity = havePromiseQuantity;
	}
	public int getHaveOrderQuantity() {
		return haveOrderQuantity;
	}
	public void setHaveOrderQuantity(int haveOrderQuantity) {
		this.haveOrderQuantity = haveOrderQuantity;
	}
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public int getDeliveredQuantity() {
		return deliveredQuantity;
	}
	public void setDeliveredQuantity(int deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}
	public int getUnDeliverQuantity() {
		return unDeliverQuantity;
	}
	public void setUnDeliverQuantity(int unDeliverQuantity) {
		this.unDeliverQuantity = unDeliverQuantity;
	}
	public String getBundleCode() {
		return bundleCode;
	}
	public void setBundleCode(String bundleCode) {
		this.bundleCode = bundleCode;
	}
	public String getWarrantyMonths() {
		return warrantyMonths;
	}
	public void setWarrantyMonths(String warrantyMonths) {
		this.warrantyMonths = warrantyMonths;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public Date getPlannedDate() {
		return plannedDate;
	}
	public void setPlannedDate(Date plannedDate) {
		this.plannedDate = plannedDate;
	}
	public int getPlannedQuantity() {
		return plannedQuantity;
	}
	public void setPlannedQuantity(int plannedQuantity) {
		this.plannedQuantity = plannedQuantity;
	}
	public Date getDeliverDate() {
		return deliverDate;
	}
	public void setDeliverDate(Date deliverDate) {
		this.deliverDate = deliverDate;
	}
	public int getDeliverQuantity() {
		return deliverQuantity;
	}
	public void setDeliverQuantity(int deliverQuantity) {
		this.deliverQuantity = deliverQuantity;
	}
	public String getPlanRemark() {
		return planRemark;
	}
	public void setPlanRemark(String planRemark) {
		this.planRemark = planRemark;
	}
	public Date getOrderDateStart() {
		return orderDateStart;
	}
	public void setOrderDateStart(Date orderDateStart) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(orderDateStart);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		this.orderDateStart = calendar.getTime();
	}
	public Date getOrderDateEnd() {
		return orderDateEnd;
	}
	public void setOrderDateEnd(Date orderDateEnd) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(orderDateEnd);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		this.orderDateEnd = calendar.getTime();
	}
	public Date getPlannedDateStart() {
		return plannedDateStart;
	}
	public void setPlannedDateStart(Date plannedDateStart) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(plannedDateStart);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		this.plannedDateStart = calendar.getTime();
	}
	public Date getPlannedDateEnd() {
		return plannedDateEnd;
	}
	public void setPlannedDateEnd(Date plannedDateEnd) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(plannedDateEnd);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		this.plannedDateEnd = calendar.getTime();
	}
	public long getUpdateVersion() {
		return updateVersion;
	}
	public void setUpdateVersion(long updateVersion) {
		this.updateVersion = updateVersion;
	}
	public int getCustDeliverQuantity() {
		return custDeliverQuantity;
	}
	public void setCustDeliverQuantity(int custDeliverQuantity) {
		this.custDeliverQuantity = custDeliverQuantity;
	}

	
}
