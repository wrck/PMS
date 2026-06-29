package com.dp.plat.util;

import com.dp.plat.data.bean.PmClQuesnaireResultHeader;

public class PmClosedLoopMarkImplD  implements PmClosedLoopMark{
	public String markExplain="问卷中，如果第二题选C或者第四题选择B，则驳回";

	@Override
	public String quesMark(PmClQuesnaireResultHeader pmClQuesResultHeader){
		if(pmClQuesResultHeader.getQuesAnw()==null||pmClQuesResultHeader.getQuesAnw().equals("")){
			return "-2";
		}
		String[] anwArr=pmClQuesResultHeader.getQuesAnw().split(";");	
		StringBuilder resultBuilder=new StringBuilder();
		for (String anwArrEle : anwArr) {
				int start=anwArrEle.indexOf(":");
				anwArrEle=anwArrEle.substring(start+1,anwArrEle.length());
				for (String optEle : anwArrEle.split(",")) {
					if (optEle.contains("-2|C")||optEle.contains("-4|B")) {
						resultBuilder.append(optEle.split("-")[0]+",");	
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
