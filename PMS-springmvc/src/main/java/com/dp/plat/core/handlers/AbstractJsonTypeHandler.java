package com.dp.plat.core.handlers;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * @author miemie
 * @since 2019-11-28
 */
public abstract class AbstractJsonTypeHandler<T> extends BaseTypeHandler<T> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
    	ps.setString(i, toJsonString(parameter));
    }
    
    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final String json = rs.getString(columnName);
        return StringUtils.isBlank(json) ? null : parse(json);
    }
    
    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final String json = rs.getString(columnIndex);
        return StringUtils.isBlank(json) ? null : parse(json);
    }
    
    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String json = cs.getString(columnIndex);
        return StringUtils.isBlank(json) ? null : parse(json);
    }
    
    protected abstract T parse(String json);
    
    protected abstract Object toJson(T obj);
    
    protected abstract String toJsonString(T obj);
}