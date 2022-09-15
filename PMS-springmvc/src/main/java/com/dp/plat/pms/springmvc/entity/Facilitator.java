package com.dp.plat.pms.springmvc.entity;

import java.util.Date;
import java.util.Map;

import com.dp.plat.core.entity.BaseEntity;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Facilitator extends BaseEntity {
    private Integer id;

    // 服务商编号
    private String code;

    // 服务商账号
    private String account;

    // 服务商名
    private String name;

    // 合作类型
    private String type;

    // 开户行信息
    private String bankInfo;

    // 收款账户
    private String bankAccount;

    // 联行号
    private String cnapsCode;

    // 联系人
    private String contacts;

    // 联系电话
    private String tel;

    // 联系邮箱
    private String email;

    // 状态
    private Boolean state;

    // 是否评审
    private Boolean needApprove;

    // 审批结果
    private Integer approveStatus;

    // 附件材料
    private String deliveryIds;

    // 关联类型
    private String relateType;

    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveFrom;

    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveTo;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    // 自定义信息
    private Map customInfo;

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
     * 获取服务商编号
     *
     * @return code - 服务商编号
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置服务商编号
     *
     * @param code 服务商编号
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取服务商账号
     *
     * @return account - 服务商账号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置服务商账号
     *
     * @param account 服务商账号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取服务商名
     *
     * @return name - 服务商名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置服务商名
     *
     * @param name 服务商名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取合作类型
     *
     * @return type - 合作类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置合作类型
     *
     * @param type 合作类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取开户行信息
     *
     * @return bankInfo - 开户行信息
     */
    public String getBankInfo() {
        return bankInfo;
    }

    /**
     * 设置开户行信息
     *
     * @param bankInfo 开户行信息
     */
    public void setBankInfo(String bankInfo) {
        this.bankInfo = bankInfo;
    }

    /**
     * 获取收款账户
     *
     * @return bankAccount - 收款账户
     */
    public String getBankAccount() {
        return bankAccount;
    }

    /**
     * 设置收款账户
     *
     * @param bankAccount 收款账户
     */
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * 获取联行号
     *
     * @return cnapsCode - 联行号
     */
    public String getCnapsCode() {
        return cnapsCode;
    }

    /**
     * 设置联行号
     *
     * @param cnapsCode 联行号
     */
    public void setCnapsCode(String cnapsCode) {
        this.cnapsCode = cnapsCode;
    }

    /**
     * 获取联系人
     *
     * @return contacts - 联系人
     */
    public String getContacts() {
        return contacts;
    }

    /**
     * 设置联系人
     *
     * @param contacts 联系人
     */
    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    /**
     * 获取联系电话
     *
     * @return tel - 联系电话
     */
    public String getTel() {
        return tel;
    }

    /**
     * 设置联系电话
     *
     * @param tel 联系电话
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * 获取联系邮箱
     *
     * @return email - 联系邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置联系邮箱
     *
     * @param email 联系邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取状态
     *
     * @return state - 状态
     */
    public Boolean getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(Boolean state) {
        this.state = state;
    }

    /**
     * 获取是否评审
     *
     * @return needApprove - 是否评审
     */
    public Boolean getNeedApprove() {
        return needApprove;
    }

    /**
     * 设置是否评审
     *
     * @param needApprove 是否评审
     */
    public void setNeedApprove(Boolean needApprove) {
        this.needApprove = needApprove;
    }

    /**
     * 获取审批结果
     *
     * @return approveStatus - 审批结果
     */
    public Integer getApproveStatus() {
        return approveStatus;
    }

    /**
     * 设置审批结果
     *
     * @param approveStatus 审批结果
     */
    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    /**
     * 获取附件材料
     *
     * @return deliveryIds - 附件材料
     */
    public String getDeliveryIds() {
        return deliveryIds;
    }

    /**
     * 设置附件材料
     *
     * @param deliveryIds 附件材料
     */
    public void setDeliveryIds(String deliveryIds) {
        this.deliveryIds = deliveryIds;
    }

    /**
     * 获取关联类型
     *
     * @return relateType - 关联类型
     */
    public String getRelateType() {
        return relateType;
    }

    /**
     * 设置关联类型
     *
     * @param relateType 关联类型
     */
    public void setRelateType(String relateType) {
        this.relateType = relateType;
    }

    /**
     * @return effectiveFrom
     */
    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * @param effectiveFrom
     */
    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /**
     * @return effectiveTo
     */
    public Date getEffectiveTo() {
        return effectiveTo;
    }

    /**
     * @param effectiveTo
     */
    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
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

    /**
     * 获取自定义信息
     *
     * @return customInfo - 自定义信息
     */
    public Map getCustomInfo() {
        return customInfo;
    }

    /**
     * 设置自定义信息
     *
     * @param customInfo 自定义信息
     */
    public void setCustomInfo(Map customInfo) {
        this.customInfo = customInfo;
    }
}