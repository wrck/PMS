package com.dp.plat.core.handlers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson 实现 JSON 字段类型处理器
 *
 * @author hubin
 * @since 2019-08-25
 */
@MappedTypes({Object.class, Map.class, LinkedHashMap.class, ArrayList.class})
@MappedJdbcTypes(value = {JdbcType.OTHER, JdbcType.JSON})
public class JacksonTypeHandler extends AbstractJsonTypeHandler<Object> {
	private final static Logger log = LoggerFactory.getLogger(JacksonTypeHandler.class);
	
    private static ObjectMapper objectMapper = new ObjectMapper();
    private Class<?> type;
    
    public JacksonTypeHandler() {
	}

    public JacksonTypeHandler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("JacksonTypeHandler(" + type + ")");
        }
        Assert.notNull(type, "Type argument cannot be null");
        this.type = type;
    }

    @Override
    protected Object parse(String json) {
        try {
        	if (type != null) {
        		return objectMapper.readValue(json, type);
        	} else {
        		return objectMapper.readValue(json, HashMap.class);
        	}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object toJson(Object obj) {
        return objectMapper.valueToTree(obj);
    }
    
    @Override
	protected String toJsonString(Object obj) {
    	try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
	}

	public static void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper should not be null");
        JacksonTypeHandler.objectMapper = objectMapper;
    }
}