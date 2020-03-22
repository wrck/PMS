package com.dp.plat.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class LinkedHashMapSort {
	/**
	 * 排序
	 * @return
	 */
	public static Map<String, String> sort(String columns,Map<String, String> map) {
		if(StringUtils.isEmpty(columns)) {
			return map;
		}
		String[] columnArr = columns.split("\\|");
		List<String> asList = Arrays.asList(columnArr);
		final Map<String, Integer> columnMap = new HashMap<String, Integer>();
		for(int i=0,l=asList.size();i<l;i++) {
			columnMap.put(asList.get(i), i);
		}
		
        Set<Entry<String,String>> set=map.entrySet();
        //创建一个泛型为Entry类型的list，用来排序
        List<Entry<String,String>> list=new ArrayList<Entry<String,String>>();
        //把set的元素全部添加到list
        list.addAll(set);
        //调用工具类Collections的sort方法排序
        Collections.sort(list, new Comparator<Entry<String,String>>(){
            public int compare(Entry<String, String> o1,
                    Entry<String, String> o2) {
                //比较键的的大小并返回比较结果
                String key01=o1.getKey();
                Integer index1 = columnMap.get(key01);
                if(index1 == null) {
                	return 0;
                }
                String key02=o2.getKey();
                Integer index2 = columnMap.get(key02);
                if(index2 == null) {
                	return -1;
                }
                return index1.compareTo(index2);
            }});
        //清空原来的map
        map.clear();
        //将完成排序的数据重新放回map
        for (Entry<String, String> en : list) {
            map.put(en.getKey(), en.getValue());
        }
        //打印查看排序结果
        System.out.println(map);
		return map;
	} 

}
