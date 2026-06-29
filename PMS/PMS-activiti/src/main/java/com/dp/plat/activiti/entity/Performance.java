package com.dp.plat.activiti.entity;

import java.util.Date;
import java.util.List;

import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Performance extends BaseVO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1495795296316800235L;
	
    private Integer id;

    @JsonSerialize(using=JsonSerializer.class)
    private Date applyDate;

    @JsonSerialize(using=JsonSerializer.class)
    private Date beginDate;

    @JsonSerialize(using=JsonSerializer.class)
    private Date endDate;

    @JsonSerialize(using=JsonSerializer.class)
    private Date dueDate;
    
    private String procInstId;

    private String reason;

    private String status;

    private Integer userId;

    private List<ExaminedPerson> examinedPersonList;
    
    private List<Indicator> indicatorsList;
    
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

	public List<ExaminedPerson> getExaminedPersonList() {
		return examinedPersonList;
	}

	public void setExaminedPersonList(List<ExaminedPerson> examinedPersonList) {
		this.examinedPersonList = examinedPersonList;
	}

	public List<Indicator> getIndicatorsList() {
		return indicatorsList;
	}

	public void setIndicatorsList(List<Indicator> indicatorsList) {
		this.indicatorsList = indicatorsList;
	}
	
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public boolean isComplete(ActivityExecution execution) {
		return this.dueDate.getTime() < new Date().getTime();
	}
	
}
