/**
 * 
 */
package com.dp.plat.subcontract.vo;

import com.dp.plat.param.DisplayParam;

/**
 * @author w02611
 *
 */
public class SubcontractPageParam extends SubcontractProjectVO {

	private DisplayParam displayParam;

	/**
	 * 
	 */
	public SubcontractPageParam() {
		super();
	}
	
	
	/**
	 * @param displayParam
	 */
	public SubcontractPageParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
	
	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
	
}
