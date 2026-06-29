package com.dp.plat.data.bean;

import java.util.Date;

public class ProjectSoftVersionEntity extends BaseCustomInfoBean {
    private static final long serialVersionUID = -7597759449171928749L;
    
    // 项目软件版本表
    private Integer id;

    // 项目ID
    private Integer projectId;

    // 软件版本变更记录
    private Integer logId;

    // 产品编码
    private String itemCode;

    // 设备序列号
    private String barCode;

    private String conp;

    // 版本类型
    private String conpType;

    // 版本系列
    private String conpSeries;

    // 软件版本掩码
    private String conpMark;

    // 备份变更之前的版本
    private String conpBak;

    // 0无更新 1有更新
    private Integer conpChange;

    private String cpld;

    private String cpldBak;

    private Integer cpldChange;

    private String boot;

    private String bootBak;

    private Integer bootChange;

    private String pcb;

    private String pcbBak;

    private Integer pcbChange;

    // 若有更新的情况下为执行更新时间，否则没有实际意义
    private Date executeTime;

    // 数据状态 0 失效 1 有效
    private Integer datastate;
    
    
    // 辅助字段
    private String projectCode;
    private String projectName;
    private String contractNo;
    private String officeCode;
    private String marketCode;
    private String systemCode;
    private String expendCode;
    private String industryCode;
    private String officeName;
    private String marketName;
    private String systemName;
    private String expendName;
    private String industryName;
    private String itemModel;
    private String itemName;
    private String receiveName;
    private String emsNum;
    private Date packdate;
    private String emsCompany;
    private String installAddress;

    // 母子公司发货存在一物双码，barCode2为系统发货记录序列号对应的条形码
    private String barCode2;
    private String itemCode2;
    private String itemModel2;
    private String itemName2;

    /**
     * 获取项目软件版本表
     *
     * @return id - 项目软件版本表
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置项目软件版本表
     *
     * @param id 项目软件版本表
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取项目ID
     *
     * @return projectId - 项目ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 设置项目ID
     *
     * @param projectId 项目ID
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取软件版本变更记录
     *
     * @return logId - 软件版本变更记录
     */
    public Integer getLogId() {
        return logId;
    }

    /**
     * 设置软件版本变更记录
     *
     * @param logId 软件版本变更记录
     */
    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    /**
     * 获取产品编码
     *
     * @return itemCode - 产品编码
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * 设置产品编码
     *
     * @param itemCode 产品编码
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * 获取设备序列号
     *
     * @return barcode - 设备序列号
     */
    public String getBarCode() {
        return barCode;
    }

    /**
     * 设置设备序列号
     *
     * @param barcode 设备序列号
     */
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    /**
     * @return conp
     */
    public String getConp() {
        return conp;
    }

    /**
     * @param conp
     */
    public void setConp(String conp) {
        this.conp = conp;
    }

    /**
     * 获取版本类型
     *
     * @return conpType - 版本类型
     */
    public String getConpType() {
        return conpType;
    }

    /**
     * 设置版本类型
     *
     * @param conpType 版本类型
     */
    public void setConpType(String conpType) {
        this.conpType = conpType;
    }

    /**
     * 获取版本系列
     *
     * @return conpSeries - 版本系列
     */
    public String getConpSeries() {
        return conpSeries;
    }

    /**
     * 设置版本系列
     *
     * @param conpSeries 版本系列
     */
    public void setConpSeries(String conpSeries) {
        this.conpSeries = conpSeries;
    }

    /**
     * 获取软件版本掩码
     *
     * @return conpMark - 软件版本掩码
     */
    public String getConpMark() {
        return conpMark;
    }

    /**
     * 设置软件版本掩码
     *
     * @param conpMark 软件版本掩码
     */
    public void setConpMark(String conpMark) {
        this.conpMark = conpMark;
    }

    /**
     * 获取备份变更之前的版本
     *
     * @return conpBak - 备份变更之前的版本
     */
    public String getConpBak() {
        return conpBak;
    }

    /**
     * 设置备份变更之前的版本
     *
     * @param conpBak 备份变更之前的版本
     */
    public void setConpBak(String conpBak) {
        this.conpBak = conpBak;
    }

    /**
     * 获取0无更新 1有更新
     *
     * @return conpChange - 0无更新 1有更新
     */
    public Integer getConpChange() {
        return conpChange;
    }

    /**
     * 设置0无更新 1有更新
     *
     * @param conpChange 0无更新 1有更新
     */
    public void setConpChange(Integer conpChange) {
        this.conpChange = conpChange;
    }

