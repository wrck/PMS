package com.dp.plat.data.bean;

import java.util.Date;

import com.dp.plat.context.UserContext;

/**
 * 对应数据库的bean 一般都有创建时间和用户、修改时间和用户、有效时间等共同信息，单独出来
 * @author admin
 *
 */
public class BaseCustomInfoBean extends CustomInfoEntity {
	private static final long serialVersionUID = 3568238758531291244L;
	
    private String createBy;
	private Date createTime;
	private String updateBy;
	private Date updateTime;
	private Date effectiveFrom;
	private Date effectiveTo;
	
	
	public Date getCreateTime() {
		if(createTime != null){
			return this.createTime;
		}
		return new Date();
	}

	public String getCreateBy() {
		if(createBy != null){
			return this.createBy;
		}
		return UserContext.getUserContext().getUsername();
	}

	public Date getUpdateTime() {
		if(updateTime != null){
			return this.updateTime;
		}
		return new Date();
	}

	public String getUpdateBy() {
		if(updateBy != null){
			return this.updateBy;
		}
		return UserContext.getUserContext().getUsername();
	}

	public Date getEffectiveFrom() {
		if(effectiveFrom != null){
			return this.effectiveFrom;
		}
		return new Date();
	}

	public Date getEffectiveTo() {
		if(effectiveTo != null){
			return this.effectiveTo;
		}
		return new Date();
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}
	
}
