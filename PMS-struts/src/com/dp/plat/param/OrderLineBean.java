package com.dp.plat.param;
/**
 * 从ERP刷新订单行信息
 * @author admin
 *
 */
public class OrderLineBean {
	private String orderNumber;
	private int lineNum;
	private String itemCode;
	private String itemDesc;
	private int orderQuantity;
	@SuppressWarnings("unused")
	private int deliverQuantity;
	private int openQuantity;
	private String bundleCode;
	private int warrantyMonth;
	private String compCode;
	private String profitCenter;
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public int getLineNum() {
		return lineNum;
	}
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public int getOpenQuantity() {
		return openQuantity;
	}
	public void setOpenQuantity(int openQuantity) {
		this.openQuantity = openQuantity;
	}
	public String getBundleCode() {
		return bundleCode;
	}
	public void setBundleCode(String bundleCode) {
		this.bundleCode = bundleCode;
	}
	public int getWarrantyMonth() {
		return warrantyMonth;
	}
	public void setWarrantyMonth(int warrantyMonth) {
		this.warrantyMonth = warrantyMonth;
	}
	public int getDeliverQuantity() {
		return orderQuantity - openQuantity;
	}
	public void setDeliverQuantity(int deliverQuantity) {
		this.deliverQuantity = deliverQuantity;
	}
    public String getCompCode() {
        return compCode;
    }
    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }
    public String getProfitCenter() {
        return profitCenter;
    }
    public void setProfitCenter(String profitCenter) {
        this.profitCenter = profitCenter;
    }
}
