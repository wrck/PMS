package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.core.entity.BaseEntity;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.util.Date;

public class AfPrjProperty extends BaseEntity {
    private Integer id;

    private String orderExecNumber;

    private String projectName;

    private String marketCode;

    private String officeCode;

    private String expendId;

    private String marketName;

    private String officeName;

    private String systemName;

    private String salesManCode;

    private Integer systemId;

    private Integer industryId;

    private String salesManName;

    private String expendName;

    private String industryName;

    private String channelName;

    // 安全服务先行类借货有值，表示出货价
    private BigDecimal engineeFee;

    private Integer objId;

    private String applyType;

    // 公司编码
    private String corporationCode;

    private String finalCustomerName;

    private String pspm;

    private String pspmName;

    private String salesMenTel;

    private String decPath;

    @JsonSerialize(using = JsonSerializer.class)
    private Date requireInDate;

    private String receiveMen;

    private String reveiveContactWay;

    private String receiveAddress;

    private String projectType;

    private String projectCode;

    private byte[] serviceTypeName;

    private byte[] customerProjectName;

    private byte[] agentName;

    private String lendCause;

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
     * @return projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return marketCode
     */
    public String getMarketCode() {
        return marketCode;
    }

    /**
     * @param marketCode
     */
    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    /**
     * @return officeCode
     */
    public String getOfficeCode() {
        return officeCode;
    }

    /**
     * @param officeCode
     */
    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    /**
     * @return expendId
     */
    public String getExpendId() {
        return expendId;
    }

    /**
     * @param expendId
     */
    public void setExpendId(String expendId) {
        this.expendId = expendId;
    }

    /**
     * @return marketName
     */
    public String getMarketName() {
        return marketName;
    }

    /**
     * @param marketName
     */
    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    /**
     * @return officeName
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * @return systemName
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * @param systemName
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * @return salesManCode
     */
    public String getSalesManCode() {
        return salesManCode;
    }

    /**
     * @param salesManCode
     */
    public void setSalesManCode(String salesManCode) {
        this.salesManCode = salesManCode;
    }

    /**
     * @return systemId
     */
    public Integer getSystemId() {
        return systemId;
    }

    /**
     * @param systemId
     */
    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    /**
     * @return industryId
     */
    public Integer getIndustryId() {
        return industryId;
    }

    /**
     * @param industryId
     */
    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }

    /**
     * @return salesManName
     */
    public String getSalesManName() {
        return salesManName;
    }

    /**
     * @param salesManName
     */
    public void setSalesManName(String salesManName) {
        this.salesManName = salesManName;
    }

    /**
     * @return expendName
     */
    public String getExpendName() {
        return expendName;
    }

    /**
     * @param expendName
     */
    public void setExpendName(String expendName) {
        this.expendName = expendName;
    }

    /**
     * @return industryName
     */
    public String getIndustryName() {
        return industryName;
    }

    /**
     * @param industryName
     */
    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    /**
     * @return channelName
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * @param channelName
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * 获取安全服务先行类借货有值，表示出货价
     *
     * @return engineeFee - 安全服务先行类借货有值，表示出货价
     */
    public BigDecimal getEngineeFee() {
        return engineeFee;
    }

    /**
     * 设置安全服务先行类借货有值，表示出货价
     *
     * @param engineeFee 安全服务先行类借货有值，表示出货价
     */
    public void setEngineeFee(BigDecimal engineeFee) {
        this.engineeFee = engineeFee;
    }

    /**
     * @return objId
     */
    public Integer getObjId() {
        return objId;
    }

    /**
     * @param objId
     */
    public void setObjId(Integer objId) {
        this.objId = objId;
    }

    /**
     * @return applyType
     */
    public String getApplyType() {
        return applyType;
    }

    /**
     * @param applyType
     */
    public void setApplyType(String applyType) {
        this.applyType = applyType;
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
     * @return finalCustomerName
     */
    public String getFinalCustomerName() {
        return finalCustomerName;
    }

    /**
     * @param finalCustomerName
     */
    public void setFinalCustomerName(String finalCustomerName) {
        this.finalCustomerName = finalCustomerName;
    }

    /**
     * @return pspm
     */
    public String getPspm() {
        return pspm;
    }

    /**
     * @param pspm
     */
    public void setPspm(String pspm) {
        this.pspm = pspm;
    }

    /**
     * @return pspmName
     */
    public String getPspmName() {
        return pspmName;
    }

    /**
     * @param pspmName
     */
    public void setPspmName(String pspmName) {
        this.pspmName = pspmName;
    }

    /**
     * @return salesMenTel
     */
    public String getSalesMenTel() {
        return salesMenTel;
    }

    /**
     * @param salesMenTel
     */
    public void setSalesMenTel(String salesMenTel) {
        this.salesMenTel = salesMenTel;
    }

    /**
     * @return decPath
     */
    public String getDecPath() {
        return decPath;
    }

    /**
     * @param decPath
     */
    public void setDecPath(String decPath) {
        this.decPath = decPath;
    }

    /**
     * @return requireInDate
     */
    public Date getRequireInDate() {
        return requireInDate;
    }

    /**
     * @param requireInDate
     */
    public void setRequireInDate(Date requireInDate) {
        this.requireInDate = requireInDate;
    }

    /**
     * @return receiveMen
     */
    public String getReceiveMen() {
        return receiveMen;
    }

    /**
     * @param receiveMen
     */
    public void setReceiveMen(String receiveMen) {
        this.receiveMen = receiveMen;
    }

    /**
     * @return reveiveContactWay
     */
    public String getReveiveContactWay() {
        return reveiveContactWay;
    }

    /**
     * @param reveiveContactWay
     */
    public void setReveiveContactWay(String reveiveContactWay) {
        this.reveiveContactWay = reveiveContactWay;
    }

    /**
     * @return receiveAddress
     */
    public String getReceiveAddress() {
        return receiveAddress;
    }

    /**
     * @param receiveAddress
     */
    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    /**
     * @return projectType
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * @param projectType
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
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
     * @return serviceTypeName
     */
    public byte[] getServiceTypeName() {
        return serviceTypeName;
    }

    /**
     * @param serviceTypeName
     */
    public void setServiceTypeName(byte[] serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    /**
     * @return customerProjectName
     */
    public byte[] getCustomerProjectName() {
        return customerProjectName;
    }

    /**
     * @param customerProjectName
     */
    public void setCustomerProjectName(byte[] customerProjectName) {
        this.customerProjectName = customerProjectName;
    }

    /**
     * @return agentName
     */
    public byte[] getAgentName() {
        return agentName;
    }

    /**
     * @param agentName
     */
    public void setAgentName(byte[] agentName) {
        this.agentName = agentName;
    }

    /**
     * @return lendCause
     */
    public String getLendCause() {
        return lendCause;
    }

    /**
     * @param lendCause
     */
    public void setLendCause(String lendCause) {
        this.lendCause = lendCause;
    }
}