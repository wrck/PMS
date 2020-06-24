package com.dp.plat.pms.springmvc.entity;

import java.util.Map;
import com.dp.plat.core.entity.BaseEntity;
import java.util.Date;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DailyReport extends BaseEntity {

    // 项目头信息主键
    private Integer projectId;

    // 项目类型，售前:20/售后:10
    private String projectType;

    // 项目名称
    private String projectCode;

    // 项目名称
    private String projectName;

    // 合同号
    private String contractNo;

    // 办事处编码
    private String officeCode;

    // 任务性质
    private String type;

    // 任务分类
    private String category;

    // 任务小类
    private String subCategory;

    // 处理时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date processTime;

    // 事项描述
    private String processDesc;

    // 解决进展
    private String processStep;

    // 遗留问题
    private String remainProblem;

    // 客户互动情况
    private String customerInteraction;

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

    // 项目实施状态
    private String projectExecutionState;

    // 是否有巡检报告
    private Boolean hasReport;

    // 问卷ID
    private Integer quesnaireId;

    // 交付件，fnd_files id
    private String deliverFileIds;

    // 备注
    private String remark;

    // 质量系数
    private Float qualityFactor;

    // 状态
    private String status;

    // 失效标记
    private Boolean disabled;

    // 自定义信息
    private Map customInfo;

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
     * 获取项目类型，售前:20/售后:10
     *
     * @return projectType - 项目类型，售前:20/售后:10
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * 设置项目类型，售前:20/售后:10
     *
     * @param projectType 项目类型，售前:20/售后:10
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
     * 获取客户互动情况
     *
     * @return customerInteraction - 客户互动情况
     */
    public String getCustomerInteraction() {
        return customerInteraction;
    }

    /**
     * 设置客户互动情况
     *
     * @param customerInteraction 客户互动情况
     */
    public void setCustomerInteraction(String customerInteraction) {
        this.customerInteraction = customerInteraction;
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
     * 获取质量系数
     *
     * @return qualityFactor - 质量系数
     */
    public Float getQualityFactor() {
        return qualityFactor;
    }

    /**
     * 设置质量系数
     *
     * @param qualityFactor 质量系数
     */
    public void setQualityFactor(Float qualityFactor) {
        this.qualityFactor = qualityFactor;
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
     * 获取失效标记
     *
     * @return disabled - 失效标记
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * 设置失效标记
     *
     * @param disabled 失效标记
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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
