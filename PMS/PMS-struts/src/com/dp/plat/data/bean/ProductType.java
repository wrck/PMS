package com.dp.plat.data.bean;

public class ProductType {
	private int id;
	private String productTypeName;
	private String productTypeIds;
	private int DIa;//当月在CRD接收的产品订单项数量
	private int DId;//CRD日期发生在当月的产品订单项数量
	private int DIPa;//当月在SPD接收的产品订单项数量
	private int DIPd;//SPD日期发生在当月的产品订单项数量
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String getProductTypeIds() {
		return productTypeIds;
	}
	public void setProductTypeIds(String productTypeIds) {
		this.productTypeIds = productTypeIds;
	}
	public int getDIa() {
		return DIa;
	}
	public void setDIa(int dIa) {
		DIa = dIa;
	}
	public int getDId() {
		return DId;
	}
	public void setDId(int dId) {
		DId = dId;
	}
	public int getDIPa() {
		return DIPa;
	}
	public void setDIPa(int dIPa) {
		DIPa = dIPa;
	}
	public int getDIPd() {
		return DIPd;
	}
	public void setDIPd(int dIPd) {
		DIPd = dIPd;
	}
	
	
	
}