    /**
     * @return cpld
     */
    public String getCpld() {
        return cpld;
    }

    /**
     * @param cpld
     */
    public void setCpld(String cpld) {
        this.cpld = cpld;
    }

    /**
     * @return cpldBak
     */
    public String getCpldBak() {
        return cpldBak;
    }

    /**
     * @param cpldBak
     */
    public void setCpldBak(String cpldBak) {
        this.cpldBak = cpldBak;
    }

    /**
     * @return cpldChange
     */
    public Integer getCpldChange() {
        return cpldChange;
    }

    /**
     * @param cpldChange
     */
    public void setCpldChange(Integer cpldChange) {
        this.cpldChange = cpldChange;
    }

    /**
     * @return boot
     */
    public String getBoot() {
        return boot;
    }

    /**
     * @param boot
     */
    public void setBoot(String boot) {
        this.boot = boot;
    }

    /**
     * @return bootBak
     */
    public String getBootBak() {
        return bootBak;
    }

    /**
     * @param bootBak
     */
    public void setBootBak(String bootBak) {
        this.bootBak = bootBak;
    }

    /**
     * @return bootChange
     */
    public Integer getBootChange() {
        return bootChange;
    }

    /**
     * @param bootChange
     */
    public void setBootChange(Integer bootChange) {
        this.bootChange = bootChange;
    }

    /**
     * @return pcb
     */
    public String getPcb() {
        return pcb;
    }

    /**
     * @param pcb
     */
    public void setPcb(String pcb) {
        this.pcb = pcb;
    }

    /**
     * @return pcbBak
     */
    public String getPcbBak() {
        return pcbBak;
    }

    /**
     * @param pcbBak
     */
    public void setPcbBak(String pcbBak) {
        this.pcbBak = pcbBak;
    }

    /**
     * @return pcbChange
     */
    public Integer getPcbChange() {
        return pcbChange;
    }

    /**
     * @param pcbChange
     */
    public void setPcbChange(Integer pcbChange) {
        this.pcbChange = pcbChange;
    }

    /**
     * 获取若有更新的情况下为执行更新时间，否则没有实际意义
     *
     * @return executeTime - 若有更新的情况下为执行更新时间，否则没有实际意义
     */
    public Date getExecuteTime() {
        return executeTime;
    }

    /**
     * 设置若有更新的情况下为执行更新时间，否则没有实际意义
     *
     * @param executeTime 若有更新的情况下为执行更新时间，否则没有实际意义
     */
    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    /**
     * 获取数据状态 0 失效 1 有效
     *
     * @return datastate - 数据状态 0 失效 1 有效
     */
    public Integer getDatastate() {
        return datastate;
    }

    /**
     * 设置数据状态 0 失效 1 有效
     *
     * @param datastate 数据状态 0 失效 1 有效
     */
    public void setDatastate(Integer datastate) {
        this.datastate = datastate;
    }
    
    
    /* 辅助字段 */
    
    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }
    
    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getExpendCode() {
        return expendCode;
    }

    public void setExpendCode(String expendCode) {
        this.expendCode = expendCode;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getExpendName() {
        return expendName;
    }

    public void setExpendName(String expendName) {
        this.expendName = expendName;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getItemModel() {
        return itemModel;
    }

    public void setItemModel(String itemModel) {
        this.itemModel = itemModel;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getEmsNum() {
        return emsNum;
    }

    public void setEmsNum(String emsNum) {
        this.emsNum = emsNum;
    }

    public Date getPackdate() {
        return packdate;
    }

    public void setPackdate(Date packdate) {
        this.packdate = packdate;
    }

    public String getEmsCompany() {
        return emsCompany;
    }

    public void setEmsCompany(String emsCompany) {
        this.emsCompany = emsCompany;
    }

    public String getInstallAddress() {
        return installAddress;
    }

    public void setInstallAddress(String installAddress) {
        this.installAddress = installAddress;
    }

    public String getBarCode2() {
        return barCode2;
    }

    public void setBarCode2(String barCode2) {
        this.barCode2 = barCode2;
    }

    public String getItemCode2() {
        return itemCode2;
    }

    public void setItemCode2(String itemCode2) {
        this.itemCode2 = itemCode2;
    }

    public String getItemModel2() {
        return itemModel2;
    }

    public void setItemModel2(String itemModel2) {
        this.itemModel2 = itemModel2;
    }

    public String getItemName2() {
        return itemName2;
    }

    public void setItemName2(String itemName2) {
        this.itemName2 = itemName2;
    }

    
}