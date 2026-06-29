package com.dp.plat.plus.unifytask.util;

import java.util.Collection;
import java.util.Map;

/**
 * 1.7 Map.getOrDefault 适配
 * @author user
 *
 */
public class MapUtil {

	public static Object getOrDefault(Map<Object, Collection> extParams, Object key, Collection defaultValue) {
		Object v;
        return (((v = extParams.get(key)) != null) || extParams.containsKey(key))
            ? v
            : defaultValue;
    }
		
	public static String getOrDefault(Map<String, String> extParams, String key, String defaultValue) {
		String v;
        return (((v = extParams.get(key)) != null) || extParams.containsKey(key))
            ? v
            : defaultValue;
    }
	
	public static Integer getOrDefault(Map<String, Integer> extParams, String key, Integer defaultValue) {
		Integer v;
        return ((((v = extParams.get(key)) != null) || extParams.containsKey(key))
            ? v
            : defaultValue);
    }

	public static Object getOrDefault(Map<String, Object> extParams, String key, Object defaultValue) {
		Object v;
        return ((((v = extParams.get(key)) != null) || extParams.containsKey(key))
            ? v
            : defaultValue);
	}
}
