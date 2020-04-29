package com.dp.plat.pms.springmvc.entity;

import java.util.Map;
import com.dp.plat.core.entity.BaseEntity;
import java.util.Date;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class CommonRelatedData extends BaseEntity {

    private Integer id;

    // 数据类型
    private String type;

    // 主数据类型
    private String objType;

    // 主数据Id
    private Integer objId;

    private String field1;

    private String field2;

    private String field3;

    private String field4;

    private String field5;

    private String field6;

    private String field7;

    private String field8;

    private String field9;

    private String field10;

    private Boolean disabled;

    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveFrom;

    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveTo;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    private Map customInfo;
    
    public CommonRelatedData() {
		super();
	}
    
	public CommonRelatedData(String objType, Integer objId, String type) {
		super();
		this.objType = objType;
		this.objId = objId;
		this.type = type;
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
     * 获取数据类型
     *
     * @return type - 数据类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置数据类型
     *
     * @param type 数据类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取主数据类型
     *
     * @return objType - 主数据类型
     */
    public String getObjType() {
        return objType;
    }

    /**
     * 设置主数据类型
     *
     * @param objType 主数据类型
     */
    public void setObjType(String objType) {
        this.objType = objType;
    }

    /**
     * 获取主数据Id
     *
     * @return objId - 主数据Id
     */
    public Integer getObjId() {
        return objId;
    }

    /**
     * 设置主数据Id
     *
     * @param objId 主数据Id
     */
    public void setObjId(Integer objId) {
        this.objId = objId;
    }

    /**
     * @return field1
     */
    public String getField1() {
        return field1;
    }

    /**
     * @param field1
     */
    public void setField1(String field1) {
        this.field1 = field1;
    }

    /**
     * @return field2
     */
    public String getField2() {
        return field2;
    }

    /**
     * @param field2
     */
    public void setField2(String field2) {
        this.field2 = field2;
    }

    /**
     * @return field3
     */
    public String getField3() {
        return field3;
    }

    /**
     * @param field3
     */
    public void setField3(String field3) {
        this.field3 = field3;
    }

    /**
     * @return field4
     */
    public String getField4() {
        return field4;
    }

    /**
     * @param field4
     */
    public void setField4(String field4) {
        this.field4 = field4;
    }

    /**
     * @return field5
     */
    public String getField5() {
        return field5;
    }

    /**
     * @param field5
     */
    public void setField5(String field5) {
        this.field5 = field5;
    }

    /**
     * @return field6
     */
    public String getField6() {
        return field6;
    }

    /**
     * @param field6
     */
    public void setField6(String field6) {
        this.field6 = field6;
    }

    /**
     * @return field7
     */
    public String getField7() {
        return field7;
    }

    /**
     * @param field7
     */
    public void setField7(String field7) {
        this.field7 = field7;
    }

    /**
     * @return field8
     */
    public String getField8() {
        return field8;
    }

    /**
     * @param field8
     */
    public void setField8(String field8) {
        this.field8 = field8;
    }

    /**
     * @return field9
     */
    public String getField9() {
        return field9;
    }

    /**
     * @param field9
     */
    public void setField9(String field9) {
        this.field9 = field9;
    }

    /**
     * @return field10
     */
    public String getField10() {
        return field10;
    }

    /**
     * @param field10
     */
    public void setField10(String field10) {
        this.field10 = field10;
    }

    /**
     * @return disabled
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * @param disabled
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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
     * @return customInfo
     */
    public Map getCustomInfo() {
        return customInfo;
    }

    /**
     * @param customInfo
     */
    public void setCustomInfo(Map customInfo) {
        this.customInfo = customInfo;
    }
}
