package com.dp.plat.warrantyCallback.entity;

import java.util.Date;

import com.dp.plat.data.bean.CustomInfoEntity;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ProjectWarrantyCallback extends CustomInfoEntity {
    private static final long serialVersionUID = -1247085251106221811L;

    // 项目维保回访问卷表
    private Integer id;

    // 项目ID
    private Integer projectId;

    // 项目编码
    private String projectCode;

    // 办事处
    private String officeCode;

    // 合同号
    private String contractNos;

    // 关联的项目
    private String projectIds;

    // 项目名称
    private String projectName;

    // 实施方式
    private String serviceImpl;

    // 行业
    private String industryName;

    // 下单代理商
    private String agentChannel;

    // 最终客户单位
    private String finalCustomerName;

    // 客户联系人1
    private String customer1;

    // 客户联系方式1
    private String customerContact1;

    // 客户联系人2
    private String customer2;

    // 客户联系方式2
    private String customerContact2;

    // 维保开始日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date warrantyStartTime;

    // 维保结束日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date warrantyEndTime;

    // 续保意向,0:否,1:有,2:待定
    private Integer renewalIntention;

    // 回访时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date callbackTime;

    // 下次回访时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date nextCallbackTime;

    // 任务ID
    private String taskId;

    // 问卷ID
    private Integer quesnaireId;

    // 问卷版本
    private Integer quesnaireVersion;

    // 状态 -1 草稿 1已提交
    private Integer quesnaireState;

    // 删除标记
    private Boolean isDelete;

    // 备注
    private String remark;

    // 所属公司
    private Integer compId;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    /**
     * 获取项目维保回访问卷表
     *
     * @return id - 项目维保回访问卷表
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置项目维保回访问卷表
     *
     * @param id 项目维保回访问卷表
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取项目ID
     *
     * @return projectId - 项目ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 设置项目ID
     *
     * @param projectId 项目ID
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取项目编码
     *
     * @return projectCode - 项目编码
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 设置项目编码
     *
     * @param projectCode 项目编码
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * 获取办事处
     *
     * @return officeCode - 办事处
     */
    public String getOfficeCode() {
        return officeCode;
    }

    /**
     * 设置办事处
     *
     * @param officeCode 办事处
     */
    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    /**
     * 获取合同号
     *
     * @return contractNos - 合同号
     */
    public String getContractNos() {
        return contractNos;
    }

    /**
     * 设置合同号
     *
     * @param contractNos 合同号
     */
    public void setContractNos(String contractNos) {
        this.contractNos = contractNos;
    }

    /**
     * 获取关联的项目
     *
     * @return projectIds - 关联的项目
     */
    public String getProjectIds() {
        return projectIds;
    }

    /**
     * 设置关联的项目
     *
     * @param projectIds 关联的项目
     */
    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
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
     * 获取实施方式
     *
     * @return serviceImpl - 实施方式
     */
    public String getServiceImpl() {
        return serviceImpl;
    }

    /**
     * 设置实施方式
     *
     * @param serviceImpl 实施方式
     */
    public void setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
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
     * 获取下单代理商
     *
     * @return agentChannel - 下单代理商
     */
    public String getAgentChannel() {
        return agentChannel;
    }

    /**
     * 设置下单代理商
     *
     * @param agentChannel 下单代理商
     */
    public void setAgentChannel(String agentChannel) {
        this.agentChannel = agentChannel;
    }

    /**
     * 获取最终客户单位
     *
     * @return finalCustomerName - 最终客户单位
     */
    public String getFinalCustomerName() {
        return finalCustomerName;
    }

    /**
     * 设置最终客户单位
     *
     * @param finalCustomerName 最终客户单位
     */
    public void setFinalCustomerName(String finalCustomerName) {
        this.finalCustomerName = finalCustomerName;
    }

    /**
     * 获取客户联系人1
     *
     * @return customer1 - 客户联系人1
     */
    public String getCustomer1() {
        return customer1;
    }

    /**
     * 设置客户联系人1
     *
     * @param customer1 客户联系人1
     */
    public void setCustomer1(String customer1) {
        this.customer1 = customer1;
    }

    /**
     * 获取客户联系方式1
     *
     * @return customerContact1 - 客户联系方式1
     */
    public String getCustomerContact1() {
        return customerContact1;
    }

    /**
     * 设置客户联系方式1
     *
     * @param customerContact1 客户联系方式1
     */
    public void setCustomerContact1(String customerContact1) {
        this.customerContact1 = customerContact1;
    }

    /**
     * 获取客户联系人2
     *
     * @return customer2 - 客户联系人2
     */
    public String getCustomer2() {
        return customer2;
    }

    /**
     * 设置客户联系人2
     *
     * @param customer2 客户联系人2
     */
    public void setCustomer2(String customer2) {
        this.customer2 = customer2;
    }

    /**
     * 获取客户联系方式2
     *
     * @return customerContact2 - 客户联系方式2
     */
    public String getCustomerContact2() {
        return customerContact2;
    }

    /**
     * 设置客户联系方式2
     *
     * @param customerContact2 客户联系方式2
     */
    public void setCustomerContact2(String customerContact2) {
        this.customerContact2 = customerContact2;
    }

    /**
     * 获取维保开始日期
     *
     * @return warrantyStartTime - 维保开始日期
     */
    public Date getWarrantyStartTime() {
        return warrantyStartTime;
    }

    /**
     * 设置维保开始日期
     *
     * @param warrantyStartTime 维保开始日期
     */
    public void setWarrantyStartTime(Date warrantyStartTime) {
        this.warrantyStartTime = warrantyStartTime;
    }

    /**
     * 获取维保结束日期
     *
     * @return warrantyEndTime - 维保结束日期
     */
    public Date getWarrantyEndTime() {
        return warrantyEndTime;
    }

    /**
     * 设置维保结束日期
     *
     * @param warrantyEndTime 维保结束日期
     */
    public void setWarrantyEndTime(Date warrantyEndTime) {
        this.warrantyEndTime = warrantyEndTime;
    }

    /**
     * 获取续保意向,0:否,1:有,2:待定
     *
     * @return renewalIntention - 续保意向
     */
    public Integer getRenewalIntention() {
        return renewalIntention;
    }

    /**
     * 设置续保意向,0:否,1:有,2:待定
     *
     * @param renewalIntention 续保意向
     */
    public void setRenewalIntention(Integer renewalIntention) {
        this.renewalIntention = renewalIntention;
    }

    /**
     * 获取回访时间
     *
     * @return callbackTime - 回访时间
     */
    public Date getCallbackTime() {
        return callbackTime;
    }

    /**
     * 设置回访时间
     *
     * @param callbackTime 回访时间
     */
    public void setCallbackTime(Date callbackTime) {
        this.callbackTime = callbackTime;
    }

    /**
     * 获取下次回访时间
     *
     * @return nextCallbackTime - 下次回访时间
     */
    public Date getNextCallbackTime() {
        return nextCallbackTime;
    }

    /**
     * 设置下次回访时间
     *
     * @param nextCallbackTime 下次回访时间
     */
    public void setNextCallbackTime(Date nextCallbackTime) {
        this.nextCallbackTime = nextCallbackTime;
    }

    /**
     * 获取任务ID
     *
     * @return taskId - 任务ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * 设置任务ID
     *
     * @param taskId 任务ID
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
     * 获取问卷版本
     *
     * @return quesnaireVersion - 问卷版本
     */
    public Integer getQuesnaireVersion() {
        return quesnaireVersion;
    }

    /**
     * 设置问卷版本
     *
     * @param quesnaireVersion 问卷版本
     */
    public void setQuesnaireVersion(Integer quesnaireVersion) {
        this.quesnaireVersion = quesnaireVersion;
    }

    /**
     * 获取状态 -1 草稿 1已提交
     *
     * @return quesnaireState - 状态 -1 草稿 1已提交
     */
    public Integer getQuesnaireState() {
        return quesnaireState;
    }

    /**
     * 设置状态 -1 草稿 1已提交
     *
     * @param quesnaireState 状态 -1 草稿 1已提交
     */
    public void setQuesnaireState(Integer quesnaireState) {
        this.quesnaireState = quesnaireState;
    }

    /**
     * 获取删除标记
     *
     * @return isDelete - 删除标记
     */
    public Boolean getIsDelete() {
        return isDelete;
    }

    /**
     * 设置删除标记
     *
     * @param isDelete 删除标记
     */
    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
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
     * 获取所属公司
     *
     * @return compId - 所属公司
     */
    public Integer getCompId() {
        return compId;
    }

    /**
     * 设置所属公司
     *
     * @param compId 所属公司
     */
    public void setCompId(Integer compId) {
        this.compId = compId;
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