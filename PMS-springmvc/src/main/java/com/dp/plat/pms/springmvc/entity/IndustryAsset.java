package com.dp.plat.pms.springmvc.entity;

import java.util.Date;
import java.util.Map;

import com.dp.plat.core.entity.BaseEntity;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class IndustryAsset extends BaseEntity {
	
	private static final long serialVersionUID = -8207505075644971447L;

	private Integer id;

    // 资产编号
    private String assetNum;

    // 资产名称
    private String assetName;

    // 资产分类
    private String assetCategory;

    // 资产类型
    private String assetType;

    // IP/URL地址/域名
    private String assetHost;

    // 开放端口情况
    private String assetOpenPorts;

    // 部署应用情况
    private String assetDeployInfo;

    // 资产用途
    private String assetUsage;

    // 单位名称
    private String customerName;

    // 所属行业
    private String industryCode;

    // 应用系统
    private String assetAS;

    // 应用系统版本号
    private String assetASVersion;

    // 应用系统识别途径
    private String assetASIdentify;

    // 应用系统架构
    private String assetASFramework;

    // 中间件名称
    private String middlewareName;

    // 中间件版本
    private String middlewareVersion;

    // 开发商品牌
    private String developerBrand;

    // 操作系统
    private String assetOS;

    // 操作系统版本
    private String assetOSVersion;

    // 数据库类型
    private String assetDB;

    // 数据库版本
    private String assetDBVersion;

    // 自定义信息
    private Map customInfo;

    // 状态
    private String status;

    // 入库状态
    private Integer trackStatus;

    // 入库时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date trackedTime;

    // 删除状态
    private Boolean disabled;

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
     * 获取资产编号
     *
     * @return assetNum - 资产编号
     */
    public String getAssetNum() {
        return assetNum;
    }

    /**
     * 设置资产编号
     *
     * @param assetNum 资产编号
     */
    public void setAssetNum(String assetNum) {
        this.assetNum = assetNum;
    }

    /**
     * 获取资产名称
     *
     * @return assetName - 资产名称
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * 设置资产名称
     *
     * @param assetName 资产名称
     */
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    /**
     * 获取资产分类
     *
     * @return assetCategory - 资产分类
     */
    public String getAssetCategory() {
        return assetCategory;
    }

    /**
     * 设置资产分类
     *
     * @param assetCategory 资产分类
     */
    public void setAssetCategory(String assetCategory) {
        this.assetCategory = assetCategory;
    }

    /**
     * 获取资产类型
     *
     * @return assetType - 资产类型
     */
    public String getAssetType() {
        return assetType;
    }

    /**
     * 设置资产类型
     *
     * @param assetType 资产类型
     */
    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    /**
     * 获取IP/URL地址/域名
     *
     * @return assetHost - IP/URL地址/域名
     */
    public String getAssetHost() {
        return assetHost;
    }

    /**
     * 设置IP/URL地址/域名
     *
     * @param assetHost IP/URL地址/域名
     */
    public void setAssetHost(String assetHost) {
        this.assetHost = assetHost;
    }

    /**
     * 获取开放端口情况
     *
     * @return assetOpenPorts - 开放端口情况
     */
    public String getAssetOpenPorts() {
        return assetOpenPorts;
    }

    /**
     * 设置开放端口情况
     *
     * @param assetOpenPorts 开放端口情况
     */
    public void setAssetOpenPorts(String assetOpenPorts) {
        this.assetOpenPorts = assetOpenPorts;
    }

    /**
     * 获取部署应用情况
     *
     * @return assetDeployInfo - 部署应用情况
     */
    public String getAssetDeployInfo() {
        return assetDeployInfo;
    }

    /**
     * 设置部署应用情况
     *
     * @param assetDeployInfo 部署应用情况
     */
    public void setAssetDeployInfo(String assetDeployInfo) {
        this.assetDeployInfo = assetDeployInfo;
    }

    /**
     * 获取资产用途
     *
     * @return assetUsage - 资产用途
     */
    public String getAssetUsage() {
        return assetUsage;
    }

    /**
     * 设置资产用途
     *
     * @param assetUsage 资产用途
     */
    public void setAssetUsage(String assetUsage) {
        this.assetUsage = assetUsage;
    }

    /**
     * 获取单位名称
     *
     * @return customerName - 单位名称
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 设置单位名称
     *
     * @param customerName 单位名称
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 获取所属行业
     *
     * @return industryCode - 所属行业
     */
    public String getIndustryCode() {
        return industryCode;
    }

    /**
     * 设置所属行业
     *
     * @param industryCode 所属行业
     */
    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    /**
     * 获取应用系统
     *
     * @return assetAS - 应用系统
     */
    public String getAssetAS() {
        return assetAS;
    }

    /**
     * 设置应用系统
     *
     * @param assetAS 应用系统
     */
    public void setAssetAS(String assetAS) {
        this.assetAS = assetAS;
    }

    /**
     * 获取应用系统版本号
     *
     * @return assetASVersion - 应用系统版本号
     */
    public String getAssetASVersion() {
        return assetASVersion;
    }

    /**
     * 设置应用系统版本号
     *
     * @param assetASVersion 应用系统版本号
     */
    public void setAssetASVersion(String assetASVersion) {
        this.assetASVersion = assetASVersion;
    }

    /**
     * 获取应用系统识别途径
     *
     * @return assetASIdentify - 应用系统识别途径
     */
    public String getAssetASIdentify() {
        return assetASIdentify;
    }

    /**
     * 设置应用系统识别途径
     *
     * @param assetASIdentify 应用系统识别途径
     */
    public void setAssetASIdentify(String assetASIdentify) {
        this.assetASIdentify = assetASIdentify;
    }

    /**
     * 获取应用系统架构
     *
     * @return assetASFramework - 应用系统架构
     */
    public String getAssetASFramework() {
        return assetASFramework;
    }

    /**
     * 设置应用系统架构
     *
     * @param assetASFramework 应用系统架构
     */
    public void setAssetASFramework(String assetASFramework) {
        this.assetASFramework = assetASFramework;
    }

    /**
     * 获取中间件名称
     *
     * @return middlewareName - 中间件名称
     */
    public String getMiddlewareName() {
        return middlewareName;
    }

    /**
     * 设置中间件名称
     *
     * @param middlewareName 中间件名称
     */
    public void setMiddlewareName(String middlewareName) {
        this.middlewareName = middlewareName;
    }

    /**
     * 获取中间件版本
     *
     * @return middlewareVersion - 中间件版本
     */
    public String getMiddlewareVersion() {
        return middlewareVersion;
    }

    /**
     * 设置中间件版本
     *
     * @param middlewareVersion 中间件版本
     */
    public void setMiddlewareVersion(String middlewareVersion) {
        this.middlewareVersion = middlewareVersion;
    }

    /**
     * 获取开发商品牌
     *
     * @return developerBrand - 开发商品牌
     */
    public String getDeveloperBrand() {
        return developerBrand;
    }

    /**
     * 设置开发商品牌
     *
     * @param developerBrand 开发商品牌
     */
    public void setDeveloperBrand(String developerBrand) {
        this.developerBrand = developerBrand;
    }

    /**
     * 获取操作系统
     *
     * @return assetOS - 操作系统
     */
    public String getAssetOS() {
        return assetOS;
    }

    /**
     * 设置操作系统
     *
     * @param assetOS 操作系统
     */
    public void setAssetOS(String assetOS) {
        this.assetOS = assetOS;
    }

    /**
     * 获取操作系统版本
     *
     * @return assetOSVersion - 操作系统版本
     */
    public String getAssetOSVersion() {
        return assetOSVersion;
    }

    /**
     * 设置操作系统版本
     *
     * @param assetOSVersion 操作系统版本
     */
    public void setAssetOSVersion(String assetOSVersion) {
        this.assetOSVersion = assetOSVersion;
    }

    /**
     * 获取数据库类型
     *
     * @return assetDB - 数据库类型
     */
    public String getAssetDB() {
        return assetDB;
    }

    /**
     * 设置数据库类型
     *
     * @param assetDB 数据库类型
     */
    public void setAssetDB(String assetDB) {
        this.assetDB = assetDB;
    }

    /**
     * 获取数据库版本
     *
     * @return assetDBVersion - 数据库版本
     */
    public String getAssetDBVersion() {
        return assetDBVersion;
    }

    /**
     * 设置数据库版本
     *
     * @param assetDBVersion 数据库版本
     */
    public void setAssetDBVersion(String assetDBVersion) {
        this.assetDBVersion = assetDBVersion;
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
     * 获取状态
     *
     * @return status - 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(String status) {
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