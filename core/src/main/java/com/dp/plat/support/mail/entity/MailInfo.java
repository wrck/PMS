package com.dp.plat.support.mail.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MailInfo {
	private Integer id;

	private String subject;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date sendTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date expectSendTime;

	private Boolean sendFlag;
	
	private Integer failedCount;

	private String createBy;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String content;

	private String tos;

	private String ccs;

	private String bccs;

	private String actualSendAddress;

	private String attachFiles;

	private Boolean isInner;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getExpectSendTime() {
		return expectSendTime;
	}

	public void setExpectSendTime(Date expectSendTime) {
		this.expectSendTime = expectSendTime;
	}

	public Boolean getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(Boolean sendFlag) {
		this.sendFlag = sendFlag;
	}

	public Integer getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(Integer failedCount) {
		this.failedCount = failedCount;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTos() {
		return tos;
	}

	public void setTos(String tos) {
		this.tos = tos;
	}

	public String getCcs() {
		return ccs;
	}

	public void setCcs(String ccs) {
		this.ccs = ccs;
	}

	public String getBccs() {
		return bccs;
	}

	public void setBccs(String bccs) {
		this.bccs = bccs;
	}

	public String getActualSendAddress() {
		return actualSendAddress;
	}

	public void setActualSendAddress(String actualSendAddress) {
		this.actualSendAddress = actualSendAddress;
	}

	public String getAttachFiles() {
		return attachFiles;
	}

	public void setAttachFiles(String attachFiles) {
		this.attachFiles = attachFiles;
	}

	public Boolean getIsInner() {
		return isInner;
	}

	public void setIsInner(Boolean isInner) {
		this.isInner = isInner;
	}

}