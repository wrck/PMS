package com.dp.plat.ibatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;

import com.ibatis.sqlmap.engine.type.TypeHandler;

/**
 * @author miemie
 * @since 2019-11-28
 */
public abstract class AbstractJsonTypeHandler<T> extends BaseTypeHandler<T> implements TypeHandler {

	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter, String jdbcType) throws SQLException {
		JdbcType jdbcTypeEnum = null;  
		try {
		    jdbcTypeEnum = JdbcType.valueOf(jdbcType);
		} catch (Exception e) {
//		    if (StringUtils.isBlank(jdbcType) || "null".equalsIgnoreCase(jdbcType)) {
//		        jdbcType = "JSON";
//		    }
        }
		if (parameter == null) {
			if (jdbcType == null) {
				throw new TypeException(
						"JDBC requires that the JdbcType must be specified for all nullable parameters.");
			}
			try {
				ps.setNull(i, jdbcTypeEnum.TYPE_CODE);
			} catch (SQLException e) {
				throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
						+ "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. "
						+ "Cause: " + e, e);
			}
		} else {
			try {
				setNonNullParameter(ps, i, (T) parameter, jdbcTypeEnum);
			} catch (Exception e) {
				throw new TypeException("Error setting non null for parameter #" + i + " with JdbcType " + jdbcType
						+ " . "
						+ "Try setting a different JdbcType for this parameter or a different configuration property. "
						+ "Cause: " + e, e);
			}
		}
	}

	@Override
	public Object valueOf(String s) {
		return s;
	}

	@Override
	public boolean equals(Object object, String string) {
		if (object == null || string == null) {
			return object == string;
		} else {
			Object castedObject = valueOf(string);
			return object.equals(castedObject);
		}
	}

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

	public abstract T parse(String json);

	public abstract Object toJson(T obj);

	public abstract String toJsonString(T obj);
}