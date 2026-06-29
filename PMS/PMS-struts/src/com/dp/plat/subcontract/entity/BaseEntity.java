package com.dp.plat.subcontract.entity;

import java.io.Serializable;
import java.util.Date;

import com.dp.plat.data.bean.CustomInfoEntity;

/**
 * @author w02611
 *
 */
public class BaseEntity extends CustomInfoEntity implements Serializable{

    private static final long serialVersionUID = 4016882606830988730L;

    private Integer id;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;
    
    private Integer orgId;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

}
