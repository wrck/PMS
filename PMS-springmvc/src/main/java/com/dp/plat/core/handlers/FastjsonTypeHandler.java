package com.dp.plat.core.handlers;
import java.util.Map;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Fastjson 实现 JSON 字段类型处理器
 *
 * @author hubin
 * @since 2019-08-25
 */
@MappedTypes({Object.class, Map.class})
@MappedJdbcTypes(JdbcType.OTHER)
public class FastjsonTypeHandler extends AbstractJsonTypeHandler<Object> {
	private final static Logger log = LoggerFactory.getLogger(JacksonTypeHandler.class);
	
    private Class<?> type;

    public FastjsonTypeHandler() {
	}

	public FastjsonTypeHandler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("FastjsonTypeHandler(" + type + ")");
        }
        Assert.notNull(type, "Type argument cannot be null");
        this.type = type;
    }

    @Override
    protected Object parse(String json) {
        return JSON.parseObject(json, type, Feature.AllowISO8601DateFormat );
    }

    @Override
    protected String toJson(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteDateUseDateFormat);
    }
}