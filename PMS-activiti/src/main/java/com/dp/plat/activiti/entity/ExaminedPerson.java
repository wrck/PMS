/**
 * 
 */
package com.dp.plat.activiti.entity;

import java.io.Serializable;

import com.dp.plat.core.pojo.User;

/**
 * @author w02611
 *
 */
public class ExaminedPerson extends User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 860221691747798687L;
	
	private Integer supervisorId;
	
	public Integer getSupervisorId() {
		return supervisorId;
	}
	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
	} 
	
	
}
