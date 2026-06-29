package com.dp.plat.pms.extend.d365.entity;

public class Purchase extends BaseEntity {
    // 源数据类型
    private String sourceType;

    // 源数据ID
    private Integer sourceId;

    // 采购订单池
    private String purchPoolId;

    // 采购订单号
    private String purchId;

    // 供应商账号
    private String vendAccount;

    // 采购事项
    private String purchName;

    // 采购合同号
    private String purContract;

    // 销售合同号
    private String salesContract;

    // 总金额
    private String contractAmount;

    // 订货人
    private String workerPurchPlacer;

    // 申请人
    private String applicant;

    // 仓库
    private String inventLocationId;

    // 交货日期
    private String deliveryDate;

    // 交货模式
    private String dlvMode;

    // 交货条款
    private String dlvTerm;

    // 付款条款
    private String payment;

    // 付款方式
    private String paymMode;

    // 整单备注
    private String remark;

    // 外部系统编号
    private String otherSysNum;

    // 项目名称
    private String projectName;

    // 项目进度
    private String projectProgress;

    // 转包类型
    private String subcontractType;

    // 转包开始日期
    private String subcontStartDate;

    // 转包结束日期
    private String subcontEndDate;

    // 账套
    private String dataAreaId;

    /**
     * 获取源数据类型
     *
     * @return sourceType - 源数据类型
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * 设置源数据类型
     *
     * @param sourceType 源数据类型
     */
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * 获取源数据ID
     *
     * @return sourceId - 源数据ID
     */
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * 设置源数据ID
     *
     * @param sourceId 源数据ID
     */
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 获取采购订单池
     *
     * @return purchPoolId - 采购订单池
     */
    public String getPurchPoolId() {
        return purchPoolId;
    }

    /**
     * 设置采购订单池
     *
     * @param purchPoolId 采购订单池
     */
    public void setPurchPoolId(String purchPoolId) {
        this.purchPoolId = purchPoolId;
    }

    /**
     * 获取采购订单号
     *
     * @return purchId - 采购订单号
     */
    public String getPurchId() {
        return purchId;
    }

    /**
     * 设置采购订单号
     *
     * @param purchId 采购订单号
     */
    public void setPurchId(String purchId) {
        this.purchId = purchId;
    }

    /**
     * 获取供应商账号
     *
     * @return vendAccount - 供应商账号
     */
    public String getVendAccount() {
        return vendAccount;
    }

    /**
     * 设置供应商账号
     *
     * @param vendAccount 供应商账号
     */
    public void setVendAccount(String vendAccount) {
        this.vendAccount = vendAccount;
    }

    /**
     * 获取采购事项
     *
     * @return purchName - 采购事项
     */
    public String getPurchName() {
        return purchName;
    }

    /**
     * 设置采购事项
     *
     * @param purchName 采购事项
     */
    public void setPurchName(String purchName) {
        this.purchName = purchName;
    }

    /**
     * 获取采购合同号
     *
     * @return purContract - 采购合同号
     */
    public String getPurContract() {
        return purContract;
    }

    /**
     * 设置采购合同号
     *
     * @param purContract 采购合同号
     */
    public void setPurContract(String purContract) {
        this.purContract = purContract;
    }

    /**
     * 获取销售合同号
     *
     * @return salesContract - 销售合同号
     */
    public String getSalesContract() {
        return salesContract;
    }

    /**
     * 设置销售合同号
     *
     * @param salesContract 销售合同号
     */
    public void setSalesContract(String salesContract) {
        this.salesContract = salesContract;
    }

    /**
     * 获取总金额
     *
     * @return contractAmount - 总金额
     */
    public String getContractAmount() {
        return contractAmount;
    }

    /**
     * 设置总金额
     *
     * @param contractAmount 总金额
     */
    public void setContractAmount(String contractAmount) {
        this.contractAmount = contractAmount;
    }

    /**
     * 获取订货人
     *
     * @return workerPurchPlacer - 订货人
     */
    public String getWorkerPurchPlacer() {
        return workerPurchPlacer;
    }

    /**
     * 设置订货人
     *
     * @param workerPurchPlacer 订货人
     */
    public void setWorkerPurchPlacer(String workerPurchPlacer) {
        this.workerPurchPlacer = workerPurchPlacer;
    }

    /**
     * 获取申请人
     *
     * @return applicant - 申请人
     */
    public String getApplicant() {
        return applicant;
    }

