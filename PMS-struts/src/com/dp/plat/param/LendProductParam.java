package com.dp.plat.param;
/**
 * 测试类借货产品配置 与SMS v_lend_product_4_pms 一一对应
 * @author admin
 *
 */
public class LendProductParam {
	private int lendInfoId;
	private String productFirstName;
	private String productName;
	private String productSubCode;
	private String productSubModel;
	private String productSubName;
	private int lendNum;
	private int orderNum;
	private int deliverNum;
	private int hexiaoNum;
	private int transferNum;
	
	private String memo;
	
	public int getLendInfoId() {
		return lendInfoId;
	}
	public void setLendInfoId(int lendInfoId) {
		this.lendInfoId = lendInfoId;
	}
	public String getProductFirstName() {
		return productFirstName;
	}
	public void setProductFirstName(String productFirstName) {
		this.productFirstName = productFirstName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductSubCode() {
		return productSubCode;
	}
	public void setProductSubCode(String productSubCode) {
		this.productSubCode = productSubCode;
	}
	public String getProductSubModel() {
		return productSubModel;
	}
	public void setProductSubModel(String productSubModel) {
		this.productSubModel = productSubModel;
	}
	public String getProductSubName() {
		return productSubName;
	}
	public void setProductSubName(String productSubName) {
		this.productSubName = productSubName;
	}
	public int getLendNum() {
		return lendNum;
	}
	public void setLendNum(int lendNum) {
		this.lendNum = lendNum;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
    public int getOrderNum() {
        return orderNum;
    }
    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }
    public int getDeliverNum() {
        return deliverNum;
    }
    public void setDeliverNum(int deliverNum) {
        this.deliverNum = deliverNum;
    }
    public int getHexiaoNum() {
        return hexiaoNum;
    }
    public void setHexiaoNum(int hexiaoNum) {
        this.hexiaoNum = hexiaoNum;
    }
    public int getTransferNum() {
        return transferNum;
    }
    public void setTransferNum(int transferNum) {
        this.transferNum = transferNum;
    }
}
