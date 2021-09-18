package com.dp.plat.ibatis.handler;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Fastjson 实现 JSON 字段类型处理器
 *
 * @author hubin
 * @since 2019-08-25
 */
//@MappedTypes({ Object.class, Map.class, AbstractCollection.class })
@MappedTypes({ /* Object.class, */Map.class, JSONObject.class, ArrayList.class})
@MappedJdbcTypes(value = { JdbcType.OTHER, JdbcType.JSON })
public class FastjsonTypeHandler extends AbstractJsonTypeHandler<Object> {
	private final static Logger log = LoggerFactory.getLogger(FastjsonTypeHandler.class);

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
		if (JSON.isValid(json)) {
			return JSON.parseObject(json, type, Feature.AllowISO8601DateFormat);
		} else {
			return json;
		}
	}

	@Override
	protected Object toJson(Object obj) {
		return JSON.toJSON(obj);
	}

	@Override
	protected String toJsonString(Object obj) {
		if (obj instanceof String) {
			return obj.toString();
		}
		if (ParserConfig.isPrimitive2(obj.getClass())) {
			return String.valueOf(obj);
		}
		return JSON.toJSONString(obj,
				SerializerFeature.WriteMapNullValue,  SerializerFeature.WriteNullListAsEmpty/*,
				SerializerFeature.WriteNullStringAsEmpty*/, SerializerFeature.WriteDateUseDateFormat);
	}

}