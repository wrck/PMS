package com.dp.plat.ehr.entity;

import java.util.Date;

import javax.persistence.Id;

import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.ehr.annotation.TreeNodeParam;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@TreeNodeParam(fields = { "id:depID", "text:depName", "parentId:adminID,Company-compID" })
public class Department {

    // 部门ID，关联外键
	@Id
    private Integer depID;

    // 部门编码
    private String depCode;

    // 部门名称
    private String depName;

    // 部门简称
    private String depAbbr;

    // 公司ID，外键
    private Integer compID;

    // 上级ID
    private Integer adminID;

    // 部门级别
    private Integer depGrade;

    // 部门类型
    private Integer depType;

    // 部门属性
    private Integer depProperty;

    // 存在部门内分级计数用
    private Integer depCost;

    // 主管
    private Integer director;

    // 分管领导
    private Integer director2;

    private Integer depEmp;

    private Integer depNum;

    // 生效时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectDate;

    // 排序
    private String xOrder;

    // 失效状态
    private Boolean isDisabled;

    // 失效时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date disabledDate;

    // 备注
    private String remark;

    // 保留字段1
    private Integer depCustom1;

    // 保留字段2
    private Integer depCustom2;

    // 保留字段3
    private Integer depCustom3;

    // 保留字段4
    private Integer depCustom4;

    // 保留字段5
    private Integer depCustom5;

    /**
     * 获取部门ID，关联外键
     *
     * @return depID - 部门ID，关联外键
     */
    public Integer getDepID() {
        return depID;
    }

    /**
     * 设置部门ID，关联外键
     *
     * @param depID 部门ID，关联外键
     */
    public void setDepID(Integer depID) {
        this.depID = depID;
    }

    /**
     * 获取部门编码
     *
     * @return depCode - 部门编码
     */
    public String getDepCode() {
        return depCode;
    }

    /**
     * 设置部门编码
     *
     * @param depCode 部门编码
     */
    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    /**
     * 获取部门名称
     *
     * @return depName - 部门名称
     */
    public String getDepName() {
        return depName;
    }

    /**
     * 设置部门名称
     *
     * @param depName 部门名称
     */
    public void setDepName(String depName) {
        this.depName = depName;
    }

    /**
     * 获取部门简称
     *
     * @return depAbbr - 部门简称
     */
    public String getDepAbbr() {
        return depAbbr;
    }

    /**
     * 设置部门简称
     *
     * @param depAbbr 部门简称
     */
    public void setDepAbbr(String depAbbr) {
        this.depAbbr = depAbbr;
    }

    /**
     * 获取公司ID，外键
     *
     * @return compID - 公司ID，外键
     */
    public Integer getCompID() {
        return compID;
    }

    /**
     * 设置公司ID，外键
     *
     * @param compID 公司ID，外键
     */
    public void setCompID(Integer compID) {
        this.compID = compID;
    }

    /**
     * 获取上级ID
     *
     * @return adminID - 上级ID
     */
    public Integer getAdminID() {
        return adminID;
    }

    /**
     * 设置上级ID
     *
     * @param adminID 上级ID
     */
    public void setAdminID(Integer adminID) {
        this.adminID = adminID;
    }

    /**
     * 获取部门级别
     *
     * @return depGrade - 部门级别
     */
    public Integer getDepGrade() {
        return depGrade;
    }

    /**
     * 设置部门级别
     *
     * @param depGrade 部门级别
     */
    public void setDepGrade(Integer depGrade) {
        this.depGrade = depGrade;
    }

    /**
     * 获取部门类型
     *
     * @return depType - 部门类型
     */
    public Integer getDepType() {
        return depType;
    }

    /**
     * 设置部门类型
     *
     * @param depType 部门类型
     */
    public void setDepType(Integer depType) {
        this.depType = depType;
    }

    /**
     * 获取部门属性
     *
     * @return depProperty - 部门属性
     */
    public Integer getDepProperty() {
        return depProperty;
    }

    /**
     * 设置部门属性
     *
     * @param depProperty 部门属性
     */
    public void setDepProperty(Integer depProperty) {
        this.depProperty = depProperty;
    }

    /**
     * 获取存在部门内分级计数用
     *
     * @return depCost - 存在部门内分级计数用
     */
    public Integer getDepCost() {
        return depCost;
    }

