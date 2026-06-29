package com.dp.plat.core.pojo;

import java.util.Date;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BasePojo {

	private Integer id;
	/**
	 * 排序值，自定义参数
	 */
	private Integer sort;
	/**
	 * 节点状态值（有效、失效）编辑时使用，自定义参数
	 */
	private Boolean status;
	
	@JsonSerialize(using=JsonSerializer.class)
	private Boolean statusJson;
	
	private String createBy;
	@JsonSerialize(using=JsonSerializer.class)
	private Date createTime;
	private String updateBy;
	@JsonSerialize(using=JsonSerializer.class)
	private Date updateTime; 
	
	
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
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	public Boolean getStatusJson() {
		return status;
	}
	
}
