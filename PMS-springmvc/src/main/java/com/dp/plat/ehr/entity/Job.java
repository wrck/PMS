package com.dp.plat.ehr.entity;

import java.util.Date;

import javax.persistence.Id;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Job {

    // 岗位ID，关联表外键
	@Id
    private Integer jobID;

    // 岗位编码
    private String jobCode;

    // 岗位名称
    private String jobName;

    // 岗位简称
    private String jobAbbr;

    // 部门ID
    private Integer depID;

    // 上级ID
    private Integer adminID;

    // 岗位级别
    private Integer jobGrage;

    // 岗位类型
    private Integer jobType;

    // 岗位属性
    private Integer jobProperty;

    private Integer jobNum;

    private Boolean isCore;

    // 生效时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectDate;

    // 排序
    private String xorder;

    // 失效状态
    private Boolean isDisabled;

    // 失效时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date disabledDate;

    // 备注
    private String remark;

    private Integer xType;

    // 保留字段1
    private Integer jobCustom1;

    // 保留字段2
    private Integer jobCustom2;

    // 保留字段3
    private Integer jobCustom3;

    // 保留字段4
    private Integer jobCustom4;

    // 保留字段5
    private Integer jobCustom5;

    /**
     * 获取岗位ID，关联表外键
     *
     * @return jobID - 岗位ID，关联表外键
     */
    public Integer getJobID() {
        return jobID;
    }

    /**
     * 设置岗位ID，关联表外键
     *
     * @param jobID 岗位ID，关联表外键
     */
    public void setJobID(Integer jobID) {
        this.jobID = jobID;
    }

    /**
     * 获取岗位编码
     *
     * @return jobCode - 岗位编码
     */
    public String getJobCode() {
        return jobCode;
    }

    /**
     * 设置岗位编码
     *
     * @param jobCode 岗位编码
     */
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    /**
     * 获取岗位名称
     *
     * @return jobName - 岗位名称
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * 设置岗位名称
     *
     * @param jobName 岗位名称
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * 获取岗位简称
     *
     * @return jobAbbr - 岗位简称
     */
    public String getJobAbbr() {
        return jobAbbr;
    }

    /**
     * 设置岗位简称
     *
     * @param jobAbbr 岗位简称
     */
    public void setJobAbbr(String jobAbbr) {
        this.jobAbbr = jobAbbr;
    }

    /**
     * 获取部门ID
     *
     * @return depID - 部门ID
     */
    public Integer getDepID() {
        return depID;
    }

    /**
     * 设置部门ID
     *
     * @param depID 部门ID
     */
    public void setDepID(Integer depID) {
        this.depID = depID;
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
     * 获取岗位级别
     *
     * @return jobGrage - 岗位级别
     */
    public Integer getJobGrage() {
        return jobGrage;
    }

    /**
     * 设置岗位级别
     *
     * @param jobGrage 岗位级别
     */
    public void setJobGrage(Integer jobGrage) {
        this.jobGrage = jobGrage;
    }

    /**
     * 获取岗位类型
     *
     * @return jobType - 岗位类型
     */
    public Integer getJobType() {
        return jobType;
    }

    /**
     * 设置岗位类型
     *
     * @param jobType 岗位类型
     */
    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    /**
     * 获取岗位属性
     *
     * @return jobProperty - 岗位属性
     */
    public Integer getJobProperty() {
        return jobProperty;
    }

    /**
     * 设置岗位属性
     *
     * @param jobProperty 岗位属性
     */
    public void setJobProperty(Integer jobProperty) {
        this.jobProperty = jobProperty;
    }

    /**
     * @return jobNum
     */
    public Integer getJobNum() {
        return jobNum;
    }

    /**
     * @param jobNum
     */
    public void setJobNum(Integer jobNum) {
        this.jobNum = jobNum;
    }

    /**
     * @return isCore
     */
    public Boolean getIsCore() {
        return isCore;
    }

    /**
     * @param isCore
     */
    public void setIsCore(Boolean isCore) {
        this.isCore = isCore;
    }

    /**
     * 获取生效时间
     *
     * @return effectDate - 生效时间
     */
    public Date getEffectDate() {
        return effectDate;
    }

    /**
     * 设置生效时间
     *
     * @param effectDate 生效时间
     */
    public void setEffectDate(Date effectDate) {
        this.effectDate = effectDate;
    }

    /**
     * 获取排序
     *
     * @return xorder - 排序
     */
    public String getXorder() {
        return xorder;
    }

    /**
     * 设置排序
     *
     * @param xorder 排序
     */
    public void setXorder(String xorder) {
        this.xorder = xorder;
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

    /**
     * @return xType
     */
    public Integer getxType() {
        return xType;
    }

    /**
     * @param xType
     */
    public void setxType(Integer xType) {
        this.xType = xType;
    }

    /**
     * 获取保留字段1
     *
     * @return jobCustom1 - 保留字段1
     */
    public Integer getJobCustom1() {
        return jobCustom1;
    }

    /**
     * 设置保留字段1
     *
     * @param jobCustom1 保留字段1
     */
    public void setJobCustom1(Integer jobCustom1) {
        this.jobCustom1 = jobCustom1;
    }

    /**
     * 获取保留字段2
     *
     * @return jobCustom2 - 保留字段2
     */
    public Integer getJobCustom2() {
        return jobCustom2;
    }

    /**
     * 设置保留字段2
     *
     * @param jobCustom2 保留字段2
     */
    public void setJobCustom2(Integer jobCustom2) {
        this.jobCustom2 = jobCustom2;
    }

    /**
     * 获取保留字段3
     *
     * @return jobCustom3 - 保留字段3
     */
    public Integer getJobCustom3() {
        return jobCustom3;
    }

    /**
     * 设置保留字段3
     *
     * @param jobCustom3 保留字段3
     */
    public void setJobCustom3(Integer jobCustom3) {
        this.jobCustom3 = jobCustom3;
    }

    /**
     * 获取保留字段4
     *
     * @return jobCustom4 - 保留字段4
     */
    public Integer getJobCustom4() {
        return jobCustom4;
    }

    /**
     * 设置保留字段4
     *
     * @param jobCustom4 保留字段4
     */
    public void setJobCustom4(Integer jobCustom4) {
        this.jobCustom4 = jobCustom4;
    }

    /**
     * 获取保留字段5
     *
     * @return jobCustom5 - 保留字段5
     */
    public Integer getJobCustom5() {
        return jobCustom5;
    }

    /**
     * 设置保留字段5
     *
     * @param jobCustom5 保留字段5
     */
    public void setJobCustom5(Integer jobCustom5) {
        this.jobCustom5 = jobCustom5;
    }
}
