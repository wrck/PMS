package com.dp.plat.util;

import com.dp.plat.data.bean.PmClQuesnaireResultHeader;

/**
 *评分规则A：低于达标分则驳回
 * @author minxue
 *
 */
public class PmClosedLoopMarkImplA implements PmClosedLoopMark {
	public String markExplain="低于问卷达标分数则驳回";
	@Override
	public String quesMark(PmClQuesnaireResultHeader pmClQuesResultHeader) {
		return pmClQuesResultHeader.getQuesMarkScore()<pmClQuesResultHeader.getQuesPassScore()?"-1":"pass";
	}
	
	@Override
	public String toString() {
		return markExplain;
	}

}
