package com.dp.plat.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JSONUtil {
    
    public static final TypeReference<Map<String, Object>> MapTypeReference = new TypeReference<Map<String, Object>>() {};
    
    /**
     * 将任意对象转换为 Map<String, Object>，并忽略 null 值字段
     */
    public static <T> Map<String, Object> toMap(T object) {
        return JSON.parseObject(
            JSON.toJSONString(object, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty),
            MapTypeReference
        );
    }

    /**
     * 更严格地忽略 null 值字段（推荐）
     */
    public static <T> Map<String, Object> toMapIgnoreNull(T object) {
        return JSON.parseObject(
            JSON.toJSONString(object, SerializerFeature.SkipTransientField,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullListAsEmpty,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullNumberAsZero,
                    SerializerFeature.WriteNullBooleanAsFalse
//                        , SerializerFeature.PrettyFormat // PrettyFormat 可选，用于调试时美观输出
            ), 
            MapTypeReference
        );
    }
}
