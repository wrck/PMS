package com.dp.plat.pms.springmvc.vo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ProjectProduct {
    private String id;

    private String projectCode;

    private String orderExecNumber;

    // 公司编码
    private String corporationCode;

    // 安全服务先行核销ID
    private String ssfrId;

    private String productCode;

    private String productfirstCode;

    private String productName;

    private String productfirstName;

    private String productsubCode;

    private String productSubModel;

    private String productSubName;

    private Integer num;

    private Integer borrowNum;
    
    private BigDecimal price;
    
    private BigDecimal purchaseDiscount;
    
    private BigDecimal purchasePrice;
    
    private String projectType;
    
    private Integer orgId;
    
    private Map customInfo;


    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return projectCode
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @param projectCode
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * @return orderExecNumber
     */
    public String getOrderExecNumber() {
        return orderExecNumber;
    }

    /**
     * @param orderExecNumber
     */
    public void setOrderExecNumber(String orderExecNumber) {
        this.orderExecNumber = orderExecNumber;
    }

    /**
     * 获取公司编码
     *
     * @return corporationCode - 公司编码
     */
    public String getCorporationCode() {
        return corporationCode;
    }

    /**
     * 设置公司编码
     *
     * @param corporationCode 公司编码
     */
    public void setCorporationCode(String corporationCode) {
        this.corporationCode = corporationCode;
    }

    /**
     * 获取安全服务先行核销ID
     *
     * @return ssfrId - 安全服务先行核销ID
     */
    public String getSsfrId() {
        return ssfrId;
    }

    /**
     * 设置安全服务先行核销ID
     *
     * @param ssfrId 安全服务先行核销ID
     */
    public void setSsfrId(String ssfrId) {
        this.ssfrId = ssfrId;
    }

    /**
     * @return productCode
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * @param productCode
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * @return productfirstCode
     */
    public String getProductfirstCode() {
        return productfirstCode;
    }

    /**
     * @param productfirstCode
     */
    public void setProductfirstCode(String productfirstCode) {
        this.productfirstCode = productfirstCode;
    }

    /**
     * @return productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return productfirstName
     */
    public String getProductfirstName() {
        return productfirstName;
    }

    /**
     * @param productfirstName
     */
    public void setProductfirstName(String productfirstName) {
        this.productfirstName = productfirstName;
    }

    /**
     * @return productsubCode
     */
    public String getProductsubCode() {
        return productsubCode;
    }

    /**
     * @param productsubCode
     */
    public void setProductsubCode(String productsubCode) {
        this.productsubCode = productsubCode;
    }

    /**
     * @return productSubModel
     */
    public String getProductSubModel() {
        return productSubModel;
    }

    /**
     * @param productSubModel
     */
    public void setProductSubModel(String productSubModel) {
        this.productSubModel = productSubModel;
    }

    /**
     * @return productSubName
     */
    public String getProductSubName() {
        return productSubName;
    }

    /**
     * @param productSubName
     */
    public void setProductSubName(String productSubName) {
        this.productSubName = productSubName;
    }

    /**
     * @return num
     */
    public Integer getNum() {
        return num;
    }

    /**
     * @param num
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * @return borrowNum
     */
    public Integer getBorrowNum() {
        return borrowNum;
    }

    /**
     * @param borrowNum
     */
    public void setBorrowNum(Integer borrowNum) {
        this.borrowNum = borrowNum;
    }

	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * @return the purchaseDiscount
	 */
	public BigDecimal getPurchaseDiscount() {
		return purchaseDiscount;
	}

	/**
	 * @param purchaseDiscount the purchaseDiscount to set
	 */
	public void setPurchaseDiscount(BigDecimal purchaseDiscount) {
		this.purchaseDiscount = purchaseDiscount;
	}

	/**
	 * @return the purchasePrice
	 */
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	/**
	 * @param purchasePrice the purchasePrice to set
	 */
	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	
	public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Map getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(Map customInfo) {
        this.customInfo = customInfo;
    }

    public Object getCustomInfoByKey(String key) {
        Map<?, ?> customInfo = getCustomInfo();
        if (customInfo != null && !customInfo.isEmpty()) {
            return customInfo.get(key);
        }
        return null;
    }
    
    public Object getCustomInfoByKey(String key, Object defaultValue) {
        Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
        if (customInfo != null && !customInfo.isEmpty()) {
            return customInfo.getOrDefault(key, defaultValue);
        }
        return defaultValue;
    }

    public void setCustomInfoByKey(String key, Object value) {
        Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
        if (customInfo == null) {
            customInfo = new HashMap<>();
            this.setCustomInfo(customInfo);
            customInfo =  (Map<String, Object>) this.getCustomInfo();
        }
        customInfo.put(key, value);
    }
}