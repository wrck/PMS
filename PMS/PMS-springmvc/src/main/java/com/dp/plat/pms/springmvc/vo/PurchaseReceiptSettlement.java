package com.dp.plat.pms.springmvc.vo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Id;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PurchaseReceiptSettlement {
    @Id
    // 物料收货行ID
    private Long slipId;

    // 订单行批次ID
    private String inventTransId;

    // 发票单号
    private String innerInvoiceId;

    // 开票时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date invoiceDate;

    // 发票号
    private String invoiceId;

    // 采购单号
    private String purchId;

    // 供应商账户
    private String vendAccount;

    // 供应商名称
    private String purchName;

    // 采购订单池
    private String purchPoolId;

    // 物料收货号
    private String packingSlipId;

    // 物料收货备注
    private String packingSlipRemark;

    // 物料收货数量
    private BigDecimal slipQty;

    // 供应商对账数量
    private BigDecimal receiveQty;

    // 开票数量
    private BigDecimal invoiceQty;

    // 单价
    private BigDecimal price;

    // 开票单据
    private BigDecimal invoicePrice;

    // 供应商对账金额
    private BigDecimal receiveAmount;

    // 开票金额
    private BigDecimal invoiceAmount;

    // 结算数量
    private BigDecimal settleQty;

    // 物料收货行金额
    private BigDecimal lineAmount;

    // 开票总金额
    private BigDecimal invoiceAmountTotal;

    // 结算总金额
    private BigDecimal settleAmountTotal;

    // 结算金额
    private BigDecimal settleAmount;

    // 物料收货时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date confirmTime;

    // 结算时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date settleTime;

    // 项目进度
    private String projectProgress;

    // 外部系统单号
    private String otherSysNum;

    private Long partition;

    private String dataAreaId;

    // 结算ID
    private Long settleId;

    /**
     * 获取物料收货行ID
     *
     * @return slipId - 物料收货行ID
     */
    public Long getSlipId() {
        return slipId;
    }

    /**
     * 设置物料收货行ID
     *
     * @param slipId 物料收货行ID
     */
    public void setSlipId(Long slipId) {
        this.slipId = slipId;
    }

    /**
     * 获取订单行批次ID
     *
     * @return inventTransId - 订单行批次ID
     */
    public String getInventTransId() {
        return inventTransId;
    }

    /**
     * 设置订单行批次ID
     *
     * @param inventTransId 订单行批次ID
     */
    public void setInventTransId(String inventTransId) {
        this.inventTransId = inventTransId;
    }

    /**
     * 获取发票单号
     *
     * @return innerInvoiceId - 发票单号
     */
    public String getInnerInvoiceId() {
        return innerInvoiceId;
    }

    /**
     * 设置发票单号
     *
     * @param innerInvoiceId 发票单号
     */
    public void setInnerInvoiceId(String innerInvoiceId) {
        this.innerInvoiceId = innerInvoiceId;
    }

    /**
     * 获取开票时间
     *
     * @return invoiceDate - 开票时间
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * 设置开票时间
     *
     * @param invoiceDate 开票时间
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * 获取发票号
     *
     * @return invoiceId - 发票号
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     * 设置发票号
     *
     * @param invoiceId 发票号
     */
    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * 获取采购单号
     *
     * @return purchId - 采购单号
     */
    public String getPurchId() {
        return purchId;
    }

    /**
     * 设置采购单号
     *
     * @param purchId 采购单号
     */
    public void setPurchId(String purchId) {
        this.purchId = purchId;
    }

    /**
     * 获取供应商账户
     *
     * @return vendAccount - 供应商账户
     */
    public String getVendAccount() {
        return vendAccount;
    }

    /**
     * 设置供应商账户
     *
     * @param vendAccount 供应商账户
     */
    public void setVendAccount(String vendAccount) {
        this.vendAccount = vendAccount;
    }

    /**
     * 获取供应商名称
     *
     * @return purchName - 供应商名称
     */
    public String getPurchName() {
        return purchName;
    }

    /**
     * 设置供应商名称
     *
     * @param purchName 供应商名称
     */
    public void setPurchName(String purchName) {
        this.purchName = purchName;
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
     * 获取物料收货号
     *
     * @return packingSlipId - 物料收货号
     */
    public String getPackingSlipId() {
        return packingSlipId;
    }

    /**
     * 设置物料收货号
     *
     * @param packingSlipId 物料收货号
     */
    public void setPackingSlipId(String packingSlipId) {
        this.packingSlipId = packingSlipId;
    }

    /**
     * 获取物料收货备注
     *
     * @return packingSlipRemark - 物料收货备注
     */
    public String getPackingSlipRemark() {
        return packingSlipRemark;
    }

    /**
     * 设置物料收货备注
     *
     * @param packingSlipRemark 物料收货备注
     */
    public void setPackingSlipRemark(String packingSlipRemark) {
        this.packingSlipRemark = packingSlipRemark;
    }

    /**
     * 获取物料收货数量
     *
     * @return slipQty - 物料收货数量
     */
    public BigDecimal getSlipQty() {
        return slipQty;
    }

    /**
     * 设置物料收货数量
     *
     * @param slipQty 物料收货数量
     */
    public void setSlipQty(BigDecimal slipQty) {
        this.slipQty = slipQty;
    }

    /**
     * 获取供应商对账数量
     *
     * @return receiveQty - 供应商对账数量
     */
    public BigDecimal getReceiveQty() {
        return receiveQty;
    }

    /**
     * 设置供应商对账数量
     *
     * @param receiveQty 供应商对账数量
     */
    public void setReceiveQty(BigDecimal receiveQty) {
        this.receiveQty = receiveQty;
    }

    /**
     * 获取开票数量
     *
     * @return invoiceQty - 开票数量
     */
    public BigDecimal getInvoiceQty() {
        return invoiceQty;
    }

    /**
     * 设置开票数量
     *
     * @param invoiceQty 开票数量
     */
    public void setInvoiceQty(BigDecimal invoiceQty) {
        this.invoiceQty = invoiceQty;
    }

    /**
     * 获取单价
     *
     * @return price - 单价
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置单价
     *
     * @param price 单价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取开票单据
     *
     * @return invoicePrice - 开票单据
     */
    public BigDecimal getInvoicePrice() {
        return invoicePrice;
    }

    /**
     * 设置开票单据
     *
     * @param invoicePrice 开票单据
     */
    public void setInvoicePrice(BigDecimal invoicePrice) {
        this.invoicePrice = invoicePrice;
    }

    /**
     * 获取供应商对账金额
     *
     * @return receiveAmount - 供应商对账金额
     */
    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    /**
     * 设置供应商对账金额
     *
     * @param receiveAmount 供应商对账金额
     */
    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    /**
     * 获取开票金额
     *
     * @return invoiceAmount - 开票金额
     */
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * 设置开票金额
     *
     * @param invoiceAmount 开票金额
     */
    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * 获取结算数量
     *
     * @return settleQty - 结算数量
     */
    public BigDecimal getSettleQty() {
        return settleQty;
    }

    /**
     * 设置结算数量
     *
     * @param settleQty 结算数量
     */
    public void setSettleQty(BigDecimal settleQty) {
        this.settleQty = settleQty;
    }

    /**
     * 获取物料收货行金额
     *
     * @return lineAmount - 物料收货行金额
     */
    public BigDecimal getLineAmount() {
        return lineAmount;
    }

    /**
     * 设置物料收货行金额
     *
     * @param lineAmount 物料收货行金额
     */
    public void setLineAmount(BigDecimal lineAmount) {
        this.lineAmount = lineAmount;
    }

    /**
     * 获取开票总金额
     *
     * @return invoiceAmountTotal - 开票总金额
     */
    public BigDecimal getInvoiceAmountTotal() {
        return invoiceAmountTotal;
    }

    /**
     * 设置开票总金额
     *
     * @param invoiceAmountTotal 开票总金额
     */
    public void setInvoiceAmountTotal(BigDecimal invoiceAmountTotal) {
        this.invoiceAmountTotal = invoiceAmountTotal;
    }

    /**
     * 获取结算总金额
     *
     * @return settleAmountTotal - 结算总金额
     */
    public BigDecimal getSettleAmountTotal() {
        return settleAmountTotal;
    }

    /**
     * 设置结算总金额
     *
     * @param settleAmountTotal 结算总金额
     */
    public void setSettleAmountTotal(BigDecimal settleAmountTotal) {
        this.settleAmountTotal = settleAmountTotal;
    }

    /**
     * 获取结算金额
     *
     * @return settleAmount - 结算金额
     */
    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    /**
     * 设置结算金额
     *
     * @param settleAmount 结算金额
     */
    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    /**
     * 获取物料收货时间
     *
     * @return confirmTime - 物料收货时间
     */
    public Date getConfirmTime() {
        return confirmTime;
    }

    /**
     * 设置物料收货时间
     *
     * @param confirmTime 物料收货时间
     */
    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    /**
     * 获取结算时间
     *
     * @return settleTime - 结算时间
     */
    public Date getSettleTime() {
        return settleTime;
    }

    /**
     * 设置结算时间
     *
     * @param settleTime 结算时间
     */
    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
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
     * 获取外部系统单号
     *
     * @return otherSysNum - 外部系统单号
     */
    public String getOtherSysNum() {
        return otherSysNum;
    }

    /**
     * 设置外部系统单号
     *
     * @param otherSysNum 外部系统单号
     */
    public void setOtherSysNum(String otherSysNum) {
        this.otherSysNum = otherSysNum;
    }

    /**
     * @return partition
     */
    public Long getPartition() {
        return partition;
    }

    /**
     * @param partition
     */
    public void setPartition(Long partition) {
        this.partition = partition;
    }

    /**
     * @return dataAreaId
     */
    public String getDataAreaId() {
        return dataAreaId;
    }

    /**
     * @param dataAreaId
     */
    public void setDataAreaId(String dataAreaId) {
        this.dataAreaId = dataAreaId;
    }

    /**
     * 获取结算ID
     *
     * @return settleId - 结算ID
     */
    public Long getSettleId() {
        return settleId;
    }

    /**
     * 设置结算ID
     *
     * @param settleId 结算ID
     */
    public void setSettleId(Long settleId) {
        this.settleId = settleId;
    }
}