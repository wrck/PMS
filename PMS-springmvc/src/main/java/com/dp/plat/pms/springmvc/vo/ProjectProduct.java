package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.core.entity.BaseEntity;

public class ProjectProduct extends BaseEntity {
    private Integer id;

    private String projectCode;

    private String orderExecNumber;

    // 公司编码
    private String corporationCode;

    // 安全服务先行核销ID
    private Integer ssfrId;

    private String productCode;

    private String productfirstCode;

    private String productName;

    private String productfirstName;

    private String productsubCode;

    private String productSubModel;

    private String productSubName;

    private Integer num;

    private Integer borrowNum;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
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
    public Integer getSsfrId() {
        return ssfrId;
    }

    /**
     * 设置安全服务先行核销ID
     *
     * @param ssfrId 安全服务先行核销ID
     */
    public void setSsfrId(Integer ssfrId) {
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
}