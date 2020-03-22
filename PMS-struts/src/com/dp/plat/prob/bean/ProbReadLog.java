package com.dp.plat.prob.bean;

import java.util.Date;

/**
 * 技术公告查看记录
 * 
 * @author w02611
 *
 */
public class ProbReadLog {
	private int id;
	private int probId;
	private String reader;
	private Date readTime;
	private Integer status;
	private Date firstTime;
	private Date commitTime;

	private String readerName;
	
	public ProbReadLog() {
	}

	public ProbReadLog(int probId, String reader) {
		this.probId = probId;
		this.reader = reader;
	}

	public ProbReadLog(int probId, String reader, Integer status) {
		this.probId = probId;
		this.reader = reader;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProbId() {
		return probId;
	}

	public void setProbId(int probId) {
		this.probId = probId;
	}

	public String getReader() {
		return reader;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public Date getReadTime() {
		return readTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

	public String getReaderName() {
		return readerName;
	}

	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}

	public Date getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(Date commitTime) {
		this.commitTime = commitTime;
	}

}
