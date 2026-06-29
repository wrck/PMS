package com.dp.plat.subcontract.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.dp.plat.data.bean.CustomInfoEntity;

public class SubcontractCallback extends CustomInfoEntity {
    private static final long serialVersionUID = 286030773173203083L;

    // 项目转包回访问卷表
    private Integer id;

    // 项目转包ID
    private Integer subcontractId;
    
    // 任务Key
    private String taskKey;

    // 任务ID
    private String taskId;

    // 问卷ID
    private Integer quesnaireId;

    // 问卷版本
    private Integer quesnaireVersion;

    // 状态 -1 草稿 1已提交
    private Integer quesnaireState;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
    private Date createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
    private Date effectiveFrom;

    @JsonFormat(pattern = "yyyy-MM-dd mm:HH:ss", locale = "zh", timezone = "GMT+8")
    private Date effectiveTo;

    /**
     * 获取项目转包回访问卷表
     *
     * @return id - 项目转包回访问卷表
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置项目转包回访问卷表
     *
     * @param id 项目转包回访问卷表
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取项目转包ID
     *
     * @return subcontractId - 项目转包ID
     */
    public Integer getSubcontractId() {
        return subcontractId;
    }

    /**
     * 设置项目转包ID
     *
     * @param subcontractId 项目转包ID
     */
    public void setSubcontractId(Integer subcontractId) {
        this.subcontractId = subcontractId;
    }
    
    /**
     * 获取任务Key
     *
     * @return taskKey - 任务Key
     */
    public String getTaskKey() {
        return taskKey;
    }

    /**
     * 设置任务Key
     *
     * @param taskKey 任务Key
     */
    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
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

    /**
     * @return effectiveFrom
     */
    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * @param effectiveFrom
     */
    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /**
     * @return effectiveTo
     */
    public Date getEffectiveTo() {
        return effectiveTo;
    }

    /**
     * @param effectiveTo
     */
    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
}