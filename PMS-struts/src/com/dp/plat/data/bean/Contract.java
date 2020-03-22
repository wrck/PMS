package com.dp.plat.data.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 合同相关信息
 * @author admin
 *
 */
public class Contract {
	private String contractNo;
	private String projectName;
	private String customerName;
	private Date orderCreateTime;
	private String orderCreateString;
	private String marketName;
	private String salesManName;
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Date getOrderCreateTime() {
		return orderCreateTime;
	}
	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public String getSalesManName() {
		return salesManName;
	}
	public void setSalesManName(String salesManName) {
		this.salesManName = salesManName;
	}
	public String getOrderCreateString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if( orderCreateTime != null){
			this.orderCreateString = sdf.format(orderCreateTime);
		}
		return orderCreateString;
	}
	public void setOrderCreateString(String orderCreateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		this.orderCreateString = orderCreateString;
		if( orderCreateTime != null){
			this.orderCreateString = sdf.format(orderCreateTime);
		}
	}
	
	
}
