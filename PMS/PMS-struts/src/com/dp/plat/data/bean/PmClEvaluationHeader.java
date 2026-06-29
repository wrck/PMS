package com.dp.plat.data.bean;

import java.util.Date;
import java.util.List;

import com.dp.plat.util.Util;

public class PmClEvaluationHeader {
	private String projectCode;
	private int status;
	private Date createdTime;
	private double evaluationScore;
	private Date evaluationTime;
	private String evaluationPeopleName;
	private String createdPerson;
	private String updatedPerson;
	private int id;
	private String evaluationComment;
	private int evaluationResult;
	private int evaluationType;
	private Date updatedTime;
	private String nextAcceptPerson;
	private String createdTimeStr;
	private String evaluationPeopleId;
	private String nextAcceptPersonName;
	private String projectName;
	private int projectId;
	private int applyHeaderId;
	private List<PmClQuesnaireResultHeader>resultHeaderList;
	private List<PmClQuesnaireResultLine>resultLineList;
	private String applyPersonId;
	private String applyPersonName;
	private Date applyTime;
	private String officeName;
	
	private String projectCustomer;
	private String projectImpl;
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		setCreatedTimeStr(Util.dateFormat(createdTime,"yyyy-MM-dd HH:mm:ss"));
		this.createdTime = createdTime;
	}
	public double getEvaluationScore() {
		return evaluationScore;
	}
	public void setEvaluationScore(double evaluationScore) {
		this.evaluationScore = evaluationScore;
	}
	public Date getEvaluationTime() {
		return evaluationTime;
	}
	public void setEvaluationTime(Date evaluationTime) {
		this.evaluationTime = evaluationTime;
	}
	public String getEvaluationPeopleName() {
		return evaluationPeopleName;
	}
	public void setEvaluationPeopleName(String evaluationPeopleName) {
		this.evaluationPeopleName = evaluationPeopleName;
	}
	public String getCreatedPerson() {
		return createdPerson;
	}
	public void setCreatedPerson(String createdPerson) {
		this.createdPerson = createdPerson;
	}
	
	public String getUpdatedPerson() {
		return updatedPerson;
	}
	public void setUpdatedPerson(String updatedPerson) {
		this.updatedPerson = updatedPerson;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEvaluationComment() {
		return evaluationComment;
	}
	public void setEvaluationComment(String evaluationComment) {
		this.evaluationComment = evaluationComment;
	}
	public int getEvaluationResult() {
		return evaluationResult;
	}
	public void setEvaluationResult(int evaluationResult) {
		this.evaluationResult = evaluationResult;
	}
	public int getEvaluationType() {
		return evaluationType;
	}
	public void setEvaluationType(int evaluationType) {
		this.evaluationType = evaluationType;
	}
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	public String getNextAcceptPerson() {
		return nextAcceptPerson;
	}
	public void setNextAcceptPerson(String nextAcceptPerson) {
		this.nextAcceptPerson = nextAcceptPerson;
	}
	public String getCreatedTimeStr() {
		return createdTimeStr;
	}
	public void setCreatedTimeStr(String createdTimeStr) {
		this.createdTimeStr = createdTimeStr;
	}
	
	public String getEvaluationPeopleId() {
		return evaluationPeopleId;
	}
	public void setEvaluationPeopleId(String evaluationPeopleId) {
		this.evaluationPeopleId = evaluationPeopleId;
	}
	public String getNextAcceptPersonName() {
		return nextAcceptPersonName;
	}
	public void setNextAcceptPersonName(String nextAcceptPersonName) {
		this.nextAcceptPersonName = nextAcceptPersonName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getApplyHeaderId() {
		return applyHeaderId;
	}
	public void setApplyHeaderId(int applyHeaderId) {
		this.applyHeaderId = applyHeaderId;
	}
	public List<PmClQuesnaireResultHeader> getResultHeaderList() {
		return resultHeaderList;
	}
	public void setResultHeaderList(List<PmClQuesnaireResultHeader> resultHeaderList) {
		this.resultHeaderList = resultHeaderList;
	}
	public List<PmClQuesnaireResultLine> getResultLineList() {
		return resultLineList;
	}
	public void setResultLineList(List<PmClQuesnaireResultLine> resultLineList) {
		this.resultLineList = resultLineList;
	}
	public String getApplyPersonId() {
		return applyPersonId;
	}
	public void setApplyPersonId(String applyPersonId) {
		this.applyPersonId = applyPersonId;
	}
	public String getApplyPersonName() {
		return applyPersonName;
	}
	public void setApplyPersonName(String applyPersonName) {
		this.applyPersonName = applyPersonName;
	}
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public String getProjectCustomer() {
		return projectCustomer;
	}
	public void setProjectCustomer(String projectCustomer) {
		this.projectCustomer = projectCustomer;
	}
	public String getProjectImpl() {
		return projectImpl;
	}
	public void setProjectImpl(String projectImpl) {
		this.projectImpl = projectImpl;
	}
	
}
