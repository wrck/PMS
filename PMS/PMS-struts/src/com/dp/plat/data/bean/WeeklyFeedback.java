package com.dp.plat.data.bean;

import java.util.Date;

/**
 * 项目周报回复内容
 * @author admin
 *
 */
public class WeeklyFeedback {
	private int id;
	private int weeklyId;
	private String feedback;
	private String feedbacker;
	private Date feedbackTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getWeeklyId() {
		return weeklyId;
	}
	public void setWeeklyId(int weeklyId) {
		this.weeklyId = weeklyId;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public String getFeedbacker() {
		return feedbacker;
	}
	public void setFeedbacker(String feedbacker) {
		this.feedbacker = feedbacker;
	}
	public Date getFeedbackTime() {
		return feedbackTime;
	}
	public void setFeedbackTime(Date feedbackTime) {
		this.feedbackTime = feedbackTime;
	}
	
	
	
}
