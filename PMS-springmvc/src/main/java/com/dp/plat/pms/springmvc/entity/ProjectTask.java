package com.dp.plat.pms.springmvc.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ProjectTask extends com.dp.plat.data.bean.ProjectTask implements Serializable{

	private static final long serialVersionUID = 871491221546927014L;

	// 任务ID
    private Integer taskId;

    private Integer projectId;

    // 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data
    private String projectType;

    // 合同号
    private String contractNo;

    // 任务类型code，关联基础数据表
    private String taskTypeCode;

    // 任务类型id，关联基础数据表
    private String taskTypeId;

    // 任务名
    private String taskName;

    // 款项计划发生日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date eventPlanHappenDate;

    // 工程计划发生日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date eventPlanHappenDateENG;

    // 计划开始日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date planStartTime;

    // 计划结束日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date planEndTime;

    // 实际开始日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date actualStartTime;

    // 实际完成日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date eventActualFinishDate;

    // 优先级
    private String priority;

    // 进度百分比
    private Integer progress;

    // 进度描述
    private String progressDesc;

    // 状态
    private String status;

    // 父级任务
    private Integer parentId;

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

    // 是否可见，1表示可见，2表示不可见
    private String visibleFlag;

    // 上传的交付件
    private String deliverFileIds;

    // 自定义信息
    private Map customeInfo;

    // 备注
    private String remark;

    public ProjectTask() {
		super();
	}
    
	public ProjectTask(Integer projectId) {
		super();
		this.projectId = projectId;
	}

	public ProjectTask(Integer projectId, String projectType) {
		super();
		this.projectId = projectId;
		this.projectType = projectType;
	}

    /**
     * 获取任务ID
     *
     * @return taskId - 任务ID
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * 设置任务ID
     *
     * @param taskId 任务ID
     */
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    /**
     * @return projectId
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @param projectId
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取项目类型 默认售后项目10 售前测试20 详见fnd_basic_data
     *
     * @return projectType - 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * 设置项目类型 默认售后项目10 售前测试20 详见fnd_basic_data
     *
     * @param projectType 项目类型 默认售后项目10 售前测试20 详见fnd_basic_data
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
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
     * 获取任务类型code，关联基础数据表
     *
     * @return taskTypeCode - 任务类型code，关联基础数据表
     */
    public String getTaskTypeCode() {
        return taskTypeCode;
    }

    /**
     * 设置任务类型code，关联基础数据表
     *
     * @param taskTypeCode 任务类型code，关联基础数据表
     */
    public void setTaskTypeCode(String taskTypeCode) {
        this.taskTypeCode = taskTypeCode;
    }

    /**
     * 获取任务类型id，关联基础数据表
     *
     * @return taskTypeId - 任务类型id，关联基础数据表
     */
    public String getTaskTypeId() {
        return taskTypeId;
    }

    /**
     * 设置任务类型id，关联基础数据表
     *
     * @param taskTypeId 任务类型id，关联基础数据表
     */
    public void setTaskTypeId(String taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    /**
     * 获取任务名
     *
     * @return taskName - 任务名
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * 设置任务名
     *
     * @param taskName 任务名
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * 获取款项计划发生日期
     *
     * @return eventPlanHappenDate - 款项计划发生日期
     */
    public Date getEventPlanHappenDate() {
        return eventPlanHappenDate;
    }

    /**
     * 设置款项计划发生日期
     *
     * @param eventPlanHappenDate 款项计划发生日期
     */
    public void setEventPlanHappenDate(Date eventPlanHappenDate) {
        this.eventPlanHappenDate = eventPlanHappenDate;
    }

    /**
     * 获取工程计划发生日期
     *
     * @return eventPlanHappenDateENG - 工程计划发生日期
     */
    public Date getEventPlanHappenDateENG() {
        return eventPlanHappenDateENG;
    }

    /**
     * 设置工程计划发生日期
     *
     * @param eventPlanHappenDateENG 工程计划发生日期
     */
    public void setEventPlanHappenDateENG(Date eventPlanHappenDateENG) {
        this.eventPlanHappenDateENG = eventPlanHappenDateENG;
    }

    /**
     * 获取计划开始日期
     *
     * @return planStartTime - 计划开始日期
     */
    public Date getPlanStartTime() {
        return planStartTime;
    }

    /**
     * 设置计划开始日期
     *
     * @param planStartTime 计划开始日期
     */
    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    /**
     * 获取计划结束日期
     *
     * @return planEndTime - 计划结束日期
     */
    public Date getPlanEndTime() {
        return planEndTime;
    }

    /**
     * 设置计划结束日期
     *
     * @param planEndTime 计划结束日期
     */
    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    /**
     * 获取实际开始日期
     *
     * @return actualStartTime - 实际开始日期
     */
    public Date getActualStartTime() {
        return actualStartTime;
    }

    /**
     * 设置实际开始日期
     *
     * @param actualStartTime 实际开始日期
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    /**
     * 获取实际完成日期
     *
     * @return eventActualFinishDate - 实际完成日期
     */
    public Date getEventActualFinishDate() {
        return eventActualFinishDate;
    }

    /**
     * 设置实际完成日期
     *
     * @param eventActualFinishDate 实际完成日期
     */
    public void setEventActualFinishDate(Date eventActualFinishDate) {
        this.eventActualFinishDate = eventActualFinishDate;
    }

    /**
     * 获取优先级
     *
     * @return priority - 优先级
     */
    public String getPriority() {
        return priority;
    }

    /**
     * 设置优先级
     *
     * @param priority 优先级
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * 获取进度百分比
     *
     * @return progress - 进度百分比
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * 设置进度百分比
     *
     * @param progress 进度百分比
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * 获取进度描述
     *
     * @return progressDesc - 进度描述
     */
    public String getProgressDesc() {
        return progressDesc;
    }

    /**
     * 设置进度描述
     *
     * @param progressDesc 进度描述
     */
    public void setProgressDesc(String progressDesc) {
        this.progressDesc = progressDesc;
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
     * 获取父级任务
     *
     * @return parentId - 父级任务
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置父级任务
     *
     * @param parentId 父级任务
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
     * 获取是否可见，1表示可见，2表示不可见
     *
     * @return visibleFlag - 是否可见，1表示可见，2表示不可见
     */
    public String getVisibleFlag() {
        return visibleFlag;
    }

    /**
     * 设置是否可见，1表示可见，2表示不可见
     *
     * @param visibleFlag 是否可见，1表示可见，2表示不可见
     */
    public void setVisibleFlag(String visibleFlag) {
        this.visibleFlag = visibleFlag;
    }

    /**
     * 获取上传的交付件
     *
     * @return deliverFileIds - 上传的交付件
     */
    public String getDeliverFileIds() {
        return deliverFileIds;
    }

    /**
     * 设置上传的交付件
     *
     * @param deliverFileIds 上传的交付件
     */
    public void setDeliverFileIds(String deliverFileIds) {
        this.deliverFileIds = deliverFileIds;
    }

    /**
     * 获取自定义信息
     *
     * @return customeInfo - 自定义信息
     */
    public Map getCustomeInfo() {
        return customeInfo;
    }

    /**
     * 设置自定义信息
     *
     * @param customeInfo 自定义信息
     */
    public void setCustomeInfo(Map customeInfo) {
        this.customeInfo = customeInfo;
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
}
