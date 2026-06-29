/**
 * 
 */
package com.dp.plat.prob.vo;

import com.dp.plat.param.DisplayParam;

/**
 * @author w02611
 *
 */
public class ProductComponentPageParam extends ProductComponentVO {

    private static final long serialVersionUID = -1510567779967651120L;
    
    private DisplayParam displayParam;

	/**
	 * 
	 */
	public ProductComponentPageParam() {
		super();
	}
	
	
	/**
	 * @param displayParam
	 */
	public ProductComponentPageParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
	
	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}
	
}
