package com.dp.plat.pms.springmvc.entity;

import java.util.Map;
import com.dp.plat.core.entity.BaseEntity;
import java.util.Date;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class IndustryLeak extends BaseEntity {

    private Integer id;

    // 漏洞编号
    private String leakCode;

    // 漏洞名称
    private String leakName;

    // 漏洞类型
    private String leakType;

    // 漏洞级别
    private String leakLevel;

    // 漏洞描述
    private String leakDesc;

    // 漏洞原始数据
    private String leakSourceInfo;

    // 备注
    private String remark;

    // 状态
    private Integer status;

    // 入库状态
    private Integer trackStatus;

    // 入库时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date trackedTime;

    // 删除状态
    private Boolean disabled;

    // 关联的资产ID
    private String assetIds;

    // 自定义信息
    private Map customInfo;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

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
     * 获取漏洞编号
     *
     * @return leakCode - 漏洞编号
     */
    public String getLeakCode() {
        return leakCode;
    }

    /**
     * 设置漏洞编号
     *
     * @param leakCode 漏洞编号
     */
    public void setLeakCode(String leakCode) {
        this.leakCode = leakCode;
    }

    /**
     * 获取漏洞名称
     *
     * @return leakName - 漏洞名称
     */
    public String getLeakName() {
        return leakName;
    }

    /**
     * 设置漏洞名称
     *
     * @param leakName 漏洞名称
     */
    public void setLeakName(String leakName) {
        this.leakName = leakName;
    }

    /**
     * 获取漏洞类型
     *
     * @return leakType - 漏洞类型
     */
    public String getLeakType() {
        return leakType;
    }

    /**
     * 设置漏洞类型
     *
     * @param leakType 漏洞类型
     */
    public void setLeakType(String leakType) {
        this.leakType = leakType;
    }

    /**
     * 获取漏洞级别
     *
     * @return leakLevel - 漏洞级别
     */
    public String getLeakLevel() {
        return leakLevel;
    }

    /**
     * 设置漏洞级别
     *
     * @param leakLevel 漏洞级别
     */
    public void setLeakLevel(String leakLevel) {
        this.leakLevel = leakLevel;
    }

    /**
     * 获取漏洞描述
     *
     * @return leakDesc - 漏洞描述
     */
    public String getLeakDesc() {
        return leakDesc;
    }

    /**
     * 设置漏洞描述
     *
     * @param leakDesc 漏洞描述
     */
    public void setLeakDesc(String leakDesc) {
        this.leakDesc = leakDesc;
    }

    /**
     * 获取漏洞原始数据
     *
     * @return leakSourceInfo - 漏洞原始数据
     */
    public String getLeakSourceInfo() {
        return leakSourceInfo;
    }

    /**
     * 设置漏洞原始数据
     *
     * @param leakSourceInfo 漏洞原始数据
     */
    public void setLeakSourceInfo(String leakSourceInfo) {
        this.leakSourceInfo = leakSourceInfo;
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
     * @return status - 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取入库状态
     *
     * @return trackStatus - 入库状态
     */
    public Integer getTrackStatus() {
        return trackStatus;
    }

    /**
     * 设置入库状态
     *
     * @param trackStatus 入库状态
     */
    public void setTrackStatus(Integer trackStatus) {
        this.trackStatus = trackStatus;
    }

    /**
     * 获取入库时间
     *
     * @return trackedTime - 入库时间
     */
    public Date getTrackedTime() {
        return trackedTime;
    }

    /**
     * 设置入库时间
     *
     * @param trackedTime 入库时间
     */
    public void setTrackedTime(Date trackedTime) {
        this.trackedTime = trackedTime;
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
     * 获取关联的资产ID
     *
     * @return assetIds - 关联的资产ID
     */
    public String getAssetIds() {
        return assetIds;
    }

    /**
     * 设置关联的资产ID
     *
     * @param assetIds 关联的资产ID
     */
    public void setAssetIds(String assetIds) {
        this.assetIds = assetIds;
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
