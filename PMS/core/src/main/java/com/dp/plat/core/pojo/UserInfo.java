package com.dp.plat.core.pojo;

import java.util.Date;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class UserInfo {

    // 员工ID，外键
    private Integer id;

    // 工号
    private String workNo;

    // 姓名
    private String realName;

    // 英文名
    private String eName;

    // 公司ID
    private Integer compID;

    // 部门ID
    private Integer depID;

    // 岗位ID
    private Integer jobID;

    // 直接上级
    private Integer reportTo;

    // 职能上级
    private Integer wfreportTo;

    // 员工状态，1：在职，2：离职
    private Integer empStatus;

    // 岗位状态
    private Integer jobStatus;

    // 聘用类型：1：正式，3：实习生
    private Integer empType;

    // 性别：1：男，0：女
    private Short sex;

    // 生日
    @JsonSerialize(using = JsonSerializer.class)
    private Date birthday;

    // 邮箱
    private String email;

    // 手机
    private String mobile;

    // 座机
    private String telphone;

    // 头像
    private String avatar;

    // 备注
    private String remark;

    // 状态
    private Integer state;

    // userId
    private Integer userId;

    // 预留字段1
    private Integer custom1;

    // 预留字段2
    private Integer custom2;

    // 预留字段3
    private String custom3;

    // 预留字段4
    private String custom4;

    // 预留字段5
    private String custom5;

    /**
     * 获取员工ID，外键
     *
     * @return id - 员工ID，外键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置员工ID，外键
     *
     * @param id 员工ID，外键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取工号
     *
     * @return workNo - 工号
     */
    public String getWorkNo() {
        return workNo;
    }

    /**
     * 设置工号
     *
     * @param workNo 工号
     */
    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    /**
     * 获取姓名
     *
     * @return realName - 姓名
     */
    public String getRealName() {
        return realName;
    }

    /**
     * 设置姓名
     *
     * @param realName 姓名
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 获取英文名
     *
     * @return eName - 英文名
     */
    public String geteName() {
        return eName;
    }

    /**
     * 设置英文名
     *
     * @param eName 英文名
     */
    public void seteName(String eName) {
        this.eName = eName;
    }

    /**
     * 获取公司ID
     *
     * @return compID - 公司ID
     */
    public Integer getCompID() {
        return compID;
    }

    /**
     * 设置公司ID
     *
     * @param compID 公司ID
     */
    public void setCompID(Integer compID) {
        this.compID = compID;
    }

    /**
     * 获取部门ID
     *
     * @return depID - 部门ID
     */
    public Integer getDepID() {
        return depID;
    }

    /**
     * 设置部门ID
     *
     * @param depID 部门ID
     */
    public void setDepID(Integer depID) {
        this.depID = depID;
    }

    /**
     * 获取岗位ID
     *
     * @return jobID - 岗位ID
     */
    public Integer getJobID() {
        return jobID;
    }

    /**
     * 设置岗位ID
     *
     * @param jobID 岗位ID
     */
    public void setJobID(Integer jobID) {
        this.jobID = jobID;
    }

    /**
     * 获取直接上级
     *
     * @return reportTo - 直接上级
     */
    public Integer getReportTo() {
        return reportTo;
    }

    /**
     * 设置直接上级
     *
     * @param reportTo 直接上级
     */
    public void setReportTo(Integer reportTo) {
        this.reportTo = reportTo;
    }

    /**
     * 获取职能上级
     *
     * @return wfreportTo - 职能上级
     */
    public Integer getWfreportTo() {
        return wfreportTo;
    }

    /**
     * 设置职能上级
     *
     * @param wfreportTo 职能上级
     */
    public void setWfreportTo(Integer wfreportTo) {
        this.wfreportTo = wfreportTo;
    }

    /**
     * 获取员工状态，1：在职，2：离职
     *
     * @return empStatus - 员工状态，1：在职，2：离职
     */
    public Integer getEmpStatus() {
        return empStatus;
    }

    /**
     * 设置员工状态，1：在职，2：离职
     *
     * @param empStatus 员工状态，1：在职，2：离职
     */
    public void setEmpStatus(Integer empStatus) {
        this.empStatus = empStatus;
    }

    /**
     * 获取岗位状态
     *
     * @return jobStatus - 岗位状态
     */
    public Integer getJobStatus() {
        return jobStatus;
    }

    /**
     * 设置岗位状态
     *
     * @param jobStatus 岗位状态
     */
    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * 获取聘用类型：1：正式，3：实习生
     *
     * @return empType - 聘用类型：1：正式，3：实习生
     */
    public Integer getEmpType() {
        return empType;
    }

    /**
     * 设置聘用类型：1：正式，3：实习生
     *
     * @param empType 聘用类型：1：正式，3：实习生
     */
    public void setEmpType(Integer empType) {
        this.empType = empType;
    }

    /**
     * 获取性别：1：男，0：女
     *
     * @return sex - 性别：1：男，0：女
     */
    public Short getSex() {
        return sex;
    }

    /**
     * 设置性别：1：男，0：女
     *
     * @param sex 性别：1：男，0：女
     */
    public void setSex(Short sex) {
        this.sex = sex;
    }

    /**
     * 获取生日
     *
     * @return birthday - 生日
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置生日
     *
     * @param birthday 生日
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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
     * 获取手机
     *
     * @return mobile - 手机
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机
     *
     * @param mobile 手机
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取座机
     *
     * @return telphone - 座机
     */
    public String getTelphone() {
        return telphone;
    }

    /**
     * 设置座机
     *
     * @param telphone 座机
     */
    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    /**
     * 获取头像
     *
     * @return avatar - 头像
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像
     *
     * @param avatar 头像
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
     * @return state - 状态
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取userId
     *
     * @return userId - userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置userId
     *
     * @param userId userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取预留字段1
     *
     * @return custom1 - 预留字段1
     */
    public Integer getCustom1() {
        return custom1;
    }

    /**
     * 设置预留字段1
     *
     * @param custom1 预留字段1
     */
    public void setCustom1(Integer custom1) {
        this.custom1 = custom1;
    }

    /**
     * 获取预留字段2
     *
     * @return custom2 - 预留字段2
     */
    public Integer getCustom2() {
        return custom2;
    }

    /**
     * 设置预留字段2
     *
     * @param custom2 预留字段2
     */
    public void setCustom2(Integer custom2) {
        this.custom2 = custom2;
    }

    /**
     * 获取预留字段3
     *
     * @return custom3 - 预留字段3
     */
    public String getCustom3() {
        return custom3;
    }

    /**
     * 设置预留字段3
     *
     * @param custom3 预留字段3
     */
    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    /**
     * 获取预留字段4
     *
     * @return custom4 - 预留字段4
     */
    public String getCustom4() {
        return custom4;
    }

    /**
     * 设置预留字段4
     *
     * @param custom4 预留字段4
     */
    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    /**
     * 获取预留字段5
     *
     * @return custom5 - 预留字段5
     */
    public String getCustom5() {
        return custom5;
    }

    /**
     * 设置预留字段5
     *
     * @param custom5 预留字段5
     */
    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserInfo [");
        if (id != null)
            builder.append("id=").append(id).append(", ");
        if (workNo != null)
            builder.append("workNo=").append(workNo).append(", ");
        if (realName != null)
            builder.append("realName=").append(realName).append(", ");
        if (eName != null)
            builder.append("eName=").append(eName).append(", ");
        if (compID != null)
            builder.append("compID=").append(compID).append(", ");
        if (depID != null)
            builder.append("depID=").append(depID).append(", ");
        if (jobID != null)
            builder.append("jobID=").append(jobID).append(", ");
        if (reportTo != null)
            builder.append("reportTo=").append(reportTo).append(", ");
        if (wfreportTo != null)
            builder.append("wfreportTo=").append(wfreportTo).append(", ");
        if (empStatus != null)
            builder.append("empStatus=").append(empStatus).append(", ");
        if (jobStatus != null)
            builder.append("jobStatus=").append(jobStatus).append(", ");
        if (empType != null)
            builder.append("empType=").append(empType).append(", ");
        if (sex != null)
            builder.append("sex=").append(sex).append(", ");
        if (birthday != null)
            builder.append("birthday=").append(birthday).append(", ");
        if (email != null)
            builder.append("email=").append(email).append(", ");
        if (mobile != null)
            builder.append("mobile=").append(mobile).append(", ");
        if (telphone != null)
            builder.append("telphone=").append(telphone).append(", ");
        if (avatar != null)
            builder.append("avatar=").append(avatar).append(", ");
        if (remark != null)
            builder.append("remark=").append(remark).append(", ");
        if (state != null)
            builder.append("state=").append(state).append(", ");
        if (userId != null)
            builder.append("userId=").append(userId).append(", ");
        if (custom1 != null)
            builder.append("custom1=").append(custom1).append(", ");
        if (custom2 != null)
            builder.append("custom2=").append(custom2).append(", ");
        if (custom3 != null)
            builder.append("custom3=").append(custom3).append(", ");
        if (custom4 != null)
            builder.append("custom4=").append(custom4).append(", ");
        if (custom5 != null)
            builder.append("custom5=").append(custom5);
        builder.append("]");
        return builder.toString();
    }
}
