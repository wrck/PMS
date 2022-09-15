package com.dp.plat.pms.extend.d365.entity;

import java.math.BigDecimal;

public class PurchaseReceiptLine extends BaseEntity {

    // 采购订单收货ID
    private Integer receiptId;

    // 采购订单号
    private String purchId;

    // 站点
    private String inventSiteId;

    // 仓库
    private String inventLocationId;

    // 库位
    private String wmsLocationId;

    // 批次号
    private String inventTransId;

    // 采购订单行号（与批次号二选一，有批次号按批次号收货）
    private String lineNum;

    // 收货数量
    private BigDecimal qty;

    // 收货单价
    private BigDecimal price;

    // 收货金额
    private BigDecimal amount;

    // 账套
    private String dataAreaId;

    /**
     * 获取采购订单收货ID
     *
     * @return receiptId - 采购订单收货ID
     */
    public Integer getReceiptId() {
        return receiptId;
    }

    /**
     * 设置采购订单收货ID
     *
     * @param receiptId 采购订单收货ID
     */
    public void setReceiptId(Integer receiptId) {
        this.receiptId = receiptId;
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
     * 获取站点
     *
     * @return inventSiteId - 站点
     */
    public String getInventSiteId() {
        return inventSiteId;
    }

    /**
     * 设置站点
     *
     * @param inventSiteId 站点
     */
    public void setInventSiteId(String inventSiteId) {
        this.inventSiteId = inventSiteId;
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
     * 获取库位
     *
     * @return wmsLocationId - 库位
     */
    public String getWmsLocationId() {
        return wmsLocationId;
    }

    /**
     * 设置库位
     *
     * @param wmsLocationId 库位
     */
    public void setWmsLocationId(String wmsLocationId) {
        this.wmsLocationId = wmsLocationId;
    }

    /**
     * 获取批次号
     *
     * @return inventTransId - 批次号
     */
    public String getInventTransId() {
        return inventTransId;
    }

    /**
     * 设置批次号
     *
     * @param inventTransId 批次号
     */
    public void setInventTransId(String inventTransId) {
        this.inventTransId = inventTransId;
    }

    /**
     * 获取采购订单行号（与批次号二选一，有批次号按批次号收货）
     *
     * @return lineNum - 采购订单行号（与批次号二选一，有批次号按批次号收货）
     */
    public String getLineNum() {
        return lineNum;
    }

    /**
     * 设置采购订单行号（与批次号二选一，有批次号按批次号收货）
     *
     * @param lineNum 采购订单行号（与批次号二选一，有批次号按批次号收货）
     */
    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    /**
     * 获取收货数量
     *
     * @return qty - 收货数量
     */
    public BigDecimal getQty() {
        return qty;
    }

    /**
     * 设置收货数量
     *
     * @param qty 收货数量
     */
    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    /**
     * 获取收货单价
     *
     * @return price - 收货单价
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置收货单价
     *
     * @param price 收货单价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取收货金额
     *
     * @return amount - 收货金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置收货金额
     *
     * @param amount 收货金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