    /**
     * 设置申请人
     *
     * @param applicant 申请人
     */
    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    /**
     * 获取仓库
     *
     * @return inventLocationId - 仓库
     */
    public String getInventLocationId() {
        return inventLocationId;
    }

    /**
     * 设置仓库
     *
     * @param inventLocationId 仓库
     */
    public void setInventLocationId(String inventLocationId) {
        this.inventLocationId = inventLocationId;
    }

    /**
     * 获取交货日期
     *
     * @return deliveryDate - 交货日期
     */
    public String getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * 设置交货日期
     *
     * @param deliveryDate 交货日期
     */
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * 获取交货模式
     *
     * @return dlvMode - 交货模式
     */
    public String getDlvMode() {
        return dlvMode;
    }

    /**
     * 设置交货模式
     *
     * @param dlvMode 交货模式
     */
    public void setDlvMode(String dlvMode) {
        this.dlvMode = dlvMode;
    }

    /**
     * 获取交货条款
     *
     * @return dlvTerm - 交货条款
     */
    public String getDlvTerm() {
        return dlvTerm;
    }

    /**
     * 设置交货条款
     *
     * @param dlvTerm 交货条款
     */
    public void setDlvTerm(String dlvTerm) {
        this.dlvTerm = dlvTerm;
    }

    /**
     * 获取付款条款
     *
     * @return payment - 付款条款
     */
    public String getPayment() {
        return payment;
    }

    /**
     * 设置付款条款
     *
     * @param payment 付款条款
     */
    public void setPayment(String payment) {
        this.payment = payment;
    }

    /**
     * 获取付款方式
     *
     * @return paymMode - 付款方式
     */
    public String getPaymMode() {
        return paymMode;
    }

    /**
     * 设置付款方式
     *
     * @param paymMode 付款方式
     */
    public void setPaymMode(String paymMode) {
        this.paymMode = paymMode;
    }

    /**
     * 获取整单备注
     *
     * @return remark - 整单备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置整单备注
     *
     * @param remark 整单备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取外部系统编号
     *
     * @return otherSysNum - 外部系统编号
     */
    public String getOtherSysNum() {
        return otherSysNum;
    }

    /**
     * 设置外部系统编号
     *
     * @param otherSysNum 外部系统编号
     */
    public void setOtherSysNum(String otherSysNum) {
        this.otherSysNum = otherSysNum;
    }

    /**
     * 获取项目名称
     *
     * @return projectName - 项目名称
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * 设置项目名称
     *
     * @param projectName 项目名称
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * 获取项目进度
     *
     * @return projectProgress - 项目进度
     */
    public String getProjectProgress() {
        return projectProgress;
    }

    /**
     * 设置项目进度
     *
     * @param projectProgress 项目进度
     */
    public void setProjectProgress(String projectProgress) {
        this.projectProgress = projectProgress;
    }

    /**
     * 获取转包类型
     *
     * @return subcontractType - 转包类型
     */
    public String getSubcontractType() {
        return subcontractType;
    }

    /**
     * 设置转包类型
     *
     * @param subcontractType 转包类型
     */
    public void setSubcontractType(String subcontractType) {
        this.subcontractType = subcontractType;
    }

    /**
     * 获取转包开始日期
     *
     * @return subcontStartDate - 转包开始日期
     */
    public String getSubcontStartDate() {
        return subcontStartDate;
    }

    /**
     * 设置转包开始日期
     *
     * @param subcontStartDate 转包开始日期
     */
    public void setSubcontStartDate(String subcontStartDate) {
        this.subcontStartDate = subcontStartDate;
    }

    /**
     * 获取转包结束日期
     *
     * @return subcontEndDate - 转包结束日期
     */
    public String getSubcontEndDate() {
        return subcontEndDate;
    }

    /**
     * 设置转包结束日期
     *
     * @param subcontEndDate 转包结束日期
     */
    public void setSubcontEndDate(String subcontEndDate) {
        this.subcontEndDate = subcontEndDate;
    }

    /**
     * 获取账套
     *
     * @return dataAreaId - 账套
     */
    public String getDataAreaId() {
        return dataAreaId;
    }

    /**
     * 设置账套
     *
     * @param dataAreaId 账套
     */
    public void setDataAreaId(String dataAreaId) {
        this.dataAreaId = dataAreaId;
    }

}