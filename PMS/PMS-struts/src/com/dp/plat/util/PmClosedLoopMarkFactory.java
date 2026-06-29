package com.dp.plat.util;

import java.util.ArrayList;
import java.util.List;

public class PmClosedLoopMarkFactory {
	List<PmClosedLoopMark>pmClosedLoopMarkList;
	public PmClosedLoopMarkFactory() {
		pmClosedLoopMarkList=new ArrayList<PmClosedLoopMark>();
		pmClosedLoopMarkList.add(new PmClosedLoopMarkImplA());
		pmClosedLoopMarkList.add(new PmClosedLoopMarkImplB());
		pmClosedLoopMarkList.add(new PmClosedLoopMarkImplC());
		pmClosedLoopMarkList.add(new PmClosedLoopMarkImplD());
	}
	public List<PmClosedLoopMark>getAllMark(){
		return pmClosedLoopMarkList;
	}
	
	public List<PmClosedLoopMark> getMarks(String markIndex) {
		List<PmClosedLoopMark>pmClosedLoopMarkSub=new ArrayList<PmClosedLoopMark>();
		if(markIndex==null||markIndex.equals("")){
			return null;
		}
		
		for (String indexStr : markIndex.split(",")) {
			if(Integer.parseInt(indexStr)>0||Integer.parseInt(indexStr)<pmClosedLoopMarkList.size()){
				pmClosedLoopMarkSub.add(pmClosedLoopMarkList.get(Integer.parseInt(indexStr)));
			}else{
				return null;
			}
		}
		return pmClosedLoopMarkSub;
	}
	
	public List<String> getMarksExplain(String markIndex) {
		List<String>markExplainList=new ArrayList<String>();
		if(markIndex==null||markIndex.equals("")){
			return null;
		}
		
		for (String indexStr : markIndex.split(",")) {
			if(Integer.parseInt(indexStr)>0||Integer.parseInt(indexStr)<pmClosedLoopMarkList.size()){
				markExplainList.add(pmClosedLoopMarkList.get(Integer.parseInt(indexStr)).toString());
			}else{
				return null;
			}
		}
		return markExplainList;
	}
	
	public List<String>getAllMarkExplain() {
		List<String>markExplainList=new ArrayList<String>();
		for (PmClosedLoopMark pmClosedLoopMarkObj : pmClosedLoopMarkList) {
			markExplainList.add(pmClosedLoopMarkObj.toString());
		}
		return markExplainList;
	}
	

}
