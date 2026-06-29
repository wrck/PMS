/**
 * 
 */
package com.dp.plat.activiti.entity;

import java.io.Serializable;
import java.util.List;

import com.dp.plat.core.pojo.User;

/**
 * @author w02611
 *
 */
public class Indicator implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2665930723766427132L;

	private Integer id;
	
	private List<User> signerList;
	
	private List<User> assigneeList;
	
	private Boolean isParallel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<User> getSignerList() {
		return signerList;
	}

	public void setSignerList(List<User> signerList) {
		this.signerList = signerList;
	}

	public List<User> getAssigneeList() {
		return assigneeList;
	}

	public void setAssigneeList(List<User> assigneeList) {
		this.assigneeList = assigneeList;
	}

	public Boolean getIsParallel() {
		return isParallel;
	}

	public void setIsParallel(Boolean isParallel) {
		this.isParallel = isParallel;
	}
	
}
