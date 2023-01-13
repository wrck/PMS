package com.dp.plat.pms.extend.d365.entity;

public class PurchaseReceipt extends BaseEntity {
    // 订单源数据类型（Subcontract,Dispatch）
    private String sourceOrderType;

    // 订单源数据ID
    private Integer sourceOrderId;

    // 订单源收货类型（SubcontractPayment, DispatchSettlement）
    private String sourceReceiptType;

    // 订单源收货ID
    private Integer sourceReceiptId;

    // 采购订单号
    private String purchId;

    // 交货日期
    private String deliveryDate;

    private String documentDate;

    // 采购收货单号
    private String packingSlipId;

    // 采购收货备注
    private String packingSlipRemark;

    // 项目进度
    private String projectProgress;

    // 账套
    private String dataAreaId;

    /**
     * 获取订单源数据类型（Subcontract,Dispatch）
     *
     * @return sourceOrderType - 订单源数据类型（Subcontract,Dispatch）
     */
    public String getSourceOrderType() {
        return sourceOrderType;
    }

    /**
     * 设置订单源数据类型（Subcontract,Dispatch）
     *
     * @param sourceOrderType 订单源数据类型（Subcontract,Dispatch）
     */
    public void setSourceOrderType(String sourceOrderType) {
        this.sourceOrderType = sourceOrderType;
    }

    /**
     * 获取订单源数据ID
     *
     * @return sourceOrderId - 订单源数据ID
     */
    public Integer getSourceOrderId() {
        return sourceOrderId;
    }

    /**
     * 设置订单源数据ID
     *
     * @param sourceOrderId 订单源数据ID
     */
    public void setSourceOrderId(Integer sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    /**
     * 获取订单源收货类型（SubcontractPayment, DispatchSettlement）
     *
     * @return sourceReceiptType - 订单源收货类型（SubcontractPayment, DispatchSettlement）
     */
    public String getSourceReceiptType() {
        return sourceReceiptType;
    }

    /**
     * 设置订单源收货类型（SubcontractPayment, DispatchSettlement）
     *
     * @param sourceReceiptType 订单源收货类型（SubcontractPayment, DispatchSettlement）
     */
    public void setSourceReceiptType(String sourceReceiptType) {
        this.sourceReceiptType = sourceReceiptType;
    }

    /**
     * 获取订单源收货ID
     *
     * @return sourceReceiptId - 订单源收货ID
     */
    public Integer getSourceReceiptId() {
        return sourceReceiptId;
    }

    /**
     * 设置订单源收货ID
     *
     * @param sourceReceiptId 订单源收货ID
     */
    public void setSourceReceiptId(Integer sourceReceiptId) {
        this.sourceReceiptId = sourceReceiptId;
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
     * @return documentDate
     */
    public String getDocumentDate() {
        return documentDate;
    }

    /**
     * @param documentDate
     */
    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    /**
     * 获取采购收货单号
     *
     * @return packingSlipId - 采购收货单号
     */
    public String getPackingSlipId() {
        return packingSlipId;
    }

    /**
     * 设置采购收货单号
     *
     * @param packingSlipId 采购收货单号
     */
    public void setPackingSlipId(String packingSlipId) {
        this.packingSlipId = packingSlipId;
    }

    /**
     * 获取采购收货备注
     *
     * @return packingSlipRemark - 采购收货备注
     */
    public String getPackingSlipRemark() {
        return packingSlipRemark;
    }

    /**
     * 设置采购收货备注
     *
     * @param packingSlipRemark 采购收货备注
     */
    public void setPackingSlipRemark(String packingSlipRemark) {
        this.packingSlipRemark = packingSlipRemark;
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