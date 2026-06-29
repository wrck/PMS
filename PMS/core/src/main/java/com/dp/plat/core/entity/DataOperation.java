package com.dp.plat.core.entity;

import java.util.Date;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DataOperation extends BaseEntity {

    // 操作名
    private String name;

    // 操作描述
    private String description;

    // 操作类型，导入:1，导出:0
    private Integer type;

    // 操作所在类
    private String clazz;

    // 操作类的方法
    private String method;

    // 方法参数类型
    private String parameterTypes;

    // 导出时的列
    private String columns;

    // 员工权限
    private String empPower;

    // 部门权限
    private String depPower;

    // 状态
    private Boolean state;

    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveFrom;

    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveTo;

    // 额外表单内容
    private String formHtml;

    // 导入时的js，导出时的sql
    private String script;

    public DataOperation(String name) {
		super();
		this.name = name;
	}

	public DataOperation() {
		super();
	}
	
    /**
     * 获取操作名
     *
     * @return name - 操作名
     */
    public String getName() {
        return name;
    }

	/**
     * 设置操作名
     *
     * @param name 操作名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取操作描述
     *
     * @return description - 操作描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置操作描述
     *
     * @param description 操作描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取操作类型，导入:1，导出:0
     *
     * @return type - 操作类型，导入:1，导出:0
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置操作类型，导入:1，导出:0
     *
     * @param type 操作类型，导入:1，导出:0
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取操作所在类
     *
     * @return clazz - 操作所在类
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * 设置操作所在类
     *
     * @param clazz 操作所在类
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    /**
     * 获取操作类的方法
     *
     * @return method - 操作类的方法
     */
    public String getMethod() {
        return method;
    }

    /**
     * 设置操作类的方法
     *
     * @param method 操作类的方法
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 获取方法参数类型
     *
     * @return parameterTypes - 方法参数类型
     */
    public String getParameterTypes() {
        return parameterTypes;
    }

    /**
     * 设置方法参数类型
     *
     * @param parameterTypes 方法参数类型
     */
    public void setParameterTypes(String parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * 获取导出时的列
     *
     * @return columns - 导出时的列
     */
    public String getColumns() {
        return columns;
    }

    /**
     * 设置导出时的列
     *
     * @param columns 导出时的列
     */
    public void setColumns(String columns) {
        this.columns = columns;
    }

    /**
     * 获取员工权限
     *
     * @return empPower - 员工权限
     */
    public String getEmpPower() {
        return empPower;
    }

    /**
     * 设置员工权限
     *
     * @param empPower 员工权限
     */
    public void setEmpPower(String empPower) {
        this.empPower = empPower;
    }

    /**
     * 获取部门权限
     *
     * @return depPower - 部门权限
     */
    public String getDepPower() {
        return depPower;
    }

    /**
     * 设置部门权限
     *
     * @param depPower 部门权限
     */
    public void setDepPower(String depPower) {
        this.depPower = depPower;
    }

    /**
     * 获取状态
     *
     * @return state - 状态
     */
    public Boolean getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(Boolean state) {
        this.state = state;
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

    /**
     * 获取额外表单内容
     *
     * @return formHtml - 额外表单内容
     */
    public String getFormHtml() {
        return formHtml;
    }

    /**
     * 设置额外表单内容
     *
     * @param formHtml 额外表单内容
     */
    public void setFormHtml(String formHtml) {
        this.formHtml = formHtml;
    }

    /**
     * 获取导入时的js，导出时的sql
     *
     * @return script - 导入时的js，导出时的sql
     */
    public String getScript() {
        return script;
    }

    /**
     * 设置导入时的js，导出时的sql
     *
     * @param script 导入时的js，导出时的sql
     */
    public void setScript(String script) {
        this.script = script;
    }
}
