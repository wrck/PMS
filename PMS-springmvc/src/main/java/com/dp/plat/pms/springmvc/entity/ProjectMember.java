package com.dp.plat.pms.springmvc.entity;

import com.dp.plat.core.entity.BaseEntity;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;

public class ProjectMember extends BaseEntity {
    private Integer id;

    private Integer projectId;

    // 项目类型 售后10 或售前 20 详见fnd_basic_data
    private Integer projectType;

    // 人员在项目中所处的角色
    private String memberRole;

    // 人员编码,外部人员为空
    private String memberCode;

    // 人员名称
    private String memberName;

    // 电话
    private String phoneNum;

    // 邮箱
    private String email;

    // 信息来源，1表示来源于项目信息，2表示来源于成员信息
    private String fromFlag;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    private String updateBy;

    // 有效结束时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveTo;

    // 有效开始时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveFrom;

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
     * 获取项目类型 售后10 或售前 20 详见fnd_basic_data
     *
     * @return projectType - 项目类型 售后10 或售前 20 详见fnd_basic_data
     */
    public Integer getProjectType() {
        return projectType;
    }

    /**
     * 设置项目类型 售后10 或售前 20 详见fnd_basic_data
     *
     * @param projectType 项目类型 售后10 或售前 20 详见fnd_basic_data
     */
    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    /**
     * 获取人员在项目中所处的角色
     *
     * @return memberRole - 人员在项目中所处的角色
     */
    public String getMemberRole() {
        return memberRole;
    }

    /**
     * 设置人员在项目中所处的角色
     *
     * @param memberRole 人员在项目中所处的角色
     */
    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    /**
     * 获取人员编码,外部人员为空
     *
     * @return memberCode - 人员编码,外部人员为空
     */
    public String getMemberCode() {
        return memberCode;
    }

    /**
     * 设置人员编码,外部人员为空
     *
     * @param memberCode 人员编码,外部人员为空
     */
    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    /**
     * 获取人员名称
     *
     * @return memberName - 人员名称
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * 设置人员名称
     *
     * @param memberName 人员名称
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    /**
     * 获取电话
     *
     * @return phoneNum - 电话
     */
    public String getPhoneNum() {
        return phoneNum;
    }

    /**
     * 设置电话
     *
     * @param phoneNum 电话
     */
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    /**
     * 获取邮箱
     *
     * @return email - 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取信息来源，1表示来源于项目信息，2表示来源于成员信息
     *
     * @return fromFlag - 信息来源，1表示来源于项目信息，2表示来源于成员信息
     */
    public String getFromFlag() {
        return fromFlag;
    }

    /**
     * 设置信息来源，1表示来源于项目信息，2表示来源于成员信息
     *
     * @param fromFlag 信息来源，1表示来源于项目信息，2表示来源于成员信息
     */
    public void setFromFlag(String fromFlag) {
        this.fromFlag = fromFlag;
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
     * 获取有效结束时间
     *
     * @return effectiveTo - 有效结束时间
     */
    public Date getEffectiveTo() {
        return effectiveTo;
    }

    /**
     * 设置有效结束时间
     *
     * @param effectiveTo 有效结束时间
     */
    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    /**
     * 获取有效开始时间
     *
     * @return effectiveFrom - 有效开始时间
     */
    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * 设置有效开始时间
     *
     * @param effectiveFrom 有效开始时间
     */
    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
}