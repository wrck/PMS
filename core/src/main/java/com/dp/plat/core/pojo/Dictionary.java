package com.dp.plat.core.pojo;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;

public class Dictionary {
    private Integer id;

    // 字典类型id
    private Integer dicTypeId;

    // 字典类型
    private String dicTypeName;

    // 字典key
    private String dicKey;

    // 字典value
    private String dicValue;

    // 自定义属性
    private String custominfo;

    // 排序
    private Integer sort;

    // 有效标志（1-有效，0-无效）
    private Integer status;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createtime;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updatetime;

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
     * 获取字典类型id
     *
     * @return dic_type_id - 字典类型id
     */
    public Integer getDicTypeId() {
        return dicTypeId;
    }

    /**
     * 设置字典类型id
     *
     * @param dicTypeId 字典类型id
     */
    public void setDicTypeId(Integer dicTypeId) {
        this.dicTypeId = dicTypeId;
    }

    /**
     * 获取字典类型
     *
     * @return dic_type_name - 字典类型
     */
    public String getDicTypeName() {
        return dicTypeName;
    }

    /**
     * 设置字典类型
     *
     * @param dicTypeName 字典类型
     */
    public void setDicTypeName(String dicTypeName) {
        this.dicTypeName = dicTypeName;
    }

    /**
     * 获取字典key
     *
     * @return dic_key - 字典key
     */
    public String getDicKey() {
        return dicKey;
    }

    /**
     * 设置字典key
     *
     * @param dicKey 字典key
     */
    public void setDicKey(String dicKey) {
        this.dicKey = dicKey;
    }

    /**
     * 获取字典value
     *
     * @return dic_value - 字典value
     */
    public String getDicValue() {
        return dicValue;
    }

    /**
     * 设置字典value
     *
     * @param dicValue 字典value
     */
    public void setDicValue(String dicValue) {
        this.dicValue = dicValue;
    }

    /**
     * 获取自定义属性
     *
     * @return customInfo - 自定义属性
     */
    public String getCustominfo() {
        return custominfo;
    }

    /**
     * 设置自定义属性
     *
     * @param custominfo 自定义属性
     */
    public void setCustominfo(String custominfo) {
        this.custominfo = custominfo;
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
     * 获取有效标志（1-有效，0-无效）
     *
     * @return status - 有效标志（1-有效，0-无效）
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置有效标志（1-有效，0-无效）
     *
     * @param status 有效标志（1-有效，0-无效）
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return createTime
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * @return updateTime
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * @param updatetime
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}