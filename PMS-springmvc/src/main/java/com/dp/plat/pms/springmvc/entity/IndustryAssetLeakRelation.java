package com.dp.plat.pms.springmvc.entity;

import com.dp.plat.core.entity.BaseEntity;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;

public class IndustryAssetLeakRelation extends BaseEntity {
    private Integer id;

    // 项目ID
    private Integer projectId;

    // 资产ID
    private Integer assetId;

    // 漏洞ID
    private Integer leakId;

    // 生效时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveFrom;

    // 失效时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date effectiveTo;

    // 删除标准
    private Boolean disabled;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
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
     * 获取项目ID
     *
     * @return projectId - 项目ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 设置项目ID
     *
     * @param projectId 项目ID
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取资产ID
     *
     * @return assetId - 资产ID
     */
    public Integer getAssetId() {
        return assetId;
    }

    /**
     * 设置资产ID
     *
     * @param assetId 资产ID
     */
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    /**
     * 获取漏洞ID
     *
     * @return leakId - 漏洞ID
     */
    public Integer getLeakId() {
        return leakId;
    }

    /**
     * 设置漏洞ID
     *
     * @param leakId 漏洞ID
     */
    public void setLeakId(Integer leakId) {
        this.leakId = leakId;
    }

    /**
     * 获取生效时间
     *
     * @return effectiveFrom - 生效时间
     */
    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * 设置生效时间
     *
     * @param effectiveFrom 生效时间
     */
    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /**
     * 获取失效时间
     *
     * @return effectiveTo - 失效时间
     */
    public Date getEffectiveTo() {
        return effectiveTo;
    }

    /**
     * 设置失效时间
     *
     * @param effectiveTo 失效时间
     */
    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    /**
     * 获取删除标准
     *
     * @return disabled - 删除标准
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * 设置删除标准
     *
     * @param disabled 删除标准
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
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