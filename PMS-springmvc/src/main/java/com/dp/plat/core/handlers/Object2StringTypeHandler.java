package com.dp.plat.core.handlers;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.StringTypeHandler;

/**
 * Map键值的映射，因为启用JSON，includeNullJdbcType之后会映射到JsonHandler上去
 *
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR})
public class Object2StringTypeHandler extends StringTypeHandler {
	
}