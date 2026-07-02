package com.dp.plat.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
public class ConvertUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static Map<String, Object> toMap(Object obj) { return mapper.convertValue(obj, Map.class); }
    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) { return mapper.convertValue(map, clazz); }
}
