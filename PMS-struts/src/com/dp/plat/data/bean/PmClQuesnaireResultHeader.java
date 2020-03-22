package com.dp.plat.data.bean;

import java.util.Date;
import java.util.List;

public class PmClQuesnaireResultHeader {
	private int id;
	private String questionnaireNum;
	private int evaluationHeaderId;
	private int quesnaireTemplateHeaderId;
	private double quesTotalScore;
	private double quesMarkScore;
	private double quesPassScore;
	private String quesAnw;	//问卷答案，用于规则评分，(格式：题目类型:题号-答案,题号-答案;)+    如：10:1-A,2-B,3-C;20:4-A,5-R;   
	private String updatedPerson;
	private Date updatedTime;
	private Date createdTime;
	private String createdPerson;
	private int quesMarkResult; 	//评分结果
	private List<PmClQuesnaireResultLine>resultLineList;
	private PmClosedLoopQuesnaire quesnaireTemp;	//问卷模板
	private int status;
	private List<String>quesResultMarkList;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getQuestionnaireNum() {
		return questionnaireNum;
	}
	public void setQuestionnaireNum(String questionnaireNum) {
		this.questionnaireNum = questionnaireNum;
	}
	public int getEvaluationHeaderId() {
		return evaluationHeaderId;
	}
	public void setEvaluationHeaderId(int evaluationHeaderId) {
		this.evaluationHeaderId = evaluationHeaderId;
	}
	public int getQuesnaireTemplateHeaderId() {
		return quesnaireTemplateHeaderId;
	}
	public void setQuesnaireTemplateHeaderId(int quesnaireTemplateHeaderId) {
		this.quesnaireTemplateHeaderId = quesnaireTemplateHeaderId;
	}
	public double getQuesTotalScore() {
		return quesTotalScore;
	}
	public void setQuesTotalScore(double quesTotalScore) {
		this.quesTotalScore = quesTotalScore;
	}
	public double getQuesMarkScore() {
		return quesMarkScore;
	}
	public void setQuesMarkScore(double quesMarkScore) {
		this.quesMarkScore = quesMarkScore;
	}
	public double getQuesPassScore() {
		return quesPassScore;
	}
	public void setQuesPassScore(double quesPassScore) {
		this.quesPassScore = quesPassScore;
	}
	public String getQuesAnw() {
		return quesAnw;
	}
	public void setQuesAnw(String quesAnw) {
		this.quesAnw = quesAnw;
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
	public int getQuesMarkResult() {
		return quesMarkResult;
	}
	public void setQuesMarkResult(int quesMarkResult) {
		this.quesMarkResult = quesMarkResult;
	}
	public List<PmClQuesnaireResultLine> getResultLineList() {
		return resultLineList;
	}
	public void setResultLineList(List<PmClQuesnaireResultLine> resultLineList) {
		this.resultLineList = resultLineList;
	}
	public PmClosedLoopQuesnaire getQuesnaireTemp() {
		return quesnaireTemp;
	}
	public void setQuesnaireTemp(PmClosedLoopQuesnaire quesnaireTemp) {
		this.quesnaireTemp = quesnaireTemp;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List<String> getQuesResultMarkList() {
		return quesResultMarkList;
	}
	public void setQuesResultMarkList(List<String> quesResultMarkList) {
		this.quesResultMarkList = quesResultMarkList;
	}
	
	
}
