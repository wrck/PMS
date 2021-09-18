/**
 * 
 */
package com.dp.plat.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dp.plat.core.context.UserContext;
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
	
	private Integer orgId;
	
	private Map customInfo;

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
		if (this.orgId == null) {
			return UserContext.getOrgId();
		}
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Map getCustomInfo() {
		return customInfo;
	}

	public void setCustomInfo(Map customInfo) {
		this.customInfo = customInfo;
	}

	public Object getCustomInfoByKey(String key) {
		Map<?, ?> customInfo = getCustomInfo();
		if (customInfo != null && !customInfo.isEmpty()) {
			return customInfo.get(key);
		}
		return null;
	}

	public void setCustomInfoByKey(String key, Object value) {
		Map<String, Object> customInfo = (Map<String, Object>) getCustomInfo();
		if (customInfo == null) {
			customInfo = new HashMap<>();
			this.setCustomInfo(customInfo);
		}
		customInfo.put(key, value);
	}

}
