package com.dp.plat.subcontract.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SubcontractPrice {
    private Integer id;

    // 转包项目Id
    private Integer subcontractId;

    // 合同号
    private String contractNo;

    // 执行单号
    private String orderExecNumber;

    // 项目编码
    private String projectCode;

    // 工程服务价
    private String engineeFee;

    // SMS链接参数1
    private String objId;

    // SMS链接参数2
    private String procType;

    // 合同转包价
    private String price;

    @JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    private Date createTime;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
    private Date updateTime;

    private String updateBy;

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
     * 获取转包项目Id
     *
     * @return subcontractId - 转包项目Id
     */
    public Integer getSubcontractId() {
        return subcontractId;
    }

    /**
     * 设置转包项目Id
     *
     * @param subcontractId 转包项目Id
     */
    public void setSubcontractId(Integer subcontractId) {
        this.subcontractId = subcontractId;
    }

    /**
     * 获取合同号
     *
     * @return contractNo - 合同号
     */
    public String getContractNo() {
        return contractNo;
    }

    /**
     * 设置合同号
     *
     * @param contractNo 合同号
     */
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    /**
     * 获取执行单号
     *
     * @return orderExecNumber - 执行单号
     */
    public String getOrderExecNumber() {
        return orderExecNumber;
    }

    /**
     * 设置执行单号
     *
     * @param orderExecNumber 执行单号
     */
    public void setOrderExecNumber(String orderExecNumber) {
        this.orderExecNumber = orderExecNumber;
    }

    /**
     * 获取项目编码
     *
     * @return projectCode - 项目编码
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 设置项目编码
     *
     * @param projectCode 项目编码
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * 获取工程服务价
     *
     * @return engineeFee - 工程服务价
     */
    public String getEngineeFee() {
        return engineeFee;
    }

    /**
     * 设置工程服务价
     *
     * @param engineeFee 工程服务价
     */
    public void setEngineeFee(String engineeFee) {
        this.engineeFee = engineeFee;
    }

    /**
     * 获取SMS链接参数1
     *
     * @return objId - SMS链接参数1
     */
    public String getObjId() {
        return objId;
    }

    /**
     * 设置SMS链接参数1
     *
     * @param objId SMS链接参数1
     */
    public void setObjId(String objId) {
        this.objId = objId;
    }

    /**
     * 获取SMS链接参数2
     *
     * @return procType - SMS链接参数2
     */
    public String getProcType() {
        return procType;
    }

    /**
     * 设置SMS链接参数2
     *
     * @param procType SMS链接参数2
     */
    public void setProcType(String procType) {
        this.procType = procType;
    }

    /**
     * 获取合同转包价
     *
     * @return price - 合同转包价
     */
    public String getPrice() {
        return price;
    }

    /**
     * 设置合同转包价
     *
     * @param price 合同转包价
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @return createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return createBy
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * @param createBy
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * @return updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return updateBy
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * @param updateBy
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}