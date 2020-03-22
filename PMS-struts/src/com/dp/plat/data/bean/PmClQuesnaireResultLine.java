package com.dp.plat.data.bean;

import java.util.Date;

public class PmClQuesnaireResultLine {
	private int id;
	private int quesnaireTemplateLineId;
	private String questionAnswer;
	private int quesnaireTemplateHeaderId;
	private double questionScore;
	private int questionTemplateOptId;
	private int quesnaireResultHeaderId;
	private String quesTypeForCB;
	private int quesTemplateLineNum;
	private String updatedPerson;
	private Date updatedTime;
	private Date createdTime;
	private String createdPerson;
	private int quesEvaResult;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getQuesnaireTemplateLineId() {
		return quesnaireTemplateLineId;
	}
	public void setQuesnaireTemplateLineId(int quesnaireTemplateLineId) {
		this.quesnaireTemplateLineId = quesnaireTemplateLineId;
	}
	public String getQuestionAnswer() {
		return questionAnswer;
	}
	public void setQuestionAnswer(String questionAnswer) {
		this.questionAnswer = questionAnswer;
	}
	public int getQuesnaireTemplateHeaderId() {
		return quesnaireTemplateHeaderId;
	}
	public void setQuesnaireTemplateHeaderId(int quesnaireTemplateHeaderId) {
		this.quesnaireTemplateHeaderId = quesnaireTemplateHeaderId;
	}
	public double getQuestionScore() {
		return questionScore;
	}
	public void setQuestionScore(double questionScore) {
		this.questionScore = questionScore;
	}
	public int getQuestionTemplateOptId() {
		return questionTemplateOptId;
	}
	public void setQuestionTemplateOptId(int questionTemplateOptId) {
		this.questionTemplateOptId = questionTemplateOptId;
	}
	public int getQuesnaireResultHeaderId() {
		return quesnaireResultHeaderId;
	}
	public void setQuesnaireResultHeaderId(int quesnaireResultHeaderId) {
		this.quesnaireResultHeaderId = quesnaireResultHeaderId;
	}
	public String getQuesTypeForCB() {
		return quesTypeForCB;
	}
	public void setQuesTypeForCB(String quesTypeForCB) {
		this.quesTypeForCB = quesTypeForCB;
	}
	public int getQuesTemplateLineNum() {
		return quesTemplateLineNum;
	}
	public void setQuesTemplateLineNum(int quesTemplateLineNum) {
		this.quesTemplateLineNum = quesTemplateLineNum;
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
	public int getQuesEvaResult() {
		return quesEvaResult;
	}
	public void setQuesEvaResult(int quesEvaResult) {
		this.quesEvaResult = quesEvaResult;
	}
	

}
