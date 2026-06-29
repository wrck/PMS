package com.dp.plat.ehr.entity;

import com.dp.plat.core.entity.BaseEntity;

public class EhrEmpPower extends BaseEntity {
    // empID
    private Integer empID;

    // 工号
    private String workNo;

    // 公司id
    private Integer compID;

    // 从ehr同步数据生成的部门权限，固定的
    private String depIDs;

    // 绩效管理附加的部门权限
    private String extraDepIDs;

    // 绩效考核管理的部门
    private String adminDepIDs;

    // 从ehr同步数据生成的下属权限，固定的
    private String empIDs;

    // 绩效管理附加的下属权限
    private String extraEmpIDs;

    // 是否生效状态
    private Boolean state;

    /**
     * 获取empID
     *
     * @return empID - empID
     */
    public Integer getEmpID() {
        return empID;
    }

    /**
     * 设置empID
     *
     * @param empID empID
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
     * 获取公司id
     *
     * @return compID - 公司id
     */
    public Integer getCompID() {
        return compID;
    }

    /**
     * 设置公司id
     *
     * @param compID 公司id
     */
    public void setCompID(Integer compID) {
        this.compID = compID;
    }

    /**
     * 获取从ehr同步数据生成的部门权限，固定的
     *
     * @return depIDs - 从ehr同步数据生成的部门权限，固定的
     */
    public String getDepIDs() {
        return depIDs;
    }

    /**
     * 设置从ehr同步数据生成的部门权限，固定的
     *
     * @param depIDs 从ehr同步数据生成的部门权限，固定的
     */
    public void setDepIDs(String depIDs) {
        this.depIDs = depIDs;
    }

    /**
     * 获取绩效管理附加的部门权限
     *
     * @return extraDepIDs - 绩效管理附加的部门权限
     */
    public String getExtraDepIDs() {
        return extraDepIDs;
    }

    /**
     * 设置绩效管理附加的部门权限
     *
     * @param extraDepIDs 绩效管理附加的部门权限
     */
    public void setExtraDepIDs(String extraDepIDs) {
        this.extraDepIDs = extraDepIDs;
    }

    /**
     * 获取绩效考核管理的部门
     *
     * @return adminDepIDs - 绩效考核管理的部门
     */
    public String getAdminDepIDs() {
        return adminDepIDs;
    }

    /**
     * 设置绩效考核管理的部门
     *
     * @param adminDepIDs 绩效考核管理的部门
     */
    public void setAdminDepIDs(String adminDepIDs) {
        this.adminDepIDs = adminDepIDs;
    }

    /**
     * 获取从ehr同步数据生成的下属权限，固定的
     *
     * @return empIDs - 从ehr同步数据生成的下属权限，固定的
     */
    public String getEmpIDs() {
        return empIDs;
    }

    /**
     * 设置从ehr同步数据生成的下属权限，固定的
     *
     * @param empIDs 从ehr同步数据生成的下属权限，固定的
     */
    public void setEmpIDs(String empIDs) {
        this.empIDs = empIDs;
    }

    /**
     * 获取绩效管理附加的下属权限
     *
     * @return extraEmpIDs - 绩效管理附加的下属权限
     */
    public String getExtraEmpIDs() {
        return extraEmpIDs;
    }

    /**
     * 设置绩效管理附加的下属权限
     *
     * @param extraEmpIDs 绩效管理附加的下属权限
     */
    public void setExtraEmpIDs(String extraEmpIDs) {
        this.extraEmpIDs = extraEmpIDs;
    }

    /**
     * 获取是否生效状态
     *
     * @return state - 是否生效状态
     */
    public Boolean getState() {
        return state;
    }

    /**
     * 设置是否生效状态
     *
     * @param state 是否生效状态
     */
    public void setState(Boolean state) {
        this.state = state;
    }
}