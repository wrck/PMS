package com.dp.plat.data.bean;

import java.util.Date;

/**
 * 基础数据bean
 * 
 * @author admin
 *
 */
public class BasicDataBean {
	private int id;
	private String basicDataTypeCode;
	private String basicDataTypeName;
	private String basicDataId;
	private String basicDataName;
	private String basicDataAttri1;// 属性1
	private Integer sortId;
	private Date createTime;
	private String createBy;
	private Date effectiveFrom;
	private Date effectiveTo;

	public String getBasicDataTypeCode() {
		return basicDataTypeCode;
	}

	public void setBasicDataTypeCode(String basicDataTypeCode) {
		this.basicDataTypeCode = basicDataTypeCode;
	}

	public String getBasicDataTypeName() {
		return basicDataTypeName;
	}

	public void setBasicDataTypeName(String basicDataTypeName) {
		this.basicDataTypeName = basicDataTypeName;
	}

	public String getBasicDataId() {
		return basicDataId;
	}

	public void setBasicDataId(String basicDataId) {
		this.basicDataId = basicDataId;
	}

	public String getBasicDataName() {
		return basicDataName;
	}

	public void setBasicDataName(String basicDataName) {
		this.basicDataName = basicDataName;
	}

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getBasicDataAttri1() {
		return basicDataAttri1;
	}

	public void setBasicDataAttri1(String basicDataAttri1) {
		this.basicDataAttri1 = basicDataAttri1;
	}
}
