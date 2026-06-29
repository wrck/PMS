package com.dp.plat.pms.springmvc.entity;

import java.util.Date;

import com.dp.plat.core.serializer.JsonSerializer;
import com.dp.plat.core.vo.DataTableColumn;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class DataFieldRelation extends DataTableColumn {

    private Integer id;

    // 数据名
    private String dataName;

    // 数据类型
    private String dataType;

    // 数据实例ID
    private Integer dataId;

    // 字段
    private String field;

    // 字段别名
    private String alias;

    // 字段名
    private String name;

    // 字段标题
    private String title;

    // 字段标题Key
    private String titleKey;

    // 字段CSS id
    private String cssId;

    // 字段CSS class
    private String cssClass;

    // 字段CSS style
    private String cssStyle;

    // 字段类型
    private String type;

    // 字段处理
    private String render;

    // 排序
    private Integer sort;

    // 允许排序
    private Boolean orderable;

    // 允许搜索
    private Boolean searchable;

    // 允许可见
    private Boolean visible;

    // 必填
    private Boolean required;

    // 只读
    private Boolean readonly;

    // 组件失效
    private Boolean disabled;

    // 外部数据
    private String extData;

    // 外部数据key
    private String extKey;

    // 外部数据value
    private String extValue;

    // 传播媒介
    private String media;

    // 类名
    private String clazzName;

    // 父类DataName
    private String superData;

    // 状态
    private Integer status;

    // 公司ID
    private Integer compId;

    // 是否为系统字段
    private Boolean isSystemField;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;
    

    public DataFieldRelation() {
		super();
	}
    
	public DataFieldRelation(String dataName, String dataType) {
		super();
		this.dataName = dataName;
		this.dataType = dataType;
	}

	public DataFieldRelation(String dataName, String dataType, Integer status) {
		super();
		this.dataName = dataName;
		this.dataType = dataType;
		this.status = status;
	}

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
     * 获取数据名
     *
     * @return dataName - 数据名
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * 设置数据名
     *
     * @param dataName 数据名
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * 获取数据类型
     *
     * @return dataType - 数据类型
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型
     *
     * @param dataType 数据类型
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取数据实例ID
     *
     * @return dataId - 数据实例ID
     */
    public Integer getDataId() {
        return dataId;
    }

    /**
     * 设置数据实例ID
     *
     * @param dataId 数据实例ID
     */
    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    /**
     * 获取字段
     *
     * @return field - 字段
     */
    public String getField() {
        return field;
    }

    /**
     * 设置字段
     *
     * @param field 字段
     */
    public void setField(String field) {
    	super.setData(field);
        this.field = field;
    }

    /**
     * 获取字段别名
     *
     * @return alias - 字段别名
     */
    public String getAlias() {
        return alias;
    }

    /**
     * 设置字段别名
     *
     * @param alias 字段别名
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * 获取字段名
     *
     * @return name - 字段名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置字段名
     *
     * @param name 字段名
     */
    public void setName(String name) {
        super.setName(name);
        this.name = name;
    }

    /**
     * 获取字段标题
     *
     * @return title - 字段标题
     */
    public String getTitle() {
    	if(this.title == null || this.title.isEmpty()) {
    		return this.getName() != null ? this.getName() : this.title;
    	}
        return title;
    }

    /**
     * 设置字段标题
     *
     * @param title 字段标题
     */
    public void setTitle(String title) {
        super.setTitle(title);
        this.title = title;
    }

    /**
     * 获取字段标题Key
     *
     * @return titleKey - 字段标题Key
     */
    public String getTitleKey() {
        return titleKey;
    }

    /**
     * 设置字段标题Key
     *
     * @param titleKey 字段标题Key
     */
    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    /**
     * 获取字段CSS id
     *
     * @return cssId - 字段CSS id
     */
    public String getCssId() {
        return cssId;
    }

    /**
     * 设置字段CSS id
     *
     * @param cssId 字段CSS id
     */
    public void setCssId(String cssId) {
        this.cssId = cssId;
    }

    /**
     * 获取字段CSS class
     *
     * @return cssClass - 字段CSS class
     */
    public String getCssClass() {
        return cssClass;
    }

    /**
     * 设置字段CSS class
     *
     * @param cssClass 字段CSS class
     */
    public void setCssClass(String cssClass) {
    	super.setClassName(cssClass);
        this.cssClass = cssClass;
    }

    /**
     * 获取字段CSS style
     *
     * @return cssStyle - 字段CSS style
     */
    public String getCssStyle() {
        return cssStyle;
    }

    /**
     * 设置字段CSS style
     *
     * @param cssStyle 字段CSS style
     */
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    /**
     * 获取字段类型
     *
     * @return type - 字段类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置字段类型
     *
     * @param type 字段类型
     */
    public void setType(String type) {
        super.setType(type);
        this.type = type;
    }

    /**
     * 获取字段处理
     *
     * @return render - 字段处理
     */
    public String getRender() {
        return render;
    }

    /**
     * 设置字段处理
     *
     * @param render 字段处理
     */
    public void setRender(String render) {
        super.setRender(render);
        this.render = render;
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取允许排序
     *
     * @return orderable - 允许排序
     */
    public Boolean getOrderable() {
        return orderable;
    }

    /**
     * 设置允许排序
     *
     * @param orderable 允许排序
     */
    public void setOrderable(Boolean orderable) {
        super.setOrderable(orderable);
        this.orderable = orderable;
    }

    /**
     * 获取允许搜索
     *
     * @return searchable - 允许搜索
     */
    public Boolean getSearchable() {
        return searchable;
    }

    /**
     * 设置允许搜索
     *
     * @param searchable 允许搜索
     */
    public void setSearchable(Boolean searchable) {
        super.setSearchable(searchable);
        this.searchable = searchable;
    }

    /**
     * 获取允许可见
     *
     * @return visible - 允许可见
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * 设置允许可见
     *
     * @param visible 允许可见
     */
    public void setVisible(Boolean visible) {
        super.setVisible(visible);
        this.visible = visible;
    }

    /**
     * 获取必填
     *
     * @return required - 必填
     */
    public Boolean getRequired() {
        return required;
    }

    /**
     * 设置必填
     *
     * @param required 必填
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * 获取只读
     *
     * @return readonly - 只读
     */
    public Boolean getReadonly() {
        return readonly;
    }

    /**
     * 设置只读
     *
     * @param readonly 只读
     */
    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * 获取组件失效
     *
     * @return disabled - 组件失效
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * 设置组件失效
     *
     * @param disabled 组件失效
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * 获取外部数据
     *
     * @return extData - 外部数据
     */
    public String getExtData() {
        return extData;
    }

    /**
     * 设置外部数据
     *
     * @param extData 外部数据
     */
    public void setExtData(String extData) {
        this.extData = extData;
    }

    /**
     * 获取外部数据key
     *
     * @return extKey - 外部数据key
     */
    public String getExtKey() {
        return extKey;
    }

    /**
     * 设置外部数据key
     *
     * @param extKey 外部数据key
     */
    public void setExtKey(String extKey) {
        this.extKey = extKey;
    }

    /**
     * 获取外部数据value
     *
     * @return extValue - 外部数据value
     */
    public String getExtValue() {
        return extValue;
    }

    /**
     * 设置外部数据value
     *
     * @param extValue 外部数据value
     */
    public void setExtValue(String extValue) {
        this.extValue = extValue;
    }

    /**
     * 获取传播媒介
     *
     * @return media - 传播媒介
     */
    public String getMedia() {
        return media;
    }

    /**
     * 设置传播媒介
     *
     * @param media 传播媒介
     */
    public void setMedia(String media) {
        this.media = media;
    }

    /**
     * 获取类名
     *
     * @return clazzName - 类名
     */
    public String getClazzName() {
        return clazzName;
    }

    /**
     * 设置类名
     *
     * @param clazzName 类名
     */
    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
        super.setClassName(this.getCssClass());
    }

    /**
     * 获取父类DataName
     *
     * @return superData - 父类DataName
     */
    public String getSuperData() {
        return superData;
    }

    /**
     * 设置父类DataName
     *
     * @param superData 父类DataName
     */
    public void setSuperData(String superData) {
        this.superData = superData;
    }

    /**
     * 获取状态
     *
     * @return status - 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取公司ID
     *
     * @return compId - 公司ID
     */
    public Integer getCompId() {
        return compId;
    }

    /**
     * 设置公司ID
     *
     * @param compId 公司ID
     */
    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    /**
     * 获取是否为系统字段
     *
     * @return isSystemField - 是否为系统字段
     */
    public Boolean getIsSystemField() {
        return isSystemField;
    }

    /**
     * 设置是否为系统字段
     *
     * @param isSystemField 是否为系统字段
     */
    public void setIsSystemField(Boolean isSystemField) {
        this.isSystemField = isSystemField;
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
