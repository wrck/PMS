package com.dp.plat.data.bean;

import java.util.Date;

public class ProjectMaintenance {
    private Integer id;

    // 项目头信息主键
    private Integer projectId;

    // 项目名称
    private String projectCode;

    // 项目名称
    private String projectName;

    // 项目类型，售前:20/售后:10
    private Integer projectType;
    
    // 项目实施状态
    private String projectExecutionState;

    // 合同号
    private String contractNo;

    // 办事处编码
    private String officeCode;

    // 所属公司
    private String compId;

    // 任务性质
    private String type;

    // 任务分类
    private String category;

    // 任务小类
    private String subCategory;

    // 处理时间
    private Date processTime;

    // 事项描述
    private String processDesc;

    // 解决进展
    private String processStep;

    // 遗留问题
    private String remainProblem;

    // 在途耗时(h)
    private Float transitHour;

    // 处理耗时(h)
    private Float processHour;

    // 产品型号
    private String itemModel;

    // 在网版本
    private String softVersion;

    // 启用功能
    private String enabledFeatures;

    // 自定义主送
    private String customTos;

    // 自定义抄送
    private String customCcs;

    // 是否有巡检报告
    private Boolean hasReport;

    // 问卷ID
    private Integer quesnaireId;

    // 交付件，fnd_files id
    private String deliverFileIds;

    // 备注
    private String remark;

    // 创建时间
    private Date createTime;

    // 创建用户
    private String createBy;

    // 最新更新时间
    private Date updateTime;

    // 最新更新用户
    private String updateBy;
    
    // 维保状态
    private String warrantyStatus;
    
    // 行业
    private String industryName;
    
    // 用户办事处
    private String userOffice;
    
    // 所属年度
    private Integer year;

    // 所属季度
    private Integer quarter;

    // 所属月份
    private Integer month;

    // 当前维保服务次数
    private Integer wsCount;

    // 当前其他服务次数
    private Integer wafCount;

    // 维保服务年次数
    private Integer wsYearCount;

    // 其他服务年次数
    private Integer wafYearCount;

    // 维保信息
    private String warrantyInfo;

    // 其他服务信息
    private String serviceInfo;
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
     * 获取项目头信息主键
     *
     * @return projectId - 项目头信息主键
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 设置项目头信息主键
     *
     * @param projectId 项目头信息主键
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
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
     * 获取项目类型，售前:20/售后:10
     *
     * @return projectType - 项目类型，售前:20/售后:10
     */
    public Integer getProjectType() {
        return projectType;
    }

    /**
     * 设置项目类型，售前:20/售后:10
     *
     * @param projectType 项目类型，售前:20/售后:10
     */
    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    /**
     * 获取项目实施状态
     *
     * @return projectExecutionState - 项目实施状态
     */
    public String getProjectExecutionState() {
        return projectExecutionState;
    }

    /**
     * 设置项目实施状态
     *
     * @param projectExecutionState 项目实施状态
     */
    public void setProjectExecutionState(String projectExecutionState) {
        this.projectExecutionState = projectExecutionState;
    }

    /**
     * 获取合同号
     *
     * @return contractNo - 合同号
     */
    public String getContractNo() {
        return contractNo;
    }

    /**
     * 设置合同号
     *
     * @param contractNo 合同号
     */
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
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
     * 获取所属公司
     *
     * @return compId - 所属公司
     */
    public String getCompId() {
        return compId;
    }

    /**
     * 设置所属公司
     *
     * @param compId 所属公司
     */
    public void setCompId(String compId) {
        this.compId = compId;
    }

    /**
     * 获取任务性质
     *
     * @return type - 任务性质
     */
    public String getType() {
        return type;
    }

    /**
     * 设置任务性质
     *
     * @param type 任务性质
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取任务分类
     *
     * @return category - 任务分类
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置任务分类
     *
     * @param category 任务分类
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取任务小类
     *
     * @return subCategory - 任务小类
     */
    public String getSubCategory() {
        return subCategory;
    }

    /**
     * 设置任务小类
     *
     * @param subCategory 任务小类
     */
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    /**
     * 获取处理时间
     *
     * @return processTime - 处理时间
     */
    public Date getProcessTime() {
        return processTime;
    }

    /**
     * 设置处理时间
     *
     * @param processTime 处理时间
     */
    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    /**
     * 获取事项描述
     *
     * @return processDesc - 事项描述
     */
    public String getProcessDesc() {
        return processDesc;
    }

