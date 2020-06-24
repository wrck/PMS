package com.dp.plat.ehr.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.ehr.annotation.TreeNodeParam;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@TreeNodeParam(fields = { "id:empID", "text:name", "parentId:Department-depID,Company-compID" })
public class Employee implements Serializable{

	private static final long serialVersionUID = -4751156799336018884L;

	// 员工ID，外键
	@Id
    private Integer empID;

    // 工号
    private String workNo;

    // 姓名
    private String name;

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

    // 加入公司日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date joinDate;

    // 工作开始日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date workBeginDate;

    // 加入公司日期（未知）
    @JsonSerialize(using = JsonSerializer.class)
    private Date jobBeginDate;

    // 实习开始时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date pracBeginDate;

    // 实习结束时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date pracEndDate;

    @JsonSerialize(using = JsonSerializer.class)
    private Date probBeginDate;

    @JsonSerialize(using = JsonSerializer.class)
    private Date probEndDate;

    // 离职时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date leaveDate;

    // 性别：1：男，2：女
    private Integer gender;

    // 邮箱
    private String email;

    // 手机
    private String mobile;

    // 座机
    private String officePhone;

    // 备注
    private String remark;

    // 失效
    private Integer disabled;

    // 预留字段1
    private Integer empCustom1;

    // 预留字段2
    private Integer empCustom2;

    // 预留字段3
    private Integer empCustom3;

    // 预留字段4
    private String empCustom4;

    // 预留字段5
    private Integer empCustom5;

    /**
     * 获取员工ID，外键
     *
     * @return empID - 员工ID，外键
     */
    public Integer getEmpID() {
        return empID;
    }

    /**
     * 设置员工ID，外键
     *
     * @param empID 员工ID，外键
     */
    public void setEmpID(Integer empID) {
        this.empID = empID;
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
     * @return name - 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name;
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
     * 获取加入公司日期
     *
     * @return joinDate - 加入公司日期
     */
    public Date getJoinDate() {
        return joinDate;
    }

    /**
     * 设置加入公司日期
     *
     * @param joinDate 加入公司日期
     */
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    /**
     * 获取工作开始日期
     *
     * @return workBeginDate - 工作开始日期
     */
    public Date getWorkBeginDate() {
        return workBeginDate;
    }

    /**
     * 设置工作开始日期
     *
     * @param workBeginDate 工作开始日期
     */
    public void setWorkBeginDate(Date workBeginDate) {
        this.workBeginDate = workBeginDate;
    }

    /**
     * 获取加入公司日期（未知）
     *
     * @return jobBeginDate - 加入公司日期（未知）
     */
    public Date getJobBeginDate() {
        return jobBeginDate;
    }

    /**
     * 设置加入公司日期（未知）
     *
     * @param jobBeginDate 加入公司日期（未知）
     */
    public void setJobBeginDate(Date jobBeginDate) {
        this.jobBeginDate = jobBeginDate;
    }

    /**
     * 获取实习开始时间
     *
     * @return pracBeginDate - 实习开始时间
     */
    public Date getPracBeginDate() {
        return pracBeginDate;
    }

    /**
     * 设置实习开始时间
     *
     * @param pracBeginDate 实习开始时间
     */
    public void setPracBeginDate(Date pracBeginDate) {
        this.pracBeginDate = pracBeginDate;
    }

    /**
     * 获取实习结束时间
     *
     * @return pracEndDate - 实习结束时间
     */
    public Date getPracEndDate() {
        return pracEndDate;
    }

    /**
     * 设置实习结束时间
     *
     * @param pracEndDate 实习结束时间
     */
    public void setPracEndDate(Date pracEndDate) {
        this.pracEndDate = pracEndDate;
    }

    /**
     * @return probBeginDate
     */
    public Date getProbBeginDate() {
        return probBeginDate;
    }

    /**
     * @param probBeginDate
     */
    public void setProbBeginDate(Date probBeginDate) {
        this.probBeginDate = probBeginDate;
    }

    /**
     * @return probEndDate
     */
    public Date getProbEndDate() {
        return probEndDate;
    }

    /**
     * @param probEndDate
     */
    public void setProbEndDate(Date probEndDate) {
        this.probEndDate = probEndDate;
    }

    /**
     * 获取离职时间
     *
     * @return leaveDate - 离职时间
     */
    public Date getLeaveDate() {
        return leaveDate;
    }

    /**
     * 设置离职时间
     *
     * @param leaveDate 离职时间
     */
    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    /**
     * 获取性别：1：男，2：女
     *
     * @return gender - 性别：1：男，2：女
     */
    public Integer getGender() {
        return gender;
    }

    /**
     * 设置性别：1：男，2：女
     *
     * @param gender 性别：1：男，2：女
     */
    public void setGender(Integer gender) {
        this.gender = gender;
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
     * @return officePhone - 座机
     */
    public String getOfficePhone() {
        return officePhone;
    }

    /**
     * 设置座机
     *
     * @param officePhone 座机
     */
    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
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
     * 获取失效
     *
     * @return disabled - 失效
     */
    public Integer getDisabled() {
        return disabled;
    }

    /**
     * 设置失效
     *
     * @param disabled 失效
     */
    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    /**
     * 获取预留字段1
     *
     * @return empCustom1 - 预留字段1
     */
    public Integer getEmpCustom1() {
        return empCustom1;
    }

    /**
     * 设置预留字段1
     *
     * @param empCustom1 预留字段1
     */
    public void setEmpCustom1(Integer empCustom1) {
        this.empCustom1 = empCustom1;
    }

    /**
     * 获取预留字段2
     *
     * @return empCustom2 - 预留字段2
     */
    public Integer getEmpCustom2() {
        return empCustom2;
    }

    /**
     * 设置预留字段2
     *
     * @param empCustom2 预留字段2
     */
    public void setEmpCustom2(Integer empCustom2) {
        this.empCustom2 = empCustom2;
    }

    /**
     * 获取预留字段3
     *
     * @return empCustom3 - 预留字段3
     */
    public Integer getEmpCustom3() {
        return empCustom3;
    }

    /**
     * 设置预留字段3
     *
     * @param empCustom3 预留字段3
     */
    public void setEmpCustom3(Integer empCustom3) {
        this.empCustom3 = empCustom3;
    }

    /**
     * 获取预留字段4
     *
     * @return empCustom4 - 预留字段4
     */
    public String getEmpCustom4() {
        return empCustom4;
    }

    /**
     * 设置预留字段4
     *
     * @param empCustom4 预留字段4
     */
    public void setEmpCustom4(String empCustom4) {
        this.empCustom4 = empCustom4;
    }

    /**
     * 获取预留字段5
     *
     * @return empCustom5 - 预留字段5
     */
    public Integer getEmpCustom5() {
        return empCustom5;
    }

    /**
     * 设置预留字段5
     *
     * @param empCustom5 预留字段5
     */
    public void setEmpCustom5(Integer empCustom5) {
        this.empCustom5 = empCustom5;
    }
}