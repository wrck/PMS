package com.dp.plat.pms.springmvc.entity;

import java.util.Date;
import java.util.Map;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ProjectHeader extends Project {

    // 项目头信息主键,跟项目其他具体信息关联
    private Integer projectId;

    // 项目类型，用服售后:10，安服售后:afss，安服先行:afxx
    private String projectType;

    // 项目名称
    private String projectCode;

    // 项目名称
    private String projectName;

    // 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态
    private String projectState;

    // 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪
    private String isback;

    // 办事处编码
    private String column001;

    // 客户编码--ERP
    private String column002;

    // 客户名称--ERP
    private String column003;

    // 市场部编码
    private String column004;

    // 系统部ID
    private String column005;

    // 拓展部ID
    private String column006;

    // 子行业ID
    private String column007;

    // 不予跟踪原因 notGrantTailCause
    private String column008;

    // 订单创建时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date column009;

    // 项目类型
    private String column010;

    // 项目分类
    private String column011;

    // 项目实施方式
    private String column012;

    // 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
    private Integer columno12_readonly;

    // 最终客户名称
    private String column013;

    // 回退说明
    private String column014;

    // 客户项目名称
    private String customerProjectName;

    // 销售类型
    private String salesType;

    // 公司ID
    private String compId;

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

    // 数据是否失效
    private Boolean disabled;

    // 项目开始实施时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date projectStartTime;

    // 项目相关数据最后编辑时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date projectRefreshTime;

    // 项目闭环时间点
    @JsonSerialize(using = JsonSerializer.class)
    private Date projectCloseTime;

//    // 自定义信息
//    private Map<?, ?> customInfo;

    // 自定义配置
    private Map<?, ?> customConfig;

    /**
     * 获取项目头信息主键,跟项目其他具体信息关联
     *
     * @return projectId - 项目头信息主键,跟项目其他具体信息关联
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 设置项目头信息主键,跟项目其他具体信息关联
     *
     * @param projectId 项目头信息主键,跟项目其他具体信息关联
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取项目类型，用服售后:10，安服售后:afss，安服先行:afxx
     *
     * @return projectType - 项目类型，用服售后:10，安服售后:afss，安服先行:afxx
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * 设置项目类型，用服售后:10，安服售后:afss，安服先行:afxx
     *
     * @param projectType 项目类型，用服售后:10，安服售后:afss，安服先行:afxx
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
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
     * 获取对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态
     *
     * @return projectState - 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态
     */
    public String getProjectState() {
        return projectState;
    }

    /**
     * 设置对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态
     *
     * @param projectState 对应项目阶段中的不同状态 ，默认1为初始创建状态，0为不予跟踪状态
     */
    public void setProjectState(String projectState) {
        this.projectState = projectState;
    }

    /**
     * 获取30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪
     *
     * @return isback - 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪
     */
    public String getIsback() {
        return isback;
    }

    /**
     * 设置30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪
     *
     * @param isback 30表示创建项目，32表示指定项目经理，34表示填写渠道信息 ,40表示工程管理部不予跟踪处理 ，42 表示项目经理选择不予跟踪
     */
    public void setIsback(String isback) {
        this.isback = isback;
    }

    /**
     * 获取办事处编码
     *
     * @return column001 - 办事处编码
     */
    public String getColumn001() {
        return column001;
    }

    /**
     * 设置办事处编码
     *
     * @param column001 办事处编码
     */
    public void setColumn001(String column001) {
        this.column001 = column001;
    }

    /**
     * 获取客户编码--ERP
     *
     * @return column002 - 客户编码--ERP
     */
    public String getColumn002() {
        return column002;
    }

    /**
     * 设置客户编码--ERP
     *
     * @param column002 客户编码--ERP
     */
    public void setColumn002(String column002) {
        this.column002 = column002;
    }

    /**
     * 获取客户名称--ERP
     *
     * @return column003 - 客户名称--ERP
     */
    public String getColumn003() {
        return column003;
    }

    /**
     * 设置客户名称--ERP
     *
     * @param column003 客户名称--ERP
     */
    public void setColumn003(String column003) {
        this.column003 = column003;
    }

    /**
     * 获取市场部编码
     *
     * @return column004 - 市场部编码
     */
    public String getColumn004() {
        return column004;
    }

    /**
     * 设置市场部编码
     *
     * @param column004 市场部编码
     */
    public void setColumn004(String column004) {
        this.column004 = column004;
    }

    /**
     * 获取系统部ID
     *
     * @return column005 - 系统部ID
     */
    public String getColumn005() {
        return column005;
    }

    /**
     * 设置系统部ID
     *
     * @param column005 系统部ID
     */
    public void setColumn005(String column005) {
        this.column005 = column005;
    }

    /**
     * 获取拓展部ID
     *
     * @return column006 - 拓展部ID
     */
    public String getColumn006() {
        return column006;
    }

    /**
     * 设置拓展部ID
     *
     * @param column006 拓展部ID
     */
    public void setColumn006(String column006) {
        this.column006 = column006;
    }

    /**
     * 获取子行业ID
     *
     * @return column007 - 子行业ID
     */
    public String getColumn007() {
        return column007;
    }

    /**
     * 设置子行业ID
     *
     * @param column007 子行业ID
     */
    public void setColumn007(String column007) {
        this.column007 = column007;
    }

    /**
     * 获取不予跟踪原因 notGrantTailCause
     *
     * @return column008 - 不予跟踪原因 notGrantTailCause
     */
    public String getColumn008() {
        return column008;
    }

    /**
     * 设置不予跟踪原因 notGrantTailCause
     *
     * @param column008 不予跟踪原因 notGrantTailCause
     */
    public void setColumn008(String column008) {
        this.column008 = column008;
    }

    /**
     * 获取订单创建时间
     *
     * @return column009 - 订单创建时间
     */
    public Date getColumn009() {
        return column009;
    }

    /**
     * 设置订单创建时间
     *
     * @param column009 订单创建时间
     */
    public void setColumn009(Date column009) {
        this.column009 = column009;
    }

    /**
     * 获取项目类型
     *
     * @return column010 - 项目类型
     */
    public String getColumn010() {
        return column010;
    }

    /**
     * 设置项目类型
     *
     * @param column010 项目类型
     */
    public void setColumn010(String column010) {
        this.column010 = column010;
    }

    /**
     * 获取项目分类
     *
     * @return column011 - 项目分类
     */
    public String getColumn011() {
        return column011;
    }

    /**
     * 设置项目分类
     *
     * @param column011 项目分类
     */
    public void setColumn011(String column011) {
        this.column011 = column011;
    }

    /**
     * 获取项目实施方式
     *
     * @return column012 - 项目实施方式
     */
    public String getColumn012() {
        return column012;
    }

    /**
     * 设置项目实施方式
     *
     * @param column012 项目实施方式
     */
    public void setColumn012(String column012) {
        this.column012 = column012;
    }

    /**
     * 获取项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
     *
     * @return columno12_readonly - 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
     */
    public Integer getColumno12_readonly() {
        return columno12_readonly;
    }

    /**
     * 设置项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
     *
     * @param columno12_readonly 项目实施方式是否可以修改 -1表示可以改 其他值表示readonly
     */
    public void setColumno12_readonly(Integer columno12_readonly) {
        this.columno12_readonly = columno12_readonly;
    }

    /**
     * 获取最终客户名称
     *
     * @return column013 - 最终客户名称
     */
    public String getColumn013() {
        return column013;
    }

    /**
     * 设置最终客户名称
     *
     * @param column013 最终客户名称
     */
    public void setColumn013(String column013) {
        this.column013 = column013;
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
    public String getCompId() {
        return compId;
    }

    /**
     * 设置公司ID
     *
     * @param compId 公司ID
     */
    public void setCompId(String compId) {
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
     * 获取数据是否失效
     *
     * @return disabled - 数据是否失效
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * 设置数据是否失效
     *
     * @param disabled 数据是否失效
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * 获取项目开始实施时间
     *
     * @return projectStartTime - 项目开始实施时间
     */
    public Date getProjectStartTime() {
        return projectStartTime;
    }

    /**
     * 设置项目开始实施时间
     *
     * @param projectStartTime 项目开始实施时间
     */
    public void setProjectStartTime(Date projectStartTime) {
        this.projectStartTime = projectStartTime;
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
     * 获取项目闭环时间点
     *
     * @return projectCloseTime - 项目闭环时间点
     */
    public Date getProjectCloseTime() {
        return projectCloseTime;
    }

    /**
     * 设置项目闭环时间点
     *
     * @param projectCloseTime 项目闭环时间点
     */
    public void setProjectCloseTime(Date projectCloseTime) {
        this.projectCloseTime = projectCloseTime;
    }

//    /**
//     * 获取自定义信息
//     *
//     * @return customInfo - 自定义信息
//     */
//    public Map<?, ?> getCustomInfo() {
//        return customInfo;
//    }
//
//    /**
//     * 设置自定义信息
//     *
//     * @param customInfo 自定义信息
//     */
//    public void setCustomInfo(Map<?, ?> customInfo) {
//        this.customInfo = customInfo;
//    }

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

    /**
     * 获取回退说明
     *
     * @return column014 - 回退说明
     */
    public String getColumn014() {
        return column014;
    }

    /**
     * 设置回退说明
     *
     * @param column014 回退说明
     */
    public void setColumn014(String column014) {
        this.column014 = column014;
    }
}
