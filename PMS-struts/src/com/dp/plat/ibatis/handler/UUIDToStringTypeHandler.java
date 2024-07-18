package com.dp.plat.ibatis.handler;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;

import com.ibatis.sqlmap.engine.type.TypeHandler;


public class UUIDToStringTypeHandler extends BaseTypeHandler<String> implements TypeHandler {

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, String jdbcType) throws SQLException {
        JdbcType jdbcTypeEnum = null;  
        try {
            jdbcTypeEnum = JdbcType.valueOf(jdbcType);
        } catch (Exception e) {
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
                setNonNullParameter(ps, i, parameter.toString(), jdbcTypeEnum);
            } catch (Exception e) {
                throw new TypeException("Error setting non null for parameter #" + i + " with JdbcType " + jdbcType
                        + " . "
                        + "Try setting a different JdbcType for this parameter or a different configuration property. "
                        + "Cause: " + e, e);
            }
        }
    }
    
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter != null) {
            ps.setString(i, parameter.toString());
        } else {
            ps.setNull(i, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        UUID uuid = rs.getObject(columnName, UUID.class);
        return uuid != null ? uuid.toString() : null;
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        UUID uuid = rs.getObject(columnIndex, UUID.class);
        return uuid != null ? uuid.toString() : null;
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        UUID uuid = cs.getObject(columnIndex, UUID.class);
        return uuid != null ? uuid.toString() : null;
    }

    @Override
    public Object valueOf(String s) {
        return UUID.fromString(s);
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

}