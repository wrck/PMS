package com.dp.plat.util;

import com.dp.plat.data.bean.PmClQuesnaireResultHeader;

/**
 * 问卷中，如果工程师满意度任意单选题出现选项C，则驳回
 * @author minxue
 *
 */
public class PmClosedLoopMarkImplB implements PmClosedLoopMark {
	public String markExplain="问卷中，如果工程师满意度任意单选题出现选项C，则驳回";

	@Override
	public String quesMark(PmClQuesnaireResultHeader pmClQuesResultHeader) {
		if(pmClQuesResultHeader.getQuesAnw()==null||pmClQuesResultHeader.getQuesAnw().equals("")){
			return "-2";
		}
		String[] anwArr=pmClQuesResultHeader.getQuesAnw().split(";");	//quesAnw:问卷答案，用于规则评分，(格式：题目类型:题号-答案,题号-答案;)+    如：10:1-A,2-B,3-C;20:4-A,5-R;
		StringBuilder resultBuilder=new StringBuilder();
		for (String anwArrEle : anwArr) {
			if(anwArrEle.contains(PmClosedLoopConstant.CL_QUESNAIRE_LINE_TYPE3+":")){
				anwArrEle=anwArrEle.replace(PmClosedLoopConstant.CL_QUESNAIRE_LINE_TYPE3+":", "");
				for (String optEle : anwArrEle.split(",")) {
					if (optEle.contains("|C")) {
						resultBuilder.append(optEle.split("-")[0]+",");	
					}
				}
				
			}
		}
		if(resultBuilder.length()<=0){
			resultBuilder.append("pass");	//评分通过
		}
		return resultBuilder.toString();
	}
	
	public String toString() {
		return markExplain;
	};

}
