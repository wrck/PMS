package com.dp.plat.prob.bean;

import java.util.Date;

import com.dp.plat.data.bean.CustomInfoEntity;

public class ProductComponent extends CustomInfoEntity {
    private static final long serialVersionUID = 3039283284866187981L;

    private Integer id;

    // 分组
    private String type;

    // 名称
    private String name;

    // 版本
    private String version;

    // 父节点
    private Integer parentId;

    // 状态
    private Boolean state;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

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
     * 获取分组
     *
     * @return type - 分组
     */
    public String getType() {
        return type;
    }

    /**
     * 设置分组
     *
     * @param type 分组
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取版本
     *
     * @return version - 版本
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置版本
     *
     * @param version 版本
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取父节点
     *
     * @return parentId - 父节点
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置父节点
     *
     * @param parentId 父节点
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