    /**
     * 设置事项描述
     *
     * @param processDesc 事项描述
     */
    public void setProcessDesc(String processDesc) {
        this.processDesc = processDesc;
    }

    /**
     * 获取解决进展
     *
     * @return processStep - 解决进展
     */
    public String getProcessStep() {
        return processStep;
    }

    /**
     * 设置解决进展
     *
     * @param processStep 解决进展
     */
    public void setProcessStep(String processStep) {
        this.processStep = processStep;
    }

    /**
     * 获取遗留问题
     *
     * @return remainProblem - 遗留问题
     */
    public String getRemainProblem() {
        return remainProblem;
    }

    /**
     * 设置遗留问题
     *
     * @param remainProblem 遗留问题
     */
    public void setRemainProblem(String remainProblem) {
        this.remainProblem = remainProblem;
    }

    /**
     * 获取在途耗时(h)
     *
     * @return transitHour - 在途耗时(h)
     */
    public Float getTransitHour() {
        return transitHour;
    }

    /**
     * 设置在途耗时(h)
     *
     * @param transitHour 在途耗时(h)
     */
    public void setTransitHour(Float transitHour) {
        this.transitHour = transitHour;
    }

    /**
     * 获取处理耗时(h)
     *
     * @return processHour - 处理耗时(h)
     */
    public Float getProcessHour() {
        return processHour;
    }

    /**
     * 设置处理耗时(h)
     *
     * @param processHour 处理耗时(h)
     */
    public void setProcessHour(Float processHour) {
        this.processHour = processHour;
    }

    /**
     * 获取产品型号
     *
     * @return itemModel - 产品型号
     */
    public String getItemModel() {
        return itemModel;
    }

    /**
     * 设置产品型号
     *
     * @param itemModel 产品型号
     */
    public void setItemModel(String itemModel) {
        this.itemModel = itemModel;
    }

    /**
     * 获取在网版本
     *
     * @return softVersion - 在网版本
     */
    public String getSoftVersion() {
        return softVersion;
    }

    /**
     * 设置在网版本
     *
     * @param softVersion 在网版本
     */
    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    /**
     * 获取启用功能
     *
     * @return enabledFeatures - 启用功能
     */
    public String getEnabledFeatures() {
        return enabledFeatures;
    }

    /**
     * 设置启用功能
     *
     * @param enabledFeatures 启用功能
     */
    public void setEnabledFeatures(String enabledFeatures) {
        this.enabledFeatures = enabledFeatures;
    }

    /**
     * 获取自定义主送
     *
     * @return customTos - 自定义主送
     */
    public String getCustomTos() {
        return customTos;
    }

    /**
     * 设置自定义主送
     *
     * @param customTos 自定义主送
     */
    public void setCustomTos(String customTos) {
        this.customTos = customTos;
    }

    /**
     * 获取自定义抄送
     *
     * @return customCcs - 自定义抄送
     */
    public String getCustomCcs() {
        return customCcs;
    }

    /**
     * 设置自定义抄送
     *
     * @param customCcs 自定义抄送
     */
    public void setCustomCcs(String customCcs) {
        this.customCcs = customCcs;
    }

    /**
     * 获取是否有巡检报告
     *
     * @return hasReport - 是否有巡检报告
     */
    public Boolean getHasReport() {
        return hasReport;
    }

    /**
     * 设置是否有巡检报告
     *
     * @param hasReport 是否有巡检报告
     */
    public void setHasReport(Boolean hasReport) {
        this.hasReport = hasReport;
    }

    /**
     * 获取问卷ID
     *
     * @return quesnaireId - 问卷ID
     */
    public Integer getQuesnaireId() {
        return quesnaireId;
    }

    /**
     * 设置问卷ID
     *
     * @param quesnaireId 问卷ID
     */
    public void setQuesnaireId(Integer quesnaireId) {
        this.quesnaireId = quesnaireId;
    }

    /**
     * 获取交付件，fnd_files id
     *
     * @return deliverFileIds - 交付件，fnd_files id
     */
    public String getDeliverFileIds() {
        return deliverFileIds;
    }

