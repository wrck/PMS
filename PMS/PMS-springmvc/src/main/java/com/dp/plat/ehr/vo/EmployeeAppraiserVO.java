package com.dp.plat.ehr.vo;

import java.util.List;
import java.util.Map;

import com.dp.plat.ehr.entity.AppraiserRelationship;

public class EmployeeAppraiserVO extends EmployeeVO {
	List<AppraiserRelationship> appraiserRelationshipList;
	Map<String, AppraiserRelationship> appraiserRelationshipMap;

	public List<AppraiserRelationship> getAppraiserRelationshipList() {
		return appraiserRelationshipList;
	}

	public void setAppraiserRelationshipList(List<AppraiserRelationship> appraiserRelationshipList) {
		this.appraiserRelationshipList = appraiserRelationshipList;
	}

	public Map<String, AppraiserRelationship> getAppraiserRelationshipMap() {
		return appraiserRelationshipMap;
	}

	public void setAppraiserRelationshipMap(Map<String, AppraiserRelationship> appraiserRelationshipMap) {
		this.appraiserRelationshipMap = appraiserRelationshipMap;
	}

}
