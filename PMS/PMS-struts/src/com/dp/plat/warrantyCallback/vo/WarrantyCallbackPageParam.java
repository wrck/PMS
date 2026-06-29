/**
 * 
 */
package com.dp.plat.warrantyCallback.vo;

import com.dp.plat.param.DisplayParam;

/**
 * @author w02611
 *
 */
public class WarrantyCallbackPageParam extends ProjectWarrantyCallbackVO {

	private DisplayParam displayParam;

	/**
	 * 
	 */
	public WarrantyCallbackPageParam() {
		super();
	}
	
	
	/**
	 * @param displayParam
	 */
	public WarrantyCallbackPageParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
	
	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
	
}
