package com.dp.plat.data.bean;
/**
 * 项目统计综述
 * @author admin
 *
 */

public class StatisticsSummarize {
	private int totalNum;//全国项目总数、已发货
	private int engineeringTypeNum;//工程类项目总数
	private int commonTypeNum;//普通类项目总数
	private int assignedNum;//已指派项目经理项目总数
	private int traceNum;//在跟踪项目总数
	
	public StatisticsSummarize(int totalNum, int engineeringTypeNum,
			int commonTypeNum, int assignedNum, int traceNum) {
		super();
		this.totalNum = totalNum;
		this.engineeringTypeNum = engineeringTypeNum;
		this.commonTypeNum = commonTypeNum;
		this.assignedNum = assignedNum;
		this.traceNum = traceNum;
	}
	
	public StatisticsSummarize() {
		
	}

	public int getTotalNum() {
		return totalNum;
	}

	public int getEngineeringTypeNum() {
		return engineeringTypeNum;
	}

	public int getCommonTypeNum() {
		return commonTypeNum;
	}

	public int getAssignedNum() {
		return assignedNum;
	}

	public int getTraceNum() {
		return traceNum;
	}
	
	
}
