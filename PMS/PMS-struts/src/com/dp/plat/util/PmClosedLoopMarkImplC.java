package com.dp.plat.util;

import com.dp.plat.data.bean.PmClQuesnaireResultHeader;

/**
 *问卷中，如果设备满意度任意单选题出现选项C或者D，则驳回
 * @author minxue
 *
 */
public class PmClosedLoopMarkImplC implements PmClosedLoopMark{
	public String markExplain="问卷中，如果设备满意度任意单选题出现选项C或者D，则驳回";

	@Override
	public String quesMark(PmClQuesnaireResultHeader pmClQuesResultHeader) {
		if(pmClQuesResultHeader.getQuesAnw()==null||pmClQuesResultHeader.getQuesAnw().equals("")){
			return "-2";
		}
		String[] anwArr=pmClQuesResultHeader.getQuesAnw().split(";");	
		StringBuilder resultBuilder=new StringBuilder();
		for (String anwArrEle : anwArr) {
			if(anwArrEle.contains(PmClosedLoopConstant.CL_QUESNAIRE_LINE_TYPE2+":")){
				anwArrEle=anwArrEle.replace(PmClosedLoopConstant.CL_QUESNAIRE_LINE_TYPE2+":", "");
				for (String optEle : anwArrEle.split(",")) {
					if (optEle.contains("|C")||optEle.contains("|D")) {
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
	
	@Override
	public String toString() {
		return markExplain;
	}

}