    /**
     * 设置交付件，fnd_files id
     *
     * @param deliverFileIds 交付件，fnd_files id
     */
    public void setDeliverFileIds(String deliverFileIds) {
        this.deliverFileIds = deliverFileIds;
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
     * 获取创建时间
     *
     * @return createTime - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取创建用户
     *
     * @return createBy - 创建用户
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 设置创建用户
     *
     * @param createBy 创建用户
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * 获取最新更新时间
     *
     * @return updateTime - 最新更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置最新更新时间
     *
     * @param updateTime 最新更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取最新更新用户
     *
     * @return updateBy - 最新更新用户
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * 设置最新更新用户
     *
     * @param updateBy 最新更新用户
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 获取维保状态
     * 
     * @return warrantyStatus - 维保状态
     */
    public String getWarrantyStatus() {
        return warrantyStatus;
    }

    /**
     * 设置维保状态
     * 
     * @param warrantyStatus 维保状态
     */
    public void setWarrantyStatus(String warrantyStatus) {
        this.warrantyStatus = warrantyStatus;
    }

    /**
     * 获取行业
     * 
     * @return industryName - 行业
     */
    public String getIndustryName() {
        return industryName;
    }

    /**
     * 设置行业
     * 
     * @param industryName 行业
     */
    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    /**
     * 获取用户办事处
     * 
     * @return userOffice - 用户办事处
     */
    public String getUserOffice() {
        return userOffice;
    }

    /**
     * 设置用户办事处
     * 
     * @param userOffice 用户办事处
     */
    public void setUserOffice(String userOffice) {
        this.userOffice = userOffice;
    }

    /**
     * 获取所属年度
     *
     * @return year - 所属年度
     */
    public Integer getYear() {
        return year;
    }

    /**
     * 设置所属年度
     *
     * @param year 所属年度
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * 获取所属季度
     *
     * @return quarter - 所属季度
     */
    public Integer getQuarter() {
        return quarter;
    }

    /**
     * 设置所属季度
     *
     * @param quarter 所属季度
     */
    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }

    /**
     * 获取所属月份
     *
     * @return month - 所属月份
     */
    public Integer getMonth() {
        return month;
    }

    /**
     * 设置所属月份
     *
     * @param month 所属月份
     */
    public void setMonth(Integer month) {
        this.month = month;
    }

    /**
     * 获取当前维保服务次数
     *
     * @return wsCount - 当前维保服务次数
     */
    public Integer getWsCount() {
        return wsCount;
    }

    /**
     * 设置当前维保服务次数
     *
     * @param wsCount 当前维保服务次数
     */
    public void setWsCount(Integer wsCount) {
        this.wsCount = wsCount;
    }

    /**
     * 获取当前其他服务次数
     *
     * @return wafCount - 当前其他服务次数
     */
    public Integer getWafCount() {
        return wafCount;
    }

    /**
     * 设置当前其他服务次数
     *
     * @param wafCount 当前其他服务次数
     */
    public void setWafCount(Integer wafCount) {
        this.wafCount = wafCount;
    }

    /**
     * 获取维保服务年次数
     *
     * @return wsYearCount - 维保服务年次数
     */
    public Integer getWsYearCount() {
        return wsYearCount;
    }

    /**
     * 设置维保服务年次数
     *
     * @param wsYearCount 维保服务年次数
     */
    public void setWsYearCount(Integer wsYearCount) {
        this.wsYearCount = wsYearCount;
    }

    /**
     * 获取其他服务年次数
     *
     * @return wafYearCount - 其他服务年次数
     */
    public Integer getWafYearCount() {
        return wafYearCount;
    }

    /**
     * 设置其他服务年次数
     *
     * @param wafYearCount 其他服务年次数
     */
    public void setWafYearCount(Integer wafYearCount) {
        this.wafYearCount = wafYearCount;
    }

    /**
     * 获取维保信息
     *
     * @return warrantyInfo - 维保信息
     */
    public String getWarrantyInfo() {
        return warrantyInfo;
    }

    /**
     * 设置维保信息
     *
     * @param warrantyInfo 维保信息
     */
    public void setWarrantyInfo(String warrantyInfo) {
        this.warrantyInfo = warrantyInfo;
    }

    /**
     * 获取其他服务信息
     *
     * @return serviceInfo - 其他服务信息
     */
    public String getServiceInfo() {
        return serviceInfo;
    }

    /**
     * 设置其他服务信息
     *
     * @param serviceInfo 其他服务信息
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}