package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.pms.springmvc.entity.DataFieldRelation;

public class DataFieldRelationVO extends DataFieldRelation {

	/**
	 * 查询带出父类
	 */
	private String dataNameWithSuper;
	
	public DataFieldRelationVO() {
		super();
	}

	public DataFieldRelationVO(String dataName, String dataType, Integer status) {
		super(dataName, dataType, status);
	}

	public DataFieldRelationVO(String dataName, String dataType) {
		super(dataName, dataType);
	}
	
	public DataFieldRelationVO(String dataName, String dataType, Integer status, Boolean widthSuper) {
		this.dataNameWithSuper = dataName;
		this.setDataType(dataType);
		this.setStatus(status);
	}
	
	public DataFieldRelationVO(String dataName, String dataType, Boolean widthSuper) {
		this.dataNameWithSuper = dataName;
		this.setDataType(dataType);
	}

	public String getDataNameWithSuper() {
		return dataNameWithSuper;
	}

	public void setDataNameWithSuper(String dataNameWithSuper) {
		this.dataNameWithSuper = dataNameWithSuper;
	}
	
	
}
