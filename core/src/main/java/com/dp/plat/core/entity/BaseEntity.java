/**
 * 
 */
package com.dp.plat.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author w02611
 *
 */
public class BaseEntity implements Serializable{

	private static final long serialVersionUID = 4016882606830988730L;

	private Integer id;

	private String createBy;

	@JsonSerialize(using=JsonSerializer.class)
	private Date createTime;

	private String updateBy;

	@JsonSerialize(using=JsonSerializer.class)
	private Date updateTime;

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

}
