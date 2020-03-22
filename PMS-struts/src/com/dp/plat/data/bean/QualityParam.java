package com.dp.plat.data.bean;
/**
 * 项目质量管理报表参数
 * @author admin
 *
 */
public class QualityParam {
	private double avgCloseScore;
	private int projectSize;
	private String officeCode;
	public double getAvgCloseScore() {
		return avgCloseScore;
	}
	public void setAvgCloseScore(double avgCloseScore) {
		this.avgCloseScore = avgCloseScore;
	}
	public int getProjectSize() {
		return projectSize;
	}
	public void setProjectSize(int projectSize) {
		this.projectSize = projectSize;
	}
	public String getOfficeCode() {
		return officeCode;
	}
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}
	
	
}
