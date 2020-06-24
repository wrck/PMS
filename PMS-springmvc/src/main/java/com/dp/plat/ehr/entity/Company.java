package com.dp.plat.ehr.entity;

import java.util.Date;

import javax.persistence.Id;

import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.ehr.annotation.TreeNodeParam;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@TreeNodeParam(fields = { "id:compID", "text:compName", "parentId:adminID" })
public class Company {

    // 公司ID，关联表外键
	@Id
    private Integer compID;

    // 公司编号
    private String compCode;

    // 公司名称
    private String compName;

    // 公司简称
    private String compAbbr;

    // 上级ID
    private Integer adminID;

    // 公司级别
    private Integer compGrade;

    // 公司类别
    private Integer compType;

    private Integer compArea;

    // 成立时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectDate;

    // 法人
    private String lawyer;

    // 地址
    private String address;

    // 注册地址
    private String regAddress;

    // 电话
    private String tel;

    // 传真
    private String fax;

    // 邮编
    private String postCode;

    // 网站
    private String webSite;

    // 失效状态
    private Boolean isDisabled;

    // 失效时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date disabledDate;

    // 备注
    private String remark;

    /**
     * 获取公司ID，关联表外键
     *
     * @return compID - 公司ID，关联表外键
     */
    public Integer getCompID() {
        return compID;
    }

    /**
     * 设置公司ID，关联表外键
     *
     * @param compID 公司ID，关联表外键
     */
    public void setCompID(Integer compID) {
        this.compID = compID;
    }

    /**
     * 获取公司编号
     *
     * @return compCode - 公司编号
     */
    public String getCompCode() {
        return compCode;
    }

    /**
     * 设置公司编号
     *
     * @param compCode 公司编号
     */
    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    /**
     * 获取公司名称
     *
     * @return compName - 公司名称
     */
    public String getCompName() {
        return compName;
    }

    /**
     * 设置公司名称
     *
     * @param compName 公司名称
     */
    public void setCompName(String compName) {
        this.compName = compName;
    }

    /**
     * 获取公司简称
     *
     * @return compAbbr - 公司简称
     */
    public String getCompAbbr() {
        return compAbbr;
    }

    /**
     * 设置公司简称
     *
     * @param compAbbr 公司简称
     */
    public void setCompAbbr(String compAbbr) {
        this.compAbbr = compAbbr;
    }

    /**
     * 获取上级ID
     *
     * @return adminID - 上级ID
     */
    public Integer getAdminID() {
        return adminID;
    }

    /**
     * 设置上级ID
     *
     * @param adminID 上级ID
     */
    public void setAdminID(Integer adminID) {
        this.adminID = adminID;
    }

    /**
     * 获取公司级别
     *
     * @return compGrade - 公司级别
     */
    public Integer getCompGrade() {
        return compGrade;
    }

    /**
     * 设置公司级别
     *
     * @param compGrade 公司级别
     */
    public void setCompGrade(Integer compGrade) {
        this.compGrade = compGrade;
    }

    /**
     * 获取公司类别
     *
     * @return compType - 公司类别
     */
    public Integer getCompType() {
        return compType;
    }

    /**
     * 设置公司类别
     *
     * @param compType 公司类别
     */
    public void setCompType(Integer compType) {
        this.compType = compType;
    }

    /**
     * @return compArea
     */
    public Integer getCompArea() {
        return compArea;
    }

    /**
     * @param compArea
     */
    public void setCompArea(Integer compArea) {
        this.compArea = compArea;
    }

    /**
     * 获取成立时间
     *
     * @return effectDate - 成立时间
     */
    public Date getEffectDate() {
        return effectDate;
    }

    /**
     * 设置成立时间
     *
     * @param effectDate 成立时间
     */
    public void setEffectDate(Date effectDate) {
        this.effectDate = effectDate;
    }

    /**
     * 获取法人
     *
     * @return lawyer - 法人
     */
    public String getLawyer() {
        return lawyer;
    }

    /**
     * 设置法人
     *
     * @param lawyer 法人
     */
    public void setLawyer(String lawyer) {
        this.lawyer = lawyer;
    }

    /**
     * 获取地址
     *
     * @return address - 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取注册地址
     *
     * @return regAddress - 注册地址
     */
    public String getRegAddress() {
        return regAddress;
    }

    /**
     * 设置注册地址
     *
     * @param regAddress 注册地址
     */
    public void setRegAddress(String regAddress) {
        this.regAddress = regAddress;
    }

    /**
     * 获取电话
     *
     * @return tel - 电话
     */
    public String getTel() {
        return tel;
    }

    /**
     * 设置电话
     *
     * @param tel 电话
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * 获取传真
     *
     * @return fax - 传真
     */
    public String getFax() {
        return fax;
    }

    /**
     * 设置传真
     *
     * @param fax 传真
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * 获取邮编
     *
     * @return postCode - 邮编
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * 设置邮编
     *
     * @param postCode 邮编
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * 获取网站
     *
     * @return webSite - 网站
     */
    public String getWebSite() {
        return webSite;
    }

    /**
     * 设置网站
     *
     * @param webSite 网站
     */
    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    /**
     * 获取失效状态
     *
     * @return isDisabled - 失效状态
     */
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    /**
     * 设置失效状态
     *
     * @param isDisabled 失效状态
     */
    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * 获取失效时间
     *
     * @return disabledDate - 失效时间
     */
    public Date getDisabledDate() {
        return disabledDate;
    }

    /**
     * 设置失效时间
     *
     * @param disabledDate 失效时间
     */
    public void setDisabledDate(Date disabledDate) {
        this.disabledDate = disabledDate;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
