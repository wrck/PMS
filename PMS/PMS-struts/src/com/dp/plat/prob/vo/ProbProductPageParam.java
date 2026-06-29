/**
 * 
 */
package com.dp.plat.prob.vo;

import com.dp.plat.param.DisplayParam;

/**
 * @author w02611
 */
public class ProbProductPageParam extends ProbProductVO {

    private static final long serialVersionUID = 7514638065674367088L;

    private DisplayParam displayParam;

    /**
     * 
     */
    public ProbProductPageParam() {
        super();
    }

    /**
     * @param displayParam
     */
    public ProbProductPageParam(DisplayParam displayParam) {
        this.displayParam = displayParam;
    }

    public DisplayParam getDisplayParam() {
        return displayParam;
    }

    public void setDisplayParam(DisplayParam displayParam) {
        this.displayParam = displayParam;
    }

}
