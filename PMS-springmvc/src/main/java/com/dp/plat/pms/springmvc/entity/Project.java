package com.dp.plat.pms.springmvc.entity;

import java.util.Date;
import java.util.Map;

import com.dp.plat.core.entity.BaseEntity;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Project extends BaseEntity {

    // 项目头信息主键,跟项目其他具体信息关联
    private Integer id;

    // 项目名称
    private String projectCode;

    // 项目名称
    private String projectName;

    // 项目状态
    private String projectState;

    // 办事处编码
    private String officeCode;

    // 客户编码--ERP
    private String customerCode;

    // 客户名称--ERP
    private String customerName;

    // 市场部名称
    private String marketName;

    // 系统部名称
    private String systemName;

    // 拓展部名称
    private String expendName;

    // 子行业名称
    private String industryName;

    // 不予跟踪原因
    private String notGrantTailCause;

    // 订单创建时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date orderCreateTime;

    // 项目类型
    private String projectType;

    // 项目分类
    private String projectCategory;

    // 项目实施方式
    private String implType;

    // 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
    private Integer implReadonly;

    // 最终客户名称
    private String finalCustomerName;

    // 回退说明
    private String backReason;

    // 客户项目名称
    private String customerProjectName;

    // 销售类型
    private String salesType;

    // 公司ID
    private Integer compId;

    // 记录数据创建时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    // 记录数据创建用户
    private String createBy;

    // 记录数据最新更新时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    // 记录数据最新更新用户
    private String updateBy;

    // 数据有效性开始时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveFrom;

    // 数据有效性结束时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveTo;

    // 项目开始时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date projectStartTime;

    // 项目结束时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date projectEndTime;

    // 项目相关数据最后编辑时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date projectRefreshTime;

    // 自定义数据
    private Map<?, ?> customInfo;

    // 自定义配置
    private Map<?, ?> customConfig;

    /**
     * 获取项目头信息主键,跟项目其他具体信息关联
     *
     * @return id - 项目头信息主键,跟项目其他具体信息关联
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置项目头信息主键,跟项目其他具体信息关联
     *
     * @param id 项目头信息主键,跟项目其他具体信息关联
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取项目名称
     *
     * @return projectCode - 项目名称
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 设置项目名称
     *
     * @param projectCode 项目名称
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * 获取项目名称
     *
     * @return projectName - 项目名称
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * 设置项目名称
     *
     * @param projectName 项目名称
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * 获取项目状态
     *
     * @return projectState - 项目状态
     */
    public String getProjectState() {
        return projectState;
    }

    /**
     * 设置项目状态
     *
     * @param projectState 项目状态
     */
    public void setProjectState(String projectState) {
        this.projectState = projectState;
    }

    /**
     * 获取办事处编码
     *
     * @return officeCode - 办事处编码
     */
    public String getOfficeCode() {
        return officeCode;
    }

    /**
     * 设置办事处编码
     *
     * @param officeCode 办事处编码
     */
    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    /**
     * 获取客户编码--ERP
     *
     * @return customerCode - 客户编码--ERP
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * 设置客户编码--ERP
     *
     * @param customerCode 客户编码--ERP
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /**
     * 获取客户名称--ERP
     *
     * @return customerName - 客户名称--ERP
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 设置客户名称--ERP
     *
     * @param customerName 客户名称--ERP
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 获取市场部名称
     *
     * @return marketName - 市场部名称
     */
    public String getMarketName() {
        return marketName;
    }

    /**
     * 设置市场部名称
     *
     * @param marketName 市场部名称
     */
    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    /**
     * 获取系统部名称
     *
     * @return systemName - 系统部名称
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * 设置系统部名称
     *
     * @param systemName 系统部名称
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * 获取拓展部名称
     *
     * @return expendName - 拓展部名称
     */
    public String getExpendName() {
        return expendName;
    }

    /**
     * 设置拓展部名称
     *
     * @param expendName 拓展部名称
     */
    public void setExpendName(String expendName) {
        this.expendName = expendName;
    }

    /**
     * 获取子行业名称
     *
     * @return industryName - 子行业名称
     */
    public String getIndustryName() {
        return industryName;
    }

    /**
     * 设置子行业名称
     *
     * @param industryName 子行业名称
     */
    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    /**
     * 获取不予跟踪原因
     *
     * @return notGrantTailCause - 不予跟踪原因
     */
    public String getNotGrantTailCause() {
        return notGrantTailCause;
    }

    /**
     * 设置不予跟踪原因
     *
     * @param notGrantTailCause 不予跟踪原因
     */
    public void setNotGrantTailCause(String notGrantTailCause) {
        this.notGrantTailCause = notGrantTailCause;
    }

    /**
     * 获取订单创建时间
     *
     * @return orderCreateTime - 订单创建时间
     */
    public Date getOrderCreateTime() {
        return orderCreateTime;
    }

    /**
     * 设置订单创建时间
     *
     * @param orderCreateTime 订单创建时间
     */
    public void setOrderCreateTime(Date orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    /**
     * 获取项目类型
     *
     * @return projectType - 项目类型
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * 设置项目类型
     *
     * @param projectType 项目类型
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    /**
     * 获取项目分类
     *
     * @return projectCategory - 项目分类
     */
    public String getProjectCategory() {
        return projectCategory;
    }

    /**
     * 设置项目分类
     *
     * @param projectCategory 项目分类
     */
    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    /**
     * 获取项目实施方式
     *
     * @return implType - 项目实施方式
     */
    public String getImplType() {
        return implType;
    }

    /**
     * 设置项目实施方式
     *
     * @param implType 项目实施方式
     */
    public void setImplType(String implType) {
        this.implType = implType;
    }

    /**
     * 获取项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
     *
     * @return implReadonly - 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
     */
    public Integer getImplReadonly() {
        return implReadonly;
    }

    /**
     * 设置项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
     *
     * @param implReadonly 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
     */
    public void setImplReadonly(Integer implReadonly) {
        this.implReadonly = implReadonly;
    }

    /**
     * 获取最终客户名称
     *
     * @return finalCustomerName - 最终客户名称
     */
    public String getFinalCustomerName() {
        return finalCustomerName;
    }

    /**
     * 设置最终客户名称
     *
     * @param finalCustomerName 最终客户名称
     */
    public void setFinalCustomerName(String finalCustomerName) {
        this.finalCustomerName = finalCustomerName;
    }

    /**
     * 获取回退说明
     *
     * @return backReason - 回退说明
     */
    public String getBackReason() {
        return backReason;
    }

    /**
     * 设置回退说明
     *
     * @param backReason 回退说明
     */
    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }

    /**
     * 获取客户项目名称
     *
     * @return customerProjectName - 客户项目名称
     */
    public String getCustomerProjectName() {
        return customerProjectName;
    }

    /**
     * 设置客户项目名称
     *
     * @param customerProjectName 客户项目名称
     */
    public void setCustomerProjectName(String customerProjectName) {
        this.customerProjectName = customerProjectName;
    }

    /**
     * 获取销售类型
     *
     * @return salesType - 销售类型
     */
    public String getSalesType() {
        return salesType;
    }

    /**
     * 设置销售类型
     *
     * @param salesType 销售类型
     */
    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    /**
     * 获取公司ID
     *
     * @return compId - 公司ID
     */
    public Integer getCompId() {
        return compId;
    }

    /**
     * 设置公司ID
     *
     * @param compId 公司ID
     */
    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    /**
     * 获取记录数据创建时间
     *
     * @return createTime - 记录数据创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置记录数据创建时间
     *
     * @param createTime 记录数据创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取记录数据创建用户
     *
     * @return createBy - 记录数据创建用户
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 设置记录数据创建用户
     *
     * @param createBy 记录数据创建用户
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * 获取记录数据最新更新时间
     *
     * @return updateTime - 记录数据最新更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置记录数据最新更新时间
     *
     * @param updateTime 记录数据最新更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取记录数据最新更新用户
     *
     * @return updateBy - 记录数据最新更新用户
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * 设置记录数据最新更新用户
     *
     * @param updateBy 记录数据最新更新用户
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 获取数据有效性开始时间
     *
     * @return effectiveFrom - 数据有效性开始时间
     */
    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * 设置数据有效性开始时间
     *
     * @param effectiveFrom 数据有效性开始时间
     */
    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /**
     * 获取数据有效性结束时间
     *
     * @return effectiveTo - 数据有效性结束时间
     */
    public Date getEffectiveTo() {
        return effectiveTo;
    }

    /**
     * 设置数据有效性结束时间
     *
     * @param effectiveTo 数据有效性结束时间
     */
    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    /**
     * 获取项目开始时间
     *
     * @return projectStartTime - 项目开始时间
     */
    public Date getProjectStartTime() {
        return projectStartTime;
    }

    /**
     * 设置项目开始时间
     *
     * @param projectStartTime 项目开始时间
     */
    public void setProjectStartTime(Date projectStartTime) {
        this.projectStartTime = projectStartTime;
    }

    /**
     * 获取项目结束时间
     *
     * @return projectEndTime - 项目结束时间
     */
    public Date getProjectEndTime() {
        return projectEndTime;
    }

    /**
     * 设置项目结束时间
     *
     * @param projectEndTime 项目结束时间
     */
    public void setProjectEndTime(Date projectEndTime) {
        this.projectEndTime = projectEndTime;
    }

    /**
     * 获取项目相关数据最后编辑时间
     *
     * @return projectRefreshTime - 项目相关数据最后编辑时间
     */
    public Date getProjectRefreshTime() {
        return projectRefreshTime;
    }

    /**
     * 设置项目相关数据最后编辑时间
     *
     * @param projectRefreshTime 项目相关数据最后编辑时间
     */
    public void setProjectRefreshTime(Date projectRefreshTime) {
        this.projectRefreshTime = projectRefreshTime;
    }

    /**
     * 获取自定义数据
     *
     * @return customInfo - 自定义数据
     */
    public Map<?, ?> getCustomInfo() {
        return customInfo;
    }

    /**
     * 设置自定义数据
     *
     * @param customInfo 自定义数据
     */
    public void setCustomInfo(Map<?, ?> customInfo) {
        this.customInfo = customInfo;
    }

    /**
     * 获取自定义配置
     *
     * @return customConfig - 自定义配置
     */
    public Map<?, ?> getCustomConfig() {
        return customConfig;
    }

    /**
     * 设置自定义配置
     *
     * @param customConfig 自定义配置
     */
    public void setCustomConfig(Map<?, ?> customConfig) {
        this.customConfig = customConfig;
    }

}