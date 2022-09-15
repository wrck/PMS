package com.dp.plat.pms.springmvc.entity;

import java.util.Date;
import java.util.Map;

import com.dp.plat.core.entity.BaseEntity;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DispatchSettlement extends BaseEntity {

    private Integer id;

    // 结算编号
    private String settleSeq;

    // 派单Id
    private Integer dispatchId;

    // 派单编号
    private String dispatchSeq;

    // 实施进展
    private String progressDesc;

    // 实施比例
    private Float progressRatio;

    // 验收进度
    private String acceptanceDesc;

    // 验收比例
    private String acceptanceRatio;

    // 此次付款比例
    private String ratio;

    // 此次付款金额
    private String amount;

    // 此次付款说明
    private String memo;

    // 提交时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date confirmTime;

    // 付款时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date paymentTime;

    // 备注
    private String remark;

    // 状态
    private Integer state;

    // sse报销单审批行ID
    private Integer sseId;

    // 结算年份
    private Integer year;

    // 结算季度
    private Integer quarter;

    // 结算月份
    private Integer month;

    // 自定义信息
    private Map<?, ?> customInfo;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    private String updateBy;

    // 删除标记
    private Boolean disabled;
    
    // 结算标记
    private Boolean settled;

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
     * 获取结算编号
     *
     * @return settleSeq - 结算编号
     */
    public String getSettleSeq() {
        return settleSeq;
    }

    /**
     * 设置结算编号
     *
     * @param settleSeq 结算编号
     */
    public void setSettleSeq(String settleSeq) {
        this.settleSeq = settleSeq;
    }

    /**
     * 获取派单Id
     *
     * @return dispatchId - 派单Id
     */
    public Integer getDispatchId() {
        return dispatchId;
    }

    /**
     * 设置派单Id
     *
     * @param dispatchId 派单Id
     */
    public void setDispatchId(Integer dispatchId) {
        this.dispatchId = dispatchId;
    }

    /**
     * 获取派单编号
     *
     * @return dispatchSeq - 派单编号
     */
    public String getDispatchSeq() {
        return dispatchSeq;
    }

    /**
     * 设置派单编号
     *
     * @param dispatchSeq 派单编号
     */
    public void setDispatchSeq(String dispatchSeq) {
        this.dispatchSeq = dispatchSeq;
    }

    /**
     * 获取实施进展
     *
     * @return progressDesc - 实施进展
     */
    public String getProgressDesc() {
        return progressDesc;
    }

    /**
     * 设置实施进展
     *
     * @param progressDesc 实施进展
     */
    public void setProgressDesc(String progressDesc) {
        this.progressDesc = progressDesc;
    }

    /**
     * 获取实施比例
     *
     * @return progressRatio - 实施比例
     */
    public Float getProgressRatio() {
        return progressRatio;
    }

    /**
     * 设置实施比例
     *
     * @param progressRatio 实施比例
     */
    public void setProgressRatio(Float progressRatio) {
        this.progressRatio = progressRatio;
    }

    /**
     * 获取验收进度
     *
     * @return acceptanceDesc - 验收进度
     */
    public String getAcceptanceDesc() {
        return acceptanceDesc;
    }

    /**
     * 设置验收进度
     *
     * @param acceptanceDesc 验收进度
     */
    public void setAcceptanceDesc(String acceptanceDesc) {
        this.acceptanceDesc = acceptanceDesc;
    }

    /**
     * 获取验收比例
     *
     * @return acceptanceRatio - 验收比例
     */
    public String getAcceptanceRatio() {
        return acceptanceRatio;
    }

    /**
     * 设置验收比例
     *
     * @param acceptanceRatio 验收比例
     */
    public void setAcceptanceRatio(String acceptanceRatio) {
        this.acceptanceRatio = acceptanceRatio;
    }

    /**
     * 获取此次付款比例
     *
     * @return ratio - 此次付款比例
     */
    public String getRatio() {
        return ratio;
    }

    /**
     * 设置此次付款比例
     *
     * @param ratio 此次付款比例
     */
    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    /**
     * 获取此次付款金额
     *
     * @return amount - 此次付款金额
     */
    public String getAmount() {
        return amount;
    }

    /**
     * 设置此次付款金额
     *
     * @param amount 此次付款金额
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * 获取此次付款说明
     *
     * @return memo - 此次付款说明
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置此次付款说明
     *
     * @param memo 此次付款说明
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 获取提交时间
     *
     * @return confirmTime - 提交时间
     */
    public Date getConfirmTime() {
        return confirmTime;
    }

    /**
     * 设置提交时间
     *
     * @param confirmTime 提交时间
     */
    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    /**
     * 获取付款时间
     *
     * @return paymentTime - 付款时间
     */
    public Date getPaymentTime() {
        return paymentTime;
    }

    /**
     * 设置付款时间
     *
     * @param paymentTime 付款时间
     */
    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
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
     * 获取状态
     *
     * @return state - 状态
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取sse报销单审批行ID
     *
     * @return sseId - sse报销单审批行ID
     */
    public Integer getSseId() {
        return sseId;
    }

    /**
     * 设置sse报销单审批行ID
     *
     * @param sseId sse报销单审批行ID
     */
    public void setSseId(Integer sseId) {
        this.sseId = sseId;
    }

    /**
     * 获取结算年份
     *
     * @return year - 结算年份
     */
    public Integer getYear() {
        return year;
    }

    /**
     * 设置结算年份
     *
     * @param year 结算年份
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * 获取结算季度
     *
     * @return quarter - 结算季度
     */
    public Integer getQuarter() {
        return quarter;
    }

    /**
     * 设置结算季度
     *
     * @param quarter 结算季度
     */
    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }

    /**
     * 获取结算月份
     *
     * @return month - 结算月份
     */
    public Integer getMonth() {
        return month;
    }

    /**
     * 设置结算月份
     *
     * @param month 结算月份
     */
    public void setMonth(Integer month) {
        this.month = month;
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
     * 获取删除标记
     *
     * @return disabled - 删除标记
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * 设置删除标记
     *
     * @param disabled 删除标记
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
    
    /**
     * 获取结算标记
     *
     * @return settled - 结算标记
     */
    public Boolean getSettled() {
        return settled;
    }

    /**
     * 设置结算标记
     *
     * @param settled 结算标记
     */
    public void setSettled(Boolean settled) {
        this.settled = settled;
    }
}
