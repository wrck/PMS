package com.dp.plat.util;

import com.dp.plat.data.bean.PmClQuesnaireResultHeader;

/**
 *问卷评分接口，不同评分规则需创建一个类实现该接口，并实现评分方法
 * @author minxue
 *
 */
public interface PmClosedLoopMark {
	/**
	 * 评分规则说明
	 */
	String markExplain="";
	
	/**
	 * 评分方法
	 * @param pmClQuesResultHeader 问卷结果对象
	 * @return 返回评分结果，1代表通过，-1代表驳回
	 */
	String quesMark(PmClQuesnaireResultHeader pmClQuesResultHeader);
}
