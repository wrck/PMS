package com.dp.plat.common.utils;

import java.util.*;

/**
 * 通用工具类 - 迁移自老系统 Util (283行, 21个方法)
 */
public class CommonUtil {

    /** 生成随机数 */
    public static String getRandNumber() {
        return String.valueOf(System.currentTimeMillis() + new Random().nextInt(1000));
    }

    /** 判断字符串是否为空 */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /** 判断字符串是否不为空 */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /** 字符串拼接(用指定分隔符) */
    public static String join(Collection<String> collection, String separator) {
        if (collection == null || collection.isEmpty()) return "";
        return String.join(separator, collection);
    }

    /** 在字符串前后加指定字符 */
    public static String appendChar(String str, String ch) {
        if (isEmpty(str)) return "";
        StringBuilder sb = new StringBuilder();
        String[] parts = str.split(",");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(ch).append(parts[i].trim()).append(ch);
        }
        return sb.toString();
    }

    /** 安全的字符串转整数 */
    public static Integer safeParseInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /** 安全的字符串转长整数 */
    public static Long safeParseLong(String str) {
        try {
            return Long.parseLong(str.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /** Map安全取值 */
    @SuppressWarnings("unchecked")
    public static <T> T getMapValue(Map<String, Object> map, String key, Class<T> clazz) {
        if (map == null || key == null) return null;
        Object val = map.get(key);
        if (val == null) return null;
        try {
            return clazz.cast(val);
        } catch (ClassCastException e) {
            return null;
        }
    }

    /** 列表转逗号分隔字符串 */
    public static String listToString(List<?> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    /** 逗号分隔字符串转列表 */
    public static List<String> stringToList(String str) {
        if (isEmpty(str)) return Collections.emptyList();
        return Arrays.asList(str.split(","));
    }

    /** 对象转Map */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) return Collections.emptyMap();
        if (obj instanceof Map) return (Map<String, Object>) obj;
        Map<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                // ignore
            }
        }
        return map;
    }

    private static class Field {
        private final java.lang.reflect.Field field;
        Field(java.lang.reflect.Field field) { this.field = field; }
        String getName() { return field.getName(); }
        void setAccessible(boolean flag) { field.setAccessible(flag); }
        Object get(Object obj) throws IllegalAccessException { return field.get(obj); }
    }
}
