package com.dp.plat.pms.springmvc.vo;

import com.dp.plat.pms.springmvc.entity.PmWorkFlow;

/**
 * @author w02611
 *
 */
public class PmWorkFlowVO extends PmWorkFlow {

	private String participantIds;

	/**
	 * @return the participantIds
	 */
	public String getParticipantIds() {
		return participantIds;
	}

	/**
	 * @param participantIds the participantIds to set
	 */
	public void setParticipantIds(String participantIds) {
		this.participantIds = participantIds;
	}
	
}
