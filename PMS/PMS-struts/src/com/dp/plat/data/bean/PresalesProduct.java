package com.dp.plat.data.bean;
/**
 * 售前项目产品明细表
 * @author admin
 *
 */
public class PresalesProduct extends BaseBean{
	private int productLineId;//明细表主键
	private int presalesId;////售前项目主表主键
	private String productFirstName;//产品一级
	private String productTypeName;//产品类型
	private String itemCode;//编码
	private String itemModel;//型号
	private String itemDesc;//描述
	private double price;//目录价
	private int productNum;//数量
	private int transferNum;// 转销数
	private int hexiaoNum;// 核销数
	private String remark;//备注
	
	public int getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}
	public int getPresalesId() {
		return presalesId;
	}
	public void setPresalesId(int presalesId) {
		this.presalesId = presalesId;
	}
	public String getProductFirstName() {
		return productFirstName;
	}
	public void setProductFirstName(String productFirstName) {
		this.productFirstName = productFirstName;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemModel() {
		return itemModel;
	}
	public void setItemModel(String itemModel) {
		this.itemModel = itemModel;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getProductNum() {
		return productNum;
	}
	public void setProductNum(int productNum) {
		this.productNum = productNum;
	}
	public int getTransferNum() {
		return transferNum;
	}
	public void setTransferNum(int transferNum) {
		this.transferNum = transferNum;
	}
	public int getHexiaoNum() {
		return hexiaoNum;
	}
	public void setHexiaoNum(int hexiaoNum) {
		this.hexiaoNum = hexiaoNum;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