    /**
     * 设置存在部门内分级计数用
     *
     * @param depCost 存在部门内分级计数用
     */
    public void setDepCost(Integer depCost) {
        this.depCost = depCost;
    }

    /**
     * 获取主管
     *
     * @return director - 主管
     */
    public Integer getDirector() {
        return director;
    }

    /**
     * 设置主管
     *
     * @param director 主管
     */
    public void setDirector(Integer director) {
        this.director = director;
    }

    /**
     * 获取分管领导
     *
     * @return director2 - 分管领导
     */
    public Integer getDirector2() {
        return director2;
    }

    /**
     * 设置分管领导
     *
     * @param director2 分管领导
     */
    public void setDirector2(Integer director2) {
        this.director2 = director2;
    }

    /**
     * @return depEmp
     */
    public Integer getDepEmp() {
        return depEmp;
    }

    /**
     * @param depEmp
     */
    public void setDepEmp(Integer depEmp) {
        this.depEmp = depEmp;
    }

    /**
     * @return depNum
     */
    public Integer getDepNum() {
        return depNum;
    }

    /**
     * @param depNum
     */
    public void setDepNum(Integer depNum) {
        this.depNum = depNum;
    }

    /**
     * 获取生效时间
     *
     * @return effectDate - 生效时间
     */
    public Date getEffectDate() {
        return effectDate;
    }

    /**
     * 设置生效时间
     *
     * @param effectDate 生效时间
     */
    public void setEffectDate(Date effectDate) {
        this.effectDate = effectDate;
    }

    /**
     * 获取排序
     *
     * @return xOrder - 排序
     */
    public String getxOrder() {
        return xOrder;
    }

    /**
     * 设置排序
     *
     * @param xOrder 排序
     */
    public void setxOrder(String xOrder) {
        this.xOrder = xOrder;
    }

    /**
     * 获取失效状态
     *
     * @return isDisabled - 失效状态
     */
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    /**
     * 设置失效状态
     *
     * @param isDisabled 失效状态
     */
    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * 获取失效时间
     *
     * @return disabledDate - 失效时间
     */
    public Date getDisabledDate() {
        return disabledDate;
    }

    /**
     * 设置失效时间
     *
     * @param disabledDate 失效时间
     */
    public void setDisabledDate(Date disabledDate) {
        this.disabledDate = disabledDate;
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
     * 获取保留字段1
     *
     * @return depCustom1 - 保留字段1
     */
    public Integer getDepCustom1() {
        return depCustom1;
    }

    /**
     * 设置保留字段1
     *
     * @param depCustom1 保留字段1
     */
    public void setDepCustom1(Integer depCustom1) {
        this.depCustom1 = depCustom1;
    }

    /**
     * 获取保留字段2
     *
     * @return depCustom2 - 保留字段2
     */
    public Integer getDepCustom2() {
        return depCustom2;
    }

    /**
     * 设置保留字段2
     *
     * @param depCustom2 保留字段2
     */
    public void setDepCustom2(Integer depCustom2) {
        this.depCustom2 = depCustom2;
    }

    /**
     * 获取保留字段3
     *
     * @return depCustom3 - 保留字段3
     */
    public Integer getDepCustom3() {
        return depCustom3;
    }

    /**
     * 设置保留字段3
     *
     * @param depCustom3 保留字段3
     */
    public void setDepCustom3(Integer depCustom3) {
        this.depCustom3 = depCustom3;
    }

    /**
     * 获取保留字段4
     *
     * @return depCustom4 - 保留字段4
     */
    public Integer getDepCustom4() {
        return depCustom4;
    }

    /**
     * 设置保留字段4
     *
     * @param depCustom4 保留字段4
     */
    public void setDepCustom4(Integer depCustom4) {
        this.depCustom4 = depCustom4;
    }

    /**
     * 获取保留字段5
     *
     * @return depCustom5 - 保留字段5
     */
    public Integer getDepCustom5() {
        return depCustom5;
    }

    /**
     * 设置保留字段5
     *
     * @param depCustom5 保留字段5
     */
    public void setDepCustom5(Integer depCustom5) {
		this.depCustom5 = depCustom5;
	}
}
