package com.dp.plat.activiti.entity;

import java.util.Date;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Vacation extends BaseVO{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1495795296316800235L;
	// 带薪假
	public final static int TYPE_PAID = 0;	
	// 病假
	public final static int TYPE_SICK = 1;
	// 事假
	public final static int TYPE_MATTER = 2;
	
    private Integer id;

    @JsonSerialize(using=JsonSerializer.class)
    private Date applyDate;

    @JsonSerialize(using=JsonSerializer.class)
    private Date beginDate;

    private Integer workDays;

    @JsonSerialize(using=JsonSerializer.class)
    private Date endDate;

    private String procInstId;

    private String reason;

    private String status;

    private Integer userId;

    private Integer vacType;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Integer getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Integer workDays) {
        this.workDays = workDays;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getVacType() {
        return vacType;
    }

    public void setVacType(Integer vacType) {
        this.vacType = vacType;
    }
}
