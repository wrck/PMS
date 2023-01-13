package com.dp.plat.pms.extend.d365.entity;

import java.math.BigDecimal;

public class PurchaseLine extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 采购订单HeaderId
    private Integer headerId;

    // 采购订单号
    private String purchId;

    // 采购订单行号（可指定）
    private String lineNum;

    // 物料编码
    private String itemId;

    // 采购数量
    private BigDecimal purchQty;

    // 采购价
    private BigDecimal purchPrice;

    // 税收组
    private String taxItemGroup;

    // 厂商型号（复用D365序列号字段）
    private String inventSerialId;

    // 站点
    private String inventSiteId;

    // 仓库
    private String inventLocationId;

    // 库位
    private String wmsLocationId;

    // 批次号
    private String inventTransId;

    // 办事处
    private String officeCode;

    // 交货日期
    private String deliveryDate;

    // 行备注
    private String remark;

    // 行多维度ID
    private String multiDimID;

    // 募投项目
    private String investmentProject;

    // 维度-银行账户
    private String dimBankAccount;

    // 维度-客户
    private String dimCustomer;

    // 维度-供应商
    private String dimVendor;

    // 维度-员工
    private String dimEmployee;

    // 维度-合同号
    private String dimContract;

    // 维度-部门
    private String dimDepartment;

    // 维度-BU
    private String dimBU;

    // 维度-产品线
    private String dimProductLine;

    // 维度-区域
    private String dimTerritory;

    // 维度-行业
    private String dimIndustry;

    // 维度-多维度ID
    private String dimMultiDimID;

    // 账套
    private String dataAreaId;

    /**
     * 获取采购订单HeaderId
     *
     * @return headerId - 采购订单HeaderId
     */
    public Integer getHeaderId() {
        return headerId;
    }

    /**
     * 设置采购订单HeaderId
     *
     * @param headerId 采购订单HeaderId
     */
    public void setHeaderId(Integer headerId) {
        this.headerId = headerId;
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
     * 获取采购订单行号（可指定）
     *
     * @return lineNum - 采购订单行号（可指定）
     */
    public String getLineNum() {
        return lineNum;
    }

    /**
     * 设置采购订单行号（可指定）
     *
     * @param lineNum 采购订单行号（可指定）
     */
    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    /**
     * 获取物料编码
     *
     * @return itemId - 物料编码
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * 设置物料编码
     *
     * @param itemId 物料编码
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取采购数量
     *
     * @return purchQty - 采购数量
     */
    public BigDecimal getPurchQty() {
        return purchQty;
    }

    /**
     * 设置采购数量
     *
     * @param purchQty 采购数量
     */
    public void setPurchQty(BigDecimal purchQty) {
        this.purchQty = purchQty;
    }

    /**
     * 获取采购价
     *
     * @return purchPrice - 采购价
     */
    public BigDecimal getPurchPrice() {
        return purchPrice;
    }

    /**
     * 设置采购价
     *
     * @param purchPrice 采购价
     */
    public void setPurchPrice(BigDecimal purchPrice) {
        this.purchPrice = purchPrice;
    }

    /**
     * 获取税收组
     *
     * @return taxItemGroup - 税收组
     */
    public String getTaxItemGroup() {
        return taxItemGroup;
    }

    /**
     * 设置税收组
     *
     * @param taxItemGroup 税收组
     */
    public void setTaxItemGroup(String taxItemGroup) {
        this.taxItemGroup = taxItemGroup;
    }

    /**
     * 获取厂商型号（复用D365序列号字段）
     *
     * @return inventSerialId - 厂商型号（复用D365序列号字段）
     */
    public String getInventSerialId() {
        return inventSerialId;
    }

    /**
     * 设置厂商型号（复用D365序列号字段）
     *
     * @param inventSerialId 厂商型号（复用D365序列号字段）
     */
    public void setInventSerialId(String inventSerialId) {
        this.inventSerialId = inventSerialId;
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
     * 获取办事处
     *
     * @return officeCode - 办事处
     */
    public String getOfficeCode() {
        return officeCode;
    }

    /**
     * 设置办事处
     *
     * @param officeCode 办事处
     */
    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
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
     * 获取行备注
     *
     * @return remark - 行备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置行备注
     *
     * @param remark 行备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取行多维度ID
     *
     * @return multiDimID - 行多维度ID
     */
    public String getMultiDimID() {
        return multiDimID;
    }

    /**
     * 设置行多维度ID
     *
     * @param multiDimID 行多维度ID
     */
    public void setMultiDimID(String multiDimID) {
        this.multiDimID = multiDimID;
    }

    /**
     * 获取募投项目
     *
     * @return investmentProject - 募投项目
     */
    public String getInvestmentProject() {
        return investmentProject;
    }

    /**
     * 设置募投项目
     *
     * @param investmentProject 募投项目
     */
    public void setInvestmentProject(String investmentProject) {
        this.investmentProject = investmentProject;
    }

    /**
     * 获取维度-银行账户
     *
     * @return dimBankAccount - 维度-银行账户
     */
    public String getDimBankAccount() {
        return dimBankAccount;
    }

    /**
     * 设置维度-银行账户
     *
     * @param dimBankAccount 维度-银行账户
     */
    public void setDimBankAccount(String dimBankAccount) {
        this.dimBankAccount = dimBankAccount;
    }

    /**
     * 获取维度-客户
     *
     * @return dimCustomer - 维度-客户
     */
    public String getDimCustomer() {
        return dimCustomer;
    }

    /**
     * 设置维度-客户
     *
     * @param dimCustomer 维度-客户
     */
    public void setDimCustomer(String dimCustomer) {
        this.dimCustomer = dimCustomer;
    }

    /**
     * 获取维度-供应商
     *
     * @return dimVendor - 维度-供应商
     */
    public String getDimVendor() {
        return dimVendor;
    }

    /**
     * 设置维度-供应商
     *
     * @param dimVendor 维度-供应商
     */
    public void setDimVendor(String dimVendor) {
        this.dimVendor = dimVendor;
    }

    /**
     * 获取维度-员工
     *
     * @return dimEmployee - 维度-员工
     */
    public String getDimEmployee() {
        return dimEmployee;
    }

    /**
     * 设置维度-员工
     *
     * @param dimEmployee 维度-员工
     */
    public void setDimEmployee(String dimEmployee) {
        this.dimEmployee = dimEmployee;
    }

    /**
     * 获取维度-合同号
     *
     * @return dimContract - 维度-合同号
     */
    public String getDimContract() {
        return dimContract;
    }

    /**
     * 设置维度-合同号
     *
     * @param dimContract 维度-合同号
     */
    public void setDimContract(String dimContract) {
        this.dimContract = dimContract;
    }

    /**
     * 获取维度-部门
     *
     * @return dimDepartment - 维度-部门
     */
    public String getDimDepartment() {
        return dimDepartment;
    }

    /**
     * 设置维度-部门
     *
     * @param dimDepartment 维度-部门
     */
    public void setDimDepartment(String dimDepartment) {
        this.dimDepartment = dimDepartment;
    }

    /**
     * 获取维度-BU
     *
     * @return dimBU - 维度-BU
     */
    public String getDimBU() {
        return dimBU;
    }

    /**
     * 设置维度-BU
     *
     * @param dimBU 维度-BU
     */
    public void setDimBU(String dimBU) {
        this.dimBU = dimBU;
    }

    /**
     * 获取维度-产品线
     *
     * @return dimProductLine - 维度-产品线
     */
    public String getDimProductLine() {
        return dimProductLine;
    }

    /**
     * 设置维度-产品线
     *
     * @param dimProductLine 维度-产品线
     */
    public void setDimProductLine(String dimProductLine) {
        this.dimProductLine = dimProductLine;
    }

    /**
     * 获取维度-区域
     *
     * @return dimTerritory - 维度-区域
     */
    public String getDimTerritory() {
        return dimTerritory;
    }

    /**
     * 设置维度-区域
     *
     * @param dimTerritory 维度-区域
     */
    public void setDimTerritory(String dimTerritory) {
        this.dimTerritory = dimTerritory;
    }

    /**
     * 获取维度-行业
     *
     * @return dimIndustry - 维度-行业
     */
    public String getDimIndustry() {
        return dimIndustry;
    }

    /**
     * 设置维度-行业
     *
     * @param dimIndustry 维度-行业
     */
    public void setDimIndustry(String dimIndustry) {
        this.dimIndustry = dimIndustry;
    }

    /**
     * 获取维度-多维度ID
     *
     * @return dimMultiDimID - 维度-多维度ID
     */
    public String getDimMultiDimID() {
        return dimMultiDimID;
    }

    /**
     * 设置维度-多维度ID
     *
     * @param dimMultiDimID 维度-多维度ID
     */
    public void setDimMultiDimID(String dimMultiDimID) {
        this.dimMultiDimID = dimMultiDimID;
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