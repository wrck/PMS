/**
 * 
 */
package com.dp.plat.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.service.BasicDataService;

import net.sf.json.JSONObject;

/**
 * @author w02611
 *
 */
public class UserUtil {

	/**
	 * 市场和用服相同办事处的权限进行补充
	 * 
	 * @param areaPower
	 * @return 补充后的areaPower
	 */
	public static String processAreaPower(String areaPower) {
		if (StringUtils.isNotBlank(areaPower)) {
			Set<String> newAreaList = new HashSet<>();
			List<String> areaList = Arrays.asList(StringUtils.split(areaPower, ","));
			newAreaList.addAll(areaList);
			BasicDataService basicDataService = SpringContext.getApplicationContext().getBean("basicDataService",
					BasicDataService.class);
			String relationsJSON = basicDataService.querySysArg("dep.market2suport.relationsJSON");
			if (StringUtils.isBlank(relationsJSON)) {
				relationsJSON = "{}";
			}
			JSONObject relations = JSONObject.fromObject(relationsJSON);
			for (String area : areaList) {
				String newArea = transferDepNo(area, relations);
				if (StringUtils.isNotBlank(newArea) && !newAreaList.contains(newArea)) {
					newAreaList.add(newArea);
				}
			}
			areaPower = StringUtils.join(newAreaList, ",");
		}
		return areaPower;
	}

	/**
	 * 转换部门编码
	 * 
	 * @param relations
	 * @return
	 */
	public static String transferDepNo(String area) {
		return transferDepNo(area, null, 0);
	}

	/**
	 * 转换部门编码
	 * 
	 * @param relations
	 * @param direction
	 *            匹配方向: -1:反向,0-双向,1:正向，默认为0
	 * @return
	 */
	public static String transferDepNo(String area, int direction) {
		return transferDepNo(area, null, direction);
	}

	/**
	 * 转换部门编码
	 * 
	 * @param area
	 * @param relations
	 * @return
	 */
	public static String transferDepNo(String area, JSONObject relations) {
		return transferDepNo(area, relations, 0);
	}

	/**
	 * 转换部门编码
	 * 
	 * @param area
	 * @param direction
	 *            匹配方向: -1:反向,0-双向,1:正向，默认为0
	 * @param relations
	 * @return
	 */
	public static String transferDepNo(String area, JSONObject relations, int direction) {
		String newArea = area;
		String prevDep = "";
		String nextDep = "";
		if (relations == null || relations.isEmpty() || relations.isNullObject()) {
			BasicDataService basicDataService = SpringContext.getApplicationContext().getBean("basicDataService",
					BasicDataService.class);
			String relationsJSON = basicDataService.querySysArg("dep.market2suport.relationsJSON");
			if (StringUtils.isBlank(relationsJSON)) {
				relationsJSON = "{}";
			}
			relations = JSONObject.fromObject(relationsJSON);
		}
		String suffix = "*";
        for (Iterator<?> iterator = relations.keys(); iterator.hasNext();) {
            String keySuffix = "";
            String valueSuffix = "";
            String key = (String) iterator.next();
            String value = relations.getString(key);
            if (key.contains(suffix)) {
                key = key.replace(suffix, "");
                keySuffix = "." + suffix;
            }
            if (value.contains(suffix)) {
                value = value.replace(suffix, "");
                valueSuffix = "." + suffix;
            }
            // 双向或正向匹配
            if (area.startsWith(key) && direction >= 0) {
                if (prevDep.length() < key.length()) {
                    prevDep = key + keySuffix;
                    nextDep = value;
                }
            } else if (area.startsWith(value) && direction <= 0) {// 双向或反向匹配
                if (prevDep.length() < value.length()) {
                    prevDep = value + valueSuffix;
                    nextDep = key;
                }
            }
        }
		if (StringUtils.isNotBlank(prevDep)) {
			newArea = area.replaceFirst(prevDep, nextDep);
		} else {
			if (area.length() > 6) {
				newArea = area.substring(0, 6);
			}
		}
		return newArea;
	}
	
