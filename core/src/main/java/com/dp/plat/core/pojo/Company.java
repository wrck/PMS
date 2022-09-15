package com.dp.plat.core.pojo;

import java.util.Date;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Company {

    // 公司ID，关联表外键
    private Integer id;

    // 公司编号
    private String compCode;
    
    // 公司账套
    private String compAccount;

    // 公司名称
    private String compName;

    // 公司简称
    private String compAbbr;

    // 上级ID
    private Integer adminID;

    // 公司级别
    private Integer compGrade;

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
    private Boolean state;

    // 成立时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveFrom;

    // 结束时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveTo;

    // 失效时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date disabledTime;

    // 备注
    private String remark;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    /**
     * 获取公司ID，关联表外键
     *
     * @return id - 公司ID，关联表外键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置公司ID，关联表外键
     *
     * @param id 公司ID，关联表外键
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取公司账套
     *
     * @return compAccount - 公司账套
     */
    public String getCompAccount() {
        return compAccount;
    }

    /**
     * 设置公司账套
     *
     * @param compAccount 公司账套
     */
    public void setCompAccount(String compAccount) {
        this.compAccount = compAccount;
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
     * @return state - 失效状态
     */
    public Boolean getState() {
        return state;
    }

    /**
     * 设置失效状态
     *
     * @param state 失效状态
     */
    public void setState(Boolean state) {
        this.state = state;
    }

    /**
     * 获取成立时间
     *
     * @return effectiveFrom - 成立时间
     */
    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * 设置成立时间
     *
     * @param effectiveFrom 成立时间
     */
    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /**
     * 获取结束时间
     *
     * @return effectiveTo - 结束时间
     */
    public Date getEffectiveTo() {
        return effectiveTo;
    }

    /**
     * 设置结束时间
     *
     * @param effectiveTo 结束时间
     */
    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    /**
     * 获取失效时间
     *
     * @return disabledTime - 失效时间
     */
    public Date getDisabledTime() {
        return disabledTime;
    }

    /**
     * 设置失效时间
     *
     * @param disabledTime 失效时间
     */
    public void setDisabledTime(Date disabledTime) {
        this.disabledTime = disabledTime;
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
}
