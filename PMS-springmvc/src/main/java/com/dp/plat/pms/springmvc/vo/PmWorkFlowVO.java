package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.pms.springmvc.entity.PmWorkFlow;

/**
 * @author w02611
 *
 */
public class PmWorkFlowVO extends PmWorkFlow {

	private String taskName;
	private String taskDesc;
	private String areaPower;
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskDesc() {
		return taskDesc;
	}
	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}
	public String getAreaPower() {
		return areaPower;
	}
	public void setAreaPower(String areaPower) {
		this.areaPower = areaPower;
	}
	
}
