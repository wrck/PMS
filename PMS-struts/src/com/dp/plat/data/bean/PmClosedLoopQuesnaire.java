package com.dp.plat.data.bean;

import java.util.Date;
import java.util.List;

import com.dp.plat.util.PmClosedLoopMark;

public class PmClosedLoopQuesnaire {
	private int id; 
	private String questionnaireTemplateNum; //问卷模板编号
	private String questionnaireTemplateName; //问卷模板名称
	private double questionnaireScore; //问卷总分数
	private double questionnairePassScore; //问卷达标分数
	private int    questionnaireStatus; //问卷状态
	private Date effectiveStartTime; 
	private Date effectiveEndTime; 
	private Date createdTime;
	private String createdPerson;
	private String updatedPerson;
	private Date updatedTime;
	private String quesType;
	private List<PmClosedLoopQuesnaireLine>pmCLQuesLineList;
	private List<PmClosedLoopQuesnaireOpt>pmCLQuesOptList;
	private String markIndexs;
	private List<PmClosedLoopMark> markList;
	private String quesTypeName;

	public int getId() { 
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getQuestionnaireTemplateNum() {
		return questionnaireTemplateNum;
	}
	public void setQuestionnaireTemplateNum(String questionnaireTemplateNum) {
		this.questionnaireTemplateNum = questionnaireTemplateNum;
	}
	public String getQuestionnaireTemplateName() {
		return questionnaireTemplateName;
	}
	public void setQuestionnaireTemplateName(String questionnaireTemplateName) {
		this.questionnaireTemplateName = questionnaireTemplateName;
	}
	public double getQuestionnaireScore() {
		return questionnaireScore;
	}
	public void setQuestionnaireScore(double questionnaireScore) {
		this.questionnaireScore = questionnaireScore;
	}
	public double getQuestionnairePassScore() {
		return questionnairePassScore;
	}
	public void setQuestionnairePassScore(double questionnairePassScore) {
		this.questionnairePassScore = questionnairePassScore;
	}
	public int getQuestionnaireStatus() {
		return questionnaireStatus;
	}
	public void setQuestionnaireStatus(int questionnaireStatus) {
		this.questionnaireStatus = questionnaireStatus;
	}
	public Date getEffectiveStartTime() {
		return effectiveStartTime;
	}
	public void setEffectiveStartTime(Date effectiveStartTime) {
		this.effectiveStartTime = effectiveStartTime;
	}
	public Date getEffectiveEndTime() {
		return effectiveEndTime;
	}
	public void setEffectiveEndTime(Date effectiveEndTime) {
		this.effectiveEndTime = effectiveEndTime;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
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
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	public String getQuesType() {
		return quesType;
	}
	public void setQuesType(String quesType) {
		this.quesType = quesType;
	}
	public List<PmClosedLoopQuesnaireLine> getPmCLQuesLineList() {
		return pmCLQuesLineList;
	}
	public void setPmCLQuesLineList(List<PmClosedLoopQuesnaireLine> pmCLQuesLineList) {
		this.pmCLQuesLineList = pmCLQuesLineList;
	}
	public List<PmClosedLoopQuesnaireOpt> getPmCLQuesOptList() {
		return pmCLQuesOptList;
	}
	public void setPmCLQuesOptList(List<PmClosedLoopQuesnaireOpt> pmCLQuesOptList) {
		this.pmCLQuesOptList = pmCLQuesOptList;
	}
	public String getMarkIndexs() {
		return markIndexs;
	}
	public void setMarkIndexs(String markIndexs) {
		this.markIndexs = markIndexs;
	}
	public List<PmClosedLoopMark> getMarkList() {
		return markList;
	}
	public void setMarkList(List<PmClosedLoopMark> markList) {
		this.markList = markList;
	}
	public String getQuesTypeName() {
		return quesTypeName;
	}
	public void setQuesTypeName(String quesTypeName) {
		this.quesTypeName = quesTypeName;
	}
	
	
}