	/**
     * 转换部门编码
     * 
     * @param area
     * @param direction
     *            匹配方向: -1:反向,0-双向,1:正向，默认为0
     * @param relations
     * @return
     */
    public static Set<String> transferDepNos(String area, JSONObject relations, int direction) {
        String prevDep = "";
        String nextDep = "";
        if (relations == null || relations.isEmpty() || relations.isNullObject()) {
            BasicDataService basicDataService = SpringContext.getApplicationContext().getBean("basicDataService",
                    BasicDataService.class);
            String relationsJSON = basicDataService.querySysArg("dep.market2suport.relationsJSON");
            if (StringUtils.isBlank(relationsJSON)) {
                relationsJSON = "{}";
            }
            relations = JSONObject.fromObject(relationsJSON);
        }
        String suffix = "*";
        Set<String> areaSet = new HashSet<>();
        for (Iterator<?> iterator = relations.keys(); iterator.hasNext();) {
            String keySuffix = "";
            String valueSuffix = "";
            String key = (String) iterator.next();
            String value = relations.getString(key);
            if (key.contains(suffix)) {
                key = key.replace(suffix, "");
                keySuffix = "." + suffix;
            }
            if (value.contains(suffix)) {
                value = value.replace(suffix, "");
                valueSuffix = "." + suffix;
            }
            // 双向或正向匹配
            if (area.startsWith(key) && direction >= 0) {
                if (prevDep.length() < key.length()) {
                    prevDep = key + keySuffix;
                    nextDep = value;
                }
            } else if (area.startsWith(value) && direction <= 0) {// 双向或反向匹配
                if (prevDep.length() < value.length()) {
                    prevDep = value + valueSuffix;
                    nextDep = key;
                }
            }
            
            String temp = "";
            if (StringUtils.isNotBlank(prevDep)) {
                temp = area.replaceFirst(prevDep, nextDep);
            } else {
                if (area.length() > 6) {
                    temp = area.substring(0, 6);
                }
            }
            if (StringUtils.isNotBlank(temp)) {
                areaSet.add(temp);
                prevDep = "";
            }
        }
        return areaSet;
    }
	
	public static void main(String[] args) {
	    String relationsJSON = "{\"3104\":\"1620\",\"3110\":\"1610\",\"3111\":\"1611\",\"310500001\":\"160300\",\"310500002\":\"165000\",\"310500001001\":\"310500001\",\"310500001002\":\"310500001\",\"310500001003\":\"310500001\",\"310500001004\":\"310500001\",\"310500001005\":\"310500001\",\"310500002001\":\"310500002\",\"310500002002\":\"310500002\"}";
        if (StringUtils.isBlank(relationsJSON)) {
            relationsJSON = "{}";
        }
        
        JSONObject relations = JSONObject.fromObject(relationsJSON);
        String areaPower = "165000";
        if (StringUtils.isNotBlank(areaPower)) {
            Set<String> newAreaList = new HashSet<>();
            List<String> areaList = Arrays.asList(StringUtils.split(areaPower, ","));
            newAreaList.addAll(areaList);
            for (String area : areaList) {
                String newArea = transferDepNo(area, relations);
                if (StringUtils.isNotBlank(newArea) && !newAreaList.contains(newArea)) {
                    newAreaList.add(newArea);
                }
            }
            areaPower = StringUtils.join(newAreaList, ",");
        }
        System.out.println(areaPower);
        if (StringUtils.isNotBlank(areaPower)) {
            Set<String> newAreaList = new HashSet<>();
            List<String> areaList = Arrays.asList(StringUtils.split(areaPower, ","));
            newAreaList.addAll(areaList);
            for (String area : areaList) {
                Set<String> newArea = transferDepNos(area, relations, 0);
                if (!newArea.isEmpty()) {
                    newAreaList.addAll(newArea);
                }
            }
            areaPower = StringUtils.join(newAreaList, ",");
        }
        System.out.println(areaPower);
    }
}
