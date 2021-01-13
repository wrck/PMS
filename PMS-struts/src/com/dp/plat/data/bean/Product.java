package com.dp.plat.data.bean;
/**
 * 订单产品项
 * @author admin
 *
 */
public class Product {
	private int id;
	private int projectId;
	private String contractNo;
	private String itemCode;
	private String itemName;
	private int projectQuantity;
	private int branchQuantity;
	private int orderQuantity;
	private int deliverQuantity;
	private int openQuantity;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public int getDeliverQuantity() {
		return deliverQuantity;
	}
	public void setDeliverQuantity(int deliverQuantity) {
		this.deliverQuantity = deliverQuantity;
	}
	public int getOpenQuantity() {
		return openQuantity;
	}
	public void setOpenQuantity(int openQuantity) {
		this.openQuantity = openQuantity;
	}
	public int getProjectQuantity() {
		return projectQuantity;
	}
	public void setProjectQuantity(int projectQuantity) {
		this.projectQuantity = projectQuantity;
	}
	public int getBranchQuantity() {
		return branchQuantity;
	}
	public void setBranchQuantity(int branchQuantity) {
		this.branchQuantity = branchQuantity;
	}
	
	
}
