package com.dp.plat.param;

import java.util.Date;
/**
 * 从ERP刷新的订单头信息
 * @author admin
 *
 */
public class OrderBean {
	private String orderNumber;
	private String contractNo;
	private String orderExecNumber;
	private Date orderCreateTime;
	private Date customerRequireTime;
	private String customerCode;
	private String customerName;
	private String projectName;
	private String orderComment;
	private String compCode;
	private String salesType;
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
	public String getOrderExecNumber() {
		return orderExecNumber;
	}
	public void setOrderExecNumber(String orderExecNumber) {
		this.orderExecNumber = orderExecNumber;
	}
	public Date getOrderCreateTime() {
		return orderCreateTime;
	}
	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}
	public Date getCustomerRequireTime() {
		return customerRequireTime;
	}
	public void setCustomerRequireTime(Date customerRequireTime) {
		this.customerRequireTime = customerRequireTime;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getProjectName() {
		if(projectName != null){
			if(projectName.startsWith("<")){
				projectName.replaceFirst("<", "");
			}
			if(projectName.endsWith(">")){
				projectName.replaceAll(">", "");
			}
		}
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getOrderComment() {
		return orderComment;
	}
	public void setOrderComment(String orderComment) {
		this.orderComment = orderComment;
	}
    public String getCompCode() {
        return compCode;
    }
    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }
    public String getSalesType() {
        return salesType;
    }
    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }
    
}
