package com.dp.plat.pms.springmvc.entity;

import java.util.Map;
import com.dp.plat.core.entity.BaseEntity;
import java.util.Date;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DispatchProject extends BaseEntity {

    private Integer id;

    // 外派名称
    private String dispatchName;

    // 外派合同号
    private String dispatchNo;

    // 外派编号
    private String dispatchSeq;

    // 项目合同号
    private String contractNos;

    // 外派的项目ID
    private String projectIds;

    // 外派类型
    private String type;

    // 外派状态
    private Integer state;

    // 外派人数
    private Integer peopleNum;

    // 回访状态
    private Integer callbackState;

    // 服务商ID
    private Integer facilitatorId;

    // 服务商编码
    private String facilitatorCode;

    // 服务商名
    private String facilitatorName;

    // 服务商开户地址
    private String bankInfo;

    // 服务商收款账户
    private String bankAccount;

    // 办事处部门
    private String officeCode;

    // 收益部门
    private String profitDepCode;

    // 项目总接口人
    private String dutyPerson;

    // 办事处接口人
    private String officeDutyPerson;

    // 是否计提
    private Boolean isAccrued;

    // 是否提供发票
    private Boolean isInvoiced;

    // 外派价
    private String dispatchAmount;

    // 预付信息（比例、金额）
    private String prepaidInfo;

    // 预付遵循原则
    private String prepaidRule;

    // 验收要求
    private String acceptanceInfo;

    // 外派原因
    private String reason;

    // 备注
    private String remark;

    // 派单时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date dispatchTime;

    // SMS项目编码
    private String smsProjectCode;

    // SMS项目提交时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date smsSubmitTime;

    // SMS项目金额
    private String smsProjectAmount;

    // 有效开始时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveFrom;

    // 有效结束时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveTo;

    // 删除状态
    private Boolean disabled;

    // 派单状态
    private Boolean dispatched;

    // 结算状态
    private Boolean settled;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    // 自定义信息
    private Map<?, ?> customInfo;

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
     * 获取外派名称
     *
     * @return dispatchName - 外派名称
     */
    public String getDispatchName() {
        return dispatchName;
    }

    /**
     * 设置外派名称
     *
     * @param dispatchName 外派名称
     */
    public void setDispatchName(String dispatchName) {
        this.dispatchName = dispatchName;
    }

    /**
     * 获取外派合同号
     *
     * @return dispatchNo - 外派合同号
     */
    public String getDispatchNo() {
        return dispatchNo;
    }

    /**
     * 设置外派合同号
     *
     * @param dispatchNo 外派合同号
     */
    public void setDispatchNo(String dispatchNo) {
        this.dispatchNo = dispatchNo;
    }

    /**
     * 获取外派编号
     *
     * @return dispatchSeq - 外派编号
     */
    public String getDispatchSeq() {
        return dispatchSeq;
    }

    /**
     * 设置外派编号
     *
     * @param dispatchSeq 外派编号
     */
    public void setDispatchSeq(String dispatchSeq) {
        this.dispatchSeq = dispatchSeq;
    }

    /**
     * 获取项目合同号
     *
     * @return contractNos - 项目合同号
     */
    public String getContractNos() {
        return contractNos;
    }

    /**
     * 设置项目合同号
     *
     * @param contractNos 项目合同号
     */
    public void setContractNos(String contractNos) {
        this.contractNos = contractNos;
    }

    /**
     * 获取外派的项目ID
     *
     * @return projectIds - 外派的项目ID
     */
    public String getProjectIds() {
        return projectIds;
    }

    /**
     * 设置外派的项目ID
     *
     * @param projectIds 外派的项目ID
     */
    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
    }

    /**
     * 获取外派类型
     *
     * @return type - 外派类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置外派类型
     *
     * @param type 外派类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取外派状态
     *
     * @return state - 外派状态
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置外派状态
     *
     * @param state 外派状态
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取外派人数
     *
     * @return peopleNum - 外派人数
     */
    public Integer getPeopleNum() {
        return peopleNum;
    }

    /**
     * 设置外派人数
     *
     * @param peopleNum 外派人数
     */
    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    /**
     * 获取回访状态
     *
     * @return callbackState - 回访状态
     */
    public Integer getCallbackState() {
        return callbackState;
    }

    /**
     * 设置回访状态
     *
     * @param callbackState 回访状态
     */
    public void setCallbackState(Integer callbackState) {
        this.callbackState = callbackState;
    }

    /**
     * 获取服务商ID
     *
     * @return facilitatorId - 服务商ID
     */
    public Integer getFacilitatorId() {
        return facilitatorId;
    }

    /**
     * 设置服务商ID
     *
     * @param facilitatorId 服务商ID
     */
    public void setFacilitatorId(Integer facilitatorId) {
        this.facilitatorId = facilitatorId;
    }

    /**
     * 获取服务商编码
     *
     * @return facilitatorCode - 服务商编码
     */
    public String getFacilitatorCode() {
        return facilitatorCode;
    }

    /**
     * 设置服务商编码
     *
     * @param facilitatorCode 服务商编码
     */
    public void setFacilitatorCode(String facilitatorCode) {
        this.facilitatorCode = facilitatorCode;
    }

    /**
     * 获取服务商名
     *
     * @return facilitatorName - 服务商名
     */
    public String getFacilitatorName() {
        return facilitatorName;
    }

    /**
     * 设置服务商名
     *
     * @param facilitatorName 服务商名
     */
    public void setFacilitatorName(String facilitatorName) {
        this.facilitatorName = facilitatorName;
    }

    /**
     * 获取服务商开户地址
     *
     * @return bankInfo - 服务商开户地址
     */
    public String getBankInfo() {
        return bankInfo;
    }

    /**
     * 设置服务商开户地址
     *
     * @param bankInfo 服务商开户地址
     */
    public void setBankInfo(String bankInfo) {
        this.bankInfo = bankInfo;
    }

    /**
     * 获取服务商收款账户
     *
     * @return bankAccount - 服务商收款账户
     */
    public String getBankAccount() {
        return bankAccount;
    }

    /**
     * 设置服务商收款账户
     *
     * @param bankAccount 服务商收款账户
     */
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * 获取办事处部门
     *
     * @return officeCode - 办事处部门
     */
    public String getOfficeCode() {
        return officeCode;
    }

    /**
     * 设置办事处部门
     *
     * @param officeCode 办事处部门
     */
    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    /**
     * 获取收益部门
     *
     * @return profitDepCode - 收益部门
     */
    public String getProfitDepCode() {
        return profitDepCode;
    }

    /**
     * 设置收益部门
     *
     * @param profitDepCode 收益部门
     */
    public void setProfitDepCode(String profitDepCode) {
        this.profitDepCode = profitDepCode;
    }

    /**
     * 获取项目总接口人
     *
     * @return dutyPerson - 项目总接口人
     */
    public String getDutyPerson() {
        return dutyPerson;
    }

    /**
     * 设置项目总接口人
     *
     * @param dutyPerson 项目总接口人
     */
    public void setDutyPerson(String dutyPerson) {
        this.dutyPerson = dutyPerson;
    }

    /**
     * 获取办事处接口人
     *
     * @return officeDutyPerson - 办事处接口人
     */
    public String getOfficeDutyPerson() {
        return officeDutyPerson;
    }

    /**
     * 设置办事处接口人
     *
     * @param officeDutyPerson 办事处接口人
     */
    public void setOfficeDutyPerson(String officeDutyPerson) {
        this.officeDutyPerson = officeDutyPerson;
    }

    /**
     * 获取是否计提
     *
     * @return isAccrued - 是否计提
     */
    public Boolean getIsAccrued() {
        return isAccrued;
    }

    /**
     * 设置是否计提
     *
     * @param isAccrued 是否计提
     */
    public void setIsAccrued(Boolean isAccrued) {
        this.isAccrued = isAccrued;
    }

    /**
     * 获取是否提供发票
     *
     * @return isInvoiced - 是否提供发票
     */
    public Boolean getIsInvoiced() {
        return isInvoiced;
    }

    /**
     * 设置是否提供发票
     *
     * @param isInvoiced 是否提供发票
     */
    public void setIsInvoiced(Boolean isInvoiced) {
        this.isInvoiced = isInvoiced;
    }

    /**
     * 获取外派价
     *
     * @return dispatchAmount - 外派价
     */
    public String getDispatchAmount() {
        return dispatchAmount;
    }

    /**
     * 设置外派价
     *
     * @param dispatchAmount 外派价
     */
    public void setDispatchAmount(String dispatchAmount) {
        this.dispatchAmount = dispatchAmount;
    }

    /**
     * 获取预付信息（比例、金额）
     *
     * @return prepaidInfo - 预付信息（比例、金额）
     */
    public String getPrepaidInfo() {
        return prepaidInfo;
    }

    /**
     * 设置预付信息（比例、金额）
     *
     * @param prepaidInfo 预付信息（比例、金额）
     */
    public void setPrepaidInfo(String prepaidInfo) {
        this.prepaidInfo = prepaidInfo;
    }

    /**
     * 获取预付遵循原则
     *
     * @return prepaidRule - 预付遵循原则
     */
    public String getPrepaidRule() {
        return prepaidRule;
    }

    /**
     * 设置预付遵循原则
     *
     * @param prepaidRule 预付遵循原则
     */
    public void setPrepaidRule(String prepaidRule) {
        this.prepaidRule = prepaidRule;
    }

    /**
     * 获取验收要求
     *
     * @return acceptanceInfo - 验收要求
     */
    public String getAcceptanceInfo() {
        return acceptanceInfo;
    }

    /**
     * 设置验收要求
     *
     * @param acceptanceInfo 验收要求
     */
    public void setAcceptanceInfo(String acceptanceInfo) {
        this.acceptanceInfo = acceptanceInfo;
    }

    /**
     * 获取外派原因
     *
     * @return reason - 外派原因
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置外派原因
     *
     * @param reason 外派原因
     */
    public void setReason(String reason) {
        this.reason = reason;
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
     * 获取派单时间
     *
     * @return dispatchTime - 派单时间
     */
    public Date getDispatchTime() {
        return dispatchTime;
    }

    /**
     * 设置派单时间
     *
     * @param dispatchTime 派单时间
     */
    public void setDispatchTime(Date dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    /**
     * 获取SMS项目编码
     *
     * @return smsProjectCode - SMS项目编码
     */
    public String getSmsProjectCode() {
        return smsProjectCode;
    }

    /**
     * 设置SMS项目编码
     *
     * @param smsProjectCode SMS项目编码
     */
    public void setSmsProjectCode(String smsProjectCode) {
        this.smsProjectCode = smsProjectCode;
    }

    /**
     * 获取SMS项目提交时间
     *
     * @return smsSubmitTime - SMS项目提交时间
     */
    public Date getSmsSubmitTime() {
        return smsSubmitTime;
    }

    /**
     * 设置SMS项目提交时间
     *
     * @param smsSubmitTime SMS项目提交时间
     */
    public void setSmsSubmitTime(Date smsSubmitTime) {
        this.smsSubmitTime = smsSubmitTime;
    }

    /**
     * 获取SMS项目金额
     *
     * @return smsProjectAmount - SMS项目金额
     */
    public String getSmsProjectAmount() {
        return smsProjectAmount;
    }

    /**
     * 设置SMS项目金额
     *
     * @param smsProjectAmount SMS项目金额
     */
    public void setSmsProjectAmount(String smsProjectAmount) {
        this.smsProjectAmount = smsProjectAmount;
    }

    /**
     * 获取有效开始时间
     *
     * @return effectiveFrom - 有效开始时间
     */
    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * 设置有效开始时间
     *
     * @param effectiveFrom 有效开始时间
     */
    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /**
     * 获取有效结束时间
     *
     * @return effectiveTo - 有效结束时间
     */
    public Date getEffectiveTo() {
        return effectiveTo;
    }

    /**
     * 设置有效结束时间
     *
     * @param effectiveTo 有效结束时间
     */
    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    /**
     * 获取删除状态
     *
     * @return disabled - 删除状态
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * 设置删除状态
     *
     * @param disabled 删除状态
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * 获取派单状态
     *
     * @return dispatched - 派单状态
     */
    public Boolean getDispatched() {
        return dispatched;
    }

    /**
     * 设置派单状态
     *
     * @param dispatched 派单状态
     */
    public void setDispatched(Boolean dispatched) {
        this.dispatched = dispatched;
    }

    /**
     * 获取结算状态
     *
     * @return settled - 结算状态
     */
    public Boolean getSettled() {
        return settled;
    }

    /**
     * 设置结算状态
     *
     * @param settled 结算状态
     */
    public void setSettled(Boolean settled) {
        this.settled = settled;
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
