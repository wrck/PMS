package com.dp.plat.ehr.vo;

import java.util.List;

import com.dp.plat.ehr.entity.Department;

public class DepartmentVO extends Department {
	private String empIDs;
	private String empIDsExclude;

	private String workNos;
	private String workNosExclude;

	private String depIDs;
	private String depIDsExclude;
	
	private String depLV1ID;
	private String depLV1Name;
	private String depLV2ID;
	private String depLV2Name;
	private String depLV3ID;
	private String depLV3Name;
	private String depAllName;
	private String compName;
	
	private List<DepartmentVO> childrenList;
	
	public String getEmpIDs() {
		return empIDs;
	}

	public void setEmpIDs(String empIDs) {
		this.empIDs = empIDs;
	}

	public String getEmpIDsExclude() {
		return empIDsExclude;
	}

	public void setEmpIDsExclude(String empIDsExclude) {
		this.empIDsExclude = empIDsExclude;
	}

	public String getWorkNos() {
		return workNos;
	}

	public void setWorkNos(String workNos) {
		this.workNos = workNos;
	}

	public String getWorkNosExclude() {
		return workNosExclude;
	}

	public void setWorkNosExclude(String workNosExclude) {
		this.workNosExclude = workNosExclude;
	}

	public String getDepIDs() {
		return depIDs;
	}

	public void setDepIDs(String depIDs) {
		this.depIDs = depIDs;
	}

	public String getDepIDsExclude() {
		return depIDsExclude;
	}

	public void setDepIDsExclude(String depIDsExclude) {
		this.depIDsExclude = depIDsExclude;
	}

	public List<DepartmentVO> getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(List<DepartmentVO> childrenList) {
		this.childrenList = childrenList;
	}

	public String getDepLV1ID() {
		return depLV1ID;
	}

	public void setDepLV1ID(String depLV1ID) {
		this.depLV1ID = depLV1ID;
	}

	public String getDepLV1Name() {
		return depLV1Name;
	}

	public void setDepLV1Name(String depLV1Name) {
		this.depLV1Name = depLV1Name;
	}

	public String getDepLV2ID() {
		return depLV2ID;
	}

	public void setDepLV2ID(String depLV2ID) {
		this.depLV2ID = depLV2ID;
	}

	public String getDepLV2Name() {
		return depLV2Name;
	}

	public void setDepLV2Name(String depLV2Name) {
		this.depLV2Name = depLV2Name;
	}

	public String getDepLV3ID() {
		return depLV3ID;
	}

	public void setDepLV3ID(String depLV3ID) {
		this.depLV3ID = depLV3ID;
	}

	public String getDepLV3Name() {
		return depLV3Name;
	}

	public void setDepLV3Name(String depLV3Name) {
		this.depLV3Name = depLV3Name;
	}

	public String getDepAllName() {
		return depAllName;
	}

	public void setDepAllName(String depAllName) {
		this.depAllName = depAllName;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

}
