package com.dp.plat.prob.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成displayTag 要进行排序的参数Map
 * @author j01441
 *
 */
public class DisplayParamUtil {
	
	/**
	 * 初始化已知技术公告管理列表界面的排序参数
	 * @return
	 */
	public static Map<String , String>  initProbColMap(){
		Map<String , String> map = new HashMap<String, String>();
		map.put("0", "probNum");
		map.put("1", "theme");
		map.put("2", "priority");
		map.put("3", "status");
		map.put("4", "createTime");
		map.put("5", "updateTime");
		return map;
	}
	
	public static Map<String, String> initProbRestoreTaskColMap(){
		Map<String , String> map = new HashMap<String, String>();
		map.put("0", "serialNum");
		map.put("1", "itemModel");
		map.put("2", "restoreStatus");
		map.put("3", "projectName");
		map.put("4", "contractNo");
		map.put("5", "officeCode");
		map.put("6", "createTime");
		map.put("7", "updateTime");
		return map;
	}
	
}
