package com.dp.plat.framework.common.util.json;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON 工具类
 *
 * <p>直接复用自 yudao-framework（yudao-common）。基于 Jackson {@link ObjectMapper}，
 * 仅保留数据权限框架日志所需的 {@link #toJsonString(Object)} 等核心方法，移除 yudao
 * 完整版本中针对 Web/Long/LocalDateTime 的扩展配置。
 *
 * @author yudao
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 序列化时，遇到空对象不抛异常
        OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    @SneakyThrows
    public static String toJsonString(Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        return OBJECT_MAPPER.readValue(text, clazz);
    }

    @SneakyThrows
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        return OBJECT_MAPPER.readValue(text, typeReference);
    }

}
