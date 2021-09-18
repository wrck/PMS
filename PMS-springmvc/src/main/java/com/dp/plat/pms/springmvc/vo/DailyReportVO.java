package com.dp.plat.pms.springmvc.vo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dp.plat.param.FileParam;
import com.dp.plat.pms.springmvc.entity.DailyReport;

public class DailyReportVO extends DailyReport {

	private static final long serialVersionUID = 8052617692266023054L;
	
	private String officeCodes;
	private String projectTypes;
	private boolean hasPower;
	private String officeName;
	private String createUser;
	private String areaPower;
	private String userPower;
	private Integer userIdPower;
	private String memberCode;
	
	private String typeName;
	private String projectTypeName;
	private String categoryName;
	private String subCategoryName;

	private String serviceManager;
	private String programManager;
	private String programManagerA;
	private String programManagerB;

	private Date processStartTime;
	private Date processEndTime;
	private Date createStartTime;
	private Date createEndTime;

	private List<FileParam> deliverFileList;
	private List<Map<String, String>> quesnaireResultList;
	private Map<String, Object> questionColumns;

	private Boolean hideQuesnaire;

	private Integer maxId;
	
	private List<String> ids;
	private String idsStr;
	
	private String categorysStr;
	private String subCategorysStr;
	private List<String> categorys;
	private List<String> subCategorys;

	public boolean isHasPower() {
		return hasPower;
	}

	public void setHasPower(boolean hasPower) {
		this.hasPower = hasPower;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public String getProjectTypeName() {
		return projectTypeName;
	}

	public void setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getAreaPower() {
		return areaPower;
	}

	public void setAreaPower(String areaPower) {
		this.areaPower = areaPower;
	}

	public String getUserPower() {
		return userPower;
	}

	public void setUserPower(String userPower) {
		this.userPower = userPower;
	}
	
	public Integer getUserIdPower() {
		return userIdPower;
	}

	public void setUserIdPower(Integer userIdPower) {
		this.userIdPower = userIdPower;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(String serviceManager) {
		this.serviceManager = serviceManager;
	}

	public String getProgramManager() {
		return programManager;
	}

	public void setProgramManager(String programManager) {
		this.programManager = programManager;
	}

	public String getProgramManagerA() {
		return programManagerA;
	}

	public void setProgramManagerA(String programManagerA) {
		this.programManagerA = programManagerA;
	}

	public String getProgramManagerB() {
		return programManagerB;
	}

	public void setProgramManagerB(String programManagerB) {
		this.programManagerB = programManagerB;
	}

	public Date getProcessStartTime() {
		return processStartTime;
	}

	public void setProcessStartTime(Date processStartTime) {
		this.processStartTime = processStartTime;
	}

	public Date getProcessEndTime() {
		return processEndTime;
	}

	public void setProcessEndTime(Date processEndTime) {
		this.processEndTime = processEndTime;
	}

	public Date getCreateStartTime() {
		return createStartTime;
	}

	public void setCreateStartTime(Date createStartTime) {
		this.createStartTime = createStartTime;
	}

	public Date getCreateEndTime() {
		return createEndTime;
	}

	public void setCreateEndTime(Date createEndTime) {
		this.createEndTime = createEndTime;
	}

	public List<FileParam> getDeliverFileList() {
		return deliverFileList;
	}

	public void setDeliverFileList(List<FileParam> deliverFileList) {
		this.deliverFileList = deliverFileList;
	}

	public List<Map<String, String>> getQuesnaireResultList() {
		return quesnaireResultList;
	}

	public void setQuesnaireResultList(List<Map<String, String>> quesnaireResultList) {
		this.quesnaireResultList = quesnaireResultList;
	}

	public Map<String, Object> getQuestionColumns() {
		return questionColumns;
	}

	public void setQuestionColumns(Map<String, Object> questionColumns) {
		this.questionColumns = questionColumns;
	}

	public Boolean getHideQuesnaire() {
		return hideQuesnaire;
	}

	public void setHideQuesnaire(Boolean hideQuesnaire) {
		this.hideQuesnaire = hideQuesnaire;
	}

	public Integer getMaxId() {
		return maxId;
	}

	public void setMaxId(Integer maxId) {
		this.maxId = maxId;
	}

	public String getOfficeCodes() {
		return officeCodes;
	}

	public void setOfficeCodes(String officeCodes) {
		this.officeCodes = officeCodes;
	}

	public String getProjectTypes() {
		return projectTypes;
	}

	public void setProjectTypes(String projectTypes) {
		this.projectTypes = projectTypes;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
		if(ids != null) {
			this.idsStr = String.join(",", ids);
		}
	}

	public String getIdsStr() {
		return idsStr;
	}

	public void setIdsStr(String idsStr) {
		this.idsStr = idsStr;
		if (idsStr != null) {
			this.ids = Arrays.asList(idsStr.split(","));
		}
	}

	public String getCategorysStr() {
		return categorysStr;
	}

	public void setCategorysStr(String categorysStr) {
		this.categorysStr = categorysStr;
		if (categorysStr != null) {
			this.categorys = Arrays.asList(categorysStr.split(","));
		}
	}

	public String getSubCategorysStr() {
		return subCategorysStr;
	}

	public void setSubCategorysStr(String subCategorysStr) {
		this.subCategorysStr = subCategorysStr;
		if (subCategorysStr != null) {
			this.subCategorys = Arrays.asList(subCategorysStr.split(","));
		}
	}

	public List<String> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<String> categorys) {
		this.categorys = categorys;
		if(categorys != null) {
			this.categorysStr = String.join(",", categorys);
		}
	}

	public List<String> getSubCategorys() {
		return subCategorys;
	}

	public void setSubCategorys(List<String> subCategorys) {
		this.subCategorys = subCategorys;
		if(subCategorys != null) {
			this.subCategorysStr = String.join(",", subCategorys);
		}
	}

}
